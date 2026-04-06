package dj.main.printing;

import dj.main.*;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PrintText {
    private Controller ct;
    private int currentLine;
    private int xCursorPos;


    public PrintText(Controller ct) {
        this.ct = ct;
    }

    public void printText(long vg, int[] width, int[] fbWidth, int[] height, NVGColor color) {
        currentLine = ct.ed.getCurrentLine();
        xCursorPos = ct.ed.getxCursorPos();
        float pxRatio = (float) fbWidth[0] / (float) width[0];
        float textHeight = ct.gui.getyOffset();
        float fontSize = ct.gui.getFontSize();
        float yOffset = ct.gui.getyOffset();

        NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
        NanoVG.nvgFontSize(vg, fontSize);
        NanoVG.nvgFontFace(vg, Gui.FONT_NAME);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
        float charWidth = NanoVG.nvgTextBounds(vg, 0, 0, "A", (float[]) null);
        int maxCharLine = (int) (width[0] / charWidth);
        int lineBreaks = getLineBreaks(maxCharLine);


        int xPos = xCursorPos % maxCharLine;
        float baseHeight = lineBreaks * fontSize + currentLine * fontSize;
        float bannerCenterY = baseHeight + yOffset + (fontSize / 2.0f);

        NanoVG.nvgRGBA((byte) 47, (byte) 51, (byte) 77, (byte) 255, color);
        NanoVG.nvgBeginPath(vg);

        // draw current line background
        NanoVG.nvgMoveTo(vg, 0.0f, bannerCenterY);
        NanoVG.nvgLineTo(vg, width[0], bannerCenterY);

        NanoVG.nvgStrokeColor(vg, color);
        NanoVG.nvgStrokeWidth(vg, fontSize);
        NanoVG.nvgStroke(vg);


        for (String s : ct.ed.getWordList()) {
            //text itself
            ArrayList<String> lines = getLines(s, width[0], charWidth);
            for (String st : lines) {
                NanoVG.nvgText(vg, 10.0f, textHeight, st);
                textHeight += fontSize;
            }
        }

        NanoVG.nvgRGBA((byte) 208, (byte) 204, (byte) 178, (byte) 255, color);
        float cursorTop = baseHeight + yOffset;
        float cursorBottom = cursorTop + fontSize;

        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgMoveTo(vg, charWidth * xPos + 10.0f, cursorTop);
        NanoVG.nvgLineTo(vg, charWidth * xPos + 10.0f, cursorBottom);

        NanoVG.nvgStrokeColor(vg, color);
        NanoVG.nvgStrokeWidth(vg, 2.0f);
        NanoVG.nvgStroke(vg);

        if (!ct.hasStarted) {
            NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
            NanoVG.nvgFontSize(vg, 54.0f);
            NanoVG.nvgFontFace(vg, Gui.FONT_NAME);
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
            NanoVG.nvgText(vg, 10.0f, height[0] - 100.0f, "Press anything to start the editor");
        }
    }

    public void printHeader(long vg, int[] width, int[] fbWidth, int[] height, NVGColor color) {
        NanoVG.nvgBeginPath(vg);
        float pxRatio = (float) fbWidth[0] / (float) width[0];
        NanoVG.nvgRGBA((byte) 30, (byte) 32, (byte) 48, (byte) 255, color);
        float bannerWidth = ct.gui.getFontSize() * 2;

        NanoVG.nvgMoveTo(vg, 0, 0.0f + bannerWidth / 2);
        NanoVG.nvgLineTo(vg, width[0], 0.0f + bannerWidth / 2);

        NanoVG.nvgStrokeColor(vg, color);
        NanoVG.nvgStrokeWidth(vg, bannerWidth);
        NanoVG.nvgStroke(vg);

        NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
        NanoVG.nvgFontSize(vg, ct.gui.getFontSize());
        NanoVG.nvgFontFace(vg, Gui.FONT_NAME);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER);
        NanoVG.nvgText(vg, (float) width[0] / 2, ct.gui.getFontSize(), ct.filePath);
    }

    private ArrayList<String> getLines(String input, int width, float charWidth) {
        ArrayList<String> lines = new ArrayList<>();
        if (charWidth * input.length() > width) {
            splitLine(lines, input, width, charWidth, 0);
            int i = 0;
            while (i < lines.size()) {
                if (charWidth * lines.get(i).length() > width) {
                    String replaceLine = lines.get(i);
                    lines.remove(i);
                    splitLine(lines, replaceLine, width, charWidth, i);
                }
                i++;
            }
        } else {
            lines.add(input);
        }
        return lines;
    }


    private void splitLine(ArrayList<String> lines, String input, int width, float charWidth, int currentIndex) {
        if (charWidth * input.length() > width) {
            float amount = width / charWidth;
            lines.add(currentIndex, input.substring((int) amount));
            lines.add(currentIndex, input.substring(0, (int) amount));
        } else {
            lines.add(input);
        }
    }

    private int getLineBreaks(int maxCharLine) {
        List<String> wordList = ct.ed.getWordList();
        int lineBreaks = 0;
        for (int i = 0; i < currentLine + 1; i++) {
            if (i == currentLine) {
                if (xCursorPos + 1 > maxCharLine) {
                    for (int j = 0; j < wordList.get(i).length() / maxCharLine; j++) {
                        if ((1 + j) * maxCharLine - 1 < xCursorPos) lineBreaks++;
                    }
                }
            } else {
                if (wordList.get(i).length() > maxCharLine) {
                    for (int j = 0; j < wordList.get(i).length() / maxCharLine; j++) {
                        lineBreaks++;
                    }
                }
            }
        }
        return lineBreaks;
    }
}
