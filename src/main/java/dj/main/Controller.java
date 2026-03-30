package dj.main;

import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

public class Controller {
    public int currentLine;
    public int xCursorPos;
    public int maxXPos;
    Gui gui;
    Editor ed;
    boolean hasStarted;
    InputHandler ih;
    CursorMovement cm;
    FileManager fm;
    Logger logger;
    String filePath;

    public Controller(String filePath) {
        this.filePath = filePath;
        this.ed = new Editor(this);
        this.hasStarted = false;
        this.ih = new InputHandler(this);
        this.cm = new CursorMovement(this);
        this.fm = new FileManager(this);
        this.currentLine = 0;
        this.xCursorPos = 0;
        this.maxXPos = 0;
        this.logger = Logger.getLogger(getClass().getName());
    }

    public void run() {
        System.out.println(filePath);
        fm.readFile(filePath);

        GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_X11);
        gui = new Gui(this);
        gui.run();
    }
}
