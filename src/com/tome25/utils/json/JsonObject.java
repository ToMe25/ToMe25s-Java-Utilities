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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tome25.utils.exception.InvalidKeyException;
import com.tome25.utils.exception.InvalidTypeException;

/**
 * A standard Json object, that can store stuff, and be converted to a string.
 * 
 * @author ToMe25
 *
 */
public class JsonObject implements JsonElement<String>, Map<String, Object>, Cloneable {

	private static final long serialVersionUID = 8864863917582360165L;
	private Map<String, Object> content = new LinkedHashMap<String, Object>();

	/**
	 * Creates a new empty JsonObject.
	 */
	public JsonObject() {
	}

	/**
	 * Creates a new JsonObject and initializes it with the given key value pair.
	 * 
	 * @param key   the key to represent value in the new JsonObject.
	 * @param value a value to add to the new JsonObject.
	 */
	public JsonObject(String key, Object value) {
		content.put(key, value);
	}

	/**
	 * Creates a new JsonObject and initializes it with the given content.
	 * 
	 * @param content the content for the new JsonObject.
	 */
	public JsonObject(Map<String, Object> content) {
		this.content.putAll(content);
	}

	@Override
	public Object add(String key, Object value) {
		if (content.containsKey(key)) {
			throw new InvalidKeyException(String.valueOf(key), "it exists already!");
		} else {
			return content.put((String) key, value);
		}
	}

