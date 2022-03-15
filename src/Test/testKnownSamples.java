package Test;

import java.util.ArrayList;

import Exp.HMRF.AllSamples;
import Exp.HMRF.KnownSamples;
import Exp.HMRF.TestSamples;

public class testKnownSamples {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
					String trainFilePath="/home/fan/lab/FamilyClassification/result/Im--0.6-train.txt";
					KnownSamples knownSamples=new KnownSamples(trainFilePath);
					String trainSimgraphFilePath="/home/fan/lab/FamilyClassification/result/trainSimgraph.gexf";
					knownSamples.getTrainSimgraph().writeSimGraphFile(trainSimgraphFilePath);
					String trainAvgSimilarityFilePath="/home/fan/lab/FamilyClassification/result/trainAvgSimFile.txt";
					knownSamples.writeFalAvgSimilarity(trainAvgSimilarityFilePath);
					String trainMaxSimilarityFilePath="/home/fan/lab/FamilyClassification/result/trainMaxSimFile.txt";
					knownSamples.writeFalMaxSimilarity(trainMaxSimilarityFilePath);
					
					String testFilePath="/home/fan/lab/FamilyClassification/result/Im--0.6-test.txt";
					TestSamples testSamples=new TestSamples(testFilePath);
					testSamples.iniProbability(knownSamples);
					System.out.println("TP:"+testSamples.getTP());
					ArrayList<String> fpList=new ArrayList<>();
					fpList=testSamples.getFPList();
					for(int i=0;i<fpList.size();i++){
						System.out.println(fpList.get(i));
					}
					testSamples.iniNeighbor();
					testSamples.transSimGraph();
					String simGraphFilePath="/home/fan/lab/FamilyClassification/result/testSimgraph.gexf";
					testSamples.getSimGraph().writeSimGraphFile(simGraphFilePath);
					System.out.println("Size:"+testSamples.getAllEdges());
//					
					AllSamples allSamples=new AllSamples(knownSamples,testSamples);
					allSamples.iniNeighbor();
					allSamples.transSimGraph();
					String allSimGraphFilePath="/home/fan/lab/FamilyClassification/result/allSimgraph.gexf";
					allSamples.getAllSimgraph().writeSimGraphFile(allSimGraphFilePath);

	}
}
