/*
 * ToMe25s-Java-Utilities is a collection of common java utilities.
 * Copyright (C) 2020-2021  ToMe25
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tome25.utils.lib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.util.Enumeration;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * This class can extract files from jar archives. You will need to copy this
 * class and {@link LibraryLoader} into your project in order to extract and add
 * this library to the classpath if you packaged it into your main jar.
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
	 * @param extractClasses whether class files should get extracted too.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, boolean extractClasses) throws IOException {
		extractJar(jarFile, extractClasses, jarFile.getParentFile());
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile        the jar file to extract.
	 * @param extractClasses whether class files should get extracted too.
	 * @param subDir         whether files in a directory inside the jar should get
	 *                       put into a sub directory of outputDir or directly into
	 *                       outputDir.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, boolean extractClasses, boolean subDir) throws IOException {
		extractJar(jarFile, extractClasses, jarFile.getParentFile(), subDir);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile        the jar file to extract.
	 * @param extractClasses whether class files should get extracted too.
	 * @param outputDir      the directory to put the extracted files in.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, boolean extractClasses, File outputDir) throws IOException {
		extractJar(jarFile, extractClasses, outputDir, true);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile        the jar file to extract.
	 * @param extractClasses whether class files should get extracted too.
	 * @param outputDir      the directory to put the extracted files in.
	 * @param extractedName  a function that generates the name for the extracted
	 *                       files after extraction. This function gets the filename
	 *                       from inside the jar including directories.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, boolean extractClasses, File outputDir,
			Function<String, String> extractedName) throws IOException {
		extractJar(jarFile, extractClasses, outputDir, true, extractedName);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile        the jar file to extract.
	 * @param extractClasses whether class files should get extracted too.
	 * @param outputDir      the directory to put the extracted files in.
	 * @param subDir         whether files in a directory inside the jar should get
	 *                       put into a sub directory of outputDir or directly into
	 *                       outputDir.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, boolean extractClasses, File outputDir, boolean subDir)
			throws IOException {
		extractJar(jarFile, (s) -> extractClasses || !s.endsWith(".class"), outputDir, subDir);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile        the jar file to extract.
	 * @param extractClasses whether class files should get extracted too.
	 * @param outputDir      the directory to put the extracted files in.
	 * @param subDir         whether files in a directory inside the jar should get
	 *                       put into a sub directory of outputDir or directly into
	 *                       outputDir.
	 * @param extractedName  a function that generates the name for the extracted
	 *                       files after extraction. This function gets the filename
	 *                       from inside the jar including directories if subDir is
	 *                       enabled and just the files name if not.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, boolean extractClasses, File outputDir, boolean subDir,
			Function<String, String> extractedName) throws IOException {
		extractJar(jarFile, (s) -> extractClasses || !s.endsWith(".class"), outputDir, subDir, extractedName);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile the jar file to extract.
	 * @param extract a predicate that defines what files should get extracted.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, Predicate<String> extract) throws IOException {
		extractJar(jarFile, extract, jarFile.getParentFile());
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile the jar file to extract.
	 * @param extract a predicate that defines what files should get extracted.
	 * @param subDir  whether files in a directory inside the jar should get put
	 *                into a sub directory of outputDir or directly into outputDir.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, Predicate<String> extract, boolean subDir) throws IOException {
		extractJar(jarFile, extract, jarFile.getParentFile(), subDir);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile   the jar file to extract.
	 * @param extract   a predicate that defines what files should get extracted.
	 * @param outputDir the directory to put the extracted files in.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, Predicate<String> extract, File outputDir) throws IOException {
		extractJar(jarFile, extract, outputDir, true);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile       the jar file to extract.
	 * @param extract       a predicate that defines what files should get
	 *                      extracted.
	 * @param outputDir     the directory to put the extracted files in.
	 * @param extractedName a function that generates the name for the extracted
	 *                      files after extraction. This function gets the filename
	 *                      from inside the jar including directories.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, Predicate<String> extract, File outputDir,
			Function<String, String> extractedName) throws IOException {
		extractJar(jarFile, extract, outputDir, true, extractedName);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile   the jar file to extract.
	 * @param extract   a predicate that defines what files should get extracted.
	 * @param outputDir the directory to put the extracted files in.
	 * @param subDir    whether files in a directory inside the jar should get put
	 *                  into a sub directory of outputDir or directly into
	 *                  outputDir.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, Predicate<String> extract, File outputDir, boolean subDir)
			throws IOException {
		extractJar(jarFile, extract, outputDir, subDir, (name) -> name);
	}

	/**
	 * Extracts the contents out of the given jar file.
	 * 
	 * This doesn't extract empty directories.
	 * 
	 * @param jarFile       the jar file to extract.
	 * @param extract       a predicate that defines what files should get
	 *                      extracted.
	 * @param outputDir     the directory to put the extracted files in.
	 * @param subDir        whether files in a directory inside the jar should get
	 *                      put into a sub directory of outputDir or directly into
	 *                      outputDir.
	 * @param extractedName a function that generates the name for the extracted
	 *                      files after extraction. This function gets the filename
	 *                      from inside the jar including directories if subDir is
	 *                      enabled and just the files name if not.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractJar(File jarFile, Predicate<String> extract, File outputDir, boolean subDir,
			Function<String, String> extractedName) throws IOException {
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}

		if (!outputDir.isDirectory()) {
			throw new NotDirectoryException(outputDir.toString());
		}

		try (JarFile jar = new JarFile(jarFile)) {
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.isDirectory()) {
					continue;
				}

				if (extract.test(entry.getName())) {
					String name = subDir ? entry.getName()
							: entry.getName().substring(entry.getName().lastIndexOf(File.separatorChar) + 1);
					File output = new File(outputDir, extractedName.apply(name));
					if (!output.toPath().normalize().startsWith(outputDir.toPath())) {
						throw new InvalidPathException(output.toPath().normalize().toString(),
								"A bad zip entry would result in a file getting extracted to this location.");
					}

					if (!output.getParentFile().exists()) {
						output.getParentFile().mkdirs();
					}

					if (!output.getParentFile().isDirectory()) {
						throw new NotDirectoryException(output.getParent());
					}

					try (FileOutputStream fiout = new FileOutputStream(output)) {
						InputStream jarin = jar.getInputStream(entry);
						while (jarin.available() > 0) {
							fiout.write(jarin.read());
						}
					}
				}
			}
		}
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile  the jar file to extract the file from.
	 * @param fileName the name of the file to extract from the jar.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractFileFromJar(File jarFile, String fileName) throws IOException {
		extractFileFromJar(jarFile, fileName, jarFile.getParentFile());
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile   the jar file to extract the file from.
	 * @param fileName  the name of the file to extract from the jar.
	 * @param outputDir the directory to put the extracted files in.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractFileFromJar(File jarFile, String fileName, File outputDir) throws IOException {
		extractFileFromJar(jarFile, fileName, outputDir, true);
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile  the jar file to extract the file from.
	 * @param fileName the name of the file to extract from the jar.
	 * @param subDir   whether files in a directory inside the jar should get put
	 *                 into a sub directory of outputDir or directly into outputDir.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractFileFromJar(File jarFile, String fileName, boolean subDir) throws IOException {
		extractFileFromJar(jarFile, fileName, jarFile.getParentFile(), subDir);
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile       the jar file to extract the file from.
	 * @param fileName      the name of the file to extract from the jar.
	 * @param extractedName the name for the file after extraction.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractFileFromJar(File jarFile, String fileName, String extractedName) throws IOException {
		extractFileFromJar(jarFile, fileName, jarFile.getParentFile(), extractedName);
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile   the jar file to extract the file from.
	 * @param fileName  the name of the file to extract from the jar.
	 * @param outputDir the directory to put the extracted files in.
	 * @param subDir    whether files in a directory inside the jar should get put
	 *                  into a sub directory of outputDir or directly into
	 *                  outputDir.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractFileFromJar(File jarFile, String fileName, File outputDir, boolean subDir)
			throws IOException {
		extractFileFromJar(jarFile, fileName, outputDir, subDir, fileName);
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile       the jar file to extract the file from.
	 * @param fileName      the name of the file to extract from the jar.
	 * @param outputDir     the directory to put the extracted files in.
	 * @param extractedName the name for the file after extraction.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractFileFromJar(File jarFile, String fileName, File outputDir, String extractedName)
			throws IOException {
		extractFileFromJar(jarFile, fileName, outputDir, true, extractedName);
	}

	/**
	 * Extracts the file with the given name from the directory if the name contains
	 * the directory names, or all files with that name if it doesn't.
	 * 
	 * @param jarFile       the jar file to extract the file from.
	 * @param fileName      the name of the file to extract from the jar.
	 * @param outputDir     the directory to put the extracted files in.
	 * @param subDir        whether files in a directory inside the jar should get
	 *                      put into a sub directory of outputDir or directly into
	 *                      outputDir.
	 * @param extractedName the name for the file after extraction.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractFileFromJar(File jarFile, String fileName, File outputDir, boolean subDir,
			String extractedName) throws IOException {
		extractJar(jarFile,
				(name) -> name.equals(fileName) || (name.contains(File.separator)
						&& name.substring(name.lastIndexOf(File.separatorChar) + 1).equals(fileName)),
				outputDir, subDir,
				(name) -> name.contains(File.separator)
						? name.substring(0, name.lastIndexOf(File.separatorChar) + 1) + extractedName
						: extractedName);
	}

	/**
	 * Extracts all jar files from jarFile, as they are potentially libraries, into
	 * the libs directory next to this jar file.
	 * 
	 * @param jarFile the jar file to extract the files from.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractLibsFromJar(File jarFile) throws IOException {
		extractLibsFromJar(jarFile, new File(jarFile.getParent(), "libs"));
	}

	/**
	 * Extracts all jar files from jarFile, as they are potentially libraries, into
	 * the libs directory next to this jar file.
	 * 
	 * @param jarFile   the jar file to extract the files from.
	 * @param outputDir the directory to put the extracted files in.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractLibsFromJar(File jarFile, File outputDir) throws IOException {
		extractLibsFromJar(jarFile, outputDir, true);
	}

	/**
	 * Extracts all jar files from jarFile, as they are potentially libraries, into
	 * the libs directory next to this jar file.
	 * 
	 * @param jarFile   the jar file to extract the files from.
	 * @param outputDir the directory to put the extracted files in.
	 * @param subDir    whether files in a directory inside the jar should get put
	 *                  into a sub directory of outputDir or directly into
	 *                  outputDir.
	 * @throws IOException if an I/O error has occurred.
	 */
	public static void extractLibsFromJar(File jarFile, File outputDir, boolean subDir) throws IOException {
		extractJar(jarFile, (s) -> s.endsWith(".jar"), new File(jarFile.getParent(), "libs"));
	}

	/**
	 * Extracts the ToMe25s-Java-Utilites.jar from jarFile, if it exists, and puts
	 * it into the libs directory next to this jar.
	 * 
	 * @param jarFile the jar file to extract the file from.
	 * @return Whether the file where this library should be extracted to exists,
	 *         after trying to extract it.
	 */
	public static boolean extractThis(File jarFile) {
		return extractThis(jarFile, new File(jarFile.getParent(), "libs"));
	}

	/**
	 * Extracts the ToMe25s-Java-Utilites.jar from jarFile, if it exists, and puts
	 * it into outputDir.
	 * 
	 * @param jarFile   the jar file to extract the file from.
	 * @param outputDir the directory to put the extracted files in.
	 * @return Whether the file where this library should be extracted to exists,
	 *         after trying to extract it.
	 */
	public static boolean extractThis(File jarFile, File outputDir) {
		return extractThis(jarFile, outputDir, false);
	}

	/**
	 * Extracts the ToMe25s-Java-Utilites.jar from jarFile, if it exists, and puts
	 * it into outputDir.
	 * 
	 * @param jarFile   the jar file to extract the file from.
	 * @param outputDir the directory to put the extracted files in.
	 * @param subDir    whether files in a directory inside the jar should get put
	 *                  into a sub directory of outputDir or directly into
	 *                  outputDir.
	 * @return Whether the file where this library should be extracted to exists,
	 *         after trying to extract it. Always returns false if this library is
	 *         in a directory inside the archive it gets extracted from and subDir
	 *         is enabled.
	 */
	public static boolean extractThis(File jarFile, File outputDir, boolean subDir) {
		try {
			extractJar(jarFile, (s) -> s.endsWith("ToMe25s-Java-Utilities.jar"), outputDir, subDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(outputDir, "ToMe25s-Java-Utilities.jar").exists();
	}

}
