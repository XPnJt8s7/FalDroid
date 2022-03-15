package Demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.sun.beans.finder.ConstructorFinder;

import ConstantVar.ConstantValue;
import Exp.ClassifyByVector.APKSiggraphSet;
import Exp.ClassifyByVector.Fregraph;
import Exp.ClassifyByVector.FregraphVector;
import Exp.ClassifyByVector.Siggraph;
import Exp.ClassifyByVector.SiggraphFeature;
import Exp.ClassifyByVector.SiggraphVector;
import sun.security.jca.GetInstance.Instance;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import wlsvm.WLSVM;

public class FeatureConstruction {

	// public static String
	// inputFilePath="/home/fan/data/Family/demoTest/demo3/1e303d5a5787dd32045a22c85b068547.apk";
	public static String inputFilePath = "";
	public static String spaceFile = "/home/fan/data/Family/small-sample-exp/exp4/featureSpace/featureSpace-0.5.model";
	public static String clusterType = "Im--0.5";
	public static ArrayList<String> familyNameList = new ArrayList<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int k = args.length;
		if (k == 0) {
			System.err.println("There is no argument!");
		} else {
			inputFilePath = args[0];
			constructFeature();
		}
		// constructFeature();
	}

	public static void constructFeature() {
		int k = inputFilePath.lastIndexOf("/");
		String outDirPath = inputFilePath.substring(0, k) + "/apktool/";
		String apkName = inputFilePath.substring(k + 1);
		outDirPath += apkName;
		String featureFilePath = outDirPath + "/SICG/feature.arff";

		FregraphVector vector = constructVector();
		String spaceString = generateVector(inputFilePath, vector);
		String resultArff = getHeader(vector);
		resultArff += spaceString;
		writeFeatrueSpaceFile(resultArff, featureFilePath);

		String finalFamilyName = getFamilyLabel(featureFilePath);
		String finalResultFilePath = outDirPath + "/SICG/familyName.txt";
		writeFinalResult(finalResultFilePath, finalFamilyName);
	}

	public static void writeFinalResult(String writeFilePath, String writeContent) {
		try {
			File file = new File(writeFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(writeContent);
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static String getFamilyLabel(String featureFilePath) {
		String familyName = "";
		try {
			File inputTestFile = new File(featureFilePath);
			ArffLoader TestAtf = new ArffLoader();
			TestAtf.setFile(inputTestFile);
			Instances testData = TestAtf.getDataSet();
			testData.setClassIndex(testData.numAttributes() - 1);
			ObjectInputStream ojs = new ObjectInputStream(new FileInputStream(ConstantValue.getVar().SVMMODEL));
			Classifier SVM = (Classifier) ojs.readObject();
			ojs.close();

			int index = Integer.valueOf((int) SVM.classifyInstance(testData.instance(0)));
			familyName = familyNameList.get(index);
			System.out.println(familyName);

			// File inputTrainFile=new
			// File("/home/fan/data/Family/file/Im--0.5-train.arff");
			// ArffLoader TrainAtf=new ArffLoader();
			// TrainAtf.setFile(inputTrainFile);
			// Instances trainData=TrainAtf.getDataSet();
			// trainData.setClassIndex(trainData.numAttributes()-1);
			//
			// Classifier SVM=new WLSVM();
			// String [] options=weka.core.Utils.splitOptions("-S 0 -t 0 -K 0 -D 3 -G 0.0 -R
			// 0.0 -N 0.5 "
			// + "-M 40.0 -C 1.0 -E 0.001 -P 0.1 -seed 1");
			// SVM.setOptions(options);
			// SVM.buildClassifier(trainData);
			//
			// ObjectOutputStream oos=new ObjectOutputStream(new
			// FileOutputStream("/home/fan/data/Family/file/0.5-train.model"));
			// oos.writeObject(SVM);
			// oos.flush();
			// oos.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return familyName;

	}

	public static FregraphVector constructVector() {
		FregraphVector vector = new FregraphVector();
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(spaceFile));
			vector = (FregraphVector) in.readObject();
			in.close();
			System.out.println(vector.fregraphList.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("Finish constructing vector");
		return vector;
	}

	public static String generateVector(String apkFilePath, FregraphVector vector) {
		APKSiggraphSet siggraphSet = new APKSiggraphSet(apkFilePath, clusterType);
		// String
		// weightScoreFilePath="/home/fan/data/Family/train-final/geinimi/FamilyInfo/MethodWeight.txt";
		siggraphSet.storeAllSubgraphs();
		siggraphSet.storeAllSignificantSubgraphs();
		siggraphSet.generateVector(vector);
		String vectorString = siggraphSet.getFeatureDoubleValueWithoutLabel();
		// System.out.println(vectorString);
		return vectorString;
	}

	public static String getHeader(FregraphVector vector) {
		String arff = "";
		arff = "@relation FamilyClassificationTrainingDataset\n";
		arff += vector.getFeatureNameList();
		arff += "@attribute label {";
		String labelList = "";
		File dataFile = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
		File falDir[] = dataFile.listFiles();
		for (int i = 0; i < falDir.length; i++) {
			String name = falDir[i].getName();
			labelList += name + ",";
			familyNameList.add(name);
		}
		labelList = labelList.substring(0, labelList.length() - 1);
		arff += labelList + "}\n\n";
		arff += "@data\n";
		return arff;
	}

	public static void writeFeatrueSpaceFile(String line, String writeFileString) {
		try {
			File file = new File(writeFileString);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(line);
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
