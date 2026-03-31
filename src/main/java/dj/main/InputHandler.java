package dj.main;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    Controller ct;

    public InputHandler(Controller ct) {
        this.ct = ct;
    }

    public void scrollWheelHandler(long w, double yOffset) {
        boolean ctrlPressed = glfwGetKey(w, GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS || glfwGetKey(w, GLFW_KEY_RIGHT_CONTROL) == GLFW_PRESS;
        if (yOffset > 0) scrollUp(ctrlPressed);
        else scrollDown(ctrlPressed);
    }

    private void scrollUp(boolean ctrlPressed) {
        if (ctrlPressed) {
            ct.gui.addFontSize(2.0f);
        } else {
            ct.gui.scrollUp(20.0f);
        }
    }

    private void scrollDown(boolean ctrlPressed) {
        if (ctrlPressed) {
            ct.gui.addFontSize(-2.0f);
        } else {
            ct.gui.scrollUp(-20.0f);
        }
    }

    public void mouseHandler(long window, int key, int action) {
        //ct.logger.info(window + ";    " + key + "    " + action); //TODO: add backwards and forwards action
    }

    public void processInput(int key, int action, int mod, long window) {
        //ct.logger.info(mod + "");
        if (action == GLFW_PRESS) inputSwitch(key, mod, window);
        if (action == GLFW_REPEAT) inputSwitch(key, mod, window);

    }

    private void inputSwitch(int key, int mod, long window) {
        switch (mod) {
            case GLFW_MOD_CONTROL:
                switch (key) {
                    case GLFW_KEY_V -> ct.ed.pasteInput(window);
                    case GLFW_KEY_LEFT -> ct.ed.backwardWord();
                    case GLFW_KEY_RIGHT -> ct.ed.forwardWord();
                    case GLFW_KEY_BACKSPACE -> ct.ed.backspaceWord();
                    case GLFW_KEY_DELETE -> ct.ed.deleteWord();
                    case GLFW_KEY_S -> ct.fm.saveFile();
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
                    case GLFW_KEY_UP -> ct.ed.cursorUp();
                    case GLFW_KEY_DOWN -> ct.ed.cursorDown();
                    case GLFW_KEY_LEFT -> ct.ed.backwardChar();
                    case GLFW_KEY_RIGHT -> ct.ed.forwardChar();
                    case GLFW_KEY_ENTER -> ct.ed.enterPressed();
                }
        }
    }
}
