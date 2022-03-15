package Test;

import java.util.Iterator;

import APKData.SmaliGraph.GexfToGraph;
import APKData.SmaliGraph.MethodGraph;
import CandicateFamily.FamilyWeightScore;
import GraphSimilarity.SubGraphSimilarity;
import sun.launcher.resources.launcher;

public class testOneNodeSim {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long starttime=System.currentTimeMillis();
				String srcGexfFilePath="/home/fan/lab/FamilyClassification/trainData/BaseBridge/FamilyInfo/Im--0.33/Cluster/0.010--0.202--2.gexf";
				String dstGexfFilePath="/home/fan/lab/FamilyClassification/trainData/DroidKungFu1/FamilyInfo/Im--0.33/Cluster/0.031--0.124--89.gexf";
				String familyWeightScoreFilePath="/home/fan/lab/FamilyClassification/trainData/DroidKungFu1/FamilyInfo/MethodWeight.txt";
				
				GexfToGraph srcGexfToGraph=new GexfToGraph(srcGexfFilePath);
				GexfToGraph dstGexfToGraph=new GexfToGraph(dstGexfFilePath);
				
				MethodGraph srcGraph=srcGexfToGraph.getGraph();
				MethodGraph dstGraph=dstGexfToGraph.getGraph();
				FamilyWeightScore weightScore=new FamilyWeightScore(familyWeightScoreFilePath);
				
				SubGraphSimilarity subGraphSimilarity=new SubGraphSimilarity(srcGraph, dstGraph, weightScore);
//				String imNodeCommonString="android/telephony/TelephonyManager: "
//						+ "getPhoneType()java.lang.Integer,Source,NETWORK_INFORMATION";
//				subGraphSimilarity.oneNodeSim(imNodeCommonString);
//				System.out.println();
//				System.out.println("AndSet: "+subGraphSimilarity.getAndSensitiveMethodSet().size());
//				System.out.println("OrSet: "+subGraphSimilarity.getOrSensitiveMethodSet().size());
				System.out.println(subGraphSimilarity.getSimScore());
				long endtime=System.currentTimeMillis();
				long usetime=endtime-starttime;
				System.out.println("Using time: "+usetime+"ms");
	}

}
