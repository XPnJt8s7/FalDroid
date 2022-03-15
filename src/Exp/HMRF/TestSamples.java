package Exp.HMRF;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TestSamples {
		private Map<String, APKObject> testInstancesMap=new HashMap<>();
		private TestSimGraph simGraph=new TestSimGraph();
		private int TP=0;
		private ArrayList<String> FPList=new ArrayList<>();
		public TestSamples(){
		}
		public TestSamples(String testFilePath){
				iniData(testFilePath);
		}
		public void iniData(String testFilePath){
			try {
				File file=new File(testFilePath);
				FileReader fReader=new FileReader(file);
				BufferedReader bReader=new BufferedReader(fReader);
				String lineString="";
				while((lineString=bReader.readLine())!=null){
					APKObject apkObject=new APKObject(lineString);
					apkObject.setNodeType("Test");
					String apkName=apkObject.getApkName();
					testInstancesMap.put(apkName, apkObject);
				}
				bReader.close();
				fReader.close();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		/*
		 *     计算每一个待检测样本的初始分配概率
		 */
		public void iniProbability(KnownSamples knownSamples){
			Iterator<String> testAPKIterator=testInstancesMap.keySet().iterator();
			while(testAPKIterator.hasNext()){
				String apkName=testAPKIterator.next();
				testInstancesMap.get(apkName).calculateProbability(knownSamples);
				testInstancesMap.get(apkName).calculateResult();
				if(testInstancesMap.get(apkName).isResult()){
					TP++;
				}
				else{
					APKObject falseObject=testInstancesMap.get(apkName);
					String mostSimAPKName=falseObject.getMostSimilarAPKName(knownSamples);
					FPList.add(falseObject.getApkName()+"--"
							+falseObject.getActualFalName()+"--"+falseObject.getPredictFalNameString()
							+"###"+mostSimAPKName);
				}
			}
		}
		/*
		 * 		计算每一个待检测样本的相邻样本
		 */
		public void iniNeighbor(){
			Iterator<String> testIterator=this.testInstancesMap.keySet().iterator();
			while(testIterator.hasNext()){
				String apkName=testIterator.next();
				Iterator<String> dstIterator=this.testInstancesMap.keySet().iterator();
				while(dstIterator.hasNext()){
					String dstAPKName=dstIterator.next();
					if(!apkName.equals(dstAPKName)){
						testInstancesMap.get(apkName).addOneNeighbor(testInstancesMap.get(dstAPKName));
					}
				}
			}
		}
		/*
		 *      将待检测样本集合转换成相似图模型
		 */
		public void transSimGraph(){
			Iterator<String> testIterator=testInstancesMap.keySet().iterator();
			while(testIterator.hasNext()){
				String apkName=testIterator.next();
				simGraph.addNode(testInstancesMap.get(apkName));
				Map<String, Double> neighborMap=new HashMap<>();
				neighborMap=testInstancesMap.get(apkName).getNeighborAPKMap();
				Iterator<String> neighborIterator=neighborMap.keySet().iterator();
				while(neighborIterator.hasNext()){
					String dstAPKName=neighborIterator.next();
					double weight=neighborMap.get(dstAPKName);
					APKEdge edge=new APKEdge();
					edge.setSrcNode(testInstancesMap.get(apkName));
					edge.setDstNode(testInstancesMap.get(dstAPKName));
					edge.setWeight(weight);
					simGraph.addEdge(edge);
				}
			}
		}
		public Map<String, APKObject> getTestInstancesMap() {
			return testInstancesMap;
		}
		public void setTestInstancesMap(Map<String, APKObject> testInstancesMap) {
			this.testInstancesMap = testInstancesMap;
		}
		public int getTP() {
			return TP;
		}
		public void setTP(int tP) {
			TP = tP;
		}
		public TestSimGraph getSimGraph() {
			return simGraph;
		}
		public void setSimGraph(TestSimGraph simGraph) {
			this.simGraph = simGraph;
		}
		public ArrayList<String> getFPList() {
			return FPList;
		}
		public void setFPList(ArrayList<String> fPList) {
			FPList = fPList;
		}
		public int getAllEdges(){
			int size=0;
			Iterator<String> testInstanceIterator=this.testInstancesMap.keySet().iterator();
			while(testInstanceIterator.hasNext()){
				String apkName=testInstanceIterator.next();
				size += testInstancesMap.get(apkName).getNeighborAPKMap().size();
			}
			size = size/2;
			return size;
		}
}
