package GED.GEDofGraphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import com.sun.swing.internal.plaf.metal.resources.metal_zh_TW;

public class GEDSimTime {

	public static ArrayList<String> timeList=new ArrayList<>();
	public static ArrayList<Double> simList=new ArrayList<>();
	public static String dotFilePath="/home/fan/data/Family/Com_GED/SameDot/";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dotDir=new File(dotFilePath);
		File dotFiles[]=dotDir.listFiles();
		for(int i=0;i<dotFiles.length;i++){
			for(int j=0;j<dotFiles.length;j++){
				if(i!=j){
					String srcFilePath=dotFiles[i].getAbsolutePath()+"/";
					String dstFilePath=dotFiles[j].getAbsolutePath()+"/";
					OnePairGraph(srcFilePath, dstFilePath);
				}
			}
		}
		String writeFilePath="/home/fan/data/Family/result/GED/GEDSameTime.csv";
		writeFile(writeFilePath);
	}
	public static void OnePairGraph(String srcFilePath, String dstFilePath){
		String srcNodeFile=srcFilePath+"/node.dot";
		String dstNodeFile=dstFilePath+"/node.dot";
		String srcEdgeFile=srcFilePath+"/edge.dot";
		String dstEdgeFile=dstFilePath+"/edge.dot";
		long startTime=System.currentTimeMillis();
		SimGED simGED=new SimGED(srcNodeFile, dstNodeFile, srcEdgeFile, dstEdgeFile);
		long endTime=System.currentTimeMillis();
		long useTime=endTime-startTime;
		double similarity=simGED.getSimilarity();
		System.out.println("Time:"+useTime+", "+"Sim:"+similarity);
		timeList.add(String.valueOf(useTime));
		simList.add(similarity);
	}
	public static void writeFile(String filePath){
		try {
			File file=new File(filePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			String line="time,similarity\n";
			bWriter.write(line);
			for(int i=0;i<timeList.size();i++){
				line=timeList.get(i)+','+simList.get(i)+"\n";
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
