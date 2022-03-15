package Util.FamilyStatistic;

import TFIDF.AllFamilyInfo;
import ConstantVar.ConstantValue;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/*    The second step of the experiment is to calculate the weight of the sensitive function
 *    Calculate the weight of each sensitive function in each family and write it in MethodWeight.txt 
 * 	  in the FamilyInfo folder of the family
 * 
 */
public class CalculateFamilySensitvieMethodWeight {
	public static String dirPathString;

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
				dirPathString = cmd.getOptionValue(familyDirOpt);
				System.out.println("Using as families dir: " + dirPathString);
			} else {
				dirPathString = ConstantValue.getVar().FAMILIESDIRPATH_STRING;
				System.out.println("No families dir provided, using default: " + dirPathString);
			}
		} catch (ParseException e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("OptionsCli", options);
			System.exit(1);
		}

		System.out.println("[CalculateFamilySensitvieMethodWeight] Start");
		long startTime = System.currentTimeMillis();
		ConstantValue.getVar();
		AllFamilyInfo allFamilyInfo = new AllFamilyInfo(dirPathString);
		allFamilyInfo.writeFamiliesInfo();
		long endTime = System.currentTimeMillis();
		long dureTime = endTime - startTime;
		System.out.println("[CalculateFamilySensitvieMethodWeight] Finished. Time elapsed: " + dureTime + " ms");
	}
}
