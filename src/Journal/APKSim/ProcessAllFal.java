package Journal.APKSim;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.omg.CORBA.PUBLIC_MEMBER;

import com.sun.org.apache.xpath.internal.axes.FilterExprWalker;

import ConstantVar.ConstantValue;

public class ProcessAllFal {

	// public static ArrayList<FalObj> falList=new ArrayList<>();
	public static Map<String, FalObj> falMap=new HashMap<>();
	public static String falSimFilePath="/home/fan/lab/Family/JResult/APKSim/HasGraphScore/vector.arff";
//	public static String writeFalSim="/home/fan/lab/Family/JResult/APKSim/HasGraphScore/result/0.9/";
	public static String APKSimFile="/home/fan/lab/Family/JResult/APKSim/HasGraphScore/result/APKScore/0.5.txt";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		iniFal();
		writeAPKResult();
	}
	public static void writeAPKResult(){
		try {
			File file=new File(APKSimFile);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			Iterator<String> falNameIterator=falMap.keySet().iterator();
			while(falNameIterator.hasNext()){
				String name=falNameIterator.next();
				FalObj falObj=falMap.get(name);
				for(int i=0;i<falObj.apkList.size();i++){
					String line=falObj.apkList.get(i).apkFileID+":"+falObj.apkList.get(i).getMaxSim()+":"+
								falObj.apkList.get(i).apkSimMap.size();
					bWriter.write(line+"\n");
				}
			}
			
			bWriter.close();
			fWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/*
	 *   读入文件，存储每一个家族的样本向量
	 */
	public static void iniFal(){
		try {
			Iterator<String> falnameIterator=ConstantValue.getVar().falNameSet.iterator();
			while(falnameIterator.hasNext()){
				String falName=falnameIterator.next();
				FalObj falObj=new FalObj();
				falObj.falName=falName;
				falMap.put(falName, falObj);
			}
			System.out.println("Fal Map Size: "+ falMap.size());
			File file=new File(falSimFilePath);
			FileReader fReader=new FileReader(file);
			BufferedReader bReader=new BufferedReader(fReader);
			String line="";
			int index=0;
			while((line=bReader.readLine())!=null){
				if(line.contains("/")){
					int k=line.lastIndexOf(",");
					int m=line.lastIndexOf("/");
					String name=line.substring(k+1, m);
					falMap.get(name).addOneAPK(line);
					index ++;
					System.out.println(index);
				}
				else{
					System.err.println(index);
				}
			}
			bReader.close();
			fReader.close();
			/*
			 *   输出每个家族的样本个数
			 */
			Iterator<String> fal=falMap.keySet().iterator();
			while(fal.hasNext()){
				String name=fal.next();
				int size=falMap.get(name).apkList.size();
				System.out.println("Fal:"+name+" size: "+size);
			}
			/*
			 *    存储每个家族内部相似性关系
			 */
			Iterator<String> falMapIterator=falMap.keySet().iterator();
			while(falMapIterator.hasNext()){
				String name=falMapIterator.next();
				FalObj falObj=falMap.get(name);
				falObj.calAPKSim();
//				falObj.storeAPKSimPathGexf(writeFalSim+name+".gexf");
//				falObj.storeAPKSimPathDot(writeFalSim+name+".dot");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
