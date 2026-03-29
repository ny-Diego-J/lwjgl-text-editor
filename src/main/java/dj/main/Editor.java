package dj.main;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;


public class Editor {
    public List<StringBuilder> inputs;
    Controller ct;

    public Editor(Controller ct) {
        this.inputs = new ArrayList<>();
        this.ct = ct;
        inputs.add(new StringBuilder());
    }


    public void tabPressed() {
        inputs.get(ct.currentLine).insert(ct.xCursorPos, "    ");
        ct.xCursorPos += 4;
        ct.maxXPos = ct.xCursorPos;
    }


    public void addKeyToList(int e) {
        inputs.get(ct.currentLine).insert(ct.xCursorPos, (char) e);
        ct.xCursorPos++;
    }

    public void pasteInput(long window) {
        String s = glfwGetClipboardString(window);
        assert s != null;
        String[] lines = s.split("\n");
        for (String line : lines) {
            inputs.get(ct.currentLine).insert(ct.xCursorPos, line);
            if (lines.length > 1) {
                inputs.add(new StringBuilder());
                ct.currentLine += 1;
                ct.xCursorPos = 0;
            }
            ct.xCursorPos += line.length();
        }
    }


    public void backspaceChar() {
        if (ct.xCursorPos == 0 && ct.currentLine >= 1) {
            ct.xCursorPos = inputs.get(ct.currentLine - 1).length();
            inputs.get(ct.currentLine - 1).append(inputs.get(ct.currentLine));
            inputs.remove(ct.currentLine);

            ct.currentLine -= 1;
            return;
        }
        if (ct.xCursorPos == 0 && ct.currentLine == 0) {
            return;
        }
        if (ct.xCursorPos <= inputs.get(ct.currentLine).length()) {
            inputs.get(ct.currentLine).replace(ct.xCursorPos - 1, ct.xCursorPos, "");
            ct.xCursorPos--;
        }
    }

    public void deleteChar() {
        if (ct.xCursorPos == inputs.get(ct.currentLine).length() && ct.currentLine != inputs.indexOf(inputs.getLast())) {
            inputs.get(ct.currentLine).append(inputs.get(ct.currentLine + 1));
            inputs.remove(ct.currentLine + 1);
            return;
        }
        if (ct.xCursorPos + 1 <= inputs.get(ct.currentLine).length()) {
            inputs.get(ct.currentLine).replace(ct.xCursorPos, ct.xCursorPos + 1, "");
        }
    }

    public void backspaceWord() {
        int[] pos = lastWord();
        if (pos[1] == ct.currentLine && pos[0] <= ct.xCursorPos) {
            inputs.get(ct.currentLine).replace(pos[0], ct.xCursorPos, "");
            ct.xCursorPos = pos[0];
            return;
        }
        if (pos[1] < ct.currentLine) {
            ct.currentLine = pos[1];
            ct.xCursorPos = pos[0];
        }
    }

    public void deleteWord() {
        int[] pos = nextWord();
        if (pos[1] == ct.currentLine && pos[0] >= ct.xCursorPos) {
            inputs.get(ct.currentLine).replace(ct.xCursorPos, pos[0], "");
            return;
        }
        if (pos[1] > ct.currentLine) {
            ct.currentLine = pos[1];
        }
    }

    public int[] lastWord() {
        int[] pos = new int[2];
        pos[0] = ct.xCursorPos;  // x pos
        pos[1] = ct.currentLine; // y pos
        if (pos[0] != 0) {
            while (inputs.get(ct.currentLine).charAt(pos[0] - 1) == ' ') {
                if (pos[0] == 1 && ct.currentLine >= 1) {
                    pos[0] = inputs.get(ct.currentLine - 1).length();
                    pos[1] -= 1;
                    return pos;
                } else if (inputs.getFirst().isEmpty() && pos[1] == 0) {
                    pos[0] = ct.xCursorPos;
                    pos[1] = ct.currentLine;
                    return pos;
                } else if (pos[0] == 0 && ct.currentLine == 0) {
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
        while (inputs.get(ct.currentLine).charAt(pos[0] - 1) != ' ') {
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
        pos[0] = ct.xCursorPos;  // x pos
        pos[1] = ct.currentLine; // y pos
        do {
            if (pos[0] == inputs.get(ct.currentLine).length() && ct.currentLine + 1 < inputs.size()) {
                return pos;
            } else if (pos[0] + 1 == inputs.get(ct.currentLine).length() && ct.currentLine + 1 < inputs.size()) {
                return pos;
            } else if (pos[0] == inputs.get(ct.currentLine).length() && ct.currentLine + 1 == inputs.size()) {
                return pos;
            } else if (pos[0] + 1 == inputs.get(ct.currentLine).length() && ct.currentLine + 1 == inputs.size()) {
                return pos;
            } else pos[0]++;
        } while (inputs.get(ct.currentLine).charAt(pos[0]) == ' ');
        while (inputs.get(ct.currentLine).charAt(pos[0]) != ' ') {
            if (pos[0] == inputs.get(ct.currentLine).length() - 1) break;
            pos[0]++;
        }
        return pos;
    }
}

