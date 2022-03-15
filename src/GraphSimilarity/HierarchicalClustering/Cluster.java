package GraphSimilarity.HierarchicalClustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sun.org.apache.xml.internal.security.keys.content.RetrievalMethod;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;

/*
 *      A cluster in hierarchical clustering, each element is a community subgraph,
 *      and contains a mapping list of apkName and community subgraph subgraph
 * 
 */
public class Cluster {
	private ArrayList<CommunitySubGraph> clusterGraphList = new ArrayList<>();
	private ArrayList<Integer> clusterIndexList = new ArrayList<>();
	private Map<String, String> apkSubgraphNameMap = new HashMap<>();
	private String index = "";

	public Cluster() {

	}

	public void iniApkNameSet() {
		for (int i = 0; i < clusterGraphList.size(); i++) {
			String path = clusterGraphList.get(i).getFilePath();
			int k = path.indexOf("SICG");
			String name = path.substring(0, k);
			apkSubgraphNameMap.put(name, clusterGraphList.get(i).getFileName());
		}
	}

	public void addOneSubgraph(CommunitySubGraph subGraph) {
		this.clusterGraphList.add(subGraph);
	}

	public void addOneIndex(int k) {
		clusterIndexList.add(k);
	}

	public String getClusterInfo() {
		String info = "";
		Iterator<String> iterator = apkSubgraphNameMap.keySet().iterator();
		while (iterator.hasNext()) {
			String apkName = iterator.next();
			String graphName = apkSubgraphNameMap.get(apkName);
			info += "	" + apkName + ": " + graphName + "\n";
		}
		return info;
	}

	public void mergeCluster(Cluster cluster) {
		for (int i = 0; i < cluster.clusterGraphList.size(); i++) {
			clusterGraphList.add(cluster.clusterGraphList.get(i));
			clusterIndexList.add(cluster.clusterIndexList.get(i));
		}
	}

	public ArrayList<CommunitySubGraph> getClusterGraphList() {
		return clusterGraphList;
	}

	public void setClusterGraphList(ArrayList<CommunitySubGraph> clusterGraphList) {
		this.clusterGraphList = clusterGraphList;
	}

	public ArrayList<Integer> getClusterIndexList() {
		return clusterIndexList;
	}

	public void setClusterIndexList(ArrayList<Integer> clusterIndexList) {
		this.clusterIndexList = clusterIndexList;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	/*
	 * Obtain the representative subgraph of the cluster,
	 * and the selection method is the one with the largest score in the subgraph
	 * set
	 */
	public CommunitySubGraph getSignificantSubgraph() {
		CommunitySubGraph subGraph = new CommunitySubGraph();
		double tmp = 0.0D;
		int index = -1;
		for (int i = 0; i < clusterGraphList.size(); i++) {
			double score = clusterGraphList.get(i).getSensitiveScore();
			if (score > tmp) {
				tmp = score;
				index = i;
			}
		}
		subGraph = clusterGraphList.get(index);
		return subGraph;
	}

	/*
	 * In the Kmeans algorithm, the similarity between the new subgraph
	 * and the cluster is calculated, and the average is taken.
	 */
	public double getAverageSimForOneGraph(CommunitySubGraph subGraph, FamilyWeightScore weightScore) {
		double sim = 0.0D;
		for (int i = 0; i < clusterGraphList.size(); i++) {
			double tmp = 0.0D;
			double dstScore = subGraph.getSensitiveScore();
			double srcScore = clusterGraphList.get(i).getSensitiveScore();
			double result = getDivideScore(srcScore, dstScore);
			if (result >= ConstantValue.getVar().minScoreSim) {
				SubGraphSimilarity subGraphSimilarity = new SubGraphSimilarity(clusterGraphList.get(i).getGraph(),
						subGraph.getGraph(), weightScore);
				tmp = subGraphSimilarity.getSimScore();
			} else {
				tmp = 0.0D;
			}
			sim += tmp;
		}
		sim = sim / Double.valueOf(this.clusterGraphList.size());
		return sim;
	}

	public double getDivideScore(double srcScore, double dstScore) {
		double result = 0.0D;
		if (srcScore <= 0.0D || dstScore <= 0.0D) {
			return result;
		}
		if (srcScore >= dstScore) {
			result = dstScore / srcScore;
		} else {
			result = srcScore / dstScore;
		}
		return result;
	}

	public Map<String, String> getApkSubgraphNameMap() {
		return apkSubgraphNameMap;
	}

	public void setApkSubgraphNameMap(Map<String, String> apkSubgraphNameMap) {
		this.apkSubgraphNameMap = apkSubgraphNameMap;
	}

	public boolean containOneAPKName(String apkName) {
		boolean contain = false;
		Iterator<String> iterator = apkSubgraphNameMap.keySet().iterator();
		while (iterator.hasNext()) {
			String tmpName = iterator.next();
			if (tmpName.equals(apkName)) {
				contain = true;
				return contain;
			}
		}
		return contain;
	}

}
