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

import ConstantVar.ConstantValue;
import Exp.ClassifyByVector.APKSiggraphSet;
import Exp.ClassifyByVector.FregraphVector;
import Exp.ClassifyByVector.SiggraphVector;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class GenerateFeatFile {
    public static String arff = "";
    public static String clusterType = "";
    public static String resultDataString = "./results/FamilyModel/";

    public static ArrayList<String> TimeList = new ArrayList<>();

    public static void main(String[] args) {
        Option helpOpt = new Option("h", "help", false, "print this message");
        Option familyDirOpt = Option.builder("f").longOpt("families-dir")
                .argName("path")
                .hasArg()
                .desc("Specify the families directory")
                .build();

        Option resultDirOpt = Option.builder("r").longOpt("result-features-dir")
                .argName("path")
                .hasArg()
                .desc("Specify the families directory")
                .build();

        // create Options object
        Options options = new Options();
        options.addOption(helpOpt);
        options.addOption(familyDirOpt);
        options.addOption(resultDirOpt);

        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption("help")) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp("OptionsCli", options);
                System.exit(0);
            }

            if (cmd.hasOption(familyDirOpt)) {
                ConstantValue.FAMILIESDIRPATH_STRING = cmd.getOptionValue(familyDirOpt);
                System.out.println("Using as families dir: " + ConstantValue.FAMILIESDIRPATH_STRING);
            } else {
                System.out.println("No families dir provided, using default: " + ConstantValue.FAMILIESDIRPATH_STRING);
            }

            if (cmd.hasOption(resultDirOpt)) {
                resultDataString = cmd.getOptionValue(resultDirOpt);
                System.out.println("Using result dir: " + resultDataString);
            } else {
                System.out.println("No result dir provided, using default: " + resultDataString);
            }

        } catch (ParseException e) {
            // e.printStackTrace();
            System.out.println(e.getMessage());
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("OptionsCli", options);
            System.exit(1);
        }

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

            // System.out.println(String.format("Support: %s", sur));
            // oneSupportType(sur);
            // System.out.println();
        }
    }

    public static void oneSupportType(String support, String threshold) {
        try {
            TimeList = new ArrayList<>();
            clusterType = "Im--" + support + "--" + ConstantValue.getVar().minScoreSim;

            // Generate a siggraph vector. A siggraph contains a list of all siggraphs,
            // and list of "feature" siggraphs.
            // A "feature" siggraph (or just feature) is a list of similar subgraphs (for a
            // given support), and a weight for these subraphs.
            SiggraphVector vector = new SiggraphVector(clusterType);

            // Generate a fregraph vector. A fregraph contains a list of paths to the
            // subgraphs, and a weight assigned to it (from the subgraphs).
            FregraphVector fregraphVector = new FregraphVector(vector);

            // for(int i=0;i<fregraphVector.fregraphList.size();i++){
            // System.out.println(fregraphVector.fregraphList.get(i).getFeatureLabel()+"---"+
            // fregraphVector.fregraphList.get(i).weight);
            // }

            fregraphVector.reorderFregraphList();
            // System.out
            // .println("******************************** Features and weights (Support " +
            // support
            // + ") ********************************");
            // for (int i = 0; i < fregraphVector.fregraphList.size(); i++) {
            // System.out.println(fregraphVector.fregraphList.get(i).getFeatureLabel() + "
            // --- " +
            // fregraphVector.fregraphList.get(i).weight);
            // }

            // String featureSpaceFile =
            // "./FamilyModel/exp01/result/featureSpace/featureSpace-"
            // + support + ".model";
            String featureSpaceFile = String.format(resultDataString + "/featureSpace/featureSpace-%s-%s.model",
                    support, threshold);

            writeFeatureSpace(featureSpaceFile, fregraphVector);

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

    public static void writeFeatureSpace(String writeFilePath, FregraphVector fregraphVector) {
        checkParentDir(writeFilePath);

        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(writeFilePath));
            oos.writeObject(fregraphVector);
            oos.flush();
            oos.close();
            System.out.println("[GenerateFeatFile] File saved as " + writeFilePath);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.exit(-1);
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
