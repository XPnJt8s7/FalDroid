package GED.MyTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import APKData.SmaliGraph.MethodEdge;
import APKData.SmaliGraph.MethodGraph;
import APKData.SmaliGraph.MethodNode;
import GraphSimilarity.CommunitySubGraph;
import Util.Tool.MapSort;

public class GEDGraphToDot {
	public String GexfFilePath="/home/fan/data/Family/Com_GED/NotSame/";
	public String DotDirPath="/home/fan/data/Family/Com_GED/NotSameDot/";
	public Map<String, Integer> nodeMap=new HashMap<>();
	public List<Map.Entry<String, Integer>> nodeList=new ArrayList<>();
	public ArrayList<String> edgeStringList=new ArrayList<>();
	public GEDGraphToDot(){
		File gexfDir=new File(GexfFilePath);
		File gexfs[]=gexfDir.listFiles();
		for(int i=0;i<gexfs.length;i++){
			String gexfFilePath=gexfs[i].getAbsolutePath();
			transOnegexf(gexfFilePath);
		}
	}
	public void transOnegexf(String srcGexfFilePath){
		CommunitySubGraph community=new CommunitySubGraph(srcGexfFilePath);
		MethodGraph graph=new MethodGraph();
		graph=community.getGraph();
		iniNodeEdge(graph);
		writeFile(srcGexfFilePath);
	}
	public void iniNodeEdge(MethodGraph graph){
		nodeMap=new HashMap<>();
		nodeList=new ArrayList<>();
		edgeStringList=new ArrayList<>();
		
		Set<MethodNode> nodeSet=new HashSet<>();
		Set<MethodEdge> edgeSet=new HashSet<>();
		nodeSet=graph.getNodeSet();
		edgeSet=graph.getEdgeSet();
		Iterator<MethodNode> nodeIterator=nodeSet.iterator();
		int index=0;
		while(nodeIterator.hasNext()){
			MethodNode node=nodeIterator.next();
			String name=node.getCommonString();
			nodeMap.put(name, index);
			index++;
			System.out.println(index+"#"+name);
		}
		MapSort<String, Integer> sort=new MapSort<>();
		nodeList=sort.sortMapByValue(nodeMap);
		Iterator<MethodEdge> edgeIterator=edgeSet.iterator();
		while(edgeIterator.hasNext()){
			MethodEdge edge=edgeIterator.next();
			String srcNodeName=edge.getCallerNode().getCommonString();
			String dstNodeName=edge.getCalleeNode().getCommonString();
			int srcNum=nodeMap.get(srcNodeName);
			int dstNum=nodeMap.get(dstNodeName);
			edgeStringList.add(srcNum+" "+dstNum+" 1");
		}
		
	}
	public void writeFile(String srcGexfFilePath){
		try {
			File srcGexfFile=new File(srcGexfFilePath);
			String fileName=srcGexfFile.getName();
			File dotFileDir=new File(DotDirPath+fileName);
			if(!dotFileDir.exists()){
				dotFileDir.mkdirs();
			}
			String nodeDotFilePath=dotFileDir.getAbsolutePath()+"/node.dot";
			String edgeDotFilePath=dotFileDir.getAbsolutePath()+"/edge.dot";
			// 
	        File nodeFile=new File(nodeDotFilePath);
	        FileWriter nodeWriter=new FileWriter(nodeFile);
	        BufferedWriter bufNodeWriter=new BufferedWriter(nodeWriter);
	        for(int i=0;i<nodeList.size();i++){
	        	String name=nodeList.get(i).getKey();
	        	int num=nodeList.get(i).getValue();
	        	String line=num+"#"+name+"\n";
	        	bufNodeWriter.write(line);
	        }
	        bufNodeWriter.close();
	        nodeWriter.close();
	        //
	        File edgeFile=new File(edgeDotFilePath);
	        FileWriter edgeWriter=new FileWriter(edgeFile);
	        BufferedWriter bufEdgeWriter=new BufferedWriter(edgeWriter);
	        for(int i=0;i<edgeStringList.size();i++){
	        	String line=edgeStringList.get(i);
	        	bufEdgeWriter.write(line+"\n");
	        }
	        bufEdgeWriter.close();
	        edgeWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
