package Test;

import java.util.ArrayList;

import APKData.SmaliGraph.GexfToGraph;
import APKData.SmaliGraph.MethodGraph;
import CandicateFamily.FamilyWeightScore;
import GraphSimilarity.Dijkstra;

public class testDijkstra {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
              MethodGraph graph=new MethodGraph();
              GexfToGraph gexfToGraph=new GexfToGraph("/home/fan/lab/FamilyClassification/test/0.110--3.399--13.gexf");
              graph=gexfToGraph.getGraph();
              String weightScoreFilePath="/home/fan/lab/FamilyClassification/data/ADRD/FamilyInfo/MethodWeight.txt";
              FamilyWeightScore familyWeightScore=new FamilyWeightScore(weightScoreFilePath);
              
              String imNodeCommonString="android/telephony/gsm/SmsManager: "
              		+ "getDefault()android/telephony/gsm/SmsManager,Source,NETWORK_INFORMATION";
              ArrayList<String> oVStrings=new ArrayList<>();
              oVStrings.add("android/content/Intent: getAction()java/lang/String,Source,NO_CATEGORY");
              oVStrings.add("AAA");
              oVStrings.add("android/telephony/TelephonyManager: getSubscriberId()java/lang/String,Source,UNIQUE_IDENTIFIER");
              oVStrings.add("android/net/NetworkInfo: getExtraInfo()java/lang/String,Source,NETWORK_INFORMATION");
              Dijkstra dijkstra=new Dijkstra(graph, familyWeightScore.getMethodScoreMap(), imNodeCommonString, oVStrings);
              ArrayList<Double> scoreList=new ArrayList<>();
              scoreList=dijkstra.getCorScoreList();
              for(int i=0;i<scoreList.size();i++){
            	  System.out.println(scoreList.get(i));
              }
	}

}
