package Test;

import java.io.File;

import ConstantVar.ConstantValue;
import Exp.ClassifyByKNN.FamilyClusterInfo;
import Exp.ClassifyByKNN.OneAPKResult;
import Exp.ClassifyByKNN.analysisOneAPK;

/*
 * 
 *      计算每一个待检测APK与已知家族的相似性，从而将其划分到相应家族中
 */
public class testAnalysisOneApk {

	public static String log="";
	public static int predictNum=0;
	public static int actualNum=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
					
				File dataFile=new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
				File fals[]=dataFile.listFiles();
				for(int i=0;i<fals.length;i++){
					String falDirPath=fals[i].getAbsolutePath()+"/";
					analysisOneFal(falDirPath);
					System.err.println("Finish analysis family: "+ fals[i].getName());
				}
//				String falDirPath="/home/fan/lab/FamilyClassification/testData/BaseBridge/"; 
//				analysisOneFal(falDirPath);
				System.out.println(log);
				System.out.println(predictNum+"/"+actualNum);
	}
	public static void analysisOneFal(String falDirPath){
		int num=0;
		int totalNum=0;
		String apktool=falDirPath+"apktool/";
		File apkToolFile= new File(apktool);
		apkToolFile.mkdirs();
		File falDir=new File(falDirPath);
		File apks[]=falDir.listFiles();
		for(int i=0;i<apks.length;i++){
			if(apks[i].isFile()){
				totalNum ++;
				String filePath=apks[i].getAbsolutePath();
	//			if(filePath.contains("419d66623a73371a4cf6e96fc7fbf0f230eee724")){
					OneAPKResult apkResult=new OneAPKResult(filePath);
					boolean same=apkResult.showResult();
					if(same==true){
						num++;
					}
				}
		//	}
		}
		actualNum +=totalNum;
		predictNum +=num;
		log +=falDirPath+": "+num+"/"+totalNum+"\n";
	}

}
