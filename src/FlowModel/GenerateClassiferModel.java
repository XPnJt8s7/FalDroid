package FlowModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import wlsvm.WLSVM;

public class GenerateClassiferModel {

	public static String trainFilePath="/home/fan/lab/Family/small-size/exp4/result/train/Im--0.5-train.arff";
	public static String classifierModelDir="/home/fan/lab/Family/small-size/exp4/classifierModel/";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		writeClassifierModel();
	}
	public static void writeClassifierModel(){
		try {
			File inputTrainFile=new File(trainFilePath);
			ArffLoader TrainAtf=new ArffLoader();
			TrainAtf.setFile(inputTrainFile);
			Instances trainData=TrainAtf.getDataSet();
			trainData.setClassIndex(trainData.numAttributes()-1);
			
		      Classifier SVM=new WLSVM();
		      String [] options=weka.core.Utils.splitOptions("-S 0 -t 0 -K 0 -D 3 -G 0.0 -R 0.0 -N 0.5 "
		      		+ "-M 40.0 -C 1.0 -E 0.001 -P 0.1 -B 1 -seed 1");
		      SVM.setOptions(options);
		      SVM.buildClassifier(trainData);
		      
		      ObjectOutputStream SVMoos=new ObjectOutputStream(new FileOutputStream(classifierModelDir+"SVM-0.5-0.model"));
		      SVMoos.writeObject(SVM);
		      SVMoos.flush();
		      SVMoos.close();
		      
		      Classifier J48=new J48();
		      J48.buildClassifier(trainData);
		      ObjectOutputStream J48oos=new ObjectOutputStream(new FileOutputStream(classifierModelDir+"J48-0.5-0.model"));
		      J48oos.writeObject(J48);
		      J48oos.flush();
		      J48oos.close();
		      
		      Classifier KNN=new IBk();
		      KNN.buildClassifier(trainData);
		      ObjectOutputStream KNNoos=new ObjectOutputStream(new FileOutputStream(classifierModelDir+"KNN-0.5-0.model"));
		      KNNoos.writeObject(KNN);
		      KNNoos.flush();
		      KNNoos.close();
		      
		      Classifier RF=new RandomForest();
		      RF.buildClassifier(trainData);
		      ObjectOutputStream RFoos=new ObjectOutputStream(new FileOutputStream(classifierModelDir+"RF-0.5-0.model"));
		      RFoos.writeObject(RF);
		      RFoos.flush();
		      RFoos.close();
		      
		      
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
}
