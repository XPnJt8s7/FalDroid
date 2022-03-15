package Util.BasicStatisticExperiment.FamilyInfo;

import java.io.File;

import ConstantVar.ConstantValue;
import Util.Tool.DirDelete;

public class DeleteFamilyInfo {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
			String dataFilePath=ConstantValue.getVar().FAMILIESDIRPATH_STRING;
			File dataFile=new File(dataFilePath);
			File fal[]=dataFile.listFiles();
			for(int i=0;i<fal.length;i++){
				String familyInfoFile=fal[i].getAbsolutePath()+"/FamilyInfo/";
				File familyInfo=new File(familyInfoFile);
				DirDelete delete=new DirDelete();
				delete.deleteDir(familyInfo);
			}
	}

}
