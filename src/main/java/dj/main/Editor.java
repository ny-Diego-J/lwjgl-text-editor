package dj.main;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

import java.util.logging.Logger;

public class Editor {
    public List<StringBuilder> inputs;
    public int currentLine;
    public int xCursorPos;
    public int maxXPos;
    Logger logger;


    public Editor() {
        this.inputs = new ArrayList<>();
        this.currentLine = 0;
        this.xCursorPos = 0;
        this.maxXPos = 0;
        this.logger = Logger.getLogger(getClass().getName());

        inputs.add(new StringBuilder());
    }

    public void processInput(int key, int action, int mod) {

        if (action == GLFW_PRESS) inputSwitch(key, mod);
        if (action == GLFW_REPEAT) inputSwitch(key, mod);

    }

    private void inputSwitch(int key, int mod) {
        switch (mod) {
            case GLFW_MOD_CONTROL:
                switch (key) {
                    case GLFW_KEY_V -> pasteInput();
                    case GLFW_KEY_LEFT -> backwardWord();
                    case GLFW_KEY_RIGHT -> forwardWord();
                    case GLFW_KEY_BACKSPACE -> backspaceWord();
                    case GLFW_KEY_DELETE -> deleteWord();
                }
                break;
            case GLFW_MOD_ALT:
                switch (key) {
                    default -> break;
                }
            case GLFW_MOD_SHIFT:
                switch (key) {
                    default -> break;
                }
            default:
                switch (key) {
                    case GLFW_KEY_BACKSPACE -> backspaceChar();
                    case GLFW_KEY_DELETE -> deleteChar();
                    case GLFW_KEY_TAB -> tabPressed();
                    case GLFW_KEY_UP -> cursorUp();
                    case GLFW_KEY_DOWN -> cursorDown();
                    case GLFW_KEY_LEFT -> backwardChar();
                    case GLFW_KEY_RIGHT -> forwardChar();
                    case GLFW_KEY_ENTER -> enterPressed();
                }
        }
        if (mod == GLFW_MOD_CONTROL && key == GLFW_KEY_V) pasteInput();

    }

    private void tabPressed() {
        inputs.get(currentLine).insert(xCursorPos, "    ");
        xCursorPos += 4;
        maxXPos = xCursorPos;
    }

    private void enterPressed() {
        String content = inputs.get(currentLine).substring(xCursorPos);
        inputs.get(currentLine).setLength(xCursorPos);
        inputs.add(currentLine + 1, new StringBuilder());

        currentLine++;
        inputs.get(currentLine).append(content);
        xCursorPos = 0;
        maxXPos = xCursorPos;
    }

    private void cursorUp() {
        if (currentLine >= 1) {
            currentLine--;
            if (xCursorPos < maxXPos) {
                if (maxXPos < inputs.get(currentLine).length()) {
                    xCursorPos = maxXPos;
                } else {
                    xCursorPos = inputs.get(currentLine).length();
                }
            } else if (xCursorPos > inputs.get(currentLine).length()) {
                xCursorPos = inputs.get(currentLine).length();
            }
        }
    }

    private void cursorDown() {
        if (currentLine + 1 < inputs.size()) {
            currentLine++;
            if (xCursorPos < maxXPos) {
                if (maxXPos < inputs.get(currentLine).length()) {
                    xCursorPos = maxXPos;
                } else {
                    xCursorPos = inputs.get(currentLine).length();
                }
            } else if (xCursorPos > inputs.get(currentLine).length()) xCursorPos = inputs.get(currentLine).length();

        }
    }

    private void backwardWord() {
        int[] pos = lastWord();
        if (pos[1] == currentLine && pos[0] < xCursorPos) {
            xCursorPos = pos[0];
            maxXPos = xCursorPos;
            return;
        }
        if (pos[1] < currentLine) {
            currentLine = pos[1];
            xCursorPos = pos[0];
            maxXPos = xCursorPos;
        }
    }

    private void backwardChar() {
        if (currentLine >= 1 && xCursorPos == 1) {
            currentLine--;
            xCursorPos = inputs.get(currentLine).length();
            maxXPos = xCursorPos;
        } else {
            xCursorPos--;
            maxXPos = xCursorPos;
        }
    }

    private void forwardWord() {
        int[] pos = nextWord();
        if (pos[1] == currentLine && pos[0] >= xCursorPos) {
            if (pos[0] + 1 == inputs.get(pos[1]).length()) {
                xCursorPos = pos[0] + 1;
                maxXPos = xCursorPos;
                return;
            }
            xCursorPos = pos[0];
            maxXPos = xCursorPos;
            return;
        }
        if (pos[1] > currentLine) {
            currentLine = pos[1];
        }
    }

    private void forwardChar() {
        if (currentLine != inputs.size()) {
            if (xCursorPos < inputs.get(currentLine).length()) {
                xCursorPos++;
                maxXPos = xCursorPos;
            } else {
                if (currentLine != inputs.size() - 1) {
                    xCursorPos = 0;
                    maxXPos = xCursorPos;
                    currentLine++;
                }
            }
        }
    }

