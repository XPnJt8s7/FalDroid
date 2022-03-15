package Util.BasicStatisticExperiment.FamilyInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import ConstantVar.ConstantValue;
import Util.Tool.DirDelete;

public class MvClusterResult {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String dataFilePath=ConstantValue.getVar().FAMILIESDIRPATH_STRING;
		File dataFile=new File(dataFilePath);
		File fal[]=dataFile.listFiles();
		for(int i=0;i<fal.length;i++){
			String familyInfoPath=fal[i].getAbsolutePath()+"/FamilyInfo/";
			double topRadio=1-ConstantValue.getVar().minTotalGraphScoreRatio;
			String radioString=String.valueOf(topRadio);
			/*
			 *     构建新文件夹
			 */
			File topFile=new File(familyInfoPath+"top-"+radioString+"/");
			if(topFile.exists()){
				DirDelete delete=new DirDelete();
				delete.deleteDir(topFile);
			}
			else{
				topFile.mkdirs();
			}
			File familyInfo=new File(familyInfoPath);
			File im[]=familyInfo.listFiles();
			for(int j=0;j<im.length;j++){
				if(im[j].getName().contains("Im")){
					String cmd="cp -r "+im[j].getAbsolutePath()+"/ "+topFile.getAbsolutePath()+"/";
					System.out.println(cmd);
					exeCmd(cmd);
				}
			}
			for(int j=0;j<im.length;j++){
				if(im[j].getName().contains("Im")){
					DirDelete delete=new DirDelete();
					delete.deleteDir(im[j]);
				}
			}
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
