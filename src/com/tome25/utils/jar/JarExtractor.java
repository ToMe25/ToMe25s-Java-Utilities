package com.tome25.utils.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarExtractor {

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile        the jar file to extract.
	 * @param extractClasses whether class files should get extracted too
	 * @throws IOException if an I/O error has occurred
	 */
	public static void extractJar(File jarFile, boolean extractClasses) throws IOException {
		extractJar(jarFile, (s) -> extractClasses || !s.endsWith(".class"));
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile the jar file to extract.
	 * @param extract a predicate that defines what files should get extracted
	 * @throws IOException if an I/O error has occurred
	 */
	public static void extractJar(File jarFile, Predicate<String> extract) throws IOException {
		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.isDirectory()) {
				continue;
			}
			if (extract.test(entry.getName())) {
				File output = new File(jarFile.getParent(), entry.getName());
				if (!output.getParentFile().exists()) {
					output.getParentFile().mkdirs();
				}
				if (!output.getParentFile().isDirectory()) {
					jar.close();
					throw new IOException(
							String.format("File %s is not a directory, but would need to be one for the unpacking "
									+ "of this jar archive to work.", output.getParent()));
				}
				FileOutputStream fiout = new FileOutputStream(output);
				InputStream jarin = jar.getInputStream(entry);
				while (jarin.available() > 0) {
					fiout.write(jarin.read());
				}
				fiout.close();
			}
		}
		jar.close();
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile  the jar file to extract a file from.
	 * @param fileName the name of the file to extract from the jar.
	 * @throws IOException
	 */
	public static void extractFileFromJar(File jarFile, String fileName) throws IOException {
		extractJar(jarFile, (s) -> s.equals(fileName)
				|| (s.contains(File.separator) && s.substring(s.lastIndexOf(File.separatorChar) + 1).equals(fileName)));
	}

}
