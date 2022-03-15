package Journal.APKSim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ConstantVar.ConstantValue;

public class FalObj {
	public ArrayList<APKObj> apkList=new ArrayList<>();
	public String falName="";
	public String filePath="";      //  家族向量文件路径
	public Map<String, Double> apkSimMap=new HashMap<>();
	public ArrayList<GroupAPK> groupList=new ArrayList<>();
	
	
	public FalObj(){
		
	}
	public FalObj(String falpath){
		filePath=falpath;
		iniFalObj();
	}
	public void addOneAPK(String apkVectorString){
		try {
			if(apkVectorString.contains(".apk")){
				/*
				 *   表示是一个APK向量
				 */
				APKObj apkObj=new APKObj(apkVectorString);
				apkList.add(apkObj);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void iniFalObj(){
		try {
			File file=new File(filePath);
			FileReader fReader=new FileReader(file);
			BufferedReader bReader=new BufferedReader(fReader);
			String line="";
			while((line=bReader.readLine())!=null){
				if(line.contains(".apk")){
					/*
					 *   表示是一个APK向量
					 */
					APKObj apkObj=new APKObj(line);
					apkList.add(apkObj);
				}
			}
			bReader.close();
			fReader.close();
			
			calAPKSim(); //  开始计算家族内部APK相似性
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	/*
	 *    计算一个家族内部APK之间的相似性
	 */
	public void calAPKSim(){
		try {
			for(int i=0;i<apkList.size();i++){
				for(int j=0;j<apkList.size();j++){
					if(i!=j){
						double sim=apkList.get(i).addOneSimAPK(apkList.get(j));
						
						if(sim>=ConstantValue.getVar().APKSIM){
							String pathString1=apkList.get(i).apkFileID+"---"+apkList.get(j).apkFileID;
							String pathString2=apkList.get(j).apkFileID+"---"+apkList.get(i).apkFileID;
							
							if(apkSimMap.containsKey(pathString1) || apkSimMap.containsKey(pathString2)){
								// do nothing
							}
							else{
								apkSimMap.put(pathString1, sim);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/*
	 *   保存APK相似图为dot格式
	 */
	public void storeAPKSimPathDot(String dotFilePath){
		try {
			File file=new File(dotFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			String lineString="";
			lineString="graph G{\n";
			bWriter.write(lineString);
			/*
			 *   写入节点
			 */
			for(int i=0;i<apkList.size();i++){
				String nodeName=apkList.get(i).apkFileID;
				lineString="	\""+nodeName+"\";\n";
				bWriter.write(lineString);
			}
			/*
			 * 写入边
			 */
			Iterator<String> edgeIterator=apkSimMap.keySet().iterator();
			while(edgeIterator.hasNext()){
				String edge=edgeIterator.next();
				String str[]=edge.split("---");
				String src=str[0];
				String dst=str[1];
				double sim=apkSimMap.get(edge);
				lineString="	\""+src+"\" -- \""+dst+"\"[label=\""+sim+"\"];\n";
				bWriter.write(lineString);
			}
			lineString="}";
			bWriter.write(lineString);
			
			bWriter.close();
			fWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/*
	 *   保存APK相似图为gexf格式
	 */
	public void storeAPKSimPathGexf(String APKSimFilePath){
		try {
			File file=new File(APKSimFilePath);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			String lineString="";
			lineString +="<?xml version='1.0' encoding='UTF-8'?>\n"+
					"<gexf xmlns='qianniao918@qq.com' version='1.2' xmlns:viz='http://www.gexf.net/1.2draft/viz'>"+
					" xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'"+ 
					" xsi:schemaLocation='http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd'>\n"+
					"	<meta lastmodifieddate='2014-9-21'>\n"+
					"		<creator>FanMing</creator>\n"+
					"		<description>An Android APK Method SCN!</description>\n"+
					"	</meta>\n"+
					"	<graph mode='static' defaultedgettype='directed'>\n"+
					"		<nodes>\n";
			bWriter.write(lineString);
			/*
			 *  开始写入节点
			 */
			String r="30"; String g="144"; String b="255";
			 for(int i=0;i<apkList.size();i++){
				 String nodeName=apkList.get(i).apkFileID;
				 lineString="		<node id='"+nodeName+"' label=''>\n"+
						   "			<attvalues></attvalues>\n"+
						   "			<viz:color r='"+r+"' g='"+g+"' b='"+b+"'></viz:color>\n"+
						   "			</node>\n";
				 bWriter.write(lineString);
			 }
				lineString ="		</nodes>\n";
				lineString +="		<edges>\n";
				bWriter.write(lineString);
			/*
			 * 开始写入边
			 */
			int id=0;
			Iterator<String> edgeIteratpr=apkSimMap.keySet().iterator();
			while(edgeIteratpr.hasNext()){
				String edge=edgeIteratpr.next();
				String str[]=edge.split("---");
				String src=str[0];
				String dst=str[1];
				double sim=apkSimMap.get(edge);
				lineString="			<edge id='"+id+"' source='"+src+"' target='"+dst+"' label='"+sim+"'/>\n";
				bWriter.write(lineString);
				id++;
			}
			
			lineString ="		</edges>\n";
			lineString +="	</graph>\n";
			lineString +="</gexf>";
			bWriter.write(lineString);
			
			bWriter.close();
			fWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
}
