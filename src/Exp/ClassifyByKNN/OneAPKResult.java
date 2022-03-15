package Exp.ClassifyByKNN;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ConstantVar.ConstantValue;

public class OneAPKResult {
			private String apkFilePath="";
			private String actualName="";
			private Map<String, Double> simFalScoreMap=new HashMap<>(); 
			private analysisOneAPK oneAPK;
			
			public OneAPKResult(String apkFilePath){
				this.apkFilePath=apkFilePath;
				int k=apkFilePath.lastIndexOf("/");
				String actualName=apkFilePath.substring(0,k);
				int k2=actualName.lastIndexOf("/");
				actualName=actualName.substring(k2+1);
				this.actualName=actualName;
				// 初始化样本，完成对样本的反编译以及社团划分
				iniData();
				
				File dataDir=new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
				File family[]=dataDir.listFiles();
				for(int i=0;i<family.length;i++){
					String familyDirFilePath=family[i].getAbsolutePath()+"/";
					oneFalAnalysis(familyDirFilePath);
				}
//				String familyDirFilePath="/home/fan/lab/FamilyClassification/trainData/FakePlayer/";
//				oneFalAnalysis(familyDirFilePath);
//				familyDirFilePath="/home/fan/lab/FamilyClassification/trainData/BaseBridge/";
//				oneFalAnalysis(familyDirFilePath);
			}
			public void iniData(){
				oneAPK=new analysisOneAPK(this.apkFilePath);
				oneAPK.disassemble(apkFilePath);
				oneAPK.communityDivide();
			}
			public void oneFalAnalysis(String familyDirFilePath){
				String scoreFilePath=familyDirFilePath+"FamilyInfo/MethodWeight.txt";
				oneAPK.createCommunityFiles(scoreFilePath);   //生成相应的子图文件夹
				oneAPK.reiniGraphList();
				
				oneAPK.storeAllSubgraphs("Im");
				FamilyClusterInfo familyClusterInfo=new FamilyClusterInfo(familyDirFilePath, "Im--0.33");                 
				oneAPK.calSimSiggraph(familyClusterInfo, scoreFilePath);
		//		oneAPK.showSimSigGraphMap(familyClusterInfo.getFamilyName());
				double simScore=oneAPK.simWithFalAPKs(familyClusterInfo);
				String falName=familyClusterInfo.getFamilyName();
				this.simFalScoreMap.put(falName, simScore);
			}
			public boolean showResult(){
				//System.out.println("Actual Name:"+this.actualName);
				String predictName="";
				double maxSim=-1.0D;
				Iterator<String> falIterator=simFalScoreMap.keySet().iterator();
				while(falIterator.hasNext()){
					String falName=falIterator.next();
					double score=simFalScoreMap.get(falName);
					if(score>maxSim){
						maxSim=score;
						predictName=falName;
					}
					System.out.println(falName+":"+score);
				}
				System.out.println("Actual Name:"+actualName+"### "+"Predict Name:"+predictName+"### "+maxSim);
				if(actualName.equals(predictName)){
					return true;
				}
				else{
					return false;
				}
			}
}
