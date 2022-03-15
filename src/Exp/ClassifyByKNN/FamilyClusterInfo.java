package Exp.ClassifyByKNN;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import GraphSimilarity.CommunitySubGraph;
import SubgraphsInDifferentFamily.sigGraphAPK;
import SubgraphsInDifferentFamily.sigsubgraphInOneFal;

public class FamilyClusterInfo {
	private String familyName;
	private String familyDirPath;
	private Map<CommunitySubGraph, Integer> sigGraphMap = new HashMap<>();
	private Map<String, sigGraphAPK> ACMap = new HashMap<>();
	private int sampleNum = -1;

	public FamilyClusterInfo() {
	}

	public FamilyClusterInfo(String familyDirPath, String communityTypeFile) {
		this.familyDirPath = familyDirPath;
		File family = new File(familyDirPath);
		this.familyName = family.getName();
		File apktool = new File(familyDirPath + "apktool/");
		this.sampleNum = apktool.listFiles().length;
		/*
		 * Store all saliency subgraphs in the family and their corresponding support in
		 * sigGraphMap
		 */
		String clusterFile = this.familyDirPath + "FamilyInfo/" + communityTypeFile + "/";
		sigsubgraphInOneFal sigFal = new sigsubgraphInOneFal(clusterFile);
		sigGraphMap = sigFal.getSiggraphNumMap();
		/*
		 * Store the correspondence between all samples in the family and the saliency
		 * submap
		 */
		iniACMap(clusterFile);
	}

	public void iniACMap(String clusterFile) {
		try {
			String acmapFilePath = clusterFile + "ACmap.txt";
			File acMapFile = new File(acmapFilePath);
			FileReader fReader = new FileReader(acMapFile);
			BufferedReader bReader = new BufferedReader(fReader);
			String lineString = "";
			while ((lineString = bReader.readLine()) != null) {
				if (lineString.contains(".gexf")) {
					String regex = "(.*?)#(.*?)#(.*)";
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(lineString);
					if (matcher.find()) {
						String apkFilePath = matcher.group(1);
						String siggraphNo = matcher.group(2);
						String siggraphName = matcher.group(3);
						if (ACMap.containsKey(apkFilePath)) {
							String siggraphFilePath = clusterFile + "Cluster/" + siggraphName;
							CommunitySubGraph subGraph = new CommunitySubGraph(siggraphFilePath);
							ACMap.get(apkFilePath).addOneSigGraph(subGraph);
						} else {
							sigGraphAPK sApk = new sigGraphAPK(apkFilePath);
							String siggraphFilePath = clusterFile + "Cluster/" + siggraphName;
							CommunitySubGraph subGraph = new CommunitySubGraph(siggraphFilePath);
							sApk.addOneSigGraph(subGraph);
							ACMap.put(apkFilePath, sApk);
						}
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

	public void showACmap() {
		Iterator<String> nameIterator = ACMap.keySet().iterator();
		while (nameIterator.hasNext()) {
			String apkName = nameIterator.next();
			String line = ACMap.get(apkName).getInfo();
			// System.out.println(line);
		}
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getFamilyDirPath() {
		return familyDirPath;
	}

	public void setFamilyDirPath(String familyDirPath) {
		this.familyDirPath = familyDirPath;
	}

	public Map<CommunitySubGraph, Integer> getSigGraphMap() {
		return sigGraphMap;
	}

	public void setSigGraphMap(Map<CommunitySubGraph, Integer> sigGraphMap) {
		this.sigGraphMap = sigGraphMap;
	}

	public Map<String, sigGraphAPK> getACMap() {
		return ACMap;
	}

	public void setACMap(Map<String, sigGraphAPK> aCMap) {
		ACMap = aCMap;
	}

	public int getSampleNum() {
		return sampleNum;
	}

	public void setSampleNum(int sampleNum) {
		this.sampleNum = sampleNum;
	}

}
