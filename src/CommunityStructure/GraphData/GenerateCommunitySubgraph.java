package CommunityStructure.GraphData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ConstantVar.ConstantValue;

/*
 *       The input is the decompiled folder of an apk file, and the four division result files obtained by four different community division algorithms are analyzed.
 *       SICG/Community/Community_Result_fg.csv
 *       SICG/Community/Community_Result_im.csv
 *       SICG/Community/Community_Result_lp.csv
 *       SICG/Community/Community_Result_ml.csv
 *       Analyze it and generate a corresponding folder for each division algorithm, which stores the subgraph set obtained by the community division algorithm. The name of each subgraph file contains its sensitivity coefficient.
 */
public class GenerateCommunitySubgraph {
	private String srcAPKFilePath = "";
	// private double molFgScore=0.0D;
	// private int comFgNum=0;
	private double molImScore = 0.0D;
	private int comImNum = 0;
	// private double molLpScore=0.0D;
	// private int comLpNum=0;
	// private double molMlScore=0.0D;
	// private int comMlNum=0;

	// private ArrayList<MethodMolNo> fgMolNoList=new ArrayList<>();
	private ArrayList<MethodMolNo> imMolNoList = new ArrayList<>();
	// private ArrayList<MethodMolNo> lpMolNoList=new ArrayList<>();
	// private ArrayList<MethodMolNo> mlMolNoList=new ArrayList<>();

	// private CommunityToGraphSet graphSetFg=new CommunityToGraphSet();
	private CommunityToGraphSet graphSetIm = new CommunityToGraphSet();
	// private CommunityToGraphSet graphSetLp=new CommunityToGraphSet();
	// private CommunityToGraphSet graphSetMl=new CommunityToGraphSet();
	private Boolean successCommunitySubraph = false;

	public GenerateCommunitySubgraph(String srcAPKFileString, String weightScoreFilePath) {
		this.successCommunitySubraph = false;
		this.srcAPKFilePath = srcAPKFileString;
		if (CheckCommunityResult() == true) {
			String srcDotGraphFileString = srcAPKFileString + "SICG/ReducedGraph.dot";
			// graphSetFg=new CommunityToGraphSet(fgMolNoList, comFgNum,
			// srcDotGraphFileString, weightScoreFilePath);
			graphSetIm = new CommunityToGraphSet(imMolNoList, comImNum, srcDotGraphFileString, weightScoreFilePath);
			if (ConstantValue.isVerbose()) {
				System.out.println("\n[GenerateCommunitySubgraph] Finished generating subgraphs, printing information");
				graphSetIm.printGraphSetInfo();
			}
			// graphSetLp=new CommunityToGraphSet(lpMolNoList, comLpNum,
			// srcDotGraphFileString, weightScoreFilePath);
			// graphSetMl=new CommunityToGraphSet(mlMolNoList, comMlNum,
			// srcDotGraphFileString, weightScoreFilePath);
			writeToDir();
			this.successCommunitySubraph = true;
		} else {
			System.err.println("[ERROR][GenerateCommunitySubgraph] File missing in: " + srcAPKFileString);
			// System.exit(-2);
		}
	}

	public Boolean getSuccessCommunitySubgraph() {
		return successCommunitySubraph;
	}

	public void writeToDir() {
		String communityPath = this.srcAPKFilePath + "SICG/Community/";
		// String subgraphSetFgDir=communityPath+"Community_Fg/";
		String subgraphSetImDir = communityPath + "Community_Im/";
		// String subgraphSetLpDir=communityPath+"Community_Lp/";
		// String subgraphSetMlDir=communityPath+"Community_Ml/";

		// graphSetFg.writeToFile(subgraphSetFgDir);
		graphSetIm.writeToFile(subgraphSetImDir);
		// graphSetLp.writeToFile(subgraphSetLpDir);
		// graphSetMl.writeToFile(subgraphSetMlDir);
	}

	public CommunityToGraphSet transFileToGraph(ArrayList<MethodMolNo> molList, int comNum, String srcGraphDotFilePath,
			String weightScoreFilePath) {
		CommunityToGraphSet communityToGraph = new CommunityToGraphSet(molList, comNum, srcGraphDotFilePath,
				weightScoreFilePath);
		return communityToGraph;
	}

	public boolean CheckCommunityResult() {
		boolean check = false;
		// String communityFG=srcAPKFilePath+"SICG/Community/Community_Result_fg.csv";
		String comminityIM = srcAPKFilePath + "SICG/Community/Community_Result_im.csv";
		// String comminityLP=srcAPKFilePath+"SICG/Community/Community_Result_lp.csv";
		// String comminityML=srcAPKFilePath+"SICG/Community/Community_Result_ml.csv";
		// File comFG=new File(communityFG);
		File comIM = new File(comminityIM);
		// pressAnyKeyToContinue();
		// File comLP=new File(comminityLP);
		// File comML=new File(comminityML);
		if (comIM.exists()) {
			check = true;
			// this.fgMolNoList=OneCommunityFile(comFG);
			this.imMolNoList = OneCommunityFile(comIM);
			// this.lpMolNoList=OneCommunityFile(comLP);
			// this.mlMolNoList=OneCommunityFile(comML);
		} else {
			System.out.println("[ERROR][GenerateCommunitySubgraph] Cannot continue without file " + comminityIM);
		}
		return check;
	}

	public ArrayList<MethodMolNo> OneCommunityFile(File communityFile) {
		String fileName = communityFile.getName();
		double molScore = 0.0D;
		int comNum = 0;
		ArrayList<MethodMolNo> molNoList = new ArrayList<>();
		try {
			FileReader fReader = new FileReader(communityFile);
			BufferedReader bReader = new BufferedReader(fReader);
			String lineString = "";
			lineString = bReader.readLine();
			String regex = "(.*?)Score:(.*?),Community Number:(.*)";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(lineString);
			if (matcher.find()) {
				String score = matcher.group(2);
				String num = matcher.group(3);
				molScore = Double.valueOf(score);
				comNum = Integer.valueOf(num);
			} else {
			}
			// if(fileName.contains("Result_fg")){
			// this.molFgScore=molScore; this.comFgNum=comNum;
			// }
			if (fileName.contains("Result_im")) {
				this.molImScore = molScore;
				this.comImNum = comNum;
			}
			// if(fileName.contains("Result_lp")){
			// this.molLpScore=molScore; this.comLpNum=comNum;
			// }
			// if(fileName.contains("Result_ml")){
			// this.molMlScore=molScore; this.comMlNum=comNum;
			// }

			while ((lineString = bReader.readLine()) != null) {
				MethodMolNo molNo = new MethodMolNo(lineString);
				molNoList.add(molNo);
			}
			bReader.close();
			fReader.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return molNoList;
	}
}
