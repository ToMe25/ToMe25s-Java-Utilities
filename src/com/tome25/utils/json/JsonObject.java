package com.tome25.utils.json;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
	 * @param key   the key to represent value in the new Json object.
	 * @param value a value to add to the new Json object.
	 */
	public JsonObject(String key, Object value) {
		content.put(key, value);
	}

	/**
	 * Creates a new Json Object and initializes it with the given content.
	 * 
	 * @param content the content for the new Json object.
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
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
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

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m mappings to be stored in this json.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 */
	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
		m.forEach((key, value) -> {
			if (key instanceof String) {
				content.put((String) key, value);
			} else {
				throw new InvalidTypeException("String", key.getClass().getSimpleName());
			}
		});
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
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
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
	 * @param key the key to look for.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
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
	 * Returns this Jsons Keys.
	 * 
	 * @return this Jsons Keys.
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
	 * Returns true if this Json contains the given key.
	 * 
	 * @param key the key to look for.
	 * @return whether this Json contains the given key.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 */
	@Override
	public boolean containsKey(Object key) {
		if (key instanceof String) {
			return content.containsKey(key);
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	/**
	 * Returns true if this Json contains the given value.
	 * 
	 * @param value the value to look for.
	 * @return this Json contains the given value.
	 */
	@Override
	public boolean containsValue(Object value) {
		return content.containsValue(value);
	}

	@Override
	public int size() {
		return size(false);
	}

	@Override
	public int size(boolean recursive) {
		if (recursive) {
			int size = 0;
			for (Object key : content.keySet()) {
				if (content.get(key) instanceof JsonElement) {
					size += ((JsonElement) content.get(key)).size(recursive);
				} else {
					size++;
				}
			}
			return size;
		} else {
			return content.size();
		}
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
		for (String key : content.keySet()) {
			try {
				Object value = content.get(key);
				if (recursive && value instanceof JsonElement && ((JsonElement) value).supportsClone()) {
					clone.add(key, ((JsonElement) value).clone(recursive));
				} else {
					clone.add(key, value);
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
	public JsonObject changes(JsonElement from) {
		return changes(from, true);
	}

	@Override
	public JsonObject changes(JsonElement from, boolean recursive) {
		if (!(from instanceof JsonObject)) {
			if (supportsClone()) {
				return clone(true);
			} else {
				return this;
			}
		}
		JsonObject last = (JsonObject) from;
		JsonObject changes = new JsonObject();
		content.keySet().forEach((key) -> {
			try {
				if (last.containsKey(key)) {
					if (content.get(key) != null && !content.get(key).equals(last.get(key))) {
						if (content.get(key) instanceof JsonElement && last.get(key) instanceof JsonElement) {
							if (recursive && ((JsonElement) content.get(key)).supportsChanges()
									&& ((JsonElement) last.get(key)).supportsChanges()) {
								changes.add(key, ((JsonElement) content.get(key)).changes((JsonElement) last.get(key)));
							} else if (((JsonElement) content.get(key)).supportsClone()) {
								changes.add(key, ((JsonElement) content.get(key)).clone(true));
							} else {
								changes.add(key, content.get(key));
							}
						} else {
							changes.add(key, content.get(key));
						}
					}
				} else if (content.get(key) instanceof JsonElement
						&& ((JsonElement) content.get(key)).supportsClone()) {
					changes.add(key, ((JsonElement) content.get(key)).clone(true));
				} else {
					changes.add(key, content.get(key));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		last.keySet().forEach((key) -> {
			if (!content.containsKey(key)) {
				changes.add(key, null);
			}
		});
		return changes;
	}

	@Override
	public JsonObject reconstruct(JsonElement from) {
		return reconstruct(from, true);
	}

	@Override
	public JsonObject reconstruct(JsonElement from, boolean recursive) {
		if (!(from instanceof JsonObject) || equals(from)) {
			if (supportsClone()) {
				return clone(true);
			} else {
				return this;
			}
		}
		JsonObject last = (JsonObject) from;
		JsonObject reconstructed = new JsonObject();
		content.keySet().forEach((key) -> {
			try {
				if (last.containsKey(key)) {
					if (content.get(key) != null && !content.get(key).equals(last.get(key))) {
						if (content.get(key) instanceof JsonElement && last.get(key) instanceof JsonElement) {
							if (recursive && ((JsonElement) content.get(key)).supportsChanges()
									&& ((JsonElement) last.get(key)).supportsChanges()) {
								reconstructed.add(key,
										((JsonElement) content.get(key)).reconstruct((JsonElement) last.get(key)));
							} else if (((JsonElement) content.get(key)).supportsClone()) {
								reconstructed.add(key, ((JsonElement) content.get(key)).clone(true));
							} else {
								reconstructed.add(key, content.get(key));
							}
						} else {
							reconstructed.add(key, content.get(key));
						}
					} else if (content.get(key) != null) {
						if (content.get(key) instanceof JsonElement
								&& ((JsonElement) content.get(key)).supportsClone()) {
							reconstructed.add(key, ((JsonElement) content.get(key)).clone(true));
						} else {
							reconstructed.add(key, content.get(key));
						}
					}
				} else if (content.get(key) instanceof JsonElement
						&& ((JsonElement) content.get(key)).supportsClone()) {
					reconstructed.add(key, ((JsonElement) content.get(key)).clone(true));
				} else {
					reconstructed.add(key, content.get(key));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		last.keySet().forEach((key) -> {
			if (!content.containsKey(key)) {
				try {
					if (last.get(key) instanceof JsonElement && ((JsonElement) last.get(key)).supportsClone()) {
						reconstructed.add(key, ((JsonElement) last.get(key)).clone(true));
					} else {
						reconstructed.add(key, last.get(key));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return reconstructed;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int entries = in.readInt();
		for (int i = 0; i < entries; i++) {
			String key = in.readUTF();
			content.put(key, in.readObject());
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(content.size());
		for (String key : content.keySet()) {
			out.writeUTF(key);
			out.writeObject(content.get(key));
		}
	}

	@Override
	public int compareTo(JsonElement o) {
		if (this.equals(o)) {
			return 0;
		}
		int[] difference = new int[] { 0 };
		List<Object> diffs1 = new ArrayList<Object>();
		List<Object> diffs2 = new ArrayList<Object>();
		if (o instanceof JsonObject) {
			content.keySet().forEach((key) -> {
				if (o.containsKey(key)) {
					difference[0] += compare(content.get(key), o.get(key));
				} else {
					diffs1.add(content.get(key));
				}
			});
			((JsonObject) o).keySet().forEach((key) -> {
				if (!content.containsKey(key)) {
					diffs2.add(o.get(key));
				}
			});
		} else {
			content.values().forEach((value) -> {
				if (!o.containsValue(value)) {
					diffs1.add(value);
				}
			});
			o.values().forEach((value) -> {
				if (!content.values().contains(value)) {
					diffs2.add(value);
				}
			});
		}
		int i = 0;
		while (i < diffs1.size() || i < diffs2.size()) {
			if (i < diffs1.size() && i < diffs2.size()) {
				difference[0] += compare(diffs1.get(i), diffs2.get(i));
			} else if (i < diffs1.size()) {
				difference[0]++;
			} else {
				difference[0]--;
			}
			i++;
		}
		if (difference[0] == 0) {
			difference[0] = 1;
		}
		return difference[0];
	}

	/**
	 * Compares the two given objects if they implement {@link Comparable}, and are
	 * compatible types. can only return 1, 0 or -1. returns 0 if the objects can't
	 * be compared.
	 * 
	 * @param obj1 the first object to compare.
	 * @param obj2 the second object to compare.
	 * @return the comparison of the two objects.
	 */
	private int compare(Object obj1, Object obj2) {
		if (obj1 instanceof Comparable<?> && obj2 instanceof Comparable<?>) {
			Class<?> class1 = obj1.getClass();
			Class<?> class2 = obj2.getClass();
			if (class1.isAssignableFrom(class2)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				int diff = ((Comparable) obj1).compareTo((Comparable) obj2);
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}
			} else if (class2.isAssignableFrom(class1)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				int diff = ((Comparable) obj2).compareTo((Comparable) obj1);
				if (diff > 0) {
					return -1;
				} else if (diff < 0) {
					return 1;
				} else {
					return 0;
				}
			}
		}
		return 0;
	}

}
