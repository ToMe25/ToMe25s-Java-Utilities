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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;

import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonObject;
import com.tome25.utils.json.JsonParser;

public class JsonTest extends AbstractBenchmark {

	/**
	 * A speed and function test for the fast json
	 * {@link JsonParser#parseStringFast(String) parsing algorithm}.
	 * 
	 * @throws ParseException if the parsing somehow fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 50, benchmarkRounds = 1000)
	public void fastParsingTest() throws ParseException {
		// test whether the basic most things work
		String jsonString = "{\"testString\":\"parsing test\",\"testInt\":123}";
		JsonObject json = new JsonObject("testString", "parsing test");
		json.put("testInt", 123);
		assertEquals(jsonString, json.toString());
		// test whether the fast parser works
		JsonElement<?> parsedJson = JsonParser.parseStringFast(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with double quotes at the end as that was a problem in
		// the past
		json.add("quoteTest", "double quotes:\"");
		jsonString = json.toString();
		parsedJson = JsonParser.parseStringFast(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with a backslash at the end as that was a problem in the
		// past
		json.add("backslashTest", "backslash:\\");
		jsonString = json.toString();
		parsedJson = JsonParser.parseStringFast(jsonString);
		assertEquals(json, parsedJson);
	}

	/**
	 * Tests the normal json {@link JsonParser#parseString(String) parsing
	 * algorithm} as much as possible. Should fail if the json parsing somehow fails
	 * to work correctly.
	 * 
	 * @throws ParseException               if the parsing of something fails.
	 * @throws UnsupportedEncodingException if converting a string to a byte array
	 *                                      with the UTF-8 encoding fails.
	 */
	@Test
	public void parsingTest() throws ParseException, UnsupportedEncodingException {
		// test whether the basic most things work
		String jsonString = "{\"testString\":\"Just a simple Test\",\"testInt\":51223,\"testJson\":{\"simple\":\"json\"}}";
		JsonObject simpleJson = new JsonObject("simple", "json");
		JsonObject json = new JsonObject("testString", "Just a simple Test");
		json.add("testInt", 51223);
		json.add("testJson", simpleJson);
		assertEquals(jsonString, json.toString());
		// test the parser with some basic things
		JsonElement<?> parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test other numeric values
		json.put("testDouble", 123.45);
		json.put("testLong", Integer.MAX_VALUE * 2l);
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test some special characters
		json.add("characterTest", "^°!\"§$%&/()=?`´@ł€¶ŧ←↓→øþ+*~#'’<>|,;·.:…-_–");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with double quotes at the end as that was a problem in
		// the past
		json.add("quoteTest", "double quotes:\"");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test a string ending with a backslash at the end as that was a problem in the
		// past
		json.add("backslashTest", "backslash:\\");
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test null handling of json objects
		json.add("nullTest", null);
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test basic json array functions
		String jsonArrayString = "[1,\"test\",531.12,{\"simple\":\"json\"}]";
		JsonArray jsonArray = new JsonArray(1, "test", 531.12, simpleJson);
		assertEquals(jsonArrayString, jsonArray.toString());
		// test the parser with arrays
		JsonElement<?> parsedJsonArray = JsonParser.parseString(jsonArrayString);
		assertEquals(jsonArray, parsedJsonArray);
		// test json array order
		assertEquals(jsonArray.get(2), parsedJsonArray.get(2));
		// test json array null handling
		((JsonArray) jsonArray).add(null);
		jsonArrayString = jsonArray.toString();
		parsedJsonArray = JsonParser.parseString(jsonArrayString);
		assertEquals(jsonArray, parsedJsonArray);
		// test json array subjson parsing
		assertEquals(simpleJson, parsedJsonArray.get(3));
		// test parsing json objects containing arrays
		json.add("array", jsonArray.clone());
		jsonString = json.toString();
		parsedJson = JsonParser.parseString(jsonString);
		assertEquals(json, parsedJson);
		// test the parsed array inside the json object
		assertEquals(parsedJson.get("array"), jsonArray);
		// test parsing arrays with complex jsons inside
		((JsonArray) jsonArray).add(json);
		jsonArrayString = jsonArray.toString();
		parsedJsonArray = JsonParser.parseString(jsonArrayString);
		assertEquals(jsonArray, parsedJsonArray);
		// test char array parsing
		char[] jsonCharArray = jsonString.toCharArray();
		parsedJson = JsonParser.parseCharArray(jsonCharArray);
		assertEquals(json, parsedJson);
		// test char array parsing for json arrays
		char[] jsonArrayCharArray = jsonArrayString.toCharArray();
		parsedJsonArray = JsonParser.parseCharArray(jsonArrayCharArray);
		assertEquals(jsonArray, parsedJsonArray);
		// test byte array parsing
		byte[] jsonByteArray = jsonString.getBytes("UTF-8");
		parsedJson = JsonParser.parseByteArray(jsonByteArray, "UTF-8");
		assertEquals(json, parsedJson);
		// test json array byte array parsing
		byte[] jsonArrayByteArray = jsonArrayString.getBytes("UTF-8");
		parsedJsonArray = JsonParser.parseByteArray(jsonArrayByteArray, "UTF-8");
		assertEquals(jsonArray, parsedJsonArray);
		// test json array order after parsing
		jsonArrayString = jsonArray.toString();
		parsedJsonArray = JsonParser.parseString(jsonArrayString);
		assertEquals(((JsonArray) jsonArray).getFirst(), ((JsonArray) parsedJsonArray).getFirst());
		assertEquals(((JsonArray) jsonArray).get(2), ((JsonArray) parsedJsonArray).get(2));
		assertEquals(((JsonArray) jsonArray).getLast(), ((JsonArray) parsedJsonArray).getLast());
		// test parsing a string starting and ending with spaces
		jsonString = "    {\"test\": " + jsonString + "}  ";
		parsedJson = JsonParser.parseString(jsonString);
		jsonString = jsonString.replaceAll(": ", ":").replaceAll("  ", "");
		assertEquals(jsonString, parsedJson.toString());
	}

