package Util.BasicStatisticExperiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import ConstantVar.ConstantValue;
import Exp.ClassifyByVector.APKSiggraphSet;
import Exp.ClassifyByVector.FregraphVector;
// import Exp.ClassifyByVector.SiggraphVector;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import Util.Tool.DirDelete;
import Util.Tool.ReadFile;
import weka.filters.unsupervised.attribute.RemoveUseless;

public class GenerateTrainTestFile {
    public static String arff = "";
    public static String clusterType = "";

    public static String testDataString;
    public static String trainDataString;

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
    public static Instant instant;
    public static ZonedDateTime zdt;
    public static String output;

    // Directory containing the feature models for various supports and similarity
    // threshold
    public static String featureDataString = "";
    public static String resultsDirString = "./results";
    public static String dataType;
    public static String tmpfs = "";
    // public static String weightScorePathString;

    public static ArrayList<String> TimeList = new ArrayList<>();

    public static int numApps = 0;

    public static void main(String[] args) {
        Option helpOpt = new Option("h", "help", false, "print this message");
        Options options = new Options();

        Option veborseOpt = Option.builder("v").longOpt("verbose")
                .desc("Print more messages")
                .build();
        options.addOption(veborseOpt);

        Option familyDirOpt = Option.builder("family").longOpt("families-dir")
                .argName("path")
                .hasArg()
                .required()
                .desc("Specify the families directory")
                .build();
        Option trainFamilyDirOpt = Option.builder("train").longOpt("train-families-dir")
                .argName("path")
                .hasArg()
                .desc("Specify the train families directory")
                .build();

        Option testFamilyDirOpt = Option.builder("test").longOpt("test-families-dir")
                .argName("path")
                .hasArg()
                .desc("Specify the test families directory")
                .build();

        Option featureDirOpt = Option.builder("feature").longOpt("features-dir")
                .argName("path")
                .hasArg()
                .required()
                .desc("Specify the feature directory")
                .build();

        Option resultsDirOpt = Option.builder("results").longOpt("results-dir")
                .argName("path")
                .hasArg()
                .desc("Specify the output results directory")
                .build();
        Option tmpfsDirOpt = Option.builder("t").longOpt("tmpfs-dir")
                .argName("path")
                .hasArg()
                .required()
                .desc("Path to the tmpfs directory")
                .build();
        options.addOption(tmpfsDirOpt);

        // create Options object
        options.addOption(helpOpt);
        options.addOption(familyDirOpt);
        options.addOption(trainFamilyDirOpt);
        options.addOption(testFamilyDirOpt);
        options.addOption(featureDirOpt);
        options.addOption(resultsDirOpt);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("OptionsCli", options);
                System.exit(0);
            }

            featureDataString = cmd.getOptionValue(featureDirOpt);
            System.out.println("Using as feature directory: " + featureDataString);
            System.out.println("Family opt: " + cmd.getOptionValue(familyDirOpt));
            ConstantValue.getVar(cmd.getOptionValue(familyDirOpt));
            System.out.println("Using Families Dir Path: " + ConstantValue.FAMILIESDIRPATH_STRING);

            if (cmd.hasOption(veborseOpt)) {
                System.out.println("Vebose: true");
                ConstantValue.setVerbose(true);
            }

            if (cmd.hasOption(tmpfsDirOpt)) {
                tmpfs = cmd.getOptionValue(tmpfsDirOpt);
            }

            if (!cmd.hasOption(trainFamilyDirOpt) && !cmd.hasOption(testFamilyDirOpt)) {
                System.out.println("Either a train or test dir is needed to continue. Exiting");
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("OptionsCli", options);
                System.exit(-1);
            }

            if (cmd.hasOption(trainFamilyDirOpt)) {
                trainDataString = cmd.getOptionValue(trainFamilyDirOpt);
                System.out.println("Using train dir: " + trainDataString);
            }

