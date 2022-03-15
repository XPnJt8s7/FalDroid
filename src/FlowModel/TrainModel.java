package FlowModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import ConstantVar.ConstantValue;
import Exp.ClassifyByVector.APKSiggraphSet;
import Exp.ClassifyByVector.FregraphVector;
import libsvm.svm;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class TrainModel {
	  public String featureSpaceFilePath="/home/fan/lab/Family/small-size/exp4/featureSpace/featureSpace-0.5.model";
	  public String classifierModelFilePath="/home/fan/lab/Family/small-size/exp4/classifierModel/SVM-0.5.model";
	  public FregraphVector vector;
	  public Classifier SVM;
	  public ArrayList<String> familyNameList=new ArrayList<>();
	  
	//   public String arff="";
	  public String clusterType="Im--0.5";
	  
	  
	  public TrainModel(){
		  // vector=new FregraphVector();
		  iniFeatureSpace();
		  iniClassifierModel();
		  iniFamilyNameList();
	  }
	  public void iniFeatureSpace(){
		  try {
			  ObjectInputStream in=new ObjectInputStream(new FileInputStream(featureSpaceFilePath));
			  vector= (FregraphVector) in.readObject();
			  in.close();
			  System.out.println(vector.fregraphList.size());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	  }
	  public void iniClassifierModel(){
		  try {
			  ObjectInputStream ojs=new ObjectInputStream(new FileInputStream(classifierModelFilePath));
			  SVM=(Classifier)ojs.readObject();
			  ojs.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	  }
	  public void iniFamilyNameList(){
		  String familyDirPath=ConstantValue.getVar().FAMILIESDIRPATH_STRING;
		  File familyDir=new File(familyDirPath);
		  File fals[]=familyDir.listFiles();
		  for(int i=0;i<fals.length;i++){
			  familyNameList.add(fals[i].getName());
		  }
	  }
	  public void processOneAPK(String apkFilePath){
		  try {
			String line=getHeader(vector);
			line += getOneAPKVector(apkFilePath, vector);
			
			File apkFile=new File(apkFilePath);
			String fileName=apkFile.getName();
			int k=apkFilePath.lastIndexOf("/");
			String outFileString=apkFilePath.substring(0,k+1);
			File falDir=new File(outFileString);
			String actualFalName=falDir.getName();
			outFileString +="apktool/"+fileName+"/";
			
			String featureVectorFilePath=outFileString + "SICG/vector.arff";
			File vectorFile=new File(featureVectorFilePath);
			FileWriter fWriter=new FileWriter(vectorFile);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			bWriter.write(line);
			bWriter.close();
			fWriter.close();
	        		
			File inputTestFile=new File(featureVectorFilePath);
			ArffLoader TestAtf=new ArffLoader();
			TestAtf.setFile(inputTestFile);
			Instances testData=TestAtf.getDataSet();
			testData.setClassIndex(testData.numAttributes()-1);
			
			 int index=Integer.valueOf((int) SVM.classifyInstance(testData.instance(0)));
			 double pro[]=SVM.distributionForInstance(testData.instance(0));
			 String predictFamilyName=familyNameList.get(index);
			 
			 String result="";
			 result += "Actual Name: "+ actualFalName+"\n";
			 result +="Predict Name: "+ predictFamilyName+"\n";
			 result +="Predict Pro: "+ pro[index]+"\n";
			 result +="Distribution: \n";
			 
			 for(int i=0;i<pro.length;i++){
				 result +=i+" "+ familyNameList.get(i)+" : "+pro[i]+"\n";
			 }
			 System.out.println(result);
			 
			 File resultFile=new File(outFileString +"SICG/result.txt");
			 FileWriter reFileWriter=new FileWriter(resultFile);
			 BufferedWriter reBufferedWriter=new BufferedWriter(reFileWriter);
			 reBufferedWriter.write(result);
			 reBufferedWriter.close();
			 reFileWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	  }
	  public String getHeader(FregraphVector vector){
			String arff ="@relation FamilyClassificationTrainingDataset\n";
			arff +=vector.getFeatureNameList();
			arff +="@attribute label {";
			String labelList="";
			File dataFile=new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
			File falDir[]=dataFile.listFiles();
			for(int i=0;i<falDir.length;i++){
				String name=falDir[i].getName();
				labelList +=name+",";
			}
			labelList = labelList.substring(0,labelList.length()-1);
			arff +=labelList+"}\n\n";
			arff +="@data\n";
			return arff;
		}
	  public String getOneFalVector(String falDirPath, FregraphVector vector){
			String falString="";
			File falFile=new File(falDirPath);
			File apks[]=falFile.listFiles();
			for(int i=0;i<apks.length;i++){
				if(apks[i].getName().endsWith(".apk")){
					String apkFilePath=apks[i].getAbsolutePath();
					falString +=getOneAPKVector(apkFilePath, vector)+"\n";
					System.out.println(falDirPath+":"+apks[i].getName()+"   #"+i);
				}
			}
			return falString;
		}
	  public String getOneAPKVector(String apkFilePath, FregraphVector vector){
			APKSiggraphSet siggraphSet=new APKSiggraphSet(apkFilePath, clusterType);
			long startTime=System.currentTimeMillis();
			
			siggraphSet.storeAllSubgraphs();
			siggraphSet.storeAllSignificantSubgraphs();
			
			
			siggraphSet.generateVector(vector);
			
//			String vectorString=siggraphSet.getFeatureIntVector();
			String vectorString=siggraphSet.getFeatureDoubleVector();
			long endTime=System.currentTimeMillis();
			long useTime=endTime-startTime;
			String time=String.valueOf(useTime);
//			TimeList.add(time);
			return vectorString;
		}
	  
}
