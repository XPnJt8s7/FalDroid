package GraphSimilarity.HierarchicalClustering;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ConstantVar.ConstantValue;
import GraphSimilarity.CommunitySubGraph;
import GraphSimilarity.FamilySubGraphSet;
import GraphSimilarity.KMeans.ClusterKmeans;
import Util.Tool.DirDelete;
import Util.PrintTime;

/*
 * The input is the root of a family, e.g. ADRD/ , and the choice of community partitioning algorithm, e.g. im
 * The output is in the ADRD/FamilyInfo/Im/ directory of the family directory, and the Cluster folder stores 
 * the representative subgraphs of the family under a specific input threshold
 *      two other files
 *      ACMap.txt:    correspond to apkName   :     ClusterID
 *      CAMap.txt:    correspond to ClusterID   :     apkName#subgraphName
 * 
 */
public class FamilyClusterResult {
	private ArrayList<Cluster> clusterList = new ArrayList<>();
	private ArrayList<CommunitySubGraph> significantSubGraphList = new ArrayList<>();
	private Set<String> apkNameSet = new HashSet<>();
	private String log = "";
	// private PrintTime timePrinter = new PrintTime();
	private String timeSpend;

	public FamilyClusterResult(String familyDirPath, String type) {
		long starttime = System.currentTimeMillis();
		iniClusterList(familyDirPath, type);
		iniSignificantGraphList();
		iniAPKNameSet(familyDirPath);

		String familyClusterPath = familyDirPath + "FamilyInfo/"
				+ type + "--" + ConstantValue.getVar().minSupport + "--" + ConstantValue.getVar().minScoreSim + "/";
		writeResultInfo(familyClusterPath);

		long endtime = System.currentTimeMillis();
		long usetime = endtime - starttime;
		// System.out.println(" using time: " + usetime);
		timeSpend = PrintTime.PrintMilis(usetime);
		System.out.println("[FamilyClusterResult] Finished with family clustering. Time spend: " + timeSpend);
	}

	public void writeResultInfo(String familyClusterPath) {
		File file = new File(familyClusterPath);
		if (file.exists()) {
			DirDelete delete = new DirDelete();
			delete.deleteDir(file);
		}
		file.mkdirs();
		mvSignificantSubgraph(familyClusterPath + "Cluster/");
		writeClusterAPKMap(familyClusterPath + "/CAmap.txt");
		writeAPKClusterMap(familyClusterPath + "/ACmap.txt");
		writeLogFile(familyClusterPath + "/log.txt");
	}

	public void iniAPKNameSet(String familyDirPath) {
		File file = new File(familyDirPath + "apktool/");
		File apks[] = file.listFiles();
		for (int i = 0; i < apks.length; i++) {
			String name = apks[i].getAbsolutePath() + "/";
			apkNameSet.add(name);
		}
	}

	public void iniClusterList(String familyDirPath, String type) {
		long starttime = System.currentTimeMillis();
		int k = -1;
		File file = new File(familyDirPath + "apktool/");
		File apks[] = file.listFiles();
		k = apks.length;
		FamilySubGraphSet familySubGraphSet = new FamilySubGraphSet(familyDirPath, type);
		long graphListTime = System.currentTimeMillis();
		/*
		 * The following classes use hierarchical clustering
		 */
		// ClusterMatrix clusterMatrix=new
		// ClusterMatrix(familySubGraphSet.getSimMatrix(),
		// familySubGraphSet.getGraphList(), k);
		// this.clusterList=clusterMatrix.getClusterList();
		/*
		 * The following classes use Kmeans clustering
		 */
		// System.out.println("Performing K-means clustering with similarity threshold "
		// + ConstantValue.getVar().minScoreSim);
		ClusterKmeans clusterKmeans = new ClusterKmeans(familySubGraphSet.getGraphList(), k, familyDirPath);
		this.clusterList = clusterKmeans.getClusterList();

		long endtime = System.currentTimeMillis();
		long usetime = endtime - starttime;

		timeSpend = PrintTime.PrintMilis(usetime);
		// System.out.println("Finish analyzing clsuters: " + usetime + "ms");
		// System.out.println("Finish analyzing clsuters. Time spend: " + timeSpend);

		log += "Family Path: " + familyDirPath + "\n";
		log += "Family Size: " + k + "\n";
		log += "Community Detecion Algorithm: " + type + "\n";
		log += "Top Score Ratio: " + (1 - ConstantValue.getVar().minTotalGraphScoreRatio) + "\n";
		log += "Min Score Threshold: " + ConstantValue.getVar().minTotalGraphScore + "\n";
		log += "Number of Effective Subgraphs: " + familySubGraphSet.getSubgraphSize() + "\n";
		log += "Min Similarity Threshold: " + ConstantValue.getVar().minScoreSim + "\n";
		log += "Min Cluster Support: " + ConstantValue.getVar().minSupport + "\n";
		log += "Using time: " + usetime + "ms\n";
		log += "Cluster Infomation:\n";
		// log +=clusterMatrix.getClusterResult();
		log += clusterKmeans.getClusterResult();
	}

