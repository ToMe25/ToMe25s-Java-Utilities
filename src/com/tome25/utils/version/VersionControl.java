package com.tome25.utils.version;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

public class VersionControl {

	private static File file;
	private static JarFile archieve;

	/**
	 * gets this libraries version attribute(only applicable if the
	 * ToMe25s-Java-Utilities-Version attribute in the MANIFEST.MF exists), if this
	 * is still in a Jar, if not this will return 0;
	 * 
	 * @return the version number without 1.(e.g. if the version is 1.19 this will
	 *         return 19)
	 */
	public static int getVersion() {
		String version = getVersionString();
		return Integer.parseInt(version.substring(version.lastIndexOf('.') + 1));
	}

	/**
	 * gets this libraries version attribute(only applicable if the
	 * ToMe25s-Java-Utilities-Version attribute in the MANIFEST.MF exists), if this
	 * is still in a Jar, if not this will return 1.0;
	 * 
	 * @return the version number
	 */
	public static String getVersionString() {
		if (file == null) {
			file = new File(VersionControl.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		}
		if (file.isDirectory()) {
			return "1.0";
		}
		if (archieve == null) {
			try {
				archieve = new JarFile(file);
			} catch (IOException e) {
				e.printStackTrace();
				return "1.0";
			}
		}
		try {
			return archieve.getManifest().getMainAttributes().getValue("ToMe25s-Java-Utilities-Version");
		} catch (IOException e) {
			e.printStackTrace();
			return "1.0";
		}
	}

}
