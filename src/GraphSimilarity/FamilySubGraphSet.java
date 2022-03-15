package GraphSimilarity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import Util.PrintTime;
import Util.Tool.QuickSort;
import sun.tools.jar.resources.jar;

/*
 *      The input is the root directory of a family, and the associated community partitioning algorithm
 *      Choose a sub-chart collection with a decentralized threshold by some selection conditions: reduucegraphlist,
 *      Then calculate the similarity between the two or two of the graphlist, and construct a similar matrix
 *      Due to the large number of sub-graph elements, the similarity calculation cost is relatively large, so when
 *      1. When the score ratio of the two subgraphs is less than the minimum similarity threshold, the similarity is 0
 *      
 */

public class FamilySubGraphSet {
	private ArrayList<SubGraphSet> subGraphSetList = new ArrayList<>();
	private String familyDirString = "";
	private String comType = "";
	private double[][] simMatrix;
	private int subgraphSize = 0;
	private ArrayList<CommunitySubGraph> graphList = new ArrayList<>();
	private double senScore[];

	public FamilySubGraphSet(String familyDirString, String comType) {
		// TODO Auto-generated constructor stub
		this.familyDirString = familyDirString;
		this.comType = comType;

		String timeSpend;
		long starttime = System.currentTimeMillis();

		iniSubGraphSetList();
		iniGraphList();
		long inigraphListTime = System.currentTimeMillis();
		// System.out.println("Finish getting graphList: " + (inigraphListTime -
		// starttime) + "ms");
		long duration = inigraphListTime - starttime;
		timeSpend = PrintTime.PrintMilis(duration);
		System.out.println("Finish getting graphList. Time spend: " + timeSpend);

		reduceGraphList();
		long reduceGraphListTime = System.currentTimeMillis();
		duration = reduceGraphListTime - inigraphListTime;
		timeSpend = PrintTime.PrintMilis(duration);
		// System.out.println("Finish reducing graphList: " + (reduceGraphListTime -
		// inigraphListTime) + "ms");
		System.out.println("Finish reducing graphList. Time spend: " + timeSpend);
		// iniSimMatrix();
		// long iniSimMatrixTime=System.currentTimeMillis();
		// System.out.println("Finish constructing similarity matrix:
		// "+(iniSimMatrixTime-reduceGraphListTime)+"ms");
	}

	/*
	 * Initialize the SubgraphSetList list
	 * Each member maintains a subgraphSet, so a family contains a List of
	 * subgraphSets
	 */
	public void iniSubGraphSetList() {
		String apktoolDir = this.familyDirString + "apktool/";
		File apktool = new File(apktoolDir);
		File apks[] = apktool.listFiles();
		System.out.println("[FamilySubGraphSet] Init Subgraph Set List for family " + this.familyDirString);
		for (int i = 0; i < apks.length; i++) {
			/*
			 * Store the subgraphs that meet the conditions in each sample in the
			 * subgraphSet Note: The conditions here refer to 0.058--0.173--76.gexf
			 * The former parameter is greater than minAvgGraphScore, and the latter
			 * parameter is greater than minTotalGraphScore. By default, the thresholds of
			 * both parameters are 0.0D, that is, the subgraphs that do not contain
			 * sensitive function nodes are deleted.
			 */
			String comDir = apks[i].getAbsolutePath() + "/SICG/Community/Community_" + comType + "/";
			// System.out.println("Checking dir " + comDir);
			SubGraphSet subGraphSet = new SubGraphSet(comDir);
			/*
			 * Set the name of the apk where the subgraph collection is located
			 */
			String apkName = apks[i].getName();
			subGraphSet.setApkName(apkName);
			subGraphSetList.add(subGraphSet);
			subgraphSize += subGraphSet.getSize();
			// System.out.println("APK :" + i + " --- " + subgraphSize);
		}
	}

	/*
	 * initialize graphList
	 */
	public void iniGraphList() {
		for (int i = 0; i < subGraphSetList.size(); i++) {
			for (int j = 0; j < subGraphSetList.get(i).getGraphList().size(); j++) {
				this.graphList.add(subGraphSetList.get(i).getGraphList().get(j));
			}
		}
		subGraphSetList = new ArrayList<>(); // Free up space in subGraphSetList
	}

	/*
	 * Delete subgraphs that do not meet the conditions
	 */
	public void reduceGraphList() {
		long startReduceTime = System.currentTimeMillis();

		double[] score = new double[graphList.size()];
		for (int i = 0; i < graphList.size(); i++) {
			score[i] = graphList.get(i).getSensitiveScore();
		}
		QuickSort sort = new QuickSort();
		sort.quick(score);
		System.out.println("Source: " + graphList.size());
		double upQuartValue = 0.0D;
		int index = 0;
		index = (int) (this.subgraphSize * (ConstantValue.getVar().minTotalGraphScoreRatio));
		upQuartValue = score[index];
		ConstantValue.getVar().minTotalGraphScore = upQuartValue;

		// Delete subgraphs whose sensitivity coefficient is less than the threshold

		ArrayList<CommunitySubGraph> newGraphList = new ArrayList<>();
		for (int i = 0; i < graphList.size(); i++) {
			if (graphList.get(i).getSensitiveScore() >= ConstantValue.minTotalGraphScore) {
				newGraphList.add(graphList.get(i));
			}
		}
		// ConstantValue.getVar().minTotalGraphScore=0.0D;
		this.graphList = new ArrayList<>();
		graphList = newGraphList;
		this.subgraphSize = graphList.size();
		newGraphList = new ArrayList<>();
		System.out.println("Reduced :" + graphList.size());
		System.out.println("UpQuartValue: " + upQuartValue);
		long endReduceTime = System.currentTimeMillis();
		long usingReduceTime = endReduceTime - startReduceTime;
		// System.out.println("Reduce Using Time: "+ usingReduceTime+"ms");
	}

