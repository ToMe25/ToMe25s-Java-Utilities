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

import java.io.Externalizable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;

import com.tome25.utils.exception.InvalidKeyException;
import com.tome25.utils.exception.InvalidTypeException;

/**
 * The interface all the Json object types implement.
 * 
 * @author ToMe25
 *
 * @param <K> the key type for this kind of json.
 */
public interface JsonElement<K> extends Iterable<Object>, Externalizable, Comparable<JsonElement<?>> {

	/**
	 * Adds the value with the given key if there is no object with this key.
	 * 
	 * @param key   the key to add.
	 * @param value the value to add for key.
	 * @throws InvalidKeyException when there already an object with this key.
	 * @return depends on the implementation.
	 */
	public Object add(K key, Object value) throws InvalidKeyException;

	/**
	 * Adds the given value to the given key replacing the current value if it
	 * already exists.
	 * 
	 * @param key   the key to add.
	 * @param value the value to set for key.
	 * @return the previous value associated with key, or null if there was no
	 *         mapping for key.
	 */
	public Object put(K key, Object value);

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m mappings to be stored in this Json.
	 */
	public void putAll(Map<? extends K, ? extends Object> m);

	/**
	 * Adds the given value to the given key replacing the current value if it
	 * already exists.
	 * 
	 * @param key   the Key to add.
	 * @param value the value to set for key.
	 * @return the previous value associated with key.
	 */
	public Object set(K key, Object value);

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m mappings to be stored in this Json.
	 */
	public default void setAll(Map<? extends K, ? extends Object> m) {
		putAll(m);
	}

	/**
	 * If key is true this removes the object to the key o from this Json, if not it
	 * removes the value o from this Json.
	 * 
	 * @param o   the object to remove.
	 * @param key whether the object is key or value.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 */
	public void remove(Object o, boolean key) throws InvalidTypeException;

	/**
	 * Gets the value for the given key.
	 * 
	 * @param key the key to look for.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 * @return the value for the given key.
	 */
	public Object get(Object key) throws InvalidTypeException;

	/**
	 * Gets the value for the given key if its a string, or a string representation
	 * of that value if it isn't.
	 * 
	 * @param key the key to look for.
	 * @return the value for the given key if its a string, or a string
	 *         representation of that value if it isn't.
	 */
	public String getString(K key);

	/**
	 * Gets this Jsons values.
	 * 
	 * @return this Jsons values.
	 */
	public default Collection<Object> getValues() {
		return values();
	}

	/**
	 * Gets this Jsons values.
	 * 
	 * @return this Jsons values.
	 */
	public Collection<Object> values();

	/**
	 * Checks whether this Json contains the given object, either as key if the type
	 * matches, or as value.
	 * 
	 * @param o the object to look for.
	 * @return whether this Json contains the given object.
	 */
	public boolean contains(Object o);

	/**
	 * Returns true if this Json contains the given key.
	 * 
	 * @param key the key to look for.
	 * @return whether this Json contains the given key.
	 */
	public boolean containsKey(Object key);

	/**
	 * Returns true if this Json contains the given value.
	 * 
	 * @param value the value to look for.
	 * @return this Json contains the given value.
	 */
	public boolean containsValue(Object value);

	/**
	 * Gets the size of this object. Not recursive.
	 * 
	 * @return the size of this object.
	 */
	public default int size() {
		return size(false);
	}

	/**
	 * Gets the size of this object. If recursive is true this counts all sub
	 * JsonElements as their size instead of one.
	 * 
	 * @param recursive whether to recursively count the size.
	 * @return the size of this object.
	 */
	public int size(boolean recursive);

	/**
	 * Returns a string representation of this element.
	 * 
	 * @return a string representation of this element.
	 */
	public String toString();

	/**
	 * Returns a string representation of this element as byte array.
	 * 
	 * @return a String representation of this element as byte array.
	 */
	public default byte[] toByteArray() {
		return toString().getBytes();
	}

