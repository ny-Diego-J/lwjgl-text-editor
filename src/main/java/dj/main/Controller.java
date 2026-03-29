package dj.main;

import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

public class Controller {
    HelloWorld h;
    Editor ed = new Editor(this);
    boolean hasStarted = false;
    InputHandler ih = new InputHandler(this);
    CursorMovement cm = new CursorMovement(this);

    public int currentLine = 0;
    public int xCursorPos = 0;
    public int maxXPos = 0;
    Logger logger = Logger.getLogger(getClass().getName());

    public void run() {
        GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_X11);
        h = new HelloWorld(this);
        h.run();
    }
}
