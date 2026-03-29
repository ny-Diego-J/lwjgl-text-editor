package dj.main;

import java.io.*;

public class SaveFile {
    Controller ct;

    public SaveFile(Controller ct) {
        this.ct = ct;
    }


    public void saveFile() {
        printToFile(ct.filePath, contentToString());
    }

    private String contentToString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ct.ed.inputs.size(); i++) {
            sb.append(ct.ed.inputs.get(i));
            if (i != ct.ed.inputs.size() - 1) sb.append("\n");
        }
        return sb.toString();
    }

    private void printToFile(String header, String content) {
        try {
            FileWriter myWriter = new FileWriter(header);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            ct.logger.warning(e.getMessage());
        }
    }
}
