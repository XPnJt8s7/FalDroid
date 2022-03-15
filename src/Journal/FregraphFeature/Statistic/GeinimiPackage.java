package Journal.FregraphFeature.Statistic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import APKData.SmaliGraph.MethodNode;
import GraphSimilarity.CommunitySubGraph;

public class GeinimiPackage {

	public static String trainDir="/home/fan/lab/Family/train-final/geinimi/apktool/";
	public static String testDir="/home/fan/lab/Family/test-final/geinimi/apktool/";
	public static Map<String, Integer> packageMap=new HashMap<>();
	public static String writeFilePath="/home/fan/lab/Family/jResult/geinimiPack.txt";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// String testCommunityFilePath="/home/fan/tmp/0.600--9.608--3.gexf";
		gatherPackage(trainDir);
		gatherPackage(testDir);
		
		writeResut(writeFilePath);
		
	}
	
	public static void gatherPackage(String srcDir){
		try {
			File dir=new File(srcDir);
			File apks[]=dir.listFiles();
			for(int i=0;i<apks.length;i++){
				String comDirPath=apks[i].getAbsolutePath()+"/SICG/Community/Community_Im/";
				File com=new File(comDirPath);
				File graphs[]=com.listFiles();
				for(int j=0;j<graphs.length;j++){
					String graphFilePath=graphs[j].getAbsolutePath();
					CommunitySubGraph communitySubGraph=new CommunitySubGraph(graphFilePath);
					if(containAPI(communitySubGraph)){
						extractPack(communitySubGraph);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void writeResut(String writeFilePath){
		try {
			File file=new File(writeFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			Iterator<String> mapIterator=packageMap.keySet().iterator();
			int total=0;
			while(mapIterator.hasNext()){
				String packname=mapIterator.next();
				int num=packageMap.get(packname);
				total +=num;
				String line=packname+":"+num;
				System.out.println(line);
				bWriter.write(line+"\n");
			}
			
			bWriter.close();
			fWriter.close();
			System.out.println("Total:"+total);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void extractPack(CommunitySubGraph communitySubGraph){
		try {
			File readFile=new File(communitySubGraph.getFilePath());
			FileReader fReader=new FileReader(readFile);
			BufferedReader bufferedReader=new BufferedReader(fReader);
			String line="";
			String packName="";
			Map<String, Integer> tmpMap=new HashMap<>();
			while((line=bufferedReader.readLine())!=null){
				if(line.contains("<edge") && line.contains("getLine1Number")){
					String reg="(.*?)source=\'(.*?): (.*?)target=(.*?)";
					Pattern pattern=Pattern.compile(reg);
					Matcher matcher=pattern.matcher(line);
					if(matcher.find()){
						String Pack=matcher.group(2);
						if(tmpMap.containsKey(Pack)){
							int k=tmpMap.get(Pack);
							k++;
							tmpMap.put(Pack, k);
						}
						else{
							tmpMap.put(Pack, 1);
						}
					}
				}
				if(line.contains("<edge") && line.contains("getSimSerialNumber")){
					String reg="(.*?)source=\'(.*?): (.*?)target=(.*?)";
					Pattern pattern=Pattern.compile(reg);
					Matcher matcher=pattern.matcher(line);
					if(matcher.find()){
						String Pack=matcher.group(2);
						if(tmpMap.containsKey(Pack)){
							int k=tmpMap.get(Pack);
							k++;
							tmpMap.put(Pack, k);
						}
						else{
							tmpMap.put(Pack, 1);
						}
					}
				}
				if(line.contains("<edge") && line.contains("getSubscriberId")){
					String reg="(.*?)source=\'(.*?): (.*?)target=(.*?)";
					Pattern pattern=Pattern.compile(reg);
					Matcher matcher=pattern.matcher(line);
					if(matcher.find()){
						String Pack=matcher.group(2);
						if(tmpMap.containsKey(Pack)){
							int k=tmpMap.get(Pack);
							k++;
							tmpMap.put(Pack, k);
						}
						else{
							tmpMap.put(Pack, 1);
						}
					}
				}
			}
			Iterator<String> tmpIterator=tmpMap.keySet().iterator();
			int max=0;
			while(tmpIterator.hasNext()){
				String tmpPack=tmpIterator.next();
				int size=tmpMap.get(tmpPack);
				if(size>max){
					packName=tmpPack;
					max=size;
				}
			}
			if(packName.equals("aC")){
				System.out.println(communitySubGraph.getFilePath());
			}
			if(packageMap.containsKey(packName)){
				int num=packageMap.get(packName);
				num++;
				packageMap.remove(packName);
				packageMap.put(packName, num);
			}
			else{
				packageMap.put(packName, 1);
			}
			bufferedReader.close();
			fReader.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public static boolean containAPI(CommunitySubGraph communitySubGraph){
		boolean contain=false;
		Set<MethodNode> nodeSet=new HashSet<>();
		nodeSet=communitySubGraph.getGraph().getNodeSet();
		Iterator<MethodNode> nodeIterator=nodeSet.iterator();
		while(nodeIterator.hasNext()){
			MethodNode node=nodeIterator.next();
			String name=node.getCommonString();
			if(name.contains("getLine1Number")){
				contain=true;
				break;
			}
		}
		
		return contain;
	}
	

}
