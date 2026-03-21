package dj.main;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Editor {
    public ArrayList<StringBuilder> inputs = new ArrayList<>();
    public int currentLine = 0;
    public int xCursorPos = 0;

    private void tabPressed() {
        inputs.get(currentLine).insert(xCursorPos, "    ");
        xCursorPos += 4;
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

    private void cursorRight() {
        if (xCursorPos < inputs.get(currentLine).length()) {
            xCursorPos++;
        }
    }

    private void cursorLeft() {
        if (xCursorPos > 0) {
            xCursorPos--;
        }
    }

    private void addKeyToList(KeyEvent e) { //TODO: get rid of KeyEvent
        switch (e.getKeyChar()) {
            case 8:
                deleteAtChar(0);
                break;
            case 127:
                deleteAtChar(1);
                break;
            case 10:
                inputs.add(currentLine + 1, new StringBuilder());
                currentLine++;
                xCursorPos = 0;
                break;
            case 65535:
            default:

                inputs.get(currentLine).insert(xCursorPos, e.getKeyChar());
                xCursorPos++;
        }
    }

    private void deleteAtChar(int mod) {
        if (xCursorPos == 0 && currentLine >= 1 && mod == 0) {
            inputs.remove(currentLine);

            currentLine -= 1;
            xCursorPos = inputs.get(currentLine).length();
            return;
        }
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
}
