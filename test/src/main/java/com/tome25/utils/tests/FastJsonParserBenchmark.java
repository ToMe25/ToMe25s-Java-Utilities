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
public class FastJsonParserBenchmark {

	private String shortObjectString;
	private JsonObject shortReferenceObject;
	private JsonElement<?> shortParsedObject;
	private String mediumObjectString;
	private JsonObject mediumReferenceObject;
	private JsonElement<?> mediumParsedObject;
	private String shortArrayString;
	private JsonArray shortReferenceArray;
	private JsonElement<?> shortParsedArray;
	private String mediumArrayString;
	private JsonArray mediumReferenceArray;
	private JsonElement<?> mediumParsedArray;
	private String longObjectString;
	private JsonObject longReferenceObject;
	private JsonElement<?> longParsedObject;
	private String longArrayString;
	private JsonArray longReferenceArray;
	private JsonElement<?> longParsedArray;

	@Setup
	public void setup() {
		// Prepare for the short object parsing benchmark.
		shortObjectString = "{\"string\": \"Test String\\\"\", \"int\": 3116}";
		shortReferenceObject = new JsonObject("string", "Test String\"");
		shortReferenceObject.put("int", 3116);
		// Prepare for the medium object parsing benchmark.
		mediumObjectString = "{\"testString\": \"Test String?\", \"testString\": \"Test String!\\\"!51{}\\\\[]\", "
				+ "\"int\": 5223156, \"int1\": 312, \"Test String\": \"Another Test String\"}";
		mediumReferenceObject = new JsonObject("testString", "Test String?");
		mediumReferenceObject.put("testString", "Test String!\"!51{}\\[]");
		mediumReferenceObject.put("int", 5223156);
		mediumReferenceObject.put("int1", 312);
		mediumReferenceObject.put("Test String", "Another Test String");
		// Prepare for the long object parsing benchmark.
		StringBuilder builder = new StringBuilder();
		builder.append('{');
		longReferenceObject = new JsonObject();
		for (int i = 0; i < 100; i++) {
			builder.append("\"key");
			builder.append(i);
			builder.append("\": \"value");
			builder.append(i);
			builder.append("\", ");
			longReferenceObject.put("key" + i, "value" + i);
		}
		builder.setCharAt(builder.length() - 2, '}');
		longObjectString = builder.toString();
		// Prepare for the short array parsing benchmark.
		shortArrayString = "[\"string\", \"Test String\\\"\", \"int\", 3116]";
		shortReferenceArray = new JsonArray("string", "Test String\"", "int", 3116);
		// Prepare for the medium array parsing benchmark.
		mediumArrayString = "[\"testString\", \"Test String?\", \"testString\", \"Test String!\\\"!51{}\\\\[]\", "
				+ "\"int\", 5223156, \"int1\", 312, \"Test String\", \"Another Test String\"]";
		mediumReferenceArray = new JsonArray("testString", "Test String?", "testString", "Test String!\"!51{}\\[]",
				"int", 5223156, "int1", 312, "Test String", "Another Test String");
		// Prepare for the long array parsing benchmark.
		builder = new StringBuilder();
		builder.append('[');
		longReferenceArray = new JsonArray();
		for (int i = 0; i < 100; i++) {
			builder.append("\"key");
			builder.append(i);
			builder.append("\", \"value");
			builder.append(i);
			builder.append("\", ");
			longReferenceArray.addAll("key" + i, "value" + i);
		}
		builder.setCharAt(builder.length() - 2, ']');
		longArrayString = builder.toString();
	}

	@TearDown
	public void check() {
		assert shortParsedObject != null || mediumParsedObject != null || longParsedObject != null
				|| shortParsedArray != null || mediumParsedArray != null || longParsedArray != null
				: "Failed to determine which test was run because all results are null.";
		if (shortParsedObject != null) {
			assert shortReferenceObject.equals(shortParsedObject)
					: String.format("Fast Json Parser returned '%s' rather then '%s'!", shortParsedObject.toString(),
							shortReferenceObject.toString());
		} else if (mediumParsedObject != null) {
			assert mediumReferenceObject.equals(mediumParsedObject)
					: String.format("Fast Json Parser returned '%s' rather then '%s'!", mediumParsedObject.toString(),
							mediumReferenceObject.toString());
		} else if (longParsedObject != null) {
			assert longReferenceObject.equals(longParsedObject)
					: String.format("Fast Json Parser returned '%s' rather then '%s'!", longParsedObject.toString(),
							longReferenceObject.toString());
		} else if (shortParsedArray != null) {
			assert shortReferenceArray.equals(shortParsedArray)
					: String.format("Fast Json Parser returned '%s' rather then '%s'!", shortParsedArray.toString(),
							shortReferenceArray.toString());
		} else if (mediumParsedArray != null) {
			assert mediumReferenceArray.equals(mediumParsedArray)
					: String.format("Fast Json Parser returned '%s' rather then '%s'!", mediumParsedArray.toString(),
							mediumReferenceArray.toString());
		} else if (longParsedArray != null) {
			assert longReferenceArray.equals(longParsedArray)
					: String.format("Fast Json Parser returned '%s' rather then '%s'!", longParsedArray.toString(),
							longReferenceArray.toString());
		}
	}

	/**
	 * Tests the speed of parsing a short {@link JsonObject} using
	 * {@link JsonParser#parseStringFast(String)}.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingShortObjectBenchmark() throws ParseException {
		shortParsedObject = JsonParser.parseStringFast(shortObjectString);
	}

	/**
	 * Tests the speed of parsing a medium {@link JsonObject} using
	 * {@link JsonParser#parseStringFast(String)}.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingMediumObjectBenchmark() throws ParseException {
		mediumParsedObject = JsonParser.parseStringFast(mediumObjectString);
	}

	/**
	 * Tests the speed of parsing a long {@link JsonObject} using
	 * {@link JsonParser#parseStringFast(String)}.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingLongObjectBenchmark() throws ParseException {
		longParsedObject = JsonParser.parseStringFast(longObjectString);
	}

	/**
	 * Tests the speed of parsing a short {@link JsonArray} using
	 * {@link JsonParser#parseStringFast(String)}.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingShortArrayBenchmark() throws ParseException {
		shortParsedArray = JsonParser.parseStringFast(shortArrayString);
	}

	/**
	 * Tests the speed of parsing a medium {@link JsonArray} using
	 * {@link JsonParser#parseStringFast(String)}.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingMediumArrayBenchmark() throws ParseException {
		mediumParsedArray = JsonParser.parseStringFast(mediumArrayString);
	}

	/**
	 * Tests the speed of parsing a long {@link JsonArray} using
	 * {@link JsonParser#parseStringFast(String)}.
	 * 
	 * @throws ParseException if parsing the string fails.
	 */
	@Benchmark
	public void fastParsingLongArrayBenchmark() throws ParseException {
		longParsedArray = JsonParser.parseStringFast(longArrayString);
	}

}
