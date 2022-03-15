package GraphSimilarity;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import APKData.SmaliGraph.GexfToGraph;
import APKData.SmaliGraph.MethodGraph;

public class CommunitySubGraph implements Serializable {
	private MethodGraph graph = new MethodGraph();
	private double SensitiveScore = 0.0D;
	private double avgSensitiveScore = 0.0D;
	private int graphNo = -1;
	private String fileName = "";
	private String filePath = "";
	private String apkName = "";

	public CommunitySubGraph() {
	}

	public CommunitySubGraph(String subgraphGexfFilePath) {
		GexfToGraph gexfToGraph = new GexfToGraph(subgraphGexfFilePath);
		this.graph = gexfToGraph.getGraph();
		this.filePath = subgraphGexfFilePath;
		if (filePath.contains("SICG")) {
			// If there is SICG in the path, extract the apkName
			int k = filePath.indexOf("SICG");
			this.apkName = this.filePath.substring(0, k);
		}
		try {
			File file = new File(subgraphGexfFilePath);
			iniScoreInfo(file.getName());
			this.fileName = file.getName();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void iniScoreInfo(String inputFileName) {
		String regex = "(.*?)--(.*?)--(.*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(inputFileName);
		if (matcher.find()) {
			this.avgSensitiveScore = Double.valueOf(matcher.group(1));
			this.SensitiveScore = Double.valueOf(matcher.group(2));
			String noTailString = matcher.group(3);
			int k = noTailString.indexOf(".");
			this.graphNo = Integer.valueOf(noTailString.substring(0, k));
		}
	}

	public void show() {
		System.out.println("Avg-Score: " + this.avgSensitiveScore);
		System.out.println("Total-Score: " + this.SensitiveScore);
		System.out.println("GraphNo: " + this.graphNo);
	}

	public MethodGraph getGraph() {
		return graph;
	}

	public void setGraph(MethodGraph graph) {
		this.graph = graph;
	}

	public double getSensitiveScore() {
		return SensitiveScore;
	}

	public void setSensitiveScore(double sensitiveScore) {
		SensitiveScore = sensitiveScore;
	}

	public double getAvgSensitiveScore() {
		return avgSensitiveScore;
	}

	public void setAvgSensitiveScore(double avgSensitiveScore) {
		this.avgSensitiveScore = avgSensitiveScore;
	}

	public int getGraphNo() {
		return graphNo;
	}

	public void setGraphNo(int graphNo) {
		this.graphNo = graphNo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		CommunitySubGraph dstGraph = (CommunitySubGraph) obj;
		if (this.filePath.equals(dstGraph.filePath)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.filePath.toString();
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return this.filePath.hashCode();
	}

	public void printCommunitySubGraphInfo() {
		System.out.println("[CommunitySubGraph] Filepath: " + filePath);
		System.out.println("[CommunitySubGraph] Graph number: " + graphNo);
		System.out.println("[CommunitySubGraph] Sensitivity score: " + SensitiveScore);
		System.out.println("[CommunitySubGraph] Avg. sensitivity score: " + avgSensitiveScore);
	}
}
