package GraphSimilarity;

/*
 *     The input is two subgraphs, and the corresponding list of family weights, 
 *     and the output is the similarity score of the two subgraphs 
 * 
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import APKData.SmaliGraph.MethodGraph;
import APKData.SmaliGraph.MethodNode;
import CandicateFamily.FamilyWeightScore;

public class SubGraphSimilarity {
	private MethodGraph srcGraph = new MethodGraph();
	private MethodGraph dstGraph = new MethodGraph();
	private Set<String> srcSensitiveMethodSet = new HashSet<>();
	private Set<String> dstSensitiveMethodSet = new HashSet<>();
	private Set<String> andSensitiveMethodSet = new HashSet<>();
	private Set<String> orSensitiveMethodSet = new HashSet<>();

	private double simScore = 0.0D;
	private Map<String, Double> scoreMap = new HashMap<>();

	public SubGraphSimilarity(MethodGraph sGraph, MethodGraph dGraph, FamilyWeightScore weightScore) {
		this.srcGraph = sGraph;
		this.dstGraph = dGraph;
		scoreMap = weightScore.getMethodScoreMap();
		iniSensitiveSet();
		AndSetNodesSim();
	}

	// Initialize a collection of sensitive functions
	public void iniSensitiveSet() {
		Iterator<MethodNode> srcNodeIterator = srcGraph.getNodeSet().iterator();
		while (srcNodeIterator.hasNext()) {
			MethodNode node = srcNodeIterator.next();
			String typeString = node.getNodeTypeString();
			if (!typeString.equals("Normal")) {
				this.srcSensitiveMethodSet.add(node.getCommonString());
			}
		}
		Iterator<MethodNode> dstNodeIterator = dstGraph.getNodeSet().iterator();
		while (dstNodeIterator.hasNext()) {
			MethodNode node = dstNodeIterator.next();
			String typeString = node.getNodeTypeString();
			if (!typeString.equals("Normal")) {
				this.dstSensitiveMethodSet.add(node.getCommonString());
			}
		}
		/*
		 * initial the andSensitiveMethodSet
		 */
		Iterator<String> srcIterator = srcSensitiveMethodSet.iterator();
		while (srcIterator.hasNext()) {
			String tmpString = srcIterator.next();
			if (dstSensitiveMethodSet.contains(tmpString)) {
				andSensitiveMethodSet.add(tmpString);
			}
		}
		/*
		 * initial the orSensitiveMethodSet
		 */
		srcIterator = srcSensitiveMethodSet.iterator();
		while (srcIterator.hasNext()) {
			String tmpString = srcIterator.next();
			orSensitiveMethodSet.add(tmpString);
		}
		Iterator<String> dstIterator = dstSensitiveMethodSet.iterator();
		while (dstIterator.hasNext()) {
			String tmpString = dstIterator.next();
			orSensitiveMethodSet.add(tmpString);
		}
	}

	public double AndSetNodesSim() {
		double simScore = 0.0D;
		double totalSensitiveSocre = 0.0D;
		double totalNodesSimScore = 0.0D;
		Iterator<String> andIterator = andSensitiveMethodSet.iterator();
		while (andIterator.hasNext()) {
			String nodeString = andIterator.next();
			// 如果scoreMap中包含敏感函数集合交集中的元素，则计算该元素节点在两个图中的相似性
			if (scoreMap.containsKey(nodeString)) {
				double nodeSim = oneNodeSim(nodeString);
				totalNodesSimScore += nodeSim * scoreMap.get(nodeString);
			}
		}
		Iterator<String> orIterator = orSensitiveMethodSet.iterator();
		while (orIterator.hasNext()) {
			String nodeString = orIterator.next();
			if (scoreMap.containsKey(nodeString)) {
				// 需要先判断敏感函数并集集合中的元素是否在scoreMap中，若存在，则获取其分值
				totalSensitiveSocre += scoreMap.get(nodeString);
			} else {
			}
		}
		simScore = totalNodesSimScore / totalSensitiveSocre;
		this.simScore = simScore;
		return simScore;
	}

	public double oneNodeSim(String imNodeCommonString) {
		ArrayList<Double> srcVector = new ArrayList<>();
		ArrayList<Double> dstVector = new ArrayList<>();
		ArrayList<String> OVStringList = new ArrayList<>();
		Iterator<String> andIterator = orSensitiveMethodSet.iterator();
		while (andIterator.hasNext()) {
			String andString = andIterator.next();
			// if(!andString.equals(imNodeCommonString)){
			// OVStringList.add(andString);
			// }
			OVStringList.add(andString);
		}
		Dijkstra dijkstra = new Dijkstra(srcGraph, scoreMap, imNodeCommonString, OVStringList);
		srcVector = dijkstra.getCorScoreList();
		dijkstra = new Dijkstra(dstGraph, scoreMap, imNodeCommonString, OVStringList);
		dstVector = dijkstra.getCorScoreList();
		double resultSimScore = 0.0D;
		resultSimScore = Cosine(srcVector, dstVector);
		// System.out.println("Cosine: " + resultSimScore);
		return resultSimScore;
	}

	public Double Cosine(ArrayList<Double> srcVector, ArrayList<Double> dstVector) {
		double cosScore = 0.0D;
		double srcSqr = 0.0D;
		double dstSqr = 0.0D;
		double fenmu = 0.0D;
		for (int i = 0; i < srcVector.size(); i++) {
			srcSqr += srcVector.get(i) * srcVector.get(i);
			dstSqr += dstVector.get(i) * dstVector.get(i);
		}
		fenmu = Math.sqrt(srcSqr) * Math.sqrt(dstSqr);
		double fenzi = 0.0D;
		for (int i = 0; i < srcVector.size(); i++) {
			fenzi += srcVector.get(i) * dstVector.get(i);
		}
		cosScore = fenzi / fenmu;
		return cosScore;
	}

	public MethodGraph getSrcGraph() {
		return srcGraph;
	}

	public void setSrcGraph(MethodGraph srcGraph) {
		this.srcGraph = srcGraph;
	}

	public MethodGraph getDstGraph() {
		return dstGraph;
	}

	public void setDstGraph(MethodGraph dstGraph) {
		this.dstGraph = dstGraph;
	}

	public Set<String> getSrcSensitiveMethodSet() {
		return srcSensitiveMethodSet;
	}

	public void setSrcSensitiveMethodSet(Set<String> srcSensitiveMethodSet) {
		this.srcSensitiveMethodSet = srcSensitiveMethodSet;
	}

	public Set<String> getDstSensitiveMethodSet() {
		return dstSensitiveMethodSet;
	}

	public void setDstSensitiveMethodSet(Set<String> dstSensitiveMethodSet) {
		this.dstSensitiveMethodSet = dstSensitiveMethodSet;
	}

	public Set<String> getAndSensitiveMethodSet() {
		return andSensitiveMethodSet;
	}

	public void setAndSensitiveMethodSet(Set<String> andSensitiveMethodSet) {
		this.andSensitiveMethodSet = andSensitiveMethodSet;
	}

	public Set<String> getOrSensitiveMethodSet() {
		return orSensitiveMethodSet;
	}

	public void setOrSensitiveMethodSet(Set<String> orSensitiveMethodSet) {
		this.orSensitiveMethodSet = orSensitiveMethodSet;
	}

	public double getSimScore() {
		return simScore;
	}

	public void setSimScore(double simScore) {
		this.simScore = simScore;
	}

	public Map<String, Double> getScoreMap() {
		return scoreMap;
	}

	public void setScoreMap(Map<String, Double> scoreMap) {
		this.scoreMap = scoreMap;
	}

}
