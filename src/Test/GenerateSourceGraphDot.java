package Test;

import java.io.File;

import APKData.SmaliGraph.GexfToGraph;
import APKData.SmaliGraph.GraphToDot;

public class GenerateSourceGraphDot {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String falPath="/home/fan/lab/Family/test-final/";
		File srcDir=new File(falPath);
		File fals[]=srcDir.listFiles();
		for(int i=0;i<fals.length;i++){
			oneFal(fals[i]);
			System.out.println(fals[i].getName());
		}
	}
	public static void oneFal(File file){
		try {
			String apkFilePath=file.getAbsolutePath()+"/apktool/";
			File apkFile=new File(apkFilePath);
			File apks[]=apkFile.listFiles();
			for(int i=0;i<apks.length;i++){
				String SICGPath=apks[i].getAbsolutePath()+"/SICG/";
				
				String gexfFilePath=SICGPath+"SourceGraph.gexf";
				GexfToGraph gexfToGraph=new GexfToGraph(gexfFilePath);
				String writeDotFilePath=SICGPath+"SourceGraph.dot";
				GraphToDot graphToDot=new GraphToDot(gexfToGraph.getGraph(), writeDotFilePath);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
