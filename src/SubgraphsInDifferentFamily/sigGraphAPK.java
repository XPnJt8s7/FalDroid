package SubgraphsInDifferentFamily;

import java.util.ArrayList;

import GraphSimilarity.CommunitySubGraph;

/*
 *     Stores sensitive subgraphs of all calls corresponding to a sample
 */
public class sigGraphAPK {
	private String apkFilePath = "";
	private ArrayList<CommunitySubGraph> invokeSigGraphList = new ArrayList<>();
	private double totalScore = 0.0D;

	public sigGraphAPK() {
	}

	public sigGraphAPK(String apkFilePath) {
		this.apkFilePath = apkFilePath;
	}

	public void addOneSigGraph(CommunitySubGraph communitySubGraph) {
		this.invokeSigGraphList.add(communitySubGraph);
		totalScore += communitySubGraph.getSensitiveScore();
	}

	public String getInfo() {
		String resultString = "";
		resultString += this.apkFilePath + "\n";
		for (int i = 0; i < invokeSigGraphList.size(); i++) {
			resultString += "	" + invokeSigGraphList.get(i).getFilePath() + "\n";
		}
		return resultString;
	}

	public String getApkFilePath() {
		return apkFilePath;
	}

	public void setApkFilePath(String apkFilePath) {
		this.apkFilePath = apkFilePath;
	}

	public ArrayList<CommunitySubGraph> getInvokeSigGraphList() {
		return invokeSigGraphList;
	}

	public void setInvokeSigGraphList(ArrayList<CommunitySubGraph> invokeSigGraphList) {
		this.invokeSigGraphList = invokeSigGraphList;
	}

	public double getTotalScore() {
		return totalScore;
	}

	public void setTotalScore(double totalScore) {
		this.totalScore = totalScore;
	}

}