    private void cursorLeft(int mod) {
        if (mod == GLFW_MOD_CONTROL && xCursorPos > 0) {
            int[] pos = lastWord();
            if (pos[1] == currentLine && pos[0] <= xCursorPos) {
                xCursorPos = pos[0];
                maxXPos = xCursorPos;
                return;
            }
            if (pos[1] < currentLine) {
                currentLine = pos[1];
                xCursorPos = pos[0];
                maxXPos = xCursorPos;
            }
        } else {
            if (currentLine >= 1 && xCursorPos == 1) {
                currentLine--;
                xCursorPos = inputs.get(currentLine).length();
                maxXPos = xCursorPos;
            } else {
                xCursorPos--;
                maxXPos = xCursorPos;
            }
        }
    }

    public void addKeyToList(int e) {
        inputs.get(currentLine).insert(xCursorPos, (char) e);
        xCursorPos++;
    }

    private void pasteInput(long window) {
        String s = glfwGetClipboardString(window);
        assert s != null;
        String[] lines = s.split("\n");
        for (String line : lines) {
            inputs.get(currentLine).insert(xCursorPos, line);
            if (lines.length > 1){
                inputs.add(new StringBuilder());
                currentLine += 1;
                xCursorPos = 0;
            }
            xCursorPos += line.length();
        }
    }


    private void backspaceChar() {
        if (xCursorPos == 0 && currentLine >= 1) {
            xCursorPos = inputs.get(currentLine - 1).length();
            inputs.get(currentLine - 1).append(inputs.get(currentLine));
            inputs.remove(currentLine);

            currentLine -= 1;
            return;
        }
        if (xCursorPos == 0 && currentLine == 0) {
            return;
        }
        if (xCursorPos <= inputs.get(currentLine).length()) {
            inputs.get(currentLine).replace(xCursorPos - 1, xCursorPos, "");
            xCursorPos--;
        }
    }

    private void deleteChar() {
        if (xCursorPos == inputs.get(currentLine).length() && currentLine != inputs.indexOf(inputs.getLast())) {
            inputs.get(currentLine).append(inputs.get(currentLine + 1));
            inputs.remove(currentLine + 1);
            return;
        }
        if (xCursorPos + 1 <= inputs.get(currentLine).length()) {
            inputs.get(currentLine).replace(xCursorPos, xCursorPos + 1, "");
        }
    }

    private void backspaceWord() {
        int[] pos = lastWord();
        if (pos[1] == currentLine && pos[0] <= xCursorPos) {
            inputs.get(currentLine).replace(pos[0], xCursorPos, "");
            xCursorPos = pos[0];
            return;
        }
        if (pos[1] < currentLine) {
            currentLine = pos[1];
            xCursorPos = pos[0];
        }
    }

    private void deleteWord() {
        int[] pos = nextWord();
        if (pos[1] == currentLine && pos[0] >= xCursorPos) {
            inputs.get(currentLine).replace(xCursorPos, pos[0], "");
            return;
        }
        if (pos[1] > currentLine) {
            currentLine = pos[1];
        }
    }

    private int[] lastWord() {
        int[] pos = new int[2];
        pos[0] = xCursorPos;  // x pos
        pos[1] = currentLine; // y pos
        while (inputs.get(currentLine).charAt(pos[0] - 1) == ' ') {
            if (pos[0] == 0 && currentLine >= 1) {
                pos[0] = inputs.get(currentLine - 1).length();
                pos[1] -= 1;
                return pos;
            } else if (inputs.getFirst().isEmpty() && pos[1] == 0) {
                pos[0] = xCursorPos;
                pos[1] = currentLine;
                return pos;
            } else if (pos[0] == 0 && currentLine == 0) {
                return pos;
            } else pos[0]--;
        }
        while (inputs.get(currentLine).charAt(pos[0] - 1) != ' ') {
            if (pos[0] == 1) {
                pos[0]--;
                break;
            }
            pos[0]--;
        }
        return pos;
    }

    private int[] nextWord() {
        int[] pos = new int[2];
        pos[0] = xCursorPos;  // x pos
        pos[1] = currentLine; // y pos
        do {
            if (pos[0] == inputs.get(currentLine).length() && currentLine + 1 < inputs.size()) {
                return pos;
            } else if (pos[0] + 1 == inputs.get(currentLine).length() && currentLine + 1 < inputs.size()) {
                return pos;
            } else if (pos[0] == inputs.get(currentLine).length() && currentLine + 1 == inputs.size()) {
                return pos;
            } else if (pos[0] + 1 == inputs.get(currentLine).length() && currentLine + 1 == inputs.size()) {
                return pos;
            } else pos[0]++;
        } while (inputs.get(currentLine).charAt(pos[0]) == ' ');
        while (inputs.get(currentLine).charAt(pos[0]) != ' ') {
            if (pos[0] == inputs.get(currentLine).length() - 1) break;
            pos[0]++;
        }
        return pos;
    }
}

