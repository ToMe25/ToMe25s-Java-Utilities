package com.tome25.utils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.tome25.utils.config.Config;
import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonObject;

public class ConfigTest {

	@Test
	public void configTest() throws IOException {
		// test the basics of the config system.
		File cfgFile = new File("Config");
		Config cfg = new Config(false, cfgFile, false);
		cfg.addConfig("Test.cfg", "testString", "\\Hello\"World:?\\",
				"This is a Test config option to test the Config class.");
		cfg.addConfig("Test.cfg", "testInt", 32123, "A Test integer");
		cfg.addConfig("Test.cfg", "testDouble", Double.MAX_VALUE, "A double for test purposes.");
		cfg.readConfig();
		assertEquals("\\Hello\"World:?\\", cfg.getConfig("testString"));
		assertEquals(32123, cfg.getConfig("testInt"));
		assertEquals(Double.MAX_VALUE, cfg.getConfig("testDouble"));
		// test the handling of changed values.
		FileInputStream fIn = new FileInputStream(new File(cfgFile, "Test.cfg"));
		byte[] buffer = new byte[fIn.available()];
		fIn.read(buffer);
		fIn.close();
		String config = new String(buffer);
		config = config.replaceAll("World", "Pond").replaceAll("32123", "" + Integer.MAX_VALUE);
		FileOutputStream fOut = new FileOutputStream(new File(cfgFile, "Test.cfg"));
		fOut.write(config.getBytes());
		fOut.close();
		cfg.readConfig();
		assertEquals("\\Hello\"Pond:?\\", cfg.getConfig("testString"));
		assertEquals(Integer.MAX_VALUE, cfg.getConfig("testInt"));
		// test the setting of values.
		cfg.setConfig("testDouble", Integer.MAX_VALUE / Math.PI);
		cfg.readConfig();
		assertEquals(Integer.MAX_VALUE / Math.PI, cfg.getConfig("testDouble"));
		// test the handling of another config file.
		cfg.addConfig("test.txt", "testString2", "Some random test String!", "Some random test String!");
		cfg.readConfig();
		assertEquals("Some random test String!", cfg.getConfig("testString2"));
		// test float handling.
		cfg.addConfig("test.txt", "floatTest", Float.MAX_EXPONENT, "A float test.");
		cfg.readConfig();
		assertEquals(Float.MAX_EXPONENT, cfg.getConfig("floatTest"));
		// test json handling.
		cfg.addConfig("Test.cfg", "testJson", new JsonObject("someTest", "Test String"), "a test json object.");
		cfg.addConfig("Test.cfg", "testJsonArray", new JsonArray("Test", 123, "some random test", Double.MIN_VALUE),
				"a test json array.");
		cfg.readConfig();
		assertEquals(new JsonObject("someTest", "Test String"), cfg.getConfig("testJson"));
		assertEquals(new JsonArray("Test", 123, "some random test", Double.MIN_VALUE), cfg.getConfig("testJsonArray"));
		// test wrong subclass handling.
		cfg.addConfig("wrong.cfg", "int", Integer.MIN_VALUE, "Some random test.");
		cfg.addConfig("wrong.cfg", "json", new JsonObject("key", "value"), "Some random test.");
		fOut = new FileOutputStream(new File(cfgFile, "wrong.cfg"));
		fOut.write("json: [123, 321]\nint: Test".getBytes());
		fOut.close();
		System.err.println(
				"The following ClassCastException and NumberFormatException are excepted to happen during this test, and not a problem!");
		cfg.readConfig();
		assertEquals(Integer.MIN_VALUE, cfg.getConfig("int"));
		assertEquals(new JsonObject("key", "value"), cfg.getConfig("json"));
		// delete config files.
		cfg.delete();
	}

	@Test
	public void configWatcherTest() throws IOException, InterruptedException {
		// test the basics of the config watcher.
		File cfgFile = new File("Config");
		final boolean[] changed = new boolean[] { false };
		Config cfg = new Config(true, cfgFile, true, (file) -> changed[0] = true);
		cfg.addConfig("Watcher.cfg", "StringTest", "Some Random String",
				"A String that definitifly wont get changed...");
		assertEquals("Some Random String", cfg.getConfig("StringTest"));
		FileInputStream fIn = new FileInputStream(new File(cfgFile, "Watcher.cfg"));
		byte[] buffer = new byte[fIn.available()];
		fIn.read(buffer);
		fIn.close();
		String config = new String(buffer);
		config = config.replace("Random", "Changed").replaceAll("wont", "will");
		FileOutputStream fOut = new FileOutputStream(new File(cfgFile, "Watcher.cfg"));
		Thread.sleep(50);// Wait for the ConfigWatcher to finish initializing.
		fOut.write(config.getBytes());
		fOut.flush();
		fOut.close();
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac") || os.contains("darwin")) {
			cfg.readConfig();
		} else {
			// skip waiting for the config watcher, as that doesn't work on macos anyways.
			final int maxWaitTime = 10000;
			int waitTime = 0;
			while (!changed[0] && waitTime < maxWaitTime) {
				Thread.sleep(5);
				waitTime += 5;
			}
			Thread.sleep(10);
			assertTrue(String.format("The ConfigWatcher didn't detect any changes in the may allowed wait time of %ds.",
					maxWaitTime / 1000), changed[0]);
		}
		assertEquals("Some Changed String", cfg.getConfig("StringTest"));
		cfg.delete();
	}

}
