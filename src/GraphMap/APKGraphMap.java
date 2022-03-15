package GraphMap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import APKData.SmaliGraph.Color;
import APKData.SmaliGraph.GexfToGraph;
import APKData.SmaliGraph.MethodEdge;
import APKData.SmaliGraph.MethodGraph;
import APKData.SmaliGraph.MethodNode;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;


public class APKGraphMap {
	public String falName="";
	public String apkFilePath="";
	public String sup="";
	public ArrayList<String> sgFilePathList=new ArrayList<>();
	public Set<MethodNode> pNodeSet=new HashSet();
	public Map<Integer, PackName> packNameMap=new HashMap<>();
	public String packNameString="";
	public APKGraphMap(){
		
	}
	public APKGraphMap(String apkFilePath, String sup){
		ini(apkFilePath, sup);
		mapGraph();
		calPackNameMap();
	}
	public void calPackNameMap(){
		AnalyzePackName analyzePackName=new AnalyzePackName(pNodeSet);
		this.packNameMap=analyzePackName.packNameMap;
		this.packNameString=analyzePackName.resultLine;
	}
	public void ini(String apkFilePath, String sup){
		this.sup=sup;
		this.apkFilePath=apkFilePath;	
		Pattern pattern=Pattern.compile("(.*?)Family/(.*?)/(.*?)/apktool/(.*)");
		Matcher matcher=pattern.matcher(apkFilePath);
		if(matcher.find()){
			falName=matcher.group(3);
		}
		String logFilePath="/home/fan/data/Family/train-final/"+falName+"/FamilyInfo/Im--"+sup+"/log.txt";
		try {
			File logFile=new java.io.File(logFilePath);
			FileReader fReader=new FileReader(logFile);
			BufferedReader bReader=new BufferedReader(fReader);
			String line="";
			while((line=bReader.readLine())!=null){
				line=line.trim();
				if(line.contains(apkFilePath)){
					int k=line.indexOf(" ");
					String sgFileName=line.substring(k+1);
					String sgFilePath=apkFilePath+"SICG/Community/Community_Im/"+sgFileName;
					sgFilePathList.add(sgFilePath);
				}
			}
			bReader.close();
			fReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void showSgFilePath(){
		System.out.println(this.apkFilePath+":");
		for(int i=0;i<sgFilePathList.size();i++){
			System.out.println("  "+sgFilePathList.get(i));
		}
	}
	public void mapGraph(){
		Set<MethodNode> nodeSet=new HashSet<>();
		Set<MethodEdge> edgeSet=new HashSet<>();
		for(int i=0;i<sgFilePathList.size();i++){
			String sgFilePath=sgFilePathList.get(i);
			CommunitySubGraph cSubGraph=new CommunitySubGraph(sgFilePath);
			Set<MethodNode> sgNodeSet=new HashSet<>();
			sgNodeSet=cSubGraph.getGraph().getNodeSet();
			Set<MethodEdge> sgEdgeSet=new HashSet<>();
			sgEdgeSet=cSubGraph.getGraph().getEdgeSet();
			nodeSet.addAll(sgNodeSet);
			edgeSet.addAll(sgEdgeSet);
		}
//		System.out.println(nodeSet.size()+"#"+edgeSet.size());
		
		GexfToGraph toGraph=new GexfToGraph(apkFilePath+"SICG/ReducedGraph.gexf");
		Set<MethodNode> allNodeSet=new HashSet<>();
		allNodeSet=toGraph.getGraph().getNodeSet();
		Set<MethodEdge> allEdgeSet=new HashSet<>();
		allEdgeSet=toGraph.getGraph().getEdgeSet();
		
		try {
			File dir=new File(apkFilePath+"SICG/MapGraph/");
			if(!dir.exists()){
			    dir.mkdir();
			}
			File file=new File(apkFilePath+"SICG/MapGraph/Map--"+sup+".gexf");
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			String lineString="";
			lineString +="<?xml version='1.0' encoding='UTF-8'?>\n"+
							"<gexf xmlns='qianniao918@qq.com' version='1.2' xmlns:viz='http://www.gexf.net/1.2draft/viz'>"+
							" xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"+ 
							" xsi:schemaLocation='http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd'>\n"+
							"	<meta lastmodifieddate='2014-9-21'>\n"+
							"		<creator>FanMing</creator>\n"+
							"		<description>An Android APK Method SCN!</description>\n"+
							"	</meta>\n"+
							"	<graph mode='static' defaultedgettype='directed'>\n"+
							"		<nodes>\n";
			bWriter.write(lineString);
			/*
			 *   写入节点信息
			 */
			Iterator<MethodNode> nodeIterator=allNodeSet.iterator();
			int index=0;
			while(nodeIterator.hasNext()){
				MethodNode node=new MethodNode();
				node=nodeIterator.next();
				String nodeCommonString=node.getCommonString();
				String r="30"; String g="144"; String b="255"; // 默认颜色为湖蓝色
				if(nodeSet.contains(node)){
					r="255"; g="255"; b="0";
					index ++;
				}
				String tmpLine="		<node id='"+nodeCommonString+"' label='"+nodeCommonString+"'>\n"+
										   "			<attvalues></attvalues>\n"+
										   "			<viz:color r='"+r+"' g='"+g+"' b='"+b+"'></viz:color>\n"+
										   "			</node>\n";
				lineString =tmpLine;
				bWriter.write(lineString);
			}
			System.out.println("Index:"+index);
		lineString ="		</nodes>\n";
		lineString +="		<edges>\n";
		bWriter.write(lineString);
		/*
		 *   写入边信息
		 */
		Iterator<MethodEdge> edgeIterator=allEdgeSet.iterator();
		int id=0;
		while(edgeIterator.hasNext()){
			MethodEdge edge=new MethodEdge();
			edge=edgeIterator.next();
			String srcCommonString=edge.getCallerNode().getCommonString();
			String dstCommonString=edge.getCalleeNode().getCommonString();
			String tmpLine="			<edge id='"+id+"' source='"+srcCommonString+"' target='"+dstCommonString+"'/>\n";
			lineString =tmpLine;
			bWriter.write(lineString);
			id++;
		}
			lineString ="		</edges>\n";
			lineString +="	</graph>\n";
			lineString +="</gexf>";
			bWriter.write(lineString);
			
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		MethodGraph graph=new MethodGraph();
		graph=toGraph.getGraph();
		CalRootNode(nodeSet, graph);
	
	}
	public void CalRootNode(Set<MethodNode> fgNodeSet, MethodGraph graph){
		// graph.iniChildrenNodesAndParentNodes();
		 Map<String, MethodNode> nodeMap=new HashMap<>();
		 nodeMap=graph.getNodeMap();
	//	 System.out.println(nodeMap.size());
		// add the fg node which contains no parent node into pNodeSet
		// add the fg node whose parent nodes are no fg nodes into pNodeSet
		Iterator<MethodNode> noParNodeIterator=fgNodeSet.iterator();
		while(noParNodeIterator.hasNext()){
			MethodNode tmpNode=noParNodeIterator.next();
	//		System.out.println(tmpNode.getCommonString());
	        tmpNode=nodeMap.get(tmpNode.getCommonString());
	        
			if(tmpNode.getParentNodes().size()==0){
				pNodeSet.add(tmpNode);
			}
			else{
				Set<MethodNode> ParNodeSet=new HashSet<>();
				ParNodeSet=tmpNode.getParentNodes();
				boolean find=false;
				Iterator<MethodNode> parIterator=ParNodeSet.iterator();
				while(parIterator.hasNext()){
					MethodNode parentNode=parIterator.next();
					if(fgNodeSet.contains(parentNode)){
						find=true;
					}
				}
				if(find==false){
					pNodeSet.add(tmpNode);
				}
			}
		}
		
	}
	public void showPNodeSet(){
		System.out.println("PNodeSet:");
		Iterator<MethodNode> pNodeIterator=pNodeSet.iterator();
		while(pNodeIterator.hasNext()){
			MethodNode node=pNodeIterator.next();
			String nameString=node.getCommonString();
			System.out.println("   "+nameString);
		}
	}
	public void showPackNameMap(){
		System.out.println(this.packNameString);
	}
	public void writeResult(){
		try {
			File file=new File(apkFilePath+"SICG/MapGraph/Pack--"+sup+".txt");
		    FileWriter fWriter=new FileWriter(file);
		    BufferedWriter bWriter=new BufferedWriter(fWriter);
		    bWriter.write(packNameString);
		    bWriter.close();
		    fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
