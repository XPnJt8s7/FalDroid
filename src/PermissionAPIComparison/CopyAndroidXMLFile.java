package PermissionAPIComparison;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CopyAndroidXMLFile {
	
	/*
	 *   拷贝出制定文件夹中的权限配置文件到目的文件夹中
	 */
	public static String srcFileString="/home/fan/lab/FamilyClassification/test/";
	public static String dstFileString="/home/fan/lab/FamilyClassification/Permission/test/";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			File srcDataFile=new File(srcFileString);
			File fals[]=srcDataFile.listFiles();
			for(int i=0;i<fals.length;i++){
					cpOneFal(fals[i]);
			}
	}
	public static void cpOneFal(File falFile){
		try {
			String name=falFile.getName();
			String newFalFileString=dstFileString+name+"/";
			File newFalFile=new File(newFalFileString);
			newFalFile.mkdirs();
			File apkFile=new File(falFile.getAbsolutePath()+"/apktool/");
			File apks[]=apkFile.listFiles();
			for(int i=0;i<apks.length;i++){
				String apkName=apks[i].getName();
				String srcFilePathString=apks[i].getAbsolutePath()+"/AndroidManifest.xml";
				String dstFilePathString=newFalFile.getAbsolutePath()+"/"+apkName+"-AndroidManifest.xml";
				String cmd="cp "+srcFilePathString+" "+dstFilePathString;
				System.out.println(cmd);
				exeCmd(cmd);
			}
			System.out.println("Finish Family" + falFile.getName());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void exeCmd(String cmd){
		Runtime rnRuntime=Runtime.getRuntime();
		String outInfo="";
		try {
			Process process=rnRuntime.exec(cmd);
			InputStream in = ( process).getErrorStream();//得到错误信息输出。
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ( (line = br.readLine())!= null) {
				outInfo = outInfo + line + "\n";
			} 
			//System.out.println(outInfo);
		}
			catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
