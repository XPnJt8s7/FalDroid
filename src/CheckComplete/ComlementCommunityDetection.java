package CheckComplete;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


import CommunityStructure.CommunityDetection.OneObjectCommunityDetection;
import ConstantVar.ConstantValue;

public class ComlementCommunityDetection {

	public static String filePath="/home/fan/tmp/za";
	public static ArrayList<String> apkFilePathList=new ArrayList<>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readFile();
		
//		String teString="/home/fan/data/Family/small-sample-exp/exp1/test/smsreg/dbfab4abbf46f005c216acce199d4450.apk";
//		oneFileCommunityDetection(teString);
		
	}
	public static void readFile(){
		try {
			File file=new File(filePath);
			FileReader fReader=new FileReader(file);
			BufferedReader bReader=new BufferedReader(fReader);
			String line="";
			while((line=bReader.readLine())!=null){
				String args[]=line.split("/apktool");
				String s1=args[0];
				String s2=args[1];
				System.out.println(s1+s2);
				apkFilePathList.add(s1+s2);
				oneFileCommunityDetection(s1+s2);
			}
			bReader.close();
			fReader.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void oneFileCommunityDetection(String inputFilePath){
		 int k=inputFilePath.lastIndexOf("/");
		    String outPutFileDir=inputFilePath.substring(0,k)+"/apktool/";
		    String apkName=inputFilePath.substring(k+1);
		    outPutFileDir +=apkName+"/";
		    String weightScoreFilePath;
		    if(inputFilePath.contains("train")){
		    	String str[]=inputFilePath.split("train/");
		    	int m=str[1].indexOf("/");
		    	String familyName=str[1].substring(0, m);
		    	weightScoreFilePath=ConstantValue.getVar().FAMILIESDIRPATH_STRING+familyName+"/FamilyInfo/MethodWeight.txt";
		    	System.out.println(familyName);
		    }
		    else{
		    	weightScoreFilePath="/home/fan/lab/Family/extend/train/droidkungfu/FamilyInfo/MethodWeight.txt";
		    }
		    
			long startTime=System.currentTimeMillis();
			OneObjectCommunityDetection oneObjectCommunityDetection=new OneObjectCommunityDetection(outPutFileDir, weightScoreFilePath);
			long endTime=System.currentTimeMillis();
			long useTime=endTime-startTime;
	}
}
