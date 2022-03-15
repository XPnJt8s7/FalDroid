package Util.FamilyStatistic;

import java.io.File;
import java.util.ArrayList;

import ConstantVar.ConstantValue;
import Util.Tool.DirDelete;

public class DeleteAllFile {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        File dataFile=new File("/home/fan/data/Family/APPLICATION/");
        File family[]=dataFile.listFiles();
        for(int i=0;i<family.length;i++){
        	deleteOne(family[i]);
        	//System.out.println(family[i].getName());
        }
	}
	public static void deleteOne(File family){
		ArrayList<String> apkList=new ArrayList<>();
		String familyString=family.getAbsolutePath();
		String apktool=familyString+"/apktool/";
	    String familyInfo=familyString+"/FamilyInfo/";
	    File apkFile=new File(apktool);
	    DirDelete delete=new DirDelete();
	    delete.deleteDir(apkFile);
	    File apks[]=family.listFiles();
	    for(int i=0;i<apks.length;i++){
	    	if(apks[i].getName().contains(" ")|| apks[i].getName().length()<30){
	    		// apks[i].delete();
	    		apkList.add(apks[i].getAbsolutePath());
	    		//System.out.println(apks[i].getAbsolutePath());
	    	}
	    }
	    for(int i=0;i<apkList.size();i++){
	    	System.out.println(apkList.get(i));
	        File file=new File(apkList.get(i));
	       file.delete();
	    }
//	    File apks[]=apkFile.listFiles();
//	    for(int i=0;i<apks.length;i++){
//	    	File comFile=new File(apks[i].getAbsolutePath()+"/SICG/Community/");
//	    	File infoFile[]=comFile.listFiles();
//	    	for(int j=0;j<infoFile.length;j++){
//	    		if(infoFile[j].getAbsolutePath().contains("Community_Im")||infoFile[j].getAbsolutePath().contains("Community_Result_im")){
//	    			//  不删除SICG 以及Android配置文件
//	    		}
//	    		else {
//					DirDelete delete=new DirDelete();
//					delete.deleteDir(infoFile[j]);
//	    			System.out.println(infoFile[j].getAbsolutePath());
//				}
//	    	}
//	    }
//	    File apkFile=new File(apktool);
//	    File infoFile=new File(familyInfo);
//	    
//	    DirDelete delete=new DirDelete();
//	    if(apkFile.exists()){
//	    	delete.deleteDir(apkFile);
//	    }
//	    if(infoFile.exists()){
//	    	delete.deleteDir(infoFile);
//	    }
	}

}