	/**
	 * Returns a string representation of this element as byte array.
	 * 
	 * @param charset the name of the {@link Charset} to use to convert the string
	 *                to a byte array.
	 * @return a String representation of this element as byte array.
	 * @throws UnsupportedEncodingException If the named {@link Charset} is not
	 *                                      supported.
	 */
	public default byte[] toByteArray(String charset) throws UnsupportedEncodingException {
		return toString().getBytes(charset);
	}

	/**
	 * Returns a string representation of this element as byte array.
	 * 
	 * @param charset the {@link Charset} to use to convert the string to a byte
	 *                array.
	 * @return a String representation of this element as byte array.
	 * @throws UnsupportedEncodingException If the named {@link Charset} is not
	 *                                      supported.
	 */
	public default byte[] toByteArray(Charset charset) throws UnsupportedEncodingException {
		return toString().getBytes(charset);
	}

	/**
	 * Returns a string representation of this element as character array.
	 * 
	 * @return a string representation of this element as character array.
	 */
	public default char[] toCharArray() {
		return toString().toCharArray();
	}

	/**
	 * Returns a string representation of this element as byte array.
	 * 
	 * @return a String representation of this element as byte array.
	 */
	public default byte[] getBytes() {
		return toByteArray();
	}

	/**
	 * Returns a string representation of this element as byte array.
	 * 
	 * @param charset the name of the {@link Charset} to use to convert the string
	 *                to a byte array.
	 * @return a String representation of this element as byte array.
	 * @throws UnsupportedEncodingException If the named {@link Charset} is not
	 *                                      supported.
	 */
	public default byte[] getBytes(String charset) throws UnsupportedEncodingException {
		return toByteArray(charset);
	}

	/**
	 * Returns a string representation of this element as byte array.
	 * 
	 * @param charset the {@link Charset} to use to convert the string to a byte
	 *                array.
	 * @return a String representation of this element as byte array.
	 * @throws UnsupportedEncodingException If the named {@link Charset} is not
	 *                                      supported.
	 */
	public default byte[] getBytes(Charset charset) throws UnsupportedEncodingException {
		return toByteArray(charset);
	}

	/**
	 * Checks whether this JsonElement can be cloned.
	 * 
	 * @return whether this JsonElement can be cloned.
	 */
	public default boolean supportsClone() {
		return this instanceof Cloneable;
	}

	/**
	 * Creates and returns a copy of this Json. Recursively.
	 * 
	 * @return a copy of this Json object.
	 * @throws CloneNotSupportedException if this element can't be cloned.
	 */
	public JsonElement<K> clone() throws CloneNotSupportedException;

	/**
	 * Creates and returns a copy of this JsonElement.
	 * 
	 * @param recursive whether objects that can be cloned inside this Json should
	 *                  get cloned as well.
	 * @return a copy of this JsonElement.
	 * @throws CloneNotSupportedException if this element can't be cloned
	 */
	public JsonElement<K> clone(boolean recursive) throws CloneNotSupportedException;

