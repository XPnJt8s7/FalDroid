package Journal.APKSim.StatisticOneFal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class APKScore {
	public Map<String, Double> apkScoreMap=new HashMap<>();
	
	public APKScore(){
		
	}
	public APKScore(String scoreFilePath){
		try {
			File file=new File(scoreFilePath);
			FileReader fReader=new FileReader(file);
			BufferedReader bReader=new BufferedReader(fReader);
			String line="";
			while((line=bReader.readLine())!=null){
				String str[]=line.split(":");
				String name=str[0];
				String score=str[1];
				Double scoreDouble=Double.valueOf(score);
				apkScoreMap.put(name, scoreDouble);
			}
			
			bReader.close();
			fReader.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
