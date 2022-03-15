package Journal.TrainSize;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class StaticalWekaResult {
	public static String resultDir="/home/fan/lab/Family/small-size/exp4/result/performance/";
	public static ArrayList<OneWekaFileResult> resultList=new ArrayList<>();
	public static ArrayList<String> SVMAvgList=new ArrayList<>();
	public static ArrayList<String> RFAvgList=new ArrayList<>();
	public static ArrayList<String> J48AvgList=new ArrayList<>();
	public static ArrayList<String> KNNAvgList=new ArrayList<>();
	public static String finalResultFilePath="/home/fan/lab/Family/JResult/TrainSize/trainSize.csv";
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readFile();
		getAvgList();
		writeResult(finalResultFilePath);
	}
	public static void readFile(){
		try {
			File file=new File(resultDir);
			File subFiles[]=file.listFiles();
			for(int i=0;i<subFiles.length;i++){
				String filePath=subFiles[i].getAbsolutePath();
				OneWekaFileResult oneResult=new OneWekaFileResult(filePath);
				resultList.add(oneResult);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void getAvgList(){
		int k=resultList.get(0).SVMList.size();
		for(int i=0;i<k;i++){
			double avgSVM=0.0D;
			double avgRF=0.0D;
			double avgJ48=0.0D;
			double avgKNN=0.0D;
			for(int j=0;j<resultList.size();j++){
				 Double tmpSVM=Double.valueOf(resultList.get(j).SVMList.get(i));
				 avgSVM +=tmpSVM;
				 Double tmpRF=Double.valueOf(resultList.get(j).RFList.get(i));
				 avgRF +=tmpRF;
				 Double tmpJ48=Double.valueOf(resultList.get(j).J48List.get(i));
				 avgJ48 +=tmpJ48;
				 Double tmpKNN=Double.valueOf(resultList.get(j).KNNList.get(i));
				 avgKNN +=tmpKNN;
				 
			}
			avgSVM = avgSVM/resultList.size();
			avgRF = avgRF/resultList.size();
			avgJ48 = avgJ48/resultList.size();
			avgKNN = avgKNN/resultList.size();
			
			SVMAvgList.add(String.valueOf(avgSVM));
			RFAvgList.add(String.valueOf(avgRF));
			J48AvgList.add(String.valueOf(avgJ48));
			KNNAvgList.add(String.valueOf(avgKNN));
		}
		
	}
	public static void writeResult(String filePath){
		try {
			File file=new File(filePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			int k=SVMAvgList.size();
			String line="";
			for(int i=0;i<k;i++){
				line = SVMAvgList.get(i)+","+RFAvgList.get(i)+","+
						J48AvgList.get(i)+","+KNNAvgList.get(i)+"\n";
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
