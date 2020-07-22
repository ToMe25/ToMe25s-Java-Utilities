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
 */
public interface JsonElement extends Iterable<Object>, Externalizable, Comparable<JsonElement> {

	/**
	 * Adds the Value with the given key if there are no Object with this Key.
	 * 
	 * @param key   the Key to add.
	 * @param value the value to add for key.
	 * @throws InvalidKeyException  when there already an Object with this Key.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 * @return depends on the implementation.
	 */
	public Object add(Object key, Object value) throws InvalidKeyException, InvalidTypeException;

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
	public Object put(Object key, Object value) throws InvalidTypeException;

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m mappings to be stored in this json.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 */
	public void putAll(Map<? extends Object, ? extends Object> m) throws InvalidTypeException;

	/**
	 * Adds the given value to the given key replacing the current value if it
	 * already exists.
	 * 
	 * @param key   the Key to add.
	 * @param value the value to set for key.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 * @return the previous value associated with key.
	 */
	public Object set(Object key, Object value) throws InvalidTypeException;

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m mappings to be stored in this Json.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 */
	public void setAll(Map<? extends Object, ? extends Object> m) throws InvalidTypeException;

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
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 */
	public String getString(Object key) throws InvalidTypeException;

	/**
	 * Gets this Jsons Values.
	 * 
	 * @return this Jsons Values.
	 */
	public default Collection<Object> getValues() {
		return values();
	}

	/**
	 * Gets this Jsons Values.
	 * 
	 * @return this Jsons Values.
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
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for {@link JsonObject}s,
	 *                              Integer for {@link JsonArray}s).
	 */
	public boolean containsKey(Object key) throws InvalidTypeException;

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
	 * Gets the size of this object. If recursive is true this counts all sub Json
	 * elements as their size instead of one.
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
		return true;
	}

	/**
	 * Creates and returns a copy of this Json. Recursive.
	 * 
	 * @return a copy of this Json Object.
	 * @throws CloneNotSupportedException if this element can't be cloned.
	 */
	public JsonElement clone() throws CloneNotSupportedException;

	/**
	 * Creates and returns a copy of this Json.
	 * 
	 * @param recursive whether Jsons inside this Json object should get cloned as
	 *                  well.
	 * @return a copy of this Json Object.
	 * @throws CloneNotSupportedException if this element can't be cloned
	 */
	public JsonElement clone(boolean recursive) throws CloneNotSupportedException;

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj the object to compare this object to.
	 * @return whether some other object is "equal to" this one.
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
		return true;
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
	 *                                       generating a changes Json.
	 */
	public default JsonElement changes(JsonElement from) throws UnsupportedOperationException {
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
	 *                                       generating a changes Json.
	 */
	public JsonElement changes(JsonElement from, boolean recursive) throws UnsupportedOperationException;

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
	 *                                       generating a changes Json.
	 */
	public default JsonElement reconstruct(JsonElement from) throws UnsupportedOperationException {
		return reconstruct(from, true);
	}

	/**
	 * Returns a new JsonElement containing the values of the given JsonElement with
	 * the changes contained in this JsonElement applied. This marks removed values
	 * by setting them to null, so it will not fully work with JsonElements
	 * containing null objects.
	 * 
	 * @param from      the previous JsonElement.
	 * @param recursive whether changedJsonElements inside this one should get
	 *                  reconstructed too, or just cloned from this one.
	 * @return a new JsonElement containing the values of the given JsonElement with
	 *         the changes contained in this JsonElement applied.
	 * @throws UnsupportedOperationException if this JsonElement doesn't support
	 *                                       generating a changes Json.
	 */
	public JsonElement reconstruct(JsonElement from, boolean recursive);

	/**
	 * Converts the given object to a string in the way intended for Jsons to match
	 * the Json specifications.
	 * 
	 * @param content the object to get the string representation for.
	 * @return a string representation of the given object.
	 */
	public default String contentToString(Object content) {
		String str = "";
		if (content == null || content instanceof Boolean || content instanceof Byte || content instanceof Short
				|| content instanceof Integer || content instanceof Float || content instanceof Double
				|| content instanceof Long || content instanceof JsonElement) {
			str += content;
		} else {
			str += "\"";
			str += content.toString().replaceAll("\\\\", "\\\\\\\\").replaceAll("\\\"", "\\\\\"");
			str += "\"";
		}
		return str;
	}

}
