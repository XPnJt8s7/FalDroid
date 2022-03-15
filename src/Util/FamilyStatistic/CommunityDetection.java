package Util.FamilyStatistic;

import CommunityStructure.CommunityDetection.AllObjectCommunityDetection;
import ConstantVar.ConstantValue;
import Util.PrintTime;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class CommunityDetection {

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

		System.out.println("[CommunityDetection] Starting community detection in all families");
		long startTime = System.currentTimeMillis();
		GenerateCommunity();

		long endTime = System.currentTimeMillis();
		long dureTime = endTime - startTime;
		String duration = PrintTime.PrintMilis(dureTime);
		System.out.println(String.format("[CommunityDetection] Finished, duration: %s", duration));
	}

	public static void GenerateCommunity() {
		AllObjectCommunityDetection communityDetection = new AllObjectCommunityDetection();
	}

}
