package com.tome25.utils.json;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface JsonElement extends Iterable<Object>, Serializable {

	/**
	 * Adds the Value with the given key if there are no Object with this Key.
	 * 
	 * @param key   the Key to add.
	 * @param value the value to add for key.
	 * @throws InvalidKeyException  when there already an Object with this Key.
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	public Object add(Object key, Object value);

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
	public Object put(Object key, Object value);

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 */
	public void putAll(Map<? extends Object, ? extends Object> m);

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
	public Object set(Object key, Object value);

	/**
	 * Adds the given values to the given keys replacing the current ones if
	 * existing.
	 * 
	 * @param m
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 */
	public void setAll(Map<? extends Object, ? extends Object> m);

	/**
	 * if key is true this removes the object to the key o from this Json, if not it
	 * removes the value o from this Json.
	 * 
	 * @param o
	 * @param key
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 */
	public void remove(Object o, boolean key);

	/**
	 * gets the value for the given key.
	 * 
	 * @param key
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	public Object get(Object key);

	/**
	 * gets the value for the given key if its a string, or a string representation
	 * of that value if it isn't.
	 * 
	 * @param key
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	public String getString(Object key);

	/**
	 * returns this Jsons Values.
	 * 
	 * @return
	 */
	public Collection<Object> getValues();

	/**
	 * whether this Json contains the given object, either as key if the type
	 * matches, or as value.
	 * 
	 * @param o
	 * @return
	 */
	public boolean contains(Object o);

	/**
	 * returns true if this Json contains the given key.
	 * 
	 * @param key
	 * @throws InvalidTypeException if the key type doesn't match the key type for
	 *                              this object(String for JsonObjects, Integer for
	 *                              JsonArrays)
	 * @return
	 */
	public boolean containsKey(Object key);

	/**
	 * returns true if this Json contains the given value.
	 * 
	 * @param value
	 * @return
	 */
	public boolean containsValue(Object value);

	/**
	 * gets the size of this object.
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Returns a String representation of this element.
	 * 
	 * @return
	 */
	public String toString();

	/**
	 * Returns a String representation of this element as byte array.
	 * 
	 * @return
	 */
	public default byte[] toByteArray() {
		return toString().getBytes();
	}

	/**
	 * Returns a String representation of this element as character array.
	 * 
	 * @return
	 */
	public default char[] toCharArray() {
		return toString().toCharArray();
	}

	/**
	 * Creates and returns a copy of this Json Object.
	 * 
	 * @return
	 */
	public Object clone() throws CloneNotSupportedException;

	/**
	 * Creates and returns a copy of this Json Object.
	 * 
	 * @param recursive whether Jsons inside this Json object should get cloned as
	 *                  well.
	 * @return
	 */
	public JsonElement clone(boolean recursive);

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj
	 * @return
	 */
	public boolean equals(Object obj);

	/**
	 * whether this is empty.
	 * 
	 * @return
	 */
	public boolean isEmpty();

	/**
	 * Clears this Json.
	 */
	public void clear();

}
