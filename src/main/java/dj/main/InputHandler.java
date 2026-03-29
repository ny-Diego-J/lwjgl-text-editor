package dj.main;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    Controller ct;

    public InputHandler(Controller ct) {
        this.ct = ct;
    }

    public void processInput(int key, int action, int mod, long window) {

        if (action == GLFW_PRESS) inputSwitch(key, mod, window);
        if (action == GLFW_REPEAT) inputSwitch(key, mod, window);

    }

    private void inputSwitch(int key, int mod, long window) {
        switch (mod) {
            case GLFW_MOD_CONTROL:
                switch (key) {
                    case GLFW_KEY_V -> ct.ed.pasteInput(window);
                    case GLFW_KEY_LEFT -> ct.cm.backwardWord();
                    case GLFW_KEY_RIGHT -> ct.cm.forwardWord();
                    case GLFW_KEY_BACKSPACE -> ct.ed.backspaceWord();
                    case GLFW_KEY_DELETE -> ct.ed.deleteWord();
                }
                break;
            case GLFW_MOD_ALT:
                switch (key) {

                }
                break;
            default:
                switch (key) {
                    case GLFW_KEY_BACKSPACE -> ct.ed.backspaceChar();
                    case GLFW_KEY_DELETE -> ct.ed.deleteChar();
                    case GLFW_KEY_TAB -> ct.ed.tabPressed();
                    case GLFW_KEY_UP -> ct.cm.cursorUp();
                    case GLFW_KEY_DOWN -> ct.cm.cursorDown();
                    case GLFW_KEY_LEFT -> ct.cm.backwardChar();
                    case GLFW_KEY_RIGHT -> ct.cm.forwardChar();
                    case GLFW_KEY_ENTER -> ct.ed.enterPressed();
                }
        }
    }
}