	/**
	 * Tests the cloning of {@link JsonObject}s and {@link JsonArray}s.
	 */
	@Test
	public void cloningTest() {
		// test the basics of the json object clone function
		JsonObject simpleJson = new JsonObject("simple", "json");
		JsonObject json = new JsonObject("testString", "test");
		json.add("testJson", simpleJson);
		JsonObject clonedJson = json.clone();
		assertEquals(json, clonedJson);
		// test that the clone isn't the same instance as the original
		assertFalse(json == clonedJson);
		// test that the subjsons get cloned too
		assertFalse(simpleJson == clonedJson.get("testJson"));
		assertEquals(simpleJson, clonedJson.get("testJson"));
		// test cloning not recursively
		clonedJson = json.clone(false);
		assertTrue(simpleJson == clonedJson.get("testJson"));
		// test the basics of the json array clone function
		JsonArray jsonArray = new JsonArray(1, "test", 531.12, simpleJson);
		JsonArray clonedJsonArray = jsonArray.clone();
		assertEquals(jsonArray, clonedJsonArray);
		// test that the clone isn't the same instance as the original
		assertFalse(jsonArray == clonedJsonArray);
		// test that the subjsons get cloned too
		assertFalse(simpleJson == clonedJsonArray.get(3));
		assertEquals(simpleJson, clonedJsonArray.get(3));
		// test cloning not recursively
		clonedJsonArray = jsonArray.clone(false);
		assertTrue(simpleJson == clonedJsonArray.get(3));
	}

