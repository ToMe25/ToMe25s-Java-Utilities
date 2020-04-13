package com.tome25.utils.config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A configuration handler.
 * 
 * @author ToMe25
 *
 */
public class Config {

	private Map<String, ConfigValue<?>> cfg = new HashMap<String, ConfigValue<?>>();
	private Map<File, List<ConfigValue<?>>> sortedConfig = new HashMap<File, List<ConfigValue<?>>>();
	private File cfgDir;

	public Config(boolean read) {
		this(new File(Config.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile(),
				read);
	}

	public Config(File configDir, boolean read) {
		cfgDir = configDir;
		if (read) {
			readConfig();
		}
	}

	/**
	 * gets the Config value for the given Name.
	 * 
	 * @param name the name of the config option to get.
	 * @return
	 */
	public Object getConfig(String name) {
		if (cfg.containsKey(name)) {
			return cfg.get(name).getValue();
		} else {
			System.err.printf("Couldn't find Config Value %s!%n", name);
			return null;
		}
	}

	/**
	 * adds a config option to be read from the given config file.
	 * 
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comment      a comment to add to this config option.
	 */
	public <T> void addConfig(String config, String name, T defaultValue, String comment) {
		addConfig(new File(cfgDir, config), name, defaultValue, comment);
	}

	/**
	 * adds a config option to be read from the given config file.
	 * 
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comments     a list of comments to add to this config option. every
	 *                     string will get its own line.
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
	 * adds a config option to be read from the given config file.
	 * 
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comment      a comment to add to this config option.
	 */
	public <T> void addConfig(File config, String name, T defaultValue, String comment) {
		try {
			if (cfg.containsKey(name)) {
				System.err.printf("There is already a Config Option with name %s!%n", name);
			} else {
				cfg.put(name, new ConfigValue<T>(config, name, defaultValue, comment));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds a config option to be read from the given config file.
	 * 
	 * @param config       the config file to read this config option from.
	 * @param name         the key for this config option.
	 * @param defaultValue this config options default value.
	 * @param comments     a list of comments to add to this config option. every
	 *                     string will get its own line.
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
	 * sets the Config value for the given Name the the given Value.
	 * 
	 * @param name  the name of the config option to get.
	 * @param value the value to set the config option to.
	 */
	@SuppressWarnings("unchecked")
	public <T> void setConfig(String name, T value) {
		if (cfg.containsKey(name)) {
			if (cfg.get(name).getTypeClass().equals(value.getClass())) {
				((ConfigValue<T>) cfg.get(name)).setValue(value);
				createConfig(cfg.get(name).getCfg());
			} else {
				System.err.printf("The config value with name %s is of type %s not %s!%n", name,
						cfg.get(name).getTypeClass().getName(), value.getClass().getName());
			}
		} else {
			System.err.printf("Couldn't find Config Value %s!%n", name);
		}
	}

	/**
	 * reads the Config.
	 */
	public void readConfig() {
		sortConfig();
		try {
			if (!cfgDir.exists() || !cfgDir.isDirectory()) {
				cfgDir.mkdirs();
			}
			for (File f : sortedConfig.keySet()) {
				if (!f.exists()) {
					createConfig(f);
				}
				Scanner sc = new Scanner(f);
				if (!sc.hasNextLine()) {
					createConfig(f);
				}
				sc.close();
				sc = new Scanner(f);
				List<ConfigValue<?>> missing = new ArrayList<ConfigValue<?>>();
				missing.addAll(sortedConfig.get(f));
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (!line.startsWith("#")) {
						for (ConfigValue<?> c : sortedConfig.get(f)) {
							if (line.startsWith(c.getKey())) {
								c.setValue(line.replaceFirst(c.getKey(), "").replaceFirst(":", "").replaceAll(" ", ""));
								missing.remove(c);
							}
						}
					}
				}
				sc.close();
				FileOutputStream fiout = new FileOutputStream(f, true);
				for (ConfigValue<?> c : missing) {
					c.writeToConfig(fiout);
				}
				fiout.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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
			}
			fiout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sortConfig() {
		for (ConfigValue<?> c : cfg.values()) {
			if (!sortedConfig.containsKey(c.getCfg())) {
				sortedConfig.put(c.getCfg(), new ArrayList<ConfigValue<?>>());
			}
			sortedConfig.get(c.getCfg()).add(c);
		}
	}
}