package Exp.HMRF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ConstantVar.ConstantValue;

public class APKObject {
	private String apkName = "";
	private ArrayList<Integer> featureInt = new ArrayList<>();
	private String actualFalName = "";
	private ArrayList<String> predictFalName = new ArrayList<>();
	private boolean result = false; // 判断测试样本的预测类别和实际类别是否一致
	private boolean consistency = false; // 判断训练样本的节点与相邻节点是否类别一致
	private String nodeType = "";
	private Map<String, Double> neighborAPKMap = new HashMap<>();
	// 该对象样本的邻居节点以及该样本与邻居节点的相似性
	private Map<String, Double> probabilityMap = new HashMap<>();

	// 存储该样本相对于所有已知家族的概率分布
	public APKObject() {
	}

	public APKObject(String inputString) {
		iniData(inputString);
	}

	/*
	 * 根据一条记录，初始化该样本的相关信息
	 */
	public void iniData(String inputString) {
		try {
			String s[] = inputString.split(",");
			int k = s.length;
			if (k > 3) {
				this.apkName = s[0];
				this.actualFalName = s[k - 1];
				for (int i = 1; i <= k - 2; i++) {
					featureInt.add(Integer.valueOf(s[i]));
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String getDataString() {
		String resultString = "";
		resultString += "APK:" + apkName + "\n";
		for (int i = 0; i < featureInt.size(); i++) {
			resultString += featureInt.get(i) + ",";
		}
		resultString += "\n";
		resultString += "Fal:" + this.actualFalName + "\n";
		return resultString;
	}

	/*
	 * 添加一个邻居节点，如果二者的相似性高于阈值，则添加上
	 */
	public void addOneNeighbor(APKObject apkObject) {
		double similarity = calculateSimilarity(apkObject);
		if (similarity >= ConstantValue.getVar().minScoreSim) {
			this.neighborAPKMap.put(apkObject.apkName, similarity);
		}
	}

	/*
	 * 计算本样本与对象样本的相似性，计算方式为Cosine
	 */
	public double calculateSimilarity(APKObject apkObject) {
		ArrayList<Integer> dstFeatureInt = new ArrayList<>();
		dstFeatureInt = apkObject.featureInt;
		double fenzi = 0.0D;
		double fenmu = 0.0D;
		double a = 0.0D;
		double b = 0.0D;
		for (int i = 0; i < this.featureInt.size(); i++) {
			fenzi += featureInt.get(i) * dstFeatureInt.get(i);
			a += featureInt.get(i) * featureInt.get(i);
			b += dstFeatureInt.get(i) * dstFeatureInt.get(i);
		}
		fenmu = Math.sqrt(Double.valueOf(a * b));
		double result = fenzi / fenmu;
		return result;
	}

	/*
	 * 计算本样本的与已知家族的初始概率分布
	 */
	public void calculateProbability(KnownSamples samples) {
		Map<String, Set<APKObject>> sampleMap = new HashMap<>();
		sampleMap = samples.getKnownSampleMap();
		double maxFalSim = -0.1D;
		Iterator<String> falIterator = sampleMap.keySet().iterator();
		while (falIterator.hasNext()) {
			String falName = falIterator.next();
			Set<APKObject> falSet = new HashSet<>();
			falSet = sampleMap.get(falName);
			double maxSim = calculateOneFalPro(falSet);
			this.probabilityMap.put(falName, maxSim);
			if (maxSim > maxFalSim) {
				maxFalSim = maxSim;
				this.predictFalName = new ArrayList<>();
				this.predictFalName.add(falName);
			} else if (maxSim == maxFalSim) {
				this.predictFalName.add(falName);
			}
		}
		normalizationProbability();
	}

	public double calculateOneFalPro(Set<APKObject> apkObjectSet) {
		double maxSim = 0.0D;
		Iterator<APKObject> apkIterator = apkObjectSet.iterator();
		while (apkIterator.hasNext()) {
			APKObject apkObject = apkIterator.next();
			double sim = this.calculateSimilarity(apkObject);
			if (sim >= maxSim) {
				maxSim = sim;
			}
		}
		return maxSim;
	}

	/*
	 * 返回与待检测样本最相似的已知样本的apkName
	 */
	public String getMostSimilarAPKName(KnownSamples knownSamples) {
		Map<String, APKObject> knownIntancesMap = new HashMap<>();
		knownIntancesMap = knownSamples.getKnowInstancesMap();
		Iterator<String> knownIterator = knownIntancesMap.keySet().iterator();
		String mostSimAPKName = "";
		double maxSim = -0.1D;
		while (knownIterator.hasNext()) {
			String apkName = knownIterator.next();
			double sim = this.calculateSimilarity(knownIntancesMap.get(apkName));
			if (sim > maxSim) {
				maxSim = sim;
				mostSimAPKName = apkName;
			}
		}
		return mostSimAPKName;
	}

	public void normalizationProbability() {
		double total = 0.0D;
		Iterator<String> proIterator = this.probabilityMap.keySet().iterator();
		while (proIterator.hasNext()) {
			String falName = proIterator.next();
			double maxSim = probabilityMap.get(falName);
			total += maxSim;
		}
		Map<String, Double> normalProMap = new HashMap<>();
		proIterator = this.probabilityMap.keySet().iterator();
		while (proIterator.hasNext()) {
			String falName = proIterator.next();
			double normalPro = probabilityMap.get(falName) / total;
			normalProMap.put(falName, normalPro);
		}
		this.probabilityMap = normalProMap;
	}

	public void showProbabilityMap() {
		Iterator<String> proMapIterator = probabilityMap.keySet().iterator();
		while (proMapIterator.hasNext()) {
			String falName = proMapIterator.next();
			double maxSim = probabilityMap.get(falName);
			System.out.println(falName + ":" + maxSim);
		}
	}

	public String getApkName() {
		return apkName;
	}

	public void setApkName(String apkName) {
		this.apkName = apkName;
	}

	public ArrayList<Integer> getFeatureInt() {
		return featureInt;
	}

	public void setFeatureInt(ArrayList<Integer> featureInt) {
		this.featureInt = featureInt;
	}

	public String getActualFalName() {
		return actualFalName;
	}

	public void setActualFalName(String actualFalName) {
		this.actualFalName = actualFalName;
	}

	public Map<String, Double> getNeighborAPKMap() {
		return neighborAPKMap;
	}

	public void setNeighborAPKMap(Map<String, Double> neighborAPKMap) {
		this.neighborAPKMap = neighborAPKMap;
	}

	public Map<String, Double> getProbabilityMap() {
		return probabilityMap;
	}

	public void setProbabilityMap(Map<String, Double> probabilityMap) {
		this.probabilityMap = probabilityMap;
	}

	public ArrayList<String> getPredictFalName() {
		return predictFalName;
	}

	public void setPredictFalName(ArrayList<String> predictFalName) {
		this.predictFalName = predictFalName;
	}

	public void showPredictFalName() {
		System.out.println(this.actualFalName + ":");
		for (int i = 0; i < predictFalName.size(); i++) {
			System.out.println("Predict:" + predictFalName.get(i) + ",");
		}
		System.out.println();
	}

	public void calculateResult() {
		if (this.predictFalName.contains(actualFalName)) {
			this.result = true;
		}
	}

	/*
	 * 主要用于测试集样本显示中标注红色节点
	 * 判断当前样本的预测类别是否于实际类别一致，如果
	 * 一致则返回真；否则返回假
	 */
	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}

	public String getPredictFalNameString() {
		String resultString = "";
		for (int i = 0; i < predictFalName.size(); i++) {
			resultString += predictFalName.get(i);
			if (i >= 1 && i != predictFalName.size() - 1) {
				resultString += ",";
			}
		}
		return resultString;
	}

	public String getNodeType() {
		return nodeType;
	}

	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}

	public boolean isConsistency() {
		return consistency;
	}

	public void setConsistency(boolean consistency) {
		this.consistency = consistency;
	}

}
