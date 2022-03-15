package Journal.APKSim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ConstantVar.ConstantValue;

public class APKObj {
	public String inputString="";
	public String apkFileID="";
	public ArrayList<Double> vectorList=new ArrayList<>();
	public Map<String, Double> apkSimMap=new HashMap<>(); //  邻居节点
	public APKObj(){
		
	}
	public boolean containSimAPK(APKObj apkObj){
		boolean contain=false;
		Iterator<String> apkSimIterator=apkSimMap.keySet().iterator();
		while(apkSimIterator.hasNext()){
			String apkName=apkSimIterator.next();
			if(apkName==apkObj.apkFileID){
				contain=true;
				break;
			}
		}
		return contain;
	}
	public double getMaxSim(){
		double max=0.0D;
		Iterator<String> simIterator=apkSimMap.keySet().iterator();
		while(simIterator.hasNext()){
			String apkName=simIterator.next();
			double tmp=apkSimMap.get(apkName);
			max +=tmp;
		}
		return max;
	}
	public APKObj(String inputString){
		try {
			if(inputString.contains(".apk")){
				String str[]=inputString.split(",");
				int k=str.length;
				apkFileID=str[k-1];
				for(int i=0;i<k-1;i++){
					Double tmp=Double.valueOf(str[i]);
					vectorList.add(tmp);
				}
			}
			else{
				System.err.println("Not a APK vector!");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public double addOneSimAPK(APKObj dstAPK){
		double sim=calCosSim(vectorList, dstAPK.vectorList);
		try {
			if(sim>=ConstantValue.getVar().APKSIM){
				/*
				 *   确认这两个APK是相似的
				 */
				apkSimMap.put(dstAPK.apkFileID, sim);
				System.out.println("One more edge: "+apkFileID+"---"+dstAPK.apkFileID);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return sim;
	}
	public double calCosSim(ArrayList<Double> srcList, ArrayList<Double> dstList){
		double sim=0.0D;
		
		if(srcList.size()!=dstList.size()){
			System.err.println("匹配列表不一致，无法计算相似性！");
		}
		else{
			// 计算分子  
			double fenzi=0.0D;
			for(int i=0;i<srcList.size();i++){
				fenzi +=srcList.get(i)*dstList.get(i);
			}
			double fenmu=0.0D;
			double srcQua=0.0D;
			for(int i=0;i<srcList.size();i++){
				srcQua += srcList.get(i)*srcList.get(i);
			}
			srcQua=Math.sqrt(srcQua);
			double dstQua=0.0D;
			for(int i=0;i<dstList.size();i++){
				dstQua +=dstList.get(i)*dstList.get(i);
			}
			dstQua=Math.sqrt(dstQua);
			fenmu=srcQua * dstQua;
			sim=fenzi/fenmu;
		
		}
		return sim;
	}
}
