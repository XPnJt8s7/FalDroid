package Util.FamilyStatistic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import APKData.SmaliGraph.SICG;
import ConstantVar.ConstantValue;
import Util.Tool.CopyDir;
import Util.Tool.DirDelete;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/*  
 *  The first step of the whole experiment: decompilation
 *  Input is a target folder, all objects in the folder are apk files, 
 *  create a new apktool file, and check all the objects in the folder.
 *  Objects are decompiled and corresponding analysis files are generated
 */
public class DirAnalysis {
	// public static String dirString =
	// ConstantValue.getVar().FAMILIESDIRPATH_STRING;
	public static String dirString;
	// public static String dirString = "/home/fan/lab/Family/Drebin/DreTest/";
	public static ArrayList<Double> timeList = new ArrayList<>();
	public static long analysisStartTime = 0;
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
	public static Instant instant;
	public static ZonedDateTime zdt;
	public static String output;
	public static String apktoolTmpfsDirString = "";
	public static ArrayList<String> apkDiStrings = new ArrayList<String>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Options options = new Options();
		Option helpOpt = new Option("h", "help", false, "print this message");
		Option familyDirOpt = Option.builder("f").longOpt("families-dir")
				.argName("path")
				.hasArg()
				.desc("Specify the families directory")
				.build();

		Option tmpfsDirOpt = Option.builder("t").longOpt("tmpfs-dir")
				.argName("path")
				.hasArg()
				.required()
				.desc("Path to the tmpfs directory")
				.build();
		options.addOption(tmpfsDirOpt);

		// create Options object
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

			apktoolTmpfsDirString = cmd.getOptionValue(tmpfsDirOpt);

			if (cmd.hasOption(familyDirOpt)) {
				dirString = cmd.getOptionValue(familyDirOpt);
				System.out.println("Using as family dir: " + dirString);
			} else {
				dirString = ConstantValue.getVar().FAMILIESDIRPATH_STRING;
				System.out.println("No family dir provided, using default: " + dirString);
			}

		} catch (ParseException e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("OptionsCli", options);
			System.exit(1);
		}

		analysisStartTime = System.currentTimeMillis();

		File file = new File(dirString);
		File family[] = file.listFiles();
		for (int i = 0; i < family.length; i++) {
			// if(family[i].getName().contains("ExploitLinuxLotoor")){
			System.out.println("Checking path " + family[i]);
			analysis(family[i].getAbsolutePath() + "/");
			// }
		}

		System.out.println("Finish!");
		long endTime = System.currentTimeMillis();

		long useTime = endTime - analysisStartTime;

		instant = Instant.ofEpochMilli(useTime);
		zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
		output = formatter.format(zdt);

		System.out.println("Time elapsed for this analysis: " + output);

		String lastDiString = file.getName();
		FileWriter writer;
		try {
			writer = new FileWriter(lastDiString + ".apks_that_didnt_work.txt");
			for (String str : apkDiStrings) {
				writer.write(str + System.lineSeparator());
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// long dureTime = (endTime - analysisStartTime) / 3600000;
		// System.out.println(dureTime + " hours");
		// String
		// writeFilePath="/home/fan/data/Family/result-final/useTime/dis-Test.txt";
		// writeFile(writeFilePath);
	}

	public static void analysis(String inputDirString) {
		File dirFile = new File(inputDirString + "apktool/");
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}

		File file = new File(inputDirString);
		File apks[] = file.listFiles();
		String inApkPath;
		String outDirPath;
		String outApktoolDirPathString;
		String apkName;
		int totalApks = apks.length;
		int numApk = 0;

		for (int i = 0; i < totalApks; i++) {
			numApk += 1;
			if (apks[i].getAbsolutePath().endsWith(".apk")) {
				long startTime = System.currentTimeMillis();

				inApkPath = apks[i].getAbsolutePath();
				apkName = apks[i].getName();
				outDirPath = dirFile.getAbsolutePath() + "/" + apks[i].getName();
				outApktoolDirPathString = Paths.get(apktoolTmpfsDirString, "apktool", apkName).toString();
				// System.out.println("[DirAnalysis] Apktool output dir: " +
				// outApktoolDirPathString);
				// System.out.println("[DirAnalysis] Directory to output to: " + outDirPath);
				// pressEnterToContinue();

				Path outDirPathPath = Paths.get(outDirPath + "/SICG/");

				// System.out.println(String.format("[DirAnalysis] Checking if path %s exists",
				// outDirPath));
				if (Files.exists(outDirPathPath)) {
					// System.out.println(String.format("[DirAnalysis] Path %s already exists,
					// continuing", outDirPath));
					continue;
				}
				System.out.println("[" + numApk + "/" + totalApks + "][DirAnalysis] Analyzing file " + inApkPath);

				SICG sicg = new SICG(inApkPath, outApktoolDirPathString);

				// Copy from tmpfs to normal
				File outApktoolDir = new File(outApktoolDirPathString);
				File outDir = new File(outDirPath);

				try {
					CopyDir.copyDirectoryCompatibityMode(outApktoolDir, outDir);
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println(String.format("[ERROR][DirAnalysis] Cannot copy %s to %s. Exiting",
							outApktoolDir.toString(), outDir.toPath()));
					apkDiStrings.add(inApkPath);
				}

				DirDelete.deleteDirFiles(outApktoolDir);

				long endTime = System.currentTimeMillis();
				long useTime = endTime - startTime;

				instant = Instant.ofEpochMilli(useTime);
				zdt = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC);
				output = formatter.format(zdt);

				System.out.println("[" + numApk + "/" + totalApks + "] APK analysis finished, duration: " + output);
				// pressEnterToContinue();
				timeList.add(Double.valueOf(useTime));
			}
		}
	}

	private static void pressEnterToContinue() {
		System.out.println("Press Enter key to continue...");
		try {
			System.in.read();
		} catch (Exception e) {
		}
	}

	public static void writeFile(String filePath) {
		try {
			File file = new File(filePath);
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter bWriter = new BufferedWriter(fileWriter);
			for (int i = 0; i < timeList.size(); i++) {
				bWriter.write(timeList.get(i) + "," + "\n");
			}
			bWriter.close();
			fileWriter.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
