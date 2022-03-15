package Util.FamilyStatistic;

import java.io.File;

import CandicateFamily.FamilyCandicateStatistic;
import ConstantVar.ConstantValue;

/*
 *  The third step of the experiment is to calculate the candidate family
 * 	Calculate the candidate family for each family member instance 
 *  and write it into the CandicateFamilies folder in the FamilyInfo folder
 * 	The naming method is top=k.csv, where k is the number of candidate families
 */
public class CalculateAllFamilyCandicates {
	public static int allPredictNum = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		ConstantValue.getVar();
		for (int i = 1; i < 31; i++) {
			calculateCandicateFamily(i);
		}
		long endTime = System.currentTimeMillis();
		long dureTime = endTime - startTime;
		System.out.println(dureTime + "ms");
	}

	public static void calculateCandicateFamily(int k) {
		ConstantValue.TopFalNumber = k;
		allPredictNum = 0;
		System.out.println("*****************" + "Top K=" + ConstantValue.TopFalNumber + "**************************");
		ConstantValue.getVar();
		String srcDataFileString = ConstantValue.FAMILIESDIRPATH_STRING;
		File srcDir = new File(srcDataFileString);
		File familyDir[] = srcDir.listFiles();
		for (int i = 0; i < familyDir.length; i++) {
			String familyDirPathString = familyDir[i].getAbsolutePath() + "/";
			FamilyCandicateStatistic falStatistic = new FamilyCandicateStatistic(familyDirPathString);
			System.out.println(falStatistic.getStatisticString());
			allPredictNum += falStatistic.getPredictNum();
			// System.out.println("Finish "+familyDir[i].getName());
		}
		ConstantValue.getVar();
		// System.out.println("Total Predict Rate:
		// "+allPredictNum+"/"+ConstantValue.TotalInstanceNumber);
	}

}
