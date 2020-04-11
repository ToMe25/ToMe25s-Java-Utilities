package com.tome25.utils.json;

import java.text.ParseException;

public class JsonParser {

	/**
	 * This method parses the given String to a JsonObject. Faster but not as
	 * reliable as the slower one.
	 * 
	 * Supported object types inside the Json: Integer and String.
	 * 
	 * WARNING: This method is not safe, over the time i worked on it before adding
	 * it to this library there were multiple character that could make it crash or
	 * break out of String values.
	 * 
	 * @param s the String to parse
	 * @return the Json Object parsed from String s
	 * @throws ParseException
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
				String value = pair[1];
				key = key.replaceAll("\"", "");
				if (value.contains("\"")) {
					json.put(key, value.replaceAll("\"", ""));
				} else {
					json.put(key, Integer.parseInt(value));
				}
			}
		}
		return json;
	}

	/**
	 * This method parses the given String to a JsonObject.
	 * 
	 * Supported object types inside the Json: Integer, Double, Boolean, String,
	 * Json Objects and Json Arrays/Lists.
	 * 
	 * WARNING: This method is not safe, over the time i worked on it before adding
	 * it to this library there were multiple characters that could make it crash or
	 * break out of String values.
	 * 
	 * @param s the String to parse
	 * @return the Json Object parsed from String s
	 * @throws ParseException
	 */
	public static JsonElement parseString(String s) throws ParseException {
		JsonElement json = null;
		boolean buildString = false;
		boolean buildJson = false;
		boolean buildOther = false;
		boolean escaped = false;
		String key = null;
		String buffer = null;
		short layer = 0;
		short offset = 0;
		for (char c : s.toCharArray()) {
			switch (c) {
			case '{':
				if (json == null) {
					json = new JsonObject();
				} else if (buildString) {
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
				if (escaped) {
					buffer += c;
					escaped = false;
				} else if (buildJson) {
					buffer += c;
					escaped = true;
				} else {
					escaped = true;
				}
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
						json.add(key, buffer);
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
				}
				break;

			case ',':
				if (buildString || buildJson) {
					buffer += c;
				} else if (buildOther) {
					if (json instanceof JsonArray) {
						((JsonArray) json).add(buildOther(buffer, offset));
					} else {
						if (key == null) {
							throw new ParseException("Key Missing!", offset);
						}
						json.add(key, buildOther(buffer, offset));
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
						JsonElement subjson = parseString(buffer);
						if (json instanceof JsonArray) {
							((JsonArray) json).add(subjson);
						} else {
							if (key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							json.add(key, subjson);
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
						((JsonArray) json).add(buildOther(buffer, offset));
					} else {
						if (key == null) {
							throw new ParseException("Key Missing!", offset);
						}
						json.add(key, buildOther(buffer, offset));
						key = null;
					}
					buffer = null;
					buildOther = false;
				} else {
					return json;
				}
				break;

			case '[':
				if (json == null) {
					json = new JsonArray();
				} else if (buildString) {
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
						JsonElement subjson = parseString(buffer);
						if (key == null) {
							throw new ParseException("Key Missing!", offset);
						}
						if (json instanceof JsonArray) {
							((JsonArray) json).add(subjson);
						} else {
							json.add(key, subjson);
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
						((JsonArray) json).add(buildOther(buffer, offset));
					} else {
						if (key == null) {
							throw new ParseException("Key Missing!", offset);
						}
						json.add(key, buildOther(buffer, offset));
						key = null;
					}
					buffer = null;
					buildOther = false;
				} else {
					return json;
				}
				break;

			case ' ':
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
		return json;
	}

	/**
	 * builds an object of any type that is neither string nor json.
	 * 
	 * @param buffer
	 * @param offset
	 * @return
	 * @throws ParseException
	 */
	private static Object buildOther(String buffer, int offset) throws ParseException {
		buffer = buffer.trim();
		try {
			if (buffer.contains(".")) {
				double d = Double.parseDouble(buffer);
				return d;
			} else {
				int i = Integer.parseInt(buffer);
				return i;
			}
		} catch (Exception e) {
			if (buffer.equalsIgnoreCase("true")) {
				return true;
			} else if (buffer.equalsIgnoreCase("false")) {
				return false;
			} else if (buffer.equalsIgnoreCase("null")) {
				return null;
			} else {
				throw new ParseException("type for value \"" + buffer + "\" Unknown!", offset);
			}
		}
	}

}
