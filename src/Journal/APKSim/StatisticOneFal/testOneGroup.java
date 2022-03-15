package Journal.APKSim.StatisticOneFal;

import java.io.File;


public class testOneGroup {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			File file=new File("/home/fan/lab/Family/JResult/APKSim/HasGraphScore/result/0.5/adwo-connect/"
					+ "1.dot");
			String  scoreFilePath="/home/fan/lab/Family/JResult/APKSim/HasGraphScore/result"
					+ "/APKScore/0.5.txt";
			OneGroup oneGroup=new OneGroup(file);
			System.out.println(oneGroup.apkIDList.size());
			APKScore apkScore=new APKScore(scoreFilePath);
			oneGroup.setMaxSimValue(apkScore);
			System.out.println(oneGroup.maxSImValue);
	}

}
