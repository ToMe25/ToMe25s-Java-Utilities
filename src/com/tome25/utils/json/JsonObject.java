package com.tome25.utils.json;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject {

	private Map<String, Object> content;

	public JsonObject() {
		content = new HashMap<String, Object>();
	}

	public JsonObject(String key, Object value) {
		content = new HashMap<String, Object>();
		content.put(key, value);
	}

	/**
	 * Adds the Value with the given key if there are no Object with this Key.
	 * 
	 * @param key   the Key.
	 * @param value the Value.
	 * @throws InvalidKeyException when there Allready an Object with this Key.
	 */
	public void add(String key, Object value) throws InvalidKeyException {
		if (!content.containsKey(key)) {
			content.put(key, value);
		} else {
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

	public Set<String> getKeySet() {
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
		String ret = "{";
		for (String s : content.keySet()) {
			ret += "\"";
			ret += s;
			ret += "\":";
			Object obj = content.get(s);
			if (obj instanceof Boolean || obj instanceof Integer) {
				ret += obj;
				ret += ",";
			} else if(obj instanceof JsonObject) {
				ret += obj.toString();
			} else {
				ret += "\"";
				ret += obj.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
				ret += "\",";
			}
		}
		if (ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		ret += "}";
		return ret;
	}

	/**
	 * Returns a String representation of this element as byte Array.
	 */
	public byte[] toByteArray() {
		return this.toString().getBytes();
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		JsonObject clone = new JsonObject();
		for (String s : content.keySet()) {
			try {
				clone.add(s, content.get(s));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JsonObject) {
			JsonObject json = (JsonObject) obj;
			if (content.size() != json.content.size()) {
				return false;
			}
			for (String key : content.keySet()) {
				if (!json.contains(key) || !get(key).equals(json.get(key))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}