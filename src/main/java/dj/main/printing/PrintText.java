package dj.main.printing;

import dj.main.*;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NanoVG;

import java.util.ArrayList;
import java.util.List;

public class PrintText {
    private final Controller ct;
    public int maxCharLine;
    private float textHeight;
    private int currentLine;
    private int xCursorPos;
    private float pxRatio;
    private float charWidth;
    private int lineBreaks;


    public PrintText(Controller ct) {
        this.ct = ct;
    }

    public void printAll(long vg, int[] width, int[] fbWidth, int[] height, NVGColor color) {
        currentLine = ct.ed.getCurrentLine();
        xCursorPos = ct.ed.getxCursorPos();
        textHeight = ct.gui.getyOffset() + ct.gui.getBannerOffset();
        pxRatio = (float) fbWidth[0] / (float) width[0];
        charWidth = NanoVG.nvgTextBounds(vg, 0, 0, "A", (float[]) null);
        maxCharLine = (int) (width[0] / charWidth);
        lineBreaks = getLineBreaks(maxCharLine);

        printLineBackground(vg, width, color);
        printText(vg, width, height, color);
        printCursor(vg, color);
        printHeader(vg, width, height, color);
    }

    private void printLineBackground(long vg, int[] width, NVGColor color) {
        float baseHeight = lineBreaks * ct.gui.getFontSize() + currentLine * ct.gui.getFontSize() + ct.gui.getBannerOffset();
        float bannerCenterY = baseHeight + ct.gui.getyOffset() + (ct.gui.getFontSize() / 2.0f);

        NanoVG.nvgRGBA((byte) 47, (byte) 51, (byte) 77, (byte) 255, color);
        NanoVG.nvgBeginPath(vg);

        // draw current line background
        NanoVG.nvgMoveTo(vg, 0.0f, bannerCenterY);
        NanoVG.nvgLineTo(vg, width[0], bannerCenterY);

        NanoVG.nvgStrokeColor(vg, color);
        NanoVG.nvgStrokeWidth(vg, ct.gui.getFontSize());
        NanoVG.nvgStroke(vg);
    }

    private void printCursor(long vg, NVGColor color) {
        float baseHeight = lineBreaks * ct.gui.getFontSize() + currentLine * ct.gui.getFontSize() + ct.gui.getBannerOffset();
        int xPos = xCursorPos % maxCharLine;
        NanoVG.nvgRGBA((byte) 208, (byte) 204, (byte) 178, (byte) 255, color);
        float cursorTop = baseHeight + ct.gui.getyOffset();
        float cursorBottom = cursorTop + ct.gui.getFontSize();

        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgMoveTo(vg, charWidth * xPos + 10.0f, cursorTop);
        NanoVG.nvgLineTo(vg, charWidth * xPos + 10.0f, cursorBottom);

        NanoVG.nvgStrokeColor(vg, color);
        NanoVG.nvgStrokeWidth(vg, 2.0f);
        NanoVG.nvgStroke(vg);
    }

    private void printText(long vg, int[] width, int[] height, NVGColor color) {
        float fontSize = ct.gui.getFontSize();
        NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
        NanoVG.nvgFontSize(vg, fontSize);
        NanoVG.nvgFontFace(vg, Gui.FONT_NAME);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);

        NanoVG.nvgRGBA((byte) 47, (byte) 51, (byte) 77, (byte) 255, color);
        NanoVG.nvgBeginPath(vg);

        // draw current line background


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
        ct.gui.setTextHeight(textHeight);

        if (!ct.hasStarted) {
            NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
            NanoVG.nvgFontSize(vg, 54.0f);
            NanoVG.nvgFontFace(vg, Gui.FONT_NAME);
            NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_LEFT | NanoVG.NVG_ALIGN_TOP);
            NanoVG.nvgText(vg, 10.0f, height[0] - 100.0f, "Press anything to start the editor");
        }
    }

    private void printHeader(long vg, int[] width, int[] height, NVGColor color) {
        NanoVG.nvgBeginPath(vg);
        NanoVG.nvgRGBA((byte) 30, (byte) 32, (byte) 48, (byte) 255, color);
        //NanoVG.nvgRGBA((byte) 0, (byte) 0, (byte) 0, (byte) 255, color);
        float bannerHeight = ct.gui.getFontSize() + ct.gui.getFontSize() / 2;

        float yPosition = bannerHeight / 2 + ct.gui.getyOffset();
        ct.gui.setBannerOffset(bannerHeight);

        NanoVG.nvgMoveTo(vg, 0, yPosition);
        NanoVG.nvgLineTo(vg, width[0], yPosition);

        NanoVG.nvgStrokeColor(vg, color);
        NanoVG.nvgStrokeWidth(vg, bannerHeight);
        NanoVG.nvgStroke(vg);

        NanoVG.nvgBeginFrame(vg, width[0], height[0], pxRatio);
        NanoVG.nvgFontSize(vg, ct.gui.getFontSize());
        NanoVG.nvgFontFace(vg, Gui.FONT_NAME);
        NanoVG.nvgTextAlign(vg, NanoVG.NVG_ALIGN_CENTER);
        NanoVG.nvgText(vg, (float) width[0] / 2, yPosition + ct.gui.getFontSize() / 2, ct.filePath);
    }

    public ArrayList<String> getLines(String input, int width, float charWidth) {
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
        int breaks = 0;
        for (int i = 0; i < currentLine + 1; i++) {
            if (i == currentLine) {
                if (xCursorPos + 1 > maxCharLine) {
                    for (int j = 0; j < wordList.get(i).length() / maxCharLine; j++) {
                        if ((1 + j) * maxCharLine - 1 < xCursorPos) breaks++;
                    }
                }
            } else {
                if (wordList.get(i).length() > maxCharLine) {
                    for (int j = 0; j < wordList.get(i).length() / maxCharLine; j++) {
                        breaks++;
                    }
                }
            }
        }
        return breaks;
    }

    public int getLines(int maxCharLine) {
        List<String> wordList = ct.ed.getWordList();
        int breaks = wordList.size();
        for (int i = 0; i < wordList.size() - 1; i++) {
            if (xCursorPos + 1 > maxCharLine) {
                for (int j = 0; j < wordList.get(i).length() / maxCharLine; j++) {
                    if ((1 + j) * maxCharLine - 1 < xCursorPos) breaks++;
                }
            }
        }
        return breaks;
    }
}
