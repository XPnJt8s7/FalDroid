package APKData.SmaliGraph;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import APKData.SmaliData.MethodCalling;
import APKData.SmaliData.SmaliClass;
import APKData.SmaliData.SmaliDir;
import APKData.SmaliData.SmaliMethod;

/*
 * The input is the file object of a decompiled folder, and the static function call diagram of the app is obtained
 */
public class MethodGraph implements Serializable {
	private String filePathString = "";
	private ArrayList<SmaliClass> smaliClassList = new ArrayList<>();
	private Set<MethodNode> nodeSet = new HashSet<>();
	private Set<MethodEdge> edgeSet = new HashSet<>();
	private Set<MethodNode> sourceNodeSet = new HashSet<>();
	private Set<MethodNode> sinkNodeSet = new HashSet<>();
	private Map<String, Integer> sourceNodeCategoryNumMap = new HashMap<>();
	private Map<String, Integer> sinkNodeCategoryNumMap = new HashMap<>();

	private Set<String> nodeNameSet = new HashSet<>();
	private Set<String> edgeNameSet = new HashSet<>();

	private Map<String, MethodNode> nodeMap = new HashMap<>();

	public MethodGraph() {
	}

	public MethodGraph(File file) {
		this.filePathString = file.getAbsolutePath();
		SmaliDir dir = new SmaliDir(file);
		smaliClassList = dir.getClassesList();
		iniNodeAndEdge();
		iniChildrenNodesAndParentNodes();
		statisticCategoryNum();
	}

	public void iniNodeAndEdge() {
		for (int i = 0; i < smaliClassList.size(); i++) {
			SmaliClass tmpClass = new SmaliClass();
			tmpClass = smaliClassList.get(i);
			ArrayList<SmaliMethod> tmpMethodList = new ArrayList<>();
			tmpMethodList = tmpClass.getMethodList();
			for (int j = 0; j < tmpMethodList.size(); j++) {
				SmaliMethod tmpMethod = new SmaliMethod();
				tmpMethod = tmpMethodList.get(j);

				ArrayList<MethodCalling> callList = new ArrayList<>();
				callList = tmpMethod.getMethodCalls();
				for (int k = 0; k < callList.size(); k++) {
					MethodCalling calling = new MethodCalling();
					calling = callList.get(k);
					MethodNode srcNode = new MethodNode(calling.getCallerMethod());
					MethodNode dstNode = new MethodNode(calling.getCalleeMethod());
					MethodEdge edge = new MethodEdge(srcNode, dstNode);
					edgeSet.add(edge);
					nodeSet.add(srcNode);
					nodeSet.add(dstNode); // Add method node to point collection
					edgeNameSet.add(edge.getMethodCallingString());
					nodeNameSet.add(srcNode.getNodeLabelString());
					nodeNameSet.add(dstNode.getNodeLabelString());
					// System.out.println(node.getNodeTypeString());
					if (srcNode.getNodeTypeString().equals("Source")) {
						this.sourceNodeSet.add(srcNode);
					} // Add source node
					else if (srcNode.getNodeTypeString().equals("Sink")) {
						this.sinkNodeSet.add(srcNode);
					} // Add sink node
					if (dstNode.getNodeTypeString().equals("Source")) {
						this.sourceNodeSet.add(dstNode);
					} else if (dstNode.getNodeTypeString().equals("Sink")) {
						this.sinkNodeSet.add(dstNode);
					}
				}
			}
		}
	}

	// public void setInDegreeNodesAndOutDegreeNodes(){
	// Iterator<MethodEdge> edgeIterator=this.edgeSet.iterator();
	// while(edgeIterator.hasNext()){
	// MethodEdge edge=edgeIterator.next();
	// MethodNode node=edge.getCallerNode();
	//
	// }
	// }
	public void iniChildrenNodesAndParentNodes() {
		/*
		 * Update the parent node and child node relationship of the node in the nodemap
		 */
		Iterator<MethodNode> nodeIterator = this.nodeSet.iterator();
		while (nodeIterator.hasNext()) {
			MethodNode node = nodeIterator.next();
			String nodeCommonString = node.getCommonString();
			nodeMap.put(nodeCommonString, node);
		}
		Iterator<MethodEdge> edgeIterator = this.edgeSet.iterator();
		while (edgeIterator.hasNext()) {
			MethodEdge edge = edgeIterator.next();
			String srcNodeCommonString = edge.getCallerNode().getCommonString();
			String dstCommonString = edge.getCalleeNode().getCommonString();
			nodeMap.get(srcNodeCommonString).addChildNode(nodeMap.get(dstCommonString));
			nodeMap.get(dstCommonString).addParentNode(nodeMap.get(srcNodeCommonString));
		}
	}

	public void statisticCategoryNum() {
		/*
		 * Count the category information of sensitive source nodes
		 */
		Iterator<MethodNode> sourceIterator = sourceNodeSet.iterator();
		while (sourceIterator.hasNext()) {
			MethodNode srcNode = sourceIterator.next();
			String methodCategoryString = srcNode.getNodeCategoryString();
			if (sourceNodeCategoryNumMap.containsKey(methodCategoryString)) {
				int k = sourceNodeCategoryNumMap.get(methodCategoryString);
				k += 1;
				sourceNodeCategoryNumMap.remove(methodCategoryString);
				sourceNodeCategoryNumMap.put(methodCategoryString, k);
			} else {
				sourceNodeCategoryNumMap.put(methodCategoryString, 1);
			}
		}
		/*
		 * Statistically sensitive destination node category information
		 */
		Iterator<MethodNode> sinkIterator = sinkNodeSet.iterator();
		while (sinkIterator.hasNext()) {
			MethodNode dstNode = sinkIterator.next();
			String methodCategoryString = dstNode.getNodeCategoryString();
			if (sinkNodeCategoryNumMap.containsKey(methodCategoryString)) {
				int k = sinkNodeCategoryNumMap.get(methodCategoryString);
				k += 1;
				sinkNodeCategoryNumMap.remove(methodCategoryString);
				sinkNodeCategoryNumMap.put(methodCategoryString, k);
			} else {
				sinkNodeCategoryNumMap.put(methodCategoryString, 1);
			}
		}
	}

