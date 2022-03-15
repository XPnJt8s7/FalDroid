package Exp.ClassifyByVector;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.sound.midi.VoiceStatus;

import ConstantVar.ConstantValue;
import Util.PrintTime;

public class SiggraphVector implements Serializable {
	private String clusterType = "";
	private ArrayList<Siggraph> allSiggraphList = new ArrayList<>();
	private ArrayList<SiggraphFeature> featureList = new ArrayList<>();

	public SiggraphVector() {
	}

	public SiggraphVector(String clsuterType) {
		this.clusterType = clsuterType;

		// Collect the most representative subgraph from the clustering
		iniSiggraphList();

		// Create a list of representative subgraphs and their similiar subgraphs
		constructFeature();

		// Assign a weight for each representative subgraph
		assignWeight();
	}

	public SiggraphVector(String featureSpaceFilePath, String clusterType) {
		try {
			File file = new File(featureSpaceFilePath);
			FileReader fReader = new FileReader(file);
			BufferedReader bReader = new BufferedReader(fReader);
			String line = "";
			while ((line = bReader.readLine()) != null) {
				if (line.contains("@attribute") && line.contains("Cluster")) {
					String tmps[] = line.split(" ");
					String featureFilePath = tmps[1];
					SiggraphFeature siggraphFeature = new SiggraphFeature();
					siggraphFeature.addSigraph(new Siggraph(featureFilePath));
					siggraphFeature.setWeight(1.0D);
					featureList.add(siggraphFeature);
				} else if (line.contains("#")) {
					String tmps[] = line.split(" # ");
					String featureFilePath = tmps[0];
					SiggraphFeature siggraphFeature = new SiggraphFeature();
					siggraphFeature.addSigraph(new Siggraph(featureFilePath));
					siggraphFeature.setWeight(Double.valueOf(tmps[1]));
					featureList.add(siggraphFeature);
				}
			}

			bReader.close();
			fReader.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public double maxWeight() {
		double max = -1000.0D;
		for (int i = 0; i < featureList.size(); i++) {
			double tmp = featureList.get(i).getWeight();
			if (tmp > max) {
				max = tmp;
			}
		}
		return max;
	}

	public double minWeight() {
		double min = 1000.0D;
		for (int i = 0; i < featureList.size(); i++) {
			double tmp = featureList.get(i).getWeight();
			if (tmp < min) {
				min = tmp;
			}
		}
		return min;
	}

	public void assignWeight() {
		for (int i = 0; i < featureList.size(); i++) {
			double weight = featureList.get(i).getEntropy();
			// System.out.println("[SiggraphVector] Weight from entropy: " + weight);
			featureList.get(i).setWeight(weight);
		}
		double maxW = maxWeight();
		double minW = minWeight();
		for (int i = 0; i < featureList.size(); i++) {
			double tmp = (featureList.get(i).getWeight() - minW) / (maxW - minW);
			double score = featureList.get(i).getScore();
			featureList.get(i).setWeight(tmp * score);
		}
	}

	public void writeFeatrueSpace(String writeFilePath) {
		try {
			File file = new File(writeFilePath);
			FileWriter fWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fWriter);
			for (int i = 0; i < featureList.size(); i++) {
				String line = featureList.get(i).getFeatureLabel() + " # " + featureList.get(i).getWeight() + "\n";
				bWriter.write(line);
			}

			bWriter.close();
			fWriter.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/*
	 * First, the saliency subgraphs in each family are stored as an object Siggraph
	 */
	public void iniSiggraphList() {
		try {
			long startTime = System.currentTimeMillis();
			File dataFile = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
			File family[] = dataFile.listFiles();
			int numFamilies = family.length;
			// System.out.println("[SiggraphVector] Number of families to include: " +
			// numFamilies);
			for (int i = 0; i < numFamilies; i++) {
				String clusterFilePath = family[i].getAbsolutePath() + "/FamilyInfo/" + clusterType + "/";
				File logFile = new File(clusterFilePath + "log.txt");
				FileReader fReader = new FileReader(logFile);
				BufferedReader bReader = new BufferedReader(fReader);
				String lineString = "";
				int size = 0;
				while ((lineString = bReader.readLine()) != null) {
					if (lineString.contains("Family Size:")) {
						String args[] = lineString.split(": ");
						size = Integer.valueOf(args[1]);
					}
					if (lineString.contains("#")) {
						String args[] = lineString.split("#");
						String fileName = args[1];
						String sup = args[2];
						String filePath = clusterFilePath + "Cluster/" + fileName;
						// System.out.println("[SiggraphVector] Checking in path " + filePath);
						Siggraph siggraph = new Siggraph(filePath);

						// Set the support of the subgraph
						int support = Integer.valueOf(sup);
						siggraph.setSupport(support);
						// Set the number of samples in the family where the subplot is located
						siggraph.setFalSize(size);
						this.allSiggraphList.add(siggraph);
					}
				}
				bReader.close();
				fReader.close();
			}
			long endTime = System.currentTimeMillis();
			long useTime = endTime - startTime;
			System.out.println("Size of all Siggraphs: " + allSiggraphList.size());
			System.out.println("It takes this much time to extract all saliency submaps: " + useTime + " ms");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.exit(-1);
		}
	}

	// public void DrebinMergeFeature() {
	// try {
	// long startTime = System.currentTimeMillis();
	// for (int i = 0; i < allSiggraphList.size(); i++) {
	// Siggraph siggraph = allSiggraphList.get(i);
	// SiggraphFeature feature = new SiggraphFeature(siggraph);
	// feature.setWeight(1.0D);
	// featureList.add(feature);

	// }
	// long endtime = System.currentTimeMillis();
	// long usetime = endtime - startTime;
	// System.out.println("Constructing the saliency submap feature space took: " +
	// usetime + " ms");
	// } catch (Exception e) {
	// // TODO: handle exception
	// e.printStackTrace();
	// }
	// }

	/*
	 * Construct feature vectors and merge same saliency submaps
	 */
	public void constructFeature() {
		try {
			long startTime = System.currentTimeMillis();
			for (int i = 0; i < allSiggraphList.size(); i++) {
				Siggraph siggraph = allSiggraphList.get(i);
				// If the feature list is empty, add first subgraph
				if (featureList.size() == 0) {
					SiggraphFeature feature = new SiggraphFeature(siggraph);
					featureList.add(feature);
				} else {
					boolean canAddFeature = false;
					for (int j = 0; j < featureList.size(); j++) {
						// If the subgraph is similiar to another already added,
						// add it to the group
						if (featureList.get(j).canAddSiggraph(siggraph)) {
							canAddFeature = true;
							featureList.get(j).addSigraph(siggraph);
							break;
						}
					}
					// If the subgraph is different, add a new element to the feature list
					if (canAddFeature == false) {
						SiggraphFeature feature = new SiggraphFeature(siggraph);
						featureList.add(feature);
					}
				}
				System.out.print("Add feature: " + i + "/ " + allSiggraphList.size() + "\r");
			}
			System.out.println(String.format("[SiggraphVector] Finished adding %i features", allSiggraphList.size()));
			long endtime = System.currentTimeMillis();
			long usetime = endtime - startTime;
			String duration = PrintTime.PrintMilis(usetime);
			System.out.println("[SiggraphVector] Constructing the saliency submap feature space took: " + duration);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void showFeature() {
		for (int i = 0; i < featureList.size(); i++) {
			System.out.println(featureList.get(i).FeatureString());
		}
	}

	public ArrayList<SiggraphFeature> getFeatureList() {
		return featureList;
	}

	public void setFeatureList(ArrayList<SiggraphFeature> featureList) {
		this.featureList = featureList;
	}

	/*
	 * Build a list of names of all feature submaps
	 */
	public String getFeatureNameList() {
		String featureString = "";
		for (int i = 0; i < featureList.size(); i++) {
			featureString += "@attribute " + featureList.get(i).getFeatureLabel() + " numeric\n";
		}
		return featureString;
	}

}
