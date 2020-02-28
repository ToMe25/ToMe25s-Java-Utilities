package com.tome25.utils.json;

import java.nio.BufferOverflowException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
//import java.util.Random;
import java.util.Set;

//import javax.management.openmbean.InvalidOpenTypeException;

public class JSONObject {
	
	private Map<String, Object> content;
	//private static Random r = new Random();
	
	public JSONObject() {
		content = new HashMap<String, Object>();
	}
	
	public JSONObject(String key, Object value) {
		content = new HashMap<String, Object>();
		content.put(key, value);
	}
	
	/*public static void main(String[] args) {
		JSONObject json = new JSONObject();
		int i = 0;
		for(String s:args) {
			try {
				json.add("test" + i, s);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		try {
			json.add("test", "Test String!");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			json.add("testS", "Another Test String.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//json.add("test1", 123);
			json.add("testI", r.nextInt(1000));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//Random r = new Random();
			json.add("testB", r.nextBoolean());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			json.add("testS1", "a Test String with this chars: \"(,;.:(){}[]@!\\)\".");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			json.add("testS2", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			json.add("testJSON", json.clone());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			//JSONObject test = getFromString(json.toString());
			//JSONObject test = parseJson(json.toString());
			//System.out.println("JSONObject: " + "subjson testI = " + ((JSONObject)json.get("testJSON")).get("testI"));
			JSONObject test = parseString(json.toString());
			System.out.println("JSONObject: " + test.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		System.out.println("JSONObject: " + json.toString());
	}*/
	
	/**
	 * Adds the Value with the given key if there are no Object with this Key.
	 * @param key the Key.
	 * @param value the Value.
	 * @throws InvalidKeyException when there Allready an Object with this Key.
	 */
	//public void add(String key, Object value) {
	public void add(String key, Object value) throws InvalidKeyException {
		//content.put(key, value);
		if(!content.containsKey(key)) {
			content.put(key, value);
			//System.out.println("JSONObject: " + "Added " + key + " = " + value + "!");
			//System.out.print("JSONObject: " + "Added " + key);
			//System.out.print(value + "!");
		}
		else {
			//throw new InvalidKeyException("Key \"" + key + "\" Allready existing!");
			throw new InvalidKeyException("Key \"" + key + "\" Allready exists!");
		}
	}
	
	public void put(String key, Object value) {
		content.put(key, value);
	}
	
	public void remove(String key) {
		content.remove(key);
	}
	
	public Object get(String key) {
		return content.get(key);
	}
	
	public Set<String> getKeySet(){
		return content.keySet();
	}
	
	public boolean contains(String key) {
		return content.containsKey(key);
	}
	
	/**
	 * 
	 * @return the size of this Object.
	 */
	public int size() {
		return content.size();
	}
	
