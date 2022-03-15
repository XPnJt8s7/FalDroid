package CommunityStructure.CommunityDetection;

import java.io.File;

import CommunityStructure.GraphData.GenerateCommunitySubgraph;
import Util.Tool.DirDelete;

public class OneObjectCommunityDetection {
	private String inputAPKFilePath;
	private String weightScoreFilePath;

	public OneObjectCommunityDetection(String inputFilePath, String weightScoreFilePath) {
		this.inputAPKFilePath = inputFilePath;
		this.weightScoreFilePath = weightScoreFilePath;
		/*
		 * execute python file
		 */
		/*
		 * The original graph is divided into several subgraphs using the community
		 * division algorithm
		 */
		CommunityFinding communityFinding = new CommunityFinding(inputFilePath);
		/*
		 * Store the division result in the corresponding folder
		 */
		GenerateCommunitySubgraph generateCommunitySubgraph = new GenerateCommunitySubgraph(inputFilePath,
				weightScoreFilePath);
		/*
		 * Delete redundant files, keep Community_Im and Community_Result_im.csv
		 */
		deleteMoreFile(inputFilePath);
	}

	public void deleteMoreFile(String inputFileString) {
		File file = new File(inputFileString + "SICG/Community/");
		File subfile[] = file.listFiles();
		for (int i = 0; i < subfile.length; i++) {
			String filePath = subfile[i].getAbsolutePath();
			if (filePath.contains("Community_Im") || filePath.contains("Community_Result_im.csv")) {

			} else {
				DirDelete delete = new DirDelete();
				delete.deleteDir(subfile[i]);
			}
		}

	}
}
