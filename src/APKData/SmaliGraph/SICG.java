package APKData.SmaliGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import APKData.APKTool;
import APKData.ReduceGraph.ReduceGraph;
import Util.Tool.DirDelete;

/*
 *     The outermost encapsulation class, the input is an apk file path, 
 *     the output is a folder, and the folder is the apk file
 *     Decompile the file and create a new subfolder SICG, which contains
 *     SICG-source.gexf   Complete function call diagram
 *     SICG-reduce.gexf　Reduced function call graph
 *     SICG-reduce.dot    The dot cell of the reduced function call graph
 *     source.txt　sensitive source node 列表
 *     sink.txt　List of sensitive destination nodes
 *     Information.txt	Basic related information of the file, including file name, number of smali files, number of nodes, number of node edges, number of sensitive source nodes, number of sensitive destination nodes
 *     
 */
public class SICG {
	private MethodGraph graph;
	private MethodGraph reduceGraph;
	private String apkFileString;
	private String outFileString;
	private String writeFileString;
	private Boolean apktoolSucess;

	public SICG(String inAPKFileString, String outFileString) {
		this.apkFileString = inAPKFileString;
		this.outFileString = outFileString;

		// System.out.println(String.format("[SICG] apktool()"));
		apktoolSucess = apktool();

		if (apktoolSucess) {
			// System.out.println(String.format("[SICG] iniGraph()"));
			iniGraph();
			// System.out.println(String.format("[SICG] newSICGFile()"));
			// Check if iniGraph was succesful, else quit and remove result folder from
			// apktool
			newSICGFile();
			// System.out.println(String.format("[SICG] writeInformationTXT()"));
			writeInformationTXT();
			writeSourceTXT();
			writeSinkTXT();
			writeSourceGraphGexf();
			writeReducedGraphGexf();
			writeGraphDotFile();
			deleteMoreFile();
		}
	}

	/*
	 * Delete redundant files, such as the decompiled source code folder and the
	 * resource folder
	 */
	public void deleteMoreFile() {
		File outfile = new File(outFileString);
		File files[] = outfile.listFiles();
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getAbsolutePath();
			if ((!name.contains("SICG")) && (!name.contains("AndroidManifest.xml"))) {
				DirDelete delete = new DirDelete();
				delete.deleteDir(files[i]);
			}
		}
	}

	public Boolean analysisSuccess() {
		return true;
	}

	public Boolean apktool() {
		APKTool apkTool = new APKTool(apkFileString, outFileString);
		return apkTool.apktoolSucces();
	}

	public void iniGraph() {
		File file = new File(outFileString);
		MethodGraph graph = new MethodGraph(file);
		this.graph = new MethodGraph();
		this.graph = graph;
	}

	public void newSICGFile() {
		try {
			File file = new File(outFileString + "/SICG/");
			file.mkdir();
			this.writeFileString = outFileString + "/SICG/";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void writeInformationTXT() {
		String writeString = this.writeFileString + "Information.txt";
		try {
			File file = new File(writeString);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			String lineString = this.graph.getGraphInformation();
			bWriter.write(lineString);
			bWriter.close();
			fWriter.close();

			// System.out.println("\n[SICG] Finished analysis, printing SICG information");
			// this.graph.printGraphInfo();
			System.out.println();
			// System.out.println("Finish writing Information.txt");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void writeSourceTXT() {
		String writeString = this.writeFileString + "Source.txt";
		try {
			File file = new File(writeString);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			String lineString = "";
			Set<MethodNode> sourceNodes = new HashSet<>();
			sourceNodes = this.graph.getSourceNodeSet();
			if (sourceNodes.size() == 0) {
				System.out.println("No Source Nodes!!!");
			}
			Iterator<MethodNode> sourceIterator = sourceNodes.iterator();
			while (sourceIterator.hasNext()) {
				MethodNode node = sourceIterator.next();
				String nameString = node.getNodeLabelString();
				String categoryString = node.getNodeCategoryString();
				lineString += nameString + "," + categoryString + "\n";
			}
			bWriter.write(lineString);
			bWriter.close();
			fWriter.close();
			// System.out.println("Finish writing Source.txt");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void writeSinkTXT() {
		String writeString = this.writeFileString + "Sink.txt";
		try {
			File file = new File(writeString);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			String lineString = "";
			Set<MethodNode> sinkNodes = new HashSet<>();
			sinkNodes = this.graph.getSinkNodeSet();
			if (sinkNodes.size() == 0) {
				System.out.println("No Source Nodes!!!");
			}
			Iterator<MethodNode> sinkIterator = sinkNodes.iterator();
			while (sinkIterator.hasNext()) {
				MethodNode node = sinkIterator.next();
				String nameString = node.getNodeLabelString();
				String categoryString = node.getNodeCategoryString();
				lineString += nameString + "," + categoryString + "\n";
			}
			bWriter.write(lineString);
			bWriter.close();
			fWriter.close();
			// System.out.println("Finish writing Sink.txt");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void writeSourceGraphGexf() {
		String writeString = this.writeFileString + "SourceGraph.gexf";
		GraphToGexf graphToGexf = new GraphToGexf(this.graph, writeString);
		// System.out.println("Finish writing SourceGraph.gexf");
	}

	public void writeReducedGraphGexf() {
		String writeString = this.writeFileString + "ReducedGraph.gexf";
		ReduceGraph reduceGraph = new ReduceGraph(this.graph);
		GraphToGexf graphToGexf = new GraphToGexf(reduceGraph.getReduceGraph(), writeString);
		// System.out.println("Finish writing ReducedGraph.gexf");
		this.reduceGraph = new MethodGraph();
		this.reduceGraph = reduceGraph.getReduceGraph();
	}

	public void writeGraphDotFile() {
		String writeString = this.writeFileString + "ReducedGraph.dot";
		GraphToDot graphToDot = new GraphToDot(this.reduceGraph, writeString);
		// System.out.println("Finish writing ReducedGraph.dot");
	}

	public Boolean getSuccesApktool() {
		return apktoolSucess;
	}
}
