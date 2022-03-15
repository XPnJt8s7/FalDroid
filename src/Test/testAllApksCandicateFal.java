package Test;

import java.io.File;

import CandicateFamily.APKCandiFal;

public class testAllApksCandicateFal {
	public static int totalNum=1260;
	public static int predictNum=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	  String srcFilePath="";
	  String dstFilePath="";
	  
			
	}
	public static void testOneFamily(String dirPathString,String name){
		
		File dir=new File(dirPathString+"apktool/");
		File apks[]=dir.listFiles();
		int k=0;
		for(int i=0;i<apks.length;i++){
			String tmpString=apks[i].getAbsolutePath()+"/";
			APKCandiFal candiFal=new APKCandiFal(tmpString);
		//	System.out.println(candiFal.getApkPathString()+": "+candiFal.showCandicateFamilies());
			if(candiFal.isInCandicateFal()){
				k++;
			}
		}
		predictNum+=k;
		System.out.println(name+"--- Predict Rate:	"+k+"/"+apks.length);
	}
}
