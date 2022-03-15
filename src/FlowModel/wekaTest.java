package FlowModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.parser.java_cup.internal_error;
import wlsvm.WLSVM;

public class wekaTest {

	//public static String trainModelFile="/home/fan/lab/Family/small-size/exp4/classifierModel/SVM-0.5-0.model";
	public static String trainFilePath="/home/fan/lab/Family/small-size/exp4/result/train/Im--0.5-train.arff";
	public static String addFilePath="/home/fan/lab/Family/small-size/exp4/result/train/addTest.arff";
	public static String testFilePath="/home/fan/lab/Family/small-size/exp4/result/train/Im--0.5-test-5.arff";
	public static Instances trainData;
	public static Instances addData;
	public static Instances testData;
	public static int iterateNum=165;
	public static int numPerAdd=5;
	public static String writeFileDir="/home/fan/lab/Family/small-size/exp4/result/performance/";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			for(int i=0;i<100;i++){
				String resultFilePath=writeFileDir+i+".txt";
				oneTime(resultFilePath);
			}
		
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void oneTime(String writeFilePath){
		try {
			File file=new File(writeFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			String line="";
			trainData=getTestData(trainFilePath);  
			testData=getTestData(testFilePath);
			addData=getTestData(addFilePath);
			
			System.out.println("Start:");
			line +="Start:\n";
			System.out.println("TestData Size:"+testData.numInstances());
			line +="TestData Size:"+testData.numInstances()+"\n";
			bWriter.write(line);
			ArrayList<Classifier> classifierList=new ArrayList<>();
			classifierList=trainModel(trainData);
			testModel(classifierList, testData);
			
			for(int i=0;i<iterateNum;i++){
				System.out.println("Result: "+i);
				line ="Result: "+i;
				bWriter.write(line);
				addInstancesToTrain();
//				System.out.println("TrainData Size: "+trainData.numInstances());
//				System.out.println("AddData Size: "+ addData.numInstances());
				classifierList=trainModel(trainData);
				line=testModel(classifierList, testData);
				bWriter.write(line);
			}
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void addInstancesToTrain(){
		Set<Integer> addSet=new HashSet<>();
		addSet=randomNumber(addData.numInstances());
		Iterator<Integer> addIterator=addSet.iterator();
		while(addIterator.hasNext()){
			int index=addIterator.next();
			trainData.add(addData.instance(index));
		}
		List<Integer> deleteList=sortByValue(addSet);
		for(int j=0;j<deleteList.size();j++){
			int index=deleteList.get(j);
			addData.delete(index);
		}
	}
    public static List<Integer> sortByValue(Set<Integer> randomSet){  
        List<Integer> setList= new ArrayList<>(randomSet);  
        Collections.sort(setList, new Comparator<Integer>() {  
            @Override  
            public int compare(Integer o1, Integer o2) {  
                // TODO Auto-generated method stub  
                return o2-o1;  
            }  
        });   
        return setList;  
    }
	public static Set<Integer> randomNumber(int max){
		Set<Integer> randomSet=new HashSet<>();
		while(randomSet.size()<numPerAdd){
			/*
			 *   当随机数个数小于5生成一个新的随机数
			 */
			Random random=new Random();
			int k=random.nextInt(max);
			randomSet.add(k);
		}
		return randomSet;
	}
	public static Instances getTestData(String testFilePath) throws IOException{
		File inputTestFile=new File(testFilePath);
		ArffLoader TestAtf=new ArffLoader();
		TestAtf.setFile(inputTestFile);
		Instances testData=TestAtf.getDataSet();
		testData.setClassIndex(testData.numAttributes()-1);
		return testData;
		
	}
	public static String testModel(ArrayList<Classifier> classifierList, Instances testDataInput) throws Exception{
//		int right=0;  //  当四种分类器结果一致的时候，分类正确的个数
//		int err=0;   //  当四种分类器结果一致的时候，分类错误的个数
		String result="";
		int SVMRight=0;
		int RFRight=0;
		int J48Right=0;
		int KNNRight=0;
	//	System.out.println("Before Size:"+trainDataInput.numInstances());
		Instances testData = testDataInput;	
	//	Instances trainData= trainDataInput;
		for(int  i = 0;i<testData.numInstances();i++)//测试分类结果
	       {
			double actualName=testData.instance(i).classValue();
			ArrayList<Double> predictNameList=new ArrayList<>(); 
			for(int j=0;j<classifierList.size();j++){
				double predictName=classifierList.get(j).classifyInstance(testData.instance(i));
				predictNameList.add(predictName);	
			}
			if(actualName==predictNameList.get(0)){
				SVMRight ++;
			}
			if(actualName==predictNameList.get(1)){
				RFRight ++;
			}
			if(actualName==predictNameList.get(2)){
				J48Right ++;
			}
			if(actualName==predictNameList.get(3)){
				KNNRight ++;
			}
			
			double addInstance=addInstance(predictNameList);
			
//			int svmIndex=Integer.valueOf((int) classifierList.get(0).classifyInstance(testData.instance(i)));
//			double svmPro[]=classifierList.get(0).distributionForInstance(testData.instance(i));
//			int RFIndex=Integer.valueOf((int) classifierList.get(1).classifyInstance(testData.instance(i)));
//			double RFPro[]=classifierList.get(1).distributionForInstance(testData.instance(i));
//			
//			for(int k=0;k<svmPro.length;k++){
//				System.out.println(svmPro[k]);
//			}
			
//			if(svmPro[svmIndex]>=0.32){
//				//System.out.println(pro[index]);
//				testData.instance(i).setClassValue(predictNameList.get(0));
//				trainData.add(testData.instance(i));
//			}
			
//			if(addInstance>0){
//				if(actualName==addInstance){   
//					right++;
//				}
//				else{
//					err ++;
//				}
//				
//			}
			
			
			// trainData.add(testData.instance(i)); 
	       }
		
//		System.out.println("right: "+right);
//		System.out.println("err: "+err);
		double SVMPrediction=Double.valueOf(SVMRight)/Double.valueOf(testData.numInstances());
		double RFPrediction=Double.valueOf(RFRight)/Double.valueOf(testData.numInstances());
		double J48Prediction=Double.valueOf(J48Right)/Double.valueOf(testData.numInstances());
		double KNNPrediction=Double.valueOf(KNNRight)/Double.valueOf(testData.numInstances());
//		System.out.println("Result:");
		System.out.println();
		result +="\n";
		System.out.println("SVM: "+SVMRight+"/"+testData.numInstances()+"---"+SVMPrediction);
		result +="SVM: "+SVMRight+"/"+testData.numInstances()+"---"+SVMPrediction+"\n";
		System.out.println("RF: "+RFRight+"/"+testData.numInstances()+"---"+RFPrediction);
		result +="RF: "+RFRight+"/"+testData.numInstances()+"---"+RFPrediction+"\n";
		System.out.println("J48: "+J48Right+"/"+testData.numInstances()+"---"+J48Prediction);
		result +="J48: "+J48Right+"/"+testData.numInstances()+"---"+J48Prediction+"\n";
		System.out.println("KNN: "+KNNRight+"/"+testData.numInstances()+"---"+KNNPrediction);
		result +="KNN: "+KNNRight+"/"+testData.numInstances()+"---"+KNNPrediction+"\n";
		//System.out.println("After Size:"+trainData.numInstances());
		//System.out.println("After Size:"+trainDataInput.numInstances());
		//return trainData;
		return result;
	}
	public static double addInstance(ArrayList<Double> predictList){
		double add=-1.0;
		Map<Double, Integer> predictMap=new HashMap<>();
		for(int i=0;i<predictList.size();i++){
			if(predictMap.keySet().contains(predictList.get(i))){
				int k=predictMap.get(predictList.get(i));
				k++;
				predictMap.remove(predictList.get(i));
				predictMap.put(predictList.get(i),k);
			}
			else{
				predictMap.put(predictList.get(i), 1);
			}
		}
		Iterator<Double> mapIterator=predictMap.keySet().iterator();
		while(mapIterator.hasNext()){
			double tmp=mapIterator.next();
			int key=predictMap.get(tmp);
			if(key>=3){
				add=tmp;
				break;
			}
		}
		return add;
	}
	public static ArrayList<Classifier> trainModel(Instances trainData) throws Exception{
		ArrayList<Classifier> classifierList=new ArrayList<>(); 
		/*
		 *   0: SVM
		 */
		Classifier SVM=new WLSVM();
	      String [] options=weka.core.Utils.splitOptions("-S 0 -T 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 "
	      		+ "-M 40.0 -V 1 -C 1.0 -E 0.001 -P 0.1 -seed 1");
	      SVM.setOptions(options);
	      SVM.buildClassifier(trainData);
	      classifierList.add(SVM);
	     /*
	      *  1: Random Forest 
	      */
	      Classifier RF=new RandomForest();
	      String []RFoptions=weka.core.Utils.splitOptions("-I 100 -K 0 -S 1");
	      RF.setOptions(RFoptions);
	      RF.buildClassifier(trainData);
	      classifierList.add(RF);
	      /*
	       *  2: J48
	       */
	      Classifier J48=new J48();
	      String []J48options=weka.core.Utils.splitOptions("-C 0.25 -M 2");
	      J48.setOptions(J48options);
	      J48.buildClassifier(trainData);
	      classifierList.add(J48);
	      /*
	       *  3: KNN
	       */
	      Classifier KNN=new IBk();
	      String []KNNoptions=weka.core.Utils.splitOptions("-K 1 -W 0");
	      KNN.setOptions(KNNoptions);
	      KNN.buildClassifier(trainData);
	      classifierList.add(KNN);
		 return classifierList;
	}
		

}
