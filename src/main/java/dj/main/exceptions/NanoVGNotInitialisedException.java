package dj.main.exceptions;

public class NanoVGNotInitialisedException extends RuntimeException {
    public NanoVGNotInitialisedException() {
        super("NanoVG could not get initialize");
    }
}