	public void iniSignificantGraphList() {
		for (int i = 0; i < clusterList.size(); i++) {
			CommunitySubGraph subGraph = new CommunitySubGraph();
			subGraph = clusterList.get(i).getSignificantSubgraph();
			significantSubGraphList.add(subGraph);
		}
	}

	public void mvSignificantSubgraph(String dstDir) {
		try {
			File file = new File(dstDir);
			// If the target folder exists, delete it first
			if (file.exists()) {
				DirDelete delete = new DirDelete();
				delete.deleteDir(file);
			}
			file.mkdirs();

			for (int i = 0; i < significantSubGraphList.size(); i++) {
				String srcFilePath = significantSubGraphList.get(i).getFilePath();
				String dstFilePath = dstDir + significantSubGraphList.get(i).getFileName();
				String cmd = "cp " + srcFilePath + " " + dstFilePath;
				// System.out.println(cmd);
				exeCmd(cmd);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void writeAPKClusterMap(String ACFilePath) {
		try {
			Map<String, ArrayList<String>> APKClusterIDMap = new HashMap<>();
			Iterator<String> iterator = apkNameSet.iterator();
			while (iterator.hasNext()) {
				String apkName = iterator.next();
				ArrayList<String> tmpList = new ArrayList<>();
				APKClusterIDMap.put(apkName, tmpList);
			}
			for (int i = 0; i < clusterList.size(); i++) {
				Map<String, String> apkNameGraphMap = new HashMap<>();
				apkNameGraphMap = clusterList.get(i).getApkSubgraphNameMap();
				Iterator<String> mapIterator = apkNameGraphMap.keySet().iterator();
				while (mapIterator.hasNext()) {
					String apkName = mapIterator.next();
					String idString = i + "#" + significantSubGraphList.get(i).getFileName();
					APKClusterIDMap.get(apkName).add(idString);
				}
			}
			// 写入文件
			File file = new File(ACFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			String line = "";
			Iterator<String> nameIterator = APKClusterIDMap.keySet().iterator();
			while (nameIterator.hasNext()) {
				String apkName = nameIterator.next();
				ArrayList<String> clusterIDList = new ArrayList<>();
				clusterIDList = APKClusterIDMap.get(apkName);
				for (int i = 0; i < clusterIDList.size(); i++) {
					line = apkName + "#" + clusterIDList.get(i);
					bWriter.write(line + "\n");
				}
				line = "\n";
				bWriter.write(line);
			}
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void writeClusterAPKMap(String CAFilePath) {
		try {
			File file = new File(CAFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			String line = "";
			for (int i = 0; i < clusterList.size(); i++) {
				line = clusterList.get(i).getSignificantSubgraph().getFileName() + "\n";
				bWriter.write(line);
				Map<String, String> apkSubgraphNameMap = new HashMap<>();
				apkSubgraphNameMap = clusterList.get(i).getApkSubgraphNameMap();
				Iterator<String> iterator = apkSubgraphNameMap.keySet().iterator();
				while (iterator.hasNext()) {
					String apkName = iterator.next();
					String filePath = apkSubgraphNameMap.get(apkName);
					line = i + ":	" + apkName + "#" + filePath + "\n";
					bWriter.write(line);
				}
				line = "\n";
				bWriter.write(line);
			}
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public void writeLogFile(String logFilePath) {
		try {
			File logFile = new File(logFilePath);
			FileWriter fWriter = new FileWriter(logFile);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			bWriter.write(log);
			bWriter.close();
			fWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public void exeCmd(String cmd) {
		Runtime rnRuntime = Runtime.getRuntime();
		String outInfo = "";
		try {
			Process process = rnRuntime.exec(cmd);
			InputStream in = (process).getErrorStream();// 得到错误信息输出。
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = br.readLine()) != null) {
				outInfo = outInfo + line + "\n";
			}
			// System.out.println(outInfo);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
