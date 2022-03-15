package Exp.ClassifyByVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class Fregraph implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5216891263759377623L;
	public ArrayList<String> sameGraphList = new ArrayList<>();
	public double weight = 0.0D;

	public Fregraph() {

	}

	public Fregraph(SiggraphFeature feature) {
		for (int i = 0; i < feature.siggraphList.size(); i++) {
			String graphFilePath = feature.siggraphList.get(i).getFilePath();
			sameGraphList.add(graphFilePath);
		}
		// System.out.println("[Fregraph] This fregraph weight: " +
		// feature.getWeight());
		this.weight = feature.getWeight();
		// System.out.println("[Fregraph] Got weight: " + this.weight);
	}

	// Use the first subgraph in the feature as the label of the feature
	public String getFeatureLabel() {
		String reString = "";
		if (sameGraphList.size() > 0) {
			String[] featSplit = sameGraphList.get(0).split("/");
			// reString = sameGraphList.get(0);
			reString = featSplit[featSplit.length - 1];
		}
		return reString;
	}

	public boolean inFeature(String graphToFind) {
		boolean found = false;
		String[] graphToFindSplit = graphToFind.split("/");
		int graphToFindSplitLen = graphToFindSplit.length;
		String graphToFindSubpath = String.join("/",
				Arrays.copyOfRange(graphToFindSplit, graphToFindSplitLen - 5, graphToFindSplitLen));

		int graphPathSplitLen;
		String graphSubPath;
		for (String graphPathString : sameGraphList) {
			String[] graphPathStringSplit = graphPathString.split("/");
			graphPathSplitLen = graphPathStringSplit.length;
			graphSubPath = String.join("/",
					Arrays.copyOfRange(graphPathStringSplit, graphPathSplitLen - 5, graphPathSplitLen));

			if (graphSubPath.equals(graphToFindSubpath)) {
				found = true;
				break;
			}

		}
		return found;
		// if (sameGraphList.contains(line)) {
		// return true;
		// }
		// return false;
	}

	public void printFregraphInfo() {
		System.out.println(String.format("[Fregraph] Weight: %f", weight));
		System.out.println("[Fregraph] List of fregraphs:");
		int n = 0;
		for (String fregraphString : sameGraphList) {
			System.out.println(String.format("[Fregraph] %d: %s", n, fregraphString));
		}
	}
}
