package dj.main;

import dj.main.exceptions.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.nanovg.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.*;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Gui {
    private static final float Y_OFFSET = 10.0f;
    private static final String FONT_NAME = "JetBrains mono";
    public int currentMod = 0;
    Logger logger = Logger.getLogger(getClass().getName());
    Controller ct;
    float fontSize = 54.0f;
    private long window;


    public Gui(Controller c) {
        this.ct = c;
    }

    public void run() {
        logger.info("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void init() {
        // Set up an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();


        if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
            if (!glfwInit())
                throw new IllegalStateException("Unable to initialize GLFW");
        } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
            //Windows version
            glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_WIN32);
        } else {
            //arch version
            glfwInitHint(GLFW_PLATFORM, GLFW_PLATFORM_WAYLAND);
        }

        if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(800, 600, "Glas editor - " + ct.filePath, NULL, NULL);
        if (window == NULL) throw new WindowFailedToCreateException();

        // Set up a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> {
            ct.ih.processInput(key, action, mods, w);
            currentMod = mods;

        });
        glfwSetCharCallback(window, (_, key) -> ct.ed.addKeyToList(key));
        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> {
            // Hier rufst du deinen Handler auf
            ct.ih.mouseHandler(w, button, action);
        });
        glfwSetScrollCallback(window, (w, xOffset, yOffset) -> ct.ih.scrollWheelHandler(w, xOffset, yOffset));

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            assert vidMode != null;
            glfwSetWindowPos(window, (vidMode.width() - pWidth.get(0)) / 2, (vidMode.height() - pHeight.get(0)) / 2);
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        GL.createCapabilities();

        // Set the clear color
        glClearColor(34.0f / 255.0f, 36.0f / 255.0f, 54.0f / 255.0f, 1.0f);


        // Initialize the Font
        long vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
        if (vg == 0) throw new NanoVGNotInitialisedException();
        int font = -1;
        if (System.getProperty("os.name").equalsIgnoreCase("Linux")) {
            font = NanoVG.nvgCreateFont(vg, FONT_NAME, "/home/digi/projects/lwjgl-text-editor/src/main/resources/fonts/main.ttf");
        } else {
            font = NanoVG.nvgCreateFont(vg, FONT_NAME, "C:\\Users\\digij\\Documents\\GitHub\\ny\\lwjgl-text-editor\\src\\main\\resources\\fonts\\main.ttf");
        }
        if (font == -1) logger.warning("Font not found.");


        NVGColor color = NVGColor.create();
        NanoVG.nvgRGBA((byte) 200, (byte) 211, (byte) 245, (byte) 255, color);


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            // positions
            int[] width = new int[1];
            int[] height = new int[1];
            int[] fbWidth = new int[1];
            int[] fbHeight = new int[1];


            ct.hasStarted = ct.ed.inputs.size() != 1 || !ct.ed.inputs.getFirst().isEmpty();

            glfwGetWindowSize(window, width, height);
            glfwGetFramebufferSize(window, fbWidth, fbHeight);
            glViewport(0, 0, fbWidth[0], fbHeight[0]);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// clear the framebuffer
            float pxRatio = (float) fbWidth[0] / (float) width[0];


            float textHeight = Y_OFFSET;


            NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
            NanoVG.nvgFontSize(vg, fontSize);
            NanoVG.nvgFontFace(vg, FONT_NAME);
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
            float charWidth = NanoVG.nvgTextBounds(vg, 0, 0, "A", (float[]) null);
            int maxCharLine = (int) (width[0] / charWidth);
            int lineBreaks = 0;
            for (int i = 0; i < ct.currentLine + 1; i++) {
                if (i == ct.currentLine) {
                    if (ct.xCursorPos + 1 > maxCharLine) {
                        for (int j = 0; j < ct.ed.inputs.get(i).length() / maxCharLine; j++) {
                            lineBreaks++;
                        }
                    }
                } else {
                    if (ct.ed.inputs.get(i).length() > maxCharLine) {
                        for (int j = 0; j < ct.ed.inputs.get(i).length() / maxCharLine; j++) {
                            lineBreaks++;
                        }
                    }
                }
            }


            int xPos = ct.xCursorPos % maxCharLine;
            float baseHeight = ct.currentLine * fontSize + lineBreaks * fontSize;

            float bannerCenterY = baseHeight + Y_OFFSET + (fontSize / 2.0f);

            NanoVG.nvgRGBA((byte) 47, (byte) 51, (byte) 77, (byte) 255, color);
            NanoVG.nvgBeginPath(vg);

            NanoVG.nvgMoveTo(vg, 0.0f, bannerCenterY);
            NanoVG.nvgLineTo(vg, width[0], bannerCenterY);

            NanoVG.nvgStrokeColor(vg, color);

            NanoVG.nvgStrokeWidth(vg, fontSize);
            NanoVG.nvgStroke(vg);


            for (StringBuilder sb : ct.ed.inputs) {
                ArrayList<String> lines = getLines(sb.toString(), width[0], charWidth);
                for (String s : lines) {
                    NanoVG.nvgText(vg, 10.0f, textHeight, s);
                    textHeight += fontSize;
                }
            }

            NanoVG.nvgRGBA((byte) 208, (byte) 204, (byte) 178, (byte) 255, color);
            float cursorTop = baseHeight + Y_OFFSET;
            float cursorBottom = cursorTop + fontSize;

            NanoVG.nvgRGBA((byte) 208, (byte) 204, (byte) 178, (byte) 255, color);
            NanoVG.nvgBeginPath(vg);

// X-Position bleibt gleich, aber Y wird dynamisch
            NanoVG.nvgMoveTo(vg, charWidth * xPos + 10.0f, cursorTop);
            NanoVG.nvgLineTo(vg, charWidth * xPos + 10.0f, cursorBottom);

            NanoVG.nvgStrokeColor(vg, color);
            NanoVG.nvgStrokeWidth(vg, 2.0f);
            NanoVG.nvgStroke(vg);

            if (!ct.hasStarted) {
                NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
                NanoVG.nvgFontSize(vg, 54.0f);
                NanoVG.nvgFontFace(vg, FONT_NAME);
                NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
                NanoVG.nvgText(vg, 10.0f, height[0] - 100.0f, "Press anything to start the editor");
            }
            NanoVG.nvgEndFrame(vg);


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    private ArrayList<String> getLines(String input, int width, float charWidth) {
        ArrayList<String> lines = new ArrayList<>();
        if (charWidth * input.length() > width) {
            splitLine(lines, input, width, charWidth, 0);
            int i = 0;
            while (i < lines.size()) {
                if (charWidth * lines.get(i).length() > width) {
                    String replaceLine = lines.get(i);
                    lines.remove(i);
                    splitLine(lines, replaceLine, width, charWidth, i);
                }
                i++;
            }
        } else {
            lines.add(input);
        }
        return lines;
    }

    private void splitLine(ArrayList<String> lines, String input, int width, float charWidth, int currentIndex) {
        if (charWidth * input.length() > width) {
            float amount = width / charWidth;
            lines.add(currentIndex, input.substring((int) amount));
            lines.add(currentIndex, input.substring(0, (int) amount));
        } else {
            lines.add(input);
        }
    }
}
