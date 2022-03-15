package GraphSimilarity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import APKData.SmaliGraph.MethodEdge;
import APKData.SmaliGraph.MethodGraph;
import APKData.SmaliGraph.MethodNode;

public class Dijkstra {
	private int [][] W1;         //图矩阵
	private ArrayList<String> nodeComList=new ArrayList<>();
	private Map<String, Double> scoreMap=new HashMap<>();
	private ArrayList<Double> corScoreList=new ArrayList<>();
	public Dijkstra(){}
	/*
	 *     注意：  OVString 中包含 imNodeCommonString
	 */
	public Dijkstra(MethodGraph graph, Map<String, Double> scoreMap, String imNodeCommonString, ArrayList<String> OVString) {
          nodeComList= iniMatrx(graph);
          this.scoreMap=scoreMap;
           ArrayList<Integer> disList=new ArrayList<>();
           disList=disVector(imNodeCommonString, OVString);
           calCorScore(disList, OVString);
	}
	public ArrayList<String> iniMatrx(MethodGraph graph){
		ArrayList<String> nodeCommonStringList=new ArrayList<>();
		Iterator<MethodNode> nodeIterator=graph.getNodeSet().iterator();
		while(nodeIterator.hasNext()){
			MethodNode node=nodeIterator.next();
			String commonString=node.getCommonString();
			nodeCommonStringList.add(commonString);
		}
		int k=nodeCommonStringList.size();
		this.W1=new int[k][k];
		for(int i=0;i<k;i++){
			for(int j=0;j<k;j++){
				if(i==j){
					W1[i][j]=0;
				}
				else{
					W1[i][j]=10000;
				}
			}
		}
		Iterator<MethodEdge> edgeIterator=graph.getEdgeSet().iterator();
		while(edgeIterator.hasNext()){
			MethodEdge edge=edgeIterator.next();
			String srcNodeString=edge.getCallerNode().getCommonString();
			String dstNodeString=edge.getCalleeNode().getCommonString();
			int k1=nodeCommonStringList.indexOf(srcNodeString);
			int k2=nodeCommonStringList.indexOf(dstNodeString);
			W1[k1][k2]=1;
			W1[k2][k1]=1;
		}
		return nodeCommonStringList;
	}
	public void calCorScore(ArrayList<Integer> disList, ArrayList<String> OVString){
		int num=disList.size();
		for(int i=0;i<num;i++){
			double corScore=0.0D;
			if(disList.get(i)>0){
				String dstCommonString=OVString.get(i);
				double senScore=0.0D;
				if(scoreMap.containsKey(dstCommonString)){
					senScore=scoreMap.get(dstCommonString);
				}
				corScore=senScore/disList.get(i);
			}
			else{
				corScore=0.0D;
			}
			DecimalFormat    df   = new DecimalFormat("######0.000");   
			String value=df.format(corScore);
			corScoreList.add(Double.valueOf(value));
		}
	}
	public ArrayList<Integer> disVector(String imNodeCommonString, ArrayList<String> OVString){
		ArrayList<Integer> disList=new ArrayList<>();
		for(int i=0;i<OVString.size();i++){
			int k1=nodeComList.indexOf(imNodeCommonString);
			int dis=0;
			int k2=-1;
			if(nodeComList.contains(OVString.get(i))){
				if(OVString.get(i).equals(imNodeCommonString)){
					dis=1;
				}
				else{
					k2=nodeComList.indexOf(OVString.get(i));
					dis=calculate(k1, k2);
				}
			}
			else{
			    dis=0;	
			}
			disList.add(dis);
		}
		return disList;
	}
	public int calculate(int start, int end){
		int dis=0;
	        boolean[] isLabel = new boolean[W1[0].length];// 是否标号  
	        int[] indexs = new int[W1[0].length];// 所有标号的点的下标集合，以标号的先后顺序进行存储，实际上是一个以数组表示的栈  
	        int i_count = -1;//栈的顶点  
	        int[] distance = W1[start].clone();// v0到各点的最短距离的初始值  
	        int index = start;// 从初始点开始  
	        int presentShortest = 0;//当前临时最短距离  
	 
	        indexs[++i_count] = index;// 把已经标号的下标存入下标集中  
	        isLabel[index] = true;  
	          
	        while (i_count<W1[0].length) {  
	            // 第一步：标号v0,即w[0][0]找到距离v0最近的点  
	 
	            int min = Integer.MAX_VALUE;  
	            for (int i = 0; i < distance.length; i++) {  
	                if (!isLabel[i] && distance[i] != -1 && i != index) {  
	                    // 如果到这个点有边,并且没有被标号  
	                    if (distance[i] < min) {  
	                        min = distance[i];  
	                        index = i;// 把下标改为当前下标  
	                    }  
	                }  
	            }  
	            if (index == end) {//已经找到当前点了，就结束程序  
	                break;  
	            }  
	            isLabel[index] = true;//对点进行标号  
	            indexs[++i_count] = index;// 把已经标号的下标存入下标集中  
	            if (W1[indexs[i_count - 1]][index] == -1 
	                    || presentShortest + W1[indexs[i_count - 1]][index] > distance[index]) {  
	                // 如果两个点没有直接相连，或者两个点的路径大于最短路径  
	                presentShortest = distance[index];  
	            } else {  
	                presentShortest += W1[indexs[i_count - 1]][index];  
	            }  
	 
	            // 第二步：将distance中的距离加入vi  
	            for (int i = 0; i < distance.length; i++) {  
	                // 如果vi到那个点有边，则v0到后面点的距离加  
	                if (distance[i] == -1 && W1[index][i] != -1) {// 如果以前不可达，则现在可达了  
	                    distance[i] = presentShortest + W1[index][i];  
	                } else if (W1[index][i] != -1 
	                        && presentShortest + W1[index][i] < distance[i]) {  
	                    // 如果以前可达，但现在的路径比以前更短，则更换成更短的路径  
	                    distance[i] = presentShortest + W1[index][i];  
	                }  
	 
	            }  
	        }  
	        //如果全部点都遍历完，则distance中存储的是开始点到各个点的最短路径  
	       dis= distance[end] - distance[start]; 
        return dis;
	}
	public ArrayList<Double> getCorScoreList() {
		return corScoreList;
	}
	public void setCorScoreList(ArrayList<Double> corScoreList) {
		this.corScoreList = corScoreList;
	}
	
}

