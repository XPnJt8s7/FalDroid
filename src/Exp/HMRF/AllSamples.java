package Exp.HMRF;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AllSamples {
			private Map<String, APKObject> allInstancesMap=new HashMap<>();
			private TestSimGraph allSimgraph=new TestSimGraph();
			public AllSamples(){
			}
			public AllSamples(KnownSamples knownSamples, TestSamples testSamples){
				Iterator<String> knownIterator=knownSamples.getKnowInstancesMap().keySet().iterator();
				while(knownIterator.hasNext()){
					String apkName=knownIterator.next();
					addOneSample(knownSamples.getKnowInstancesMap().get(apkName));
				}
				Iterator<String> testIterator=testSamples.getTestInstancesMap().keySet().iterator();
				while(testIterator.hasNext()){
					String apkName=testIterator.next();
					addOneSample(testSamples.getTestInstancesMap().get(apkName));
				}
			}
			public void addOneSample(APKObject apkObject){
				String apkName=apkObject.getApkName();
				this.allInstancesMap.put(apkName, apkObject);
			}
			public void iniNeighbor(){
				Iterator<String> intancesItetrator=allInstancesMap.keySet().iterator();
				while(intancesItetrator.hasNext()){
					String apkName=intancesItetrator.next();
					Iterator<String> dstInstanceIterator=allInstancesMap.keySet().iterator();
					while(dstInstanceIterator.hasNext()){
						String dstAPKName=dstInstanceIterator.next();
						if(!apkName.equals(dstAPKName)){
							allInstancesMap.get(apkName).addOneNeighbor(allInstancesMap.get(dstAPKName));
		
						}
					}
					
				}
			}
			public void transSimGraph(){
				Iterator<String> allIterator=allInstancesMap.keySet().iterator();
				while(allIterator.hasNext()){
					String apkName=allIterator.next();
					allSimgraph.addNode(allInstancesMap.get(apkName));
					Map<String, Double> neighborMap=new HashMap<>();
					neighborMap=allInstancesMap.get(apkName).getNeighborAPKMap();
					Iterator<String> neighborIterator=neighborMap.keySet().iterator();
					while(neighborIterator.hasNext()){
						String dstAPKName=neighborIterator.next();
						double weight=neighborMap.get(dstAPKName);
						APKEdge edge=new APKEdge();
						edge.setSrcNode(allInstancesMap.get(apkName));
						edge.setDstNode(allInstancesMap.get(dstAPKName));
						edge.setWeight(weight);
						allSimgraph.addEdge(edge);
					}
				}
				
			}
			public Map<String, APKObject> getAllInstancesMap() {
				return allInstancesMap;
			}
			public void setAllInstancesMap(Map<String, APKObject> allInstancesMap) {
				this.allInstancesMap = allInstancesMap;
			}
			public TestSimGraph getAllSimgraph() {
				return allSimgraph;
			}
			public void setAllSimgraph(TestSimGraph allSimgraph) {
				this.allSimgraph = allSimgraph;
			}
}
