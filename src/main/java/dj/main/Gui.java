package dj.main;

import dj.main.exceptions.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.nanovg.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.*;
import java.util.*;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Gui {
    public static final String FONT_NAME = "JetBrains mono";
    private static final String OPERATING_SYSTEM = "os.name";
    private Controller ct;
    private long window;
    private Logger logger = Logger.getLogger(getClass().getName());
    private float fontSize = 54.0f;
    private float yOffset = 0.0f;
    private float bannerOffset = 0.0f;
    private float textHeight = 0.0f;

    public Gui(Controller c) {
        this.ct = c;
    }

    public float getFontSize() {
        return fontSize;
    }

    public float getyOffset() {
        return yOffset;
    }

    public void setTextHeight(float textHeight) {
        this.textHeight = textHeight;
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


        if (System.getProperty(OPERATING_SYSTEM).equalsIgnoreCase("Linux")) {
            if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
        } else if (System.getProperty(OPERATING_SYSTEM).toLowerCase().contains("win")) {
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
        glfwSetKeyCallback(window, (w, key, scancode, action, mods) -> ct.ih.processInput(key, action, mods, w));
        glfwSetCharCallback(window, (_, key) -> ct.ed.addKeyToList(key));
        glfwSetMouseButtonCallback(window, (w, button, action, mods) -> ct.ih.mouseHandler(w, button, action));
        glfwSetScrollCallback(window, (w, xOffset, yOffset) -> ct.ih.scrollWheelHandler(w, yOffset));

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

        // Set window icon
        GLFWImage.Buffer icon = extractByteBufferFromImagePath("C:\\Users\\digij\\Documents\\GitHub\\ny\\lwjgl-text-editor\\src\\main\\resources\\images\\Noe.jpg");
        glfwSetWindowIcon(window, icon);

        // Make the window visible
        glfwShowWindow(window);
    }

    public GLFWImage.Buffer extractByteBufferFromImagePath(String s) {
        try {
            BufferedImage bi = ImageIO.read(new java.io.File(s));

            int width = bi.getWidth();
            int height = bi.getHeight();

            int[] pixels = new int[width * height];
            bi.getRGB(0, 0, width, height, pixels, 0, width);

            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buffer.put((byte) ((pixel >> 16) & 0xFF));
                    buffer.put((byte) ((pixel >> 8) & 0xFF));
                    buffer.put((byte) (pixel & 0xFF));
                    buffer.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            buffer.flip();

            GLFWImage icon = GLFWImage.malloc();
            icon.set(width, height, buffer);

            GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
            iconBuffer.put(0, icon);

            return iconBuffer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void loop() {
        GL.createCapabilities();

        // Set the clear color
        glClearColor(34.0f / 255.0f, 36.0f / 255.0f, 54.0f / 255.0f, 1.0f);


        // Initialize the Font
        long vg = NanoVGGL3.nvgCreate(NanoVGGL3.NVG_ANTIALIAS | NanoVGGL3.NVG_STENCIL_STROKES);
        if (vg == 0) throw new NanoVGNotInitialisedException();
        int font = -1;
        if (System.getProperty(OPERATING_SYSTEM).equalsIgnoreCase("Linux")) {
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

            List<String> wordlist = ct.ed.getWordList();
            ct.hasStarted = wordlist.size() != 1 || !wordlist.getFirst().isEmpty();

            glfwGetWindowSize(window, width, height);
            glfwGetFramebufferSize(window, fbWidth, fbHeight);
            glViewport(0, 0, fbWidth[0], fbHeight[0]);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);// clear the framebuffer

            ct.pt.printAll(vg, width, fbWidth, height, color);
            //System.out.println(yOffset + " " + textHeight);
            NanoVG.nvgEndFrame(vg);


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public void addFontSize(float amount) {
        if (fontSize < 1000.0f && fontSize + amount > 0) fontSize += amount;
    }

    public void scrollUp(float amount) {
        int lines = ct.pt.getLines(ct.pt.maxCharLine);
        float maxHeight = (lines * fontSize + bannerOffset) * -1;
        if (yOffset + amount <= 0.0f && yOffset + amount >= maxHeight) yOffset += amount;
    }

    public float getBannerOffset() {
        return bannerOffset;
    }

    public void setBannerOffset(float bannerOffset) {
        this.bannerOffset = bannerOffset;
    }
}
