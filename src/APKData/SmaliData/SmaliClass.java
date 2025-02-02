package APKData.SmaliData;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/*
 *   Input is a smali file, analyze its internal code, obtain function nodes and their calling relationships
 * 
 */
public class SmaliClass implements Serializable {
	private String classPath;
	private String sourceFileString;
	private SmaliMethod tmpMethod;
	private ArrayList<SmaliMethod> methodList = new ArrayList<>();
	private FileReader fReader;
	private BufferedReader bReader;
	private String lineString;

	public SmaliClass() {
	}

	public SmaliClass(File file) {
		try {
			String filepathString = file.getAbsolutePath();
			if (filepathString.endsWith(".smali")) {
				fReader = new FileReader(file);
				bReader = new BufferedReader(fReader);
				while ((lineString = bReader.readLine()) != null) {
					getInfomation(lineString);
				}
				bReader.close();
				fReader.close();
				analysisMethod(); // start extract the information from the methodList
			}

			else {
				System.out.println("This file" + filepathString + " is not a smali format file!");
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		// showClassInfomation();
	}

	/*
	 * analysis each line in the smali file , extract the method context
	 */
	public String getInfomation(String line) {
		if (line.startsWith(".class ")) {
			int k = line.lastIndexOf(" ");
			classPath = line.substring(k + 1, line.length() - 1);
			classPath = classPath.replace("L", "");
			return classPath;
		} else if (line.startsWith(".source")) {
			int k = line.lastIndexOf(" ");
			sourceFileString = line.substring(k + 1);
			return sourceFileString;
		} else if (line.startsWith(".method")) {
			tmpMethod = new SmaliMethod(this.classPath); // Pass the name of the class where the current function is
															// located
			tmpMethod.addSmaliCode(line);
			try {
				while (!((lineString = bReader.readLine()).startsWith(".end method"))) {
					tmpMethod.addSmaliCode(lineString);
				}
				tmpMethod.addSmaliCode(lineString);

				methodList.add(tmpMethod);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	public void analysisMethod() {
		if (methodList.size() > 0) {
			for (int i = 0; i < methodList.size(); i++) {
				methodList.get(i).extractCallInformation();
			}
		}
	}

	public void showClassInfomation() {
		System.out.println("ClassPath: " + classPath);
		System.out.println("SourceFile: " + sourceFileString);
		System.out.println("Method Number: " + methodList.size());
		for (int i = 0; i < methodList.size(); i++) {
			methodList.get(i).showCallingInformation();
		}
	}

	public ArrayList<SmaliMethod> getMethodList() {
		return methodList;
	}

	public void setMethodList(ArrayList<SmaliMethod> methodList) {
		this.methodList = methodList;
	}

}
