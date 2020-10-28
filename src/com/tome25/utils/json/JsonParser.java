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
	 * This method parses the given string to a {@link JsonObject}. Faster but not
	 * as reliable as the slower one.
	 * 
	 * Supported object types inside the Json: Integer and String.
	 * 
	 * WARNING: This method is not safe, over the time i worked on it before adding
	 * it to this library there were multiple character that could make it crash or
	 * break out of string values.
	 * 
	 * @param str the String to parse.
	 * @return the {@link JsonObject} parsed from the given string.
	 * @throws ParseException if something goes wrong while parsing.
	 */
	public static JsonObject parseStringFast(String str) throws ParseException {
		JsonObject json = new JsonObject();
		if (str.contains("{") && str.contains("}")) {
			str = str.substring(str.indexOf("{") + 1, str.lastIndexOf("}"));
		} else if (str.contains("{")) {
			throw new ParseException(str, str.length());
		} else {
			throw new ParseException(str, 0);
		}
		for (String s : str.split(",")) {
			if (s.contains(":")) {
				String[] pair = s.split(":");
				String key = pair[0];
				String value = "";
				boolean v = false;
				int size = pair.length;
				for (int i = 1; i < size; i++) {
					value += (v ? ":" : "") + pair[i];
					v = true;
				}
				key = key.substring(1, key.length() - 1);
				if (value.contains("\"")) {
					value = value.substring(1, value.length() - 1);
					if (value.contains("\\\"")) {
						value = value.replaceAll("\\\\\"", "\"");
					}
					if (value.contains("\\\\")) {
						value = value.replaceAll("\\\\\\\\", "\\\\");
					}
					json.put(key, value);
				} else {
					json.put(key, Integer.parseInt(value));
				}
			}
		}
		return json;
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
		String key = null;
		String buffer = null;
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
					buffer += c;
				} else if (buildJson) {
					buffer += c;
					layer++;
				} else {
					buffer = "{";
					buildJson = true;
					layer++;
				}
				break;

			case '\\':
				if (escaped || buildJson) {
					buffer += c;
				}
				escaped = !escaped;
				break;

			case '"':
				if (escaped) {
					buffer += c;
					escaped = false;
				} else if (buildJson) {
					buffer += c;
					buildString = !buildString;
				} else if (buildString) {
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buffer);
						buffer = null;
					} else if (key == null) {
						key = buffer;
						buffer = null;
					} else {
						((JsonObject) json).add(key, buffer);
						key = null;
						buffer = null;
					}
					buildString = false;
				} else {
					buffer = "";
					buildString = true;
				}
				break;

			case ':':
				if (buildString || buildJson) {
					buffer += c;
				} else if (json instanceof JsonArray) {
					throw new ParseException(
							String.format("Found key value pair with key \"%s\" while parsing JsonArray '%s'!",
									json.get(json.size() - 1), new String(charArr)),
							offset);
				}
				break;

			case ',':
				if (buildString || buildJson) {
					buffer += c;
				} else if (buildOther) {
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buildOther(buffer, charArr, offset));
					} else {
						if (key == null) {
							throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!", buffer,
									new String(charArr)), offset - buffer.length());
						}
						((JsonObject) json).add(key, buildOther(buffer, charArr, offset));
						key = null;
					}
					buffer = null;
					buildOther = false;
				}
				break;

			case '}':
				if (buildString) {
					buffer += c;
				} else if (buildJson) {
					buffer += c;
					if (layer > 0) {
						layer--;
					}
					if (layer <= 0) {
						JsonElement<?> subjson = parseString(buffer);
						if (json instanceof JsonArray) {
							((JsonArray) json).add(subjson);
						} else {
							if (key == null) {
								throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!",
										buffer, new String(charArr)), offset - buffer.length());
							}
							((JsonObject) json).add(key, subjson);
							key = null;
						}
						buffer = null;
						buildJson = false;
					}
				} else if (buildOther) {
					buffer = buffer.trim();
					if (buffer.isEmpty()) {
						return json;
					}
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buildOther(buffer, charArr, offset));
					} else {
						if (key == null) {
							throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!", buffer,
									new String(charArr)), offset - buffer.length());
						}
						((JsonObject) json).add(key, buildOther(buffer, charArr, offset));
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
					buffer += c;
				} else if (buildJson) {
					buffer += c;
					layer++;
				} else {
					buffer = "[";
					buildJson = true;
					layer++;
				}
				break;

			case ']':
				if (buildString) {
					buffer += c;
				} else if (buildJson) {
					buffer += c;
					if (layer > 0) {
						layer--;
					}
					if (layer <= 0) {
						JsonElement<?> subjson = parseString(buffer);
						if (json instanceof JsonArray) {
							((JsonArray) json).add(subjson);
						} else {
							if (key == null) {
								throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!",
										buffer, new String(charArr)), offset - buffer.length());
							}
							((JsonObject) json).add(key, subjson);
							key = null;
						}
						buffer = null;
						buildJson = false;
					}
				} else if (buildOther) {
					buffer = buffer.trim();
					if (buffer.isEmpty()) {
						return json;
					}
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buildOther(buffer, charArr, offset));
					} else {
						if (key == null) {
							throw new ParseException(String.format("Missing key for value \"%s\" in json '%s'!", buffer,
									new String(charArr)), offset - buffer.length());
						}
						((JsonObject) json).add(key, buildOther(buffer, charArr, offset));
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
					buffer += c;
				}
				break;

			case '\n':
				if (buildString || buildJson) {
					buffer += c;
				}
				break;

			default:
				if (buffer == null) {
					buffer = "" + c;
					buildOther = true;
				} else {
					buffer += c;
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
