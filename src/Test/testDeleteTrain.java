package Test;

import java.io.File;

import ConstantVar.ConstantValue;
import Util.Tool.DirDelete;

public class testDeleteTrain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
				File dataFile=new File("/home/fan/data/Family/test-final/");
				File family[]=dataFile.listFiles();
				for(int i=0;i<family.length;i++){
					deleteOneFal(family[i]);
				}
	}
	public static void deleteOneFal(File falDir){
		String apktoolPath=falDir.getAbsolutePath()+"/apktool/";
		File apkfile=new File(apktoolPath);
		File familyInfo=new File(falDir.getAbsolutePath()+"/FamilyInfo/");
	//	System.out.println(apkfile.getAbsolutePath());
		DirDelete delete=new DirDelete();
		delete.deleteDir(apkfile);
		delete.deleteDir(familyInfo);
	}

}
