package dj.main.exceptions;

public class WindowFailedToCreateException extends RuntimeException {
    public WindowFailedToCreateException() {
        super("Failed to create the GLFW window");
    }
}
