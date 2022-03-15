package APKData;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

/*
 *    The input is the absolute path of an apk file and its preset output path, and the apktool command tool is called to decompile it
 *    
 */
public class APKTool {
	private String inputPathString;
	private String outputPathString;
	private static int exeCmdExitValue = 0;
	private static Boolean smaliDirFilled = false;

	public APKTool(String inputPath, String outputPath) {
		File file = new File(inputPath);
		try {
			if (file.exists()) {
				/*
				 * apktool 执行命令 apktool d input output
				 */
				String cmdString = "apktool -f d " + inputPath + " -o " + outputPath;
				// System.out.println(String.format("Command to exec: %s", cmdString));
				exeCmd(cmdString);

				if (exeCmdExitValue != 0) {
					// Check if there's an "smali" folder created
					smaliDirFilled = false;
					Path smaliPath = Paths.get(outputPath, "smali");

					if (Files.exists(smaliPath)) {
						try (Stream<Path> entries = Files.list(smaliPath)) {
							smaliDirFilled = entries.findFirst().isPresent();
						}
					}

					if (!smaliDirFilled) {
						File dirToDelete = new File(outputPath);
						deleteDir(dirToDelete);
						System.out.println("[ERROR][APKTool] Can not process the file (No smali dir found) (exit value "
								+ exeCmdExitValue
								+ "): " + inputPath);
						// System.exit(-1);
					}
				}
				// System.out.println(cmdString);
			} else {
				System.err.println("[ERROR][APKTool] Can not find the file: " + inputPath);
				System.exit(-1);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public Boolean apktoolSucces() {
		if (exeCmdExitValue == 0) {
			return true;
		} else {
			if (smaliDirFilled) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static void exeCmd(String cmd) {
		Runtime rnRuntime = Runtime.getRuntime();
		String outInfo = "";
		try {
			Process process = rnRuntime.exec(cmd);
			InputStream in = (process).getErrorStream(); // Get error message output.
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			String line = "";
			while ((line = br.readLine()) != null) {
				outInfo = outInfo + line + "\n";
			}
			System.out.println(outInfo);

			process.waitFor();
			exeCmdExitValue = process.exitValue();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	public static void exeCmd2(String cmd) {
		Runtime rnRuntime = Runtime.getRuntime();
		String outInfo = "";
		try {
			Process proc = rnRuntime.exec(cmd);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

			BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

			// Read any errors from the attempted command
			String s = null;
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}

			// Read the output from the command
			System.out.println("Here is the standard output of the command:\n");
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// InputStream in = (process).getErrorStream(); // Get error message output.
			// BufferedReader br = new BufferedReader(new InputStreamReader(in));

			// exeCmdExitValue = process.waitFor();
			// if (exeCmdExitValue != 0) {
			// String line = "";
			// while ((line = br.readLine()) != null) {
			// outInfo = outInfo + line + "\n";
			// }
			// System.out.println(outInfo);
			// }
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public String getInputPathString() {
		return inputPathString;
	}

	public void setInputPathString(String inputPathString) {
		this.inputPathString = inputPathString;
	}

	public String getOutputPathString() {
		return outputPathString;
	}

	public void setOutputPathString(String outputPathString) {
		this.outputPathString = outputPathString;
	}

	void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					deleteDir(f);
				}
			}
		}
		file.delete();
	}
}
