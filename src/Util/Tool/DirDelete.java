package Util.Tool;

import java.io.File;
import java.nio.file.Files;

public class DirDelete {
	private String dirPath = "";

	public DirDelete() {
	}

	public boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static void deleteDirFiles(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					deleteDirFiles(f);
				}
			}
		}
		file.delete();
	}
}
