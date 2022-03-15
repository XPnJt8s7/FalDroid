package SmallSize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;



import CandicateFamily.FamilyWeightScore;



public class MergeFeatureSpace {
	public static String trainFilePath="/home/fan/data/Family/small-sample-exp/exp1/result/Im--0.4-train.arff";
	public static String testFilePath="/home/fan/data/Family/small-sample-exp/exp2/test/unknown/FamilyInfo/Im--0.5/Cluster/";
	public static String writeFeatureFilePath="/home/fan/data/Family/small-sample-exp/exp2/result/MergeFeatureSpace/Im--0.4.arff";
	
	
	public static ArrayList<FregraphFeature> trainFeatureList=new ArrayList<>();
	public static ArrayList<FregraphFeature> testFeatureList=new ArrayList<>();
	public static ArrayList<FregraphFeature> mergeFeatureList=new ArrayList<>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			iniTrainFeatureList(trainFilePath);
			iniTestFeatureList(testFilePath);
			mergeFeatureList();
			writeFeatureSpace(writeFeatureFilePath);
	}
	public static void iniTrainFeatureList(String trainFilePath){
		try {
			File file=new File(trainFilePath);
			FileReader fReader=new FileReader(file);
			BufferedReader bReader=new BufferedReader(fReader);
		    String line="";
		    while((line=bReader.readLine())!=null){
		    	// @attribute /home/fan/data/Family/small-sample-exp/exp1/train/plankton/FamilyInfo/Im--0.9/Cluster/0.203--3.038--27.gexf numeric
		    	if(line.contains("@attribute")&&line.contains("numeric")){
		    		String str[]=line.split(" ");
		    		String featureFilePath=str[1];
		    	//	System.out.println(featureFilePath);
		    		
		    		FregraphFeature feature=new FregraphFeature(featureFilePath);
		    		trainFeatureList.add(feature);
		    	}
		    	
		    }
			
			bReader.close();
			fReader.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void iniTestFeatureList(String testFileDir){
		try {
			File file=new File(testFileDir);
			File graphs[]=file.listFiles();
			for(int i=0;i<graphs.length;i++){
				String featureFilePath=graphs[i].getAbsolutePath();
				FregraphFeature fregraphFeature=new FregraphFeature(featureFilePath);
				testFeatureList.add(fregraphFeature);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void mergeFeatureList(){
		
		for(int i=0;i<testFeatureList.size();i++){
			FregraphFeature testFeature=testFeatureList.get(i);
			boolean find=false;
			for(int j=0;j<trainFeatureList.size();j++){
				FregraphFeature trainFeature=trainFeatureList.get(j);
				String trainFeatureFilePath=trainFeature.filePath;
				String str[]=trainFeatureFilePath.split("Im");
				String weightScoreFilePath=str[0]+"MethodWeight.txt";
				FamilyWeightScore weightScore=new FamilyWeightScore(weightScoreFilePath);
				if(testFeature.isSimilar(trainFeature, weightScore)){
					find=true;
					break;
				}
			}
			if(find==true){
				// do nothing
			}
			else{
				mergeFeatureList.add(testFeature);
			}
			
		}
		System.out.println("Distinc Testing Feature Size: "+mergeFeatureList.size());
		for(int i=0;i<trainFeatureList.size();i++){
			mergeFeatureList.add(trainFeatureList.get(i));
		}
		System.out.println("Merging Feature Size: "+mergeFeatureList.size());
	}
	public static void writeFeatureSpace(String writeFilePath){
		try {
			File file=new File(writeFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			String head="Training Feature Space Size: "+trainFeatureList.size()+"\n";
			bWriter.write(head);
			head = "Testing Feature Space Size: "+testFeatureList.size()+"\n";
			bWriter.write(head);
			head = "Merging Feature Sapce Size: "+mergeFeatureList.size()+"\n";
			bWriter.write(head);
			
			head="Feature Space: \n\n";
			bWriter.write(head);
			
			for(int i=0;i<mergeFeatureList.size();i++){
				String line=mergeFeatureList.get(i).filePath;
				bWriter.write(line+"\n");
				
			}
			
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
