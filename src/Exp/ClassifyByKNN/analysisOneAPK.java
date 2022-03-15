package Exp.ClassifyByKNN;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import APKData.SmaliGraph.SICG;
import CandicateFamily.FamilyWeightScore;
import CommunityStructure.CommunityDetection.CommunityFinding;
import CommunityStructure.CommunityDetection.OneObjectCommunityDetection;
import CommunityStructure.GraphData.GenerateCommunitySubgraph;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;
import SubgraphsInDifferentFamily.sigGraphAPK;

public class analysisOneAPK {
			private String filePath;
			private String actualFalName;
			private String predictFalName;
			private String fileName;
			private String outFilePath;
			//  元素为该APK的所有子图
			private ArrayList<CommunitySubGraph> subgraphList=new ArrayList<>();
			/*
			 *    元素为该APK的所有显著性子图
			 */
			private ArrayList<CommunitySubGraph> simSigGraphList=new ArrayList<>();
			
			public analysisOneAPK(){
			}
			public analysisOneAPK(String apkFilePath){
				 this.filePath=apkFilePath;
				 File apkFile=new File(apkFilePath);
				 String apkName=apkFile.getName();
				 this.fileName=apkName;
			}
			 /*
			  *     反编译apk文件并且生成相应的分析结果
			  */
			public void disassemble(String apkFilePath){
				 int k=apkFilePath.lastIndexOf("/");
				 String outFileString=apkFilePath.substring(0,k+1);
				 File outFileDir=new File(outFileString);
				 this.actualFalName=outFileDir.getName();
				 outFileString +="apktool/out-"+fileName+"/";
				 SICG sicg=new SICG(apkFilePath, outFileString);
				 this.outFilePath=outFileString;
			}
		    /*
		     *     进行社团划分，需要输入解析结果以及家族分值列表文件作为参数
		     */
			public void communityDivide(){
				/*
				 *   采用社团划分算法将原图划分成为若干个子图
				 */
				CommunityFinding communityFinding=new CommunityFinding(outFilePath);
				 System.out.println("\n Finish Community Detection");
			}
			/*
			 *    生成每一个算法的生成的子图集合，需要一个分值列表文件作为参数
			 */
			public void createCommunityFiles(String weightScoreFilePath){
				GenerateCommunitySubgraph generateCommunitySubgraph=new GenerateCommunitySubgraph(outFilePath, weightScoreFilePath);
//				System.out.println("Finsh CommunityFile Creation");
			}
			public void reiniGraphList(){
				this.subgraphList=new ArrayList<>();
				this.simSigGraphList=new ArrayList<>();
			}
			 /*
			  *     存储该样本的所有生成子图
			  */
			public void storeAllSubgraphs(String communityType){
				 String graphFilePath=this.outFilePath+"SICG/Community/Community_"+communityType+"/";
				 File graphFile=new File(graphFilePath);
				 File subgraphFile[]=graphFile.listFiles();
				 for(int i=0;i<subgraphFile.length;i++){
					 CommunitySubGraph subGraph=new CommunitySubGraph(subgraphFile[i].getAbsolutePath());
					 double score=subGraph.getSensitiveScore();
					 if(score>ConstantValue.getVar().minTotalGraphScore){
						 subgraphList.add(subGraph);
					 }
				 }
			}
			/*
			 *    传递一个家族的显著性子图信息，以及相应的分值列表文件路径
			 *    计算该样本与指定家族的显著性子图的匹配结果
			 */
			public void calSimSiggraph(FamilyClusterInfo familyClusterInfo, String scoreFilePath){
				FamilyWeightScore weightScore=new FamilyWeightScore(scoreFilePath);
				for(int i=0;i<subgraphList.size();i++){
					CommunitySubGraph srcGraph=subgraphList.get(i);
					Iterator<CommunitySubGraph> iterator=familyClusterInfo.getSigGraphMap().keySet().iterator();
					while(iterator.hasNext()){
						CommunitySubGraph sigGraph=iterator.next();
						SubGraphSimilarity similarity=new SubGraphSimilarity(srcGraph.getGraph(), sigGraph.getGraph(), weightScore);
						double score=similarity.getSimScore();
						if(score>ConstantValue.getVar().minScoreSim){
							this.simSigGraphList.add(sigGraph);
						}
					}
				}
			}
			public double simWithFalAPKs(FamilyClusterInfo familyClusterInfo){
//				double sigScore=0.0D;
//				int num=familyClusterInfo.getSampleNum();
//				Map<CommunitySubGraph, Integer> sigGraphMap=new HashMap<>();
//				sigGraphMap=familyClusterInfo.getSigGraphMap();
//				for(int i=0;i<simSigGraphList.size();i++){
//					CommunitySubGraph subGraph=simSigGraphList.get(i);
//					int support=sigGraphMap.get(subGraph);
//					double tmp=subGraph.getSensitiveScore()*Double.valueOf(support)/Double.valueOf(num);
//					sigScore +=tmp;
//				}
//				return sigScore;
				
				 int size=familyClusterInfo.getACMap().size();
				 double maxSimScore=0.0D;
				 String maxSimFilePath="";
				 double totalSimScore=0.0D;
				 double avgSimScore=0.0D;
				 Map<String, Double> simScoreMap=new HashMap<>();
				 Iterator<String> apkIterator=familyClusterInfo.getACMap().keySet().iterator();
				 while(apkIterator.hasNext()){
					 String apkFilePath=apkIterator.next();
					 sigGraphAPK sigAPK=new sigGraphAPK();
					 sigAPK=familyClusterInfo.getACMap().get(apkFilePath);
					 double simScore=simWithSigGraphAPK(familyClusterInfo, sigAPK);
					 simScoreMap.put(apkFilePath, simScore);
					 totalSimScore += simScore;
					 if(simScore>maxSimScore){
						 maxSimScore=simScore;
						 maxSimFilePath=apkFilePath;
					 }
				 }
				 double effectiveScore=0.0D;
				 for(int i=0;i<simSigGraphList.size();i++){
					 CommunitySubGraph subGraph=simSigGraphList.get(i);
					 int sur=familyClusterInfo.getSigGraphMap().get(subGraph);
					 effectiveScore += subGraph.getSensitiveScore()*Double.valueOf(sur)/Double.valueOf(familyClusterInfo.getSampleNum());
					 
				 }
		//		 avgSimScore=totalSimScore/Double.valueOf(familyClusterInfo.getSampleNum());
		//		 System.out.println("最大相似样本："+maxSimFilePath);
				 return maxSimScore*effectiveScore;
			}
			//  计算本待检测样本与一个sigGraphAPK中所调用的显著性子图的相似性匹配情况
			public double simWithSigGraphAPK(FamilyClusterInfo familyClusterInfo, sigGraphAPK sigAPK){
				double simScore=0.0D;
				Map<CommunitySubGraph, Integer> sigGraphMap=new HashMap<>();
				sigGraphMap=familyClusterInfo.getSigGraphMap();
				
				
				// 计算两样本之间显著子图的交集
				Set<CommunitySubGraph> andSigGraphSet=new HashSet<>();
				for(int i=0;i<simSigGraphList.size();i++){
					for(int j=0;j<sigAPK.getInvokeSigGraphList().size();j++){
						if(simSigGraphList.get(i).equals(sigAPK.getInvokeSigGraphList().get(j))){
							andSigGraphSet.add(simSigGraphList.get(i));
						}
					}
				}
				//计算两样本之间显著子图的并集
				Set<CommunitySubGraph> orSigGraphSet=new HashSet<>();
				for(int i=0;i<simSigGraphList.size();i++){
					orSigGraphSet.add(simSigGraphList.get(i));
				}
				for(int i=0;i<sigAPK.getInvokeSigGraphList().size();i++){
					orSigGraphSet.add(sigAPK.getInvokeSigGraphList().get(i));
				}
				
				// 每一个显著性子图计算有效分值为： score*support/totalNum  
				// 两个样本的相似性计算公式为：  2|A交B|/|A|+|B| 
				int num=familyClusterInfo.getSampleNum();
				double fenzi=0.0D;
				Iterator<CommunitySubGraph> andIterator=andSigGraphSet.iterator();
				while(andIterator.hasNext()){
					CommunitySubGraph com=andIterator.next();
					int support=sigGraphMap.get(com);
					double tmp=com.getSensitiveScore()*Double.valueOf(support)/Double.valueOf(num);
					fenzi +=tmp;
				}
				fenzi = fenzi *2;
				double fenmu=0.0D;
				for(int i=0;i<simSigGraphList.size();i++){
					CommunitySubGraph subGraph=simSigGraphList.get(i);
					int support=sigGraphMap.get(subGraph);
					double tmp=subGraph.getSensitiveScore()*Double.valueOf(support)/Double.valueOf(num);
					fenmu +=tmp;
				}
				for(int i=0;i<sigAPK.getInvokeSigGraphList().size();i++){
					CommunitySubGraph subGraph=new CommunitySubGraph();
					subGraph=sigAPK.getInvokeSigGraphList().get(i);
						int support=sigGraphMap.get(subGraph);
						double tmp=subGraph.getSensitiveScore()*Double.valueOf(support)/Double.valueOf(num);
						fenmu += tmp;
				}
				simScore=fenzi/fenmu;
				return simScore;
			}
			public String showSimSigGraphMap(String falName){
				String resultString="SigGraph: "+falName+"\n";
				for(int i=0;i<simSigGraphList.size();i++){
					resultString +=simSigGraphList.get(i).getFilePath()+"\n";
				}
//				System.out.println(resultString);
				return resultString;
			}
}
