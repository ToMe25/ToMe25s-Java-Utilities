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
	 * it to this library there were multiple character that could make it crash or
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
					buffer = buffer.trim();
					try {
						if (buffer.contains(".")) {
							double d = Double.parseDouble(buffer);
							if (json instanceof JsonArray) {
								((JsonArray) json).add(d);
							} else {
								json.add(key, d);
								key = null;
							}
						} else {
							int i = Integer.parseInt(buffer);
							if (json instanceof JsonArray) {
								((JsonArray) json).add(i);
							} else {
								json.add(key, i);
								key = null;
							}
						}
						buffer = null;
						buildOther = false;
					} catch (Exception e) {
						if (buffer.equalsIgnoreCase("true")) {
							if (json instanceof JsonArray) {
								((JsonArray) json).add(buffer);
							} else {
								if (key == null) {
									throw new ParseException("Key Missing!", offset);
								}
								json.add(key, true);
								key = null;
							}
							buffer = null;
							buildOther = false;
						} else if (buffer.equalsIgnoreCase("false")) {
							if (json instanceof JsonArray) {
								((JsonArray) json).add(buffer);
							} else {
								if (key == null) {
									throw new ParseException("Key Missing!", offset);
								}
								json.add(key, false);
								key = null;
							}
							buffer = null;
							buildOther = false;
						} else {
							throw new ParseException("type for value \"" + buffer + "\" Unknown!", offset);
						}
					}
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
					try {
						if (buffer.contains(".")) {
							double d = Double.parseDouble(buffer);
							json.add(key, d);
						} else {
							int i = Integer.parseInt(buffer);
							json.add(key, i);
						}
						key = null;
						buffer = null;
						buildOther = false;
					} catch (Exception e) {
						if (buffer.equalsIgnoreCase("true")) {
							if (key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							json.add(key, true);
							key = null;
							buffer = null;
							buildOther = false;
						} else if (buffer.equalsIgnoreCase("false")) {
							if (key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							json.add(key, false);
							key = null;
							buffer = null;
							buildOther = false;
						} else {
							throw new ParseException("type for value \"" + buffer + "\" Unknown!", offset);
						}
					}
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
						}
						key = null;
						buffer = null;
						buildJson = false;
					}
				} else if (buildOther) {
					buffer = buffer.trim();
					if (buffer.isEmpty()) {
						return json;
					}
					try {
						if (buffer.contains(".")) {
							double d = Double.parseDouble(buffer);
							((JsonArray) json).add(d);
						} else {
							int i = Integer.parseInt(buffer);
							((JsonArray) json).add(i);
						}
						key = null;
						buffer = null;
						buildOther = false;
					} catch (Exception e) {
						if (buffer.equalsIgnoreCase("true")) {
							if (key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							((JsonArray) json).add(true);
							key = null;
							buffer = null;
							buildOther = false;
						} else if (buffer.equalsIgnoreCase("false")) {
							if (key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							((JsonArray) json).add(false);
							key = null;
							buffer = null;
							buildOther = false;
						} else {
							throw new ParseException("type for value \"" + buffer + "\" Unknown!", offset);
						}
					}
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

}
