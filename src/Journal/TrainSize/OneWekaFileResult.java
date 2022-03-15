package Journal.TrainSize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class OneWekaFileResult {
		public ArrayList<String> SVMList=new ArrayList<>();
		public ArrayList<String> RFList=new ArrayList<>();
		public ArrayList<String> J48List=new ArrayList<>();
		public ArrayList<String> KNNList=new ArrayList<>();
		
		public OneWekaFileResult(){
			
		}
		public OneWekaFileResult(String inputFile){
			try {
				File file=new File(inputFile);
				FileReader fReader=new FileReader(file);
				BufferedReader bReader=new BufferedReader(fReader);
				String line="";
				while((line=bReader.readLine())!=null){
					if(line.contains("SVM")){
						String str[]=line.split("---");
						SVMList.add(str[1]);
					}
					if(line.contains("RF")){
						String str[]=line.split("---");
						RFList.add(str[1]);
					}
					if(line.contains("J48")){
						String str[]=line.split("---");
						J48List.add(str[1]);
					}
					if(line.contains("KNN")){
						String str[]=line.split("---");
						KNNList.add(str[1]);
					}
				}
				bReader.close();
				fReader.close();
				
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
		
	}
