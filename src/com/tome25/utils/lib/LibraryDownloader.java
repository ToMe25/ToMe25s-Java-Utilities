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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 
 * This class can download files from web servers. You will need to copy this
 * class and {@link LibraryLoader} into your project in order to add this
 * library to the classpath if you want to download it at the start of your
 * software.
 * 
 * @author ToMe25
 * 
 */
public class LibraryDownloader {

	/**
	 * The content types that could be a jar archive.
	 */
	private static final String[] JAR_CONTENT_TYPES = { "application/jar", "application/java-archive",
			"application/octet-stream", "application/download", "application/force-download" };
	private static final Predicate<String> JAR_CONTENT_TYPE_CHECKER = (s) -> Arrays.asList(JAR_CONTENT_TYPES)
			.contains(s);
	/**
	 * The default content of the file that defines where to download this library.
	 */
	protected static final String DEFAULT_TOME25S_JAVA_UTILITIES_URL_STORAGE = String
			.format("# The URL(s) to try and download ToMe25's-Java-Utilities from.%n"
					+ "# Entries are separated with a ',', they will be tried from start to end.%n"
					+ "https://github.com/ToMe25/ToMe25s-Java-Utilities/raw/master/ToMe25s-Java-Utilities.jar%n");

	private List<URL> urls;
	private File target;
	/**
	 * The predicate checking whether a content type is valid.
	 */
	private Predicate<String> typeCheck;
	private boolean override;
	/**
	 * Whether the target file should get set to executable after the download
	 * finished.
	 */
	private boolean executable;
	/**
	 * Whether target is a directory and the file should be put in it, or the file
	 * where the download should be saved.
	 */
	private boolean targetDir;
	private int size;
	private int downloaded;
	private URL downloadedFrom;

