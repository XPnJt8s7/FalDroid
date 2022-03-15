package Journal.FregraphFeature.Statistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;





public class StatisticInfo {

	public static ArrayList<Integer> NumSSGList=new ArrayList<>();
//	public static 
	public static String dirFile=ConstantValue.getVar().FAMILIESDIRPATH_STRING;
	public static String writeFileString="/home/fan/lab/Family/jResult/Statistic/nodeInSG-Reduce.txt";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		NodeInSG();
	}
	public static void NodeInSG(){
		File file=new File(dirFile);
		File fals[]=file.listFiles();
		for(int i=0;i<fals.length;i++){
			File apkTool=new File(fals[i].getAbsolutePath()+"/apktool/");
			File apks[]=apkTool.listFiles();
			for(int j=0;j<apks.length;j++){
				File sourceCom=new File(apks[j].getAbsolutePath()+"/SICG/Community/Community_Im/");
				if(sourceCom.exists()){
				File coms[]=sourceCom.listFiles();
				if(coms.length>0){
					for(int n=0;n<coms.length;n++){
						CommunitySubGraph communitySubGraph=new CommunitySubGraph(coms[n].getAbsolutePath());
						if(communitySubGraph.getSensitiveScore()>0){
							int nodeNum=communitySubGraph.getGraph().getNodeSet().size();
							writeFile(String.valueOf(nodeNum));
						}
					}
				//	NumSSGList.add(senNum);
				//	writeFile(String.valueOf(senNum));
				}
				}
			}
			
		}
	}
	public static void writeFile(String line){
		try {
			File file=new File(writeFileString);
			FileWriter fWriter=new FileWriter(file, true);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			bWriter.write(line+"\n");
			
			bWriter.close();
			fWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	public static void NumSenSubgraph(){
		File file=new File(dirFile);
		File fals[]=file.listFiles();
		for(int i=0;i<fals.length;i++){
			
			File apkTool=new File(fals[i].getAbsolutePath()+"/apktool/");
			File apks[]=apkTool.listFiles();
			for(int j=0;j<apks.length;j++){
//				if(apks[j].getAbsolutePath().contains("adwo") && 
//						apks[j].getAbsolutePath().contains("0af3d3235f0539222de4adce214dff1d.apk")){
//					System.err.println("aa");
				int senNum=0;
				File sourceCom=new File(apks[j].getAbsolutePath()+"/SICG/Community-Source/Community_Im/");
				if(sourceCom.exists()){
				File coms[]=sourceCom.listFiles();
				if(coms.length>0){
					for(int n=0;n<coms.length;n++){
						CommunitySubGraph communitySubGraph=new CommunitySubGraph(coms[n].getAbsolutePath());
						if(communitySubGraph.getSensitiveScore()>0){
							senNum++;
						}
					}
					NumSSGList.add(senNum);
					writeFile(String.valueOf(senNum));
				}
				}
				//}
			}
			
		}
	}
	
	

}