	/**
	 * Tests the deduplication and reconstuction algorithms of {@link JsonObject}s
	 * and {@link JsonArray}s.
	 */
	@Test
	public void deduplcationTest() {
		// test the basic deduplication functionality
		JsonObject json1 = new JsonObject("stringTest", "String Test");
		json1.add("longTest", Integer.MAX_VALUE * 5l);
		json1.add("doubleTest", 654.321);
		JsonObject json2 = json1.clone();
		json2.remove("doubleTest");
		json2.add("intTest", 468);
		JsonObject deduplicatedJson = new JsonObject("doubleTest", null);
		deduplicatedJson.add("intTest", 468);
		assertEquals(deduplicatedJson, json2.changes(json1));
		// test reconstruction
		JsonObject reconstructedJson = deduplicatedJson.reconstruct(json1);
		assertEquals(json2, reconstructedJson);
		// test deduplication of subjsons
		json1.add("jsonTest", json2);
		json2 = json1.clone();
		((JsonObject) json2.get("jsonTest")).put("stringTest", "Another Test String");
		deduplicatedJson = new JsonObject("jsonTest", new JsonObject("stringTest", "Another Test String"));
		assertEquals(deduplicatedJson, json2.changes(json1));
		// test reconstruction of subjsons
		reconstructedJson = deduplicatedJson.reconstruct(json1);
		assertEquals(json2, reconstructedJson);
		// test cloning of subjsons with recursive deduplication disabled
		deduplicatedJson = json2.changes(json1, false);
		assertFalse(json2.get("jsonTest") == deduplicatedJson.get("jsonTest"));
		assertEquals(json2.get("jsonTest"), deduplicatedJson.get("jsonTest"));
		// test not recursive reconstruction
		reconstructedJson = deduplicatedJson.reconstruct(json1, false);
		assertEquals(json2, reconstructedJson);
		// test deduplication and reconstruction of a json containing a json array
		JsonArray jsonArray = new JsonArray("array", 123);
		json1.add("arrayTest", jsonArray);
		jsonArray = jsonArray.clone();
		jsonArray.addAll(Integer.MAX_VALUE * 2l, "test");
		json2.add("arrayTest", jsonArray);
		deduplicatedJson = json2.changes(json1);
		reconstructedJson = deduplicatedJson.reconstruct(json1);
		assertEquals(json2, reconstructedJson);
		// test deduplication of jsons if an entire subjson got removed
		((JsonObject) json1.get("jsonTest")).put("stringTest", "Another Test String");
		json2 = json1.clone();
		json2.remove("jsonTest");
		deduplicatedJson = json2.changes(json1);
		reconstructedJson = deduplicatedJson.reconstruct(json1);
		assertEquals(json2, reconstructedJson);
		// test basic deduplication of json arrays
		JsonArray jsonArray1 = new JsonArray(json1.values());
		JsonArray jsonArray2 = jsonArray1.clone();
		jsonArray2.remove(3);
		jsonArray2.add(1, "test");
		JsonArray deduplicatedJsonArray = jsonArray2.changes(jsonArray1);
		assertNotEquals(jsonArray2, deduplicatedJsonArray);
		JsonArray reconstructedJsonArray = deduplicatedJsonArray.reconstruct(jsonArray1);
		assertEquals(jsonArray2, reconstructedJsonArray);
		// test deduplication of json arrays with a changed subjson
		jsonArray1.add(0, "test");
		jsonArray2 = jsonArray1.clone();
		((JsonObject) jsonArray2.get(1)).set("longTest", 123);
		deduplicatedJsonArray = jsonArray2.changes(jsonArray1);
		reconstructedJsonArray = deduplicatedJsonArray.reconstruct(jsonArray1);
		assertEquals(jsonArray2, reconstructedJsonArray);
	}

	/**
	 * Test the serialization and deserialization of {@link JsonObject}s and
	 * {@link JsonArray}s.
	 * 
	 * @throws IOException            if something with the
	 *                                {@link PipedInputStream}s and
	 *                                {@link PipedOutputStream}s goes wrong.
	 * @throws ClassNotFoundException if the class of a serialized object cannot be
	 *                                found.
	 */
	@Test
	public void serializationTest() throws IOException, ClassNotFoundException {
		// test basic serialization and deserialization
		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream pIn = new PipedInputStream(pOut);
		ObjectOutputStream oOut = new ObjectOutputStream(pOut);
		ObjectInputStream oIn = new ObjectInputStream(pIn);
		JsonObject json = new JsonObject("stringTest", "Test String &2$?");
		json.add("longTest", Integer.MAX_VALUE * 2l);
		json.add("jsonTest", json.clone());
		json.remove("longTest", true);
		oOut.writeObject(json.clone());// Clone the json because otherwise the caching will prevent it from getting
										// written multiple times.
		Object deserialzedJson = oIn.readObject();
		assertEquals(json, deserialzedJson);
		// test some more string stuff
		json.add("backslashTest", "Test\\");
		json.add("newlineTest", "Newline\nTest");
		oOut.writeObject(json.clone());// Clone the json because otherwise the caching will prevent it from getting
										// written multiple times.
		deserialzedJson = oIn.readObject();
		assertEquals(json, deserialzedJson);
		// test json array serialization
		JsonArray jsonArray = new JsonArray(json.values());
		jsonArray.addAll(123, 456, 789);
		oOut.writeObject(jsonArray.clone());// Clone the json because otherwise the caching will prevent it from getting
											// written multiple times.
		Object deserializedJsonArray = oIn.readObject();
		assertEquals(jsonArray, deserializedJsonArray);
		// close oIn
		oIn.close();
	}

