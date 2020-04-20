package com.tome25.utils.json;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.tome25.utils.exception.InvalidKeyException;
import com.tome25.utils.exception.InvalidTypeException;

/**
 * A standard json object, that can store stuff, and be converted to a string.
 * 
 * @author ToMe25
 *
 */
public class JsonObject implements JsonElement, Map<Object, Object> {

	private static final long serialVersionUID = 8864863917582360165L;
	private Map<String, Object> content = new HashMap<String, Object>();

	/**
	 * Creates a new empty Json Object.
	 */
	public JsonObject() {
	}

	/**
	 * Creates a new Json Object and initializes it with the given key value pair.
	 * 
	 * @param key   the key to represent value in the new json object.
	 * @param value a value to add to the new json object.
	 */
	public JsonObject(String key, Object value) {
		content.put(key, value);
	}

	/**
	 * Creates a new Json Object and initializes it with the given content.
	 * 
	 * @param content the content for the new json object.
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
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key.
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
	 * @param key the key to remove.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key.
	 */
	@Override
	public Object remove(Object key) {
		if (key instanceof String) {
			return content.remove(key);
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	@Override
	public void remove(Object o, boolean key) {
		if (key) {
			remove(o);
		} else {
			content.values().remove(o);
		}
	}

	/**
	 * Gets the value for the given key.
	 * 
	 * @param key the key to get the value for.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return the value for the given key.
	 */
	@Override
	public Object get(Object key) {
		if (key instanceof String) {
			return content.get(key);
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	@Override
	public String getString(Object key) {
		if (key instanceof String) {
			return content.get(key).toString();
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	/**
	 * returns this jsons Keys.
	 * 
	 * @return this jsons Keys.
	 */
	public Set<String> getKeySet() {
		return content.keySet();
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
	 * @param key the key to look for.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return whether this Json contains the given key.
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
			ret += contentToString(content.get(s));
			ret += ",";
		}
		if (ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		ret += "}";
		return ret;
	}

	@Override
	public JsonObject clone() {
		return clone(true);
	}

	@Override
	public JsonObject clone(boolean recursive) {
		JsonObject clone = new JsonObject();
		for (String s : content.keySet()) {
			try {
				if (recursive && content.get(s) instanceof JsonElement
						&& ((JsonElement) content.get(s)).supportsClone()) {
					clone.add(s, ((JsonElement) content.get(s)).clone(recursive));
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
			if (content.size() != json.size()) {
				return false;
			}
			for (String key : content.keySet()) {
				if (!json.contains(key)) {
					return false;
				} else if (get(key) == null ^ json.get(key) == null) {
					return false;
				} else if (get(key) == json.get(key)) {
					continue;
				} else if (!get(key).equals(json.get(key))) {
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
		return content.values();
	}

	@Override
	public Iterator<Object> iterator() {
		return keySet().iterator();
	}

	@Override
	public JsonObject changes(JsonElement from) throws UnsupportedOperationException {
		return changes(from, true);
	}

	@Override
	public JsonObject changes(JsonElement from, boolean recursive) {
		JsonObject last;
		if (from instanceof JsonObject) {
			last = (JsonObject) from;
		} else {
			return clone(true);
		}
		JsonObject changes = new JsonObject();
		for (String s : content.keySet()) {
			try {
				if (last.containsKey(s)) {
					if (content.get(s) != null && !content.get(s).equals(last.get(s))) {
						if (content.get(s) instanceof JsonElement && last.get(s) instanceof JsonElement) {
							if (recursive && ((JsonElement) content.get(s)).supportsChanges()
									&& ((JsonElement) last.get(s)).supportsChanges()) {
								changes.add(s, ((JsonElement) content.get(s)).changes((JsonElement) last.get(s)));
							} else if (((JsonElement) content.get(s)).supportsClone()) {
								changes.add(s, ((JsonElement) content.get(s)).clone(true));
							} else {
								changes.add(s, content.get(s));
							}
						} else {
							changes.add(s, content.get(s));
						}
					}
				} else if (content.get(s) instanceof JsonElement && ((JsonElement) content.get(s)).supportsClone()) {
					changes.add(s, ((JsonElement) content.get(s)).clone(true));
				} else {
					changes.add(s, content.get(s));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Object s : last.keySet()) {
			if (!content.containsKey(s)) {
				changes.add(s, null);
			}
		}
		return changes;
	}

	@Override
	public JsonObject reconstruct(JsonElement from) throws UnsupportedOperationException {
		return reconstruct(from, true);
	}

	@Override
	public JsonObject reconstruct(JsonElement from, boolean recursive) {
		JsonObject last;
		if (from instanceof JsonObject) {
			last = (JsonObject) from;
		} else {
			return clone(true);
		}
		JsonObject reconstructed = new JsonObject();
		for (String s : content.keySet()) {
			try {
				if (last.containsKey(s)) {
					if (content.get(s) != null && !content.get(s).equals(last.get(s))) {
						if (content.get(s) instanceof JsonElement && last.get(s) instanceof JsonElement) {
							if (recursive && ((JsonElement) content.get(s)).supportsChanges()
									&& ((JsonElement) last.get(s)).supportsChanges()) {
								reconstructed.add(s,
										((JsonElement) content.get(s)).reconstruct((JsonElement) last.get(s)));
							} else if (((JsonElement) content.get(s)).supportsClone()) {
								reconstructed.add(s, ((JsonElement) content.get(s)).clone(true));
							} else {
								reconstructed.add(s, content.get(s));
							}
						} else {
							reconstructed.add(s, content.get(s));
						}
					}
				} else if (content.get(s) instanceof JsonElement && ((JsonElement) content.get(s)).supportsClone()) {
					reconstructed.add(s, ((JsonElement) content.get(s)).clone(true));
				} else {
					reconstructed.add(s, content.get(s));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		for (Object s : last.getKeySet()) {
			if (!content.containsKey(s)) {
				try {
					if (last.get(s) instanceof JsonElement && ((JsonElement) content.get(s)).supportsClone()) {
						reconstructed.add(s, ((JsonElement) last.get(s)).clone(true));
					} else {
						reconstructed.add(s, last.get(s));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return reconstructed;
	}

}