	/**
	 * Adds the given value to the given key replacing the current value if it
	 * already exists.
	 * 
	 * @param key   the Key to add.
	 * @param value the value to set for key.
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key.
	 */
	@Override
	public Object put(String key, Object value) {
		return content.put(key, value);
	}

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m mappings to be stored in this Json.
	 */
	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		content.putAll(m);
	}

	@Override
	public Object set(String key, Object element) {
		return put(key, element);
	}

	/**
	 * Removes the mapping for the given key from this map if it is present.
	 * 
	 * @param key the key to remove.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s).
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
	 *                              this object(String for {@link JsonObject}s).
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
	public String getString(String key) {
		return String.valueOf(content.get(key));
	}

	/**
	 * Returns this Jsons keys.
	 * 
	 * @return this Jsons keys.
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
	 *                              this object(String for {@link JsonObject}s).
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
					size += ((JsonElement<?>) content.get(key)).size(recursive);
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
		StringBuilder ret = new StringBuilder();
		ret.append('{');
		for (String s : content.keySet()) {
			ret.append('"');
			ret.append(s);
			ret.append("\":");
			contentToString(content.get(s), ret);
			ret.append(',');
		}
		if (ret.length() > 1) {
			ret.deleteCharAt(ret.length() - 1);
		}
		ret.append('}');
		return ret.toString();
	}

	@Override
	public JsonObject clone() {
		return clone(true);
	}

	@Override
	public JsonObject clone(boolean recursive) {
		JsonObject clone = null;
		try {
			clone = (JsonObject) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> contentClone = (LinkedHashMap<String, Object>) ((LinkedHashMap<String, Object>) content).clone();
		content.forEach((key, value) -> {
			try {
				if (recursive && value instanceof JsonElement && ((JsonElement<?>) value).supportsClone()) {
					contentClone.put(key, ((JsonElement<?>) value).clone(true));
				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		});
		clone.content = contentClone;
		return clone;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		JsonObject other = (JsonObject) obj;
		if (content == null) {
			if (other.content != null) {
				return false;
			}
		} else if (!content.equals(other.content)) {
			return false;
		}
		return true;
	}

	@Override
	public void clear() {
		this.content.clear();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return content.entrySet();
	}

	@Override
	public boolean isEmpty() {
		return content.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return content.keySet();
	}

	@Override
	public Collection<Object> values() {
		return content.values();
	}

	@Override
	public Iterator<Object> iterator() {
		Set<Object> objectSet = new HashSet<Object>();
		objectSet.addAll(keySet());
		return objectSet.iterator();
	}

	@Override
	public boolean supportsChanges() {
		return true;
	}

	@Override
	public JsonObject changes(JsonElement<String> from) {
		return changes(from, true);
	}

	@Override
	public JsonObject changes(JsonElement<String> from, boolean recursive) {
		JsonObject last = (JsonObject) from;
		JsonObject changes = new JsonObject();
		content.keySet().forEach((key) -> {
			try {
				if (last.containsKey(key)) {
					if (content.get(key) != null && !content.get(key).equals(last.get(key))) {
						if (content.get(key) instanceof JsonElement && last.get(key) instanceof JsonElement) {
							JsonElement<?> value = (JsonElement<?>) content.get(key);
							JsonElement<?> lastValue = (JsonElement<?>) last.get(key);
							if (recursive && value.supportsChanges() && lastValue.supportsChanges()
									&& value.getKeyType().equals(lastValue.getKeyType())) {
								changes.add(key, changes(lastValue, value));
							} else if (((JsonElement<?>) content.get(key)).supportsClone()) {
								changes.add(key, ((JsonElement<?>) content.get(key)).clone(true));
							} else {
								changes.add(key, content.get(key));
							}
						} else {
							changes.add(key, content.get(key));
						}
					}
				} else if (content.get(key) instanceof JsonElement
						&& ((JsonElement<?>) content.get(key)).supportsClone()) {
					changes.add(key, ((JsonElement<?>) content.get(key)).clone(true));
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
	public JsonObject reconstruct(JsonElement<String> from) {
		return reconstruct(from, true);
	}

	@Override
	public JsonObject reconstruct(JsonElement<String> from, boolean recursive) {
		JsonObject last = (JsonObject) from;
		JsonObject reconstructed = new JsonObject();
		content.keySet().forEach((key) -> {
			try {
				if (last.containsKey(key)) {
					if (content.get(key) != null && !content.get(key).equals(last.get(key))) {
						if (content.get(key) instanceof JsonElement && last.get(key) instanceof JsonElement) {
							JsonElement<?> value = (JsonElement<?>) content.get(key);
							JsonElement<?> lastValue = (JsonElement<?>) last.get(key);
							if (recursive && value.supportsChanges() && lastValue.supportsChanges()
									&& value.getKeyType().equals(lastValue.getKeyType())) {
								reconstructed.add(key, reconstruct(lastValue, value));
							} else if (((JsonElement<?>) content.get(key)).supportsClone()) {
								reconstructed.add(key, ((JsonElement<?>) content.get(key)).clone(true));
							} else {
								reconstructed.add(key, content.get(key));
							}
						} else {
							reconstructed.add(key, content.get(key));
						}
					} else if (content.get(key) != null) {
						if (content.get(key) instanceof JsonElement
								&& ((JsonElement<?>) content.get(key)).supportsClone()) {
							reconstructed.add(key, ((JsonElement<?>) content.get(key)).clone(true));
						} else {
							reconstructed.add(key, content.get(key));
						}
					}
				} else if (content.get(key) instanceof JsonElement
						&& ((JsonElement<?>) content.get(key)).supportsClone()) {
					reconstructed.add(key, ((JsonElement<?>) content.get(key)).clone(true));
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
					if (last.get(key) instanceof JsonElement && ((JsonElement<?>) last.get(key)).supportsClone()) {
						reconstructed.add(key, ((JsonElement<?>) last.get(key)).clone(true));
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
	public int compareTo(JsonElement<?> o) {
		if (this.equals(o)) {
			return 0;
		}
		int[] difference = new int[] { 0 };
		List<Object> diffs1 = new ArrayList<Object>();
		List<Object> diffs2 = new ArrayList<Object>();
		if (o instanceof JsonObject) {
			JsonObject jo = (JsonObject) o;
			content.keySet().forEach((key) -> {
				if (jo.containsKey(key)) {
					difference[0] += compare(content.get(key), jo.get(key));
				} else {
					diffs1.add(content.get(key));
				}
			});
			jo.keySet().forEach((key) -> {
				if (!content.containsKey(key)) {
					diffs2.add(jo.get(key));
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
				int diff = ((Comparable<Comparable>) obj1).compareTo((Comparable<?>) obj2);
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}
			} else if (class2.isAssignableFrom(class1)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				int diff = ((Comparable<Comparable>) obj2).compareTo((Comparable<?>) obj1);
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

	@Override
	public Class<String> getKeyType() {
		return String.class;
	}

}
