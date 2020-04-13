package com.tome25.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tome25.utils.exception.InvalidTypeException;

/**
 * 
 * A utility class that stores config values.
 * 
 * @author ToMe25
 *
 */
public class ConfigValue<T> {

	private final File cfg;
	private final String key;
	private static final Class<?>[] validTypes = { String.class, Integer.class, Short.class, Byte.class, Boolean.class,
			Double.class, Float.class };
	private final Class<?> type;
	private T value;
	private final T defaultValue;
	private final String description;

	/**
	 * Initializes a new ConfigValue Object
	 * 
	 * @param cfg          the Configuration File
	 * @param key          the Configuration Key
	 * @param defaultValue the default Value
	 * @param description  the Description for this Config Object
	 * @throws NullPointerException if key is null
	 * @throws InvalidTypeException if the given type is invalid
	 */
	public ConfigValue(File cfg, String key, T defaultValue, String description)
			throws NullPointerException, InvalidTypeException {
		if (key == null) {
			throw new NullPointerException("Configuration Key can't be null!");
		}
		type = defaultValue.getClass();
		if (!validType(type)) {
			throw new InvalidTypeException("Type " + type.getName() + " isn't valid.");
		}
		this.cfg = cfg;
		this.key = key;
		this.defaultValue = defaultValue;
		this.value = defaultValue;
		this.description = description;
	}

	/**
	 * @return the Configuration File
	 */
	public File getCfg() {
		return cfg;
	}

	/**
	 * @return the Config key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * gets this values type as a String
	 * 
	 * @return the type
	 */
	public String getTypeString() {
		return type.getSimpleName();
	}

	/**
	 * gets this values type as a Class
	 * 
	 * @return the type
	 */
	public Class<?> getTypeClass() {
		return type;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * @param value the value to set(will be parsed to the type you have selected)
	 */
	@SuppressWarnings("unchecked")
	public void setValue(String value) {
		if (String.class.isAssignableFrom(type)) {
			((ConfigValue<String>) this).value = value;
		} else if (Integer.class.isAssignableFrom(type)) {
			((ConfigValue<Integer>) this).value = Integer.parseInt(value);
		} else if (Short.class.isAssignableFrom(type)) {
			((ConfigValue<Short>) this).value = Short.parseShort(value);
		} else if (Byte.class.isAssignableFrom(type)) {
			((ConfigValue<Byte>) this).value = Byte.parseByte(value);
		} else if (Boolean.class.isAssignableFrom(type)) {
			((ConfigValue<Boolean>) this).value = Boolean.parseBoolean(value);
		} else if (Double.class.isAssignableFrom(type)) {
			((ConfigValue<Double>) this).value = Double.parseDouble(value);
		} else if (Float.class.isAssignableFrom(type)) {
			((ConfigValue<Float>) this).value = Float.parseFloat(value);
		}
	}

	/**
	 * @return the default Value
	 */
	public T getDefaultValue() {
		return defaultValue;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Writes the Config Option to the given FileOutputStream.
	 * 
	 * @param fiout the File OutputStream to write
	 * @throws IOException if an IO Error Occures while writing to the
	 *                     FileOutputStream
	 */
	public void writeToConfig(FileOutputStream fiout) throws IOException {
		String comment = String.format("# %s%n# Default: %s", description, defaultValue);
		String toWrite = comment + System.lineSeparator() + key + ": " + value + System.lineSeparator();
		fiout.write(toWrite.getBytes());
		fiout.flush();
	}

	/**
	 * Checks if the given Type is valid(ignoring case)
	 * 
	 * @param type the type to check
	 * @return is type valid?
	 */
	public static boolean validType(String type) {
		return validType(type, true);
	}

	/**
	 * Checks if the given Type is valid. A type is valid if it is the simple name
	 * of a class in the validTypes array.
	 * 
	 * @param type       the type to check
	 * @param ignoreCase ingnore case while checking?
	 * @return is type valid?
	 */
	public static boolean validType(String type, boolean ignoreCase) {
		for (Class<?> cls : validTypes) {
			if (ignoreCase ? cls.getSimpleName().equalsIgnoreCase(type) : cls.getSimpleName().equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given Type is valid. A type is valid if it is a class in the
	 * validTypes array or a subclass of one of those.
	 * 
	 * @param type the type to check
	 * @return is type valid?
	 */
	public static boolean validType(Class<?> type) {
		for (Class<?> cls : validTypes) {
			if (cls.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

}