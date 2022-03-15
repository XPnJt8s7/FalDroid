package Util.FamilyStatistic;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import ConstantVar.ConstantValue;

public class SymlinkAnalysis {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // First argument: goodmal file
        // Second argument: apk source directory
        if (args.length != 2) {
            System.out.println("This program requiers two arguments");
            System.exit(-1);
        }

        String goodmalPathString = args[0];
        String apkSourcePathString = args[1];

        Path goodmalPath = Paths.get(goodmalPathString);
        if (!Files.exists(goodmalPath)) {
            System.out.println(String.format("File \"%s\" doesn't exists, exiting", goodmalPath));
        }
        Path apkSourcePath = Paths.get(apkSourcePathString);
        if (!Files.exists(apkSourcePath)) {
            System.out.println(String.format("Dir \"%s\" doesn't exists, exiting", apkSourcePath));
        }

        // String familyPathString = ConstantValue.getVar().FAMILIESDIRPATH_STRING;

        try (BufferedReader br = new BufferedReader(new FileReader(goodmalPathString))) {
            int n = 0;
            for (String line; (line = br.readLine()) != null;) {
                n++;
                if (n == 1) {
                    continue;
                }

                // System.out.println(String.format("This is a line: %s", line));
                String hashString = line.split(",")[0];
                String familyString;

                String split1 = line.split(",")[1];
                // System.out.println(String.format("split1: %s", split1));

                if (split1.equals("1")) {
                    familyString = "Malware";
                } else if (split1.equals("0")) {
                    familyString = "Goodware";
                } else {
                    familyString = "Not Good";
                }

                System.out.println(String.format("Hash: %s, family: %s", hashString, familyString));

                break;
            }
            // line is not visible here.
        }
    }
}