	/**
	 * Returns a hash code value for the object. This method is supported for the
	 * benefit of hash tables such as those provided by {@link java.util.HashMap}.
	 * <p>
	 * The general contract of {@code hashCode} is:
	 * <ul>
	 * <li>Whenever it is invoked on the same object more than once during an
	 * execution of a Java application, the {@code hashCode} method must
	 * consistently return the same integer, provided no information used in
	 * {@code equals} comparisons on the object is modified. This integer need not
	 * remain consistent from one execution of an application to another execution
	 * of the same application.
	 * <li>If two objects are equal according to the {@code equals(Object)} method,
	 * then calling the {@code hashCode} method on each of the two objects must
	 * produce the same integer result.
	 * <li>It is <em>not</em> required that if two objects are unequal according to
	 * the {@link java.lang.Object#equals(java.lang.Object)} method, then calling
	 * the {@code hashCode} method on each of the two objects must produce distinct
	 * integer results. However, the programmer should be aware that producing
	 * distinct integer results for unequal objects may improve the performance of
	 * hash tables.
	 * </ul>
	 *
	 * @return a hash code value for this object.
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @see java.lang.System#identityHashCode
	 */
	public int hashCode();

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * <p>
	 * The {@code equals} method implements an equivalence relation on non-null
	 * object references:
	 * <ul>
	 * <li>It is <i>reflexive</i>: for any non-null reference value {@code x},
	 * {@code x.equals(x)} should return {@code true}.
	 * <li>It is <i>symmetric</i>: for any non-null reference values {@code x} and
	 * {@code y}, {@code x.equals(y)} should return {@code true} if and only if
	 * {@code y.equals(x)} returns {@code true}.
	 * <li>It is <i>transitive</i>: for any non-null reference values {@code x},
	 * {@code y}, and {@code z}, if {@code x.equals(y)} returns {@code true} and
	 * {@code y.equals(z)} returns {@code true}, then {@code x.equals(z)} should
	 * return {@code true}.
	 * <li>It is <i>consistent</i>: for any non-null reference values {@code x} and
	 * {@code y}, multiple invocations of {@code x.equals(y)} consistently return
	 * {@code true} or consistently return {@code false}, provided no information
	 * used in {@code equals} comparisons on the objects is modified.
	 * <li>For any non-null reference value {@code x}, {@code x.equals(null)} should
	 * return {@code false}.
	 * </ul>
	 * <p>
	 * Note that it is generally necessary to override the {@code hashCode} method
	 * whenever this method is overridden, so as to maintain the general contract
	 * for the {@code hashCode} method, which states that equal objects must have
	 * equal hash codes.
	 *
	 * @param obj the reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj argument;
	 *         {@code false} otherwise.
	 * @see #hashCode()
	 * @see java.util.HashMap
	 */
	public boolean equals(Object obj);

	/**
	 * Checks whether this Json is empty.
	 * 
	 * @return whether this Json is empty.
	 */
	public boolean isEmpty();

	/**
	 * Clears this Json.
	 */
	public void clear();

	/**
	 * Checks whether this element supports getting the changes against a given
	 * version.
	 * 
	 * @return whether this element supports getting the changes against a given
	 *         version.
	 */
	public default boolean supportsChanges() {
		return false;
	}

	/**
	 * Returns a new JsonElement containing the changes from the given JsonElement
	 * to this one. Recursive. This marks removed values by setting them to null, so
	 * it will not fully work with JsonElements containing null objects.
	 * 
	 * @param from the previous JsonElement.
	 * @return a new JsonElement containing the changes from the given JsonElement
	 *         to this one.
	 * @throws UnsupportedOperationException if this JsonElement doesn't support
	 *                                       generating a changes JsonElement.
	 */
	public default JsonElement<K> changes(JsonElement<K> from) throws UnsupportedOperationException {
		return changes(from, true);
	}

	/**
	 * Returns a new JsonElement containing the changes from the given JsonElement
	 * to this one. This marks removed values by setting them to null, so it will
	 * not fully work with JsonElements containing null objects.
	 * 
	 * @param from      the previous JsonElement.
	 * @param recursive whether changed JsonElements inside this one should get
	 *                  checked for changes too, or just cloned from this one.
	 * @return a new JsonElement containing the changes from the given JsonElement
	 *         to this one.
	 * @throws UnsupportedOperationException if this JsonElement doesn't support
	 *                                       generating a changes JsonElement.
	 */
	public default JsonElement<K> changes(JsonElement<K> from, boolean recursive) throws UnsupportedOperationException {
		throw new UnsupportedOperationException("This object does not support creating a changes json from it!");
	}

	/**
	 * A utility method to call to.{@link #changes(JsonElement) changes}(from). This
	 * requires from to have the same {@link #getKeyType() key type} as to.
	 * 
	 * @param <T>  the key type of the json elements.
	 * @param from the json from before the changes.
	 * @param to   the json with the changes applied.
	 * @return a new JsonElement containing the changes from the given JsonElement
	 *         to this one.
	 * @throws InvalidTypeException if the key types of from and to don't match.
	 */
	@SuppressWarnings("unchecked")
	default <T> JsonElement<T> changes(JsonElement<?> from, JsonElement<T> to) throws InvalidTypeException {
		if (!to.getKeyType().equals(from.getKeyType())) {
			throw new InvalidTypeException(to.getKeyType().getName(), to.getKeyType().getName());
		}
		return to.changes((JsonElement<T>) from);
	}

