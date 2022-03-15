package CheckComplete;

import java.io.File;

import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;

public class CheckSmaliComplete {
	public static String dataFilePathString=ConstantValue.FAMILIESDIRPATH_STRING;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
			File file=new File(dataFilePathString);
			File family[]=file.listFiles();
			for(int i=0;i<family.length;i++){
				String familyApktoolString=family[i].getAbsolutePath()+"/apktool/";
				File apktoolFile=new File(familyApktoolString);
				File apks[]=apktoolFile.listFiles();
				for(int j=0;j<apks.length;j++){
					String apkPathString=apks[j].getAbsolutePath();
					/*
					 * 			检验反编译文件的完整性，依据为是否能够获取安卓的权限配置文件
					 */
					String smaliPathString=apkPathString+"/AndroidManifest.xml";
					CommunitySubGraph graph=new CommunitySubGraph(apkPathString+"/SICG/ReducedGraph.gexf");
					int k=graph.getGraph().getNodeMap().size();
					try {
						File smali=new File(smaliPathString);
						if(!smali.exists() || k==0){
							System.out.println(apkPathString);
						}
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
	}

}