	public void showNodeSet() {
		String resultString = "";
		Iterator<MethodNode> nodeIterator = nodeSet.iterator();
		while (nodeIterator.hasNext()) {
			MethodNode node = nodeIterator.next();
			resultString += node.getNodeLabelString() + "\n";
		}
		System.out.println(resultString);
	}

	public String getGraphInformation() {
		String resultString = "";
		resultString += "File path: " + this.filePathString + "\n";
		// System.out.println("Number of nodes: " + this.nodeSet.size());
		resultString += "Number of nodes:" + this.nodeSet.size() + "\n";

		// System.out.println("Number of sides: " + this.edgeSet.size());
		resultString += "Number of sides:" + this.edgeSet.size() + "\n";

		// System.out.println("Number of sensitive source nodes: " +
		// this.sourceNodeSet.size());
		resultString += "sensitive source node:" + this.sourceNodeSet.size() + "\n";
		Iterator<String> srcIterator = sourceNodeCategoryNumMap.keySet().iterator();
		while (srcIterator.hasNext()) {
			String sourceString = srcIterator.next();
			int num = sourceNodeCategoryNumMap.get(sourceString);
			resultString += "	" + sourceString + ":	" + num + "\n";
		}
		resultString += "sensitive destination node:" + this.sinkNodeSet.size() + "\n";
		Iterator<String> dstIterator = sinkNodeCategoryNumMap.keySet().iterator();
		while (dstIterator.hasNext()) {
			String sinkString = dstIterator.next();
			int num = sinkNodeCategoryNumMap.get(sinkString);
			resultString += "	" + sinkString + ":	" + num + "\n";
		}
		return resultString;
	}

	public void printGraphInfo() {
		System.out.println("Number of nodes: " + this.nodeSet.size());
		System.out.println("Number of sides: " + this.edgeSet.size());

		System.out.println("Number of sensitive source nodes: " + this.sourceNodeSet.size());
		Iterator<String> srcIterator = sourceNodeCategoryNumMap.keySet().iterator();
		while (srcIterator.hasNext()) {
			String sourceString = srcIterator.next();
			int num = sourceNodeCategoryNumMap.get(sourceString);
			System.out.println(String.format("- %s: %d", sourceString, num));
		}

		System.out.println("Number of sensitive destination nodes: " + this.sinkNodeSet.size());
		Iterator<String> dstIterator = sinkNodeCategoryNumMap.keySet().iterator();
		while (dstIterator.hasNext()) {
			String sinkString = dstIterator.next();
			int num = sinkNodeCategoryNumMap.get(sinkString);
			System.out.println(String.format("- %s: %d", sinkString, num));
		}
	}

	public String getFilePathString() {
		return filePathString;
	}

	public void setFilePathString(String filePathString) {
		this.filePathString = filePathString;
	}

	public ArrayList<SmaliClass> getSmaliClassList() {
		return smaliClassList;
	}

	public void setSmaliClassList(ArrayList<SmaliClass> smaliClassList) {
		this.smaliClassList = smaliClassList;
	}

	public Set<MethodNode> getNodeSet() {
		return nodeSet;
	}

	public void setNodeSet(Set<MethodNode> nodeSet) {
		this.nodeSet = nodeSet;
	}

	public Set<MethodEdge> getEdgeSet() {
		return edgeSet;
	}

	public void setEdgeSet(Set<MethodEdge> edgeSet) {
		this.edgeSet = edgeSet;
	}

	public Set<MethodNode> getSourceNodeSet() {
		return sourceNodeSet;
	}

	public void setSourceNodeSet(Set<MethodNode> sourceNodeSet) {
		this.sourceNodeSet = sourceNodeSet;
	}

	public Set<MethodNode> getSinkNodeSet() {
		return sinkNodeSet;
	}

	public void setSinkNodeSet(Set<MethodNode> sinkNodeSet) {
		this.sinkNodeSet = sinkNodeSet;
	}

	public Set<String> getNodeNameSet() {
		return nodeNameSet;
	}

	public void setNodeNameSet(Set<String> nodeNameSet) {
		this.nodeNameSet = nodeNameSet;
	}

	public Set<String> getEdgeNameSet() {
		return edgeNameSet;
	}

	public void setEdgeNameSet(Set<String> edgeNameSet) {
		this.edgeNameSet = edgeNameSet;
	}

	public Map<String, MethodNode> getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(Map<String, MethodNode> nodeMap) {
		this.nodeMap = nodeMap;
	}

	public void checkNodeMap() {
		String str = "";
		Iterator<String> iterator = nodeMap.keySet().iterator();
		while (iterator.hasNext()) {
			String nodeLabelString = iterator.next();
			MethodNode node = nodeMap.get(nodeLabelString);
			System.out.println(nodeLabelString + ": child : # " + node.getChildrenNodes().size());
		}
	}
}
