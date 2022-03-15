package GraphSimilarity.HierarchicalClustering;

import java.util.ArrayList;

import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;

/*
 *        Hierarchical clustering algorithm, the input is a similarity matrix and a list of elements,
 *        The final similarity matrix and cluster list are obtained by continuously updating the iterative matrix
 * 
 */
public class ClusterMatrix {
	private int srcSize = 0;
	private ArrayList<Cluster> clusterList = new ArrayList<>();
	private double[][] simMatrix;
	private int row = -1;
	private int col = -1;

	public ClusterMatrix() {
	}

	public ClusterMatrix(double simMatrix[][], ArrayList<CommunitySubGraph> graphList, int familyNum) {
		int size = graphList.size();
		srcSize = graphList.size();
		this.simMatrix = new double[size][size];
		this.simMatrix = simMatrix;
		for (int i = 0; i < size; i++) {
			Cluster cluster = new Cluster();
			cluster.addOneSubgraph(graphList.get(i));
			cluster.addOneIndex(i);
			clusterList.add(cluster);
		}
		exchangeMatrix();
		surpport(familyNum);
	}

	public void exchangeMatrix() {
		while (findMergePos()) {
			clusterList.get(row).mergeCluster(clusterList.get(col));
			clusterList.remove(col);
			simMatrix = deleteOneData(simMatrix, row, col);
		}
		for (int i = 0; i < clusterList.size(); i++) {
			clusterList.get(i).setIndex(String.valueOf(i));
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

		ArrayList<Cluster> newClusterList = new ArrayList<>();
		for (int i = 0; i < clusterList.size(); i++) {
			clusterList.get(i).iniApkNameSet();
			if (clusterList.get(i).getApkSubgraphNameMap().size() >= min) {
				newClusterList.add(clusterList.get(i));
			}
		}
		this.clusterList = newClusterList;
	}

	public double[][] deleteOneData(double[][] data, int row, int col) {
		int t = data[0].length;
		for (int i = 0; i < t; i++) {
			data[row][i] = (data[row][i] + data[col][i]) / 2;
			data[i][row] = data[row][i];
		}
		int k = data[0].length - 1;
		double[][] newMatrix = new double[k][k];
		for (int i = 0; i < k; i++) {
			for (int j = 0; j < k; j++) {
				if (i == j) {
					newMatrix[i][j] = 0.0D;
				} else {
					if (i < col && j < col) {
						newMatrix[i][j] = data[i][j];
					}
					if (i >= col && j < col) {
						newMatrix[i][j] = data[i + 1][j];
					}
					if (i < col && j >= col) {
						newMatrix[i][j] = data[i][j + 1];
					}
					if (i >= col && j >= col) {
						newMatrix[i][j] = data[i + 1][j + 1];
					}
				}
			}
		}
		return newMatrix;
	}

	public boolean findMergePos() {
		boolean find = false;
		double tmp = ConstantValue.getVar().minScoreSim;
		for (int i = 0; i < clusterList.size(); i++) {
			for (int j = 0; j < clusterList.size(); j++) {
				if (simMatrix[i][j] == 1.0D) {
					tmp = simMatrix[i][j];
					row = i;
					col = j;
					find = true;
					return true;
				} else if (simMatrix[i][j] > tmp) {
					tmp = simMatrix[i][j];
					row = i;
					col = j;
					find = true;
				}
			}
		}
		return find;
	}

	public ArrayList<Cluster> getClusterList() {
		return clusterList;
	}

	public void setClusterList(ArrayList<Cluster> clusterList) {
		this.clusterList = clusterList;
	}

}
