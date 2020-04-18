package com.tome25.utils.json;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import com.tome25.utils.exception.InvalidKeyException;
import com.tome25.utils.exception.InvalidTypeException;

/**
 * A standard json list/array, that can store stuff, and be converted to a
 * string.
 * 
 * @author ToMe25
 *
 */
public class JsonArray implements JsonElement, List<Object> {

	private static final long serialVersionUID = 5205197497094672807L;
	private List<Object> content = new ArrayList<Object>();

	/**
	 * creates a new empty Json Array.
	 */
	public JsonArray() {
	}

	/**
	 * creates a new Json Array and initializes it with the given content.
	 * 
	 * @param content the content for the new json list/array.
	 */
	public JsonArray(Object... content) {
		for (Object obj : content) {
			this.content.add(obj);
		}
	}

	public JsonArray(Collection<Object> content) {
		this.content.addAll(content);
	}

	@Override
	public Object add(Object key, Object value) {
		throw new InvalidKeyException(
				"Json Arrays can't store key values pairs, please use add(value) or put(index, value) instead.");
	}

	@Override
	public boolean add(Object value) {
		return content.add(value);
	}

	@Override
	public Object put(Object key, Object value) {
		if (key instanceof Integer) {
			return content.set((int) key, value);
		} else {
			throw new InvalidTypeException("Integer", key.getClass().getSimpleName());
		}
	}

	@Override
	public void putAll(Map<? extends Object, ? extends Object> m) {
		for (Entry<? extends Object, ? extends Object> entry : m.entrySet()) {
			if (entry.getKey() instanceof Integer) {
				content.set((Integer) entry.getKey(), entry.getValue());
			} else {
				throw new InvalidTypeException("Integer", entry.getKey().getClass().getSimpleName());
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

	@Override
	public Object remove(int index) {
		return content.remove(index);
	}

	public boolean remove(Object value) {
		return content.remove(value);
	}

	@Override
	public void remove(Object o, boolean key) {
		if (key) {
			remove((int) o);
		} else {
			remove(o);
		}
	}

	@Override
	public Object get(int index) {
		return content.get(index);
	}

	@Override
	public Object get(Object key) {
		if (key instanceof Integer) {
			return content.get((int) key);
		} else {
			throw new InvalidTypeException("Integer", key.getClass().getSimpleName());
		}
	}

	@Override
	public String getString(Object key) {
		if (key instanceof Integer) {
			return content.get((int) key).toString();
		} else {
			throw new InvalidTypeException("String", key.getClass().getSimpleName());
		}
	}

	@Override
	public Collection<Object> values() {
		return content;
	}

	/**
	 * checks whether this Json contains the given object, either as key if the type
	 * matches, or as value.
	 * 
	 * @param o the object to look for
	 * @return whether this Json contains the given object.
	 */
	@Override
	public boolean contains(Object o) {
		if (o instanceof Integer) {
			return containsKey(o) || containsValue(o);
		} else {
			return containsValue(o);
		}
	}

	@Override
	public boolean containsKey(Object key) {
		if (key instanceof Integer) {
			return size() > (int) key;
		} else {
			throw new InvalidTypeException("Integer", key.getClass().getSimpleName());
		}
	}

	@Override
	public boolean containsValue(Object value) {
		return content.contains(value);
	}

	@Override
	public int size() {
		return content.size();
	}

	@Override
	public String toString() {
		String ret = "[";
		for (Object obj : content) {
			ret += contentToString(obj);
			ret += ",";
		}
		if (ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		ret += "]";
		return ret;
	}

	@Override
	public Object clone() {
		return clone(true);
	}

	@Override
	public JsonArray clone(boolean recursive) {
		JsonArray clone = new JsonArray();
		for (Object obj : content) {
			try {
				if (recursive && obj instanceof JsonElement && ((JsonElement) obj).supportsClone()) {
					clone.add(((JsonElement) obj).clone(recursive));
				} else {
					clone.add(obj);
				}
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
			if (content.size() != json.size()) {
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
	public boolean isEmpty() {
		return content.isEmpty();
	}

	@Override
	public void clear() {
		content.clear();
	}

	@Override
	public Iterator<Object> iterator() {
		return content.iterator();
	}

	@Override
	public void add(int index, Object element) {
		content.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends Object> c) {
		return content.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		return content.addAll(index, c);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return content.containsAll(c);
	}

	@Override
	public int indexOf(Object o) {
		return content.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return content.lastIndexOf(o);
	}

	@Override
	public ListIterator<Object> listIterator() {
		return content.listIterator();
	}

	@Override
	public ListIterator<Object> listIterator(int index) {
		return content.listIterator(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return content.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return content.retainAll(c);
	}

	@Override
	public Object set(int index, Object element) {
		return put(index, element);
	}

	@Override
	public List<Object> subList(int fromIndex, int toIndex) {
		return new JsonArray(content.subList(fromIndex, toIndex));
	}

	@Override
	public Object[] toArray() {
		return content.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return content.toArray(a);
	}

	@Override
	public boolean supportsChanges() {
		return false;
	}

	@Override
	public JsonElement changes(JsonElement from, boolean recursive) throws UnsupportedOperationException {
		throw new UnsupportedOperationException(
				"Json Arrays don't support this operation as it relies on the key-value-pair structure.");
	}

	@Override
	public JsonElement reconstruct(JsonElement from, boolean recursive) {
		throw new UnsupportedOperationException(
				"Json Arrays don't support this operation as it relies on the key-value-pair structure.");
	}

}
