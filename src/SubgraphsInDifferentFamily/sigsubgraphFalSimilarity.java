package SubgraphsInDifferentFamily;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import APKData.SmaliGraph.MethodGraph;
import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;

/*
 *       输入为sigsubgraphInOneFal 类的两个对象，遍历第一个家族的所有显著性子图，计算其
 *       与第二个家族的所有显著性子图的相似性，如果相似性大于阈值，则simGraphNum++
 *       由于在计算相似性的时候需要输入一个家族的分值列表，因此会计算两次，分别将第一个家族
 *       和第二个家族的分值列表进行输入，并将结果存储到log中。
 * 
 */
public class sigsubgraphFalSimilarity {
	private Map<CommunitySubGraph, Integer> 	srcSubgraphMap=new HashMap<>();
	private Map<CommunitySubGraph, Integer>   dstSubgraphMap=new HashMap<>();
	private FamilyWeightScore srcScore;
	private FamilyWeightScore dstScore;
	private String srcFalName;
	private String dstFalName;
	private String log="";
	private int simGraphNum=0;
	public sigsubgraphFalSimilarity(sigsubgraphInOneFal srcFal, sigsubgraphInOneFal dstFal){
		  srcSubgraphMap=srcFal.getSiggraphNumMap();
		  srcScore=new FamilyWeightScore(srcFal.getWeightScoreFile());
		  dstSubgraphMap=dstFal.getSiggraphNumMap();
		  dstScore=new FamilyWeightScore(dstFal.getWeightScoreFile());
		  srcFalName=srcFal.getFamilyName();
		  dstFalName=dstFal.getFamilyName();
		  
		  calculateSim();
	}
	public void calculateSim(){
		Iterator<CommunitySubGraph> srcSubgraphIterator=srcSubgraphMap.keySet().iterator();
		while(srcSubgraphIterator.hasNext()){
			CommunitySubGraph srcGraph=srcSubgraphIterator.next();
			Iterator<CommunitySubGraph> dstSubgraphIterator=dstSubgraphMap.keySet().iterator();
			while(dstSubgraphIterator.hasNext()){
				CommunitySubGraph dstGraph=dstSubgraphIterator.next();
				MethodGraph srcMethodGraph=srcGraph.getGraph();
				MethodGraph dstMethodGraph=dstGraph.getGraph();
				
				SubGraphSimilarity srcSim=new SubGraphSimilarity(srcMethodGraph, dstMethodGraph, srcScore);
				SubGraphSimilarity dstSim=new SubGraphSimilarity(srcMethodGraph, dstMethodGraph, dstScore);
				if(srcSim.getSimScore()>ConstantValue.getVar().minScoreSim
						&& dstSim.getSimScore()>ConstantValue.getVar().minScoreSim){
					log += srcFalName+":"+srcGraph.getFileName()+"---"+dstFalName+":"+dstGraph.getFileName()+" # "+srcSim.getSimScore()+"\n";
					log += dstFalName+":"+dstGraph.getFileName()+"---"+srcFalName+":"+srcGraph.getFileName()+" # "+dstSim.getSimScore()+"\n";
					simGraphNum ++;
				}
			}
		}
		log +="Total Sim Graph:"+dstFalName+"#"+simGraphNum+"\n";
	}
	
	public void writeLogFile(String writeFilePath, String content){
		try {
			File file=new File(writeFilePath);
			if(file.exists()){
				file.delete();
			}
			FileWriter fWriter=new FileWriter(file);            // 已追加的形式写入内容
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			bWriter.write(content);
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public int getSimGraphNum() {
		return simGraphNum;
	}
	public void setSimGraphNum(int simGraphNum) {
		this.simGraphNum = simGraphNum;
	}
	public String getSrcFalName() {
		return srcFalName;
	}
	public void setSrcFalName(String srcFalName) {
		this.srcFalName = srcFalName;
	}
	public String getDstFalName() {
		return dstFalName;
	}
	public void setDstFalName(String dstFalName) {
		this.dstFalName = dstFalName;
	}
	
}
