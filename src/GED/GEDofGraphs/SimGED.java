package GED.GEDofGraphs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SimGED {
	public int[][] costMatrix;
	public int edgenum1;
	public int edgenum2;
	public ArrayList<Node> srcNodeList = new ArrayList<>();
	public ArrayList<Node> dstNodeList = new ArrayList<>();
	public ArrayList<Edge> srcEdgeList = new ArrayList<>();
	public ArrayList<Edge> dstEdgeList = new ArrayList<>();
	public int[][] srcMatrix;
	public int[][] dstMatrix;
	public Map<Integer, Integer> map = new HashMap<>();
	public Map<Integer, Integer> change = new HashMap<>();
	public double sim;

	public SimGED(String srcNodeFilePath, String dstNodeFilePath, String srcEdgeFilePath, String dstEdgeFilePath) {
		ReadNode(srcNodeFilePath, dstNodeFilePath); // 锟斤拷始锟斤拷nodeList
		int srcSize = srcNodeList.size();
		int dstSize = dstNodeList.size();
		srcMatrix = new int[srcSize][srcSize];
		dstMatrix = new int[dstSize][dstSize];
		ReadEdge(srcEdgeFilePath, dstEdgeFilePath); // 锟斤拷始锟斤拷edgeList 锟皆硷拷 锟斤拷锟斤拷
		Traversal(srcNodeList, srcMatrix);
		Traversal(dstNodeList, dstMatrix);
		CreatCostmatrix(srcMatrix, dstMatrix, srcNodeList, dstNodeList);
		calculateSimilarity(srcSize, dstSize);
	}

	public double getSimilarity() {
		return this.sim;
	}

	public double calculateSimilarity(int srcSize, int dstSize) {
		int m = srcSize;
		int n = dstSize;
		System.out.println("Hungry锟斤拷锟斤拷锟斤拷");
		Index hungary = new Index();
		KuhnMunkres km = new KuhnMunkres(m + n);
		double[] result = new double[m + n];
		int[][] recostmatrix = new int[m + n][m + n];
		for (int i = 0; i < m + n; i++) {
			for (int j = 0; j < m + n; j++) {
				recostmatrix[i][j] = -costMatrix[i][j];
			}
		}
		int[][] re = km.getMaxBipartie(recostmatrix, result);// KuhnMunkres锟姐法锟斤拷锟斤拷锟斤拷小锟斤拷鄣亩锟斤拷锟狡ワ拷锟�
		int len = Math.min(km.getlenX(), km.getlenY());
		System.out.println("Hungry锟斤拷锟斤拷锟斤拷锟�" + " len:" + len);
		map = new HashMap<Integer, Integer>();
		for (int i = 0; i < len; i++) {
			map.put(re[i][0], re[i][1]);
		}
		change = new HashMap<Integer, Integer>();
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			int i = map.get(key).intValue();
			int j = map.get(val).intValue();
			if (i < m)
				change.put(i, j);// 锟斤拷锟斤拷匹锟斤拷锟斤拷锟�
			// System.out.println(i + " " + j);//锟斤拷锟皆讹拷锟斤拷匹锟斤拷锟斤拷锟斤拷
		}
		Iterator iter1 = change.entrySet().iterator();
		while (iter1.hasNext()) {
			Map.Entry entry = (Map.Entry) iter1.next();
			Object key = entry.getKey();
			Object val = entry.getValue();
			System.out.println(key + " " + val);// 锟斤拷锟斤拷匹锟斤拷锟斤拷锟�
		}
		long NodeCost = NodeCost(map, m, n);
		long EdgeCost = EdgeCost(map, m, n);
		System.out.println("NodeCost:" + NodeCost);
		System.out.println("EdgeCost:" + EdgeCost);
		long EditDistance = NodeCost + EdgeCost;// 图锟洁辑锟斤拷锟斤拷
		double Similarity = (double) EditDistance / (double) (m + n + edgenum1 + edgenum2);
		System.out.println("EditDistance:" + EditDistance);// 锟斤拷锟斤拷图锟洁辑锟斤拷锟斤拷
		System.out.println("Similarity:" + (1 - Similarity));// 锟斤拷锟斤拷图锟斤拷锟狡讹拷
		this.sim = 1 - Similarity;
		return 1 - Similarity;
	}

	public int[][] CreatCostmatrix(int[][] graph1, int[][] graph2, ArrayList<Node> nodes1, ArrayList<Node> nodes2) {
		System.out.println("锟斤拷锟斤拷锟斤拷木锟斤拷锟斤拷锟�");
		int m = nodes1.size();
		int n = nodes2.size();
		Map<Integer, Integer> inmap1 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> outmap1 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> inmap2 = new HashMap<Integer, Integer>();
		Map<Integer, Integer> outmap2 = new HashMap<Integer, Integer>();
		ArrayList<Integer> inset1 = new ArrayList<Integer>();
		ArrayList<Integer> outset1 = new ArrayList<Integer>();
		ArrayList<Integer> inset2 = new ArrayList<Integer>();
		ArrayList<Integer> outset2 = new ArrayList<Integer>();
		ArrayList<Integer> inter = new ArrayList<Integer>();
		ArrayList<Integer> outer = new ArrayList<Integer>();// 锟斤拷锟斤拷
		costMatrix = new int[m + n][m + n];
		int inedge_cost = 0;
		int outedge_cost = 0;
		int relabel_cost = 0;
		for (int i = 0; i < m; i++) {
			inmap1 = nodes1.get(i).getinMap();
			outmap1 = nodes1.get(i).getoutMap();
			inset1 = Aggregate.Travel(inmap1);
			outset1 = Aggregate.Travel(outmap1);
			for (int j = 0; j < n; j++) {
				inmap2 = nodes2.get(j).getinMap();
				outmap2 = nodes2.get(j).getoutMap();
				inset2 = Aggregate.Travel(inmap2);
				outset2 = Aggregate.Travel(outmap2);
				if (!(nodes1.get(i).getName().equals(nodes2.get(j).getName())))
				// relabel_cost = 1;
				{
					relabel_cost = 0;
				} else
					relabel_cost = 0;
				inter = Aggregate.intersect(inmap1, inmap2);
				outer = Aggregate.intersect(outmap1, outmap2);
				inedge_cost = inset1.size() + inset2.size() - 2 * inter.size();
				outedge_cost = outset1.size() + outset2.size() - 2 * outer.size();
				// System.out.println("["+i+"]["+j+"]:");
				// System.out.println(inedge_cost);
				// System.out.println(outedge_cost);
				costMatrix[i][j] = relabel_cost + inedge_cost + outedge_cost;// 锟斤拷锟较角撅拷锟斤拷cost
				// costMatrix[i][j] = 0;
			}
		}
		// System.out.println("costmatrix:"+costmatrix[1][1]);
		for (int i = 0; i < m; i++) {
			inmap1 = nodes1.get(i).getinMap();
			outmap1 = nodes1.get(i).getoutMap();
			inset1 = Aggregate.Travel(inmap1);
			outset1 = Aggregate.Travel(outmap1);
			inedge_cost = inset1.size();
			outedge_cost = outset1.size();
			for (int j = n; j < m + n; j++) {
				costMatrix[i][j] = 0x7fffffff;// 锟斤拷锟矫非对斤拷锟斤拷锟斤拷锟轿拷锟斤拷锟斤拷
			}
			costMatrix[i][n + i] = 1 + inedge_cost + outedge_cost;// 锟斤拷锟铰角撅拷锟斤拷
		}
		for (int j = 0; j < n; j++) {
			inmap2 = nodes2.get(j).getinMap();
			outmap2 = nodes2.get(j).getoutMap();
			inset2 = Aggregate.Travel(inmap2);
			outset2 = Aggregate.Travel(outmap2);
			inedge_cost = inset2.size();
			outedge_cost = outset2.size();
			for (int i = m; i < m + n; i++) {
				costMatrix[i][j] = 0x7fffffff;
			}
			costMatrix[m + j][j] = 1 + inedge_cost + outedge_cost;// 锟斤拷锟较角撅拷锟斤拷
		} // 锟斤拷锟铰角撅拷锟斤拷为锟斤拷
			// 锟斤拷印锟斤拷锟斤拷锟斤拷锟�
		int t = m + n;
		for (int i = 0; i < t; i++) {
			System.out.println();
			for (int j = 0; j < t; j++) {
				System.out.print(costMatrix[i][j] + " ");
			}
		}
		return costMatrix;
	}

	public void ReadNode(String srcNodeFilePath, String dstNodeFilePath) {
		String thisLine1 = null;
		String thisLine2 = null;
		int count = 0;
		try {
			// open input stream test.txt for reading purpose.
			FileInputStream f1 = new FileInputStream(srcNodeFilePath);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(f1));
			while ((thisLine1 = br1.readLine()) != null) {
				Node node = new Node();
				int num = Integer.valueOf(thisLine1.split("#")[0]).intValue();
				String attr = thisLine1.split("#")[1];
				node.setNum(num);
				node.setName(attr);
				srcNodeList.add(node);
				// System.out.println(num);
				// System.out.println(attr);
			}
			// System.out.println(nodes1.get(nodes1.size()-1).getNum());
			br1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// open input stream test.txt for reading purpose.
			FileInputStream f2 = new FileInputStream(dstNodeFilePath);
			BufferedReader br2 = new BufferedReader(new InputStreamReader(f2));
			while ((thisLine2 = br2.readLine()) != null) {
				Node node = new Node();
				int num = Integer.valueOf(thisLine2.split("#")[0]).intValue();
				String attr = thisLine2.split("#")[1];
				node.setNum(num);
				node.setName(attr);
				dstNodeList.add(node);
				// System.out.println(num);
				// System.out.println(attr);
			}
			// System.out.println(nodes2.get(nodes2.size()-1).getNum());
			br2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void ReadEdge(String srcEdgeFilePath, String dstEdgeFilePath) {
		edgenum1 = 0;
		edgenum2 = 0;
		String thisLine1 = null;
		String thisLine2 = null;
		try {
			// open input stream test.txt for reading purpose.
			FileInputStream f1 = new FileInputStream(srcEdgeFilePath);
			BufferedReader br1 = new BufferedReader(new InputStreamReader(f1));
			while ((thisLine1 = br1.readLine()) != null) {
				String[] line = thisLine1.split(" ");
				int source = Integer.valueOf(line[0]).intValue();
				int target = Integer.valueOf(line[1]).intValue();
				int weight = Integer.valueOf(line[2]).intValue();
				Edge edge = new Edge();
				edge.setSource(source);
				edge.setTarget(target);
				edge.setweight(weight);
				srcEdgeList.add(edge);
				edgenum1 = edgenum1 + weight;
				srcMatrix[source][target] = weight;
				// System.out.println(num);
				// System.out.println(attr);
			}
			// System.out.println(graph1[0][0]);
			br1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			// open input stream test.txt for reading purpose.
			FileInputStream f2 = new FileInputStream(dstEdgeFilePath);
			BufferedReader br2 = new BufferedReader(new InputStreamReader(f2));
			while ((thisLine2 = br2.readLine()) != null) {
				String[] line = thisLine2.split(" ");
				int source = Integer.valueOf(line[0]).intValue();
				int target = Integer.valueOf(line[1]).intValue();
				int weight = Integer.valueOf(line[2]).intValue();
				Edge edge = new Edge();
				edge.setSource(source);
				edge.setTarget(target);
				edge.setweight(weight);
				dstEdgeList.add(edge);
				edgenum2 = edgenum2 + weight;
				dstMatrix[source][target] = weight;
				// System.out.println(num);
				// System.out.println(attr);
			}
			// System.out.println(graph2[0][4]);
			br2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Traversal(ArrayList<Node> nodeList, int[][] graph) {
		System.out.println("锟斤拷锟斤拷锟斤拷");
		int size = nodeList.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				if (graph[j][i] != 0) {
					Node in = nodeList.get(j);
					int innum = nodeList.get(j).getNum();
					// System.out.println(innum);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
					nodeList.get(i).setParent(in);
					nodeList.get(i).setinMap(innum, graph[j][i]);
				}
				if (graph[i][j] != 0) {
					Node out = nodeList.get(j);
					int outnum = nodeList.get(j).getNum();
					// System.out.println(outnum);//锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
					nodeList.get(i).setChild(out);
					nodeList.get(i).setoutMap(outnum, graph[i][j]);
				}
			}
		}

	}

	public int NodeCost(Map<Integer, Integer> map, int m, int n) {
		int count = 0;
		int nodecost = 0;
		Iterator iter = map.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Integer key = (Integer) entry.getKey();
			Integer val = (Integer) entry.getValue();
			int i = key.intValue();
			int j = val.intValue();
			// System.out.println("Node:"+i + " " + j);
			if (i < m && j < n) {
				// if(!(srcNodeList.get(i).getName().equals(dstNodeList.get(j).getName()))){
				//// System.out.println("Node:"+i + " " + j);
				//// System.out.println("Node:"+nodes1.get(i).getName() + " " +
				// nodes2.get(j).getName());
				// nodecost = nodecost+1;//锟斤拷锟斤拷锟斤拷锟斤拷
				//
				// }
			} else if (i < m && j >= n && j < m + n) {
				nodecost = nodecost + 1;

			} else if (i >= m && i < m + n && j < n) {
				nodecost = nodecost + 1;// 锟秸节碉拷匹锟斤拷锟斤拷实锟节碉拷cost+1

			}

		}
		// System.out.println("count:"+ count);
		return nodecost;
	}

	public int EdgeCost(Map<Integer, Integer> map, int m, int n) {
		int count = 0;
		int edgecost = 0;
		int source1 = 0;
		int source2 = 0;
		int target1 = 0;
		int target2 = 0;
		int weight = 0;
		int weight1 = 0;
		int weight2 = 0;
		int change1 = 0;
		int change2 = 0;
		for (Edge edge1 : srcEdgeList) {
			source1 = edge1.getSource();
			target1 = edge1.getTarget();
			weight1 = edge1.getweight();
			change1 = change.get(Integer.valueOf(source1)).intValue();
			change2 = change.get(Integer.valueOf(target1)).intValue();
			// System.out.println("change1:"+ change1);
			// System.out.println("change2:"+ change2);
			for (Edge edge2 : dstEdgeList) {
				source2 = edge2.getSource();
				target2 = edge2.getTarget();
				if (change1 == source2 && change2 == target2) {
					count++;
					weight2 = edge2.getweight();
					weight = Math.min(weight1, weight2);
					edgecost = edgecost + weight;
					// System.out.println("weight:"+ weight);
				}
			}
		}
		// System.out.println("count:"+ count);
		// System.out.println("edgecost:"+ edgecost);
		edgecost = edgenum1 + edgenum2 - 2 * edgecost;
		return edgecost;
	}
}
