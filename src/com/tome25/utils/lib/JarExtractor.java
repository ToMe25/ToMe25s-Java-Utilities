package com.tome25.utils.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * This class can extract files from jar archives. You will need to copy this
 * class and <link>LibraryLoader</link> into your project in order to extract
 * and add this library to the classpath if you packaged it into your main jar.
 * 
 * @author ToMe25
 * 
 */
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
		extractJar(jarFile, extract, jarFile.getParentFile());
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile   the jar file to extract.
	 * @param extract   a predicate that defines what files should get extracted
	 * @param outputDir the directory to put the extracted files in.
	 * @throws IOException if an I/O error has occurred
	 */
	public static void extractJar(File jarFile, Predicate<String> extract, File outputDir) throws IOException {
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		if (!outputDir.isDirectory()) {
			throw new IOException(
					String.format("File %s is not a directory, but would need to be one for the unpacking "
							+ "of this jar archive to work.", outputDir));
		}
		JarFile jar = new JarFile(jarFile);
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			if (entry.isDirectory()) {
				continue;
			}
			if (extract.test(entry.getName())) {
				File output = new File(outputDir, entry.getName());
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
	 * @param jarFile  the jar file to extract the file from.
	 * @param fileName the name of the file to extract from the jar.
	 * @throws IOException
	 */
	public static void extractFileFromJar(File jarFile, String fileName) throws IOException {
		extractJar(jarFile, (s) -> s.equals(fileName)
				|| (s.contains(File.separator) && s.substring(s.lastIndexOf(File.separatorChar) + 1).equals(fileName)));
	}

	/**
	 * Extracts all jar files from jarFile, as they are potentially libraries, into
	 * the libs directory next to this jar file.
	 * 
	 * @param jarFile the jar file to extract the files from.
	 * @throws IOException
	 */
	public static void extractLibsFromJar(File jarFile) throws IOException {
		extractJar(jarFile, (s) -> s.endsWith(".jar"), new File(jarFile.getParent(), "libs"));
	}

	/**
	 * Extracts the ToMe25s-Java-Utilites.jar from jarFile, if it exists, and puts
	 * it into the libs directory next to this jar.
	 * 
	 * @param jarFile the jar file to extract the file from.
	 */
	public static void extractThis(File jarFile) {
		try {
			extractJar(jarFile, (s) -> s.endsWith("ToMe25s-Java-Utilities.jar"), new File(jarFile.getParent(), "libs"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
