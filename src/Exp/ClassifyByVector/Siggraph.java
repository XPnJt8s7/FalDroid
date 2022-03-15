package Exp.ClassifyByVector;

import java.io.Serializable;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;

public class Siggraph implements Serializable{
		private CommunitySubGraph subGraph;
		private String filePath="";
		private String falName="";
		private String falWeightFilePath="";
		private int support=0;
		private int falSize=0;
		public Siggraph(){
		}
		public Siggraph(String filePath){
			subGraph=new CommunitySubGraph(filePath);
			this.filePath=filePath;
			int k=filePath.indexOf("/FamilyInfo");
			String falPath=filePath.substring(0,k);
			this.falWeightFilePath=falPath+"/FamilyInfo/MethodWeight.txt";
			k=falPath.lastIndexOf("/");
			this.falName=falPath.substring(k+1);
		}
		public boolean simWithSiggraph(Siggraph dstGraph){
			boolean sim=false;
			String srcFamilyWeightScroreFilePath=this.falWeightFilePath;
			String dstFamilyWeightScoreFilePath=dstGraph.falWeightFilePath;
			FamilyWeightScore srcWeightScore=new FamilyWeightScore(srcFamilyWeightScroreFilePath);
			FamilyWeightScore dstWeightScore=new FamilyWeightScore(dstFamilyWeightScoreFilePath);
			
			SubGraphSimilarity srcSimilarity=new SubGraphSimilarity(this.subGraph.getGraph(), dstGraph.getSubGraph().getGraph()
					, srcWeightScore);
			double srcSim=srcSimilarity.getSimScore();
			SubGraphSimilarity dstSimilarity=new SubGraphSimilarity(dstGraph.getSubGraph().getGraph(), this.subGraph.getGraph(),
					dstWeightScore);
			double dstSim=dstSimilarity.getSimScore();
			double minScore=ConstantValue.getVar().minScoreSim;
			if(srcSim>=minScore && dstSim>=minScore){
				sim=true;
			}
			return sim;
		}
		public CommunitySubGraph getSubGraph() {
			return subGraph;
		}
		public void setSubGraph(CommunitySubGraph subGraph) {
			this.subGraph = subGraph;
		}
		public String getFilePath() {
			return filePath;
		}
		public void setFilePath(String filePath) {
			this.filePath = filePath;
		}
		public String getFalName() {
			return falName;
		}
		public void setFalName(String falName) {
			this.falName = falName;
		}
		public int getSupport() {
			return support;
		}
		public void setSupport(int support) {
			this.support = support;
		}
		public int getFalSize() {
			return falSize;
		}
		public void setFalSize(int falSize) {
			this.falSize = falSize;
		}
		
}