	/**
	 * Creates a new LibraryDownloader that reads all URLs from urlStorage, and
	 * tries them until one is valid, as long as they are separated by a ',' or a
	 * ';'. This ignores lines starting with a '#'. If the file doesn't exists it
	 * creates it with defaultUrlStorage as its content. If defaultUrlStorage is
	 * null or empty it wont create a file, and therefore can't download anything.
	 * 
	 * Valid content types are application/jar, application/java-archive,
	 * application/octet-stream, application/download and
	 * application/force-download.
	 * 
	 * @param urlStorage        the file to read the URLs from.
	 * @param defaultUrlStorage the default content for urlStorage.
	 * @param overrideTarget    whether the target file should get overridden if it
	 *                          already exists.
	 * @param makeExecutable    whether the target file should get made executable.
	 */
	public LibraryDownloader(File urlStorage, String defaultUrlStorage, boolean overrideTarget,
			boolean makeExecutable) {
		this(readUrlStorage(urlStorage, defaultUrlStorage), null, JAR_CONTENT_TYPE_CHECKER, overrideTarget,
				makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader that reads all URLs from urlStorage, and
	 * tries them until one is valid, as long as they are separated by a ',' or a
	 * ';'. This ignores lines starting with a '#'. If the file doesn't exists it
	 * creates it with defaultUrlStorage as its content. If defaultUrlStorage is
	 * null or empty it wont create a file, and therefore can't download anything.
	 * 
	 * @param urlStorage        the file to read the URLs from.
	 * @param defaultUrlStorage the default content for urlStorage.
	 * @param contentTypeCheck  a Predicate that checks the content type of the
	 *                          response from the URL.
	 * @param overrideTarget    whether the target file should get overridden if it
	 *                          already exists.
	 * @param makeExecutable    whether the target file should get made executable.
	 */
	public LibraryDownloader(File urlStorage, String defaultUrlStorage, Predicate<String> contentTypeCheck,
			boolean overrideTarget, boolean makeExecutable) {
		this(readUrlStorage(urlStorage, defaultUrlStorage), null, contentTypeCheck, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader that reads all URLs from urlStorage, and
	 * tries them until one is valid, as long as they are separated by a ',' or a
	 * ';'. This ignores lines starting with a '#'. If the file doesn't exists it
	 * creates it with defaultUrlStorage as its content. If defaultUrlStorage is
	 * null or empty it wont create a file, and therefore can't download anything.
	 * 
	 * Valid content types are application/jar, application/java-archive,
	 * application/octet-stream, application/download and
	 * application/force-download.
	 * 
	 * @param urlStorage        the file to read the URLs from.
	 * @param defaultUrlStorage the default content for urlStorage.
	 * @param target            the target location for the File. If it is a
	 *                          directory the download will get saved into that
	 *                          directory with the name it has on the server. If
	 *                          this is null the file will be saved into the libs
	 *                          directory next to this jar.
	 * @param overrideTarget    whether the target file should get overridden if it
	 *                          already exists.
	 * @param makeExecutable    whether the target file should get made executable.
	 */
	public LibraryDownloader(File urlStorage, String defaultUrlStorage, File target, boolean overrideTarget,
			boolean makeExecutable) {
		this(readUrlStorage(urlStorage, defaultUrlStorage), target, JAR_CONTENT_TYPE_CHECKER, overrideTarget,
				makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader that reads all URLs from urlStorage, and
	 * tries them until one is valid, as long as they are separated by a ',' or a
	 * ';'. This ignores lines starting with a '#'. If the file doesn't exists it
	 * creates it with defaultUrlStorage as its content. If defaultUrlStorage is
	 * null or empty it wont create a file, and therefore can't download anything.
	 * 
	 * @param urlStorage        the file to read the URLs from.
	 * @param defaultUrlStorage the default content for urlStorage.
	 * @param target            the target location for the File. If it is a
	 *                          directory the download will get saved into that
	 *                          directory with the name it has on the server. If
	 *                          this is null the file will be saved into the libs
	 *                          directory next to this jar.
	 * @param contentTypeCheck  a Predicate that checks the content type of the
	 *                          response from the URL.
	 * @param overrideTarget    whether the target file should get overridden if it
	 *                          already exists.
	 * @param makeExecutable    whether the target file should get made executable.
	 */
	public LibraryDownloader(File urlStorage, String defaultUrlStorage, File target, Predicate<String> contentTypeCheck,
			boolean overrideTarget, boolean makeExecutable) {
		this(readUrlStorage(urlStorage, defaultUrlStorage), target, contentTypeCheck, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading a jar file from a single URL,
	 * this will download the file into a directory named libs in the same directory
	 * where the file containing this class is, and give it the name of the file on
	 * from the URL.
	 * 
	 * Valid content types are application/jar, application/java-archive,
	 * application/octet-stream, application/download and
	 * application/force-download.
	 * 
	 * @param url            the URL to download a file from.
	 * @param overrideTarget whether the target file should get overridden if it
	 *                       already exists.
	 * @param makeExecutable whether the target file should get made executable.
	 */
	public LibraryDownloader(URL url, boolean overrideTarget, boolean makeExecutable) {
		this(url, null, JAR_CONTENT_TYPE_CHECKER, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading a jar file from a single URL,
	 * this will download the file into a directory named libs in the same directory
	 * where the file containing this class is, and give it the name of the file on
	 * from the URL.
	 * 
	 * @param url              the URL to download a file from.
	 * @param contentTypeCheck a Predicate that checks the content type of the
	 *                         response from the URL.
	 * @param overrideTarget   whether the target file should get overridden if it
	 *                         already exists.
	 * @param makeExecutable   whether the target file should get made executable.
	 */
	public LibraryDownloader(URL url, Predicate<String> contentTypeCheck, boolean overrideTarget,
			boolean makeExecutable) {
		this(url, null, contentTypeCheck, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading a jar file from a single URL.
	 * 
	 * @param url            the URL to download a file from.
	 * @param target         the target location for the File. If it is a directory
	 *                       the download will get saved into that directory with
	 *                       the name it has on the server. If this is null the file
	 *                       will be saved into the libs directory next to this jar.
	 * @param overrideTarget whether the target file should get overridden if it
	 *                       already exists.
	 * @param makeExecutable whether the target file should get made executable.
	 */
	public LibraryDownloader(URL url, File target, boolean overrideTarget, boolean makeExecutable) {
		this(url, target, JAR_CONTENT_TYPE_CHECKER, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading from a single URL if the content
	 * type of that web site is accepted by contentTypeCheck.
	 * 
	 * @param url              the URL to download a file from.
	 * @param target           the target location for the File. If it is a
	 *                         directory the download will get saved into that
	 *                         directory with the name it has on the server. If this
	 *                         is null the file will be saved into the libs
	 *                         directory next to this jar.
	 * @param contentTypeCheck a Predicate that checks the content type of the
	 *                         response from the URL.
	 * @param overrideTarget   whether the target file should get overridden if it
	 *                         already exists.
	 * @param makeExecutable   whether the target file should get made executable.
	 */
	public LibraryDownloader(URL url, File target, Predicate<String> contentTypeCheck, boolean overrideTarget,
			boolean makeExecutable) {
		this(toList(url), target, contentTypeCheck, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading a Jar file from the first valid
	 * of a list of URLs, this will download the file into a directory named libs in
	 * the same directory where the file containing this class is, and give it the
	 * name of the file on from the URL.
	 * 
	 * Valid content types are application/jar, application/java-archive,
	 * application/octet-stream, application/download and
	 * application/force-download.
	 * 
	 * @param urls           the List of URLs to download a file from.
	 * @param overrideTarget whether the target file should get overridden if it
	 *                       already exists.
	 * @param makeExecutable whether the target file should get made executable.
	 */
	public LibraryDownloader(List<URL> urls, boolean overrideTarget, boolean makeExecutable) {
		this(urls, null, JAR_CONTENT_TYPE_CHECKER, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading from the first URL of a list that
	 * is accepted by contentTypeCheck and responds without errors, this will
	 * download the file into a directory named libs in the same directory where the
	 * file containing this class is, and give it the name of the file on from the
	 * URL.
	 * 
	 * @param urls             the List of URLs to download a file from.
	 * @param contentTypeCheck a Predicate that checks the content type of the
	 *                         response from the URL.
	 * @param overrideTarget   whether the target file should get overridden if it
	 *                         already exists.
	 * @param makeExecutable   whether the target file should get made executable.
	 */
	public LibraryDownloader(List<URL> urls, Predicate<String> contentTypeCheck, boolean overrideTarget,
			boolean makeExecutable) {
		this(urls, null, contentTypeCheck, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading a Jar file from the first valid
	 * of a list of URLs.
	 * 
	 * Valid content types are application/jar, application/java-archive,
	 * application/octet-stream, application/download and
	 * application/force-download.
	 * 
	 * @param urls           the List of URLs to download a file from.
	 * @param target         the target location for the File. If it is a directory
	 *                       the download will get saved into that directory with
	 *                       the name it has on the server. If this is null the file
	 *                       will be saved into the libs directory next to this jar.
	 * @param overrideTarget whether the target file should get overridden if it
	 *                       already exists.
	 * @param makeExecutable whether the target file should get made executable.
	 */
	public LibraryDownloader(List<URL> urls, File target, boolean overrideTarget, boolean makeExecutable) {
		this(urls, target, JAR_CONTENT_TYPE_CHECKER, overrideTarget, makeExecutable);
	}

	/**
	 * Creates a new LibraryDownloader downloading from the first URL of a list that
	 * is accepted by contentTypeCheck and responds without errors.
	 * 
	 * @param urls             the List of URLs to download a file from.
	 * @param target           the target location for the File. If it is a
	 *                         directory the download will get saved into that
	 *                         directory with the name it has on the server. If this
	 *                         is null the file will be saved into the libs
	 *                         directory next to this jar.
	 * @param contentTypeCheck a Predicate that checks the content type of the
	 *                         response from the URL.
	 * @param overrideTarget   whether the target file should get overridden if it
	 *                         already exists.
	 * @param makeExecutable   whether the target file should get made executable.
	 */
	public LibraryDownloader(List<URL> urls, File target, Predicate<String> contentTypeCheck, boolean overrideTarget,
			boolean makeExecutable) {
		this.urls = urls;
		if (target == null) {
			target = new File(LibraryLoader.getMainDir(), "libs");
			targetDir = true;
		}
		this.target = target;
		if (target.isDirectory()) {
			targetDir = true;
		}
		this.typeCheck = contentTypeCheck;
		this.override = overrideTarget;
		this.executable = makeExecutable;
	}

	/**
	 * Downloads the file from the first working URL from the URLs set in the
	 * constructor.
	 * 
	 * @return whether the download was successful.
	 */
	public boolean downloadFile() {
		return downloadFile((e) -> e.printStackTrace());
	}

	/**
	 * Downloads the file from the first working URL from the URLs set in the
	 * constructor.
	 * 
	 * @param exceptionHandler a consumer that handles exceptions if any occur.
	 * @return whether the download was successful.
	 */
	public boolean downloadFile(Consumer<Exception> exceptionHandler) {
		for (URL url : urls) {
			try {
				if (downloadFile(url)) {
					return true;
				}
			} catch (IOException e) {
				exceptionHandler.accept(e);
			}
		}
		return false;
	}

	/**
	 * Downloads the file from the given URL.
	 * 
	 * @param url the file to download.
	 * @return whether the download was successful.
	 * @throws IOException if an I/O error has occurred.
	 */
	public boolean downloadFile(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		if (connection instanceof HttpURLConnection) {
			((HttpURLConnection) connection).setInstanceFollowRedirects(false);// handle redirects below.
		}
		while (connection.getHeaderField("Location") != null) {
			String location = connection.getHeaderField("Location");
			if (location.contains("://")) {
				url = new URL(location);
			} else if (location.startsWith("/")) {
				url = new URL(String.format("%s://%s:%s%s", url.getProtocol(), url.getHost(),
						url.getPort() == -1 ? url.getDefaultPort() : url.getPort(), location));
			} else {
				url = new URL(String.format("%s://%s:%s%s/%s", url.getProtocol(), url.getHost(),
						url.getPort() == -1 ? url.getDefaultPort() : url.getPort(),
						url.getPath().substring(0, url.getPath().lastIndexOf('/')), location));
			}
			connection = url.openConnection();
			if (connection instanceof HttpURLConnection) {
				((HttpURLConnection) connection).setInstanceFollowRedirects(false);
			}
		}
		if (typeCheck.test(String.valueOf(connection.getContentType()))) {
			if (targetDir) {
				if (url.getFile().contains("?")) {
					target = new File(target,
							url.getFile().substring(url.getFile().lastIndexOf('/') + 1, url.getFile().indexOf('?')));
				} else {
					target = new File(target, url.getFile().substring(url.getFile().lastIndexOf('/') + 1));
				}
			}
			if (target.exists()) {
				if (override) {
					target.delete();
				} else {
					return false;
				}
			}
			if (!target.getParentFile().exists()) {
				target.getParentFile().mkdirs();
			} else if (!target.getParentFile().isDirectory()) {
				System.err.println(target.getParent()
						+ " is not a directory, but needs to be one in order for the download to be saved there.");
				return false;
			}
			downloadedFrom = url;
			size = connection.getContentLength();
			BufferedInputStream bin = new BufferedInputStream(connection.getInputStream());
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(target));
			byte[] buffer = new byte[1024];
			int buffered;
			while ((buffered = bin.read(buffer)) >= 0) {
				downloaded += buffered;
				bout.write(buffer, 0, buffered);
			}
			bin.close();
			bout.close();
			if (executable) {
				target.setExecutable(true);
			}
			return true;
		}
		return false;
	}

	/**
	 * Downloads ToMe25s-Java-Utilities from the first valid URL in the file
	 * ToMe25s-Java-Utilities-Download-Url.txt.
	 * 
	 * The default for this file is the GitHub download URL for
	 * ToMe25s-Java-Utilities.
	 * 
	 * @return whether the download was successful.
	 */
	public static boolean downloadThis() {
		return downloadThis(DEFAULT_TOME25S_JAVA_UTILITIES_URL_STORAGE);
	}

	/**
	 * Downloads ToMe25s-Java-Utilities from the first valid URL in the file
	 * ToMe25s-Java-Utilities-Download-Url.txt.
	 * 
	 * @param defaultUrls the default content for
	 *                    ToMe25s-Java-Utilities-Download-Url.txt.
	 * @return whether the download was successful.
	 */
	public static boolean downloadThis(String defaultUrls) {
		return downloadThis(defaultUrls, (e) -> e.printStackTrace());
	}

	/**
	 * Downloads ToMe25s-Java-Utilities from the first valid URL in the file
	 * ToMe25s-Java-Utilities-Download-Url.txt.
	 * 
	 * @param exceptionHandler a consumer that handles exceptions if any occur.
	 * @return whether the download was successful.
	 */
	public static boolean downloadThis(Consumer<Exception> exceptionHandler) {
		return downloadThis(DEFAULT_TOME25S_JAVA_UTILITIES_URL_STORAGE, exceptionHandler);
	}

	/**
	 * Downloads ToMe25s-Java-Utilities from the first valid URL in the file
	 * ToMe25s-Java-Utilities-Download-Url.txt.
	 * 
	 * @param defaultUrls      the default content for
	 *                         ToMe25s-Java-Utilities-Download-Url.txt.
	 * @param exceptionHandler a consumer that handles exceptions if any occur.
	 * @return whether the download was successful.
	 */
	public static boolean downloadThis(String defaultUrls, Consumer<Exception> exceptionHandler) {
		LibraryDownloader downloader = new LibraryDownloader(
				new File(LibraryLoader.getMainDir(), "ToMe25s-Java-Utilities-Download-Url.txt"), defaultUrls, true,
				true);
		return downloader.downloadFile();
	}

	/**
	 * Returns the size of the file to download in bytes.
	 * 
	 * @return the size of the file to download in bytes.
	 */
	public int getDownloadSize() {
		return size;
	}

	/**
	 * Returns the amount of bytes that are already downloaded.
	 * 
	 * @return the amount of bytes that are already downloaded.
	 */
	public int getBytesDownloaded() {
		return downloaded;
	}

	/**
	 * Returns the percentage of the file that is already downloaded.
	 * 
	 * @return the percentage of the file that is already downloaded.
	 */
	public double getPercentDownloaded() {
		return downloaded / (size / 100D);
	}

	/**
	 * Returns the URL that got used for the download, after all redirects.
	 * 
	 * @return the URL that got used for the download.
	 */
	public URL getDownloadUrl() {
		return downloadedFrom;
	}

	/**
	 * Creates a new {@link ArrayList} and adds all the given objects to it.
	 * 
	 * @param <T>     the type of objects and list.
	 * @param objects the objects to put into a list.
	 * @return a new list containing all the given objects
	 */
	@SafeVarargs
	private static <T> List<T> toList(T... objects) {
		List<T> list = new ArrayList<T>();
		for (T obj : objects) {
			list.add(obj);
		}
		return list;
	}

	/**
	 * Reads all URLs from the given file, as long as they are separated by a ',' or
	 * a ';'. This ignores lines starting with a '#'. If the file doesn't exists it
	 * creates it with defaultUrlStorage as its content. If defaultUrlStorage is
	 * null or empty it wont create a file, and will return an empty list.
	 * 
	 * @param urlStorage        the file to read the URLs from.
	 * @param defaultUrlStorage the default content for urlStorage.
	 * @return the URLs listed in the URL storage file.
	 */
	private static List<URL> readUrlStorage(File urlStorage, String defaultUrlStorage) {
		List<URL> urls = new ArrayList<URL>();
		if (!urlStorage.exists() && defaultUrlStorage != null && !defaultUrlStorage.isEmpty()) {
			if (!urlStorage.getAbsoluteFile().getParentFile().exists()) {
				urlStorage.getParentFile().mkdirs();
			}
			try {
				FileOutputStream fiout = new FileOutputStream(urlStorage);
				fiout.write(defaultUrlStorage.getBytes());
				fiout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (urlStorage.exists()) {
			try {
				String file = "";
				List<String> lines = Files.readAllLines(urlStorage.toPath());
				for (String line : lines) {
					if (!line.startsWith("#")) {
						file += line;
					}
				}
				for (String url : file.split(",|;")) {
					try {
						urls.add(new URL(url));
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return urls;
	}

}
