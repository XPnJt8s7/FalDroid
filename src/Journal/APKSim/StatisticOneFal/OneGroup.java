package Journal.APKSim.StatisticOneFal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;


public class OneGroup {

		public ArrayList<String> apkIDList=new ArrayList<>();
		public double maxSImValue=0.0D;
		
		
		public OneGroup(){
			
		}
		public OneGroup(File file){
			try {
				FileReader fReader=new FileReader(file);
				BufferedReader bReader=new BufferedReader(fReader);
				String line="";
				while((line=bReader.readLine())!=null){
					if(line.contains(".apk") && !line.contains("->")){
						int start=line.indexOf("\"");
						int end=line.lastIndexOf("\"");
						String ID=line.substring(start+1, end);
						apkIDList.add(ID);
					}
				}
				
				bReader.close();
				fReader.close();
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public void setMaxSimValue(APKScore apkScore){
			double max=-1.0D;
			for(int i=0;i<apkIDList.size();i++){
				String ID=apkIDList.get(i);
				double tmp=apkScore.apkScoreMap.get(ID);
				if(tmp>max){
					max=tmp;
				}
			}
			maxSImValue=max;
		}
		
		
		
}
