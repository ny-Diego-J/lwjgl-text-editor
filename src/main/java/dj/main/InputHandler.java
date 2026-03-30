package dj.main;

import java.util.logging.Logger;

import static org.lwjgl.glfw.GLFW.*;

public class InputHandler {
    Controller ct;

    public InputHandler(Controller ct) {
        this.ct = ct;
    }


    public void scrollWheelHandler(long w, double xOffset, double yOffset) {
        //ct.logger.info(w + ";    " + xOffset + "    " + yOffset);
        if (yOffset > 0) scrollUp();
        else scrollDown();
    }

    private void scrollUp() {
        if (ct.gui.currentMod == GLFW_MOD_CONTROL) {
            System.out.println("zoom in");
        } else {
            System.out.println("up");
        }
    }

    private void scrollDown() {
        if (ct.gui.currentMod == GLFW_MOD_CONTROL) {
            System.out.println("zoom out");
        } else {
            System.out.println("down");
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
                    case GLFW_KEY_LEFT -> ct.cm.backwardWord();
                    case GLFW_KEY_RIGHT -> ct.cm.forwardWord();
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
                    case GLFW_KEY_UP -> ct.cm.cursorUp();
                    case GLFW_KEY_DOWN -> ct.cm.cursorDown();
                    case GLFW_KEY_LEFT -> ct.cm.backwardChar();
                    case GLFW_KEY_RIGHT -> ct.cm.forwardChar();
                    case GLFW_KEY_ENTER -> ct.cm.enterPressed();
                }
        }
    }
}
