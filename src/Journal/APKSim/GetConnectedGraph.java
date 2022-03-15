package Journal.APKSim;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import ConstantVar.ConstantValue;
import Util.Tool.DirDelete;

public class GetConnectedGraph {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String dirFile="/home/fan/lab/Family/JResult/APKSim/NoGraphScore/result/0.8/";
		getOneFalConnectedGraph(dirFile);
		
	}
	public static void getOneFalConnectedGraph(String dirFilePath){
		try {
			File file=new File(dirFilePath);
			File fals[]=file.listFiles();
			for(int i=0;i<fals.length;i++){
				//if(!fals[i].getName().contains("droidkungfu")){
				String name=fals[i].getName();
				if(name.contains(".dot")){
					//System.out.println(name);
					/*
					 *  分析dot文件
					 */
					String srcFilePath=fals[i].getAbsolutePath();
					int index=srcFilePath.lastIndexOf(".");
					String outFilePath=srcFilePath.substring(0,index)+"-connect";
					File outFile=new File(outFilePath);
					if(outFile.exists()){
						DirDelete delete=new DirDelete();
						delete.deleteDir(outFile);
					}
					outFile.mkdirs();
					String cmd="python "+ConstantValue.CONNECEDFILE_STRING+" "+fals[i].getAbsolutePath()+
							" "+outFilePath+"/";
					
					//exeCmd(cmd);
					System.out.println(cmd+"\n");
				}
				//}
				
			}
			
			
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
