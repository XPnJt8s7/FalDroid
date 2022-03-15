package CommunityStructure.CommunityDetection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import ConstantVar.ConstantValue;
import Util.PrintTime;

public class AllObjectCommunityDetection {
	public ArrayList<Double> comTimeList = new ArrayList<>();
	public String filePath = "./java_result/time/AllObjectCommunityDetection.txt";

	public AllObjectCommunityDetection() {
		GenerateAllCommunity();

		// writeFile(filePath);
	}

	public void writeFile(String filePath, String line) {
		try {
			File file = new File(filePath);

			String parentDirPath = file.getParent();
			File parentDir = new File(parentDirPath);
			if (!parentDir.exists()) {
				parentDir.mkdirs();
			}

			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter bWriter = new BufferedWriter(fileWriter);
			// for(int i=0;i<comTimeList.size();i++){
			// bWriter.write(comTimeList.get(i)+","+"\n");
			// }
			bWriter.write(line + "\n");
			bWriter.close();
			fileWriter.close();
			// System.out.println("[AllObjectCommunityDetection] File saved as " +
			// filePath);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void GenerateAllCommunity() {
		ConstantValue.getVar();
		String dataDirPathString = ConstantValue.FAMILIESDIRPATH_STRING;
		System.out.println("[AllObjectCommunityDetection] Using directory: " + dataDirPathString);
		// System.exit(0);
		File file = new File(dataDirPathString);
		File family[] = file.listFiles();
		for (int i = 0; i < family.length; i++) {
			// if(family[i].getAbsolutePath().contains("fakeinst")){
			String apkFilePathString = family[i].getAbsolutePath() + "/apktool/";
			String weightScoreFilePath = family[i].getAbsolutePath() + "/FamilyInfo/MethodWeight.txt";

			// String weightScoreFilePath =
			// "/home/fan/lab/Family/extend/train/droidkungfu/FamilyInfo/MethodWeight.txt";
			File apktoolFile = new File(apkFilePathString);
			File apks[] = apktoolFile.listFiles();
			for (int j = 0; j < apks.length; j++) {
				// if(apks[j].getName().contains("0a1b0cc8b30533f5919223a371a04590.apk")){
				String inputFilePath = apks[j].getAbsolutePath() + "/";
				System.out.println("FamilyNum: " + i + "  APKNum: " + j + "      " + inputFilePath);
				long startTime = System.currentTimeMillis();
				OneObjectCommunityDetection oneObjectCommunityDetection = new OneObjectCommunityDetection(inputFilePath,
						weightScoreFilePath);
				long endTime = System.currentTimeMillis();
				long useTime = endTime - startTime;
				comTimeList.add(Double.valueOf(useTime));
				String duration = PrintTime.PrintMilis(useTime);
				// String timeDouble = String.valueOf(useTime);
				String timeDouble = String.format("%s: %s", inputFilePath, duration);
				writeFile(filePath, timeDouble);
				// }
			}
		}
		// }
	}
}
