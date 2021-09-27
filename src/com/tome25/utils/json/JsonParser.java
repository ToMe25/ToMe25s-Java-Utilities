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
import java.util.Stack;

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
	 * crash or break out of string values, though i know of none with this version.
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
	 * crash or break out of string values, though i know of none with this version.
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
	 * crash or break out of string values, though i know of none with this version.
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
	 * crash or break out of string values, though i know of none with this version.
	 * 
	 * @param charArr the character array to parse.
	 * @return the {@link JsonElement} parsed from the given char array.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	public static JsonElement<?> parseCharArray(char[] charArr) throws ParseException {
		Stack<JsonElement<?>> parsing = new Stack<>();
		JsonElement<?> json = null;
		JsonElement<?> subjson = null;
		boolean buildString = false;
		boolean buildOther = false;
		boolean escaped = false;
		boolean separated = false;
		StringBuilder key = null;
		StringBuilder buffer = null;
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

		while (++offset < charArr.length) {
			char c = charArr[offset];
			switch (c) {
			case '{':
				if (buildString) {
					buffer.append(c);
				} else {
					parsing.push(json);
					subjson = new JsonObject();
					if (json instanceof JsonArray) {
						((JsonArray) json).add(subjson);
					} else {
						if (key == null) {
							int[] value = getJsonAtPosition(charArr, offset + 1);
							throw createParseException(String.format("Missing key for value \"%s\" in json '%%s'!",
									new String(charArr, value[0], value[1])), charArr, offset);
						}
						((JsonObject) json).put(key.toString(), subjson);
						key = null;
					}
					json = subjson;
					separated = false;
				}
				break;

			case '\\':
				if (escaped) {
					buffer.append(c);
				}
				escaped = !escaped;
				break;

			case '"':
				if (escaped) {
					buffer.append(c);
					escaped = false;
				} else if (buildString) {
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buffer.toString());
					} else if (key == null) {
						key = buffer;
					} else {
						((JsonObject) json).put(key.toString(), buffer.toString());
						key = null;
					}
					buffer = null;
					buildString = false;
				} else {
					buffer = new StringBuilder();
					buildString = true;
					separated = false;
				}
				break;

			case ':':
				if (buildString) {
					buffer.append(c);
				} else if (json instanceof JsonArray) {
					throw createParseException(
							String.format("Found key value pair with key \"%s\" while parsing JsonArray '%%s'!",
									json.get(json.size() - 1)),
							charArr, offset);
				} else if (key == null) {
					throw createParseException("Missing key in json '%s'!", charArr, offset);
				} else {
					separated = true;
				}
				break;

			case ',':
				if (buildString) {
					buffer.append(c);
				} else if (buildOther) {
					buildOther(buffer, json, key, charArr, offset);
					buffer = null;
					buildOther = false;
					if (json instanceof JsonObject) {
						key = null;
					}
					separated = true;
				} else if (separated) {
					// FIXME having a comma as the first thing in a json object or array works even
					// tho it should be a syntax error.
					throw createParseException("Missing value in json '%s'!", charArr, offset);
				} else {
					separated = true;
				}
				break;

			case '}':
				if (buildString) {
					buffer.append(c);
				} else if (buildOther) {
					if (json instanceof JsonArray) {
						throw createParseException("JsonArray '%s' ends with a curly bracket!", charArr, offset);
					} else {
						buildOther(buffer, json, key, charArr, offset);
						buffer = null;
						buildOther = false;
						key = null;

						if (parsing.size() > 0) {
							json = parsing.pop();
						} else {
							return json;
						}
					}
				} else if (json instanceof JsonArray) {
					throw createParseException("JsonArray '%s' ends with a curly bracket!", charArr, offset);
				} else if (separated) {
					throw createParseException("Missing value in json '%s'!", charArr, offset);
				} else if (parsing.size() > 0) {
					json = parsing.pop();
				} else {
					return json;
				}
				break;

			case '[':
				if (buildString) {
					buffer.append(c);
				} else {
					parsing.push(json);
					subjson = new JsonArray();
					if (json instanceof JsonArray) {
						((JsonArray) json).add(subjson);
					} else {
						if (key == null) {
							int[] value = getJsonAtPosition(charArr, offset + 1);
							throw createParseException(String.format("Missing key for value \"%s\" in json '%%s'!",
									new String(charArr, value[0], value[1])), charArr, offset);
						}
						((JsonObject) json).put(key.toString(), subjson);
						key = null;
					}
					json = subjson;
					separated = false;
				}
				break;

			case ']':
				if (buildString) {
					buffer.append(c);
				} else if (buildOther) {
					if (json instanceof JsonObject) {
						throw createParseException("JsonObject '%s' ends with a square bracket!", charArr, offset);
					} else {
						buildOther(buffer, json, key, charArr, offset);
						buffer = null;
						buildOther = false;

						if (parsing.size() > 0) {
							json = parsing.pop();
						} else {
							return json;
						}
					}
				} else if (json instanceof JsonObject) {
					throw createParseException("JsonObject '%s' ends with a square bracket!", charArr, offset);
				} else if (separated) {
					throw createParseException("Missing value in json '%s'!", charArr, offset);
				} else if (parsing.size() > 0) {
					json = parsing.pop();
				} else {
					return json;
				}
				break;

			case ' ':
			case '\n':
				if (buildString) {
					buffer.append(c);
				}
				break;

			default:
				if (buffer == null) {
					buffer = new StringBuilder();
					buffer.append(c);
					buildOther = true;
					separated = false;
				} else {
					buffer.append(c);
				}
				break;
			}
		}
		throw new ParseException(String.format("Json '%s' is missing the %s bracket at the end!", new String(charArr),
				json instanceof JsonObject ? "curly" : "square"), offset);

	}

	/**
	 * Parses and object of any type that is neither string nor json.
	 * 
	 * @param buffer  the {@link StringBuilder} containing the string representation
	 *                of the object to parse.
	 * @param json    the json to add the parsed object to.
	 * @param key     the key for the value to parse. Ignored for {@link JsonArray
	 *                JsonArrays}.
	 * @param charArr the character array containing the full json to parse. Only
	 *                used for error messages.
	 * @param offset  the offset in the string to parse. Only used for error
	 *                messages.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	private static void buildOther(StringBuilder buffer, JsonElement<?> json, StringBuilder key, char[] charArr,
			int offset) throws ParseException {
		String buf = buffer.toString();
		buf = buf.trim();
		if (buf.isEmpty()) {
			throw createParseException("Missing value in json '%s'!", charArr, offset);
		}

		Object result;
		try {
			if (buf.contains(".")) {
				result = Double.parseDouble(buf);
			} else {
				long l = Long.parseLong(buf);
				if (l <= Integer.MAX_VALUE && l >= Integer.MIN_VALUE) {
					result = (int) l;
				} else {
					result = l;
				}
			}
		} catch (Exception e) {
			if (buf.equalsIgnoreCase("true")) {
				result = true;
			} else if (buf.equalsIgnoreCase("false")) {
				result = false;
			} else if (buf.equalsIgnoreCase("null")) {
				result = null;
			} else {
				throw createParseException(String.format("Found object \"%s\" of unknown type in json '%%s'!", buf),
						charArr, offset - buffer.length());
			}
		}

		if (json instanceof JsonArray) {
			((JsonArray) json).add(result);
		} else {
			if (key == null) {
				throw createParseException(String.format("Missing key for value \"%s\" in json '%%s'!", buf), charArr,
						offset - buffer.length());
			}
			((JsonObject) json).put(key.toString(), result);
		}
	}

	/**
	 * Creates a {@link ParseException} with the given error message containing the
	 * subjson that was currently being parsed. The subjson is detected based on the
	 * given offset. The errorOffset of the new {@link ParseException} is based on
	 * said subjson.
	 * 
	 * @param error   the error message for the {@link ParseException}. Should
	 *                contain one string placeholder to be replaced with the
	 *                subjson.
	 * @param charArr the full character array to be parsed.
	 * @param offset  the offset where the error occured.
	 * @return the newly created exception.
	 */
	private static ParseException createParseException(String error, char[] charArr, int offset) {
		int[] json = getJsonAtPosition(charArr, offset);
		return new ParseException(String.format(error, new String(charArr, json[0], json[1])), offset - json[0]);
	}

	/**
	 * Gets the starting position and length of the inner most json at the given
	 * position.
	 * 
	 * @param charArr  a character array containing a string representation of the
	 *                 full json to search in.
	 * @param position the position to look for.
	 * @return an integer array containing the starting position in the first
	 *         position and the length of the json at the second.
	 */
	private static int[] getJsonAtPosition(char[] charArr, int position) {
		Stack<int[]> openJsons = new Stack<>();
		int start = 0;
		boolean string = false;
		boolean escaped = false;
		for (int i = 0; i < charArr.length; i++) {
			char c = charArr[i];
			switch (c) {
			case '{':
				if (!string) {
					if (i < position) {
						start = i;
						openJsons.clear();
					}
					openJsons.push(new int[] { c, i });
				}
				break;

			case '}':
				if (!string) {
					int[] open = openJsons.pop();
					if (open[0] != '{' && i != position) {
						try {
							throw new ParseException(String.format("JsonArray '%s' ends with a curly bracket!",
									new String(charArr, open[1], i - open[1] + 1)), i - open[1]);
						} catch (ParseException e) {
							// Since this method is used for getting the jsons for other ParseExceptions
							// this can't actually throw one.
							e.printStackTrace();
						}
					}

					if (i >= position && openJsons.empty()) {
						return new int[] { start, i - start + 1 };
					}
				}
				break;

			case '[':
				if (!string) {
					if (i < position) {
						start = i;
						openJsons.clear();
					}
					openJsons.push(new int[] { c, i });
				}
				break;

			case ']':
				if (!string) {
					int[] open = openJsons.pop();
					if (open[0] != '[' && i != position) {
						try {
							throw new ParseException(String.format("JsonObject '%s' ends with a square bracket!",
									new String(charArr, open[1], i - open[1] + 1)), i - open[1]);
						} catch (ParseException e) {
							// Since this method is used for getting the jsons for other ParseExceptions
							// this can't actually throw one.
							e.printStackTrace();
						}
					}

					if (i >= position && openJsons.empty()) {
						return new int[] { start, i - start + 1 };
					}
				}
				break;

			case '"':
				if (escaped) {
					escaped = false;
				} else {
					string = !string;
				}
				break;

			case '\\':
				escaped = !escaped;
				break;
			}
		}
		return new int[] { start, charArr.length };
	}

}
