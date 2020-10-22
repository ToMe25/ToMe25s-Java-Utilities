package com.tome25.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import javax.naming.ConfigurationException;

import com.tome25.utils.exception.InvalidTypeException;

/**
 * A configuration file handler. This class handles reading from and writing to
 * configuration files, automatically handling value types.
 * 
 * @author ToMe25
 *
 */
public class Config {

	private Map<String, ConfigValue<?>> cfg = new HashMap<String, ConfigValue<?>>();
	private Map<File, List<ConfigValue<?>>> sortedConfig = new HashMap<File, List<ConfigValue<?>>>();
	private Map<File, ReentrantLock> fileLocks = new HashMap<File, ReentrantLock>();
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
				ConfigValue<T> val = (ConfigValue<T>) cfg.get(name);
				File config = val.getCfg();
				if (!fileLocks.containsKey(config.getAbsoluteFile())) {
					fileLocks.put(config.getAbsoluteFile(), new ReentrantLock(true));
				}
				ReentrantLock lock = fileLocks.get(config.getAbsoluteFile());
				lock.lock();
				boolean changed = !val.getValue().equals(value);
				val.setValue(value);
				if (changed) {
					createConfig(config);
				}
				lock.unlock();
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
			if (!fileLocks.containsKey(file.getAbsoluteFile())) {
				fileLocks.put(file.getAbsoluteFile(), new ReentrantLock(true));
			}
			ReentrantLock lock = fileLocks.get(file.getAbsoluteFile());
			lock.lock();
			if (!file.exists()) {
				createConfig(file);
			}
			Scanner sc = new Scanner(file);
			if (!sc.hasNextLine()) {
				createConfig(file);
			}
			sc.close();
			sc = new Scanner(file);
			List<ConfigValue<?>> missing = new ArrayList<ConfigValue<?>>(sortedConfig.get(file));
			boolean error = false;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				if (!line.startsWith("#") && !line.trim().isEmpty()) {
					for (ConfigValue<?> c : sortedConfig.get(file)) {
						if (line.replaceAll(" ", "").startsWith(c.getKey() + ":")) {
							String value = line.replaceFirst(c.getKey(), "").replaceFirst(":", "");
							while (value.startsWith(" ")) {
								value = value.substring(1);
							}
							Object oldValue = c.getValue();
							c.setValue(value);
							changed = changed || !oldValue.equals(c.getValue());
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
			} else if (!missing.isEmpty()) {
				FileOutputStream fiout = new FileOutputStream(file, true);
				for (ConfigValue<?> c : missing) {
					c.writeToConfig(fiout);
				}
				fiout.close();
			}
			lock.unlock();
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
			config = config.getAbsoluteFile();
			if (!fileLocks.containsKey(config.getAbsoluteFile())) {
				fileLocks.put(config.getAbsoluteFile(), new ReentrantLock(true));
			}
			ReentrantLock lock = fileLocks.get(config.getAbsoluteFile());
			lock.lock();
			File dir = config.getParentFile();
			if (!dir.exists() || !dir.isDirectory()) {
				dir.mkdirs();
			}
			if (config.exists() && !config.isFile()) {
				config.delete();
			}
			if (!config.exists()) {
				config.createNewFile();
			}
			config.setReadable(true, true);
			config.setWritable(true, true);
			try {
				// This line has it's own try catch Block because sometimes this Program hasn't
				// the Permissions to do that.
				config.setExecutable(false, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (sortedConfig == null) {
				sortConfig();
			}
			if (sortedConfig.isEmpty()) {
				sortConfig();
			}
			if (sortedConfig.containsKey(config)) {
				FileOutputStream fiout = new FileOutputStream(config);
				fiout.write(String.format("# The %s Configuration for %s.%n",
						config.getName().substring(0, config.getName().lastIndexOf('.')),
						new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath())
								.getName())
						.getBytes());
				fiout.flush();
				List<ConfigValue<?>> configCopy = new ArrayList<ConfigValue<?>>(sortedConfig.get(config));
				for (ConfigValue<?> c : configCopy) {
					c.writeToConfig(fiout);
					fiout.write(System.lineSeparator().getBytes());
					fiout.flush();
				}
				fiout.close();
			}
			lock.unlock();
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cfg == null) ? 0 : cfg.hashCode());
		result = prime * result + ((cfgDir == null) ? 0 : cfgDir.hashCode());
		result = prime * result + (read ? 1231 : 1237);
		result = prime * result + ((sortedConfig == null) ? 0 : sortedConfig.hashCode());
		result = prime * result + ((watcher == null) ? 0 : watcher.hashCode());
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
		Config other = (Config) obj;
		if (cfg == null) {
			if (other.cfg != null) {
				return false;
			}
		} else if (!cfg.equals(other.cfg)) {
			return false;
		}
		if (cfgDir == null) {
			if (other.cfgDir != null) {
				return false;
			}
		} else if (!cfgDir.equals(other.cfgDir)) {
			return false;
		}
		if (read != other.read) {
			return false;
		}
		if (sortedConfig == null) {
			if (other.sortedConfig != null) {
				return false;
			}
		} else if (!sortedConfig.equals(other.sortedConfig)) {
			return false;
		}
		if (watcher == null) {
			if (other.watcher != null) {
				return false;
			}
		} else if (!watcher.equals(other.watcher)) {
			return false;
		}
		return true;
	}
}
