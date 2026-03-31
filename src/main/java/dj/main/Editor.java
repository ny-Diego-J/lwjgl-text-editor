package dj.main;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;


public class Editor {
    public List<StringBuilder> inputs;
    Controller ct;
    private int currentLine;
    private int xCursorPos;
    private int maxXPos;

    public Editor(Controller ct) {
        this.inputs = new ArrayList<>();
        this.ct = ct;
        this.currentLine = 0;
        this.xCursorPos = 0;
        this.maxXPos = 0;
        if (!inputs.isEmpty()) {
            currentLine = inputs.size() - 1;
            xCursorPos = inputs.get(currentLine).length();
        }
        inputs.add(new StringBuilder());
    }


    public void tabPressed() {
        inputs.get(currentLine).insert(xCursorPos, "    ");
        xCursorPos += 4;
        maxXPos = xCursorPos;
    }


    public void addKeyToList(int e) {
        inputs.get(currentLine).insert(xCursorPos, (char) e);
        xCursorPos++;
    }

    public void pasteInput(long window) {
        String s = glfwGetClipboardString(window);
        assert s != null;
        String[] lines = s.split("\n");
        for (String line : lines) {
            inputs.get(currentLine).insert(xCursorPos, line);
            if (lines.length > 1) {
                inputs.add(new StringBuilder());
                currentLine += 1;
                xCursorPos = 0;
            }
            xCursorPos += line.length();
        }
    }


    public void backspaceChar() {
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

    public void deleteChar() {
        if (xCursorPos == inputs.get(currentLine).length() && currentLine != inputs.indexOf(inputs.getLast())) {
            inputs.get(currentLine).append(inputs.get(currentLine + 1));
            inputs.remove(currentLine + 1);
            return;
        }
        if (xCursorPos + 1 <= inputs.get(currentLine).length()) {
            inputs.get(currentLine).replace(xCursorPos, xCursorPos + 1, "");
        }
    }

    public void backspaceWord() {
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

    public void deleteWord() {
        int[] pos = nextWord();
        if (pos[1] == currentLine && pos[0] >= xCursorPos) {
            inputs.get(currentLine).replace(xCursorPos, pos[0], "");
            return;
        }
        if (pos[1] > currentLine) {
            currentLine = pos[1];
        }
    }

    public int[] lastWord() {
        int[] pos = new int[2];
        pos[0] = xCursorPos;  // x pos
        pos[1] = currentLine; // y pos
        if (pos[0] != 0) {
            while (inputs.get(currentLine).charAt(pos[0] - 1) == ' ') {
                if (pos[0] == 1 && currentLine >= 1) {
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
        } else {
            if (pos[1] > 0) {
                pos[1]--;
                pos[0] = inputs.get(pos[1]).length();
            }
            return pos;
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

    public int[] nextWord() {
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

    public void cursorUp() {
        if (currentLine >= 1) {
            currentLine--;
            if (xCursorPos < maxXPos) {
                if (maxXPos < ct.ed.inputs.get(currentLine).length()) {
                    xCursorPos = maxXPos;
                } else {
                    xCursorPos = ct.ed.inputs.get(currentLine).length();
                }
            } else if (xCursorPos > ct.ed.inputs.get(currentLine).length()) {
                xCursorPos = ct.ed.inputs.get(currentLine).length();
            }
        }
    }

    public void cursorDown() {
        if (currentLine + 1 < ct.ed.inputs.size()) {
            currentLine++;
            if (xCursorPos < maxXPos) {
                if (maxXPos < ct.ed.inputs.get(currentLine).length()) {
                    xCursorPos = maxXPos;
                } else {
                    xCursorPos = ct.ed.inputs.get(currentLine).length();
                }
            } else if (xCursorPos > ct.ed.inputs.get(currentLine).length())
                xCursorPos = ct.ed.inputs.get(currentLine).length();

        }
    }

    public void backwardWord() {
        int[] pos = ct.ed.lastWord();
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

    public void backwardChar() {
        if (currentLine >= 1 && xCursorPos == 1) {
            currentLine--;
            xCursorPos = ct.ed.inputs.get(currentLine).length();
            maxXPos = xCursorPos;
        } else {
            if (xCursorPos == 0) {
                if (currentLine > 0) {
                    currentLine--;
                    xCursorPos = ct.ed.inputs.get(currentLine).length();
                    maxXPos = xCursorPos;
                }
                return;
            }
            xCursorPos--;
            maxXPos = xCursorPos;
        }
    }

    public void forwardWord() {
        int[] pos = ct.ed.nextWord();
        if (pos[1] == currentLine && pos[0] >= xCursorPos) {
            if (pos[0] + 1 == ct.ed.inputs.get(pos[1]).length()) {
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

    public void forwardChar() {
        if (currentLine != ct.ed.inputs.size()) {
            if (xCursorPos < ct.ed.inputs.get(currentLine).length()) {
                xCursorPos++;
                maxXPos = xCursorPos;
            } else {
                if (currentLine != ct.ed.inputs.size() - 1) {
                    xCursorPos = 0;
                    maxXPos = xCursorPos;
                    currentLine++;
                }
            }
        }
    }

    public void cursorLeft(int mod) {
        if (mod == GLFW_MOD_CONTROL && xCursorPos > 0) {
            int[] pos = ct.ed.lastWord();
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
                xCursorPos = ct.ed.inputs.get(currentLine).length();
                maxXPos = xCursorPos;
            } else {
                xCursorPos--;
                maxXPos = xCursorPos;
            }
        }
    }

    public void enterPressed() {
        String content = ct.ed.inputs.get(currentLine).substring(xCursorPos);
        ct.ed.inputs.get(currentLine).setLength(xCursorPos);
        ct.ed.inputs.add(currentLine + 1, new StringBuilder());

        currentLine++;
        ct.ed.inputs.get(currentLine).append(content);
        xCursorPos = 0;
        maxXPos = xCursorPos;
    }

    public int getxCursorPos() {
        return xCursorPos;
    }

    public int getCurrentLine() {
        return currentLine;
    }

    public List<String> getWordList() {
        List<String> wordList = new ArrayList<>();
        for (StringBuilder sb : inputs) {
            wordList.add(sb.toString());
        }
        return wordList;
    }
}