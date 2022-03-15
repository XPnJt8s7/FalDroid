package GraphMap;

import java.io.File;

import ConstantVar.ConstantValue;


public class testAPKGraphMap {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	///	String apkFilePath="/home/fan/data/Family/train-final/geinimi/apktool/84454ca15c40e632725675dc58689b17.apk/";
		String sup="0.5";
		String srcDirFile=ConstantValue.getVar().FAMILIESDIRPATH_STRING;
		File srcDir=new File(srcDirFile);
		File fals[]=srcDir.listFiles();
		for(int i=0;i<fals.length;i++){
		String falPath=fals[i].getAbsolutePath()+"/";
		mapOneFal(falPath, sup);
		}
		
	}
	public static void mapOneFal(String falPath, String sup){
		String apkToolPath=falPath+"apktool/";
		File apktool=new File(apkToolPath);
		File apks[]=apktool.listFiles();
		for(int i=0;i<apks.length;i++){
			String apkFilePath=apks[i].getAbsolutePath()+"/";
			APKGraphMap apkGraphMap=new APKGraphMap(apkFilePath, sup);
			apkGraphMap.writeResult();
			System.out.println("Finish: "+ apkFilePath+"---"+i+"#"+apks.length);
		}
	}
}
