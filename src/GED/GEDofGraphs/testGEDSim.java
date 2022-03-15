package GED.GEDofGraphs;

import java.io.BufferedWriter;

public class testGEDSim {
	public static String srcFile="/home/fan/tmp/3/";
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String srcNodeFile=srcFile+"node1.dot";
		String dstNodeFile=srcFile+"node2.dot";
		String srcEdgeFile=srcFile+"edge1.dot";
		String dstEdgeFile=srcFile+"edge2.dot";
		SimGED simGED=new SimGED(srcNodeFile, dstNodeFile, srcEdgeFile, dstEdgeFile);
		
	}

}
