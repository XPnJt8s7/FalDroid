package APKData.ReduceGraph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import APKData.SmaliGraph.MethodEdge;
import APKData.SmaliGraph.MethodGraph;
import APKData.SmaliGraph.MethodNode;

public class ReduceGraph {
	private MethodGraph sourceGraph;
	private MethodGraph reduceGraph;

	public ReduceGraph(MethodGraph graph) {
		sourceGraph = new MethodGraph();
		reduceGraph = new MethodGraph();

		sourceGraph = graph;
		iniReduceGraphNode();
	}

	public void iniReduceGraphNode() {
		/*
		 * Based on the sensitive source node and sensitive destination node, add its
		 * parent nodes to the new network in turn
		 */
		Set<MethodNode> reduceNodeSet = new HashSet<>();
		Set<MethodEdge> reduceEdgeSet = new HashSet<>();
		/*
		 * First add sensitive nodes to the network
		 */
		Set<MethodNode> nodeSet = new HashSet<>();
		nodeSet = sourceGraph.getNodeSet();
		Iterator<MethodNode> nodeIterator = nodeSet.iterator();
		while (nodeIterator.hasNext()) {
			MethodNode node = nodeIterator.next();
			if (node.getNodeTypeString().equals("Source") || node.getNodeTypeString().equals("Sink")) {
				reduceNodeSet.add(node);
			}
		}
		/*
		 * Then add its parent node in turn
		 */
		while (true) {
			int k = reduceNodeSet.size();
			Iterator<MethodNode> reIterator = reduceNodeSet.iterator();
			Set<MethodNode> addNodes = new HashSet<>();
			while (reIterator.hasNext()) {
				MethodNode node = new MethodNode();
				node = reIterator.next();
				Set<MethodNode> parentsSet = new HashSet<>();
				parentsSet = node.getParentNodes();
				Iterator<MethodNode> parentIterator = parentsSet.iterator();
				while (parentIterator.hasNext()) {
					MethodNode parentNode = new MethodNode();
					parentNode = parentIterator.next();
					addNodes.add(parentNode);
				}
			}
			Iterator<MethodNode> addIterator = addNodes.iterator();
			while (addIterator.hasNext()) {
				MethodNode add = addIterator.next();
				reduceNodeSet.add(add);
			}
			Iterator<MethodEdge> edgeIterator = sourceGraph.getEdgeSet().iterator();
			while (edgeIterator.hasNext()) {
				MethodEdge edge = edgeIterator.next();
				MethodNode srcNode = edge.getCallerNode();
				MethodNode dstNode = edge.getCalleeNode();
				if (reduceNodeSet.contains(srcNode) && reduceNodeSet.contains(dstNode)) {
					reduceEdgeSet.add(edge);
				}
			}
			int m = reduceNodeSet.size();
			if (k == m) {
				break;
			}
		}
		reduceGraph.setNodeSet(reduceNodeSet);
		reduceGraph.setEdgeSet(reduceEdgeSet);
	}

	public MethodGraph getReduceGraph() {
		return reduceGraph;
	}

	public void setReduceGraph(MethodGraph reduceGraph) {
		this.reduceGraph = reduceGraph;
	}

}