	/*
	 * Build a similarity matrix
	 * 
	 */
	public void iniSimMatrix() {
		System.out.println("Subgraph Size:" + this.subgraphSize);
		simMatrix = new double[this.subgraphSize][this.subgraphSize];
		String familyWeightFilePath = this.familyDirString + "FamilyInfo/MethodWeight.txt";
		FamilyWeightScore weightScore = new FamilyWeightScore(familyWeightFilePath);
		double sim = 0.0D;
		for (int i = 0; i < this.subgraphSize; i++) {
			for (int j = i; j < this.subgraphSize; j++) {
				if (i == j) {
					sim = -1.0D;
				} else {
					CommunitySubGraph srcSubGraph = new CommunitySubGraph();
					CommunitySubGraph dstSubGraph = new CommunitySubGraph();
					srcSubGraph = graphList.get(i);
					dstSubGraph = graphList.get(j);
					if (calculateCondition(srcSubGraph, dstSubGraph)) {
						try {
							SubGraphSimilarity similarity = new SubGraphSimilarity(srcSubGraph.getGraph(),
									dstSubGraph.getGraph(), weightScore);
							simMatrix[i][j] = similarity.getSimScore();
							DecimalFormat df = new DecimalFormat("######0.000");
							String value = df.format(simMatrix[i][j]);
							simMatrix[i][j] = Double.valueOf(value);
							simMatrix[j][i] = Double.valueOf(value);
						} catch (Exception e) {
							// TODO: handle exception
							// Since a certain subgraph will be disconnected,
							// an exception will occur when calculating the distance.
							// The temporary processing method is to set its similarity to 0
							System.out.println("[FamilySubGraphSet] Exception: Disconnected subgraph");
							System.out.println(srcSubGraph.getFilePath() + " ---- " + dstSubGraph.getFilePath());
							simMatrix[i][j] = 0.0D;
						}

					} else {
						simMatrix[i][j] = 0.0D;
					}
				}
			}
		}
	}

	public void showSimMatrix() {
		for (int i = 0; i < this.subgraphSize; i++) {
			System.out.println();
			for (int j = 0; j < subgraphSize; j++) {
				System.out.print(simMatrix[i][j] + ", ");
			}
		}
	}

	// public void processMatrix(){
	// ArrayList<CommunitySubGraph> noSimGraphList=new ArrayList<>();
	// for(int i=0;i<subgraphSize;i++){
	// boolean allLess=true;
	// for(int j=0;j<subgraphSize;j++){
	// if(simMatrix[i][j] > ConstantValue.getVar().minScoreSim){
	// allLess=false;
	// }
	// }
	// if(allLess==true){
	// // 删除第 i行 和第i列
	// }
	// }
	// }
	public boolean calculateCondition(CommunitySubGraph srcSubGraph, CommunitySubGraph dstSubGraph) {
		double srcSenScore = srcSubGraph.getSensitiveScore();
		double dstSenScore = dstSubGraph.getSensitiveScore();
		double ratio = getMinDouble(srcSenScore, dstSenScore) / getMaxDouble(srcSenScore, dstSenScore);
		if (ratio > ConstantValue.getVar().minScoreSim) {
			return true;
		}
		return false;
	}

	public double getMaxDouble(double a, double b) {
		if (a >= b) {
			return a;
		} else {
			return b;
		}
	}

	public double getMinDouble(double a, double b) {
		if (a < b) {
			return a;
		} else {
			return b;
		}
	}

	public ArrayList<SubGraphSet> getSubGraphSetList() {
		return subGraphSetList;
	}

	public void setSubGraphSetList(ArrayList<SubGraphSet> subGraphSetList) {
		this.subGraphSetList = subGraphSetList;
	}

	public String getFamilyDirString() {
		return familyDirString;
	}

	public void setFamilyDirString(String familyDirString) {
		this.familyDirString = familyDirString;
	}

	public String getComType() {
		return comType;
	}

	public void setComType(String comType) {
		this.comType = comType;
	}

	public double[][] getSimMatrix() {
		return simMatrix;
	}

	public void setSimMatrix(double[][] simMatrix) {
		this.simMatrix = simMatrix;
	}

	public int getSubgraphSize() {
		return subgraphSize;
	}

	public void setSubgraphSize(int subgraphSize) {
		this.subgraphSize = subgraphSize;
	}

	public ArrayList<CommunitySubGraph> getGraphList() {
		return graphList;
	}

	public void setGraphList(ArrayList<CommunitySubGraph> graphList) {
		this.graphList = graphList;
	}

}
