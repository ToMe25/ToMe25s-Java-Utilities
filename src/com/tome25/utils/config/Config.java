package com.tome25.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

import javax.naming.ConfigurationException;

import com.tome25.utils.exception.InvalidTypeException;

/**
 * A configuration file handler.
 * 
 * @author ToMe25
 *
 */
public class Config {

	private Map<String, ConfigValue<?>> cfg = new HashMap<String, ConfigValue<?>>();
	private Map<File, List<ConfigValue<?>>> sortedConfig = new HashMap<File, List<ConfigValue<?>>>();
	private File cfgDir;
	private boolean read;
	private ConfigWatcher watcher;

	/**
	 * Creates a new Config.
	 */
	public Config() {
		this(true);
	}

	/**
	 * Creates a new Config.
	 * 
	 * @param read whether the config file should be automatically read on addition
	 *             of options.
	 */
	public Config(boolean read) {
		this(new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile(),
				read);
	}

	/**
	 * Creates a new Config.
	 * 
	 * @param configDir the directory to put config files in.
	 */
	public Config(File configDir) {
		this(configDir, true);
	}

	/**
	 * Creates a new Config.
	 * 
	 * @param configDir the directory to put config files in.
	 * @param read      whether the config file should be automatically read on
	 *                  addition of options.
	 */
	public Config(File configDir, boolean read) {
		this(read, configDir, true);
	}

	/**
	 * Creates a new Config.
	 * 
	 * @param read      whether the config file should be automatically read on
	 *                  addition of options.
	 * @param configDir the directory to put config files in.
	 * @param watch     whether to watch the config directory, and update config
	 *                  options on change.
	 */
	public Config(boolean read, File configDir, boolean watch) {
		this(read, configDir, watch, null);
	}

	/**
	 * Creates a new Config.
	 * 
	 * @param read      whether the config file should be automatically read on
	 *                  addition of options.
	 * @param configDir the directory to put config files in.
	 * @param watch     whether to watch the config directory, and update config
	 *                  options on change.
	 * @param callback  a consumer to call with the changed file when a files config
	 *                  values change.
	 */
	public Config(boolean read, File configDir, boolean watch, Consumer<File> callback) {
		configDir = configDir.getAbsoluteFile();
		cfgDir = configDir;
		this.read = read;
		if (watch) {
			if (!configDir.exists()) {
				configDir.mkdirs();
			}
			watcher = new ConfigWatcher(configDir, file -> {
				sortConfig();
				if (readConfigFile(file) && callback != null) {
					callback.accept(file);
				}
			});
		}
	}

	/**
	 * Gets the config value for the given Name.
	 * 
	 * @param <T>  the option type.
	 * @param name the name of the config option to get.
	 * @return the config value for the given Name.
	 * @throws RuntimeException caused by {@link ConfigurationException} if there is
	 *                          no config option with the given name.
	 */
	@SuppressWarnings("unchecked")
	public <T> T getConfig(String name) {
		if (cfg.containsKey(name)) {
			return (T) cfg.get(name).getValue();
		} else {
			throw new RuntimeException(
					new ConfigurationException(String.format("Couldn't find Config Value %s!", name)));
		}
	}

	/**
	 * Adds a config option to be read from the given config file.
	 * 
	 * @param <T>          the option type.
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comment      a comment to add to this config option.
	 * @throws RuntimeException caused by {@link ConfigurationException} if a config
	 *                          option with the given name already exists.
	 */
	public <T> void addConfig(String config, String name, T defaultValue, String comment) {
		addConfig(new File(cfgDir, config), name, defaultValue, comment);
	}

	/**
	 * Adds a config option to be read from the given config file.
	 * 
	 * @param <T>          the option type.
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comments     a list of comments to add to this config option. Every
	 *                     string will get its own line.
	 * @throws RuntimeException caused by {@link ConfigurationException} if a config
	 *                          option with the given name already exists.
	 */
	public <T> void addConfig(String config, String name, T defaultValue, String... comments) {
		String comment = "";
		for (String str : comments) {
			comment += String.format("# %s%n", str);
		}
		comment = comment.substring(2, comment.length() - (System.lineSeparator().length()));
		addConfig(new File(cfgDir, config), name, defaultValue, comment);
	}

	/**
	 * Adds a config option to be read from the given config file.
	 * 
	 * @param <T>          the option type.
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comments     a list of comments to add to this Config option. Every
	 *                     string will get its own line.
	 * @throws RuntimeException caused by {@link ConfigurationException} if a config
	 *                          option with the given name already exists.
	 */
	public <T> void addConfig(File config, String name, T defaultValue, String... comments) {
		String comment = "";
		for (String str : comments) {
			comment += String.format("# %s%n", str);
		}
		comment = comment.substring(2, comment.length() - (System.lineSeparator().length()));
		addConfig(config, name, defaultValue, comment);
	}

