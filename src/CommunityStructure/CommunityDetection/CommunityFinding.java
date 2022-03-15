package CommunityStructure.CommunityDetection;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import ConstantVar.ConstantValue;

/*
 *     The input is the path to the decompiled folder of an object, and the ReducedGraph.dot in the SICG folder is used as input
 *     Run the community discovery algorithm, respectively
 *     infomap
 *     fast greedy 
 *     propagation
 *     multilevel
 *     Obtain the results of four community divisions
 */
public class CommunityFinding {
	private String srcFilePathString = "";
	private String outPutPathString = "";

	public CommunityFinding(String srcFilePathString) {
		this.srcFilePathString = srcFilePathString + "SICG/ReducedGraph.dot";
		// this.srcFilePathString=srcFilePathString+"SICG/SourceGraph.dot";
		this.outPutPathString = srcFilePathString + "SICG/Community";
		File file = new File(outPutPathString);
		if (!file.exists()) {
			file.mkdir();
		}
		exePython(this.srcFilePathString, this.outPutPathString + "/");
	}

	public void exePython(String inputFileString, String outputFileString) {
		ConstantValue.getVar();
		String pythonScriptPath = ConstantValue.CONMMUNITYDETECTIONPYTHONFILEPATH_STRING;
		File pythonScript = new File(pythonScriptPath);
		if (!pythonScript.exists()) {
			System.err.println(
					"[ERROR][CommunityFinding] The Python script at " + pythonScriptPath + " doesn't exists, exiting");
			System.exit(-3);
		}
		String cmdString = "python2.7 " + pythonScriptPath + " " + inputFileString
				+ " " +
				outputFileString;
		// System.out.println("Command to execute: " + cmdString);
		exeCmd(cmdString);
	}

	public void exeCmd(String cmd) {
		Runtime rnRuntime = Runtime.getRuntime();
		String outInfo = "";
		try {
			Process process = rnRuntime.exec(cmd);
			InputStream in = (process).getErrorStream();// Get error message output.
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = br.readLine()) != null) {
				outInfo = outInfo + line + "\n";
			}
			System.out.println(outInfo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
