package Journal.APKSim;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ConstantVar.ConstantValue;

public class GroupAPK {
	public ArrayList<APKObj> apkList=new ArrayList<>();
	public Map<String, Double> apkSimMap=new HashMap<>();
	
	public GroupAPK(){
		
	}
	public boolean addOneAPK(APKObj apkObj){
		boolean canAdd=false;
		for(int i=0;i<apkList.size();i++){
			if(apkList.get(i).containSimAPK(apkObj)){
				canAdd=true;
				break;
			}
		}
		return canAdd;
	}
	/*
	 *    计算一个group内部APK之间的相似性
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
	
}
