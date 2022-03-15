package CheckComplete;

import java.io.File;

import ConstantVar.ConstantValue;

public class CheckCommunityFileComplete {
	public static int k=0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		familyCheck();
		System.out.println(k);
	}
	public static void familyCheck(){
		ConstantValue.getVar();
		File file=new File(ConstantValue.FAMILIESDIRPATH_STRING);
		File family[]=file.listFiles();
		for(int i=0;i<family.length;i++){
			String familyApktoolString=family[i].getAbsolutePath()+"/apktool/";
			File apktoolFile=new File(familyApktoolString);
			File apks[]=apktoolFile.listFiles();
			for(int j=0;j<apks.length;j++){
				String apkPathString=apks[j].getAbsolutePath();
			//	String fgPathString=apkPathString+"/SICG/Community/Community_Result_fg.csv";
				String imPathString=apkPathString+"/SICG/Community/Community_Result_im.csv";
				String imComminityFilePath=apkPathString+"/SICG/Community/Community_Im/";
			//	String lpPathString=apkPathString+"/SICG/Community/Community_Result_lp.csv";
			//	String mlPathString=apkPathString+"/SICG/Community/Community_Result_ml.csv";
				try {
			//		File fg=new File(fgPathString);
					File im=new File(imPathString);
			//		File lp=new File(lpPathString);
			//		File ml=new File(mlPathString);
					File comFile=new File(imComminityFilePath);
					File comGexfs[]=comFile.listFiles();
					
					if(!im.exists()||(!comFile.exists()) || (comGexfs.length==0)){
						
						System.out.println(apkPathString);
						k++;
					}
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				
			}
			
		}
	}
}
