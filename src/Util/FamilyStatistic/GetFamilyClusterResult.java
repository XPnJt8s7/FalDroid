package Util.FamilyStatistic;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ConstantVar.ConstantValue;
import GraphSimilarity.HierarchicalClustering.FamilyClusterResult;
import Util.BasicStatisticExperiment.GenerateTrainFile;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/*
 * Analyze the clustering results for each family,
 * The function OneFamilyClusterResult generates a family subgraph clustering result
 */
public class GetFamilyClusterResult {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Option helpOpt = new Option("h", "help", false, "print this message");
		Option familyDirOpt = Option.builder("f").longOpt("families-dir")
				.argName("path")
				.hasArg()
				.desc("Specify the families directory")
				.build();

		// create Options object
		Options options = new Options();
		options.addOption(helpOpt);
		options.addOption(familyDirOpt);

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args);
			if (cmd.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("OptionsCli", options);
				System.exit(0);
			}
			if (cmd.hasOption(familyDirOpt)) {
				ConstantValue.FAMILIESDIRPATH_STRING = cmd.getOptionValue(familyDirOpt);
				System.out.println("Using as families dir: " + ConstantValue.FAMILIESDIRPATH_STRING);
			} else {
				// dirPathString = ConstantValue.getVar().FAMILIESDIRPATH_STRING;
				System.out.println("No families dir provided, using default: " + ConstantValue.FAMILIESDIRPATH_STRING);
			}
		} catch (ParseException e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("OptionsCli", options);
			System.exit(1);
		}

		ArrayList<Double> supportSetInt = new ArrayList<>();
		supportSetInt.add(0.1D);
		supportSetInt.add(0.2D);
		// supportSetInt.add(0.3D);
		// supportSetInt.add(0.4D);
		// supportSetInt.add(0.5D);
		// supportSetInt.add(0.6D);
		// supportSetInt.add(0.7D);
		// supportSetInt.add(0.8D);
		// supportSetInt.add(0.9D);

		// Set<Double> simTrSetInt = new HashSet<>();
		ArrayList<Double> simTrSetInt = new ArrayList<>();
		simTrSetInt.add(0.1D);
		simTrSetInt.add(0.2D);
		simTrSetInt.add(0.3D);
		simTrSetInt.add(0.4D);
		simTrSetInt.add(0.5D);
		simTrSetInt.add(0.6D);
		simTrSetInt.add(0.7D);
		simTrSetInt.add(0.8D);
		// simTrSetInt.add(0.9D);

		Iterator<Double> iterator = supportSetInt.iterator();
		// Iterator<Double> simTriterator = simTrSetInt.iterator();
		int len = simTrSetInt.size();
		while (iterator.hasNext()) {
			double sur = iterator.next();
			// Iterator<Double> simTrIterator = simTrSetInt.iterator();
			for (int i = 0; i < len; i++) {
				double simTr = simTrSetInt.get(i);
				// System.out.println("Doing support: " + sur);
				ConstantValue.getVar().minScoreSim = simTr;
				System.out.println(String.format("Doing support: %s and similarity threshold: %s",
						sur, ConstantValue.getVar().minScoreSim));
				oneSupportType(sur);
			}
			System.out.println();
		}
	}

	public static void oneSupportType(double support) {
		ConstantValue.getVar().minSupport = support;
		System.out.println(
				"### Start support: " + ConstantValue.getVar().minSupport
						+ " ********************************************");
		File dataFile = new File(ConstantValue.getVar().FAMILIESDIRPATH_STRING);
		File family[] = dataFile.listFiles();
		for (int i = 0; i < family.length; i++) {
			// if(family[i].getName().contains("dowgin")){
			System.out.println("************************** Start Family " + i + ": " + family[i].getName()
					+ " ************************");
			String familyDir = family[i].getAbsolutePath() + "/";
			String type = "Im";
			OneFamilyClusterResult(familyDir, type);
			ConstantValue.getVar().iniValue();
			System.out.println("************************** Finish Family " + i + ": " + family[i].getName()
					+ " ************************");
			// }
		}
		System.out.println("### Finish support :" + ConstantValue.getVar().minSupport
				+ "********************************************\n");
	}

	public static void OneFamilyClusterResult(String familyDirPath, String type) {
		FamilyClusterResult clusterResult = new FamilyClusterResult(familyDirPath, type);
	}
}
