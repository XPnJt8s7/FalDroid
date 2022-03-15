package CommunityStructure.GraphData;

import APKData.SmaliGraph.GraphToGexf;
import APKData.SmaliGraph.MethodEdge;
import APKData.SmaliGraph.MethodGraph;
import APKData.SmaliGraph.MethodNode;
import CandicateFamily.FamilyWeightScore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/*
 *    A subgraph is constructed by a community, and the data is not saved when the object is initialized
 *    Pass in the set of parameter nodes by calling the function iniSubgraph and build a subgraph
 *    Calculate the sensitivity coefficient of the subgraph by calling the function iniSensitivityCoefficient 
 *    and passing in the parameter FamilyWeightScore object
 */
public class CommunitySG {
	private MethodGraph subgraph = new MethodGraph();
	private double sensitivityScore = 0.0D;
	private String noString = "";
	private ArrayList<MethodMolNo> molList = new ArrayList<>();

	public CommunitySG(String no) {
		this.noString = no;
	}

	public CommunitySG() {
	}

	public void iniSensitivityCoefficient(FamilyWeightScore weightScore) {
		double totalScore = 0.0D;
		Map<String, Double> scoreMap = new HashMap<>();
		scoreMap = weightScore.getMethodScoreMap();
		Iterator<MethodNode> nodeIterator = this.subgraph.getNodeSet().iterator();
		while (nodeIterator.hasNext()) {
			MethodNode node = nodeIterator.next();
			String commonString = node.getCommonString();
			if (scoreMap.containsKey(commonString)) {
				double score = scoreMap.get(commonString);
				totalScore += score;
			}
		}
		this.sensitivityScore = totalScore;
	}

	public void iniSubgraph(Set<MethodEdge> srcEdgeSet) {
		Set<MethodNode> nodeSet = new HashSet<>();
		Set<MethodEdge> edgeSet = new HashSet<>();
		for (int i = 0; i < molList.size(); i++) {
			MethodMolNo molNo = molList.get(i);
			String commonString = molNo.getMethodName();
			MethodNode node = new MethodNode(commonString);
			nodeSet.add(node);
		}
		Iterator<MethodEdge> srcEdgeIterator = srcEdgeSet.iterator();
		while (srcEdgeIterator.hasNext()) {
			MethodEdge srcEdge = srcEdgeIterator.next();
			if (nodeSet.contains(srcEdge.getCallerNode()) && nodeSet.contains(srcEdge.getCalleeNode())) {
				edgeSet.add(srcEdge);
			}
		}
		this.subgraph.setNodeSet(nodeSet);
		this.subgraph.setEdgeSet(edgeSet);
	}

	/*
	 * This function writes the subgraph of the object to a file
	 */
	public void writeToGexfFile(String writeGexfFile) {
		GraphToGexf graphToGexf = new GraphToGexf(this.subgraph, writeGexfFile);
	}

	public void addOneMethod(MethodMolNo mol) {
		this.molList.add(mol);
		// System.out.print(molList.size()+" ");
	}

	public void setNodeSet(Set<MethodNode> nodeSet) {
		subgraph.setNodeSet(nodeSet);
	}

	public void setEdgeSet(Set<MethodEdge> edgeSet) {
		subgraph.setEdgeSet(edgeSet);
	}

	public MethodGraph getSubgraph() {
		return subgraph;
	}

	public void setSubgraph(MethodGraph subgraph) {
		this.subgraph = subgraph;
	}

	public double getSensitivityScore() {
		return sensitivityScore;
	}

	// public void setSensitivityScore(double sensitivityScore) {
	// this.sensitivityScore = sensitivityScore;
	// }

	public void showCommunitySubGraph() {
		System.out.println("subgraph:" + this.noString);
		System.out.println("  Node:");
		Iterator<MethodNode> nodeIterator = subgraph.getNodeSet().iterator();
		while (nodeIterator.hasNext()) {
			MethodNode node = nodeIterator.next();
			System.out.println("    " + node.getCommonString());
		}
		System.out.println("   Edge");
		Iterator<MethodEdge> edgeIterator = subgraph.getEdgeSet().iterator();
		while (edgeIterator.hasNext()) {
			MethodEdge edge = edgeIterator.next();
			System.out.println(edge.getMethodCallingString());
		}
	}

	public void showMolList() {
		for (int i = 0; i < molList.size(); i++) {
			System.out.println(molList.get(i).getCommonString());
		}
	}

	public String getNoString() {
		return noString;
	}

	public void setNoString(String noString) {
		this.noString = noString;
	}

	public void printCommunitySGInfo() {
		System.out.println("[CommunitySG] Graph file path: " + subgraph.getFilePathString());
		System.out.println("[CommunitySG] Graph number string: " + noString);
		System.out.println("[CommunitySG] Sensitivity score: " + sensitivityScore);
	}
}
