package SmallSize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;

public class OldNewFeature {

	public static String oldFamilyDir="/home/fan/data/Family/train-final/";
	public static String newFamilyDir="/home/fan/data/Family/small-sample-exp/exp1/train/";
	public static String resultFilePath="/home/fan/data/Family/small-sample-exp/exp1/result/oldnewFeature/featureSim.txt";
	public static String supportValue="";
	public static String line="";
	
	public static void main(String[] args) {
		
		ArrayList<String> supportList=new ArrayList<>();
		supportList.add("Im--0.1");
//		supportList.add("Im--0.2");
//		supportList.add("Im--0.3");
//		supportList.add("Im--0.4");
//		supportList.add("Im--0.5");
//		supportList.add("Im--0.6");
//		supportList.add("Im--0.7");
//		supportList.add("Im--0.8");
//		supportList.add("Im--0.9");
		
		File oldDir=new File(oldFamilyDir);
		File newDir=new File(newFamilyDir);
		
        File oldFals[]=oldDir.listFiles();
        File newFals[]=newDir.listFiles();
        // the length of oldFals is the same as the length of newFals
        
        
        for(int k=0;k<supportList.size();k++){
        	supportValue=supportList.get(k);
        	line +=supportValue+"\n";
        	System.out.println(supportValue);
	        for(int i=0;i<oldFals.length;i++){
	        	
	        	String oldFamilyName=oldFals[i].getName();
	        	String oldFalFeatureFile=oldFals[i].getAbsolutePath()+"/FamilyInfo/"+
	        			supportValue+"/Cluster/";
	        	
	        	String newFamilyName=newFals[i].getName();
	        	String newFalFeatureFile=newFals[i].getAbsolutePath()+"/FamilyInfo/"+
	        			supportValue+"/Cluster/";
	        	
	        	String weightScoreFilePath=oldFals[i].getAbsolutePath()+"/FamilyInfo/"+
	        			"MethodWeight.txt";
	        	
	        	line +="Family Name: "+oldFamilyName+"\n";
	        	System.out.println("Family Name: "+oldFamilyName);
	        	if(oldFamilyName.equals("imlog")){
	        		oneFalFeatureSim(oldFalFeatureFile, newFalFeatureFile, weightScoreFilePath);
	        	}
	        }
        }
        
      //  writeResult(line, resultFilePath);
		
	}
	public static void oneFalFeatureSim(String srcFalFilePath, String dstFalFilePath, String weightScoreFilePath){
		File srcFal=new File(srcFalFilePath);
		File srcSubragphs[]=srcFal.listFiles();
		File dstFal=new File(dstFalFilePath);
		File dstSubgraphs[]=dstFal.listFiles();
		FamilyWeightScore weightScore=new FamilyWeightScore(weightScoreFilePath);
		
		
		line +="	Old Fal Subgraphs Size: "+srcSubragphs.length+"\n";
		line +=" 	New Fal Subgraphs Size: "+dstSubgraphs.length+"\n";
		System.out.println("	Old Fal Subgraphs Size: "+srcSubragphs.length);
		System.out.println(" 	New Fal Subgraphs Size: "+dstSubgraphs.length);
		int simNum=0;
		
		
		for(int i=0;i<srcSubragphs.length;i++){
			String srcFilePath=srcSubragphs[i].getAbsolutePath();
			CommunitySubGraph srcCommunitySubGraph=new CommunitySubGraph(srcFilePath);
			for(int j=0;j<dstSubgraphs.length;j++){
				String dstFilePath=dstSubgraphs[j].getAbsolutePath();
				CommunitySubGraph dstCommunitySubGraph=new CommunitySubGraph(dstFilePath);
				
				SubGraphSimilarity similarity=new SubGraphSimilarity(srcCommunitySubGraph.getGraph(),
						dstCommunitySubGraph.getGraph(), weightScore);
				double simDouble=similarity.getSimScore();
				if(simDouble>=ConstantValue.getVar().minScoreSim){
					simNum++;
					System.out.println(srcFilePath+" -----  "+dstFilePath+":   "+simDouble);
				}
			}
		}
		line +="	Same Subgraphs Size: "+simNum+"\n";
		System.out.println("	Same Subgraphs Size: "+simNum);
	}
	public static void writeResult(String line, String writeFilePath){
		try {
			File file=new File(writeFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			bWriter.write(line);
			bWriter.close();
			fWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