	/**
	 * Tests the comparison of {@link JsonObject}s to other {@link JsonObject}s.
	 * Also tests {@link JsonArray}s.
	 */
	@Test
	public void compareTest() {
		// test json object comparison
		JsonObject json1 = new JsonObject("testString", "Some Random String!");
		json1.add("testInt", 321456);
		json1.add("testJson", json1.clone());
		JsonObject json2 = json1.clone();
		json2.put("testInt", 2);
		assertNotEquals(0, json1.compareTo(json2));
		assertEquals(json1.compareTo(json2), -json2.compareTo(json1));
		// test comparing two equal json objects
		json1.add("testLong", Long.MIN_VALUE);
		json2 = json1.clone();
		assertEquals(0, json1.compareTo(json2));
		// test comparing json arrays
		JsonArray jsonArray1 = new JsonArray("testString", json1, 321234, "TesT StrinG");
		JsonArray jsonArray2 = jsonArray1.clone();
		jsonArray2.add("test");
		assertNotEquals(0, jsonArray1.compareTo(jsonArray2));
		assertEquals(jsonArray1.compareTo(jsonArray2), -jsonArray2.compareTo(jsonArray1));
		// test cross comparison
		assertNotEquals(0, json1.compareTo(jsonArray2));
		assertEquals(json1.compareTo(jsonArray2), -jsonArray2.compareTo(json1));
	}

	/**
	 * Tests whether {@link JsonParser#parseString(String)} throws the correct
	 * exceptions everywhere it should.
	 */
	@Test
	public void parsingExceptionTest() {
		// test missing start bracket
		try {
			JsonParser.parseString("\"test\": \"test\"}");
		} catch (ParseException e) {
			assertEquals("Parsing a Json without starting bracket returned invalid error offset!", 0,
					e.getErrorOffset());
		}
		// test missing key in a json object
		try {
			JsonParser.parseString("{123}");
		} catch (ParseException e) {
			assertEquals("Parsing a Json Object with a missing key returned invalid error offset!", 1,
					e.getErrorOffset());
		}
		// test a key in a json array
		try {
			JsonParser.parseString("[\"testKey\": \"random value\"]");
		} catch (ParseException e) {
			assertEquals("Parsing a Json Array with a key value pair returned invalid error offset!", 10,
					e.getErrorOffset());
		}
		// test json parsing with invalid value type
		try {
			JsonParser.parseString("{\"testString\": \"testStr\", \"test\": test}");
		} catch (ParseException e) {
			assertEquals("Parsing a Json with unknown value type returned invalid error offset!", 34,
					e.getErrorOffset());
		}
		// test missing json object end bracket
		try {
			JsonParser.parseString("{\"testInt\": 321, \"testString\": \"test\"");
		} catch (ParseException e) {
			assertEquals("Parsing a Json Object without ending bracket returned invalid error offset!", 37,
					e.getErrorOffset());
		}
		// test missing json array end bracket
		try {
			JsonParser.parseString("[123, \"testString\", \"test\"");
		} catch (ParseException e) {
			assertEquals("Parsing a Json Array without ending bracket returned invalid error offset!", 26,
					e.getErrorOffset());
		}
		// test a subjson with a missing end bracket
		try {
			JsonParser.parseString(
					"[\"test\", {\"testString\": \"something\", \"testInt\": 123}, {\"something\": \"or other\"]");
		} catch (ParseException e) {
			assertEquals(
					"Parsing a JsonArray containing a JsonObject with a missing end bracket returned invalid error offset!",
					24, e.getErrorOffset());
		}
		// test json object ending with square bracket
		try {
			JsonParser.parseString("{\"test\": \"test\"]");
		} catch (ParseException e) {
			assertEquals("Parsing a Json without starting bracket returned invalid error offset!", 15,
					e.getErrorOffset());
		}
		// test json array ending with curly bracket
		try {
			JsonParser.parseString("[\"test\", \"test\"}");
		} catch (ParseException e) {
			assertEquals("Parsing a Json without starting bracket returned invalid error offset!", 15,
					e.getErrorOffset());
		}
	}