	/**
	 * Adds a config option to be read from the given config file.
	 * 
	 * @param <T>          the option type.
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comment      a comment to add to this config option.
	 * @throws RuntimeException caused by {@link ConfigurationException} if a config
	 *                          option with the given name already exists.
	 */
	public <T> void addConfig(File config, String name, T defaultValue, String comment) {
		if (cfg.containsKey(name)) {
			throw new RuntimeException(
					new ConfigurationException(String.format("There is already a Config Option with name %s!", name)));
		} else {
			cfg.put(name, new ConfigValue<T>(config, name, defaultValue, comment));
		}
		if (read) {
			sortConfig();
			readConfigFile(config);
		}
	}

	/**
	 * Sets the config value for the given name the the given value.
	 * 
	 * @param <T>   the option type.
	 * @param name  the name of the config option to get.
	 * @param value the value to set the config option to.
	 * @throws RuntimeException     caused by {@link ConfigurationException} if
	 *                              there is no config option with the given name.
	 * @throws InvalidTypeException if the type of the config option with the give
	 *                              name does not match the type of the give value.
	 */
	@SuppressWarnings("unchecked")
	public <T> void setConfig(String name, T value) {
		if (cfg.containsKey(name)) {
			if (cfg.get(name).getTypeClass().equals(value.getClass())) {
				((ConfigValue<T>) cfg.get(name)).setValue(value);
				createConfig(cfg.get(name).getCfg());
			} else {
				throw new InvalidTypeException(String.format("The config value with name %s is of type %s not %s!",
						name, cfg.get(name).getTypeClass().getName(), value.getClass().getName()));
			}
		} else {
			throw new RuntimeException(
					new ConfigurationException(String.format("Couldn't find Config Value %s!", name)));
		}
	}

	/**
	 * Reads this Config from the files.
	 * 
	 * @return whether any of the config options changed.
	 */
	public boolean readConfig() {
		sortConfig();
		boolean changed = false;
		try {
			if (!cfgDir.exists() || !cfgDir.isDirectory()) {
				cfgDir.mkdirs();
			}
			for (File f : sortedConfig.keySet()) {
				changed = readConfigFile(f) || changed;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return changed;
	}

	/**
	 * Reads all {@link ConfigValue}s from the given config file.
	 * 
	 * @param file the file to read.
	 * @return whether any of the config options changed.
	 */
	public boolean readConfigFile(File file) {
		boolean changed = false;
		try {
			if (!sortedConfig.containsKey(file)) {
				return false;
			}
			if (!file.exists()) {
				createConfig(file);
			}
			Scanner sc = new Scanner(file);
			if (!sc.hasNextLine()) {
				createConfig(file);
			}
			sc.close();
			sc = new Scanner(file);
			List<ConfigValue<?>> missing = new ArrayList<ConfigValue<?>>();
			missing.addAll(sortedConfig.get(file));
			boolean error = false;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.startsWith("#")) {
					for (ConfigValue<?> c : sortedConfig.get(file)) {
						if (line.replaceAll(" ", "").startsWith(c.getKey() + ":")) {
							String value = line.replaceFirst(c.getKey(), "").replaceFirst(":", "");
							while (value.startsWith(" ")) {
								value = value.substring(1);
							}
							changed = changed || !value.equals(c.getValue());
							c.setValue(value);
							error = error || c.isError();
							c.clearError();
							missing.remove(c);
						}
					}
				}
			}
			sc.close();
			if (error) {
				createConfig(file);
			} else {
				FileOutputStream fiout = new FileOutputStream(file, true);
				for (ConfigValue<?> c : missing) {
					c.writeToConfig(fiout);
				}
				fiout.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return changed;
	}

	/**
	 * Writes all {@link ConfigValue}s in the given file to that file.
	 * 
	 * @param config the file to write.
	 */
	private void createConfig(File config) {
		try {
			File dir = config.getParentFile();
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
			config.createNewFile();
			config.setReadable(true, true);
			config.setWritable(true, true);
			try {
				// This line has it's own try catch Block because sometimes this Program hasn't
				// the Permissions to do that.
				config.setExecutable(false, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			FileOutputStream fiout = new FileOutputStream(config);
			fiout.write(String.format("# The %s Configuration for %s.%n",
					config.getName().substring(0, config.getName().lastIndexOf('.')),
					new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()).getName())
					.getBytes());
			fiout.flush();
			for (ConfigValue<?> c : sortedConfig.get(config)) {
				c.writeToConfig(fiout);
				fiout.write(System.lineSeparator().getBytes());
				fiout.flush();
			}
			fiout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sorts the {@link ConfigValue}s by the file they are stored in.
	 */
	private void sortConfig() {
		sortedConfig.clear();
		for (ConfigValue<?> c : cfg.values()) {
			if (!sortedConfig.containsKey(c.getCfg())) {
				sortedConfig.put(c.getCfg(), new ArrayList<ConfigValue<?>>());
			}
			sortedConfig.get(c.getCfg()).add(c);
		}
	}

	/**
	 * Deletes all used config files, and the config directory, if it is empty.
	 */
	public void delete() {
		if (watcher != null) {
			watcher.stop();
		}
		sortedConfig.keySet().forEach(file -> file.delete());
		cfgDir.delete();
	}
}
