package com.tome25.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.tome25.utils.exception.InvalidTypeException;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonParser;

/**
 * A utility class that stores {@link Config} values.
 * 
 * @author ToMe25
 *
 */
public class ConfigValue<T> {

	private final File cfg;
	private final String key;
	private static final Class<?>[] validTypes = { String.class, Integer.class, Short.class, Byte.class, Boolean.class,
			Double.class, Float.class, JsonElement.class };
	private final Class<?> type;
	private T value;
	private boolean error;
	private final T defaultValue;
	private final String description;

	/**
	 * Initializes a new ConfigValue object.
	 * 
	 * @param cfg          the configuration file.
	 * @param key          the configuration key.
	 * @param defaultValue the configuration default value.
	 * @param description  the description for this ConfigValue object.
	 * @throws NullPointerException if key is null.
	 * @throws InvalidTypeException if the given type is invalid.
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
	 * Gets the configuration file.
	 * 
	 * @return the configuration file.
	 */
	public File getCfg() {
		return cfg;
	}

	/**
	 * Gets the configuration key.
	 * 
	 * @return the configuration key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets this ConfigValues type as a string.
	 * 
	 * @return this ConfigValues type as a string.
	 */
	public String getTypeString() {
		return type.getSimpleName();
	}

	/**
	 * Gets this ConfigValues type as a {@link Class}.
	 * 
	 * @return this ConfigValues type as a {@link Class}.
	 */
	public Class<?> getTypeClass() {
		return type;
	}

	/**
	 * Gets this ConfigValues value.
	 * 
	 * @return this ConfigValues value.
	 */
	public T getValue() {
		return value;
	}

	/**
	 * Sets this ConfigValues value.
	 * 
	 * @param value the value to set.
	 */
	public void setValue(T value) {
		this.value = value;
	}

	/**
	 * Sets this ConfigValues value.
	 * 
	 * @param value the value to set(will be parsed to the type you have selected).
	 */
	@SuppressWarnings("unchecked")
	public void setValue(String value) {
		try {
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
			} else if (JsonElement.class.isAssignableFrom(type)) {
				((ConfigValue<T>) this).value = (T) type.cast(JsonParser.parseString(value));
			}
		} catch (Exception e) {
			e.printStackTrace();
			error = true;
			this.value = defaultValue;
		}
	}

	/**
	 * Gets this ConfigValues default value.
	 * 
	 * @return this ConfigValues default value.
	 */
	public T getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Gets this ConfigValues description.
	 * 
	 * @return this ConfigValues description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Writes the ConfigValue to the given {@link FileOutputStream}.
	 * 
	 * @param fiout the {@link FileOutputStream} to write to.
	 * @throws IOException if an IO Error occures while writing to the
	 *                     {@link FileOutputStream}.
	 */
	public void writeToConfig(FileOutputStream fiout) throws IOException {
		String comment = String.format("# %s%n# Default: %s", description, defaultValue);
		String toWrite = comment + System.lineSeparator() + key + ": " + value + System.lineSeparator();
		fiout.write(toWrite.getBytes());
		fiout.flush();
	}

	/**
	 * Checks if the given type is valid(ignoring case).
	 * 
	 * @param type the type to check.
	 * @return whether the type is valid.
	 */
	public static boolean validType(String type) {
		return validType(type, true);
	}

	/**
	 * Checks if the given type is valid. A type is valid if it is the simple name
	 * of a class in the validTypes array.
	 * 
	 * @param type       the type to check.
	 * @param ignoreCase whether the check should be case sensitive.
	 * @return whether the type is valid.
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
	 * Checks if the given type is valid. A type is valid if it is a class in the
	 * validTypes array or a subclass of one of those.
	 * 
	 * @param type the type to check.
	 * @return whether the type is valid.
	 */
	public static boolean validType(Class<?> type) {
		for (Class<?> cls : validTypes) {
			if (cls.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks whether this value caught an error while trying to parse a value.
	 * 
	 * @return whether this value caught an error while trying to parse a value.
	 */
	public boolean isError() {
		return error;
	}

	/**
	 * Resets the error state of this value.
	 */
	public void clearError() {
		error = false;
	}

}