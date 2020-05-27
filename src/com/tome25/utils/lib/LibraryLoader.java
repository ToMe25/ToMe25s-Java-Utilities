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
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

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

	private static Instrumentation instrumentation;
	private static byte[] buffer;
	private static String mainArgs;

	/**
	 * Initializes a library loader, restarts your jvm if necessary, tries to
	 * download ToMe25s-Java-Utilites, if that doesn't work it tries to extract it
	 * from this Jar and adds it to the classpath.
	 * 
	 * @param args the program arguments.
	 */
	public static void init(String[] args) {
		init(args, null);
	}

	/**
	 * Initializes a library loader, restarts your jvm if necessary, tries to
	 * download ToMe25s-Java-Utilites, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the System Outputs to
	 * TracingMultiPrintStreams printing to the log files and the previous output
	 * streams.
	 * 
	 * @param args    the program arguments.
	 * @param logFile the log file for System.out and System.err. set to null to
	 *                disable changing System.out and System.err.
	 */
	public static void init(String[] args, File logFile) {
		init(args, logFile, LibraryDownloader.DEFAULT_TOME25S_JAVA_UTILITIES_URL_STORAGE);
	}

	/**
	 * Initializes a library loader, restarts your jvm if necessary, tries to
	 * download ToMe25s-Java-Utilites, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the System Outputs to
	 * TracingMultiPrintStreams printing to the log file and the previous output
	 * streams.
	 * 
	 * @param args              the program arguments.
	 * @param logFile           the log file for System.out and System.err. set to
	 *                          null to disable changing System.out and System.err.
	 * @param defaultUrlStorage the default contents for the URL storage file that
	 *                          lists the urls to try and download
	 *                          ToMe25s-Java-Utilites from.
	 */
	public static void init(String[] args, File logFile, String defaultUrlStorage) {
		init(args, logFile, logFile, defaultUrlStorage);
	}

	/**
	 * Initializes a library loader, restarts your jvm if necessary, tries to
	 * download ToMe25s-Java-Utilites, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the System Outputs to
	 * TracingMultiPrintStreams printing to the log files and the previous output
	 * streams.
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
	 * Initializes a library loader, restarts your jvm if necessary, tries to
	 * download ToMe25s-Java-Utilites, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the System Outputs to
	 * TracingMultiPrintStreams printing to the log files and the previous output
	 * streams.
	 * 
	 * @param args              the program arguments.
	 * @param outputLogFile     the log file for System.out. set to null to disable
	 *                          changing System.out.
	 * @param errorLogFile      the log file for System.err. set to null to disable
	 *                          changing System.err.
	 * @param defaultUrlStorage the default contents for the URL storage file that
	 *                          lists the urls to try and download
	 *                          ToMe25s-Java-Utilites from.
	 */
	public static void init(String[] args, File outputLogFile, File errorLogFile, String defaultUrlStorage) {
		init(args, outputLogFile, errorLogFile, defaultUrlStorage, false);
	}

	/**
	 * Initializes a library loader, restarts your jvm if necessary, tries to
	 * download ToMe25s-Java-Utilites, if that doesn't work it tries to extract it
	 * from this Jar, adds it to the classpath, and sets the System Outputs to
	 * TracingMultiPrintStreams printing to the log files and the previous output
	 * streams.
	 * 
	 * @param args              the program arguments.
	 * @param outputLogFile     the log file for System.out. set to null to disable
	 *                          changing System.out.
	 * @param errorLogFile      the log file for System.err. set to null to disable
	 *                          changing System.err.
	 * @param defaultUrlStorage the default contents for the URL storage file that
	 *                          lists the urls to try and download
	 *                          ToMe25s-Java-Utilites from.
	 * @param update            whether to download/extract the file if it already
	 *                          exists.
	 */
	public static void init(String[] args, File outputLogFile, File errorLogFile, String defaultUrlStorage,
			boolean update) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		PrintStream pb = new PrintStream(buf);
		File codeSource = new File(LibraryLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		if (update || !new File(new File(codeSource.getParent(), "libs"), "ToMe25s-Java-Utilities.jar").exists()) {
			LibraryDownloader downloader = new LibraryDownloader(
					new File(codeSource.getParent(), "ToMe25s-Java-Utilities-Download-Url.txt"), defaultUrlStorage,
					true, true);
			if (downloader.downloadFile()) {
				pb.format("Successfully downloaded ToMe25s-Java-Utilites from %s.%n",
						downloader.getDownloadUrl().toString());
			} else {
				JarExtractor.extractThis(codeSource);
			}
		}
		setArgs(args);
		addLibsToClasspath();
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
	 * your program, as this may need to restart to jvm, which is what it needs the
	 * arguments from your main method for. Part of the Java Agent.
	 * 
	 * @param mainArgs the main methods arguments.
	 * @throws IOException if this program isn't a jar or doesn't exists. And if
	 *                     somehow creating a JarFile instance fails.
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
				FileOutputStream fiout = new FileOutputStream(tempFile);
				JarOutputStream jarOut = new JarOutputStream(fiout, jar.getManifest());
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
				jar = new JarFile(tempFile);
				fiout = new FileOutputStream(file);
				jarOut = new JarOutputStream(fiout);
				entries = jar.entries();
				while (entries.hasMoreElements()) {
					JarEntry entry = entries.nextElement();
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
	 * The premain method needed for this Agent to work. Part of the Java Agent.
	 * 
	 * @param args            some arguments.
	 * @param instrumentation the instrumentation instance to use.
	 */
	@Deprecated
	public static void premain(String args, Instrumentation instrumentation) {
		LibraryLoader.instrumentation = instrumentation;
	}

	/**
	 * converts a String Array to a String. entries are separated with a space.
	 * 
	 * @param array the array to get the string representation of.
	 * @return a string representation of the given string array.
	 */
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
	 */
	@Deprecated
	public void addJarToClasspath(File library) throws IOException {
		addJarToClasspath(new JarFile(library));
	}

	/**
	 * Adds the given library jar to the classpath. Part of the Java Agent.
	 * 
	 * @param library the jar archive that should get added to the classpath.
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
	 * next to this jar.
	 */
	public static void addThisToClasspath() {
		File library = new File("libs", "ToMe25s-Java-Utilities.jar");
		try {
			addToClasspath(library);
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
		File lib = new File("libs", library);
		try {
			addToClasspath(lib);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds everything inside the libs folder to the classpath. Sadly just adding a
	 * "*" file in the directory doesn't work in the MANIFEST.MF attribute, so this
	 * looks for all .jar files in the libs direcotry.
	 */
	public static void addLibsToClasspath() {
		try {
			for (String file : new File("libs").list()) {
				if (file.endsWith(".jar")) {
					addToClasspath(new File("libs", file));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds the given String to the classpath. Requires setArgs to be run first!
	 * 
	 * @param path the path to add.
	 * @throws IOException if something goes wrong.
	 */
	public static void addToClasspath(Path path) throws IOException {
		addToClasspath(path.toString());
	}

	/**
	 * Adds the given String to the classpath. Requires setArgs to be run first!
	 * 
	 * @param path the path to add.
	 * @throws IOException if something goes wrong.
	 */
	public static void addToClasspath(File path) throws IOException {
		addToClasspath(path.getPath());
	}

	/**
	 * Adds the given String to the classpath. Requires setArgs to be run first!
	 * 
	 * @param path the path to add.
	 * @throws IOException if something goes wrong.
	 */
	public static void addToClasspath(String path) throws IOException {
		File file = new File(LibraryLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
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
		String classpath = "";
		if (jar.getManifest().getMainAttributes().get(new Attributes.Name("Class-Path")) != null) {
			classpath = (String) jar.getManifest().getMainAttributes().get(new Attributes.Name("Class-Path"));
		}
		if (!classpath.contains(path)) {
			if (!classpath.isEmpty()) {
				classpath += ' ';
			}
			classpath += path;
			jar.getManifest().getMainAttributes().put(new Attributes.Name("Class-Path"), classpath);
			File tempFile = new File(file.getParent(), "tmp.jar");
			tempFile.deleteOnExit();
			FileOutputStream fiout = new FileOutputStream(tempFile);
			JarOutputStream jarOut = new JarOutputStream(fiout, jar.getManifest());
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
			jar = new JarFile(tempFile);
			fiout = new FileOutputStream(file);
			jarOut = new JarOutputStream(fiout);
			entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
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
			tempFile.delete();
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", file.getAbsolutePath(), stringArrayToString(
					ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0])), mainArgs);
			pb.inheritIO();
			pb.start();
			System.exit(0);
		}
	}

	/**
	 * Removes the given String from the classpath. Requires setArgs to be run
	 * first!
	 * 
	 * @param path the path to remove.
	 * @throws IOException if something goes wrong.
	 */
	public static void removeToClasspath(Path path) throws IOException {
		addToClasspath(path.toString());
	}

	/**
	 * Removes the given String from the classpath. Requires setArgs to be run
	 * first!
	 * 
	 * @param path the path to remove.
	 * @throws IOException if something goes wrong.
	 */
	public static void removeToClasspath(File path) throws IOException {
		addToClasspath(path.getPath());
	}

	/**
	 * Removes the given String from the classpath. Requires setArgs to be run
	 * first!
	 * 
	 * @param path the path to remove.
	 * @throws IOException if something goes wrong.
	 */
	public static void removeToClasspath(String path) throws IOException {
		File file = new File(LibraryLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath());
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
		String classpath = "";
		if (jar.getManifest().getMainAttributes().get(new Attributes.Name("Class-Path")) != null) {
			classpath = (String) jar.getManifest().getMainAttributes().get(new Attributes.Name("Class-Path"));
		}
		if (classpath.contains(path + ' ') || classpath.endsWith(path)) {
			if (classpath.contains(path + ' ')) {
				classpath = classpath.replaceAll(path + ' ', "");
			} else if (classpath.contains(' ' + path)) {
				classpath = classpath.replaceAll(' ' + path, "");
			} else if (classpath.endsWith(path)) {
				classpath = classpath.substring(0, classpath.length() - path.length());
			}
			jar.getManifest().getMainAttributes().put(new Attributes.Name("Class-Path"), classpath);
			File tempFile = new File(file.getParent(), "tmp.jar");
			tempFile.deleteOnExit();
			FileOutputStream fiout = new FileOutputStream(tempFile);
			JarOutputStream jarOut = new JarOutputStream(fiout, jar.getManifest());
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
			jar = new JarFile(tempFile);
			fiout = new FileOutputStream(file);
			jarOut = new JarOutputStream(fiout);
			entries = jar.entries();
			while (entries.hasMoreElements()) {
				JarEntry entry = entries.nextElement();
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
			tempFile.delete();
			ProcessBuilder pb = new ProcessBuilder("java", "-jar", file.getAbsolutePath(), stringArrayToString(
					ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0])), mainArgs);
			pb.inheritIO();
			pb.start();
			System.exit(0);
		} else

		{
			System.err.format("Couldn't remove %s from the classpath, as it wasn't there.%n", path);
		}
	}

	/**
	 * Sets the main args to be used for eventual restarts.
	 * 
	 * @param args the arguments of the main method.
	 */
	public static void setArgs(String[] args) {
		mainArgs = stringArrayToString(args);
	}

	/**
	 * if init got used, this is the way to get its output buffer.
	 * 
	 * @return the output buffer used in init.
	 */
	public static byte[] getBuffer() {
		return buffer;
	}

}
