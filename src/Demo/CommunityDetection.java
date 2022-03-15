package Demo;

import java.io.File;

import CommunityStructure.CommunityDetection.OneObjectCommunityDetection;

public class CommunityDetection {

	public static String inputFilePath = "/home/fan/data/Family/small-sample-exp/exp1/test/smsreg/apktool/dbfab4abbf46f005c216acce199d4450.apk";

	// public static String inputFilePath="";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int k = args.length;
		if (k == 0) {
			System.err.println("There is no argument!");
		} else {
			inputFilePath = args[0];
			detection(inputFilePath);
		}
		detection(inputFilePath);
	}

	public static void detection(String inputFilePath) {
		int k = inputFilePath.lastIndexOf("/");
		String outPutFileDir = inputFilePath.substring(0, k) + "/apktool/";
		String apkName = inputFilePath.substring(k + 1);
		outPutFileDir += apkName + "/";

		String weightScoreFilePath = "/home/fan/data/Family/small-sample-exp/exp1/train/geinimi/FamilyInfo/MethodWeight.txt";
		long startTime = System.currentTimeMillis();
		OneObjectCommunityDetection oneObjectCommunityDetection = new OneObjectCommunityDetection(outPutFileDir,
				weightScoreFilePath);
		long endTime = System.currentTimeMillis();
		long useTime = endTime - startTime;
	}

}
