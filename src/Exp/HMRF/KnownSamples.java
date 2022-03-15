package Exp.HMRF;

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

public class KnownSamples {
		private Map<String, Set<APKObject>> knownSampleMap=new HashMap<>();    //  键为家族名，值为家族内部所有对象
		private Map<String, APKObject> knowInstancesMap=new HashMap<>(); // 键为apk名，值为对象
		private Map<String, ArrayList<Double>> knownFalAvgSimilarityMap=new HashMap<>();   //存储每一个家族的内部样本的平均相似度
		private Map<String, ArrayList<Double>> knownFalMaxSimilarityMap=new HashMap<>();  //存储每一个家族的内部样本的最大相似度
		private TestSimGraph trainSimgraph=new TestSimGraph();   //训练集样本的相似性图模型
		public KnownSamples(String trainFilePath){
			try {
				File trainFile=new File(trainFilePath);
				FileReader fReader=new FileReader(trainFile);
				BufferedReader bufferedReader=new BufferedReader(fReader);
				String lineString="";
				while((lineString=bufferedReader.readLine())!=null){
					APKObject apkObject=new APKObject(lineString);
					apkObject.setNodeType("Train");
					addOneSample(apkObject);
				}
				bufferedReader.close();
				fReader.close();
				
				iniNeighbor();
				transSimGraph();
				calculateFalSimilarity();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public void addOneSample(APKObject apkObject){
			String falName=apkObject.getActualFalName();
			String apkName=apkObject.getApkName();
			this.knowInstancesMap.put(apkName, apkObject);
			if(knownSampleMap.containsKey(falName)){
				knownSampleMap.get(falName).add(apkObject);
			}
			else{
				Set<APKObject> apkSet=new HashSet<>();
				apkSet.add(apkObject);
				knownSampleMap.put(falName, apkSet);
			}
		}
		/*
		 *    初始化训练集的邻居节点
		 */
		public void iniNeighbor(){
			Iterator<String> knownIterator=this.knowInstancesMap.keySet().iterator();
			while(knownIterator.hasNext()){
				String apkName=knownIterator.next();
				Iterator<String> dstIterator=this.knowInstancesMap.keySet().iterator();
				while(dstIterator.hasNext()){
					String dstAPKName=dstIterator.next();
					if(!apkName.equals(dstAPKName)){
						knowInstancesMap.get(apkName).addOneNeighbor(knowInstancesMap.get(dstAPKName));
					}
				}
				//   计算每一个样本与它相邻样本的一致性,如果标签不一致的节点相邻
				//   则置该样本的consistency变量为false
				Map<String, Double> neighborMap=new HashMap<>();
				neighborMap=knowInstancesMap.get(apkName).getNeighborAPKMap();
				if(neighborMap.size()==0){
					knowInstancesMap.get(apkName).setConsistency(true);
				}
				else{
					Iterator<String> neighborIterator=neighborMap.keySet().iterator();
					while(neighborIterator.hasNext()){
						String neighborName=neighborIterator.next();
						if(knowInstancesMap.get(apkName).getActualFalName()
								.equals(knowInstancesMap.get(neighborName).getActualFalName())){
							knowInstancesMap.get(apkName).setConsistency(true);
						}
						else{
							knowInstancesMap.get(apkName).setConsistency(false);
							break;
						}
					}
				}
			}
		}
		
		/*
		 * 			转换成训练集的相似图
		 * 
		 */
		public void transSimGraph(){
			Iterator<String> trainIterator=knowInstancesMap.keySet().iterator();
			while(trainIterator.hasNext()){
				String apkName=trainIterator.next();
				trainSimgraph.addNode(knowInstancesMap.get(apkName));
				Map<String, Double> neighborMap=new HashMap<>();
				neighborMap=knowInstancesMap.get(apkName).getNeighborAPKMap();
				Iterator<String> neighborIterator=neighborMap.keySet().iterator();
				while(neighborIterator.hasNext()){
					String dstAPKName=neighborIterator.next();
					double weight=neighborMap.get(dstAPKName);
					APKEdge edge=new APKEdge();
					edge.setSrcNode(knowInstancesMap.get(apkName));
					edge.setDstNode(knowInstancesMap.get(dstAPKName));
					edge.setWeight(weight);
					trainSimgraph.addEdge(edge);
				}
			}
		}
		public void calculateFalSimilarity(){
			Iterator<String> falIterator=this.knownSampleMap.keySet().iterator();
			while(falIterator.hasNext()){
				String falName=falIterator.next();
				Set<APKObject> falSet=new HashSet<>();
				falSet=this.knownSampleMap.get(falName);
				ArrayList<Double> falAvgSimList=new ArrayList<>();
				ArrayList<Double> falMaxSimList=new ArrayList<>();
				Iterator<APKObject> srcIterator=falSet.iterator();
				while(srcIterator.hasNext()){
					String srcAPKName=srcIterator.next().getApkName();
					double sim=0.0D;
					double maxSim=0.0D;
					Iterator<APKObject> dstIterator=falSet.iterator();
					while(dstIterator.hasNext()){
						String dstAPKName=dstIterator.next().getApkName();
						if(!srcAPKName.equals(dstAPKName)){
							double tmpSim=this.knowInstancesMap.get(srcAPKName).
									calculateSimilarity(this.knowInstancesMap.get(dstAPKName));
							sim += tmpSim;
							if(tmpSim>maxSim){
								maxSim=tmpSim;
							}
						}
					}
					sim = sim/ Double.valueOf(falSet.size()-1);
					falAvgSimList.add(sim);
					falMaxSimList.add(maxSim);
				}
				knownFalAvgSimilarityMap.put(falName, falAvgSimList);
				knownFalMaxSimilarityMap.put(falName, falMaxSimList);
			}
		}
		public void writeFalAvgSimilarity(String falSimFilePath){
			try {
				File file=new File(falSimFilePath);
				FileWriter fWriter=new FileWriter(file);
				BufferedWriter bWriter=new BufferedWriter(fWriter);
				String lineString="";
				Iterator<String> falIterator=this.knownFalAvgSimilarityMap.keySet().iterator();
				while(falIterator.hasNext()){
					String falName=falIterator.next();
					lineString =falName;
					ArrayList<Double> simList=new ArrayList<>();
					simList=this.knownFalAvgSimilarityMap.get(falName);
					for(int i=0;i<simList.size();i++){
						lineString +=","+simList.get(i);
					}
					bWriter.write(lineString+"\n");
				}
				bWriter.close();
				fWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public void writeFalMaxSimilarity(String falSimFilePath){
			try {
				File file=new File(falSimFilePath);
				FileWriter fWriter=new FileWriter(file);
				BufferedWriter bWriter=new BufferedWriter(fWriter);
				String lineString="";
				Iterator<String> falIterator=this.knownFalMaxSimilarityMap.keySet().iterator();
				while(falIterator.hasNext()){
					String falName=falIterator.next();
					lineString =falName;
					ArrayList<Double> simList=new ArrayList<>();
					simList=this.knownFalMaxSimilarityMap.get(falName);
					for(int i=0;i<simList.size();i++){
						lineString +=","+simList.get(i);
					}
					bWriter.write(lineString+"\n");
				}
				bWriter.close();
				fWriter.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public void showData(){
			Iterator<String> falIterator=knownSampleMap.keySet().iterator();
			while(falIterator.hasNext()){
				String falName=falIterator.next();
				int size=knownSampleMap.get(falName).size();
				System.out.println(falName+":"+size);
			}
		}
		public Map<String, Set<APKObject>> getKnownSampleMap() {
			return knownSampleMap;
		}
		public void setKnownSampleMap(Map<String, Set<APKObject>> knownSampleMap) {
			this.knownSampleMap = knownSampleMap;
		}
		public Map<String, APKObject> getKnowInstancesMap() {
			return knowInstancesMap;
		}
		public void setKnowInstancesMap(Map<String, APKObject> knowInstancesMap) {
			this.knowInstancesMap = knowInstancesMap;
		}
		public TestSimGraph getTrainSimgraph() {
			return trainSimgraph;
		}
		public void setTrainSimgraph(TestSimGraph trainSimgraph) {
			this.trainSimgraph = trainSimgraph;
		}
		
}
