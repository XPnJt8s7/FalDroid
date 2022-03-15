package Exp.ClassifyByVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import Util.Tool.MapSort;

public class FregraphVector implements Serializable {
	/**
	* 
	*/
	private static final long serialVersionUID = 7465496020354988682L;
	public ArrayList<Fregraph> fregraphList = new ArrayList<>();

	public FregraphVector() {

	}

	public FregraphVector(SiggraphVector vector) {
		for (int i = 0; i < vector.getFeatureList().size(); i++) {
			Fregraph fregraph = new Fregraph(vector.getFeatureList().get(i));
			fregraphList.add(fregraph);
		}
		// reorderFregraphList();
	}

	public void reorderFregraphList() {
		Map<Fregraph, Double> fregraphScoreMap = new HashMap<>();

		for (int i = 0; i < fregraphList.size(); i++) {
			double score = fregraphList.get(i).weight;
			// System.out.println("[FregraphVector ] - Weight: " + score);
			fregraphScoreMap.put(fregraphList.get(i), score);
		}

		ArrayList<Map.Entry<Fregraph, Double>> orderList = new ArrayList<>();
		MapSort<Fregraph, Double> mapSort = new MapSort<>();

		orderList = (ArrayList<Entry<Fregraph, Double>>) mapSort.sortMapByValue(fregraphScoreMap);
		// System.out

		fregraphList = new ArrayList<>();
		for (int i = 0; i < orderList.size(); i++) {
			fregraphList.add(orderList.get(i).getKey());
		}
	}

	/*
	 * Build a list of names of all feature submaps
	 */
	public String getFeatureNameList() {
		String featureString = "";
		for (int i = 0; i < fregraphList.size(); i++) {
			featureString += "@attribute " + fregraphList.get(i).getFeatureLabel() + " numeric\n";
		}
		return featureString;
	}

	public void printFregraphVectorInfo() {
		int n = 0;
		// double weight;
		for (Fregraph fregraph : fregraphList) {
			n++;
			System.out.println(String.format("[FregraphVector] Frepgraph %s:", n));
			// weight = fregraph.weight;
			// System.out.println("[FregraphVector] List of fregraphs:");
			fregraph.printFregraphInfo();
		}
	}
}