	/**
	 * Returns a new JsonElement containing the values of the given JsonElement with
	 * the changes contained in this JsonElement applied. Recursive. This marks
	 * removed values by setting them to null, so it will not fully work with
	 * JsonElements containing null objects.
	 * 
	 * @param from the previous JsonElement.
	 * @return a new JsonElement containing the values of the given JsonElement with
	 *         the changes contained in this JsonElement applied.
	 * @throws UnsupportedOperationException if this JsonElement doesn't support
	 *                                       generating a changes JsonElement.
	 */
	public default JsonElement<K> reconstruct(JsonElement<K> from) throws UnsupportedOperationException {
		return reconstruct(from, true);
	}

	/**
	 * Returns a new JsonElement containing the values of the given JsonElement with
	 * the changes contained in this JsonElement applied. This marks removed values
	 * by setting them to null, so it will not fully work with JsonElements
	 * containing null objects.
	 * 
	 * @param from      the previous JsonElement.
	 * @param recursive whether changed JsonElements inside this one should get
	 *                  reconstructed too, or just cloned from this one.
	 * @return a new JsonElement containing the values of the given JsonElement with
	 *         the changes contained in this JsonElement applied.
	 * @throws UnsupportedOperationException if this JsonElement doesn't support
	 *                                       generating a changes JsonElement.
	 */
	default public JsonElement<K> reconstruct(JsonElement<K> from, boolean recursive) {
		throw new UnsupportedOperationException("This object does not support creating a changes json from it!");
	}

	/**
	 * A utility method to call to.{@link #reconstruct(JsonElement)
	 * reconstruct}(from). This requires from to have the same {@link #getKeyType()
	 * key type} as changes.
	 * 
	 * @param <T>     the key type of the json elements.
	 * @param from    the json from before the changes.
	 * @param changes the json with the changes to apply.
	 * @return a new JsonElement containing the changes from the given JsonElement
	 *         to this one.
	 * @throws InvalidTypeException if the key types of from and to don't match.
	 */
	@SuppressWarnings("unchecked")
	default <T> JsonElement<T> reconstruct(JsonElement<?> from, JsonElement<T> changes) throws InvalidTypeException {
		if (!changes.getKeyType().equals(from.getKeyType())) {
			throw new InvalidTypeException(changes.getKeyType().getName(), changes.getKeyType().getName());
		}
		return changes.reconstruct((JsonElement<T>) from);
	}

	/**
	 * Converts the given object to a string in the way intended for Jsons to match
	 * the Json specifications.
	 * 
	 * @param content the object to get the string representation for.
	 * @return a string representation of the given object.
	 */
	@Deprecated
	default String contentToString(Object content) {
		StringBuilder buffer = new StringBuilder();
		contentToString(content, buffer);
		return buffer.toString();
	}

	/**
	 * Writes the given object to the given {@link StringBuilder} in the way
	 * intended for Jsons to match the Json specifications.
	 * 
	 * @param content the object to get the string representation for.
	 * @param builder the {@link StringBuilder} to write the object to.
	 */
	// TODO move me to string utils
	default void contentToString(Object content, StringBuilder builder) {
		if (content == null || content instanceof Boolean || content instanceof Byte || content instanceof Short
				|| content instanceof Integer || content instanceof Float || content instanceof Double
				|| content instanceof Long || content instanceof JsonElement) {
			builder.append(content);
		} else {
			String contentString = content.toString();
			builder.ensureCapacity(builder.length() + contentString.length() + 2);
			builder.append('"');
			for (char c:contentString.toCharArray()) {
				if (c == '\\' || c == '"') {
					builder.append('\\');
				}
				builder.append(c);
			}
			builder.append('"');
		}
	}

	/**
	 * Gets the {@link Class} for the key type of this json instance.
	 * 
	 * @return the {@link Class} for the key type of this json instance.
	 */
	public Class<K> getKeyType();

}
