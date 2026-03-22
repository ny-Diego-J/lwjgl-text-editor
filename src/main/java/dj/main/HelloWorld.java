package dj.main;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;
import org.lwjgl.nanovg.NanoVGGL3;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class HelloWorld {
    boolean test = false;
    Editor ed = new Editor();
    // The window handle
    private long window;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

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

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            ed.processInput(this, window, key, action, mods);
        });
        glfwSetCharCallback(window, (window, key) -> {
            ed.addKeyToList(key);
        });


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
            glfwSetWindowPos(
                    window,
                    (vidMode.width() - pWidth.get(0)) / 2,
                    (vidMode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }

    private void loop() {
        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);


        // Initialize the Font
        long vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
        if (vg == 0) throw new RuntimeException("NanoVG could not get initialize");

        int font = NanoVG.nvgCreateFont(vg, "JetBrains mono", "src/main/resources/fonts/main.ttf");
        if (font == -1) System.err.println("Font not found.");


        NVGColor color = NVGColor.create();
        NanoVG.nvgRGBA((byte) 255, (byte) 255, (byte) 255, (byte) 255, color);


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while (!glfwWindowShouldClose(window)) {
            int[] width = new int[1];
            int[] height = new int[1];
            int[] fbWidth = new int[1];
            int[] fbHeight = new int[1];

            glfwGetWindowSize(window, width, height);
            glfwGetFramebufferSize(window, fbWidth, fbHeight);
            glViewport(0, 0, fbWidth[0], fbHeight[0]);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// clear the framebuffer
            float pxRatio = (float) fbWidth[0] / (float) width[0];


            float textHeight = 10.0f;
            float fontSize = 54.0f;

            for (StringBuilder sb : ed.inputs) {
                NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
                NanoVG.nvgFontSize(vg, fontSize);
                NanoVG.nvgFontFace(vg, "JetBrains mono");
                NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
                NanoVG.nvgText(vg, 10.0f, textHeight, sb.toString());
                NanoVG.nvgEndFrame(vg);
                textHeight += fontSize;
            }
            float charWidth = NanoVG.nvgTextBounds(vg, 0, 0, "A", (float[]) null);


            NanoVG.nvgBeginPath(vg);
            NanoVG.nvgMoveTo(vg, charWidth * ed.xCursorPos + 10.0f, ed.currentLine * fontSize + 10.0f);
            NanoVG.nvgLineTo(vg, charWidth * ed.xCursorPos + 10.0f, ed.currentLine * fontSize + fontSize);
            NanoVG.nvgStrokeColor(vg, color);
            NanoVG.nvgStrokeWidth(vg, 1.0f);
            NanoVG.nvgStroke(vg);


            NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
            NanoVG.nvgFontSize(vg, 54.0f);
            NanoVG.nvgFontFace(vg, "JetBrains mono");
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
            NanoVG.nvgText(vg, 10.0f, height[0] - 100, "Press anything to start the editor");
            NanoVG.nvgEndFrame(vg);

            if (test) {
                NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
                NanoVG.nvgFontSize(vg, 64.0f);
                NanoVG.nvgFontFace(vg, "JetBrains mono");
                NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER | NanoVG.NVG_ALIGN_TOP);
                NanoVG.nvgText(vg, width[0] / 2.0f, 10, "Hello World");
                NanoVG.nvgEndFrame(vg);
            }


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }
}
