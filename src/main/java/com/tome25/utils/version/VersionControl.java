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
package com.tome25.utils.version;

import java.util.HashMap;
import java.util.Map;

/**
 * A utility class to check library versions.
 * 
 * @author ToMe25
 *
 */
public class VersionControl {

	private static final Map<String, String> LIBRARY_VERSION_STRINGS = new HashMap<String, String>();
	private static final Map<String, int[]> LIBRARY_VERSION_ARRAYS = new HashMap<String, int[]>();
	private static String versionString;
	private static int[] versionArray;

	/**
	 * Gets this libraries build number, if this is still in its Jar, if not this
	 * will return 0;
	 * 
	 * @return the build number.
	 */
	@Deprecated
	public static int getVersion() {
		return getBuild();
	}

	/**
	 * Gets the major part of this libraries version number, or 1 if it isn't
	 * available.
	 * 
	 * @return the major version.
	 */
	public static int getMajor() {
		int[] version = getVersionArray();
		if (version.length > 0) {
			return version[0];
		} else {
			return 1;
		}
	}

	/**
	 * Gets the minor part of this libraries version number, or 0 if it isn't
	 * available.
	 * 
	 * @return the minor version.
	 */
	public static int getMinor() {
		int[] version = getVersionArray();
		if (version.length > 1) {
			return version[1];
		} else {
			return 0;
		}
	}

	/**
	 * Gets the build/patch part of this libraries version number, or 0 if it isn't
	 * available.
	 * 
	 * @return the build number.
	 */
	public static int getBuild() {
		int[] version = getVersionArray();
		if (version.length > 2) {
			return version[2];
		} else {
			return 0;
		}
	}

	/**
	 * Gets the build/patch part of this libraries version number, or 0 if it isn't
	 * available.
	 * 
	 * @return the build number.
	 */
	public static int getPatch() {
		return getBuild();
	}

	/**
	 * Gets this libraries version number split into its components, if this is
	 * still in its Jar, if not this will return [1, 0]. the used format is [MAJOR,
	 * MINOR, BUILD].
	 * 
	 * @return the version number.
	 */
	public static int[] getVersionArray() {
		if (versionArray == null) {
			String[] split = getVersionString().split("\\.");
			int[] version = new int[split.length];
			int i = 0;
			for (String part : split) {
				version[i] = Integer.parseInt(part);
				i++;
			}
			versionArray = version;
		}
		if (!LIBRARY_VERSION_ARRAYS.containsKey("ToMe25s-Java-Utilities")) {
			setVersionArray("ToMe25s-Java-Utilities", versionArray);
		}
		return versionArray;
	}

	/**
	 * Gets this libraries version number as a string, if this is still in its Jar, if
	 * not this will return 1.0. the used format is "MAJOR.MINOR.BUILD".
	 * 
	 * @return the version number.
	 */
	public static String getVersionString() {
		if (versionString == null) {
			versionString = VersionControl.class.getPackage().getImplementationVersion();
			if (versionString == null) {
				versionString = "1.0";
			}
		}
		if (!LIBRARY_VERSION_STRINGS.containsKey("ToMe25s-Java-Utilities")) {
			setVersionString("ToMe25s-Java-Utilities", versionString);
		}
		return versionString;
	}

	/**
	 * Sets the currently used version of a library. This sets both the String, and
	 * the Array.
	 * 
	 * @param name          the name of the library.
	 * @param versionString the version of the library. Please use the format
	 *                      "MAJOR.MINOR.BUILD", as this utility wont be able to
	 *                      handle anything else.
	 */
	public static void setVersionString(String name, String versionString) {
		name = name.toLowerCase();
		LIBRARY_VERSION_STRINGS.put(name, versionString);
		String[] split = versionString.split("\\.");
		int[] versionArray = new int[split.length];
		int i = 0;
		for (String part : split) {
			versionArray[i] = Integer.parseInt(part);
			i++;
		}
		LIBRARY_VERSION_ARRAYS.put(name, versionArray);
	}

