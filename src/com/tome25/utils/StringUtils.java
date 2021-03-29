/*
 * ToMe25s-Java-Utilities is a collection of common java utilities.
 * Copyright (C) 2021  ToMe25
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
package com.tome25.utils;

import com.tome25.utils.json.JsonElement;

/**
 * A collection of commonly used string utilities.
 * 
 * @author ToMe25
 */
public abstract class StringUtils {

	/**
	 * Converts the given object to an escaped string.<br>
	 * For numbers, booleans, {@link JsonElement JsonElements} and null this just
	 * returns the string representation of that object.<br>
	 * Everything else will be in double quotes, and all double quotes and
	 * backslashes in the string representation of the object will be escaped.
	 * 
	 * @param obj the object to get the string version of.
	 * @return the resulting string.
	 * @see #toEscapedString(StringBuilder, Object)
	 */
	public static String toEscapedString(Object obj) {
		StringBuilder builder = new StringBuilder();
		toEscapedString(builder, obj);
		return builder.toString();
	}

	/**
	 * Writes the escaped string version of the given object to the given
	 * {@link StringBuilder}.<br>
	 * For numbers, booleans, {@link JsonElement JsonElements} and null this just
	 * returns the string representation of that object.<br>
	 * Everything else will be in double quotes, and all double quotes and
	 * backslashes in the string representation of the object will be escaped.
	 * 
	 * @param obj the object to get the string version of.
	 */
	public static void toEscapedString(StringBuilder builder, Object obj) {
		if (obj == null || obj instanceof Boolean || obj instanceof Number || obj instanceof JsonElement) {
			builder.append(obj);
		} else {
			String contentString = obj.toString();
			builder.ensureCapacity(builder.length() + contentString.length() + 2);
			builder.append('"');
			for (char c : contentString.toCharArray()) {
				if (c == '\\' || c == '"') {
					builder.append('\\');
				}
				builder.append(c);
			}
			builder.append('"');
		}
	}

	/**
	 * Converts the given array to a string.
	 * 
	 * @param array     the array to get the string representation of.
	 * @param separator the string to put between the elements of the array.
	 * @return a string representation of the given string array.
	 * @see #arrayToString(StringBuilder, Object[], String)
	 * @see java.util.Arrays#toString(Object[])
	 */
	public static String arrayToString(Object[] array, String separator) {
		StringBuilder builder = new StringBuilder();
		arrayToString(builder, array, separator);
		return builder.toString();
	}

	/**
	 * Appends the string representation of the given array to the given
	 * {@link StringBuilder}.
	 * 
	 * @param builder   the {@link StringBuilder} to append the values to.
	 * @param array     the array to get the string representation of.
	 * @param separator the string to put between the elements of the array.
	 * @see #arrayToString(StringBuilder, Object[], String)
	 */
	public static void arrayToString(StringBuilder builder, Object[] array, String separator) {
		for (Object obj : array) {
			builder.append(obj);
			builder.append(separator);
		}

		if (builder.length() > 0) {
			builder.setLength(builder.length() - separator.length());
		}
	}

}
