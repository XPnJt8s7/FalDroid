package CommunityStructure.GraphData;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import APKData.SmaliGraph.DotToGraph;
import APKData.SmaliGraph.MethodEdge;
import CandicateFamily.FamilyWeightScore;
import Util.Tool.DirDelete;

/*
 *          The input is a List of community division results, and it is divided into several subgraphs
 *          Stored in the graphScoreMap object, the key represents the subgraph, 
 *          and the value represents the sensitivity coefficient of the subgraph
 * 
 */
public class CommunityToGraphSet {
	private Map<CommunitySG, Double> graphScoreMap = new HashMap<>();
	private String srcGraphDotFilePath = "";
	private String weightScoreFilePath = "";

	public CommunityToGraphSet(ArrayList<MethodMolNo> molList, int comNum, String srcGraphDotFilePath,
			String weightScoreFilePath) {
		this.srcGraphDotFilePath = srcGraphDotFilePath;
		// iniWeightScoreFilePath();
		this.weightScoreFilePath = weightScoreFilePath;
		decomposeMolNoList(molList, comNum);
	}

	public void iniWeightScoreFilePath() {
		int k = srcGraphDotFilePath.indexOf("/apktool");
		String familyDirPath = srcGraphDotFilePath.substring(0, k);
		this.weightScoreFilePath = familyDirPath + "/FamilyInfo/MethodWeight.txt";
	}

	public CommunityToGraphSet() {
	}

	/*
	 * This function parses the input community division result list to obtain
	 * several subgraphs, and
	 * the sensitivity coefficient of each subgraph is calculated
	 */
	public void decomposeMolNoList(ArrayList<MethodMolNo> molList, int comNum) {
		ArrayList<CommunitySG> subgraphList = new ArrayList<>();
		for (int i = 0; i < comNum; i++) {
			CommunitySG communitySubGraph = new CommunitySG(String.valueOf(i + 1));
			subgraphList.add(communitySubGraph);
		}
		for (int i = 0; i < molList.size(); i++) {
			MethodMolNo molNo = molList.get(i);
			String no = molNo.getMolNo();
			int index = Integer.valueOf(no) - 1;
			subgraphList.get(index).addOneMethod(molNo);
		}
		DotToGraph dotToGraph = new DotToGraph(srcGraphDotFilePath);
		Set<MethodEdge> srcEdgeSet = new HashSet<>();
		srcEdgeSet = dotToGraph.getGraph().getEdgeSet();
		for (int i = 0; i < comNum; i++) {
			subgraphList.get(i).iniSubgraph(srcEdgeSet);
			FamilyWeightScore weightScore = new FamilyWeightScore(weightScoreFilePath);
			subgraphList.get(i).iniSensitivityCoefficient(weightScore);
			/*
			 * add the graph to Map
			 */
			graphScoreMap.put(subgraphList.get(i), subgraphList.get(i).getSensitivityScore());
		}

	}

	public void writeToFile(String subgraphSetDirPath) {
		try {
			File file = new File(subgraphSetDirPath);
			if (!file.exists()) {
				file.mkdirs();
			} else {
				DirDelete delete = new DirDelete();
				delete.deleteDir(file);
				file.mkdirs();
			}
			Iterator<CommunitySG> subgraphIterator = graphScoreMap.keySet().iterator();

			while (subgraphIterator.hasNext()) {
				CommunitySG subGraph = new CommunitySG();
				subGraph = subgraphIterator.next();
				double score = graphScoreMap.get(subGraph);
				double avg_score = score / (subGraph.getSubgraph().getNodeSet().size());

				DecimalFormat df = new DecimalFormat("######0.000");
				String value = df.format(score);
				String avg_value = df.format(avg_score);
				String subNoString = subGraph.getNoString();
				String writeString = subgraphSetDirPath + "/" + avg_value + "--" + value + "--" + subNoString + ".gexf";
				subGraph.writeToGexfFile(writeString);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void printGraphSetInfo() {
		double csgSensitivityScore;
		CommunitySG cSG;
		int n = 0;
		for (Map.Entry<CommunitySG, Double> entry : graphScoreMap.entrySet()) {
			n++;
			cSG = entry.getKey();
			csgSensitivityScore = entry.getValue();
			System.out.println(
					String.format("[CommunityToGraphSet] Graph %d sensitivity score: %f", n, csgSensitivityScore));
			cSG.printCommunitySGInfo();
		}
		System.out.println("[CommunityToGraphSet] Total subraphs: " + n);
	}
}
