package Util.BasicStatisticExperiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import ConstantVar.ConstantValue;
import GraphSimilarity.SubGraphSet;

public class StatisticFamilySubgraph {

	public static ArrayList<String> fgInfoList=new ArrayList<>();
	public static ArrayList<String> imInfoList=new ArrayList<>();
	public static ArrayList<String> lpInfoList=new ArrayList<>();
	public static ArrayList<String> mlInfoList=new ArrayList<>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
           String dataDir=ConstantValue.getVar().FAMILIESDIRPATH_STRING;
           File dataFile=new File(dataDir);
           File family[]=dataFile.listFiles();
           for(int i=0;i<family.length;i++){
        	   statisticOneFamily(family[i].getAbsoluteFile()+"/");
        	   System.out.println("Finish analysis: "+ family[i].getName());
           }
                  
	}
	public static void statisticOneFamily(String familyDirPath){
		fgInfoList=new ArrayList<>();
		imInfoList=new ArrayList<>();
		lpInfoList=new ArrayList<>();
		mlInfoList=new ArrayList<>();
		calFamilyNum(familyDirPath);
        mkCommunityStatisticInfo(familyDirPath);
	}
	public static void calFamilyNum(String familyDirFileString){
		String apktoolFilePath=familyDirFileString + "apktool/";
		File apktool=new File(apktoolFilePath);
		File apks[]=apktool.listFiles();
		for(int i=0;i<apks.length;i++){
			String communityFilePath=apks[i].getAbsolutePath()+"/SICG/Community/";
			String fgFilePath=communityFilePath + "/Community_Fg/";
			String ImFilePath=communityFilePath + "/Community_Im/";
			String LpFilePath=communityFilePath + "/Community_Lp/";
			String MlFilePath=communityFilePath + "/Community_Ml/";
			File fgFile=new File(fgFilePath);
			File imFile=new File(ImFilePath);
			File lpFile=new File(LpFilePath);
			File mlFile=new File(MlFilePath);
			String fg=statisticOneCommunitySet(fgFile);
			fgInfoList.add(fg);
			String im=statisticOneCommunitySet(imFile);
			imInfoList.add(im);
			String lp=statisticOneCommunitySet(lpFile);
			lpInfoList.add(lp);
			String ml=statisticOneCommunitySet(mlFile);
			mlInfoList.add(ml);
		}
	}
	public static String statisticOneCommunitySet(File communityFile){
		String infomation="";
		SubGraphSet subGraphSet=new SubGraphSet(communityFile.getAbsolutePath()+"/");
		infomation = subGraphSet.getInfomation();
		infomation +=","+communityFile.listFiles().length;
		return infomation;
	}
	public static void mkCommunityStatisticInfo(String familyDirFileString){
		String communityInfo=familyDirFileString + "FamilyInfo/communityStatisticInfo/";
		File communityDir=new File(communityInfo);
		if(!communityDir.exists()){
			communityDir.mkdirs();
		}
		String fgInfoFile=communityInfo+"fgInfo.txt";
		writeInfomationToFile(fgInfoFile, fgInfoList);
//		System.out.println("Finish writing :" + fgInfoFile);
		String imInfoFile=communityInfo +"imInfo.txt";
		writeInfomationToFile(imInfoFile, imInfoList);
//		System.out.println("Finish writing :" + imInfoFile);
		String lpInfoFile=communityInfo+"lpInfo.txt";
		writeInfomationToFile(lpInfoFile, lpInfoList);
//		System.out.println("Finish writing :" + lpInfoFile);
		String mlInfoFile=communityInfo+"mlInfo.txt";
		writeInfomationToFile(mlInfoFile, mlInfoList);
//		System.out.println("Finish writing :" + mlInfoFile);
		
	}
	public static void writeInfomationToFile(String  writeFilePath, ArrayList<String> infoList){
		try {
			File file=new File(writeFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			for(int i=0;i<infoList.size();i++){
				bWriter.write(infoList.get(i)+"\n");
			}
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
