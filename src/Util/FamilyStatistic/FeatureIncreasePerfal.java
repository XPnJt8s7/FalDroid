package Util.FamilyStatistic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import Exp.ClassifyByVector.Siggraph;
import Exp.ClassifyByVector.SiggraphFeature;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;

public class FeatureIncreasePerfal {

	public static Set<String> falSet=new TreeSet<>();
//	public ArrayList<SiggraphFeature> featureList=new ArrayList<>();
	public static ArrayList<CommunitySubGraph> graphList=new ArrayList<>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			iniFalSet();
			
			Iterator<String> falIterator=falSet.iterator();
			while(falIterator.hasNext()){
				addOneFalFeature(falIterator.next());
			}
	}
	public static void iniFalSet(){
		try {
			Set<String> tmpSet=new HashSet<>();
			tmpSet=ConstantValue.getVar().falNameSet;
			
			Iterator<String> iterator=tmpSet.iterator();
			while(iterator.hasNext()){
				String falName=iterator.next();
				falSet.add(falName);
			}
			
//		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void addOneFalFeature(String falName){
		try {
			System.out.println("Start: "+falName);
			
			ArrayList<CommunitySubGraph> falGraphList=new ArrayList<>();
			File falSigFile=new File(ConstantValue.FAMILIESDIRPATH_STRING+falName+"/FamilyInfo/Im--0.5/Cluster/");
			FamilyWeightScore weightScore=new FamilyWeightScore(ConstantValue.FAMILIESDIRPATH_STRING+falName
					+"/FamilyInfo/MethodWeight.txt");
			File gfile[]=falSigFile.listFiles();
			if(gfile.length>0){
			for(int i=0;i<gfile.length;i++){
				CommunitySubGraph subGraph=new CommunitySubGraph(gfile[i].getAbsolutePath());
				if(graphList.size()>0){
					boolean find=false;
					for(int j=0;j<graphList.size();j++){
						SubGraphSimilarity similarity=new SubGraphSimilarity(subGraph.getGraph(), graphList.get(j).getGraph(), 
								weightScore);
						if(similarity.getSimScore()>0.95){
							find=true;
							break;
						}
					}
					if(find==true){
						
					}
					else{
						graphList.add(subGraph);
					}
				}
				else{
					graphList.add(subGraph);
				}
			}
			}
			
			System.out.println(graphList.size());
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

}
