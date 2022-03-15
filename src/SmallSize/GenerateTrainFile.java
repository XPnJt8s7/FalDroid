package SmallSize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Set;

import CandicateFamily.FamilyWeightScore;
import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;


public class GenerateTrainFile {

	public static String featureFile="/home/fan/data/Family/small-sample-exp/exp4/result/Im--0.5-train.arff";
	public static ArrayList<FregraphFeature> featureList=new ArrayList<>();
	public static String trainDir="/home/fan/data/Family/small-sample-exp/exp1/train/";
	public static String testDir="/home/fan/data/Family/small-sample-exp/exp1/test/";
	public static ArrayList<String> familyNameList=new ArrayList<>();
	public static String writeTrainFilePath="/home/fan/data/Family/small-sample-exp/exp1/result/tmp/train.arff";
	public static String writeTestFilePath="/home/fan/data/Family/small-sample-exp/exp1/result/tmp/test.arff";
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> supportList=new ArrayList<>();
//		supportList.add("0.5");
//		supportList.add("0.6");
//		supportList.add("0.7");
//		supportList.add("0.8");
//		supportList.add("0.9");
		
		
//		for(int i=0;i<supportList.size();i++){
//			oneSupport(supportList.get(i));
//		}
		readFeature();
		generateFile(writeTrainFilePath, "train");
		generateFile(writeTestFilePath, "test");
	}
//	public static void oneSupport(String support){
//		featureList=new ArrayList<>();
//		familyNameList=new ArrayList<>();
//		
//		
//		featureFile +="Im--"+support+".arff";
//		writeTrainFilePath +="Im--"+support+"--train.arff";
//		writeTestFilePath +="Im--"+support+"--test.arff";
//		
//		readFeature();
//		generateFile(writeTrainFilePath, "train");
//		generateFile(writeTestFilePath, "test");
//	}
	public static void generateFile(String writeFilePath, String type){
		try {
			File writeFile=new File(writeFilePath);
			FileWriter fileWriter=new FileWriter(writeFile);
			BufferedWriter bWriter=new BufferedWriter(fileWriter);
			
			
			String trainData="";
			trainData +="@relation FamilyClassificationTrainingDataset\n";
			for(int i=0;i<featureList.size();i++){
				trainData +="@attribute "+featureList.get(i).filePath+" numeric\n";
			}
			trainData +="@attribute label\n";
					
			
			File file;
			if(type=="train"){
				file=new File(trainDir);
			}
			else{
				file=new File(testDir);
			}
			
			File fals[]=file.listFiles();
			String nameTmp="{";
			for(int i=0;i<fals.length;i++){
				String name=fals[i].getName();
				familyNameList.add(name);
				nameTmp +=name +",";
			}
			nameTmp = nameTmp.substring(0,nameTmp.length()-1);
			nameTmp +="}\n\n";
			trainData +=nameTmp;
			trainData +="@data\n";
			bWriter.write(trainData);
			trainData="";
			
			for(int i=0;i<fals.length;i++){
				String name=fals[i].getName();
				FamilyWeightScore weightScore;
				if(type=="train"){
					String weightScoreFilePath=fals[i].getAbsolutePath()+"/FamilyInfo/MethodWeight.txt";
					weightScore=new FamilyWeightScore(weightScoreFilePath);
				}
				else{
					String weightScoreFilePath="/home/fan/data/Family/small-sample-exp/exp1/train/geinimi/FamilyInfo/MethodWeight.txt";
					weightScore=new FamilyWeightScore(weightScoreFilePath);
				}
				String apkTool=fals[i].getAbsolutePath()+"/apktool/";
				File apktool=new File(apkTool);
				File apks[]=apktool.listFiles();
				for(int j=0;j<apks.length;j++){
					String graphSetFilePath=apks[j].getAbsolutePath()+"/SICG/Community/Community_Im/";
					String apkVectorString=oneAPKVector(graphSetFilePath, weightScore);
					apkVectorString +=name+"\n";
					trainData =apkVectorString;
					bWriter.write(trainData);
					System.out.println(name+" #"+i+": "+j);
				}
			}
			
			bWriter.close();
		    fileWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static String oneAPKVector(String graphSetFilePath, FamilyWeightScore weightScore){
		ArrayList<String> vector=new ArrayList<>();
		File graphFile=new File(graphSetFilePath);
		File graphs[]=graphFile.listFiles();
		for(int i=0;i<featureList.size();i++){
			FregraphFeature feature=featureList.get(i);
			boolean find=false;
			for(int j=0;j<graphs.length;j++){
				String fileName=graphs[j].getName();
				if(!fileName.startsWith("0.000")){
					CommunitySubGraph dstGraph=new CommunitySubGraph(graphs[j].getAbsolutePath());
					SubGraphSimilarity similarity=new SubGraphSimilarity(feature.subGraph.getGraph(), dstGraph.getGraph(), weightScore);
					if(similarity.getSimScore()>ConstantValue.getVar().minScoreSim){
						find=true;
						break;
					}
				}
			}
			if(find==true){
				vector.add("1,");
			}
			else{
				vector.add("0,");
			}
			
		}
		String reString="";
		for(int i=0;i<vector.size();i++){
			reString +=vector.get(i);
		}
		return reString;
	}
	public static void readFeature(){
		try {
			File file=new File(featureFile);
			FileReader fReader=new FileReader(file);
			BufferedReader bReader=new BufferedReader(fReader);
			String line="";
			while((line=bReader.readLine())!=null){
				if(line.contains("Cluster")){
					FregraphFeature feature;
					if(line.contains("@attribute")){
						String tmpStr[]=line.split(" ");
						feature=new FregraphFeature(tmpStr[1]);
					}
					else{
						feature=new FregraphFeature(line);
					}
					
					
					featureList.add(feature);
				}
			}
			System.out.println("Feature Size: "+featureList.size());
			bReader.close();
			fReader.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

}
