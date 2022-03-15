package Exp.ClassifyByVector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import APKData.SmaliGraph.SICG;
import CandicateFamily.FamilyWeightScore;
import CommunityStructure.CommunityDetection.CommunityFinding;
import CommunityStructure.GraphData.GenerateCommunitySubgraph;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;
import Util.Tool.CopyDir;
import Util.Tool.DirDelete;

public class APKSiggraphSet {
	private String filePath;
	private String actualFalName;
	private String predictFalName;
	private String fileName;
	private String fileBaseame;
	private String outFilePath;
	private String clusterType = "";
	private String weightScoreFilePathString;
	private Boolean successAPKSiggraphSet = false;
	private Boolean successApktool = false;
	// private Boolean successCommunityDivide = false;
	private Boolean successFregraphGen = false;
	private String tmpfsString = "";
	// Elements are all submaps of this APK
	private ArrayList<CommunitySubGraph> subgraphList = new ArrayList<>();
	/*
	 * The element is the saliency submap corresponding to all families of the APK
	 */
	private ArrayList<CommunitySubGraph> simSigGraphList = new ArrayList<>();
	private ArrayList<Integer> featureInt = new ArrayList<>();
	private ArrayList<Double> featureDouble = new ArrayList<>();

	public APKSiggraphSet() {
	}

	public APKSiggraphSet(String apkFilePath, String clusterType, String apktoolOutputPathString) {
		this.successAPKSiggraphSet = false;
		this.filePath = apkFilePath;
		File apkFile = new File(apkFilePath);
		String apkName = apkFile.getName();
		this.fileBaseame = apkFile.getName().split("\\.")[0];
		this.fileName = apkName;
		this.tmpfsString = apktoolOutputPathString;

		this.clusterType = clusterType;

		int k = apkFilePath.lastIndexOf("/");
		String outFileString = apkFilePath.substring(0, k + 1);
		File falDir = new File(outFileString);
		this.actualFalName = falDir.getName();
		outFileString += "apktool/" + fileName + "/";

		weightScoreFilePathString = Paths.get(ConstantValue.FAMILIESDIRPATH_STRING, actualFalName,
				"FamilyInfo", "MethodWeight.txt").toString();
		Path apktoolFilePath = Paths.get(outFileString);
		this.outFilePath = outFileString;
		// System.out.println(String.format("[APKSiggraphSet] outFilePath: %s",
		// outFilePath));

		if (Files.exists(apktoolFilePath)) {
			System.out
					.println(String.format("[APKSiggraphSet] Path %s found, deleting it", apktoolFilePath.toString()));
			// File apktoolDir = new File(apktoolFilePath);
			DirDelete.deleteDirFiles(apktoolFilePath.toFile());
		}

		System.out.println("[APKSiggraphSet] Generate reduced graph");
		disassemble(apkFilePath);
		System.out.println("[APKSiggraphSet] Performing community divide");
		communityDivide();
		System.out.println("[APKSiggraphSet] Getting subgraphs and weights");
		createCommunityFiles(weightScoreFilePathString);

		if (successApktool && successFregraphGen) {
			this.successAPKSiggraphSet = true;
		} else {
			if (!successApktool) {
				System.out.println("[APKSiggraphSet] apktool failed to run");
			}
			if (!successFregraphGen) {
				System.out.println("[APKSiggraphSet] Fregraphs were not generated");
			}
		}

		// System.out.println(outFileString);
		// }
	}

