package dj.main;

import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class Editor {
    public ArrayList<StringBuilder> inputs = new ArrayList<>();
    public int currentLine = 0;
    public int xCursorPos = 0;


    public Editor() {
        inputs.add(new StringBuilder());
    }

    public void processInput(HelloWorld h, long window, int key, int action, int mod) { //TODO: use mod for ctrl and shift
//        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
//            glfwSetWindowShouldClose(window, true);// We will detect this in the rendering loop
//            return;
//        }

        if (action == GLFW_PRESS) inputSwitch(key, mod);
        if (action == GLFW_REPEAT) inputSwitch(key, mod);

    }

    private void inputSwitch(int key, int mod) {
        switch (key) {
            case GLFW_KEY_BACKSPACE -> deleteAtChar(0, mod);
            case GLFW_KEY_DELETE -> deleteAtChar(1, mod);
            case GLFW_KEY_TAB -> tabPressed();
            case GLFW_KEY_UP -> cursorUp();
            case GLFW_KEY_DOWN -> cursorDown();
            case GLFW_KEY_LEFT -> cursorLeft(mod);
            case GLFW_KEY_RIGHT -> cursorRight(mod);
            case GLFW_KEY_ENTER -> enterPressed();
        }
    }

    private void tabPressed() {
        inputs.get(currentLine).insert(xCursorPos, "    ");
        xCursorPos += 4;
    }

    private void enterPressed() {
        String content = inputs.get(currentLine).substring(xCursorPos);
        inputs.get(currentLine).setLength(xCursorPos);
        inputs.add(currentLine + 1, new StringBuilder());


        currentLine++;
        inputs.get(currentLine).append(content);
        xCursorPos = 0;
    }

    private void cursorUp() {
        if (currentLine >= 1) {
            currentLine--;
            if (xCursorPos > inputs.get(currentLine).length()) {
                xCursorPos = inputs.get(currentLine).length();
            }
        }
    }

    private void cursorDown() {
        if (currentLine + 1 < inputs.size()) {
            currentLine++;
            if (xCursorPos > inputs.get(currentLine).length()) {
                xCursorPos = inputs.get(currentLine).length();
            }
        }
    }

    private void cursorRight(int mod) {
        if (mod == GLFW_MOD_CONTROL && xCursorPos < inputs.get(currentLine).length()) {
            int pos = xCursorPos;
            if (xCursorPos == inputs.get(currentLine).length() - 1) {
                if (currentLine != inputs.size() - 1) {
                    xCursorPos = 0;
                    currentLine++;
                }
            }
            while (inputs.get(currentLine).charAt(pos) == ' ') {
                if (pos == inputs.get(currentLine).length() && currentLine < inputs.size() - 1) {
                    xCursorPos = inputs.get(currentLine).length();
                    return;
                } else pos++;
            }
            while (inputs.get(currentLine).charAt(pos) != ' ') {
                pos++;
                if (pos == inputs.get(currentLine).length()) break;

            }
            xCursorPos = pos;

        } else {
            if (currentLine != inputs.size()) {
                if (xCursorPos < inputs.get(currentLine).length()) {
                    xCursorPos++;
                } else {
                    if (currentLine != inputs.size() - 1) {
                        xCursorPos = 0;
                        currentLine++;
                    }
                }
            }
        }

    }

    private void cursorLeft(int mod) {
        if (mod == GLFW_MOD_CONTROL && xCursorPos > 0) {
            int pos = xCursorPos;
            while (inputs.get(currentLine).charAt(pos - 1) == ' ') {
                if (pos == 0 && currentLine >= 1) {
                    xCursorPos = inputs.get(currentLine - 1).length();
                    currentLine -= 1;
                    return;
                } else pos--;
            }
            while (inputs.get(currentLine).charAt(pos - 1) != ' ') {
                if (pos == 1) break;
                pos--;
            }
            xCursorPos = pos;
        }
        if (xCursorPos > 0) xCursorPos--;
        else {
            if (currentLine >= 1) {
                currentLine--;
                xCursorPos = inputs.get(currentLine).length();
            }
        }

    }

    public void addKeyToList(int e) {
        inputs.get(currentLine).insert(xCursorPos, (char) e);
        xCursorPos++;
    }

    private void deleteAtChar(int mod, int alter) {
        if (xCursorPos == 0 && currentLine >= 1 && mod == 0) {
            xCursorPos = inputs.get(currentLine - 1).length();
            inputs.get(currentLine - 1).append(inputs.get(currentLine));
            inputs.remove(currentLine);

            currentLine -= 1;
            return;
        }
        if (alter == GLFW_MOD_CONTROL && mod == 0) deleteWord();
        if (mod == 1 && xCursorPos == inputs.get(currentLine).length() && currentLine != inputs.indexOf(inputs.getLast())) {
            inputs.get(currentLine).append(inputs.get(currentLine + 1));
            inputs.remove(currentLine + 1);
            return;
        }

        if (xCursorPos == 0 && currentLine == 0 && mod == 0) {
            return;
        }

        if (xCursorPos + mod <= inputs.get(currentLine).length()) {
            inputs.get(currentLine).replace(xCursorPos - 1 + mod, xCursorPos + mod, "");
            if (mod == 0) {
                xCursorPos--;
            }
        }
    }

    private void deleteWord() {
        int pos = xCursorPos;
        while (inputs.get(currentLine).charAt(pos - 1) == ' ') {
            if (pos == 0 && currentLine >= 1) {
                xCursorPos = inputs.get(currentLine - 1).length();
                inputs.get(currentLine - 1).append(inputs.get(currentLine));
                inputs.remove(currentLine);
                currentLine -= 1;
                return;
            } else if (pos == 0 && currentLine == 0) {
                inputs.getFirst().replace(0, xCursorPos, "");
            } else pos--;
        }
        while (inputs.get(currentLine).charAt(pos - 1) != ' ') {
            if (pos == 1) break;
            pos--;
        }
        inputs.get(currentLine).replace(pos, xCursorPos, "");
        xCursorPos = pos;
    }
}

