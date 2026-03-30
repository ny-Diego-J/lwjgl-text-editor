package dj.main;

import java.io.*;

public class FileManager {
    Controller ct;

    public FileManager(Controller ct) {
        this.ct = ct;
    }


    public void readFile(String path) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            boolean isFistLine = true;
            while ((line = br.readLine()) != null) {
                if (isFistLine) ct.ed.inputs.getFirst().append(line);
                else ct.ed.inputs.add(new StringBuilder(line));
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
