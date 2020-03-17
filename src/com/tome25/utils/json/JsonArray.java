package com.tome25.utils.json;

import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JsonArray extends JsonObject implements Iterable<Object> {

	private List<Object> content;

	/**
	 * creates a new empty Json Array.
	 */
	public JsonArray() {
		content = new ArrayList<Object>();
	}

	/**
	 * creates a new Json Array and initializes it with the given content.
	 * 
	 * @param content
	 */
	public JsonArray(Object... content) {
		this();
		for (Object obj : content) {
			this.content.add(obj);
		}
	}

	@Override
	public void add(String key, Object value) throws InvalidKeyException {
		throw new RuntimeException(
				"Json Arrays can't store key values pairs, please use add(value) or put(index, value) instead.");
	}

	/**
	 * adds the given value to this Json Array.
	 * 
	 * @param value
	 */
	public void add(Object value) {
		content.add(value);
	}

	@Override
	public void put(String key, Object value) {
		throw new RuntimeException(
				"Json Arrays can't store key values pairs, please use add(value) or put(index, value) instead.");
	}

	/**
	 * adds the given value to this Json Array at the given index.
	 * 
	 * @param value
	 * @param index
	 */
	public void put(int index, Object value) {
		content.set(index, value);
	}

	@Override
	public void remove(String key) {
		remove(key);
	}

	/**
	 * removes the given value from this Json Array.
	 * 
	 * @param value
	 */
	public void remove(Object value) {
		content.remove(value);
	}

	/**
	 * removes the object at the given index from this Json Array.
	 * 
	 * @param index
	 */
	public void remove(int index) {
		content.remove(index);
	}

	@Override
	public Object get(String key) {
		throw new RuntimeException("Json Arrays can't store key values pairs, please use get(index) instead.");
	}

	/**
	 * gets the object at the given index.
	 * 
	 * @param index
	 */
	public Object get(int index) {
		return content.get(index);
	}

	@Override
	public Set<String> getKeySet() {
		throw new RuntimeException("Json Arrays can't store key values pairs, so they don't have a keySet.");
	}

	@Override
	public Collection<Object> getValues() {
		return content;
	}

	/**
	 * returns whether this Json Array contains the given value.
	 * 
	 * @param value
	 * @return
	 */
	public boolean contains(Object value) {
		return content.contains(value);
	}

	@Override
	public boolean contains(String key) {
		return contains(key);
	}

	@Override
	public boolean containsKey(String key) {
		throw new RuntimeException("Json Arrays can't store key values pairs, so they don't have a keys.");
	}

	@Override
	public boolean containsValue(Object value) {
		return contains(value);
	}

	/**
	 * 
	 * @return the size of this Object.
	 */
	@Override
	public int size() {
		return content.size();
	}

	/**
	 * Returns a String representation of this array.
	 */
	@Override
	public String toString() {
		String ret = "[";
		for (Object obj : content) {
			if (obj instanceof Boolean || obj instanceof Integer) {
				ret += obj;
				ret += ",";
			} else if (obj instanceof JsonObject) {
				ret += obj.toString();
				ret += ",";
			} else {
				ret += "\"";
				ret += obj.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
				ret += "\",";
			}
		}
		if (ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		ret += "]";
		return ret;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		JsonArray clone = new JsonArray();
		for (Object obj : content) {
			try {
				clone.add(obj);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JsonArray) {
			JsonArray json = (JsonArray) obj;
			if (content.size() != json.content.size()) {
				return false;
			}
			for (Object value : content) {
				if (!json.contains(value)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public Iterator<Object> iterator() {
		return content.iterator();
	}

}
