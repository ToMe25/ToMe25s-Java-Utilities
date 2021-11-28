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
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.tome25.utils.General;
import com.tome25.utils.StringUtils;
import com.tome25.utils.exception.InvalidTypeException;

/**
 * A standard Json list/array, that can store stuff, and be converted to a
 * string.
 * 
 * @author ToMe25
 *
 */
public class JsonArray implements JsonElement<Integer>, List<Object>, Cloneable {

	private static final long serialVersionUID = 5205197497094672807L;
	private List<Object> content;

	/**
	 * Creates a new empty JsonArray.
	 */
	public JsonArray() {
		content = new ArrayList<>();
	}

	/**
	 * Creates a new JsonArray and initializes it with the given content.
	 * 
	 * @param content the content for the new JsonArray.
	 */
	public JsonArray(Object... content) {
		this.content = new ArrayList<>(content.length);
		for (Object obj : content) {
			this.content.add(obj);
		}
	}

	/**
	 * Creates a new JsonArray and initializes it with the given content.
	 * 
	 * If the given {@link List} implements {@link Cloneable} it will be used as the
	 * internal list.<br>
	 * If not a new {@link ArrayList} will be used and the values from content will
	 * be added to it.
	 * 
	 * @param content the content for the new JsonArray.
	 */
	public JsonArray(List<Object> content) {
		if (content instanceof Cloneable) {
			this.content = content;
		} else {
			this.content = new ArrayList<>(content);
		}
	}

	/**
	 * Creates a new JsonArray and initializes it with the given content.<br>
	 * This content will be added to a new {@link ArrayList} used internally.
	 * 
	 * @param content the content for the new JsonArray.
	 */
	public JsonArray(Collection<Object> content) {
		this.content = new ArrayList<>(content);
	}

	@Override
	public Object add(Integer key, Object value) {
		content.add((int) key, value);
		return null;
	}

	/**
	 * Adds the given object to the given index.
	 * 
	 * @param index the index to add the object to.
	 * @param value the value to add.
	 */
	@Override
	public void add(int index, Object value) {
		add((Integer) index, value);
	}

	@Override
	public boolean add(Object value) {
		return content.add(value);
	}

	@Override
	public Object put(Integer key, Object value) {
		return content.set((int) key, value);
	}

	@Override
	public void putAll(Map<? extends Integer, ? extends Object> m) {
		m.forEach((key, value) -> {
			content.set((Integer) key, value);
		});
	}

	@Override
	public Object set(Integer key, Object element) {
		return put(key, element);
	}

	@Override
	public Object remove(int index) {
		return content.remove(index);
	}

	@Override
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
	public Object get(Object key) throws InvalidTypeException {
		if (key instanceof Integer) {
			return content.get((int) key);
		} else {
			throw new InvalidTypeException("Integer", key.getClass().getSimpleName());
		}
	}

	@Override
	public String getString(Integer key) {
		return content.get(key).toString();
	}

	@Override
	public Collection<Object> values() {
		return content;
	}

