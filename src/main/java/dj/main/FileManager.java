package dj.main;

import java.io.*;

public class FileManager {
    private Controller ct;

    public FileManager(Controller ct) {
        this.ct = ct;
    }


    public void readFile(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            boolean isFistLine = true;
            while ((line = br.readLine()) != null) {
                if (isFistLine) {
                    ct.ed.appendAtFirstLine(line);
                    isFistLine = false;
                } else ct.ed.addLineWithText(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveFile() {
        printToFile(ct.filePath, ct.ed.contentToString());
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