	public APKSiggraphSet(String apkFilePath, String clusterType) {
		// System.out.println("[APKSiggraphSet] Working with apk: " + apkFilePath);
		this.successAPKSiggraphSet = false;
		this.filePath = apkFilePath;
		File apkFile = new File(apkFilePath);
		String apkName = apkFile.getName();
		// System.out.println("[APKSiggraphSet] apkName: " + apkName);
		// System.out.println("[APKSiggraphSet] split: " + apkName);
		// String[] apkFileSplit = apkName.split("\\.");
		// System.out.println("[APKSiggraphSet] split: " + apkFileSplit[0]);
		this.fileBaseame = apkFile.getName().split("\\.")[0];
		this.fileName = apkName;

		// weightScoreFilePath = family[i].getAbsolutePath() +
		// "/FamilyInfo/MethodWeight.txt";
		this.clusterType = clusterType;

		// if (apkFilePath.contains("train")) {
		// int k = apkFilePath.lastIndexOf("/");
		// String outFileString = apkFilePath.substring(0, k + 1);
		// File falDir = new File(outFileString);
		// this.actualFalName = falDir.getName();
		// outFileString += "apktool/" + fileName + "/";
		// this.outFilePath = outFileString;
		// } else if (apkFilePath.contains("testData")) {
		// int k = apkFilePath.lastIndexOf("/");
		// String outFileString = apkFilePath.substring(0, k + 1);
		// File falDir = new File(outFileString);
		// this.actualFalName = falDir.getName();
		// outFileString += "apktool/out-" + fileName + "/";
		// this.outFilePath = outFileString;
		// } else {
		int k = apkFilePath.lastIndexOf("/");
		String outFileString = apkFilePath.substring(0, k + 1);
		File falDir = new File(outFileString);
		this.actualFalName = falDir.getName();
		outFileString += "apktool/" + fileName + "/";

		weightScoreFilePathString = Paths.get(ConstantValue.FAMILIESDIRPATH_STRING, actualFalName,
				"FamilyInfo", "MethodWeight.txt").toString();
		// weightScoreFilePathString = weightScoreString;
		// System.out.println("[APKSiggraphSet] Weight score in " +
		// weightScoreFilePathString);
		// String apktoolFilePathString = falDirPath + "/apktool/" + apkName;
		Path apktoolFilePath = Paths.get(outFileString);

		if (!Files.exists(apktoolFilePath)) {
			System.out.println("[APKSiggraphSet] Generate reduced graph");
			disassemble(apkFilePath);
			System.out.println("[APKSiggraphSet] Performing community divide");
			communityDivide();
			System.out.println("[APKSiggraphSet] Getting subgraphs and weights");
			createCommunityFiles(weightScoreFilePathString);
		} else {
			System.out.println(String.format("[APKSiggraphSet] Apktool path %s already exists, skipping", apkFilePath));
		}

		if (successApktool && successFregraphGen) {
			this.successAPKSiggraphSet = true;
		} else {
			if (!successApktool) {
				System.out.println("[APKSiggraphSet] apktool failed to run");
			}
			if (!successFregraphGen) {
				System.out.println("[APKSiggraphSet] Fregraphs were not generated");
			}
		}

		this.outFilePath = outFileString;
		// System.out.println(outFileString);
		// }
	}

	// public APKSiggraphSet(String apkFilePath, String clusterType, String apkName)
	// {
	// this.filePath = apkFilePath;
	// // File apkFile = new File(apkFilePath);
	// // String apkName = apkFile.getName();
	// this.fileName = apkName;
	// this.clusterType = clusterType;
	// int numSubdirs = apkFilePath.split(",").length;

	// this.actualFalName = apkFilePath.split(",")[numSubdirs - 2];
	// Path apktool_path = Paths.get(apkFilePath, "apktool", apkName);
	// this.outFilePath = apktool_path.toString();
	// }

	/*
	 * Decompile apk files and generate corresponding analysis results
	 */
	public void disassemble(String apkFilePath) {
		// System.out.println("[APKSiggraphSet] Proceeding to generate reduced graph for
		// " + apkFilePath);
		int k = apkFilePath.lastIndexOf("/");
		String outFileString = apkFilePath.substring(0, k + 1);
		String apkFileString = Paths.get(apkFilePath).getFileName().toString();
		File outFileDir = new File(outFileString);
		this.actualFalName = outFileDir.getName();
		if (!tmpfsString.equals("")) {
			outFileString = Paths.get(tmpfsString, "apktool", apkFileString).toString();
		} else {
			outFileString += "apktool/" + fileName + "/";
		}
		SICG sicg = new SICG(apkFilePath, outFileString);

		File outApktoolDir = new File(outFileString);
		File outDir = new File(outFilePath);

		try {
			// System.out.println(String.format("[DirAnalysis] Copy %s -> %s",
			// outApktoolDir.toString(), outDir.toPath()));
			CopyDir.copyDirectoryCompatibityMode(outApktoolDir, outDir);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(String.format("[ERROR][DirAnalysis] Cannot copy %s to %s. Exiting",
					outApktoolDir.toString(), outDir.toPath()));
			// apkDiStrings.add(inApkPath);
		}

		DirDelete.deleteDirFiles(outApktoolDir);

		successApktool = sicg.getSuccesApktool();
		// this.outFilePath = outFileString;
	}

