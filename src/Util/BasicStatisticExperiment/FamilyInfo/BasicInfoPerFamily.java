package Util.BasicStatisticExperiment.FamilyInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.media.sound.Toolkit;

import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;

/*
 *    获取每个家族的基本信息，例如平均子图个数等等
 */
public class BasicInfoPerFamily {

	public static ArrayList<Integer> norGraphNumList = new ArrayList<>();
	public static ArrayList<Integer> senGraphNumList = new ArrayList<>();
	public static ArrayList<Integer> senNodeNumList = new ArrayList<>();
	public static ArrayList<Integer> nodeNumList = new ArrayList<>();
	public static ArrayList<Integer> edgeNumList = new ArrayList<>();
	public static ArrayList<String> falNameList = new ArrayList<>();
	public static ArrayList<Integer> NodeNumberOfSGList = new ArrayList<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
		String outString = "";
		File family[] = file.listFiles();
		for (int i = 0; i < family.length; i++) {
			// if(family[i].getName().equals("youmi")){
			String familyPath = family[i].getAbsolutePath();
			String familyName = family[i].getName();
			// outString+="Family Name:"+familyName+"\n";
			// outString += oneFalSubgraph(familyPath, familyName);
			// outString +=communityScore(familyPath);
			// outString +=clusterInfo(familyPath);
			// oneFalSubgraph(familyPath, familyName);
			// NodeAndEdgeNum(familyPath);
			System.out.println("Finish: " + familyName);
			// }
		}
		// String
		// writeFileString="/home/fan/data/Family/result-final/BasicInfo/basicInfo.txt";
		// writeString(outString, writeFileString);
		String infoString = "/home/fan/data/Family/result-final/BasicInfo/statistic_train.txt";
		writeSenGraphNum(infoString);
		String numNodeofSGFile = "/home/fan/data/Family/result-final/BasicInfo/statistic_train_PerNodeInSG.txt";
		writeNumberOfNodeOfSG(numNodeofSGFile);
	}

	public static void writeNumberOfNodeOfSG(String writeFilePath) {
		try {
			File file = new File(writeFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			String line = "";
			for (int i = 0; i < NodeNumberOfSGList.size(); i++) {
				line = NodeNumberOfSGList.get(i) + "\n";
				bWriter.write(line);
			}
			bWriter.close();
			fWriter.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*
	 * 写入正常，敏感子图个数以及节点边以及敏感节点个数到相应文件
	 */
	public static void writeSenGraphNum(String fileString) {
		try {
			File file = new File(fileString);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			String lineString = "NorGraphNum,SenGraphNum,Node,Edge,SenNode\n";
			bWriter.write(lineString);
			int k1 = norGraphNumList.size();
			int k2 = senGraphNumList.size();
			int k3 = senNodeNumList.size();
			int k4 = nodeNumList.size();
			int k5 = edgeNumList.size();
			if ((k1 == k2) && (k2 == k3) && (k3 == k4) && (k4 == k5)) {
				for (int i = 0; i < senGraphNumList.size(); i++) {
					lineString = norGraphNumList.get(i) + "," + senGraphNumList.get(i) + ","
							+ nodeNumList.get(i) + "," + edgeNumList.get(i) + "," + senNodeNumList.get(i) + ","
							+ falNameList.get(i) + "\n";
					bWriter.write(lineString);
				}
				bWriter.close();
				fWriter.close();
			} else {
				System.err.println("The numbers are not in consistence!!!");
				bWriter.close();
				fWriter.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*
	 * 统计所有样本的节点个数，边个数以及敏感节点个数
	 */
	public static void NodeAndEdgeNum(String filePath) {
		String apkFilePath = filePath + "/apktool/";
		File apkFile = new File(apkFilePath);
		File apks[] = apkFile.listFiles();
		for (int i = 0; i < apks.length; i++) {
			File infoFile = new File(apks[i].getAbsolutePath() + "/SICG/Information.txt");

			try {
				FileReader fReader = new FileReader(infoFile);
				BufferedReader bReader = new BufferedReader(fReader);
				String lineString = "";
				int srcSenNum = 0;
				int dstSenNum = 0;
				while ((lineString = bReader.readLine()) != null) {
					if (lineString.startsWith("Number of nodes:")) {
						String arg[] = lineString.split(":");
						int nodeNum = Integer.valueOf(arg[1]);
						nodeNumList.add(nodeNum);
					}
					if (lineString.startsWith("Number of sides:")) {
						String args[] = lineString.split(":");
						int edgeNum = Integer.valueOf(args[1]);
						edgeNumList.add(edgeNum);
					}
					if (lineString.startsWith("sensitive source node:")) {
						int k = lineString.indexOf(":");
						srcSenNum = Integer.valueOf(lineString.substring(k + 1));
					}
					if (lineString.startsWith("sensitive destination node:")) {
						int k = lineString.indexOf(":");
						dstSenNum = Integer.valueOf(lineString.substring(k + 1));
					}

				}
				bReader.close();
				fReader.close();
				int senNodeNum = srcSenNum + dstSenNum;
				senNodeNumList.add(senNodeNum);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/*
	 * 获取每个家族的平均子图个数以及平均有效子图个数
	 */
	public static String oneFalSubgraph(String filePath, String falName) {
		int totalNum = 0; // 子图数量
		int effNum = 0; // 敏感系数大于0的子图数量
		int apkNum = 0;
		String apkFilePath = filePath + "/apktool/";
		File apkFile = new File(apkFilePath);
		File apks[] = apkFile.listFiles();
		apkNum = apks.length;
		for (int i = 0; i < apks.length; i++) {
			falNameList.add(falName);
			String apkPath = apks[i].getAbsolutePath();
			String subgraphFilePath = apkPath + "/SICG/Community/Community_Im/";
			File subgraphFile = new File(subgraphFilePath);
			File subFile[] = subgraphFile.listFiles();
			totalNum += subFile.length;
			norGraphNumList.add(subFile.length);
			int senNum = 0;
			for (int j = 0; j < subFile.length; j++) {
				CommunitySubGraph subGraph = new CommunitySubGraph(subFile[j].getAbsolutePath());
				double score = subGraph.getSensitiveScore();
				if (score > 0.0D) {
					effNum++;
					senNum++;
					int nodeNum = subGraph.getGraph().getNodeSet().size();
					NodeNumberOfSGList.add(nodeNum);
				}
			}
			senGraphNumList.add(senNum);
		}
		String resultString = "";
		double perSubgraphSize = Double.valueOf(totalNum) / Double.valueOf(apkNum);
		double perEffectSize = Double.valueOf(effNum) / Double.valueOf(apkNum);
		resultString += "	Total Subgraph Size: " + totalNum + "\n";
		resultString += "	Effective Subgraph Size: " + effNum + "\n";
		resultString += "	Normal Subgraph Per APK: " + perSubgraphSize + "\n";
		resultString += "	Effect Subgraph Per APK: " + perEffectSize + "\n";
		return resultString;
	}

	/*
	 * 获取每个家族的平均社团结构得分
	 */
	public static String communityScore(String filePath) {
		int apkNum = 0;
		double totalScore = 0.0D;
		String apkFilePath = filePath + "/apktool/";
		File apkFile = new File(apkFilePath);
		File apks[] = apkFile.listFiles();
		apkNum = apks.length;
		for (int i = 0; i < apks.length; i++) {
			String communityFilePath = apks[i].getAbsolutePath() + "/SICG/Community/Community_Result_im.csv";
			try {
				File file = new File(communityFilePath);
				FileReader fReader = new FileReader(file);
				BufferedReader bReader = new BufferedReader(fReader);
				String lineString = bReader.readLine();
				String regex = "(.*?)Score:(.*?),Com(.*)";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(lineString);
				double score = 0.0D;
				if (matcher.find()) {
					String scoreString = matcher.group(2);
					score = Double.valueOf(scoreString);
				}
				totalScore += score;
				bReader.close();
				fReader.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		double avgScore = totalScore / Double.valueOf(apkNum);
		String resultString = "	Avg Community Score: " + avgScore + "\n";
		return resultString;
	}

	public static String clusterInfo(String filePath) {
		String outString = "";
		try {
			String topFilePath = filePath + "/FamilyInfo/";
			File topFile = new File(topFilePath);
			File supFile[] = topFile.listFiles();
			for (int i = 0; i < supFile.length; i++) {
				if (supFile[i].isDirectory()) {
					/*
					 * 获取每个支持度下的显著性子图的个数
					 */
					String clusterString = supFile[i].getAbsolutePath() + "/Cluster/";
					File clusterFile = new File(clusterString);
					File clusters[] = clusterFile.listFiles();
					int clusterNum = clusters.length;
					outString += "		" + supFile[i].getName() + ": " + clusterNum;
					/*
					 * 获取每个支持度下构建显著性子图的时间开销
					 */
					File logFile = new File(supFile[i].getAbsolutePath() + "/log.txt");
					FileReader fReader = new FileReader(logFile);
					BufferedReader bReader = new BufferedReader(fReader);
					String lineString = "";
					String timeString = "";
					double time = 0.0D;
					while ((lineString = bReader.readLine()) != null) {
						if (lineString.startsWith("Using time: ")) {
							int k = lineString.indexOf(":");
							timeString = lineString.substring(k + 2, lineString.length() - 2);
							time = Double.valueOf(timeString) / Double.valueOf(1000);
							break;
						}
					}
					outString += "# time: " + time + "s\n";

					bReader.close();
					fReader.close();
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return outString;
	}

	public static void writeString(String outString, String writeFilePath) {
		try {
			File file = new File(writeFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(outString);
			bWriter.close();
			fWriter.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
