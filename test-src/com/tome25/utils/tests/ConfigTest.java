package com.tome25.utils.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.tome25.utils.config.Config;

public class ConfigTest {

	@Test
	public void configTest() throws IOException {
		// test the basics of the config system.
		File cfgFile = new File(".", "Config");
		Config cfg = new Config(cfgFile, false);
		cfg.addConfig("Test.cfg", "testString", "\\Hello\"World:?\\",
				"This is a Test config option to test the Config class.");
		cfg.addConfig("Test.cfg", "testInt", 32123, "A Test integer");
		cfg.addConfig("Test.cfg", "testDouble", Double.MAX_VALUE, "A double for test purposes.");
		cfg.readConfig();
		assertEquals("\\Hello\"World:?\\", cfg.getConfig("testString"));
		assertEquals(32123, cfg.getConfig("testInt"));
		assertEquals(Double.MAX_VALUE, cfg.getConfig("testDouble"));
		// test the handling of changed values.
		FileInputStream fIn = new FileInputStream(new File(cfgFile.getName(), "Test.cfg"));
		byte[] buffer = new byte[fIn.available()];
		fIn.read(buffer);
		fIn.close();
		String config = new String(buffer);
		config = config.replaceAll("World", "Pond").replaceAll("32123", "" + Integer.MAX_VALUE);
		FileOutputStream fOut = new FileOutputStream(new File(cfgFile.getName(), "Test.cfg"));
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
		cfg.delete();
	}

}
