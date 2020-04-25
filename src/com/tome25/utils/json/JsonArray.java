package com.tome25.utils.json;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

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
		if (key instanceof Integer) {
			content.add((int) key, value);
			return null;
		} else {
			throw new InvalidTypeException("Integer", key.getClass().getSimpleName());
		}
	}

	@Override
	public void add(int index, Object element) {
		content.add(index, element);
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
		m.keySet().forEach((key) -> {
			if (key instanceof Integer) {
				content.set((Integer) key, m.get(key));
			} else {
				throw new InvalidTypeException("Integer", key.getClass().getSimpleName());
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
		return size(false);
	}

	@Override
	public int size(boolean recursive) {
		if (recursive) {
			int[] size = new int[] { 0 };
			content.forEach((value) -> {
				if (value instanceof JsonElement) {
					size[0] += ((JsonElement) value).size(recursive);
				} else {
					size[0]++;
				}
			});
			return size[0];
		} else {
			return content.size();
		}
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
	public JsonArray clone() {
		return clone(true);
	}

	@Override
	public JsonArray clone(boolean recursive) {
		JsonArray clone = new JsonArray();
		content.forEach((value) -> {
			try {
				if (recursive && value instanceof JsonElement && ((JsonElement) value).supportsClone()) {
					clone.add(((JsonElement) value).clone(recursive));
				} else {
					clone.add(value);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
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
	public boolean addAll(Collection<? extends Object> c) {
		return content.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends Object> c) {
		return content.addAll(index, c);
	}

	/**
	 * adds all the given objects to this array.
	 * 
	 * @param obj the objects to add.
	 */
	public void addAll(Object... obj) {
		for (Object o : obj) {
			add(o);
		}
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
	public JsonArray changes(JsonElement from) {
		return changes(from, true);
	}

	@Override
	public JsonArray changes(JsonElement from, boolean recursive) {
		if (!(from instanceof JsonArray)) {
			if (supportsClone()) {
				return clone(true);
			} else {
				return this;
			}
		}
		JsonArray last = (JsonArray) from;
		JsonArray changes = new JsonArray();
		int[] index = new int[] { 0 };
		content.forEach((value) -> {
			try {
				if (!last.contains(value)) {
					if (value instanceof JsonElement && ((JsonElement) value).supportsClone()) {
						JsonObject val = new JsonObject("val", (JsonElement) value).clone(true);
						val.put("after", index[0]);
						changes.add(val);
					} else {
						JsonObject val = new JsonObject("val", value);
						val.put("after", index[0]);
						changes.add(val);
					}
				} else {
					index[0]++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		index[0] = 0;
		last.forEach((value) -> {
			if (!content.contains(value)) {
				changes.add(new JsonObject("rm", index[0]));
			}
			index[0]++;
		});
		return changes;
	}

	@Override
	public JsonArray reconstruct(JsonElement from) {
		return reconstruct(from, true);
	}

	@Override
	public JsonArray reconstruct(JsonElement from, boolean recursive) {
		if (!(from instanceof JsonArray) || equals(from)) {
			if (supportsClone()) {
				return clone(true);
			} else {
				return this;
			}
		}
		JsonArray last = (JsonArray) from;
		JsonArray reconstructed = new JsonArray(last);
		int[] offset = new int[] { 0 };
		content.forEach((change) -> {
			try {
				if (change instanceof JsonObject) {
					JsonObject chg = (JsonObject) change;
					if (chg.containsKey("rm")) {
						reconstructed.remove(((int) chg.get("rm")) + offset[0]);
						offset[0]--;
					} else if (chg.containsKey("after") && chg.containsKey("val")) {
						reconstructed.add(((int) chg.get("after")) + offset[0], chg.get("val"));
						offset[0]++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return reconstructed;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int entries = in.readInt();
		while (entries > 0) {
			content.add(in.readObject());
			entries--;
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(content.size());
		int i = 0;
		while (i < content.size()) {
			out.writeObject(content.get(i));
			i++;
		}
	}

	@Override
	public int compareTo(JsonElement o) {
		if (this.equals(o)) {
			return 0;
		}
		List<Object> diffs1 = new ArrayList<Object>();
		List<Object> diffs2 = new ArrayList<Object>();
		int difference = 0;
		content.forEach((value) -> {
			if (!o.containsValue(value)) {
				diffs1.add(value);
			}
		});
		o.values().forEach((value) -> {
			if (!content.contains(value)) {
				diffs2.add(value);
			}
		});
		int i = 0;
		while (i < diffs1.size() || i < diffs2.size()) {
			if (i < diffs1.size() && i < diffs2.size()) {
				difference += compare(diffs1.get(i), diffs2.get(i));
			} else if (i < diffs1.size()) {
				difference++;
			} else {
				difference--;
			}
			i++;
		}
		if (difference == 0) {
			difference = 1;
		}
		return difference;
	}

	/**
	 * compares the two given objects if they implement Comparable, and are
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
