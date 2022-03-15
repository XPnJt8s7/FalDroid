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

public class GenerateTrainFile {
	public static String arff = "";
	public static String clusterType = "";

	public static String testDataString = "./FamilyModel/test/";

	// public static String trainDataString = "./FamilyModel/train/";
	public static String trainDataString = "./Family";
	public static String resultDataString = "./FamilyFastModel/test1/";
	// public static String
	// featureSpaceFilePath="/home/fan/data/Family/small-sample-exp/exp4/result/feature-0.5.txt";

	public static ArrayList<String> TimeList = new ArrayList<>();

	public static void main(String[] args) {
		// First argument: training set
		// If second argument: test set

		// TODO Auto-generated method stub
		Set<String> supportSet = new HashSet<>();
		supportSet.add("0.1");
		supportSet.add("0.2");
		// supportSet.add("0.3");
		// supportSet.add("0.4");
		// supportSet.add("0.5");
		// supportSet.add("0.6");
		// supportSet.add("0.7");
		// supportSet.add("0.8");
		// supportSet.add("0.9");

		ArrayList<Double> simTrSetInt = new ArrayList<>();
		simTrSetInt.add(0.1D);
		simTrSetInt.add(0.2D);
		simTrSetInt.add(0.3D);
		simTrSetInt.add(0.4D);
		simTrSetInt.add(0.5D);
		simTrSetInt.add(0.6D);
		simTrSetInt.add(0.7D);
		simTrSetInt.add(0.8D);
		simTrSetInt.add(0.9D);
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
				oneSupportType(sur);
			}
			System.out.println();

			// System.out.println(String.format("Support: %s", sur));
			// oneSupportType(sur);
			// System.out.println();
		}
		// oneSupportType("0.6");
	}

	public static void oneSupportType(String support) {
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
			System.out
					.println("******************************** Features and weights (Support " + support
							+ ") ********************************");
			for (int i = 0; i < fregraphVector.fregraphList.size(); i++) {
				System.out.println(fregraphVector.fregraphList.get(i).getFeatureLabel() + " --- " +
						fregraphVector.fregraphList.get(i).weight);
			}

			String featureSpaceFile = "./FamilyModel/exp01/result/featureSpace/featureSpace-"
					+ support + ".model";
			writeFeatureSpace(featureSpaceFile, fregraphVector);

			// FregraphVector fregraphVector=readFeatureSpace(featureSpaceFile);

			// writeTrainFile(fregraphVector);
			// writeTestFile(fregraphVector);
			// writeTimeFile();
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
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.exit(-1);
		}
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
		getHeader(vector);
		File dataFile = new File(trainDataString);
		File fals[] = dataFile.listFiles();
		for (int i = 0; i < fals.length; i++) {
			String falFileString = fals[i].getAbsolutePath() + "/";
			arff += getOneFalVector(falFileString, vector);
		}
		String writeFile = resultDataString + clusterType + "-train.arff";
		writeFile(writeFile);
	}

	public static void writeTestFile(FregraphVector vector) {
		getHeader(vector);

		File dataFile = new File(testDataString);
		File fals[] = dataFile.listFiles();
		for (int i = 0; i < fals.length; i++) {
			String falFileString = fals[i].getAbsolutePath() + "/";
			System.out.println("Family: " + falFileString);
			arff += getOneFalVector(falFileString, vector);
		}
		String writeFile = resultDataString + clusterType + "-test.arff";
		writeFile(writeFile);
	}

	public static void writeTimeFile() {
		String writeFile = resultDataString + clusterType + "-Time.txt";

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
		for (int i = 0; i < apks.length; i++) {
			String apkName = apks[i].getName();
			if (apkName.endsWith(".apk")) {
				String apkFilePath = apks[i].getAbsolutePath();
				// Check if there's an apktool output for this apk
				String apktoolFilePathString = falDirPath + "/apktool/" + apkName;
				Path apktoolFilePath = Paths.get(apktoolFilePathString);

				if (!Files.exists(apktoolFilePath)) {
					continue;
				}

				falString += getOneAPKVector(apkFilePath, vector) + "\n";
				// System.out.println(falDirPath + ":" + apks[i].getName() + " #" + i);
				System.out.println(String.format("[GenerateTrainFile] Added sample %d/%d", i, apks.length));
			}
		}
		return falString;
	}

	// public static String getOneFamilyVector(String familyDirString,
	// FregraphVector vector) {
	// String familyString = "";
	// File familyFile = new File(Paths.get(familyDirString, "apktool").toString());

	// File apksFileList[] = familyFile.listFiles();
	// for (int i = 0; i < apksFileList.length; i++) {
	// File apktoolFile = apksFileList[i];
	// String apkName = apktoolFile.getName();
	// familyString += getOneAPKVector(apktoolFile.getPath(), apkName, vector);
	// }

	// return familyString;
	// }

	public static String getOneAPKVector(String apkFilePath, FregraphVector vector) {
		APKSiggraphSet siggraphSet = new APKSiggraphSet(apkFilePath, clusterType);

		/*
		 * For unknown samples, the weight score of each function is not known
		 * at the beginning, and an initial score list is required to
		 * Naming each generated subgraph will not affect the later. During specific
		 * detection, it will be based on the target family.
		 * The score list of subgraphs is recalculated
		 * 
		 */
		long startTime = System.currentTimeMillis();

		siggraphSet.storeAllSubgraphs();
		siggraphSet.storeAllSignificantSubgraphs();

		siggraphSet.generateVector(vector);

		// String vectorString=siggraphSet.getFeatureIntVector();
		String vectorString = siggraphSet.getFeatureDoubleVector();
		long endTime = System.currentTimeMillis();
		long useTime = endTime - startTime;
		String time = String.valueOf(useTime);
		TimeList.add(time);
		return vectorString;
	}

	// public static String getOneAPKVector(String apkFilePath, String apkName,
	// FregraphVector vector) {
	// APKSiggraphSet siggraphSet = new APKSiggraphSet(apkFilePath, clusterType,
	// apkName);

	// /*
	// * For unknown samples, the weight score of each function is not known
	// * at the beginning, and an initial score list is required to
	// * Naming each generated subgraph will not affect the later. During specific
	// * detection, it will be based on the target family.
	// * The score list of subgraphs is recalculated
	// *
	// */
	// long startTime = System.currentTimeMillis();

	// siggraphSet.storeAllSubgraphs();
	// siggraphSet.storeAllSignificantSubgraphs();

	// siggraphSet.generateVector(vector);

	// // String vectorString=siggraphSet.getFeatureIntVector();
	// String vectorString = siggraphSet.getFeatureDoubleVector();
	// long endTime = System.currentTimeMillis();
	// long useTime = endTime - startTime;
	// String time = String.valueOf(useTime);
	// TimeList.add(time);
	// return vectorString;
	// }

	public static void writeFile(String writeFilePath) {
		checkParentDir(writeFilePath);
		try {
			File file = new File(writeFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(arff);
			bWriter.close();
			fWriter.close();
			System.out.println("[GenerateTrainFile] File saved as " + writeFilePath);

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
