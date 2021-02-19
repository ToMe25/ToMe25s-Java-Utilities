package com.tome25.utils.tests;

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
	public void fastParsingTest() throws ParseException {
		// test everything possible in one string 50 times.
		String jsonString = "{\"string\":\"Test String?\",\"testString\":\"Test String!\\\"!51{}\\\\[]\",\"int\":5223156,\"int1\":312}";
		for (int i = 0; i < 50; i++) {
			JsonParser.parseStringFast(jsonString);
		}
	}

	/**
	 * Tests the speed of parsing the string from the fast parsing test with
	 * {@link JsonParser#parseString(String)} 50 times.
	 * 
	 * @throws ParseException if the parsing fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void comparisonTest() throws ParseException {
		// test normal parsing with the same string the fast parsing test uses 50 times.
		String jsonString = "{\"string\":\"Test String?\",\"testString\":\"Test String!\\\"!51{}\\\\[]\",\"int\":5223156,\"int1\":312}";
		for (int i = 0; i < 50; i++) {
			JsonParser.parseString(jsonString);
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
		for (int i = 0; i < 100; i++) {
			JsonParser.parseString(jsonString);
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
		for (int i = 0; i < 100; i++) {
			JsonParser.parseString(jsonString);
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
	}

	/**
	 * Tests adding 200 elements to a {@link JsonObject} and then cloning it 50
	 * times.
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
			json.clone();
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
			json.clone();
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
			json.contains("key" + i * 2);
		}
		// check for 100 not existing keys whether they are in the json object.
		for (int i = 0; i < 100; i++) {
			json.contains("key1" + i);
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
			json.contains("value" + i * 2);
		}
		// check for 100 not existing keys whether they are in the json object.
		for (int i = 0; i < 100; i++) {
			json.contains("value1" + i);
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
	 * Tests converting a 200 value {@link JsonObject} to a string 50 times. The
	 * toString tests are run less often because they take forever.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void objectToStringTest() {
		// add 200 objects to a json object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 200; i++) {
			json.add("key" + i, "value" + i);
		}
		// convert the json object to string 50 times.
		for (int i = 0; i < 50; i++) {
			json.toString();
		}
	}

	/**
	 * Tests converting a 200 value {@link JsonArray} to a string 50 times. The
	 * toString tests are run less often because they take forever.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 5000)
	public void arrayToStringTest() {
		// add 200 objects to a json array.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 200; i++) {
			json.add("value" + i);
		}
		// convert the json array to string 50 times.
		for (int i = 0; i < 50; i++) {
			json.toString();
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
			oIn.readObject();
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
			oIn.readObject();
		}
		// close oIn
		oIn.close();
	}

}