	/**
	 * Sets the currently used version of a library. This sets both the String, and
	 * the Array.
	 * 
	 * @param name         the name of the library.
	 * @param versionArray the version of the library. Please use the format [MAJOR,
	 *                     MINOR, BUILD], as this utility wont be able to handle
	 *                     anything else.
	 */
	public static void setVersionArray(String name, int[] versionArray) {
		name = name.toLowerCase();
		LIBRARY_VERSION_ARRAYS.put(name, versionArray);
		String versionString = "";
		for (int i : versionArray) {
			versionString += i + ".";
		}
		if (versionString.length() > 0) {
			versionString = versionString.substring(0, versionString.length() - 1);
		}
		LIBRARY_VERSION_STRINGS.put(name, versionString);
	}

	/**
	 * Gets the version number of the given library as a string, if it was
	 * registered prior to this call, if not this will return "1.0". The used format
	 * is "MAJOR.MINOR.BUILD", if the library adding it didn't mess up.
	 * 
	 * @param name the name of the library to check.
	 * @return the version number. Null if no library with this name was found.
	 */
	public static String getVersionString(String name) {
		name = name.toLowerCase();
		if (name.equals("tome25s-java-utilities") && !LIBRARY_VERSION_STRINGS.containsKey(name)) {
			return getVersionString();
		}
		return LIBRARY_VERSION_STRINGS.containsKey(name) ? LIBRARY_VERSION_STRINGS.get(name) : null;
	}

	/**
	 * Gets the version number of the given library as a string, if it was
	 * registered prior to this call, if not this will return "1.0". The used format
	 * is "MAJOR.MINOR.BUILD", if the library adding it didn't mess up.
	 * 
	 * @param name the name of the library to check.
	 * @return the version number. Empty size 0 array of no library with this name was found.
	 */
	public static int[] getVersionArray(String name) {
		name = name.toLowerCase();
		if (name.equals("tome25s-java-utilities") && !LIBRARY_VERSION_ARRAYS.containsKey(name)) {
			return getVersionArray();
		}
		return LIBRARY_VERSION_ARRAYS.containsKey(name) ? LIBRARY_VERSION_ARRAYS.get(name) : new int[0];
	}

	/**
	 * Gets the major part of the given libraries version number, or 1 if it isn't
	 * available.
	 * 
	 * @param name the name of the library to check.
	 * @return the major version.
	 */
	public static int getMajor(String name) {
		int[] version = getVersionArray(name);
		if (version.length > 0) {
			return version[0];
		} else {
			return 1;
		}
	}

	/**
	 * Gets the minor part of the given libraries version number, or 0 if it isn't
	 * available.
	 * 
	 * @param name the name of the library to check.
	 * @return the minor version.
	 */
	public static int getMinor(String name) {
		int[] version = getVersionArray(name);
		if (version.length > 1) {
			return version[1];
		} else {
			return 0;
		}
	}

	/**
	 * Gets the build/patch part of the given libraries version number, or 0 if it
	 * isn't available.
	 * 
	 * @param name the name of the library to check.
	 * @return the build number.
	 */
	public static int getBuild(String name) {
		int[] version = getVersionArray(name);
		if (version.length > 2) {
			return version[2];
		} else {
			return 0;
		}
	}

	/**
	 * Gets the build/patch part of the given libraries version number, or 0 if it
	 * isn't available.
	 * 
	 * @param name the name of the library to check.
	 * @return the build number.
	 */
	public static int getPatch(String name) {
		return getBuild(name);
	}

	@Override
	public String toString() {
		return String.format("VersionControl[%s]", toStringStatic());
	}

	/**
	 * Converts all the stored version data to a single String.
	 * 
	 * @return all the stored version data.
	 */
	public static String toStringStatic() {
		if (!LIBRARY_VERSION_ARRAYS.containsKey("ToMe25s-Java-Utilities")) {
			getVersionString();
		}
		String[] string = new String[] { "" };
		LIBRARY_VERSION_STRINGS.forEach((name, version) -> string[0] += String.format("%s: %s, ", name, version));
		string[0] = string[0].substring(0, string[0].length() - 2);
		return string[0];
	}

}
