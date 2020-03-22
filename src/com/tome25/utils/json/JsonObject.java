package com.tome25.utils.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.tome25.utils.exception.InvalidKeyException;
import com.tome25.utils.exception.InvalidTypeException;

public class JsonObject implements JsonElement, Map<Object, Object> {

	private static final long serialVersionUID = 8864863917582360165L;
	private Map<String, Object> content = new HashMap<String, Object>();

	/**
	 * creates a new empty Json Object.
	 */
	public JsonObject() {
	}

	/**
	 * creates a new Json Object and initializes it with the given key value pair.
	 * 
	 * @param key
	 * @param value
	 */
	public JsonObject(String key, Object value) {
		content.put(key, value);
	}

	/**
	 * creates a new Json Object and initializes it with the given content.
	 * 
	 * @param content
	 */
	public JsonObject(Map<String, Object> content) {
		this.content.putAll(content);
	}

	@Override
	public Object add(Object key, Object value) {
		if (key instanceof String) {
			if (content.containsKey(key)) {
				throw new InvalidKeyException("Key \"" + key + "\" Allready exists!");
			} else {
				return content.put((String) key, value);
			}
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	/**
	 * Adds the given value to the given key replacing the current value if it
	 * already exists.
	 * 
	 * @param key   the Key to add.
	 * @param value the value to set for key.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	@Override
	public Object put(Object key, Object value) {
		if (key instanceof String) {
			return content.put((String) key, value);
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
		for (Entry<? extends Object, ? extends Object> entry : m.entrySet()) {
			if (entry.getKey() instanceof String) {
				content.put((String) entry.getKey(), entry.getValue());
			} else {
				throw new InvalidTypeException("String", entry.getKey().getClass().getSimpleName());
			}
		}
	}

	@Override
	public Object set(Object key, Object element) {
		return put(key, element);
	}

	@Override
	public void setAll(Map<? extends Object, ? extends Object> m) {
		putAll(m);
	}

	/**
	 * Removes the mapping for the given key from this map if it is present.
	 * 
	 * @param key
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	@Override
	public Object remove(Object key) {
		if (key instanceof String) {
			return content.remove(key);
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	/**
	 * gets the value for the given key.
	 * 
	 * @param key
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			return content.get(key);
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	/**
	 * returns this Json object's Keys.
	 * 
	 * @return
	 */
	public Set<String> getKeySet() {
		return content.keySet();
	}

	@Override
	public Collection<Object> getValues() {
		return content.values();
	}

	@Override
	public boolean contains(Object o) {
		if (o instanceof String) {
			return containsKey(o) || containsValue(o);
		} else {
			return containsValue(o);
		}
	}

	/**
	 * returns true if this Json contains the given key.
	 * 
	 * @param key
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return content.containsKey(key);
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	@Override
	public boolean containsValue(Object value) {
		return content.containsValue(value);
	}

	@Override
	public int size() {
		return content.size();
	}

	@Override
	public String toString() {
		String ret = "{";
		for (String s : content.keySet()) {
			ret += "\"";
			ret += s;
			ret += "\":";
			Object obj = content.get(s);
			if (obj instanceof Boolean || obj instanceof Integer || obj instanceof Short || obj instanceof Byte
					|| obj instanceof Double || obj instanceof Float) {
				ret += obj;
				ret += ",";
			} else if (obj instanceof JsonElement) {
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
		ret += "}";
		return ret;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return clone(true);
	}

	@Override
	public JsonObject clone(boolean recursive) {
		JsonObject clone = new JsonObject();
		for (String s : content.keySet()) {
			try {
				if (content.get(s) instanceof JsonElement) {
					if (recursive) {
						clone.add(s, ((JsonElement) content.get(s)).clone(recursive));
					} else {
						clone.add(s, content.get(s));
					}
				} else {
					clone.add(s, content.get(s));
				}
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

	@Override
	public void clear() {
		this.content.clear();
	}

	@Override
	public Set<Entry<Object, Object>> entrySet() {
		Map<Object, Object> converter = new HashMap<Object, Object>();
		converter.putAll(content);
		return converter.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return content.isEmpty();
	}

	@Override
	public Set<Object> keySet() {
		Set<Object> converter = new HashSet<Object>();
		converter.addAll(content.keySet());
		return converter;
	}

	@Override
	public Collection<Object> values() {
		return getValues();
	}

	@Override
	public Iterator<Object> iterator() {
		return keySet().iterator();
	}

}