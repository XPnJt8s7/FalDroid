package Util.Tool;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class ReadFile {
    public static String readThisFile(String filePathString) {

        String output = "";
        try {
            List<String> lines = Files.readAllLines(Paths.get(filePathString), StandardCharsets.US_ASCII);
            for (String string : lines) {
                output += string + "\n";
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("[ReadFile] Could not read file");
            System.exit(-1);
        }
        return output;
    }
}
