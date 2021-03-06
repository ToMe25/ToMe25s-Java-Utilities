/*
 * ToMe25s-Java-Utilities is a collection of common java utilities.
 * Copyright (C) 2020-2021  ToMe25
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.tome25.utils.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.tome25.utils.config.Config;
import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonObject;

public class ConfigTest {

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void configTest() throws IOException {
		// test the basics of the config system.
		File cfgFolder = tempFolder.newFolder("ToMe25s-Java-Utilities-Config-Test");
		Config cfg = new Config(false, cfgFolder, false);
		cfg.addConfig("Test.cfg", "testString", "\\Hello\"World:?\\",
				"This is a Test config option to test the Config class.");
		cfg.addConfig("Test.cfg", "testInt", 32123, "A Test integer");
		cfg.addConfig("Test.cfg", "testDouble", Double.MAX_VALUE, "A double for test purposes.");
		cfg.readConfig();
		assertEquals("\\Hello\"World:?\\", cfg.getConfig("testString"));
		assertEquals(32123, (int) cfg.getConfig("testInt"));
		assertEquals(Double.MAX_VALUE, (double) cfg.getConfig("testDouble"), 0);
		// test the handling of changed values.
		File cfgFile = new File(cfgFolder, "Test.cfg");
		FileInputStream fIn = new FileInputStream(cfgFile);
		byte[] buffer = new byte[fIn.available()];
		fIn.read(buffer);
		fIn.close();
		String config = new String(buffer);
		config = config.replaceAll("World", "Pond").replaceAll("32123", "" + Integer.MAX_VALUE);
		FileOutputStream fOut = new FileOutputStream(cfgFile);
		fOut.write(config.getBytes());
		fOut.close();
		cfg.readConfig();
		assertEquals("\\Hello\"Pond:?\\", cfg.getConfig("testString"));
		assertEquals(Integer.MAX_VALUE, (int) cfg.getConfig("testInt"));
		// test the setting of values.
		cfg.setConfig("testDouble", Integer.MAX_VALUE / Math.PI);
		cfg.readConfig();
		assertEquals(Integer.MAX_VALUE / Math.PI, (double) cfg.getConfig("testDouble"), 0);
		// test the handling of another config file.
		cfg.addConfig("test.txt", "testString2", "Some random test String!", "Some random test String!");
		cfg.readConfig();
		assertEquals("Some random test String!", cfg.getConfig("testString2"));
		// test float handling.
		cfg.addConfig("test.txt", "floatTest", (float) Float.MAX_EXPONENT, "A float test.");
		cfg.readConfig();
		assertEquals(Float.MAX_EXPONENT, (float) cfg.getConfig("floatTest"), 0);
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
		fOut = new FileOutputStream(new File(cfgFolder, "wrong.cfg"));
		fOut.write("json: [123, 321]\nint: Test".getBytes());
		fOut.close();
		System.err.println(
				"The following ClassCastException and NumberFormatException are excepted to happen during this test, and not a problem!");
		cfg.readConfig();
		assertEquals(Integer.MIN_VALUE, (int) cfg.getConfig("int"));
		assertEquals(new JsonObject("key", "value"), cfg.getConfig("json"));
		// test handling of an empty config file.
		cfgFile.delete();
		cfgFile.createNewFile();
		cfg.readConfig();
		MatcherAssert.assertThat(cfgFile.length(), CoreMatchers.anyOf(CoreMatchers.is(535L), CoreMatchers.is(556L)));
		assertEquals(Integer.MAX_VALUE / Math.PI, cfg.getConfig("testDouble"), 0);
		// test config file deletion.
		cfg.delete();
		assertFalse("Deleting the config files failed!", cfgFile.exists());
	}

	@Test
	public void configWatcherTest() throws IOException, InterruptedException {
		// test the basics of the config watcher.
		File cfgFolder = tempFolder.newFolder("ToMe25s-Java-Utilities-Config-Test");
		final boolean[] changed = new boolean[] { false };
		Config cfg = new Config(true, cfgFolder, true, (file) -> changed[0] = true);
		cfg.addConfig("Watcher.cfg", "stringTest", "Some Random String",
				"A String that definitely wont get changed...");
		cfg.readConfig();
		assertEquals("Some Random String", cfg.getConfig("stringTest"));
		FileInputStream fIn = new FileInputStream(new File(cfgFolder, "Watcher.cfg"));
		byte[] buffer = new byte[fIn.available()];
		fIn.read(buffer);
		fIn.close();
		String config = new String(buffer);
		config = config.replace("Random", "Changed").replace("wont", "will");
		FileOutputStream fOut = new FileOutputStream(new File(cfgFolder, "Watcher.cfg"));
		Thread.sleep(100);// Wait for the ConfigWatcher to finish initializing.
		fOut.write(config.getBytes());
		fOut.flush();
		fOut.close();
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("mac") || os.contains("darwin")) {
			cfg.readConfig();
			System.out.println("Skipping ConfigWatcher test because it doesn't work in macos.");
		} else {
			// skip waiting for the config watcher, as that doesn't work on macos anyways.
			final int maxWaitTime = 10000;
			int waitTime = 0;
			while (!changed[0] && waitTime < maxWaitTime) {
				Thread.sleep(5);
				waitTime += 5;
			}
			Thread.sleep(10);
			assertTrue(
					String.format("The ConfigWatcher didn't detect any changes in the max allowed wait time of %ds."
							+ " This can sometimes just happen randomly. but thats rare.", maxWaitTime / 1000),
					changed[0]);
		}
		assertEquals("The string read from the file doesn't match.", "Some Changed String",
				cfg.getConfig("stringTest"));
		// test config watcher handling of files changed by Config.
		changed[0] = false;
		cfg.addConfig("Watcher.cfg", "intTest", 123, "A random integer here to test the ConfigWatcher");
		cfg.setConfig("intTest", Integer.MIN_VALUE);
		if (os.contains("mac") || os.contains("darwin")) {
			System.out.println("Skipping ConfigWatcher test because it doesn't work in macos.");
		} else {
			// skip waiting for the config watcher, as that doesn't work on macos anyways.
			final int maxWaitTime = 5000;
			int waitTime = 0;
			while (!changed[0] && waitTime < maxWaitTime) {
				Thread.sleep(5);
				waitTime += 5;
			}
			Thread.sleep(10);
			assertFalse(
					String.format("The ConfigWatcher detected changes after %dms, while it shouldn't have.", waitTime),
					changed[0]);
		}
		assertEquals("The integer read from the file doesn't match.", Integer.MIN_VALUE,
				(int) cfg.getConfig("intTest"));
		// test setting a bunch of config options quickly
		cfg.addConfig("Watcher.cfg", "doubleTest", 0.1, "Some double used for testing.");
		for (int i = 0; i < 10; i++) {
			cfg.setConfig("stringTest", "Some String " + i);
			cfg.setConfig("intTest", i);
			cfg.setConfig("doubleTest", 1.0 / (i + 1));
			assertEquals("The string read from the file doesn't match.", "Some String " + i,
					cfg.getConfig("stringTest"));
			assertEquals("The integer read from the file doesn't match.", i, (int) cfg.getConfig("intTest"));
			assertEquals("The double read from the file doesn't match.", 1.0 / (i + 1),
					(double) cfg.getConfig("doubleTest"), 0);
		}
		// delete config files.
		cfg.delete();
	}

}
