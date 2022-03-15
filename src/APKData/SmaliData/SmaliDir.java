package APKData.SmaliData;

import java.io.File;
import java.util.ArrayList;

/*
 *     The input is an apk decompiled folder object, File type
 *     And built a file tree class, the middle node on the tree represents the folder, 
 *     and the leaf node represents the smali file
 */
public class SmaliDir {
	private String smaliDirPath; // 当前smali文件夹的绝对路径
	private fileTree tree; // 文件数对象
	private ArrayList<String> classFilePathList = new ArrayList<>();
	private ArrayList<String> dirFilePathList = new ArrayList<>();
	private ArrayList<SmaliClass> classesList = new ArrayList<>();
	private int fileNum;

	public SmaliDir(File file) {
		String apkPathString = file.getAbsolutePath();
		String smaliDirPath = apkPathString + "/smali/";
		File smaliDirFile = new File(smaliDirPath);
		if (smaliDirFile.exists()) {
			this.smaliDirPath = smaliDirFile.getAbsolutePath() + "/";
			tree = new fileTree(smaliDirFile); // build file tree
			fileNum = classFilePathList.size();
			// System.out.println("file: " + file.getAbsolutePath());
			// System.out.println("number of folders: " + dirFilePathList.size());
			// System.out.println("number of files: " + fileNum);
		} else {
			System.err.println("[Error][SmaliDir] There is no smali file in the apk file: " + apkPathString);
			// System.exit(-1);
		}
	}

	public String getSmaliDirPath() {
		return smaliDirPath;
	}

	public void setSmaliDirPath(String smaliDirPath) {
		this.smaliDirPath = smaliDirPath;
	}

	public fileTree getTree() {
		return tree;
	}

	public void setTree(fileTree tree) {
		this.tree = tree;
	}

	public ArrayList<String> getClassFilePathList() {
		return classFilePathList;
	}

	public void setClassFilePathList(ArrayList<String> classFilePathList) {
		this.classFilePathList = classFilePathList;
	}

	public ArrayList<String> getDirFilePathList() {
		return dirFilePathList;
	}

	public void setDirFilePathList(ArrayList<String> dirFilePathList) {
		this.dirFilePathList = dirFilePathList;
	}

	public ArrayList<SmaliClass> getClassesList() {
		return classesList;
	}

	public void setClassesList(ArrayList<SmaliClass> classesList) {
		this.classesList = classesList;
	}

	public int getFileNum() {
		return fileNum;
	}

	public void setFileNum(int fileNum) {
		this.fileNum = fileNum;
	}

	/*
	 * the files construct a tree,each node stands for a file
	 */
	class fileTree {
		private fileNode rootFileNode;

		public fileTree(File file) {
			rootFileNode = new fileNode(file);
			addTreeNode(rootFileNode);
		}

		public void addTreeNode(fileNode tmpNode) {
			File[] files = tmpNode.nodeFile.listFiles();
			if (files.length > 0) {
				for (int i = 0; i < files.length; i++) {
					fileNode node = new fileNode(files[i]);
					tmpNode.addNode(node);
					if (files[i].isDirectory()) {
						dirFilePathList.add(files[i].getAbsolutePath());
						addTreeNode(node);
					} else { // this node is a file which end with smali , then analysis this file
						classFilePathList.add(files[i].getAbsolutePath());
						SmaliClass smaliClass = new SmaliClass(files[i]);
						classesList.add(smaliClass);
					}
				}
			}
		}
	}

	/*
	 * 
	 */
	class fileNode {
		private File nodeFile;
		private fileNode parentNode;
		private String nodeType; // 若为 dir 表示该节点为文件夹， 若为 File 表示该节点为smali文件
		private ArrayList<fileNode> childrenNodes = new ArrayList<>();

		public fileNode(File file) {
			this.nodeFile = file;
			parentNode = null;
			if (file.isDirectory()) {
				this.nodeType = "Dir";
			} else {
				this.nodeType = "File";
			}
		}

		public void addNode(fileNode node) {
			node.parentNode = this;
			this.childrenNodes.add(node);
		}

	}
}
