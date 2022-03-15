package SmallSize;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;

public class FregraphFeature {
      public String filePath="";
      public int trainSelfFalNum=0;
      public int trainOtherFalNum=0;
      public int testNum=0;
      
      public String type="";
      public CommunitySubGraph subGraph;
      public double weight=0.0D;
      
      public FregraphFeature(){
    	  
      }
      public FregraphFeature(String filePath){
    	  subGraph=new CommunitySubGraph(filePath);
    	  this.filePath=filePath;
    	  if(filePath.contains("train")){
    		  this.type="train";
    	  }
    	  else {
    		  this.type="test";
    	  }
      }
      public FregraphFeature(String filePath, double weight){
    	  subGraph=new CommunitySubGraph(filePath);
    	  this.filePath=filePath;
    	  if(filePath.contains("train")){
    		  this.type="train";
    	  }
    	  else {
    		  this.type="test";
    	  }
    	  this.weight=weight;
      }
      public boolean isSimilar(FregraphFeature feature, FamilyWeightScore weightScore){
    	  boolean result =false;
    	  SubGraphSimilarity similarity=new SubGraphSimilarity(this.subGraph.getGraph(), 
    			  feature.subGraph.getGraph(), weightScore);
    	  if(similarity.getSimScore()>=ConstantValue.getVar().minScoreSim){
    		  result=true;
    	  }
    	  return result;
      }
      
}
