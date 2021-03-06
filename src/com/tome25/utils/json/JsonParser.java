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
package com.tome25.utils.json;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;

/**
 * A parser to convert strings to {@link JsonObject}s and {@link JsonArray}s.
 * 
 * @author ToMe25
 *
 */
public class JsonParser {

	/**
	 * This method parses the given string to a {@link JsonObject} or
	 * {@link JsonArray}. Faster but not as reliable as the slower one. Also since
	 * some optimizations this seems to no longer be always faster, so test how much
	 * benefit it brings on a case by case basis.
	 * 
	 * Supported object types inside the {@link JsonElement}: Integer and String.
	 * 
	 * WARNING: This method is not safe!!! Having a comma in a string WILL cause
	 * this parsing to fail. Over time when i worked on this before adding it to
	 * this library there were multiple character that could make it crash or break
	 * out of string values.
	 * 
	 * @param str the String to parse.
	 * @return the {@link JsonElement} parsed from the given string.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	public static JsonElement<?> parseStringFast(String str) throws ParseException {
		str = str.trim();
		if (str.charAt(0) == '{') {
			if (str.contains("}")) {
				JsonObject json = new JsonObject();
				str = str.substring(1, str.lastIndexOf("}"));

				int index = 0;
				for (String s : str.split(",")) {
					index += s.length();
					if (s.contains(":")) {
						int sep = s.indexOf(':');
						String key = s.substring(1, sep - 1);
						String value = s.substring(sep + 1).trim();

						if (value.charAt(0) == '"') {
							value = value.substring(1, value.length() - 1);
							if (value.contains("\\\\")) {
								value = value.replace("\\\\", "\\");
							}

							if (value.contains("\\\"")) {
								value = value.replace("\\\"", "\"");
							}
							json.put(key, value);
						} else {
							json.put(key, Integer.parseInt(value));
						}
					} else {
						throw new ParseException("Couldn't find key value separation in \"" + str + "\"!", index);
					}
				}

				return json;
			} else {
				throw new ParseException("Couldn't find a json end in \"" + str + "\"!", str.length());
			}
		} else if (str.charAt(0) == '[') {
			if (str.contains("]")) {
				JsonArray json = new JsonArray();
				str = str.substring(1, str.lastIndexOf("]"));

				for (String s : str.split(",")) {
					s = s.trim();

					if (s.charAt(0) == '"') {
						s = s.substring(1, s.length() - 1);
						if (s.contains("\\\\")) {
							s = s.replace("\\\\", "\\");
						}

						if (s.contains("\\\"")) {
							s = s.replace("\\\"", "\"");
						}
						json.add(s);
					} else {
						json.add(Integer.parseInt(s));
					}
				}

				return json;
			} else {
				throw new ParseException("Couldn't find a json end in \"" + str + "\"!", str.length());
			}
		} else {
			throw new ParseException("Couldn't find a json start in \"" + str + "\"!", 0);
		}
	}

	/**
	 * This method parses the given string to a {@link JsonObject}, or
	 * {@link JsonArray}.
	 * 
	 * Supported object types inside the Json: Integer, Long, Double, Boolean,
	 * String, {@link JsonObject} and {@link JsonArray}.
	 * 
	 * This parser will handle numbers as double if they contain a dot, as long if
	 * they are too big(or too small) to be a integer, and as an integer in any
	 * other case.
	 * 
	 * WARNING: This method may not be safe, over the time i worked on it before
	 * adding it to this library there were multiple characters that could make it
	 * crash or break out of string values, tho i know of none with this version.
	 * 
	 * @param str the string to parse.
	 * @return the {@link JsonElement} parsed from the given string.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	public static JsonElement<?> parseString(String str) throws ParseException {
		return parseCharArray(str.toCharArray());
	}

	/**
	 * This method parses the given byte array to a {@link JsonObject}, or
	 * {@link JsonArray}.
	 * 
	 * Supported object types inside the Json: Integer, Long, Double, Boolean,
	 * String, {@link JsonObject} and {@link JsonArray}.
	 * 
	 * This parser will handle numbers as double if they contain a dot, as long if
	 * they are too big(or too small) to be a integer, and as an integer in any
	 * other case.
	 * 
	 * WARNING: This method may not be safe, over the time i worked on it before
	 * adding it to this library there were multiple characters that could make it
	 * crash or break out of string values, tho i know of none with this version.
	 * 
	 * @param byteArr the byte array to parse.
	 * @return the {@link JsonElement} parsed from the given byte array.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	public static JsonElement<?> parseByteArray(byte[] byteArr) throws ParseException {
		return parseByteArray(byteArr, null);
	}

	/**
	 * This method parses the given byte array to a {@link JsonObject}, or
	 * {@link JsonArray}.
	 * 
	 * Supported object types inside the Json: Integer, Long, Double, Boolean,
	 * String, {@link JsonObject} and {@link JsonArray}.
	 * 
	 * This parser will handle numbers as double if they contain a dot, as long if
	 * they are too big(or too small) to be a integer, and as an integer in any
	 * other case.
	 * 
	 * WARNING: This method may not be safe, over the time i worked on it before
	 * adding it to this library there were multiple characters that could make it
	 * crash or break out of string values, tho i know of none with this version.
	 * 
	 * @param byteArr the byte array to parse.
	 * @param charset the name of the {@link Charset} to use for the conversion to a
	 *                character array. Set to null to use the default
	 *                {@link Charset}.
	 * @return the {@link JsonElement} parsed from the given byte array.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	public static JsonElement<?> parseByteArray(byte[] byteArr, String charset) throws ParseException {
		Charset cSet;
		if (charset == null || charset.isEmpty()) {
			cSet = Charset.defaultCharset();
		} else {
			cSet = Charset.forName(charset);
		}
		CharBuffer cBuf = cSet.decode(ByteBuffer.wrap(byteArr));
		char[] chars = new char[cBuf.length()];
		cBuf.get(chars);
		return parseCharArray(chars);
	}

	/**
	 * This method parses the given char array to a {@link JsonObject}, or
	 * {@link JsonArray}.
	 * 
	 * Supported object types inside the Json: Integer, Long, Double, Boolean,
	 * String, {@link JsonObject} and {@link JsonArray}.
	 * 
	 * This parser will handle numbers as double if they contain a dot, as long if
	 * they are too big(or too small) to be a integer, and as an integer in any
	 * other case.
	 * 
	 * WARNING: This method may not be safe, over the time i worked on it before
	 * adding it to this library there were multiple characters that could make it
	 * crash or break out of string values, tho i know of none with this version.
	 * 
	 * @param charArr the character array to parse.
	 * @return the {@link JsonElement} parsed from the given char array.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	public static JsonElement<?> parseCharArray(char[] charArr) throws ParseException {
		JsonElement<?> json = null;
		boolean buildString = false;
		boolean buildJson = false;
		boolean buildOther = false;
		boolean escaped = false;
		StringBuilder key = null;
		StringBuilder buffer = null;
		short layer = 0;
		int offset = 0;
		for (char c : charArr) {
			if (c == '{') {
				json = new JsonObject();
				break;
			} else if (c == '[') {
				json = new JsonArray();
				break;
			} else if (c != ' ') {
				throw new ParseException(String.format("Missing curly or square bracket at the start of the json '%s'!",
						new String(charArr)), offset);
			}
			offset++;
		}
		offset++;
		while (offset < charArr.length) {
			char c = charArr[offset];
			switch (c) {
			case '{':
				if (buildString) {
					buffer.append(c);
				} else if (buildJson) {
					buffer.append(c);
					layer++;
				} else {
					buffer = new StringBuilder();
					buffer.append('{');
					buildJson = true;
					layer++;
				}
				break;

			case '\\':
				if (escaped || buildJson) {
					buffer.append(c);
				}
				escaped = !escaped;
				break;

			case '"':
				if (escaped) {
					buffer.append(c);
					escaped = false;
				} else if (buildJson) {
					buffer.append(c);
					buildString = !buildString;
				} else if (buildString) {
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buffer.toString());
						buffer = null;
					} else if (key == null) {
						key = buffer;
						buffer = null;
					} else {
						((JsonObject) json).add(key.toString(), buffer.toString());
						key = null;
						buffer = null;
					}
					buildString = false;
				} else {
					buffer = new StringBuilder();
					buildString = true;
				}
				break;

			case ':':
				if (buildString || buildJson) {
					buffer.append(c);
				} else if (json instanceof JsonArray) {
					throw new ParseException(
							String.format("Found key value pair with key \"%s\" while parsing JsonArray '%s'!",
									json.get(json.size() - 1), new String(charArr)),
							offset);
				}
				break;

			case ',':
				if (buildString || buildJson) {
					buffer.append(c);
				} else if (buildOther) {
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buildOther(buffer.toString(), charArr, offset));
					} else {
						if (key == null) {
							throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!",
									buffer.toString(), new String(charArr)), offset - buffer.length());
						}
						((JsonObject) json).add(key.toString(), buildOther(buffer.toString(), charArr, offset));
						key = null;
					}
					buffer = null;
					buildOther = false;
				}
				break;

			case '}':
				if (buildString) {
					buffer.append(c);
				} else if (buildJson) {
					buffer.append(c);
					if (layer > 0) {
						layer--;
					}
					if (layer <= 0) {
						JsonElement<?> subjson = parseString(buffer.toString());
						if (json instanceof JsonArray) {
							((JsonArray) json).add(subjson);
						} else {
							if (key == null) {
								throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!",
										buffer.toString(), new String(charArr)), offset - buffer.length());
							}
							((JsonObject) json).add(key.toString(), subjson);
							key = null;
						}
						buffer = null;
						buildJson = false;
					}
				} else if (buildOther) {
					String buf = buffer.toString();
					buf = buf.trim();
					if (buf.isEmpty()) {
						return json;
					}
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buildOther(buf, charArr, offset));
					} else {
						if (key == null) {
							throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!", buf,
									new String(charArr)), offset - buffer.length());
						}
						((JsonObject) json).add(key.toString(), buildOther(buf, charArr, offset));
						key = null;
					}
					buffer = null;
					buildOther = false;
					if (json instanceof JsonArray) {
						throw new ParseException(
								String.format("JsonArray '%s' ended with a curly bracket!", new String(charArr)),
								offset);
					} else {
						return json;
					}
				} else if (json instanceof JsonArray) {
					throw new ParseException(
							String.format("JsonArray '%s' ended with a curly bracket!", new String(charArr)), offset);
				} else {
					return json;
				}
				break;

			case '[':
				if (buildString) {
					buffer.append(c);
				} else if (buildJson) {
					buffer.append(c);
					layer++;
				} else {
					buffer = new StringBuilder();
					buffer.append(c);
					buildJson = true;
					layer++;
				}
				break;

			case ']':
				if (buildString) {
					buffer.append(c);
				} else if (buildJson) {
					buffer.append(c);
					if (layer > 0) {
						layer--;
					}
					if (layer <= 0) {
						JsonElement<?> subjson = parseString(buffer.toString());
						if (json instanceof JsonArray) {
							((JsonArray) json).add(subjson);
						} else {
							if (key == null) {
								throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!",
										buffer.toString(), new String(charArr)), offset - buffer.length());
							}
							((JsonObject) json).add(key.toString(), subjson);
							key = null;
						}
						buffer = null;
						buildJson = false;
					}
				} else if (buildOther) {
					String buf = buffer.toString();
					buf = buf.trim();
					if (buf.isEmpty()) {
						return json;
					}
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buildOther(buf, charArr, offset));
					} else {
						if (key == null) {
							throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!", buf,
									new String(charArr)), offset - buffer.length());
						}
						((JsonObject) json).add(key.toString(), buildOther(buf, charArr, offset));
						key = null;
					}
					buffer = null;
					buildOther = false;
					if (json instanceof JsonObject) {
						throw new ParseException(
								String.format("JsonObject '%s' ended with a square bracket!", new String(charArr)),
								offset);
					} else {
						return json;
					}
				} else if (json instanceof JsonObject) {
					throw new ParseException(
							String.format("JsonObject '%s' ended with a square bracket!", new String(charArr)), offset);
				} else {
					return json;
				}
				break;

			case ' ':
				if (buildString || buildJson) {
					buffer.append(c);
				}
				break;

			case '\n':
				if (buildString || buildJson) {
					buffer.append(c);
				}
				break;

			default:
				if (buffer == null) {
					buffer = new StringBuilder();
					buffer.append(c);
					buildOther = true;
				} else {
					buffer.append(c);
				}
				break;
			}
			offset++;
		}
		throw new ParseException(String.format("Json '%s' is missing the %s bracket at the end!", new String(charArr),
				json instanceof JsonObject ? "curly" : "square"), offset);

	}

	/**
	 * Builds an object of any type that is neither string nor Json.
	 * 
	 * @param buffer  the buffer to build the object from.
	 * @param charArr the full Json character array, FOR USE IN ERROR HANDLING ONLY.
	 * @param offset  the offset in the string to parse, FOR USE IN ERROR HANDLING
	 *                ONLY.
	 * @return the built object.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	private static Object buildOther(String buffer, char[] charArr, int offset) throws ParseException {
		buffer = buffer.trim();
		try {
			if (buffer.contains(".")) {
				double d = Double.parseDouble(buffer);
				return d;
			} else {
				long l = Long.parseLong(buffer);
				if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) {
					return (int) l;
				}
				return l;
			}
		} catch (Exception e) {
			if (buffer.equalsIgnoreCase("true")) {
				return true;
			} else if (buffer.equalsIgnoreCase("false")) {
				return false;
			} else if (buffer.equalsIgnoreCase("null")) {
				return null;
			} else {
				throw new ParseException(
						String.format("Type for value \"%s\" is unknown in json '%s'!", buffer, new String(charArr)),
						offset - buffer.length());
			}
		}
	}

}