            if (cmd.hasOption(testFamilyDirOpt)) {
                testDataString = cmd.getOptionValue(testFamilyDirOpt);
                System.out.println("Using test dir: " + testDataString);
            }
            if (cmd.hasOption(resultsDirOpt)) {
                resultsDirString = cmd.getOptionValue(resultsDirOpt);
                System.out.println("Using results dir: " + resultsDirString);
            } else {
                System.out.println("No results dir provided, using default current directory");
            }
        } catch (ParseException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("OptionsCli", options);
            System.exit(1);
        }
        long startTime = System.currentTimeMillis();

        // TODO Auto-generated method stub
        Set<String> supportSet = new HashSet<>();
        supportSet.add("0.1");
        // supportSet.add("0.2");
        // supportSet.add("0.3");
        // supportSet.add("0.4");
        // supportSet.add("0.5");
        // supportSet.add("0.6");
        // supportSet.add("0.7");
        // supportSet.add("0.8");
        // supportSet.add("0.9");

        ArrayList<Double> simTrSetInt = new ArrayList<>();
        // simTrSetInt.add(0.1D);
        // simTrSetInt.add(0.2D);
        // simTrSetInt.add(0.3D);
        // simTrSetInt.add(0.4D);
        // simTrSetInt.add(0.5D);
        // simTrSetInt.add(0.6D);
        // simTrSetInt.add(0.7D);
        simTrSetInt.add(0.8D);
        // simTrSetInt.add(0.9D);
        int len_simTrSetInt = simTrSetInt.size();

        Iterator<String> iterator = supportSet.iterator();

        while (iterator.hasNext()) {
            String sur = iterator.next();

            for (int i = 0; i < len_simTrSetInt; i++) {
                double simTr = simTrSetInt.get(i);
                // System.out.println("Doing support: " + sur);
                ConstantValue.getVar().minScoreSim = simTr;
                System.out.println(String.format("Doing support: %s and similarity threshold: %s",
                        sur, ConstantValue.getVar().minScoreSim));
                String simTrString = String.valueOf(simTr);
                oneSupportType(sur, simTrString);
            }
            System.out.println();
        }

        long endTime = System.currentTimeMillis();
        long useTime = endTime - startTime;

        instant = Instant.ofEpochMilli(useTime);
        zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
        output = formatter.format(zdt);

        // System.out.println("[GenerateTrainTestFile] Analysis finished. Total apps:
        // %d, duration: %s" + output);
        System.out.println(String.format("[GenerateTrainTestFile] Analysis finished. Total apps: %d, duration: %s",
                numApps, output));
    }

    public static void oneSupportType(String support, String threshold) {
        try {
            TimeList = new ArrayList<>();
            // Assings global clusterType
            clusterType = "Im--" + support + "--" + ConstantValue.getVar().minScoreSim;

            // Generate a fregraph vector. A fregraph contains a list of paths to the
            // subgraphs, and a weight assigned to it (from the subgraphs).
            // FregraphVector fregraphVector = new FregraphVector(vector);
            String featureSpaceFile = Paths.get(featureDataString, String.format("featureSpace-%s-%s.model",
                    support, threshold)).toString();
            System.out.println("[GenerateTrainTestFile] Using feature file " + featureSpaceFile);
            FregraphVector fregraphVector = constructVector(featureSpaceFile);

            if (trainDataString != null) {
                System.out.println("[GenerateTrainTestFile] Start write train file");
                writeTrainFile(fregraphVector);
            }
            if (testDataString != null) {
                writeTestFile(fregraphVector);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public static FregraphVector constructVector(String spaceFile) {
        FregraphVector vector = new FregraphVector();
        System.out.println("[GenerateTrainTestFile] Loading feature space vector");
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(spaceFile));
            vector = (FregraphVector) in.readObject();
            in.close();
            if (ConstantValue.isVerbose()) {
                vector.printFregraphVectorInfo();
            }
            System.out.println(
                    "[GenerateTrainTestFile] Feature vector loaded. Number of features: " + vector.fregraphList.size());
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.exit(-1);
        }
        System.out.println("Finish constructing vector");
        return vector;
    }

    public static FregraphVector readFeatureSpace(String readFilePath) {

        try {

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(readFilePath));
            FregraphVector vector = (FregraphVector) in.readObject();
            in.close();
            System.out.println(vector.fregraphList.size());
            return vector;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public static void writeTrainFile(FregraphVector vector) {
        String dirName = Paths.get(trainDataString).getFileName().toString();
        String writeFileString = Paths.get(resultsDirString, dirName, dirName + "." + clusterType + "-train.arff")
                .toString();
        if (Paths.get(writeFileString).toFile().exists()) {
            System.out.println(
                    String.format("[GenerateTrainTestFile] File %s already exists, skipping", writeFileString));
        } else {
            getHeader(vector);
            dataType = "test";
            File dataFile = new File(trainDataString);
            File fals[] = dataFile.listFiles();
            System.out.println("[GenerateTrainTestFile] Number of files: " + fals.length);
            for (int i = 0; i < fals.length; i++) {
                String falFileString = fals[i].getAbsolutePath() + "/";
                System.out.println("[GenerateTrainTestFile] Writting row in training file for " + falFileString);
                arff += getOneFalVector(falFileString, vector);
            }
            writeFile(writeFileString);
        }

    }

    public static void writeTestFile(FregraphVector vector) {
        String dirName = Paths.get(testDataString).getFileName().toString();
        String writeFileString = Paths.get(resultsDirString, dirName, dirName + "." + clusterType + "-test.arff")
                .toString();

        if (Paths.get(writeFileString).toFile().exists()) {
            System.out.println(
                    String.format("[GenerateTrainTestFile] File %s already exists, skipping", writeFileString));
        } else {
            getHeader(vector);
            dataType = "test";

            File dataFile = new File(testDataString);
            File fals[] = dataFile.listFiles();
            for (int i = 0; i < fals.length; i++) {
                String falFileString = fals[i].getAbsolutePath() + "/";
                // System.out.println("Family: " + falFileString);
                System.out.println("[GenerateTrainTestFile] Writting row in test file for " + falFileString);
                arff += getOneFalVector(falFileString, vector);
            }
            writeFile(writeFileString);
        }
    }

    public static void writeTimeFile() {
        String writeFile = resultsDirString + clusterType + "-Time.txt";

        checkParentDir(writeFile);

        try {
            File file = new File(writeFile);
            FileWriter fWriter = new FileWriter(file);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            for (int i = 0; i < TimeList.size(); i++) {
                String time = TimeList.get(i);
                bWriter.write(time + "\n");
            }
            bWriter.close();
            fWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void getHeader(FregraphVector vector) {
        arff = "@relation FamilyClassificationTrainingDataset\n";
        arff += "@attribute sha256 string\n";
        arff += vector.getFeatureNameList();
        arff += "@attribute label {";
        String labelList = "";
        File dataFile = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
        File falDir[] = dataFile.listFiles();
        for (int i = 0; i < falDir.length; i++) {
            String name = falDir[i].getName();
            labelList += name + ",";
        }
        labelList = labelList.substring(0, labelList.length() - 1);
        arff += labelList + "}\n\n";
        arff += "@data\n";
    }

    public static String getOneFalVector(String falDirPath, FregraphVector vector) {
        String falString = "";
        File falFile = new File(falDirPath);
        File apks[] = falFile.listFiles();
        String dirName = Paths.get(falDirPath).getParent().getFileName().toString();
        String subDataString;
        String subDataFileString;

        for (int i = 0; i < apks.length; i++) {
            String apkName = apks[i].getName();
            if (apkName.endsWith(".apk")) {
                String apkFilePath = apks[i].getAbsolutePath();
                // Check if sub-data string exists already
                subDataFileString = Paths
                        .get(resultsDirString, dirName, "sub_results",
                                apkName + "." + clusterType + "-" + dataType + ".txt")
                        .toString();
                if (Files.exists(Paths.get(subDataFileString))) {
                    subDataString = ReadFile.readThisFile(subDataFileString);
                } else {
                    subDataString = getOneAPKVector(apkFilePath, vector) + "\n";
                    writeSubData(subDataFileString, subDataString);
                }

                falString += subDataString;
                // System.out.println(falDirPath + ":" + apks[i].getName() + " #" + i);
                System.out.println(String.format("[GenerateTrainFile] Added sample %d/%d", i, apks.length));
            }
        }
        return falString;
    }

    public static String getOneAPKVector(String apkFilePath, FregraphVector vector) {
        // APKSiggraphSet siggraphSet = new APKSiggraphSet(apkFilePath, clusterType);
        // String apkFileString = Paths.get(apkFilePath).getFileName().toString();
        // String tmpfsPathString = Paths.get(tmpfs, "apktool",
        // apkFileString).toString();
        numApps += 1;
        if (ConstantValue.isVerbose()) {
            System.out.println("[GenerateTrainTestFile] Start analyze: " + apkFilePath);
        }
        APKSiggraphSet siggraphSet = new APKSiggraphSet(apkFilePath, clusterType, tmpfs);

        if (!siggraphSet.getSuccessAPKSiggraphSet()) {
            System.out.println("[GenerateTrainTestFile] Failed to analyze this application, defaulting to 0");
            String resultString = "";
            resultString += siggraphSet.getActualFalName() + ",";
            for (int i = 0; i < vector.fregraphList.size(); i++) {
                resultString += "0.0" + ",";
            }
            // This will result in an error when read by Weka: nominal value not declared in
            // header (for the family label)
            resultString += siggraphSet.getActualFalName();

            String apktoolDiString = siggraphSet.getApktoolPathString();
            File apktoolDir = new File(apktoolDiString);

            if (apktoolDir.exists()) {
                DirDelete.deleteDirFiles(apktoolDir);
            }
            return resultString;
        }

        /*
         * For unknown samples, the weight score of each function is not known
         * at the beginning, and an initial score list is required.
         * Naming each generated subgraph will not affect the later. During specific
         * detection, it will be based on the target family.
         * The score list of subgraphs is recalculated
         * 
         */
        long startTime = System.currentTimeMillis();

        siggraphSet.storeAllSubgraphs();
        siggraphSet.storeAllSignificantSubgraphs();
        if (ConstantValue.isVerbose()) {
            System.out.println("\n[GenerateTrainTestFile] Finished generating fregraphs");
            siggraphSet.printSimGraphs();
        }

        siggraphSet.generateVector(vector);

        if (ConstantValue.isVerbose()) {
            System.out.println("\n[GenerateTrainTestFile] Finished generating sample feature vector");
            siggraphSet.printFeatureVector();
        }

        // String vectorString=siggraphSet.getFeatureIntVector();
        String vectorString = siggraphSet.getFeatureDoubleVector();
        long endTime = System.currentTimeMillis();
        long useTime = endTime - startTime;
        String time = String.valueOf(useTime);
        TimeList.add(time);

        String apktoolDiString = siggraphSet.getApktoolPathString();
        File apktoolDir = new File(apktoolDiString);

        if (apktoolDir.exists()) {
            // deleteDirFiles(apktoolDir);
            // System.out.println("[GenerateTrainTestFile] Removing apktool directory " +
            // apktoolDiString);
            DirDelete.deleteDirFiles(apktoolDir);
        }
        return vectorString;
    }

    public static String emptyVector() {
        return "";
    }

    public static void writeFile(String writeFilePath) {
        checkParentDir(writeFilePath);
        try {
            File file = new File(writeFilePath);
            FileWriter fWriter = new FileWriter(file);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            bWriter.write(arff);
            bWriter.close();
            fWriter.close();
            System.out.println("[GenerateTrainTestFile] File saved as " + writeFilePath);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void writeSubData(String writeFilePath, String subDataString) {
        checkParentDir(writeFilePath);
        try {
            File file = new File(writeFilePath);
            FileWriter fWriter = new FileWriter(file);
            BufferedWriter bWriter = new BufferedWriter(fWriter);
            bWriter.write(subDataString);
            bWriter.close();
            fWriter.close();
            System.out.println("[GenerateTrainTestFile] Sub-data saved as " + writeFilePath);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    public static void checkParentDir(String filePathString) {
        Path writeFilePathPath = Paths.get(filePathString);
        Path parentDirPathPath = writeFilePathPath.getParent();

        if (!Files.exists(parentDirPathPath)) {
            try {
                Files.createDirectories(parentDirPathPath);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }

}
