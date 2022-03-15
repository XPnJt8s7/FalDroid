package Util.FamilyStatistic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import APKData.SmaliGraph.SICG;
import ConstantVar.ConstantValue;
import Util.PrintTime;

public class SingleAnalysis {
    public static void main(String[] args) {
        // arg 1: apk path
        // arg 2: family
        if (args.length != 2) {
            System.out.println("[SingleAnalysis] This program requiers one argument");
            System.exit(-1);
        }

        String apkPathString = args[0];
        String familyString = args[1];

        Path apkPath = Paths.get(apkPathString);
        if (!Files.exists(apkPath)) {
            System.out.println(String.format("[SingleAnalysis] File \"%s\" doesn't exists, exiting", apkPath));
            System.exit(-1);
        }

        // Create apktool folder if it doesn't exists
        Path apktoolPath = Paths.get(ConstantValue.getVar().FAMILIESDIRPATH_STRING, familyString,
                "apktool");
        Path outputPath = Paths.get(ConstantValue.getVar().FAMILIESDIRPATH_STRING, familyString,
                "apktool", apkPath.getFileName().toString());

        if (!Files.exists(apktoolPath)) {
            try {
                Files.createDirectories(apktoolPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.exit(-1);
            }
        }

        Path sicgPath = Paths.get(ConstantValue.getVar().FAMILIESDIRPATH_STRING, familyString,
                "apktool", apkPath.getFileName().toString(), "SICG");

        if (!Files.exists(sicgPath)) {
            System.out.println("[SingleAnalysis] Analyzing file " + apkPath.toString());

            long startTime = System.currentTimeMillis();
            // outputPath must be like "families_dir/family/apktool/xyz.apk"
            SICG sicg = new SICG(apkPath.toString(), outputPath.toString());

            long endTime = System.currentTimeMillis();
            long useTime = endTime - startTime;

            String duration = PrintTime.PrintMilis(useTime);
            System.out.println("[SingleAnalysis] APK analysis finished, duration: " + duration);
        }
    }
}
