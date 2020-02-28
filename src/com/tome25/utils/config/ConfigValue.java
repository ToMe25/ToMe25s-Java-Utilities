package com.tome25.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 
 * @author ToMe25
 * 
 *         a utility class that stores config values
 *
 */
public class ConfigValue {

	private final File cfg;
	private final String key;
	private final String[] validTypes = { "string", "integer", "int", "boolean", "bool" };
	private final String type;
	private Object value;
	private final Object defaultValue;
	private final String description;

	/**
	 * Initializes a new ConfigValue Object
	 * 
	 * @param cfg          the Configuration File
	 * @param key          the Configuration Key
	 * @param type         the Type of this Config Object
	 * @param defaultValue the default Value
	 * @param description  the Description for this Config Object
	 * @throws NullPointerException if key is null
	 * @throws InvalidTypeException if the given type is invalid
	 */
	public ConfigValue(File cfg, String key, String type, Object defaultValue, String description)
			throws NullPointerException, InvalidTypeException {
		if (key == null) {
			throw new NullPointerException("Configuration Key can't be null!");
		}
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = value;
	}

	/**
	 * @param value the value to set(will be parsed to the type you have selected)
	 */
	public void setValue(String value) {
		if (type.equalsIgnoreCase("string")) {
			this.value = value;
		} else if (type.equalsIgnoreCase("integer") || type.equalsIgnoreCase("int")) {
			this.value = Integer.parseInt(value);
		} else if (type.equalsIgnoreCase("boolean")) {
			this.value = Boolean.parseBoolean(value);
		}
	}

	/**
	 * @return the default Value
	 */
	public Object getDefaultValue() {
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
	private boolean validType(String type) {
		return validType(type, true);
	}

	/**
	 * Checks if the given Type is valid
	 * 
	 * @param type       the type to check
	 * @param ignoreCase ingnore case while checking?
	 * @return is type valid?
	 */
	private boolean validType(String type, boolean ignoreCase) {
		for (String t : validTypes) {
			if (ignoreCase ? t.equalsIgnoreCase(type) : t.equals(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @author ToMe25
	 * 
	 *         Just a simple Exception for Invalid types
	 */
	public class InvalidTypeException extends Exception {

		/**
		 * useless or not?
		 */
		private static final long serialVersionUID = 123L;
		private String message;

		public InvalidTypeException() {

		}

		public InvalidTypeException(String msg) {
			message = msg;
		}

		@Override
		public String getMessage() {
			return message == null ? "" : message;
		}

	}

}