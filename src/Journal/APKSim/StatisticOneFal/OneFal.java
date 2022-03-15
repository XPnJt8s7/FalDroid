package Journal.APKSim.StatisticOneFal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OneFal {
	public ArrayList<OneGroup> groupList=new ArrayList<>();
	public String falFilePath="";
	public String falName="";
	public int SingleGroupNum=0;
	public int TwoGroupNum=0;
	public int ThreeGroupNum=0;
	public int BiggerThreeGroupNum=0;
	public int APKSumNum=0;
	
	public OneFal(){
		
	}
	public OneFal(String dirFilePath){
		this.falFilePath=dirFilePath;
		File file=new File(dirFilePath);
		String name=file.getName();
		int k=name.indexOf("-");
		name=name.substring(0,k);
		falName=name;
		
	}
	public void iniGroups(APKScore apkScore){
		try {
			File file=new File(falFilePath);
			File groups[]=file.listFiles();
			for(int i=0;i<groups.length;i++){
				OneGroup oneGroup=new OneGroup(groups[i]);
				oneGroup.setMaxSimValue(apkScore);
				
				groupList.add(oneGroup);
				if(oneGroup.apkIDList.size()==1){
					SingleGroupNum ++;
				}
				if(oneGroup.apkIDList.size()==2){
					TwoGroupNum ++;
				}
				if(oneGroup.apkIDList.size()==3){
					ThreeGroupNum ++;
				}
				if(oneGroup.apkIDList.size()>3){
					BiggerThreeGroupNum ++;
				}
				APKSumNum += oneGroup.apkIDList.size();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	@SuppressWarnings("unchecked")
	public String getResult(){
		String result="";
		result +=falName+"\n";
		result +="   "+"Group Num:"+groupList.size()+"\n";

		double reduceRadio=1.0D-(Double.valueOf(groupList.size())/
					Double.valueOf(APKSumNum));
		result +="   "+"Reduce Radio:"+reduceRadio+"\n";
		result +="   "+"Max Sim Value:\n";
		result +="   "+"Single Num:"+SingleGroupNum+"\n";
		result +="   "+"Two Num:" + TwoGroupNum+"\n";
		result +="   "+"Three Num:"+ThreeGroupNum+"\n";
		result +="   "+"More than Three Num:"+BiggerThreeGroupNum+"\n";
		
		Collections.sort(groupList, new Comparator() {

			@Override
			public int compare(Object o1, Object o2) {
				// TODO Auto-generated method stub
				OneGroup srcGroup= (OneGroup)o1;
				OneGroup dstGroup= (OneGroup)o2;
				int k=-1;
				if(srcGroup.maxSImValue>dstGroup.maxSImValue){
					k=1;
				}
				return k;
			}
		    });
		for(int i=0;i<groupList.size();i++){
			result +="     "+groupList.get(i).maxSImValue+"  ##  "+ groupList.get(i).apkIDList.size()+"\n";
		}
//		
//		for(int i=0;i<groupList.size();i++){
//			simValueList.add(groupList.get(i).maxSImValue);
//		}
//		Collections.sort(simValueList);
//		for(int i=0;i<simValueList.size();i++){
//			result +="     "+simValueList.get(i)+"\n";	
//		}
		return result;
	}
}
