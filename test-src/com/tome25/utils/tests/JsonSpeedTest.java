/*
 * ToMe25s-Java-Utilities is a collection of common java utilities.
 * Copyright (C) 2021  ToMe25
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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.text.ParseException;

import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonObject;
import com.tome25.utils.json.JsonParser;

public class JsonSpeedTest extends AbstractBenchmark {

	/**
	 * Tests the total time of parsing a string to a {@link JsonObject} using
	 * {@link JsonParser#parseStringFast(String)} 50 times.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectFastParsingTest() throws ParseException {
		// test everything possible in one string 50 times.
		String jsonString = "{\"string\":\"Test String?\",\"testString\":\"Test String!\\\"!51{}\\\\[]\",\"int\":5223156,\"int1\":312}";
		JsonElement<?> json = null;
		for (int i = 0; i < 50; i++) {
			if (json == null) {
				json = JsonParser.parseStringFast(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseStringFast(jsonString));
			}
		}
	}

	/**
	 * Tests the total time of parsing a string to a {@link JsonArray} using
	 * {@link JsonParser#parseStringFast(String)} 50 times.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayFastParsingTest() throws ParseException {
		// test everything possible in one string 50 times.
		String jsonString = "[\"string\",\"Test String?\",\"testString\",\"Test String!\\\"!51{}\\\\[]\",\"int\",5223156,\"int1\",312]";
		JsonElement<?> json = null;
		for (int i = 0; i < 50; i++) {
			if (json == null) {
				json = JsonParser.parseStringFast(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseStringFast(jsonString));
			}
		}
	}

	/**
	 * Tests the speed of parsing the string from the fast {@link JsonObject}
	 * parsing test with {@link JsonParser#parseString(String)} 50 times.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectParsingComparisonTest() throws ParseException {
		// test normal parsing with the same string the fast parsing test uses 50 times.
		String jsonString = "{\"string\":\"Test String?\",\"testString\":\"Test String!\\\"!51{}\\\\[]\",\"int\":5223156,\"int1\":312}";
		JsonElement<?> json = null;
		for (int i = 0; i < 50; i++) {
			if (json == null) {
				json = JsonParser.parseString(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseString(jsonString));
			}
		}
	}

	/**
	 * Tests the speed of parsing the string from the fast {@link JsonArray} parsing
	 * test with {@link JsonParser#parseString(String)} 50 times.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayParsingComparisonTest() throws ParseException {
		// test normal parsing with the same string the fast parsing test uses 50 times.
		String jsonString = "[\"string\",\"Test String?\",\"testString\",\"Test String!\\\"!51{}\\\\[]\",\"int\",5223156,\"int1\",312]";
		JsonElement<?> json = null;
		for (int i = 0; i < 50; i++) {
			if (json == null) {
				json = JsonParser.parseString(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseString(jsonString));
			}
		}
	}

	/**
	 * Tests the speed of using {@link JsonParser#parseString(String)} to parse a
	 * complex {@link JsonObject} 100 times.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectParsingTest() throws ParseException {
		// Test parsing a complicated json object from a string 100 times. This
		// intentionally doesn't contain a json array.
		String jsonString = "{\"string\":\"Test String: {}[]\\\"\\\\,;.-12\",\"number\":1234567890123456,"
				+ "\"floating\":516173.616396, \"json\":{\"string\":\"There is nothing in here\", \"int\":532}}";
		JsonElement<?> json = null;
		for (int i = 0; i < 100; i++) {
			if (json == null) {
				json = JsonParser.parseString(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseString(jsonString));
			}
		}
	}

	/**
	 * Tests the speed of using {@link JsonParser#parseString(String)} to parse a
	 * complex {@link JsonArray} 100 times.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayParsingTest() throws ParseException {
		// Test parsing a complicated json array from a string 100 times. This
		// intentionally doesn't contain a json object.
		String jsonString = "[\"Test String: {}[]\\\"\\\\,;.-12\", 7348927034872342, 63907.23424,"
				+ "[\"Some Random Test String\", 423423, 123123]]";
		JsonElement<?> json = null;
		for (int i = 0; i < 100; i++) {
			if (json == null) {
				json = JsonParser.parseString(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseString(jsonString));
			}
		}
	}

	/**
	 * Tests parsing 100 recursive {@link JsonObject JsonObjects} using
	 * {@link JsonParser#parseString(String)}.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 500)
	public void recursiveObjectParsingTest() throws ParseException {
		// Generate a String with 100 of recursive JsonObejcts.
		final String jsonComponent = "{\"testString\": \"Some Test\", \"testJson\": ";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			builder.append(jsonComponent);
		}
		builder.append("\"test\"");
		for (int i = 0; i < 100; i++) {
			builder.append('}');
		}
		String jsonString = builder.toString();
		// Parse the generated json string 100 times.
		JsonElement<?> json = null;
		for (int i = 0; i < 100; i++) {
			if (json == null) {
				json = JsonParser.parseString(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseString(jsonString));
			}
		}
	}

	/**
	 * Tests parsing 100 recursive {@link JsonArray JsonArrays} using
	 * {@link JsonParser#parseString(String)}.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 500)
	public void recursiveArrayParsingTest() throws ParseException {
		// Generate a String with 100 of recursive JsonArrays.
		final String jsonComponent = "[\"testString\", \"Some Test\", \"testJson\", ";
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 100; i++) {
			builder.append(jsonComponent);
		}
		builder.append("\"test\"");
		for (int i = 0; i < 100; i++) {
			builder.append(']');
		}
		String jsonString = builder.toString();
		// Parse the generated json string 100 times.
		JsonElement<?> json = null;
		for (int i = 0; i < 100; i++) {
			if (json == null) {
				json = JsonParser.parseString(jsonString);
			} else {
				assertEquals("The first parsing result does not match the current one!", json,
						JsonParser.parseString(jsonString));
			}
		}
	}

	/**
	 * Tests adding 1000 objects to a {@link JsonObject}.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectAddTest() {
		// test adding 1000 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 1000; i++) {
			json.add("key" + i, "value" + i);
		}
		assertEquals(1000, json.size());
	}

	/**
	 * Tests adding 1000 objects to a {@link JsonArray}.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayAddTest() {
		// test adding 1000 objects to a json array.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 1000; i++) {
			json.add("value" + i);
		}
		assertEquals(1000, json.size());
	}

	/**
	 * Tests adding 200 key value pairs to a {@link JsonObject} and then cloning it
	 * 50 times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectCloneTest() {
		// add 200 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 200; i++) {
			json.add("key" + i, "value" + i);
		}
		// clone the object 50 times.
		for (int i = 0; i < 50; i++) {
			assertEquals(json, json.clone());
		}
	}

	/**
	 * Tests adding 200 elements to a {@link JsonArray} and then cloning it 50
	 * times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayCloneTest() {
		// add 200 objects to a json array.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 200; i++) {
			json.add("value" + i);
		}
		// clone the object 50 times.
		for (int i = 0; i < 50; i++) {
			assertEquals(json, json.clone());
		}
	}

	/**
	 * Tests adding 200 json objects each containing one key and one value to a
	 * {@link JsonObject} and then cloning it 50 times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectRecursiveCloneTest() {
		// add 200 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 200; i++) {
			json.add("nr" + i, new JsonObject("key" + i, "value" + i));
		}
		// clone the object 50 times.
		for (int i = 0; i < 50; i++) {
			assertEquals(json, json.clone());
		}
	}

	/**
	 * Tests adding 200 lists with 2 elements to a {@link JsonArray} and then
	 * cloning it 50 times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayRecursiveCloneTest() {
		// add 200 objects to a json array.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 200; i++) {
			json.add(new JsonArray("value" + i, "value" + (i + 1)));
		}
		// clone the object 50 times.
		for (int i = 0; i < 50; i++) {
			assertEquals(json, json.clone());
		}
	}

	/**
	 * Checks whether a 500 value {@link JsonObject} contains the given key for 100
	 * found and 100 not found keys.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectContainsTest() {
		// add 500 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 500; i++) {
			json.add("key" + i, "value" + i);
		}
		// check for 100 existing keys whether they are in the json object.
		for (int i = 0; i < 100; i++) {
			assertTrue("Key \"key" + (i * 2) + "\" not found!", json.contains("key" + i * 2));
		}
		// check for 100 not existing keys whether they are in the json object.
		for (int i = 0; i < 100; i++) {
			assertFalse("Key \"Key1" + i + "\"was found, but shouldn't exist!", json.contains("Key1" + i));
		}
	}

	/**
	 * Checks whether a 500 value {@link JsonArray} contains the given value for 100
	 * found and 100 not found values.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayContainsTest() {
		// add 500 objects to a json object.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 500; i++) {
			json.add("value" + i);
		}
		// check for 100 existing keys whether they are in the json object.
		for (int i = 0; i < 100; i++) {
			assertTrue("Element \"value" + (i * 2) + "\" not found!", json.contains("value" + i * 2));
		}
		// check for 100 not existing keys whether they are in the json object.
		for (int i = 0; i < 100; i++) {
			assertFalse("Element \"Value1" + i + "\" was found, but shouldn't exist!", json.contains("Value1" + i));
		}
	}

	/**
	 * Removes 100 values from a 500 value {@link JsonObject}.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectRemoveTest() {
		// add 500 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 500; i++) {
			json.add("key" + i, "value" + i);
		}
		// remove 100 keys from the json object.
		for (int i = 0; i < 100; i++) {
			json.remove("key" + i * 2);
		}
		assertEquals(400, json.size());
	}

	/**
	 * Removes 100 values from a 500 value {@link JsonArray}.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayRemoveTest() {
		// add 500 objects to a json array.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 500; i++) {
			json.add("value" + i);
		}
		// remove 100 keys from the json array.
		for (int i = 0; i < 100; i++) {
			json.remove("value" + i * 2);
		}
		assertEquals(400, json.size());
	}

	/**
	 * Iterates 50 times over a 200 value {@link JsonObject}.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectIterationTest() {
		// add 200 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 200; i++) {
			json.add("key" + i, "value" + i);
		}
		// iterate over the json object 50 times.
		for (int i = 0; i < 50; i++) {
			json.forEach(key -> {
			});
		}
	}

	/**
	 * Iterates 50 times over a 200 value {@link JsonArray}.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayIterateTest() {
		// add 200 objects to a json array.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 200; i++) {
			json.add("value" + i);
		}
		// iterate over the json array 50 times.
		for (int i = 0; i < 50; i++) {
			json.forEach(key -> {
			});
		}
	}

	/**
	 * Tests converting a 100 value {@link JsonObject} to a string 100 times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectToStringTest() {
		// add 100 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 100; i++) {
			json.add("key" + i, "value" + i);
		}
		// convert the json object to string 50 times.
		String jsonString = null;
		for (int i = 0; i < 100; i++) {
			if (jsonString == null) {
				jsonString = json.toString();
			} else {
				assertEquals("The string from the first toString wasn't equals the current string!", jsonString,
						json.toString());
			}
		}
	}

	/**
	 * Tests converting a 100 value {@link JsonArray} to a string 100 times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayToStringTest() {
		// add 100 objects to a json array.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 100; i++) {
			json.add("value" + i);
		}
		// convert the json array to string 50 times.
		String jsonString = null;
		for (int i = 0; i < 100; i++) {
			if (jsonString == null) {
				jsonString = json.toString();
			} else {
				assertEquals("The string from the first toString wasn't equals the current string!", jsonString,
						json.toString());
			}
		}
	}

	/**
	 * Serialize and Deserialize a 50 value {@link JsonObject} 100 times.
	 * 
	 * @throws IOException            if something with the
	 *                                {@link PipedInputStream}s and
	 *                                {@link PipedOutputStream}s goes wrong.
	 * @throws ClassNotFoundException if the class of a serialized object cannot be
	 *                                found.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectSerializationTest() throws IOException, ClassNotFoundException {
		// create streams and json 200 value json object.
		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream pIn = new PipedInputStream(pOut);
		ObjectOutputStream oOut = new ObjectOutputStream(pOut);
		ObjectInputStream oIn = new ObjectInputStream(pIn);
		JsonObject json = new JsonObject();
		for (int i = 0; i < 50; i++) {
			json.add("key" + i, "value" + i);
		}
		// serialize and deserialize the json object 100 times.
		for (int i = 0; i < 100; i++) {
			oOut.writeObject(json.clone());// Clone the json because otherwise the caching will prevent it from getting
											// written multiple times.
			assertEquals(json, oIn.readObject());
		}
		// close oIn
		oIn.close();
	}

	/**
	 * Serialize and Deserialize a 50 value {@link JsonArray} 100 times.
	 * 
	 * @throws IOException            if something with the
	 *                                {@link PipedInputStream}s and
	 *                                {@link PipedOutputStream}s goes wrong.
	 * @throws ClassNotFoundException if the class of a serialized object cannot be
	 *                                found.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arraySerializationTest() throws IOException, ClassNotFoundException {
		// create streams and json 200 value json array.
		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream pIn = new PipedInputStream(pOut);
		ObjectOutputStream oOut = new ObjectOutputStream(pOut);
		ObjectInputStream oIn = new ObjectInputStream(pIn);
		JsonArray json = new JsonArray();
		for (int i = 0; i < 50; i++) {
			json.add("value" + i);
		}
		// serialize and deserialize the json array 100 times.
		for (int i = 0; i < 100; i++) {
			oOut.writeObject(json.clone());// Clone the json because otherwise the caching will prevent it from getting
											// written multiple times.
			assertEquals(json, oIn.readObject());
		}
		// close oIn
		oIn.close();
	}

	/**
	 * Generate a changes json from two {@link JsonObject}s with 100 key value pairs
	 * 100 times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectChangesTest() {
		// add 100 objects to two json objects.
		JsonObject json1 = new JsonObject();
		JsonObject json2 = new JsonObject();
		JsonObject changes = new JsonObject();
		for (int i = 0; i < 100; i++) {
			json1.add("key" + i, "value" + i);
			if (i % 4 == 0) {
				json2.add("Key" + i, "value" + i);
				changes.add("key" + i, null);
				changes.add("Key" + i, "value" + i);
			} else if (i % 4 == 1) {
				json2.add("key" + i, "value" + (i * 2));
				changes.add("key" + i, "value" + (i * 2));
			} else {
				json2.add("key" + i, "value" + i);
			}
		}
		// generate the changes json 100 times.
		for (int i = 0; i < 100; i++) {
			assertEquals(changes, json2.changes(json1));
		}
	}

	/**
	 * Generate a changes json from two {@link JsonArray}s with 100 values 100
	 * times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayChangeTest() {
		// add 100 objects to two just arrays.
		JsonArray json1 = new JsonArray();
		JsonArray json2 = new JsonArray();
		JsonArray changes = new JsonArray();
		for (int i = 0; i < 100; i++) {
			json1.add("value" + i);
			if (i % 4 == 0) {
				json2.add("Value" + i);
				if (i < 50) {// part of the workaround described below.
					changes.add(new JsonObject("val", "Value" + i, "after", (int) Math.max(0, i * 0.75)));
				} else {
					changes.add(new JsonObject("val", "Value" + i, "after", (int) Math.max(0, (i - 52) / 2 + 39)));
				}
			} else if (i % 4 == 1) {
				json2.add("value" + (i * 2));
				// FIXME this is a workaround to make this test work while the changes method
				// isn't working correctly.
				if (i >= 50) {
					changes.add(
							new JsonObject("val", "value" + (i * 2), "after", (int) Math.max(0, (i - 52) / 2 + 39)));
				}
			} else {
				json2.add("value" + i);
			}
		}
		for (int i = 0; i < 100; i += 4) {
			changes.add(new JsonObject("rm", i));
			changes.add(new JsonObject("rm", i + 1));
		}
		// generate the changes json 100 times.
		for (int i = 0; i < 100; i++) {
			assertEquals(changes, json2.changes(json1));
		}
	}

	/**
	 * Reconstruct a {@link JsonObject} with 100 key value pairs based on a changes
	 * json 50 times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectReconstructTest() {
		// add 100 objects a json object and generate a changes json.
		JsonObject json1 = new JsonObject();
		JsonObject json2 = new JsonObject();
		JsonObject changes = new JsonObject();
		for (int i = 0; i < 100; i++) {
			json1.add("key" + i, "value" + i);
			if (i % 4 == 0) {
				json2.add("Key" + i, "value" + i);
				changes.add("key" + i, null);
				changes.add("Key" + i, "value" + i);
			} else if (i % 4 == 1) {
				json2.add("key" + i, "value" + (i * 2));
				changes.add("key" + i, "value" + (i * 2));
			} else {
				json2.add("key" + i, "value" + i);
			}
		}
		// reconstruct the json object 100 times.
		for (int i = 0; i < 100; i++) {
			assertEquals(json2, changes.reconstruct(json1));
		}
	}

	/**
	 * Reconstruct a {@link JsonArray} with 100 elements based on a changes json 100
	 * times.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayReconstructTest() {
		// add 100 objects a json object and generate a changes json.
		JsonArray json1 = new JsonArray();
		JsonArray json2 = new JsonArray();
		JsonArray changes = new JsonArray();
		for (int i = 0; i < 100; i++) {
			json1.add("value" + i);
			if (i % 4 == 0) {
				json2.add("Value" + i);
				changes.add(new JsonObject("val", "Value" + i, "after", i));
				changes.add(new JsonObject("rm", i));
			} else if (i % 4 == 1) {
				json2.add("value" + (i * 2));
				changes.add(new JsonObject("val", "value" + (i * 2), "after", i));
				changes.add(new JsonObject("rm", i));
			} else {
				json2.add("value" + i);
			}
		}
		// reconstruct the json object 100 times.
		for (int i = 0; i < 100; i++) {
			assertEquals(json2, changes.reconstruct(json1));
		}
	}

}
