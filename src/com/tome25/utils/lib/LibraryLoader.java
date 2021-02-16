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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * 
 * This class can add Jar Files to the classpath at runtime. You will need to
 * copy this class and {@link JarExtractor} and/or {@link LibraryDownloader}
 * into your project in order to add this library to the classpath at
 * runtime(requires a restart as of version 1.0.126). This class can be used as
 * a java agent, to add stuff to the classpath without a restart, this behavior
 * is deprecated tho.
 * 
 * @author ToMe25
 *
 */
public class LibraryLoader {

	private static final URL MANIFEST = getManifest();

	@Deprecated
	private static Instrumentation instrumentation;
	private static byte[] buffer;
	private static String[] mainArgs;
	private static File mainFile;
	private static Manifest manifest;

	/**
	 * Initializes a LibraryLoader, restarts your JVM if necessary, tries to
	 * download ToMe25s-Java-Utilities, if that doesn't work it tries to extract it
	 * from this Jar and adds it to the classpath.
	 * 
	 * @param args the program arguments.
	 */
	public static void init(String[] args) {
		init(args, null);
	}

	/**
	 * Initializes a LibraryLoader, restarts your JVM if necessary, tries to
	 * download ToMe25s-Java-Utilities, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the system outputs to
	 * {@link com.tome25.utils.logging.LoggingPrintStream} printing to a logger
	 * writing to the log files and the previous {@link java.io.OutputStream}s.
	 * 
	 * @param args    the program arguments.
	 * @param logFile the log file for System.out and System.err. set to null to
	 *                disable changing System.out and System.err.
	 */
	public static void init(String[] args, File logFile) {
		init(args, logFile, LibraryDownloader.DEFAULT_TOME25S_JAVA_UTILITIES_URL_STORAGE);
	}

	/**
	 * Initializes a LibraryLoader, restarts your JVM if necessary, tries to
	 * download ToMe25s-Java-Utilities, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the system outputs to
	 * {@link com.tome25.utils.logging.LoggingPrintStream} printing to a logger
	 * writing to the log files and the previous {@link java.io.OutputStream}s.
	 * 
	 * @param args              the program arguments.
	 * @param logFile           the log file for System.out and System.err. set to
	 *                          null to disable changing System.out and System.err.
	 * @param defaultUrlStorage the default contents for the URL storage file that
	 *                          lists the URLs to try and download
	 *                          ToMe25s-Java-Utilities from.
	 */
	public static void init(String[] args, File logFile, String defaultUrlStorage) {
		init(args, logFile, logFile, defaultUrlStorage);
	}

	/**
	 * Initializes a LibraryLoader, restarts your JVM if necessary, tries to
	 * download ToMe25s-Java-Utilities, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the system outputs to
	 * {@link com.tome25.utils.logging.LoggingPrintStream} printing to a logger
	 * writing to the log files and the previous {@link java.io.OutputStream}s.
	 * 
	 * @param args          the program arguments.
	 * @param outputLogFile the log file for System.out. set to null to disable
	 *                      changing System.out.
	 * @param errorLogFile  the log file for System.err. set to null to disable
	 *                      changing System.err.
	 */
	public static void init(String[] args, File outputLogFile, File errorLogFile) {
		init(args, outputLogFile, errorLogFile, LibraryDownloader.DEFAULT_TOME25S_JAVA_UTILITIES_URL_STORAGE);
	}

	/**
	 * Initializes a LibraryLoader, restarts your JVM if necessary, tries to
	 * download ToMe25s-Java-Utilities, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the system outputs to
	 * {@link com.tome25.utils.logging.LoggingPrintStream} printing to a logger
	 * writing to the log files and the previous {@link java.io.OutputStream}s.
	 * 
	 * @param args              the program arguments.
	 * @param outputLogFile     the log file for System.out. set to null to disable
	 *                          changing System.out.
	 * @param errorLogFile      the log file for System.err. set to null to disable
	 *                          changing System.err.
	 * @param defaultUrlStorage the default contents for the URL storage file that
	 *                          lists the URLs to try and download
	 *                          ToMe25s-Java-Utilities from.
	 */
	public static void init(String[] args, File outputLogFile, File errorLogFile, String defaultUrlStorage) {
		init(args, outputLogFile, errorLogFile, defaultUrlStorage, false);
	}

