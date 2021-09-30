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

import java.text.ParseException;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonElement;
import com.tome25.utils.json.JsonObject;
import com.tome25.utils.json.JsonParser;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class JsonParserBenchmark {

	private String shortComparisonObjectString;
	private JsonObject shortComparisonReferenceObject;
	private JsonElement<?> shortComparisonParsedObject;
	private String mediumComparisonObjectString;
	private JsonObject mediumComparisonReferenceObject;
	private JsonElement<?> mediumComparisonParsedObject;
	private String longComparisonObjectString;
	private JsonObject longComparisonReferenceObject;
	private JsonElement<?> longComparisonParsedObject;
	private String shortComparisonArrayString;
	private JsonArray shortComparisonReferenceArray;
	private JsonElement<?> shortComparisonParsedArray;
	private String mediumComparisonArrayString;
	private JsonArray mediumComparisonReferenceArray;
	private JsonElement<?> mediumComparisonParsedArray;
	private String longComparisonArrayString;
	private JsonArray longComparisonReferenceArray;
	private JsonElement<?> longComparisonParsedArray;
	private char[] objectString;
	private JsonObject referenceObject;
	private JsonElement<?> parsedObject;
	private char[] arrayString;
	private JsonArray referenceArray;
	private JsonElement<?> parsedArray;
	private char[] recursiveObjectString;
	private JsonObject referenceRecursiveObject;
	private JsonElement<?> parsedRecursiveObject;
	private char[] recursiveArrayString;
	private JsonArray referenceRecursiveArray;
	private JsonElement<?> parsedRecursiveArray;

	@Setup
	public void setup() {
		// Prepare for the short object parsing comparison benchmark.
		shortComparisonObjectString = "{\"string\": \"Test String\\\"\", \"int\": 3116}";
		shortComparisonReferenceObject = new JsonObject("string", "Test String\"");
		shortComparisonReferenceObject.put("int", 3116);
		// Prepare for the medium object parsing comparison benchmark.
		mediumComparisonObjectString = "{\"testString\": \"Test String?\", \"testString1\": \"Test String!\\\"!51{}\\\\[]\", "
				+ "\"int\": 5223156, \"int1\": 312, \"Test String\": \"Another Test String\"}";
		mediumComparisonReferenceObject = new JsonObject("testString", "Test String?");
		mediumComparisonReferenceObject.put("testString1", "Test String!\"!51{}\\[]");
		mediumComparisonReferenceObject.put("int", 5223156);
		mediumComparisonReferenceObject.put("int1", 312);
		mediumComparisonReferenceObject.put("Test String", "Another Test String");
		// Prepare for the long object parsing comparison benchmark.
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		longComparisonReferenceObject = new JsonObject();
		for (int i = 0; i < 100; i++) {
			builder.append("\"key");
			builder.append(i);
			builder.append("\": \"value");
			builder.append(i);
			builder.append("\", ");
			longComparisonReferenceObject.put("key" + i, "value" + i);
		}
		builder.setCharAt(builder.length() - 2, '}');
		longComparisonObjectString = builder.toString();
		// Prepare for the short array parsing comparison benchmark.
		shortComparisonArrayString = "[\"string\", \"Test String\\\"\", \"int\", 3116]";
		shortComparisonReferenceArray = new JsonArray("string", "Test String\"", "int", 3116);
		// Prepare for the medium array parsing comparison benchmark.
		mediumComparisonArrayString = "[\"testString\", \"Test String?\", \"testString\", \"Test String!\\\"!51{}\\\\[]\", "
				+ "\"int\", 5223156, \"int1\", 312, \"Test String\", \"Another Test String\"]";
		mediumComparisonReferenceArray = new JsonArray("testString", "Test String?", "testString",
				"Test String!\"!51{}\\[]", "int", 5223156, "int1", 312, "Test String", "Another Test String");
		// Prepare for the long array comparison parsing benchmark.
		builder = new StringBuilder();
		builder.append('[');
		longComparisonReferenceArray = new JsonArray();
		for (int i = 0; i < 100; i++) {
			builder.append("\"key");
			builder.append(i);
			builder.append("\", \"value");
			builder.append(i);
			builder.append("\", ");
			longComparisonReferenceArray.addAll("key" + i, "value" + i);
		}
		builder.setCharAt(builder.length() - 2, ']');
		longComparisonArrayString = builder.toString();
		// Prepare for the object parsing benchmark.
		objectString = ("{\"string\": \"Test String: {}[]\\\"\\\\,;.-12\", \"number\": 1234567890123456, "
				+ "\"floating\": 516173.616396, \"json\": {\"string\": \"There is nothing in here\", \"int\": 532}}")
						.toCharArray();
		referenceObject = new JsonObject("string", "Test String: {}[]\"\\,;.-12");
		referenceObject.put("number", 1234567890123456L);
		referenceObject.put("floating", 516173.616396);
		referenceObject.put("json", new JsonObject("string", "There is nothing in here", "int", 532));
		// Prepare for the array parsing benchmark.
		arrayString = ("[\"string\", \"Test String: {}[]\\\"\\\\,;.-12\", \"number\", 1234567890123456, "
				+ "\"floating\", 516173.616396, \"json\", [\"string\", \"There is nothing in here\", \"int\", 532]]")
						.toCharArray();
		referenceArray = new JsonArray("string", "Test String: {}[]\"\\,;.-12", "number", 1234567890123456L, "floating",
				516173.616396, "json", new JsonArray("string", "There is nothing in here", "int", 532));
		// Prepare for the recursive object parsing benchmark.
		String jsonComponent = "{\"testString\": \"Some Test\", \"testJson\": ";
		builder = new StringBuilder();
		JsonObject subJsonObject = referenceRecursiveObject = new JsonObject("testString", "Some Test");
		for (int i = 0; i < 100; i++) {
			builder.append(jsonComponent);
			if (i < 99) {
				subJsonObject.put("testJson", subJsonObject = new JsonObject("testString", "Some Test"));
			}
		}
		builder.append("\"test\"");
		subJsonObject.put("testJson", "test");
		for (int i = 0; i < 100; i++) {
			builder.append('}');
		}
		recursiveObjectString = builder.toString().toCharArray();
		// Prepare for the recursive array parsing benchmark.
		jsonComponent = "[\"testString\", \"Some Test\", \"testJson\", ";
		builder = new StringBuilder();
		JsonArray subJsonArray = referenceRecursiveArray = new JsonArray("testString", "Some Test", "testJson");
		for (int i = 0; i < 100; i++) {
			builder.append(jsonComponent);
			if (i < 99) {
				subJsonArray.add(subJsonArray = new JsonArray("testString", "Some Test", "testJson"));
			}
		}
		builder.append("\"test\"");
		subJsonArray.add("test");
		for (int i = 0; i < 100; i++) {
			builder.append(']');
		}
		recursiveArrayString = builder.toString().toCharArray();
	}

	@TearDown
	public void check() {
		assert shortComparisonParsedObject != null || mediumComparisonParsedObject != null
				|| longComparisonParsedObject != null || shortComparisonParsedArray != null
				|| mediumComparisonParsedArray != null || longComparisonParsedArray != null || parsedObject != null
				|| parsedArray != null || parsedRecursiveObject != null || parsedRecursiveArray != null
				: "Failed to determine which test was run because all results are null.";
		if (shortComparisonParsedObject != null) {
			assert shortComparisonReferenceObject.equals(shortComparisonParsedObject)
					: String.format("Json Parser returned '%s' rather then '%s'!",
							shortComparisonParsedObject.toString(), shortComparisonReferenceObject.toString());
		} else if (mediumComparisonParsedObject != null) {
			assert mediumComparisonReferenceObject.equals(mediumComparisonParsedObject)
					: String.format("Json Parser returned '%s' rather then '%s'!",
							mediumComparisonParsedObject.toString(), mediumComparisonReferenceObject.toString());
		} else if (longComparisonParsedObject != null) {
			assert longComparisonReferenceObject.equals(longComparisonParsedObject)
					: String.format("Json Parser returned '%s' rather then '%s'!",
							longComparisonParsedObject.toString(), longComparisonReferenceObject.toString());
		} else if (shortComparisonParsedArray != null) {
			assert shortComparisonReferenceArray.equals(shortComparisonParsedArray)
					: String.format("Json Parser returned '%s' rather then '%s'!",
							shortComparisonParsedArray.toString(), shortComparisonReferenceArray.toString());
		} else if (mediumComparisonParsedArray != null) {
			assert mediumComparisonReferenceArray.equals(mediumComparisonParsedArray)
					: String.format("Json Parser returned '%s' rather then '%s'!",
							mediumComparisonParsedArray.toString(), mediumComparisonReferenceArray.toString());
		} else if (longComparisonParsedArray != null) {
			assert longComparisonReferenceArray.equals(longComparisonParsedArray)
					: String.format("Json Parser returned '%s' rather then '%s'!", longComparisonParsedArray.toString(),
							longComparisonReferenceArray.toString());
		} else if (parsedObject != null) {
			assert referenceObject.equals(parsedObject) : String.format("Json Parser returned '%s' rather then '%s'!",
					parsedObject.toString(), referenceObject.toString());
		} else if (parsedArray != null) {
			assert referenceArray.equals(parsedArray) : String.format("Json Parser returned '%s' rather then '%s'!",
					parsedArray.toString(), referenceArray.toString());
		} else if (parsedRecursiveObject != null) {
			assert referenceRecursiveObject.equals(parsedRecursiveObject)
					: String.format("Json Parser returned '%s' rather then '%s'!", parsedRecursiveObject.toString(),
							referenceRecursiveObject.toString());
		} else if (parsedRecursiveArray != null) {
			assert referenceRecursiveArray.equals(parsedRecursiveArray)
					: String.format("Json Parser returned '%s' rather then '%s'!", parsedRecursiveArray.toString(),
							referenceRecursiveArray.toString());
		}
	}

	/**
	 * Tests the speed of parsing a short {@link JsonObject} using
	 * {@link JsonParser#StringFast(String)}. Uses the same string as the equivalent
	 * {@link JsonParser#parseStringFast(String)} benchmark to allow comparing the
	 * speed of the two.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingShortObjectComparisonBenchmark() throws ParseException {
		shortComparisonParsedObject = JsonParser.parseStringFast(shortComparisonObjectString);
	}

	/**
	 * Tests the speed of parsing a medium {@link JsonObject} using
	 * {@link JsonParser#parseString(String)}. Uses the same string as the
	 * equivalent {@link JsonParser#parseStringFast(String)} benchmark to allow
	 * comparing the speed of the two.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingMediumObjectComparisonBenchmark() throws ParseException {
		mediumComparisonParsedObject = JsonParser.parseStringFast(mediumComparisonObjectString);
	}

	/**
	 * Tests the speed of parsing a long {@link JsonObject} using
	 * {@link JsonParser#parseString(String)}. Uses the same string as the
	 * equivalent {@link JsonParser#parseStringFast(String)} benchmark to allow
	 * comparing the speed of the two.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingLongObjectComparisonBenchmark() throws ParseException {
		longComparisonParsedObject = JsonParser.parseStringFast(longComparisonObjectString);
	}

	/**
	 * Tests the speed of parsing a short {@link JsonArray} using
	 * {@link JsonParser#parseString(String)}. Uses the same string as the
	 * equivalent {@link JsonParser#parseStringFast(String)} benchmark to allow
	 * comparing the speed of the two.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingShortArrayComparisonBenchmark() throws ParseException {
		shortComparisonParsedArray = JsonParser.parseStringFast(shortComparisonArrayString);
	}

	/**
	 * Tests the speed of parsing a medium {@link JsonArray} using
	 * {@link JsonParser#parseString(String)}. Uses the same string as the
	 * equivalent {@link JsonParser#parseStringFast(String)} benchmark to allow
	 * comparing the speed of the two.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingMediumArrayComparisonBenchmark() throws ParseException {
		mediumComparisonParsedArray = JsonParser.parseStringFast(mediumComparisonArrayString);
	}

	/**
	 * Tests the speed of parsing a long {@link JsonArray} using
	 * {@link JsonParser#parseString(String)}. Uses the same string as the
	 * equivalent {@link JsonParser#parseStringFast(String)} benchmark to allow
	 * comparing the speed of the two.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingLongArrayComparisonBenchmark() throws ParseException {
		longComparisonParsedArray = JsonParser.parseStringFast(longComparisonArrayString);
	}

	/**
	 * Tests the speed of parsing a {@link JsonObject} using
	 * {@link JsonParser#parseCharArray(char[])}.
	 * 
	 * @throws ParseException if parsing the character array fails.
	 */
	@Benchmark
	public void objectParsingBenchmark() throws ParseException {
		parsedObject = JsonParser.parseCharArray(objectString);
	}

	/**
	 * Tests the speed of parsing a {@link JsonArray} using
	 * {@link JsonParser#parseCharArray(char[])}.
	 * 
	 * @throws ParseException if parsing the character array fails.
	 */
	@Benchmark
	public void arrayParsingBenchmark() throws ParseException {
		parsedArray = JsonParser.parseCharArray(arrayString);
	}

	/**
	 * Tests the speed of parsing 100 recursive {@link JsonObject JsonObjects} using
	 * {@link JsonParser#parseCharArray(char[])}.
	 * 
	 * @throws ParseException if parsing the character array fails.
	 */
	@Benchmark
	public void recursiveObjectParsingBenchmark() throws ParseException {
		parsedRecursiveObject = JsonParser.parseCharArray(recursiveObjectString);
	}

	/**
	 * Tests the speed of parsing 100 recursive {@link JsonArray JsonArrays} using
	 * {@link JsonParser#parseCharArray(char[])}.
	 * 
	 * @throws ParseException if parsing the character array fails.
	 */
	@Benchmark
	public void recursiveArrayParsingBenchmark() throws ParseException {
		parsedRecursiveArray = JsonParser.parseCharArray(recursiveArrayString);
	}

}
