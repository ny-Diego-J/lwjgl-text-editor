package dj.main;

import org.lwjgl.glfw.GLFW;

public class Main {
    public static void main() {
        GLFW.glfwInitHint(GLFW.GLFW_PLATFORM, GLFW.GLFW_PLATFORM_X11);

        // 2. Starte dein Programm
        HelloWorld h = new HelloWorld();
        h.run();
    }
}