package GraphSimilarity.KMeans;
/*
 * 				Since the Kmeans algorithm needs to specify the k value at the beginning, 
 * 				this algorithm uses the kmeans idea
 * 				Calculate the similarity between each element in the subgraph set 
 * 				and the known cluster in turn. If it is greater than the threshold, 
 * 				then add the subgraph to the
 *              In this cluster; if the new subgraph element is not similar to any cluster, 
 * 				create a new cluster and use this subgraph as the first of the new cluster
 *              element storage
 */

import java.util.ArrayList;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.HierarchicalClustering.Cluster;

public class ClusterKmeans {

	public ArrayList<Cluster> clusterList = new ArrayList<>();
	public ArrayList<CommunitySubGraph> graphList = new ArrayList<>();
	public int familyNum;
	public String familyDirString = "";
	public FamilyWeightScore weightScore;

	/*
	 * The input parameters are a list of subgraphs,
	 * the number of samples of the corresponding family,
	 * and the file location of the corresponding family
	 */
	public ClusterKmeans(ArrayList<CommunitySubGraph> graphList, int familyNum, String familyDirString) {
		this.graphList = graphList;
		this.familyDirString = familyDirString;
		this.familyNum = familyNum;

		System.out.println("[ClusterKmeans] Performing K-means clustering with similarity threshold: "
				+ ConstantValue.getVar().minScoreSim);
		weightScore = new FamilyWeightScore(this.familyDirString + "FamilyInfo/MethodWeight.txt");
		iniClusterList();
		surpport(familyNum);
	}

	public void iniClusterList() {
		for (int i = 0; i < graphList.size(); i++) {
			CommunitySubGraph subGraph = new CommunitySubGraph();
			subGraph = graphList.get(i);
			/*
			 * The current number of clusters is 0, create a new cluster for the first
			 * element and put it in
			 */
			if (clusterList.size() == 0) {
				Cluster cluster = new Cluster();
				cluster.addOneSubgraph(subGraph);
				cluster.addOneIndex(i);
				clusterList.add(cluster);
			} else {
				/*
				 * Otherwise, calculate the similarity between the current subgraph and the
				 * known cluster,
				 * and if it is greater than the threshold, add it to the corresponding cluster
				 * find is true to find the corresponding cluster
				 */
				boolean find = false;
				int index = -1;
				for (int j = 0; j < clusterList.size(); j++) {
					double sim = clusterList.get(j).getAverageSimForOneGraph(subGraph, weightScore);
					if (sim >= ConstantValue.getVar().minScoreSim) {
						find = true;
						index = j;
						break;
					}
				}
				if (find == true) {
					clusterList.get(index).addOneSubgraph(subGraph);
					clusterList.get(index).addOneIndex(i);
				} else {
					Cluster cluster = new Cluster();
					cluster.addOneSubgraph(subGraph);
					cluster.addOneIndex(i);
					clusterList.add(cluster);
				}
			}
		}
	}

	public String getClusterResult() {
		String result = "";
		for (int i = 0; i < clusterList.size(); i++) {
			result += "Cluster " + i + " :  #" + clusterList.get(i).getSignificantSubgraph().getFileName() + "#"
					+ clusterList.get(i).getApkSubgraphNameMap().size() + "\n";
			result += clusterList.get(i).getClusterInfo();
		}
		return result;
	}

	public void surpport(int familyNum) {
		int min = (int) (ConstantValue.getVar().minSupport * familyNum);
		// int min=(int) ConstantValue.getVar().minUnknownNum; //the min support value
		// in the unknown cluster.
		ArrayList<Cluster> newClusterList = new ArrayList<>();
		for (int i = 0; i < clusterList.size(); i++) {
			clusterList.get(i).iniApkNameSet();
			if (clusterList.get(i).getApkSubgraphNameMap().size() >= min) {
				newClusterList.add(clusterList.get(i));
			}
		}
		this.clusterList = newClusterList;
	}

	public ArrayList<Cluster> getClusterList() {
		return clusterList;
	}

	public void setClusterList(ArrayList<Cluster> clusterList) {
		this.clusterList = clusterList;
	}

	public ArrayList<CommunitySubGraph> getGraphList() {
		return graphList;
	}

	public void setGraphList(ArrayList<CommunitySubGraph> graphList) {
		this.graphList = graphList;
	}

	public int getFamilyNum() {
		return familyNum;
	}

	public void setFamilyNum(int familyNum) {
		this.familyNum = familyNum;
	}

	public String getFamilyDirString() {
		return familyDirString;
	}

	public void setFamilyDirString(String familyDirString) {
		this.familyDirString = familyDirString;
	}

	public FamilyWeightScore getWeightScore() {
		return weightScore;
	}

	public void setWeightScore(FamilyWeightScore weightScore) {
		this.weightScore = weightScore;
	}

}