	/**
	 * Initializes a LibraryLoader, restarts your JVM if necessary, tries to
	 * download ToMe25s-Java-Utilities, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the system outputs to
	 * {@link com.tome25.utils.logging.LoggingPrintStream} printing to a logger
	 * writing to the log files and the previous {@link java.io.OutputStream}s.
	 * 
	 * @param args              the program arguments.
	 * @param outputLogFile     the log file for System.out. set to null to disable
	 *                          changing System.out.
	 * @param errorLogFile      the log file for System.err. set to null to disable
	 *                          changing System.err.
	 * @param defaultUrlStorage the default contents for the URL storage file that
	 *                          lists the URLs to try and download
	 *                          ToMe25s-Java-Utilities from.
	 * @param update            whether to download/extract the file if it already
	 *                          exists.
	 */
	public static void init(String[] args, File outputLogFile, File errorLogFile, String defaultUrlStorage,
			boolean update) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		PrintStream pb = new PrintStream(buf);
		if (MANIFEST != null) {
			File program = new File(MANIFEST.getFile().substring(5)).getParentFile().getParentFile();
			program = new File(program.toString().substring(0, program.toString().length() - 1));
			boolean missing = !new File(new File(program.getParent(), "libs"), "ToMe25s-Java-Utilities.jar").exists();
			boolean created = false;
			if (missing || update) {
				LibraryDownloader downloader = new LibraryDownloader(
						new File(program.getParent(), "ToMe25s-Java-Utilities-Download-Url.txt"), defaultUrlStorage,
						true, true);
				if (downloader.downloadFile()) {
					pb.format("Successfully downloaded ToMe25s-Java-Utilites from %s.%n",
							downloader.getDownloadUrl().toString());
					created = missing;
				} else if (JarExtractor.extractThis(program)) {
					pb.format("Successfully extracted ToMe25s-Java-Utilites from %s.%n", program.toString());
					created = missing;
				}
			}
			setArgs(args);
			addLibsToClasspath();
			if (created) {
				restart();
			}
		}
		try {
			com.tome25.utils.logging.LogTracer.traceOutputs(outputLogFile, errorLogFile);// importing this would cause
																							// it to crash on loading.
			pb.close();
			if (buf.size() > 0) {
				System.out.print(buf.toString());
			}
			buffer = buf.toByteArray();
			buf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new LibraryLoader, it is recommended to do this at the start of
	 * your program, as this may need to restart to JVM, which is what it needs the
	 * arguments from your main method for. Part of the Java Agent.
	 * 
	 * @param mainArgs the main methods arguments.
	 * @throws IOException if this program isn't a jar or doesn't exists. And if
	 *                     somehow creating a {@link JarFile} instance fails.
	 * @deprecated use the non java agent library loading instead.
	 */
	@Deprecated
	public LibraryLoader(String[] mainArgs) throws IOException {
		if (instrumentation == null) {
			File file = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			if (!file.exists()) {
				throw new FileNotFoundException(
						"This programs Code Source doesn't exists, most likely it got deleted while running!");
			} else if (!file.isFile()) {
				throw new FileNotFoundException(
						"This programs Code Source isn't a file, most likely this is run before being packaged "
								+ "into a jar, but this can't work then!");
			}
			file.setReadable(true);
			file.setWritable(true);
			file.setExecutable(true);
			JarFile jar = new JarFile(file);
			if (jar.getManifest().getMainAttributes().get(new Attributes.Name("Premain-Class")) == null) {
				jar.getManifest().getMainAttributes().put(new Attributes.Name("Premain-Class"),
						this.getClass().getName());
				File tempFile = new File(file.getParent(), "tmp.jar");
				tempFile.deleteOnExit();
				copyJar(file, tempFile, jar.getManifest());
				copyJar(tempFile, file);
				jar.close();
				tempFile.delete();
			}
			ProcessBuilder pb = new ProcessBuilder("java", "-javaagent:" + file.getAbsolutePath(), "-jar",
					file.getAbsolutePath(),
					stringArrayToString(
							ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0])),
					stringArrayToString(mainArgs));
			pb.inheritIO();
			pb.start();
			System.exit(0);
		}
	}

	/**
	 * The premain method needed for this Java Agent to work. Part of the Java
	 * Agent.
	 * 
	 * @param args            some arguments.
	 * @param instrumentation the instrumentation instance to use.
	 * @deprecated use the non java agent library loading instead.
	 */
	@Deprecated
	public static void premain(String args, Instrumentation instrumentation) {
		LibraryLoader.instrumentation = instrumentation;
	}

	/**
	 * Converts a string array to a string. Entries are separated with a space.
	 * 
	 * @param array the array to get the string representation of.
	 * @return a string representation of the given string array.
	 * @deprecated use {@link java.util.Arrays#toString(Object[])} instead.
	 */
	@Deprecated
	private static String stringArrayToString(String[] array) {
		String string = "";
		for (String str : array) {
			string += str + " ";
		}
		if (string.length() > 0) {
			string = string.substring(0, string.length() - 1);
		}
		return string;
	}

	/**
	 * Adds the given library jar to the classpath. Part of the Java Agent.
	 * 
	 * @param library the jar archive that should get added to the classpath.
	 * @throws IOException if an I/O error has occurred.
	 * @deprecated use the non java agent library loading instead.
	 */
	@Deprecated
	public void addJarToClasspath(String library) throws IOException {
		addJarToClasspath(new File(library));
	}

	/**
	 * Adds the given library jar to the classpath. Part of the Java Agent.
	 * 
	 * @param library the jar archive that should get added to the classpath.
	 * @throws IOException if an I/O error has occurred.
	 * @deprecated use the non java agent library loading instead.
	 */
	@Deprecated
	public void addJarToClasspath(File library) throws IOException {
		addJarToClasspath(new JarFile(library));
	}

	/**
	 * Adds the given library jar to the classpath. Part of the Java Agent.
	 * 
	 * @param library the jar archive that should get added to the classpath.
	 * @deprecated use the non java agent library loading instead.
	 */
	@Deprecated
	public void addJarToClasspath(JarFile library) {
		instrumentation.appendToSystemClassLoaderSearch(library);
	}

	/**
	 * Adds all jar archives in the given directory to the classpath. Part of the
	 * Java Agent.
	 * 
	 * @param libDir the directory containing the libraries.
	 * @throws IOException if an I/O error has occurred.
	 * @deprecated use the non java agent library loading instead.
	 */
	@Deprecated
	public void addJarsToClasspath(String libDir) throws IOException {
		addJarsToClasspath(new File(libDir));
	}

	/**
	 * Adds the given library jar to the classpath. Part of the Java Agent.
	 * 
	 * @param libDir the directory containing the libraries.
	 * @throws IOException if an I/O error has occurred.
	 * @deprecated use the non java agent library loading instead.
	 */
	@Deprecated
	public void addJarsToClasspath(File libDir) throws IOException {
		if (!libDir.isDirectory()) {
			throw new IOException("The file " + libDir.getAbsolutePath() + " isn't a directory!");
		}
		for (File file : libDir.listFiles()) {
			if (file.getName().endsWith(".jar")) {
				addJarToClasspath(file);
			}
		}
	}

	/**
	 * Adds ToMe25s-Java-Utilities to the classpath of it is in the libs directory
	 * next to this jar. Automatically restarts this software if necessary.
	 */
	public static void addThisToClasspath() {
		File lib = new File(MANIFEST.getFile().substring(5)).getParentFile().getParentFile().getParentFile();
		lib = new File(lib, "libs");
		lib = new File(lib, "ToMe25s-Java-Utilities.jar");
		try {
			addToClasspath(lib, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the library with the given name inside the libs directory to the
	 * classpath.
	 * 
	 * @param library the library to add.
	 */
	public static void addLibToClasspath(String library) {
		if (!library.endsWith(".jar") && !library.endsWith("*")) {
			library += ".jar";
		}
		File lib = new File(MANIFEST.getFile().substring(5)).getParentFile().getParentFile().getParentFile();
		lib = new File(lib, "libs");
		lib = new File(lib, library);
		try {
			addToClasspath(lib);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds everything inside the libs folder to the classpath. Sadly just adding a
	 * "*" file in the directory doesn't work in the MANIFEST.MF attribute, so this
	 * looks for all .jar files that don't have "sources" or "javadoc" in their name
	 * in the libs directory. Automatically restarts this software if necessary.
	 */
	public static void addLibsToClasspath() {
		addLibsToClasspath(name -> name.endsWith(".jar") && !name.contains("sources") && !name.contains("javadoc"));
	}

	/**
	 * Adds everything inside the libs folder to the classpath. Sadly just adding a
	 * "*" file in the directory doesn't work in the MANIFEST.MF attribute, so this
	 * looks for all files matching the libraryChecker predicate. Automatically
	 * restarts this software if necessary.
	 * 
	 * @param libraryChecker the predicate that checks what files to add to the
	 *                       classpath.
	 */
	public static void addLibsToClasspath(Predicate<String> libraryChecker) {
		if (MANIFEST == null)
			return;
		try {
			File libs = new File(MANIFEST.getFile().substring(5)).getParentFile().getParentFile().getParentFile();
			libs = new File(libs, "libs");
			boolean restart = false;
			for (String file : libs.list()) {
				if (libraryChecker.test(file)) {
					restart = addToClasspath(new File("libs", file)) || restart;
				}
			}
			if (restart) {
				restart();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the given string to the classpath.
	 * 
	 * @param path the path to add.
	 * @return whether a restart is necessary.
	 * @throws IOException if something goes wrong.
	 */
	public static boolean addToClasspath(Path path) throws IOException {
		return addToClasspath(path.toString());
	}

	/**
	 * Adds the given string to the classpath.
	 * 
	 * @param path the path to add.
	 * @return whether a restart is necessary.
	 * @throws IOException if something goes wrong.
	 */
	public static boolean addToClasspath(File path) throws IOException {
		return addToClasspath(path.getPath());
	}

	/**
	 * Adds the given string to the classpath.
	 * 
	 * @param path the path to add.
	 * @return whether a restart is necessary.
	 * @throws IOException if something goes wrong.
	 */
	public static boolean addToClasspath(String path) throws IOException {
		if (MANIFEST == null) {
			throw new FileNotFoundException(
					"This programs MANIFEST.MF doesn't exist, most likely this is run before being packaged "
							+ "into a jar, but this can't work then!");
		}
		File program = new File(MANIFEST.getFile().substring(5)).getParentFile().getParentFile();
		program = new File(program.toString().substring(0, program.toString().length() - 1));
		if (manifest == null) {
			manifest = new Manifest(MANIFEST.openStream());
		}
		String classpath = manifest.getMainAttributes().getValue("Class-Path");
		if (classpath == null)
			classpath = "";
		if (!classpath.contains(path)) {
			if (!classpath.isEmpty()) {
				classpath += ' ';
			}
			classpath += path;
			manifest.getMainAttributes().putValue("Class-Path", classpath);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Adds the given string to the classpath.
	 * 
	 * @param path    the path to add.
	 * @param restart whether to automatically restart this JVM if necessary.
	 *                Requires setArgs to be run first!
	 * @throws IOException if something goes wrong.
	 */
	public static void addToClasspath(Path path, boolean restart) throws IOException {
		addToClasspath(path.toString(), restart);
	}

	/**
	 * Adds the given string to the classpath.
	 * 
	 * @param path    the path to add.
	 * @param restart whether to automatically restart this JVM if necessary.
	 *                Requires setArgs to be run first!
	 * @throws IOException if something goes wrong.
	 */
	public static void addToClasspath(File path, boolean restart) throws IOException {
		addToClasspath(path.getPath(), restart);
	}

	/**
	 * Adds the given string to the classpath.
	 * 
	 * @param path    the path to add.
	 * @param restart whether to automatically restart this JVM if necessary.
	 *                Requires setArgs to be run first!
	 * @throws IOException if something goes wrong.
	 */
	public static void addToClasspath(String path, boolean restart) throws IOException {
		if (addToClasspath(path) && restart) {
			restart();
		}
	}

	/**
	 * Removes the given string from the classpath.
	 * 
	 * @param path the path to remove.
	 * @return whether a restart is necessary.
	 * @throws IOException if something goes wrong.
	 */
	public static boolean removeFromClasspath(Path path) throws IOException {
		return removeFromClasspath(path.toString());
	}

	/**
	 * Removes the given string from the classpath.
	 * 
	 * @param path the path to remove.
	 * @return whether a restart is necessary.
	 * @throws IOException if something goes wrong.
	 */
	public static boolean removeFromClasspath(File path) throws IOException {
		return removeFromClasspath(path.getPath());
	}

	/**
	 * Removes the given string from the classpath.
	 * 
	 * @param path the path to remove.
	 * @return whether a restart is necessary.
	 * @throws IOException if something goes wrong.
	 */
	public static boolean removeFromClasspath(String path) throws IOException {
		if (MANIFEST == null) {
			throw new FileNotFoundException(
					"This programs MANIFEST.MF doesn't exist, most likely this is run before being packaged "
							+ "into a jar, but this can't work then!");
		}
		File program = new File(MANIFEST.getFile().substring(5)).getParentFile().getParentFile();
		program = new File(program.toString().substring(0, program.toString().length() - 1));
		if (manifest == null) {
			manifest = new Manifest(MANIFEST.openStream());
		}
		String classpath = manifest.getMainAttributes().getValue("Class-Path");
		if (classpath.contains(path + ' ') || classpath.endsWith(path)) {
			if (classpath.contains(path + ' ')) {
				classpath = classpath.replaceAll(path + ' ', "");
			} else if (classpath.contains(' ' + path)) {
				classpath = classpath.replaceAll(' ' + path, "");
			} else if (classpath.endsWith(path)) {
				classpath = classpath.substring(0, classpath.length() - path.length());
			}
			manifest.getMainAttributes().putValue("Class-Path", classpath);
			return true;
		} else {
			System.err.format("Couldn't remove %s from the classpath, as it wasn't there.%n", path);
			return false;
		}
	}

	/**
	 * Removes the given string from the classpath.
	 * 
	 * @param path    the path to remove.
	 * @param restart whether to automatically restart this JVM if necessary.
	 *                Requires setArgs to be run first!
	 * @throws IOException if something goes wrong.
	 */
	public static void removeFromClasspath(Path path, boolean restart) throws IOException {
		removeFromClasspath(path.toString(), restart);
	}

	/**
	 * Removes the given string from the classpath.
	 * 
	 * @param path    the path to remove.
	 * @param restart whether to automatically restart this JVM if necessary.
	 *                Requires setArgs to be run first!
	 * @throws IOException if something goes wrong.
	 */
	public static void removeFromClasspath(File path, boolean restart) throws IOException {
		removeFromClasspath(path.getPath(), restart);
	}

	/**
	 * Removes the given string from the classpath.
	 * 
	 * @param path    the path to remove.
	 * @param restart whether to automatically restart this JVM if necessary.
	 *                Requires setArgs to be run first!
	 * @throws IOException if something goes wrong.
	 */
	public static void removeFromClasspath(String path, boolean restart) throws IOException {
		if (removeFromClasspath(path) && restart) {
			restart();
		}
	}

	/**
	 * Sets the main args to be used for eventual restarts.
	 * 
	 * @param args the arguments of the main method.
	 */
	public static void setArgs(String[] args) {
		mainArgs = args;
	}

	/**
	 * If init got used, this is the way to get its output buffer.
	 * 
	 * @return the output buffer used in init.
	 */
	public static byte[] getBuffer() {
		return buffer;
	}

	/**
	 * Returns the previously set main method arguments.
	 * 
	 * @return the previously set main method arguments.
	 * @deprecated use {@link #getMainArgsArray} instead.
	 */
	@Deprecated
	public static String getMainArgs() {
		return stringArrayToString(mainArgs);
	}

	/**
	 * Returns the previously set main method arguments.
	 * 
	 * @return the previously set main method arguments.
	 */
	public static String[] getMainArgsArray() {
		return mainArgs;
	}

	/**
	 * Restarts this JVM. Requires setArgs to be run first! Rewrites the manifest if
	 * necessary.
	 */
	public static void restart() {
		File program = new File(MANIFEST.getFile().substring(5)).getParentFile().getParentFile();
		program = new File(program.toString().substring(0, program.toString().length() - 1));

		try {
			Manifest man = new Manifest(MANIFEST.openStream());
			if (!man.equals(manifest)) {
				File tempFile = new File(program.getParent(), "tmp.jar");
				tempFile.deleteOnExit();
				program.setWritable(true);
				copyJar(program, tempFile, manifest);
				copyJar(tempFile, program);
				tempFile.delete();
				program.setExecutable(true);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<String> command = new ArrayList<String>();
		command.add("java");
		command.add("-jar");
		command.add(program.getAbsolutePath());
		command.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
		command.addAll(Arrays.asList(getMainArgsArray()));
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.inheritIO();
		try {
			pb.start();
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the {@link URL} of the currently running jar file. Null if the manifest
	 * was not found.
	 * 
	 * @return the {@link URL} of the currently running jar file. Null if the
	 *         manifest was not found.
	 */
	private static URL getManifest() {
		try {
			Enumeration<URL> manifests = Thread.currentThread().getContextClassLoader()
					.getResources("META-INF/MANIFEST.MF");
			if (LibraryLoader.class.getPackage().getImplementationTitle() == null) {
				System.out.println("This package has no implementation title. This is most likely because "
						+ "it is being run in a development environment, but this function can't work then.");
				return null;
			}

			while (manifests.hasMoreElements()) {
				URL manifestLocation = manifests.nextElement();
				Manifest manifest = new Manifest(manifestLocation.openStream());
				if (LibraryLoader.class.getPackage().getImplementationTitle()
						.equalsIgnoreCase(manifest.getMainAttributes().getValue("Implementation-Title"))) {
					return manifestLocation;
				}
			}
		} catch (IOException e) {
		}
		return null;
	}

	/**
	 * Copies the jar file from input to output.
	 * 
	 * @param input  the location to copy the jar from.
	 * @param output the location to copy the jar to.
	 */
	public static void copyJar(File input, File output) {
		copyJar(input, output, null);
	}

	/**
	 * Copies the jar file from input to output, replacing the MANIFEST.MF file with
	 * the given one.
	 * 
	 * @param input    the location to copy the jar from.
	 * @param output   the location to copy the jar to.
	 * @param manifest the manifest of the output jar. Set to null to use the one
	 *                 from the input file.
	 */
	public static void copyJar(File input, File output, Manifest manifest) {
		try {
			JarFile jar = new JarFile(input);
			if (manifest == null) {
				manifest = jar.getManifest();
			}

			FileOutputStream fiout = new FileOutputStream(output);
			JarOutputStream jarOut = new JarOutputStream(fiout, manifest);
			Enumeration<JarEntry> entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
				if (entry.getName().equals("META-INF/MANIFEST.MF")) {
					continue;
				}
				jarOut.putNextEntry(entry);
				InputStream jarIn = jar.getInputStream(entry);
				while (jarIn.available() > 0) {
					jarOut.write(jarIn.read());
				}
				jarIn.close();
			}

			jarOut.close();
			jar.close();
			fiout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the directory the jar that is currently being executed is in.
	 * 
	 * @return the directory containing the jar that is currently being executed.
	 */
	public static File getMainDir() {
		if (mainFile != null)
			return mainFile;

		URL resource = Thread.currentThread().getContextClassLoader().getResource("");
		if (resource == null) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			for (Thread thread : Thread.getAllStackTraces().keySet()) {
				if (thread.getId() == 1) {
					stackTrace = Thread.getAllStackTraces().get(thread);
					break;
				}
			}

			try {
				Class<?> mainClass = Class.forName(stackTrace[stackTrace.length - 1].getClassName());
				mainFile = new File(mainClass.getProtectionDomain().getCodeSource().getLocation().getPath());
				mainFile = mainFile.getParentFile();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			mainFile = new File(resource.getPath());
		}

		return mainFile;
	}

}
