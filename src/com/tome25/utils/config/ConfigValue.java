package com.tome25.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tome25.utils.config.exception.InvalidTypeException;

/**
 * 
 * @author ToMe25
 * 
 *         a utility class that stores config values
 *
 */
public class ConfigValue<T> {

	private final File cfg;
	private final String key;
	private static final String[] validTypes = { "string", "integer", "int", "boolean", "float" };
	private final String type;
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
		String type = defaultValue.getClass().getSimpleName().toLowerCase();
		if (!validType(type)) {
			throw new InvalidTypeException("Type " + type + " isn't valid.");
		}
		this.cfg = cfg;
		this.key = key;
		this.type = type;
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
		return type;
	}

	/**
	 * gets this values type as a Class
	 * 
	 * @return the type
	 */
	public Class<?> getTypeClass() {
		return defaultValue.getClass();
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
		if (type.equalsIgnoreCase("string")) {
			((ConfigValue<String>) this).value = value;
		} else if (type.equalsIgnoreCase("integer") || type.equalsIgnoreCase("int")) {
			((ConfigValue<Integer>) this).value = Integer.parseInt(value);
		} else if (type.equalsIgnoreCase("boolean")) {
			((ConfigValue<Boolean>) this).value = Boolean.parseBoolean(value);
		} else if (type.equalsIgnoreCase("float")) {
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
		String comment = "# " + description + " [DEFAULT: " + defaultValue + "]";
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
	 * Checks if the given Type is valid
	 * 
	 * @param type       the type to check
	 * @param ignoreCase ingnore case while checking?
	 * @return is type valid?
	 */
	public static boolean validType(String type, boolean ignoreCase) {
		for (String t : validTypes) {
			if (ignoreCase ? t.equalsIgnoreCase(type) : t.equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the given Type is valid
	 * 
	 * @param type the type to check
	 * @return is type valid?
	 */
	public static boolean validType(Class<?> type) {
		String typ = type.getSimpleName();
		for (String t : validTypes) {
			if (t.equalsIgnoreCase(typ)) {
				return true;
			}
		}
		return false;
	}

}