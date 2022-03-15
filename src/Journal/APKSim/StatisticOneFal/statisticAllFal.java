package Journal.APKSim.StatisticOneFal;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class statisticAllFal {

	public static String srcDirFilePath="/home/fan/lab/Family/JResult/"
			+ "APKSim/HasGraphScore/result/0.5/";
	public static String scoreFilePath="/home/fan/lab/Family/JResult/APKSim/HasGraphScore/result"
			+ "/APKScore/0.5.txt";
	public static APKScore apkScore;
	public static String statisticResultFileParh="/home/fan/lab/Family/JResult"
			+ "/APKSim/HasGraphScore/result/Statistic/0.5.txt";
	public static ArrayList<OneFal> falList=new ArrayList<>();
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		apkScore=new APKScore(scoreFilePath);
		iniFal();
		wirteResult(statisticResultFileParh);
	}
	
	public static void iniFal(){
		try {
			File file=new File(srcDirFilePath);
			File subfiles[]=file.listFiles();
			for(int i=0;i<subfiles.length;i++){
				if(subfiles[i].getName().contains("-connect")){
					/*
					 *    表明是一个家族的内部样本链接文件
					 */
					OneFal oneFal=new OneFal(subfiles[i].getAbsolutePath());
					oneFal.iniGroups(apkScore);
					
					falList.add(oneFal);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void wirteResult(String filePath){
		try {
			File file=new File(filePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			for(int i=0;i<falList.size();i++){
				String line=falList.get(i).getResult();
				bWriter.write(line);
			}
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