	/**
	 * Returns a String representation of this element.
	 */
	@Override
	public String toString() {
		//String ret = "";
		String ret = "{";
		for(String s:content.keySet()) {
			//ret += "{\"";
			ret += "\"";
			ret += s;
			//ret += "\",";
			ret += "\":";
			Object obj = content.get(s);
			if(obj instanceof Boolean || obj instanceof Integer) {
				ret += obj;
				ret += ",";
			}
			//else if(obj instanceof String) {
				//ret += "\"";
				//ret += (String) obj;
				//ret += "\",";
			//}
			else {
				ret += "\"";
				//ret += obj.toString();
				ret += obj.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
				//ret += "\"";
				ret += "\",";
				//while(!ret.endsWith(",")) {
					//System.out.println("JSONObject: " + ret);
					//ret += "\",";
				//}
				//System.out.println("JSONObject: " + "String!");
				//System.out.println("JSONObject: " + ret);
				//for(byte b:ret.getBytes()) {
					//System.out.println("JSONObject: " + b);
					//System.out.println("JSONObject: " + (char) b);
					//try {
						//System.out.println("JSONObject: " + (char) b);
					//} catch (Exception e) {
						// TODO: handle exception
						//e.printStackTrace();
					//}
				//}
			}
			//ret += content.get(s).toString();
		}
		if(ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		//ret = ret.substring(0, ret.length() - 1);
		//System.out.println("JSONObject: " + ret);
		ret += "}";
		//while(!ret.endsWith("}")) {
			//ret += "}";
		//}
		//System.out.println("JSONObject: " + ret);
		return ret;
		//return "";
	}
	
	/**
	 * Returns a String representation of this element as byte Array.
	 */
	public byte[] toByteArray() {
		return this.toString().getBytes();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		JSONObject clone = new JSONObject();
		for(String s:content.keySet()) {
			try {
				clone.add(s, content.get(s));
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return clone;
	}
	
	/**
	 * parses the given String to a JSONObject.
	 * faster but can only parse String and Integer Objects inside of JSON's no booleans no Other JSON's etc.
	 * not as reliable as the slower one.
	 * @param s the String to parse
	 * @return the JSON Object parsed from String s
	 * @throws ParseException 
	 */
	//public static JSONObject parseStringFast(String str) {
	public static JSONObject parseStringFast(String str) throws ParseException {
		JSONObject json = new JSONObject();
		//if(str.contains("}")) {
		if(str.contains("{") && str.contains("}")) {
			//str = str.substring(0, str.lastIndexOf("}"));
			//str = str.substring(str.indexOf("{"), str.lastIndexOf("}"));
			str = str.substring(str.indexOf("{") + 1, str.lastIndexOf("}"));
		}
		//else {
			//throw new ParseException(str, 0);
		//}
		else if(str.contains("{")) {
			throw new ParseException(str, str.length());
		}
		else {
			throw new ParseException(str, 0);
			//throw new ParseException(str, 1);
		}
		for(String s:str.split(",")) {
			//s = s.replaceAll(":", "").replaceAll(" ", "").replaceAll("\"", "").replaceAll("\\{", "").replaceAll("\\}", "");
			//s = s.replaceAll(":", "").replaceAll(" ", "").replaceAll("\\{", "").replaceAll("\\}", "");
			//s = s.replaceAll(" ", "").replaceAll("\\{", "").replaceAll("\\}", "");
			//s = s.replaceAll(" ", "").replaceAll("\\{", "");
			//s = s.replaceAll(" ", "");
			if(s.contains(":")) {
				//String[] pair = s.replaceAll("\"", "").split(":");
				String[] pair = s.split(":");
				String key = pair[0];
				String value = pair[1];
				key = key.replaceAll("\"", "");
				if(value.contains("\"")) {
					json.put(key, value.replaceAll("\"", ""));
				}
				else {
					json.put(key, Integer.parseInt(value));
				}
				//json.put(pair[0], pair[1]);
			}
		}
		return json;
	}
	
	/**
	 * parses the given String to a JSONObject.
	 * @param s the String to parse
	 * @return the JSON Object parsed from String s
	 * @throws InvalidKeyException
	 * @throws ParseException
	 */
	public static JSONObject parseString(String s) throws InvalidKeyException, ParseException {
		JSONObject json = null;
		boolean buildString = false;
		boolean buildJson = false;
		boolean buildOther = false;
		boolean escaped = false;
		String key = null;
		String buffer = null;
		short layer = 0;
		short offset = 0;
		for(char c:s.toCharArray()) {
			switch(c) {
			case '{':
				if(json == null) {
					json = new JSONObject();
				}
				else if(buildString) {
					buffer += c;
				}
				else if(buildJson) {
					buffer += c;
					layer++;
				}
				else {
					buffer = "{";
					buildJson = true;
					layer++;
				}
				break;
				
			case '\\':
				if(escaped) {
					buffer += c;
					escaped = false;
				}
				else if(buildJson) {
					buffer += c;
					escaped = true;
				}
				else {
					escaped = true;
				}
				break;
				
			case '"':
				if(escaped) {
					buffer += c;
					escaped = false;
				}
				else if(buildJson) {
					buffer += c;
					buildString = !buildString;
				}
				else if(buildString) {
					if(key == null) {
						key = buffer;
						buffer =  null;
					}
					else {
						json.add(key, buffer);
						key = null;
						buffer = null;
					}
					buildString = false;
				}
				else {
					buffer = "";
					buildString = true;
				}
				break;
				
			case ':':
				if(buildString || buildJson) {
					buffer += c;
				}
				break;
				
			case ',':
				if(buildString || buildJson) {
					buffer += c;
				}
				else if(buildOther) {
					try {
						int i = Integer.parseInt(buffer);
						json.add(key, i);
						key = null;
						buffer = null;
						buildOther = false;
					} catch (Exception e) {
						if(buffer.equalsIgnoreCase("true")) {
							if(key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							json.add(key, true);
							key = null;
							buffer = null;
							buildOther = false;
						}
						else if(buffer.equalsIgnoreCase("false")) {
							if(key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							json.add(key, false);
							key = null;
							buffer = null;
							buildOther = false;
						}
						else {
							throw new ParseException("type for value \"" + buffer + "\" Unknown!", offset);
						}
					}
				}
				break;
				
			case '}':
				if(buildString) {
					buffer += c;
				}
				else if(buildJson) {
					buffer += c;
					if(layer > 0) {
						layer--;
					}
					if(layer <= 0) {
						JSONObject subjson = parseString(buffer);
						if(key == null) {
							throw new ParseException("Key Missing!", offset);
						}
						json.add(key, subjson);
						key = null;
						buffer = null;
						buildJson = false;
					}
				}
				else if(buildOther) {
					try {
						int i = Integer.parseInt(buffer);
						json.add(key, i);
						key = null;
						buffer = null;
						buildOther = false;
					} catch (Exception e) {
						if(buffer.equalsIgnoreCase("true")) {
							if(key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							json.add(key, true);
							key = null;
							buffer = null;
							buildOther = false;
						}
						else if(buffer.equalsIgnoreCase("false")) {
							if(key == null) {
								throw new ParseException("Key Missing!", offset);
							}
							json.add(key, false);
							key = null;
							buffer = null;
							buildOther = false;
						}
						else {
							//throw new InvalidOpenTypeException("type for value " + buffer + "Unknown!");
							throw new ParseException("type for value \"" + buffer + "\" Unknown!", offset);
						}
					}
				}
				else {
					return json;
				}
				break;
				
			default:
				if(buffer == null) {
					buffer = "" + c;
					buildOther = true;
				}
				else if(buildString || buildJson || buildOther) {
					buffer += c;
				}
				break;
			}
			offset++;
		}
		return json;
	}
	
	/**
	 * Returns an JSONObject build from this String.
	 * use parseString(String s) instead!
	 * @param str the String to use.
	 * @return the build JSONObject.
	 * @throws ParseException if the String isnt a correct JSON or contains unsupported components.
	 * @throws InvalidKeyException when in the new JSONObject are Allready an Object with this Key.
	 * @throws BufferOverflowException if a Buffer Overflows.
	 */
	@Deprecated
	public static JSONObject parseJson(String str) throws ParseException, InvalidKeyException, BufferOverflowException {
		JSONObject ret = null;
		for(char c:str.toCharArray()) {
			switch(c) {
			case '{':
				ParseHelper.start.parse(c);
				break;
				
			case '}':
				ParseHelper.end.parse(c);
				ret = ParseHelper.end.getJson();
				break;
				
			case '\\':
				ParseHelper.backslash.parse(c);
				break;
				
			//case '=':
				//ParseHelper.equals.parse(c);
				//break;
				
			case '"':
				ParseHelper.quote.parse(c);
				break;
				
			case ':':
				ParseHelper.colon.parse(c);
				break;
				
			case ',':
				ParseHelper.comma.parse(c);
				break;
				
			default:
				ParseHelper.string.parse(c);
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Returns an JSONObject build from this String.
	 * Please use parseJson(String str) instead!
	 * @param str the String to use.
	 * @return the build JSONObject.
	 * @throws ParseException if the String isnt a correct JSON or contains unsupported components.
	 * @throws InvalidKeyException when in the new JSONObject are Allready an Object with this Key.
	 * @throws BufferOverflowException if a Buffer Overflows.
	 */
	//@SuppressWarnings("null")
	@Deprecated
	public static JSONObject parse(String str) throws ParseException, InvalidKeyException, BufferOverflowException {
		//System.out.println("JSONObject: " + str);
		JSONObject ret = null;
		//char[] buffer = null;
		char[] StrBuffer = null;
		char[] IntBuffer = null;
		char[] BoolBuffer = null;
		String key = null;
		int i = 0;
		for(char c:str.toCharArray()) {
			//if(c == '{') {
				//if(ret.equals(null)) {
				//if(ret == null) {
					//ret = new JSONObject();
				//}
			//}
			//else if(c == '}') {
				//if(IntBuffer != null) {
					//ret.add(key, new String(IntBuffer));
					//ret.add(key, Integer.parseInt(new String(IntBuffer, 0, i)));
					//IntBuffer = null;
				//}
				//else if(BoolBuffer != null) {
					//ret.add(key, new String(BoolBuffer));
					//ret.add(key, Boolean.parseBoolean(new String(BoolBuffer, 0, i)));
					//BoolBuffer = null;
				//}
				//return ret;
			//}
			if(c == ':') {
				if(key == null) {
					//key = new String(StrBuffer, 0, StrBuffer.length - 1);
					//key = new String(StrBuffer, 0, i - 1);
					key = new String(StrBuffer, 0, i);
					StrBuffer = null;
					//key = null;
				}
				else {
					StrBuffer[i] = c;
					i++;
				}
			}
			//if(c == '"') {
			else if(c == '"') {
				//System.out.println("JSONObject: " + '"');
				if(StrBuffer == null) {
				//if(StrBuffer == null || i == 0) {
					StrBuffer = new char[1024];
					i = 0;
				}
				//else if(key == null && i < 2) {
				//else if(i < 1) {
				else if(i < 2) {
					//if(StrBuffer[0] == ',') {
					if(key == null && StrBuffer[0] == ',') {
						StrBuffer = new char[1024];
						i = 0;
					}
					else {
						StrBuffer[i] = c;
						i++;
					}
				}
				else if(key != null) {
				//else if(key != null && key != "") {
					ret.add(key, new String(StrBuffer, 0, i));
					StrBuffer = null;
					key = null;
				}
				//else if(i < 2) {
					//if(StrBuffer[0] == ',') {
						//StrBuffer = new char[1024];
						//i = 0;
					//}
				//}
				//else {
					//StrBuffer[i] = c;
					//ret.add(key, new String(StrBuffer, 0, i));
					//StrBuffer = null;
					//key = null;
				//}
				//if(buffer == null) {
					//buffer = new char[1024];
					//i = 0;
				//}
				//else if(key == null) {
					//key = buffer.toString();
					//key = new String(buffer);
					//System.out.println("JSONObject: " + new String(buffer));
					//buffer = null;
					//key = new String(StrBuffer);
					//key = new String(StrBuffer, 0, i + 1);
					//key = new String(StrBuffer, 0, i);
					//System.out.println("JSONObject: " + new String(StrBuffer));
					//System.out.println("JSONObject: " + "key = " + new String(StrBuffer));
					//StrBuffer = null;
				//}
				//else {
					//char[] Test = {'H', 'A', 'L', 'L', 'O'};
					//char[] Test = {'H', 'a', 'l', 'l', 'o'};
					//ret.add(key, buffer.toString());
					//ret.add(key, new String(buffer));
					//ret.put(key, new String(buffer));
					//System.out.println("JSONObject: " + buffer.toString());
					//System.out.println("JSONObject: " + new String(buffer));
					//buffer = null;
					//key = null;
					//String value = new String(StrBuffer);
					//System.out.println("JSONOblect: " + value);
					//ret.add(key, new String(Test));
					//ret.add(new String(Test), new String(Test));
					//ret.add(key, value);
					//ret.add(key, new String(StrBuffer));
					//ret.add(key, new String(StrBuffer, 0, i));
					//System.out.println("JSONObject: " + key + " = " + new String(StrBuffer) + "!");
					//System.out.println("JSONObject: " + key + " : " + new String(StrBuffer) + "!");
					//System.out.println("JSONObject: " + "value = " + new String(StrBuffer));
					//StrBuffer = null;
					//key = null;
				//}
				//buffer = new char[1024];
				//i = 0;
				//System.out.println("JSONOBject: " + '"');
			}
			//else if(c == ':') {
				
			//}
			else if(StrBuffer != null) {
				if(i < StrBuffer.length) {
					StrBuffer[i] = c;
					i++;
				}
				else {
					throw new BufferOverflowException();
				}
			}
			else if(c == '{') {
				if(ret == null) {
					ret = new JSONObject();
				}
			}
			else if(c == '}') {
				if(IntBuffer != null) {
					ret.add(key, Integer.parseInt(new String(IntBuffer, 0, i)));
					IntBuffer = null;
				}
				else if(BoolBuffer != null) {
					ret.add(key, Boolean.parseBoolean(new String(BoolBuffer, 0, i)));
					BoolBuffer = null;
				}
				return ret;
			}
			//else if(c == ':') {
				
			//}
			else if(Character.isDigit(c)) {
				if(IntBuffer == null) {
					//if(key != null) {
					//if(StrBuffer == null && BoolBuffer == null) {
					if(key != null && StrBuffer == null && BoolBuffer == null) {
						IntBuffer = new char[1024];
						i = 0;
					}
					//IntBuffer = new char[1024];
					//i = 0;
				}
				if(i < IntBuffer.length) {
					IntBuffer[i] = c;
					i++;
				}
				else {
					throw new BufferOverflowException();
				}
				//IntBuffer[i] = c;
				
			}
			else if(c == ',') {
				if(IntBuffer != null) {
					//int in = Integer.parseInt(new String(IntBuffer));
					//ret.add(key, in);
					//ret.add(key, Integer.parseInt(new String(IntBuffer)));
					ret.add(key, Integer.parseInt(new String(IntBuffer, 0, i)));
					IntBuffer = null;
					key = null;
				}
				else if(BoolBuffer != null) {
					//ret.add(key, Boolean.parseBoolean(new String(BoolBuffer)));
					ret.add(key, Boolean.parseBoolean(new String(BoolBuffer, 0, i)));
					BoolBuffer = null;
					key = null;
				}
				//else {
					//StrBuffer = null;
				//}
			}
			else {
				if(BoolBuffer == null) {
					//if(key != null) {
					//if(StrBuffer == null && IntBuffer == null) {
					if(key != null && StrBuffer == null && IntBuffer == null) {
						BoolBuffer = new char[1024];
						i = 0;
					}
					//BoolBuffer = new char[1024];
					//i = 0;
				}
				if(BoolBuffer != null) {
					if(i < BoolBuffer.length) {
						BoolBuffer[i] = c;
						i++;
					}
					else {
						throw new BufferOverflowException();
					}
				}
				//if(i < BoolBuffer.length) {
					//BoolBuffer[i] = c;
					//i++;
				//}
				//else {
					//throw new BufferOverflowException();
				//}
			}
			//else if(!buffer.equals(null)) {
			//else if(buffer != null) {
				//if(i < buffer.length) {
					//buffer[i] = c;
					//i++;
				//}
				//else {
					//throw new BufferOverflowException();
				//}
				//System.out.println("JSONObject: " + buffer.length);
			//}
		}
		throw new ParseException(str, str.length());
		//return ret;
		/**JSONObject ret = null;
		byte[] buffer = null;
		String key;
		for(Byte b:str.getBytes()) {
			if(b.equals('{')) {
				if(ret.equals(null)) {
					ret = new JSONObject();
				}
			}
			//else if(b.equals('"')) {
			//else if(b.equals(Byte.parseByte("\""))) {
			else if(b.equals("\"")) {
				buffer = new byte[1024];
				System.out.println("JSONOBject: " + '"');
			}
			else if(!buffer.equals(null)) {
				System.out.println("JSONObject: " + buffer.length);
			}
		}
		return ret;*/
	}
	
	/**
	 * Returns an JSONObject build from this String.
	 * Please use parseString(String str) instead!
	 * @param str the String to use.
	 * @return the build JSONObject.
	 * @throws ParseException id the String isnt a correct JSON or contains unsupported components.
	 */
	@Deprecated
	public static JSONObject getFromString(String str) throws ParseException {
		if(str.startsWith("{")) {
			if(str.endsWith("}")) {
				//str = str.replaceAll("\\{", "").replaceAll("\\}", "");
				str = str.substring(1, str.length() - 1);
				//System.out.println(str);
				//if(str.length() == 2) {
				if(str.length() == 0) {
					return new JSONObject();
				}
				//else if(str.length() < 7) {
				else if(str.length() < 5) {
					throw new ParseException(str, str.length() - 2);
				}
				JSONObject ret = new JSONObject();
				String[] elements = str.split(",");
				for(String element:elements) {
					String key = "";
					String rawValue = "";
					int i = 0;
					for(String s:element.split(":")) {
						switch (i) {
						case 0:
							key = s.replaceAll("\"", "");
							break;
							
						case 1:
							rawValue += s;
							break;
							
						default:
							rawValue += ":";
							rawValue += s;
							break;
						}
						i++;
					}
					//String key = element.split(":")[0];
					//String key = element.split(":")[0].replaceAll("\"", "");
					//String rawValue = element.split(":")[1];
					//if(rawValue.startsWith("\"")) {
					if(rawValue.startsWith("\"") && rawValue.endsWith("\"")) {
						String Value = rawValue.substring(1, rawValue.length() -1);
						ret.put(key, Value);
						//ret.put(key, rawValue.substring(1, rawValue.length() -1));
					}
					else {
						try {
							int Value = Integer.parseInt(rawValue);
							ret.put(key, Value);
						} catch (Exception e) {
							boolean Value = false;
							if(rawValue.equalsIgnoreCase("true")) {
								Value = true;
								//System.out.println("true");
							}
							else if(rawValue.equalsIgnoreCase("false")) {
								Value = false;
								//System.out.println("false");
							}
							else {
								throw new ParseException(str, str.length() - 1);
							}
							//boolean Value = Boolean.parseBoolean(rawValue);
							ret.put(key, Value);
						}
					}
				}
				return ret;
			}
			else {
				throw new ParseException(str, str.length() - 1);
			}
		}
		else {
			throw new ParseException(str, 0);
		}
	}
	
	private enum ParseHelper {
		
		//equals() {
			
		//},
		string() {
			@Override
			protected void parse(char c) {
				// TODO Auto-generated method stub
				//super.parse(c);
				//stringBuffer[strBuf] = c;
				//strBuf ++;
				//if(stringBuffer != null) {
					//stringBuffer[strBuf] = c;
					//strBuf ++;
				//}
				//else if(key != null) {
					//key += c;
				//}
				if(startQuote) {
					if(stringBuffer != null) {
						stringBuffer[strBuf] = c;
						strBuf ++;
					}
					else if(key != null) {
						key += c;
					}
				}
				else {
					if(integerBuffer != null) {
						integerBuffer[intBuf] = c;
						intBuf++;
					}
					else if(booleanBuffer != null) {
						booleanBuffer[boolBuf] = c;
						boolBuf++;
					}
					else if(c == 't' || c == 'f' || c == 'T' || c == 'F') {
						booleanBuffer = new char[1024];
						booleanBuffer[boolBuf] = c;
						boolBuf++;
					}
					//else {
					else if(Character.isDigit(c)) {
						integerBuffer = new char[1024];
						integerBuffer[intBuf] = c;
						intBuf++;
					}
				}
			}
		},
		start() {
			@Override
			protected void parse(char c) {
				// TODO Auto-generated method stub
				//super.parse(c);
				//json = new JSONObject();
				if(json == null) {
					json = new JSONObject();
				}
				else if(startQuote) {
					string.parse(c);
				}
			}
		},
		end() {
			@Override
			protected void parse(char c) {
				// TODO Auto-generated method stub
				//super.parse(c);
				if(startQuote) {
					string.parse(c);
				}
				else {
					try {
						if(stringBuffer != null) {
							String val = new String(stringBuffer);
							val = val.substring(0, val.indexOf(0));
							json.add(key, val);
							key = null;
							stringBuffer = null;
							strBuf = 0;
						}
						else if(integerBuffer != null) {
							String str = new String(integerBuffer);
							str = str.substring(0, str.indexOf(0));
							int val = Integer.parseInt(str);
							json.add(key, val);
							key = null;
							integerBuffer = null;
							intBuf = 0;
						}
						else if(booleanBuffer != null) {
							String str = new String(booleanBuffer);
							str = str.substring(0, str.indexOf(0));
							boolean val = Boolean.parseBoolean(str);
							json.add(key, val);
							key = null;
							booleanBuffer = null;
							boolBuf = 0;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		},
		quote() {
			@Override
			protected void parse(char c) {
				// TODO Auto-generated method stub
				//super.parse(c);
				//if(key == null) {
					//key = "";
				//}
				//else {
					//stringBuffer = new char[1024];
				//}
				//if(!startQuote) {
					//if(key == null) {
						//key = "";
					//}
					//else {
						//stringBuffer = new char[1024];
					//}
					//startQuote = true;
					//System.out.println("JSONObject: " + "Startquote!");
				//}
				//else {
					//try {
						//json.add(key, new String(stringBuffer));
						//if(stringBuffer != null) {
							//System.out.println("JSONObject: " + new String(stringBuffer).length());
							//json.add(key, new String(stringBuffer));
							//String val = new String(stringBuffer);
							//val = val.substring(0, val.indexOf(0));
							//json.add(key, val);
						//}
					//} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					//}
					//startQuote = false;
					//System.out.println("JSONObject: " + "Endquote!");
				//}
				if(!escaped) {
					if(!startQuote) {
						if(key == null) {
							key = "";
						}
						else {
							stringBuffer = new char[1024];
						}
						startQuote = true;
						//System.out.println("JSONObject: " + "Startquote!");
					}
					else {
						startQuote = false;
						//System.out.println("JSONObject: " + "Endquote!");
					}
				}
				else {
					string.parse(c);
					escaped = false;
				}
			}
		},
		colon() {
			@Override
			protected void parse(char c) {
				// TODO Auto-generated method stub
				//super.parse(c);
				if(startQuote) {
					ParseHelper.string.parse(c);
				}
			}
		},
		comma() {
			@Override
			protected void parse(char c) {
				// TODO Auto-generated method stub
				//super.parse(c);
				if(startQuote) {
					string.parse(c);
				}
				else {
					try {
						if(stringBuffer != null) {
							String val = new String(stringBuffer);
							val = val.substring(0, val.indexOf(0));
							json.add(key, val);
							key = null;
							stringBuffer = null;
							strBuf = 0;
						}
						else if(integerBuffer != null) {
							String str = new String(integerBuffer);
							str = str.substring(0, str.indexOf(0));
							int val = Integer.parseInt(str);
							json.add(key, val);
							key = null;
							integerBuffer = null;
							intBuf = 0;
						}
						else if(booleanBuffer != null) {
							String str = new String(booleanBuffer);
							str = str.substring(0, str.indexOf(0));
							boolean val = Boolean.parseBoolean(str);
							json.add(key, val);
							key = null;
							booleanBuffer = null;
							boolBuf = 0;
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		},
		backslash() {
			@Override
			protected void parse(char c) {
				// TODO Auto-generated method stub
				//super.parse(c);
				if(!escaped) {
					escaped = true;
					//System.out.println("JSONObject: " + "Escaped");
				}
				else {
					string.parse(c);
					escaped = false;
				}
			}
		};
		
		private ParseHelper() {
			//stringBuffer = new char[1024];
			//init();
		}
		
		//private void init() {
			//stringBuffer = new char[1024];
		//}
		
		private static char[] stringBuffer;
		private static int strBuf;
		private static char[] integerBuffer;
		private static int intBuf;
		private static char[] booleanBuffer;
		private static int boolBuf;
		private static JSONObject json;
		private static String key;
		private static boolean startQuote;
		private static boolean escaped;
		//private static boolean Int;
		//private static boolean Bool;
		
		protected void parse(char c) {
			
		}
		
		protected JSONObject getJson() {
			return json;
		}
		
	}
	
}