	/*
	 * For community division, you need to input the analysis result and the family
	 * score list file as parameters
	 */
	public void communityDivide() {
		/*
		 * The original graph is divided into several subgraphs using the community
		 * division algorithm
		 */
		CommunityFinding communityFinding = new CommunityFinding(outFilePath);
		// System.out.println("\n Finish Community Detection");
	}

	/*
	 * Generate a set of generated subgraphs for each algorithm, requiring a score
	 * list file as a parameter
	 */
	public void createCommunityFiles(String weightScoreFilePath) {
		GenerateCommunitySubgraph generateCommunitySubgraph = new GenerateCommunitySubgraph(outFilePath,
				weightScoreFilePath);

		successFregraphGen = generateCommunitySubgraph.getSuccessCommunitySubgraph();
		// System.out.println("Finsh CommunityFile Creation");
	}

	public void reiniGraphList() {
		this.subgraphList = new ArrayList<>();
		this.simSigGraphList = new ArrayList<>();
	}

	/*
	 * store all generated subgraphs for this sample
	 */
	public void storeAllSubgraphs() {
		String type = "";
		int k = clusterType.indexOf("-");
		type = clusterType.substring(0, k);
		String graphFilePath = this.outFilePath + "SICG/Community/Community_" + type + "/";
		// System.out.println(String.format("[APKSiggraphSet] Checking for dir %s",
		// graphFilePath));

		File graphFile = new File(graphFilePath);
		if (!graphFile.exists()) {
			System.out.println(String.format("[ERROR][APKSiggraphSet] Dir %s doesn't exists. Exiting", graphFilePath));
			System.exit(-1);
		}
		File subgraphFile[] = graphFile.listFiles();
		for (int i = 0; i < subgraphFile.length; i++) {
			CommunitySubGraph subGraph = new CommunitySubGraph(subgraphFile[i].getAbsolutePath());
			// double score = subGraph.getSensitiveScore();
			// if(score>=ConstantValue.getVar().minTotalGraphScore){
			subgraphList.add(subGraph);
			// }
		}
	}

	/*
	 * Store the significance submaps of this sample for all families
	 */
	public void storeAllSignificantSubgraphs() {
		File dataFile = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
		File family[] = dataFile.listFiles();
		for (int i = 0; i < family.length; i++) {
			addFalSignificantSubgraphs(family[i].getAbsolutePath());
		}
	}

	public void addFalSignificantSubgraphs(String familyDirPath) {
		String weightScoreFilePath = familyDirPath + "/FamilyInfo/MethodWeight.txt";
		FamilyWeightScore weightScore = new FamilyWeightScore(weightScoreFilePath);
		File clusterFile = new File(familyDirPath + "/FamilyInfo/" + clusterType + "/Cluster/");
		File gexfFile[] = clusterFile.listFiles();
		for (int i = 0; i < gexfFile.length; i++) {
			CommunitySubGraph srcGraph = new CommunitySubGraph(gexfFile[i].getAbsolutePath());
			for (int j = 0; j < subgraphList.size(); j++) {
				CommunitySubGraph dstGraph = subgraphList.get(j);
				SubGraphSimilarity similarity = new SubGraphSimilarity(srcGraph.getGraph(), dstGraph.getGraph(),
						weightScore);
				if (similarity.getSimScore() > ConstantValue.getVar().minScoreSim) {
					simSigGraphList.add(srcGraph);
					break;
				}
			}
		}
	}

	public String showSigGraphList() {
		String resultString = "";
		for (int i = 0; i < simSigGraphList.size(); i++) {
			resultString += simSigGraphList.get(i).getFilePath() + "\n";
		}
		return resultString;
	}

