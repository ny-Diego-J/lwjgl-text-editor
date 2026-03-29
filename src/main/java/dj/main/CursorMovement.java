package dj.main;

import static org.lwjgl.glfw.GLFW.*;

public class CursorMovement {
    Controller ct;

    public CursorMovement(Controller ct) {
        this.ct = ct;
    }

    public void cursorUp() {
        if (ct.currentLine >= 1) {
            ct.currentLine--;
            if (ct.xCursorPos < ct.maxXPos) {
                if (ct.maxXPos < ct.ed.inputs.get(ct.currentLine).length()) {
                    ct.xCursorPos = ct.maxXPos;
                } else {
                    ct.xCursorPos = ct.ed.inputs.get(ct.currentLine).length();
                }
            } else if (ct.xCursorPos > ct.ed.inputs.get(ct.currentLine).length()) {
                ct.xCursorPos = ct.ed.inputs.get(ct.currentLine).length();
            }
        }
    }

    public void cursorDown() {
        if (ct.currentLine + 1 < ct.ed.inputs.size()) {
            ct.currentLine++;
            if (ct.xCursorPos < ct.maxXPos) {
                if (ct.maxXPos < ct.ed.inputs.get(ct.currentLine).length()) {
                    ct.xCursorPos = ct.maxXPos;
                } else {
                    ct.xCursorPos = ct.ed.inputs.get(ct.currentLine).length();
                }
            } else if (ct.xCursorPos > ct.ed.inputs.get(ct.currentLine).length())
                ct.xCursorPos = ct.ed.inputs.get(ct.currentLine).length();

        }
    }

    public void backwardWord() {
        int[] pos = ct.ed.lastWord();
        if (pos[1] == ct.currentLine && pos[0] < ct.xCursorPos) {
            ct.xCursorPos = pos[0];
            ct.maxXPos = ct.xCursorPos;
            return;
        }
        if (pos[1] < ct.currentLine) {
            ct.currentLine = pos[1];
            ct.xCursorPos = pos[0];
            ct.maxXPos = ct.xCursorPos;
        }
    }

    public void backwardChar() {
        if (ct.currentLine >= 1 && ct.xCursorPos == 1) {
            ct.currentLine--;
            ct.xCursorPos = ct.ed.inputs.get(ct.currentLine).length();
            ct.maxXPos = ct.xCursorPos;
        } else {
            if (ct.xCursorPos == 0) {
                if (ct.currentLine > 0) {
                    ct.currentLine--;
                    ct.xCursorPos = ct.ed.inputs.get(ct.currentLine).length();
                    ct.maxXPos = ct.xCursorPos;
                }
                return;
            }
            ct.xCursorPos--;
            ct.maxXPos = ct.xCursorPos;
        }
    }

    public void forwardWord() {
        int[] pos = ct.ed.nextWord();
        if (pos[1] == ct.currentLine && pos[0] >= ct.xCursorPos) {
            if (pos[0] + 1 == ct.ed.inputs.get(pos[1]).length()) {
                ct.xCursorPos = pos[0] + 1;
                ct.maxXPos = ct.xCursorPos;
                return;
            }
            ct.xCursorPos = pos[0];
            ct.maxXPos = ct.xCursorPos;
            return;
        }
        if (pos[1] > ct.currentLine) {
            ct.currentLine = pos[1];
        }
    }

    public void forwardChar() {
        if (ct.currentLine != ct.ed.inputs.size()) {
            if (ct.xCursorPos < ct.ed.inputs.get(ct.currentLine).length()) {
                ct.xCursorPos++;
                ct.maxXPos = ct.xCursorPos;
            } else {
                if (ct.currentLine != ct.ed.inputs.size() - 1) {
                    ct.xCursorPos = 0;
                    ct.maxXPos = ct.xCursorPos;
                    ct.currentLine++;
                }
            }
        }
    }

    public void cursorLeft(int mod) {
        if (mod == GLFW_MOD_CONTROL && ct.xCursorPos > 0) {
            int[] pos = ct.ed.lastWord();
            if (pos[1] == ct.currentLine && pos[0] <= ct.xCursorPos) {
                ct.xCursorPos = pos[0];
                ct.maxXPos = ct.xCursorPos;
                return;
            }
            if (pos[1] < ct.currentLine) {
                ct.currentLine = pos[1];
                ct.xCursorPos = pos[0];
                ct.maxXPos = ct.xCursorPos;
            }
        } else {
            if (ct.currentLine >= 1 && ct.xCursorPos == 1) {
                ct.currentLine--;
                ct.xCursorPos = ct.ed.inputs.get(ct.currentLine).length();
                ct.maxXPos = ct.xCursorPos;
            } else {
                ct.xCursorPos--;
                ct.maxXPos = ct.xCursorPos;
            }
        }
    }

    public void enterPressed() {
        String content = ct.ed.inputs.get(ct.currentLine).substring(ct.xCursorPos);
        ct.ed.inputs.get(ct.currentLine).setLength(ct.xCursorPos);
        ct.ed.inputs.add(ct.currentLine + 1, new StringBuilder());

        ct.currentLine++;
        ct.ed.inputs.get(ct.currentLine).append(content);
        ct.xCursorPos = 0;
        ct.maxXPos = ct.xCursorPos;
    }
}
