package GED.MyTime;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import CandicateFamily.FamilyWeightScore;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.SubGraphSimilarity;

public class SimGraphTime {
	public static String srcFilePath="/home/fan/data/Family/Com_GED/NotSame/";
	public static ArrayList<String> timeList=new ArrayList<>();
	public static ArrayList<Double> simList=new ArrayList<>();
	public static String weightScoreFilePath="/home/fan/data/Family/train/geinimi/FamilyInfo/MethodWeight.txt";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File srcDir=new File(srcFilePath);
		File graphFiles[]=srcDir.listFiles();
		int size=graphFiles.length;
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				if(i!=j){
					double sim=OnePairGraph(graphFiles[i].getAbsolutePath(), graphFiles[j].getAbsolutePath());
				// System.out.println(i+"#"+j+" : "+sim);
				}
			}
		}
		String writeFilePath="/home/fan/data/Family/result/GED/myNotSameTime.csv";
	    writeFile(writeFilePath);
	}
	public static double OnePairGraph(String srcGexfFile, String dstGexfFile){
		CommunitySubGraph srcCommunity=new CommunitySubGraph(srcGexfFile);
		CommunitySubGraph dstCommunity=new CommunitySubGraph(dstGexfFile);
		FamilyWeightScore weightScore=new FamilyWeightScore(weightScoreFilePath);
		long startTime=System.currentTimeMillis();
		SubGraphSimilarity similarity=new SubGraphSimilarity(srcCommunity.getGraph(), 
				dstCommunity.getGraph(), weightScore);
		double simScore=similarity.getSimScore();
		long endTime=System.currentTimeMillis();
		long useTime=endTime-startTime;
		String time=String.valueOf(useTime);
		timeList.add(time);
		simList.add(simScore);
		return simScore;
	}
	public static void writeFile(String writeFilePath){
		try {
			File file=new File(writeFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			String line="time,similarity\n";
			bWriter.write(line);
			for(int i=0;i<timeList.size();i++){
				line = timeList.get(i)+","+simList.get(i)+"\n";
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
