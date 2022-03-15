package Util.BasicStatisticExperiment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ConstantVar.ConstantValue;
import SubgraphsInDifferentFamily.sigsubgraphFalSimilarity;
import SubgraphsInDifferentFamily.sigsubgraphInOneFal;

/*
 *     Calculate the similarity of the saliency subgraphs between different families, 
 *     and calculate the similarity of the saliency subgraphs of other families for each family
 *     and store the result in its Im--0.5/familyDiff
 * 
 */
public class DiffFalSiggraph {

	public static Map<String, Integer> simMap = new HashMap<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File dataFile = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
		File fals[] = dataFile.listFiles();
		for (int i = 0; i < fals.length; i++) {
			String tmpFamily = fals[i].getAbsolutePath() + "/";
			oneFalToAll(tmpFamily);
			System.out.println("Finish: " + fals[i].getName());
		}
		showSimMap();
	}

	public static void oneFalToAll(String familyDir) {
		try {
			String type = "Im--0.9";
			String srcSigDir = familyDir + "FamilyInfo/" + type + "/";
			File srcFile = new File(familyDir);
			String srcName = srcFile.getName();
			File dataFile = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
			File fals[] = dataFile.listFiles();
			String content = "";
			sigsubgraphInOneFal srcFal = new sigsubgraphInOneFal(srcSigDir);
			for (int i = 0; i < fals.length; i++) {
				String dstName = fals[i].getName();
				if (!srcName.equals(dstName)) {
					String dstSigDir = fals[i].getAbsolutePath() + "/FamilyInfo/" + type + "/";
					sigsubgraphInOneFal dstFal = new sigsubgraphInOneFal(dstSigDir);
					sigsubgraphFalSimilarity similarity = new sigsubgraphFalSimilarity(srcFal, dstFal);
					int k = similarity.getSimGraphNum();
					String simString = similarity.getSrcFalName() + "---" + similarity.getDstFalName();
					if (k > 0) {
						simMap.put(simString, k);
					}
					content += similarity.getLog();
				}
			}
			String writeStringPath = srcSigDir + "familyDiff.txt";
			writeLogToOneFal(writeStringPath, content);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void writeLogToOneFal(String writeFilePath, String content) {
		try {
			File file = new File(writeFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(content);
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static void showSimMap() {
		Iterator<String> simIterator = simMap.keySet().iterator();
		while (simIterator.hasNext()) {
			String simString = simIterator.next();
			int k = simMap.get(simString);
			System.out.println(simString + ": " + k);
		}
	}
}
