package GraphSimilarity;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ConstantVar.ConstantValue;

public class SubGraphSet {
	private ArrayList<CommunitySubGraph> graphList = new ArrayList<>();
	private int size = 0;
	private double totalSensitiveScore = 0.0D;
	private String graphSetFilePath = "";
	private String apkName = "";

	public SubGraphSet() {

	}

	public SubGraphSet(String graphSetFilePath) {
		this.graphSetFilePath = graphSetFilePath;
		iniGraphSet(graphSetFilePath);
		this.size = graphList.size();
	}

	public void iniGraphSet(String inputDirPath) {
		// System.out.println("[SubGraphSet] Checking dir " + inputDirPath);
		try {
			File file = new File(inputDirPath);
			File gexfFile[] = file.listFiles();
			for (int i = 0; i < gexfFile.length; i++) {
				if (satisfyMinScore(gexfFile[i].getName())) {
					String gexfFilePath = gexfFile[i].getAbsolutePath();
					CommunitySubGraph subGraph = new CommunitySubGraph(gexfFilePath);
					// Set the name of the apk for each subgraph
					subGraph.setApkName(this.apkName);
					this.graphList.add(subGraph);
					this.totalSensitiveScore += subGraph.getSensitiveScore();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.exit(0);
		}
	}

	public boolean satisfyMinScore(String fileName) {
		boolean satisfy = false;
		double avgScore = -1.0D;
		double totalScore = -1.0D;
		String regex = "(.*?)--(.*?)--(.*)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(fileName);
		if (matcher.find()) {
			avgScore = Double.valueOf(matcher.group(1));
			totalScore = Double.valueOf(matcher.group(2));
		} else {
			System.err.println("The file name is wrong！！！");
		}
		if (avgScore > ConstantValue.getVar().minAvgGraphScore &&
				totalScore > ConstantValue.getVar().minTotalGraphScore) {
			satisfy = true;
		}
		return satisfy;
	}

	public ArrayList<CommunitySubGraph> getGraphList() {
		return graphList;
	}

	public void setGraphList(ArrayList<CommunitySubGraph> graphList) {
		this.graphList = graphList;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public double getTotalSensitiveScore() {
		return totalSensitiveScore;
	}

	public void setTotalSensitiveScore(double totalSensitiveScore) {
		this.totalSensitiveScore = totalSensitiveScore;
	}

	public String getInfomation() {
		String result = "";
		DecimalFormat df = new DecimalFormat("######0.000");
		String value = df.format(totalSensitiveScore);
		result += this.graphSetFilePath + "," + value + "," + size;
		return result;
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

}
