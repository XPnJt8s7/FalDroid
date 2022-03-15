package Exp.ClassifyByVector;

import java.io.Serializable;
import java.util.ArrayList;

import GraphSimilarity.CommunitySubGraph;

public class SiggraphFeature implements Serializable {
	// A feature consists of a list of subgraphs, all of which are subgraphs whose
	// similarity is greater than the threshold
	public ArrayList<Siggraph> siggraphList = new ArrayList<>();
	private double weight;

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public SiggraphFeature() {
	}

	public SiggraphFeature(Siggraph siggraph) {
		this.siggraphList.add(siggraph);
	}

	public void addSigraph(Siggraph addSiggraph) {
		siggraphList.add(addSiggraph);
	}

	public boolean canAddSiggraph(Siggraph addSiggraph) {
		boolean canAdd = true;
		for (int i = 0; i < siggraphList.size(); i++) {
			Siggraph tmpSigraph = siggraphList.get(i);
			if (!tmpSigraph.simWithSiggraph(addSiggraph)) {
				canAdd = false;
				break;
			}
		}
		return canAdd;
	}

	public String FeatureString() {
		String resultString = "";
		for (int i = 0; i < siggraphList.size(); i++) {
			resultString += siggraphList.get(i).getFalName() + "->" + siggraphList.get(i).getSubGraph().getFileName()
					+ "\n";
		}
		return resultString;
	}

	/*
	 * Determine whether a subgraph is in the current feature, if so, return true
	 */
	public boolean inFeature(CommunitySubGraph subrgaph) {

		String dstFilePath = subrgaph.getFilePath();
		for (int i = 0; i < siggraphList.size(); i++) {
			String tmpString = siggraphList.get(i).getFilePath();
			if (tmpString.equals(dstFilePath)) {
				return true;
			}
		}
		// if(siggraphList.get(0).getFilePath().equals(dstFilePath)){
		// return true;
		// }
		return false;
	}

	// 将该特征中的第一个子图当做特征的标签
	public String getFeatureLabel() {
		String reString = "";
		if (siggraphList.size() > 0) {
			reString = siggraphList.get(0).getFilePath();
		}
		return reString;
	}

	// 输入参数为一个家族的家族名，获取该特征中该家族相应的子图的有效分值
	/*
	 * 由于会存在一种情况，样本a属于A家族，它具有特征f2，但是f2并不是A家族中的
	 * 显著性子图，f2是其他家族的特征，在这种情况下，尽管a具有f2特征，但是
	 * 在该维特征上的值为 0
	 */
	public double getSigraphScoreWithFalName(String falName) {
		Siggraph siggraph = new Siggraph();
		double effecScore = 0.0D;
		double avgScore = 0.0D;
		boolean find = false;
		for (int i = 0; i < siggraphList.size(); i++) {
			String sigGraphFilePath = siggraphList.get(i).getFilePath();
			if (sigGraphFilePath.contains(falName)) {
				siggraph = siggraphList.get(i);
				effecScore = Double.valueOf(siggraph.getSupport()) / Double.valueOf(siggraph.getFalSize());
				effecScore = effecScore * siggraph.getSubGraph().getSensitiveScore();
				find = true;
				break;
			} else {
				siggraph = siggraphList.get(i);
				double tmpScore = 0.0D;
				tmpScore = Double.valueOf(siggraph.getSupport()) / Double.valueOf(siggraph.getFalSize());
				tmpScore = tmpScore * siggraph.getSubGraph().getSensitiveScore();
				avgScore += tmpScore;
			}
		}
		if (find == true) {
			// 当找到匹配的家族的时候,返回值依旧是effecScore保持不变
		} else {
			// 当该样本存在该特征，但是其特征label中并不存在其家族名称，取其子图集的平均值
			avgScore = avgScore / Double.valueOf(siggraphList.size());
			effecScore = avgScore;
			// effecScore=0.0D;
		}
		if (siggraphList.size() > 1) {
			effecScore = 0.0D;
		}
		return effecScore;
	}

	// 对于测试样本来说，不知道该家族实际名称，因此取该特征的平均有效分值
	public double getSiggraphScoreWithoutFalName() {
		double avg = 0.0D;
		for (int i = 0; i < siggraphList.size(); i++) {
			Siggraph siggraph = siggraphList.get(i);
			double effecScore = 0.0D;
			effecScore = Double.valueOf(siggraph.getSupport()) / Double.valueOf(siggraph.getFalSize());
			effecScore = effecScore * siggraph.getSubGraph().getSensitiveScore();
			avg += effecScore;
		}
		avg = avg / Double.valueOf(siggraphList.size());
		if (siggraphList.size() > 1) {
			avg = 0.0D;
		}
		return avg;
	}

	public double getEntropy() {
		double entropy = 0.0D;
		double totalSupportRadio = 0.0D;
		for (int i = 0; i < siggraphList.size(); i++) {
			totalSupportRadio += Double.valueOf(siggraphList.get(i).getSupport())
					/ Double.valueOf(siggraphList.get(i).getFalSize());
		}
		for (int i = 0; i < siggraphList.size(); i++) {
			double tmpRadio = Double.valueOf(siggraphList.get(i).getSupport())
					/ Double.valueOf(siggraphList.get(i).getFalSize());
			double r = tmpRadio / totalSupportRadio;
			entropy += r * Math.log(r);
		}

		// entropy = -entropy;
		// entropy=1-entropy;
		// System.out.println("[SiggraphFeature] - Entropy: " + entropy);
		return entropy;
	}

	/*
	 * Get subgraph score
	 */
	public double getScore() {
		double graphScore = 0.0D;
		double totalSupportRadio = 0.0D;
		for (int i = 0; i < siggraphList.size(); i++) {
			totalSupportRadio += Double.valueOf(siggraphList.get(i).getSupport())
					/ Double.valueOf(siggraphList.get(i).getFalSize());
		}
		for (int i = 0; i < siggraphList.size(); i++) {
			double tmpRadio = Double.valueOf(siggraphList.get(i).getSupport())
					/ Double.valueOf(siggraphList.get(i).getFalSize());
			double r = tmpRadio / totalSupportRadio;
			graphScore += siggraphList.get(i).getSubGraph().getSensitiveScore() * r;
		}
		// System.out.println("[SiggraphFeature] - Score: " + graphScore);
		return graphScore;
	}
}
