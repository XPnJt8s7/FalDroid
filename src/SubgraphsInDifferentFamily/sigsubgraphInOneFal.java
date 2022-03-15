package SubgraphsInDifferentFamily;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import GraphSimilarity.CommunitySubGraph;
/*
 *      输入为一个家族显著性子图分析结果的文件夹，例如ADRD/FamilyInfo/Im--0.5/
 * 
 */
public class sigsubgraphInOneFal {
		 private Map<CommunitySubGraph, Integer> siggraphNumMap=new HashMap<>();
		 private String familyName="";
		 private String weightScoreFile="";
		 private String familyClusterDir="";
		 
		 public sigsubgraphInOneFal(String familyClusterDir){
			 this.familyClusterDir=familyClusterDir;
			 iniFamilyName(familyClusterDir);
			 iniSiggraphNumMap(familyClusterDir);
		 }
		 public void iniFamilyName(String familyClusterDir){
			 int k=familyClusterDir.indexOf("/FamilyInfo");
			 String nameString=familyClusterDir.substring(0,k);
			 String weightScoreFilePath=nameString+"/FamilyInfo/MethodWeight.txt";
			 k=nameString.lastIndexOf("/");
			 nameString=nameString.substring(k+1);
			 this.familyName=nameString;
			 this.weightScoreFile=weightScoreFilePath;
		 }
		 public void iniSiggraphNumMap(String familyClusterDir){
			 try {
				String logFileString=familyClusterDir+"log.txt";
				File logFile=new File(logFileString);
				FileReader fReader=new FileReader(logFile);
				BufferedReader bReader=new BufferedReader(fReader);
				String lineStirng="";
				/*
				 *     分析Log文件日志，将每一个显著性子图以及其支持度存储到 siggraphNumMap中
				 */
				while((lineStirng=bReader.readLine())!=null){
					if(lineStirng.startsWith("Cluster")&&lineStirng.contains("#")){
						String regex="(.*?)#(.*?)#(.*)";
						Pattern pattern=Pattern.compile(regex);
						Matcher matcher=pattern.matcher(lineStirng);
						if(matcher.find()){
							String name=matcher.group(2);
							String num=matcher.group(3);
							name = familyClusterDir+"Cluster/"+name;
						//	System.out.println(name+"::"+num);
							CommunitySubGraph communitySubGraph=new CommunitySubGraph(name);
							Integer sigNum=Integer.valueOf(num);
							siggraphNumMap.put(communitySubGraph, sigNum);
						}
					}
				}
				bReader.close();
				fReader.close();
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		 }
		public Map<CommunitySubGraph, Integer> getSiggraphNumMap() {
			return siggraphNumMap;
		}
		public void setSiggraphNumMap(Map<CommunitySubGraph, Integer> siggraphNumMap) {
			this.siggraphNumMap = siggraphNumMap;
		}
		public String getFamilyName() {
			return familyName;
		}
		public void setFamilyName(String familyName) {
			this.familyName = familyName;
		}
		public String getWeightScoreFile() {
			return weightScoreFile;
		}
		public void setWeightScoreFile(String weightScoreFile) {
			this.weightScoreFile = weightScoreFile;
		}
		public String getFamilyClusterDir() {
			return familyClusterDir;
		}
		public void setFamilyClusterDir(String familyClusterDir) {
			this.familyClusterDir = familyClusterDir;
		}
		 
		 
		 
}
