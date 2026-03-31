package dj.main;

import dj.main.printing.PrintText;
import org.lwjgl.glfw.GLFW;

import java.util.logging.Logger;

public class Controller {

    public Gui gui;
    public Editor ed;
    public boolean hasStarted;
    InputHandler ih;
    FileManager fm;
    Logger logger;
    String filePath;
    PrintText pt;

    public Controller(String filePath) {
        this.filePath = filePath;
        this.ed = new Editor(this);
        this.hasStarted = false;
        this.ih = new InputHandler(this);
        this.fm = new FileManager(this);

        this.logger = Logger.getLogger(getClass().getName());
        this.pt = new PrintText(this);
    }

    public void run() {
        System.out.println(filePath);
        fm.readFile(filePath);
        GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_X11);
        gui = new Gui(this);
        gui.run();
    }
}