	/**
	 * Tests the speed of executing various actions on a {@link JsonObject}.
	 * 
	 * @throws ParseException if the parsing in this speed test fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 800)
	public void jsonObjectSpeedTest() throws ParseException {
		// Add 200 objects to a Json Object.
		JsonObject json = new JsonObject();
		for (int i = 0; i < 200; i++) {
			if (i % 5 == 0) {
				json.add("string" + i, "Random string number " + i);
			} else if (i % 5 == 1) {
				json.add("int" + i, i * 2);
			} else if (i % 5 == 2) {
				json.add("long" + i, ((long) i) * Integer.MAX_VALUE);
			} else if (i % 5 == 3) {
				json.add("double" + i, i * Math.PI);
			} else {
				json.add("json" + i, new JsonObject("testString", "Some random stinrg with number " + i));
			}
		}
		// Iterate over the json object 50 times.
		for (int i = 0; i < 50; i++) {
			json.forEach((k, v) -> {
				// Do something to prevent the compiler from removing this.
				if (k == null || v == null) {// this should never happen!
					throw new NullPointerException();
				}
			});
		}
		// Get 100 objects.
		for (int i = 0; i < 100; i++) {
			Object value;
			if (i % 5 == 0) {
				value = json.get("string" + i);
			} else if (i % 5 == 1) {
				value = json.remove("int" + i);
			} else if (i % 5 == 2) {
				value = json.remove("long" + i);
			} else if (i % 5 == 3) {
				value = json.remove("double" + i);
			} else {
				value = json.remove("json" + i);
			}
			assertNotEquals(value, null);
		}
		// Remove 50 objects.
		for (int i = 0; i < 50; i++) {
			if (i % 5 == 0) {
				json.remove("string" + i);
			} else if (i % 5 == 1) {
				json.remove("int" + i);
			} else if (i % 5 == 2) {
				json.remove("long" + i);
			} else if (i % 5 == 3) {
				json.remove("double" + i);
			} else {
				json.remove("json" + i);
			}
		}
		// Clone the json object 50 times.
		for (int i = 0; i < 50; i++) {
			json = json.clone();
		}
		// Convert json to string and parse it again 10 times.
		for (int i = 0; i < 10; i++) {
			json = (JsonObject) JsonParser.parseString(json.toString());
		}
	}

	/**
	 * Tests the speed of executing various actions on a {@link JsonArray}.
	 * 
	 * @throws ParseException if the parsing in this speed test fails.
	 */
	@Test
	@BenchmarkOptions(warmupRounds = 100, benchmarkRounds = 800)
	public void jsonArraySpeedTest() throws ParseException {
		// Add 200 objects to a Json Object.
		JsonArray json = new JsonArray();
		for (int i = 0; i < 200; i++) {
			if (i % 5 == 0) {
				json.add("Random string number " + i);
			} else if (i % 5 == 1) {
				json.add(i * 2);
			} else if (i % 5 == 2) {
				json.add(((long) i) * Integer.MAX_VALUE);
			} else if (i % 5 == 3) {
				json.add(i * Math.PI);
			} else {
				json.add(new JsonObject("testString", "Some random stinrg with number " + i));
			}
		}
		// Iterate over the json object 50 times.
		for (int i = 0; i < 50; i++) {
			json.forEach(v -> {
				// Do something to prevent the compiler from removing this.
				if (v == null) {// this should never happen!
					throw new NullPointerException();
				}
			});
		}
		// Get 100 objects.
		for (int i = 0; i < 100; i++) {
			Object value = json.get(i);
			assertNotEquals(value, null);
		}
		// Remove 50 objects.
		for (int i = 0; i < 50; i++) {
			if (i % 5 == 0) {
				json.remove("string" + i);
			} else if (i % 5 == 1) {
				json.remove("int" + i);
			} else if (i % 5 == 2) {
				json.remove("long" + i);
			} else if (i % 5 == 3) {
				json.remove("double" + i);
			} else {
				json.remove("json" + i);
			}
		}
		// Clone the json object 50 times.
		for (int i = 0; i < 50; i++) {
			json = json.clone();
		}
		// Convert json to string and parse it again 10 times.
		for (int i = 0; i < 10; i++) {
			json = (JsonArray) JsonParser.parseString(json.toString());
		}
	}

}