	public void generateVector(FregraphVector vector) {
		ArrayList<Fregraph> fregraphList = new ArrayList<>();
		fregraphList = vector.fregraphList;
		for (int i = 0; i < fregraphList.size(); i++) {
			boolean in = false;
			for (int j = 0; j < simSigGraphList.size(); j++) {
				// Search through all the similiar subgraphs, and check that at least one of
				// them is part of the feature's list of subgraphs
				if (fregraphList.get(i).inFeature(simSigGraphList.get(j).getFilePath())) {
					in = true;
					break;
				}
			}
			if (in == true) {
				featureInt.add(1);
				double tmp = fregraphList.get(i).weight;
				// System.out.println("[APKSiggraphSet] - Weight: " + tmp);

				if (ConstantValue.isVerbose()) {
					System.out.println(String.format("[APKSiggraphSet] Graph %s: %f",
							fregraphList.get(i).getFeatureLabel(), tmp));
				}
				featureDouble.add(tmp);
				// If the sample is a training set sample
				// if(this.filePath.contains("train")){
				// // System.out.println(i+": "+featureList.get(i).FeatureString());
				// String falName=this.actualFalName; //存储本样本的实际家族
				//// double tmp=featureList.get(i).getSigraphScoreWithFalName(falName); //
				// 返回该特征相对于该
				//// //已知样本子图的有效分值
				// /*
				// * 返回该特征的熵值
				// */
				// double tmp=featureList.get(i).getWeight();
				// featureDouble.add(tmp);
				// }
				// else if(this.filePath.contains("test")){
				// // System.out.println(i+": "+featureList.get(i).FeatureString());
				// // double tmp=featureList.get(i).getSiggraphScoreWithoutFalName();
				// double tmp=featureList.get(i).getWeight();
				// featureDouble.add(tmp);
				// }
				// else{
				// double tmp=featureList.get(i).getWeight();
				// featureDouble.add(tmp);
				// }
			} else {
				if (ConstantValue.isVerbose()) {
					System.out.println(String.format("[APKSiggraphSet] Graph %s: not found (0.0)",
							fregraphList.get(i).getFeatureLabel()));
				}
				featureInt.add(0);
				featureDouble.add(0.0D);
			}
		}
	}

	public String getFeatureIntVector() {
		String resultString = "";
		for (int i = 0; i < featureInt.size(); i++) {
			resultString += featureInt.get(i) + ",";
		}
		resultString += this.actualFalName;
		return resultString;
	}

	public String getFeatureDoubleVector() {
		String resultString = "";
		resultString += this.fileBaseame + ",";
		for (int i = 0; i < featureInt.size(); i++) {
			resultString += featureDouble.get(i) + ",";
		}
		// This will result in an error when read by Weka: nominal value not declared in
		// header (for the family label)
		// resultString += this.actualFalName + "/" + this.fileName;
		resultString += this.actualFalName;
		// resultString +=this.actualFalName;
		return resultString;
	}

	public String getFeatureDoubleVectorWithAPKName() {
		String resultString = "";
		for (int i = 0; i < featureInt.size(); i++) {
			resultString += featureDouble.get(i) + ",";
		}
		resultString += this.actualFalName + "/" + this.fileName;
		return resultString;
	}

	public String getFeatureDoubleValueWithoutLabel() {
		String resultString = "";
		for (int i = 0; i < featureInt.size(); i++) {
			resultString += featureDouble.get(i) + ",";
		}
		resultString += "?";
		return resultString;
	}

	public String getApktoolPathString() {
		return outFilePath;
	}

	public Boolean getSuccessAPKSiggraphSet() {
		return successAPKSiggraphSet;
	}

	public String getActualFalName() {
		return actualFalName;
	}

	public String getFileBasename() {
		return fileBaseame;
	}

	public void printSimGraphs() {
		int n = 0;
		for (CommunitySubGraph csg : simSigGraphList) {
			n++;
			System.out.println(String.format("[APKSiggraphSet] Information about Simgraph %d:", n));
			// csg.pri
			csg.printCommunitySubGraphInfo();
		}
		System.out.println(String.format("[APKSiggraphSet] Total simgraphs %d:", n));
	}

	public void printFeatureVector() {
		int n = 0;
		for (Double f : featureDouble) {
			n++;
			System.out.println("[APKSiggraphSet] Feature value: " + f);
		}
		System.out.println(String.format("[APKSiggraphSet] Total features: %d", n));
	}
}
