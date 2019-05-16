import java.io.*;
import java.util.ArrayList;

public class FileRead {


    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public FileRead(String filePath) {
        this.filePath = filePath;
    }

    public ArrayList<String> loadFile() {
        try {
            BufferedReader file = new BufferedReader(new FileReader(new File(this.filePath)));
            String line;
            ArrayList<String> lines = new ArrayList<>();
            while ((line = file.readLine()) != null) {
                lines.add(line.toUpperCase());
            }
            return lines;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


    }
}