	/**
	 * Checks whether this Json contains the given object, either as key if the type
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
	public boolean containsKey(Object key) throws InvalidTypeException {
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
			content.forEach(value -> {
				if (value instanceof JsonElement) {
					size[0] += ((JsonElement<?>) value).size(recursive);
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
		StringBuilder ret = new StringBuilder();
		ret.append('[');
		content.forEach(value -> {
			StringUtils.toEscapedString(ret, value);
			ret.append(',');
		});
		if (ret.length() > 1) {
			ret.deleteCharAt(ret.length() - 1);
		}
		ret.append(']');
		return ret.toString();
	}

	@Override
	public JsonArray clone() {
		return clone(true);
	}

	@Override
	public JsonArray clone(boolean recursive) {
		JsonArray clone = null;
		try {
			clone = (JsonArray) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		@SuppressWarnings("unchecked")
		final List<Object> contentClone = (List<Object>) General.reflectiveClone((Cloneable) content);
		if (recursive) {
			for (int i = 0; i < contentClone.size(); i++) {
				Object value = contentClone.get(i);
				if (value instanceof JsonElement && ((JsonElement<?>) value).supportsClone()) {
					try {
						contentClone.set(i, ((JsonElement<?>) value).clone(recursive));
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
					}
				} else if (value instanceof Cloneable) {
					contentClone.set(i, General.reflectiveClone((Cloneable) value));
				}
			}
		}
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
		JsonArray other = (JsonArray) obj;
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
	 * Adds all the given objects to this array.
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
	public boolean supportsChanges() {
		return true;
	}

	@Override
	public JsonArray changes(JsonElement<Integer> from) {
		return changes(from, true);
	}

	@Override
	public JsonArray changes(JsonElement<Integer> from, boolean recursive) {
		JsonArray last = (JsonArray) from;
		JsonArray changes = new JsonArray();
		HashSet<Object> contentCopy;
		contentCopy = new HashSet<>(Math.max(content.size(), last.content.size()));
		contentCopy.addAll(last.content);

		int[] index = new int[] { 0 };
		for (Object value : content) {
			try {
				if (!contentCopy.contains(value)) {
					JsonObject val;
					if (value instanceof JsonElement && ((JsonElement<?>) value).supportsClone()) {
						val = new JsonObject("val", ((JsonElement<?>) value).clone(true), "after", index[0]);
					} else if (value instanceof Cloneable) {
						val = new JsonObject("val", General.reflectiveClone((Cloneable) value), "after", index[0]);
					} else {
						val = new JsonObject("val", value, "after", index[0]);
					}
					changes.content.add(val);
				} else {
					index[0]++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		contentCopy.clear();
		contentCopy.addAll(content);
		index[0] = 0;
		last.content.forEach((value) -> {
			if (!contentCopy.contains(value)) {
				changes.content.add(new JsonObject("rm", index[0]));
			}
			index[0]++;
		});
		return changes;
	}

	@Override
	public JsonArray reconstruct(JsonElement<Integer> from) {
		return reconstruct(from, true);
	}

	@Override
	public JsonArray reconstruct(JsonElement<Integer> from, boolean recursive) {
		JsonArray last = (JsonArray) from;
		JsonArray reconstructed = last.clone();
		int[] offset = new int[] { 0 };
		content.forEach(change -> {
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
		});
		return reconstructed;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int entries = in.readInt();
		for (int i = 0; i < entries; i++) {
			content.add(in.readObject());
		}
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(content.size());
		int size = content.size();
		for (int i = 0; i < size; i++) {
			out.writeObject(content.get(i));
		}
	}

	@Override
	public int compareTo(JsonElement<?> o) {
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
	 * Compares the two given objects if they implement {@link Comparable}, and are
	 * compatible types.<br>
	 * This method can only return 1, 0 or -1.<br>
	 * Returns 0 if the objects can't be compared.
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
				@SuppressWarnings({ "unchecked" })
				int diff = ((Comparable<Comparable<?>>) obj1).compareTo((Comparable<?>) obj2);
				if (diff > 0) {
					return 1;
				} else if (diff < 0) {
					return -1;
				} else {
					return 0;
				}
			} else if (class2.isAssignableFrom(class1)) {
				@SuppressWarnings({ "unchecked" })
				int diff = ((Comparable<Comparable<?>>) obj2).compareTo((Comparable<?>) obj1);
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

	/**
	 * Gets the last object from this list.
	 * 
	 * @return the last object from this list.
	 */
	public Object getLast() {
		return content.get(content.size() - 1);
	}

	/**
	 * Gets the first object from this list.
	 * 
	 * @return the first object from this list.
	 */
	public Object getFirst() {
		return content.get(0);
	}

	@Override
	public Class<Integer> getKeyType() {
		return Integer.class;
	}

}
