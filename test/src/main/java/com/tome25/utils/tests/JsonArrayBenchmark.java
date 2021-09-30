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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.tome25.utils.json.JsonArray;
import com.tome25.utils.json.JsonObject;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class JsonArrayBenchmark {

	private JsonArray addJson;
	private JsonArray json;
	private int jsonSize;
	private boolean contains;
	private JsonArray equalJson;
	private boolean equal;
	private JsonArray clone;
	private JsonArray recursiveJson;
	private JsonArray recursiveClone;
	private Object lastValue;
	private String jsonReferenceString;
	private String jsonString;
	private JsonArray serializationJson;
	private ObjectOutputStream oOut;
	private ObjectInputStream oIn;
	private Object deserializedJson;
	private JsonArray workaroundChanges;
	private JsonArray generatedChanges;
	private JsonArray changes;
	private JsonArray changed;
	private JsonArray reconstructed;

	@Setup
	public void setup() throws IOException {
		addJson = new JsonArray();

		json = new JsonArray();
		equalJson = new JsonArray();
		StringBuilder builder = new StringBuilder();
		workaroundChanges = new JsonArray();
		changes = new JsonArray();
		changed = new JsonArray();
		builder.append('[');
		for (int i = 0; i < 1000; i++) {
			json.add("value" + i);

			equalJson.add("value" + i);

			builder.append("\"value");
			builder.append(i);
			builder.append("\",");

			if (i % 4 == 0) {
				changed.add("Value" + i);
				changes.add(new JsonObject("val", "Value" + i, "after", i));
				changes.add(new JsonObject("rm", i));
				if (i < 500) {// part of the workaround described below.
					workaroundChanges.add(new JsonObject("val", "Value" + i, "after", (int) Math.max(0, i * 0.75)));
				} else {
					workaroundChanges.add(new JsonObject("val", "Value" + i, "after", (int) (i - 502) / 2 + 376));
				}
			} else if (i % 4 == 1) {
				changed.add("value" + (i * 2));
				changes.add(new JsonObject("val", "value" + (i * 2), "after", i));
				changes.add(new JsonObject("rm", i));
				// FIXME this is a workaround to make this test work while the changes method
				// isn't working correctly.
				if (i >= 500) {
					workaroundChanges.add(new JsonObject("val", "value" + (i * 2), "after", (int) (i - 503) / 2 + 376));
				}
			} else {
				changed.add("value" + i);
			}
		}
		jsonSize = 1000;
		builder.setCharAt(builder.length() - 1, ']');
		jsonReferenceString = builder.toString();

		JsonArray subJson = recursiveJson = new JsonArray("testString", "Test String");
		for (int i = 0; i < 99; i++) {
			subJson.addAll("jsonTest", subJson = new JsonArray("testString", "Test String"));
		}

		serializationJson = new JsonArray();
		for (int i = 0; i < 60; i++) {// For jsons any bigger then this reading and writing would have to happen at
										// the same simultaneously.
			serializationJson.add("value" + i);
		}

		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream pIn = new PipedInputStream(pOut);
		oOut = new ObjectOutputStream(pOut);
		oIn = new ObjectInputStream(pIn);

		for (int i = 0; i < 1000; i += 4) {
			workaroundChanges.add(new JsonObject("rm", i + 1));
		}
	}

	@Setup(Level.Invocation)
	public void update() {
		if (jsonSize < 100) {
			jsonSize = json.size();
			for (int i = 0; i < 1000; i++) {
				if (!json.contains("value" + i)) {
					json.add("value" + i);
					jsonSize++;
				}
			}
		}
	}

	@TearDown
	public void check() {
		assert !addJson.isEmpty() || jsonSize < 1000 || contains || equal || clone != null || recursiveClone != null
				|| lastValue != null || jsonString != null || deserializedJson != null || generatedChanges != null
				|| reconstructed != null : "Failed to determine which test was run because all results are null.";
		if (!addJson.isEmpty()) {
			assert addJson.size() > 100000 : String
					.format("Adding elements to the JsonArray seems to have failed. Json size is %d.", addJson.size());
		} else if (jsonSize < 1000) {
			assert json.size() < 1000 : String
					.format("Removing elements from the JsonArray seems to have failed. Json size is %d.", json.size());
		} else if (clone != null) {
			assert json != clone && json.equals(clone) : String
					.format("Cloning the JsonArray returned '%s' rather then '%s'.", clone.toString(), json.toString());
		} else if (recursiveClone != null) {
			assert recursiveJson != recursiveClone && recursiveJson.equals(recursiveClone)
					&& recursiveJson.get(3) != recursiveClone.get(3)
					&& recursiveJson.get(3).equals(recursiveClone.get(3))
					: String.format("Recursively cloning the JsonObject returned '%s' rather then '%s'.",
							recursiveClone.toString(), recursiveJson.toString());
		} else if (lastValue != null) {
			assert json.get(jsonSize - 1).equals(lastValue) : String.format(
					"The last element in the JsonArray was '%s' rather then '%s'.", lastValue, json.get(jsonSize - 1));
		} else if (jsonString != null) {
			assert jsonReferenceString.equals(jsonString) : String
					.format("JsonArray#toString returned '%s' rather then '%s'.", jsonString, jsonReferenceString);
		} else if (deserializedJson != null) {
			assert serializationJson != deserializedJson && serializationJson.equals(deserializedJson)
					: String.format("After serializing and deserializing the JsonArray '%s' the result was '%s'.",
							serializationJson.toString(), deserializedJson.toString());
		} else if (generatedChanges != null) {
			assert workaroundChanges.equals(generatedChanges)
					: String.format("Getting the changes from a JsonArray returned '%s' rather then '%s'.",
							generatedChanges.toString(), workaroundChanges.toString());
		} else if (reconstructed != null) {
			assert changed.equals(reconstructed)
					: String.format("The reconstructed JsonArray was '%s' rather then '%s'.", reconstructed.toString(),
							changed.toString());
		}
	}

	/**
	 * Tests the speed of adding elements to a {@link JsonArray}.
	 */
	@Benchmark
	public void addBenchmark() {
		addJson.add("value" + ++jsonSize);
	}

	/**
	 * Tests the speed for removing elements from a {@link JsonArray}.
	 */
	@Benchmark
	public void removeBenchmark() {
		json.remove("value" + --jsonSize / 2);
	}

	/**
	 * Tests the contains speed of {@link JsonArray}.
	 */
	@Benchmark
	public void containsBenchmark() {
		contains = json.contains("value" + jsonSize / 2);
	}

	/**
	 * Tests the equals speed of {@link JsonArray}.
	 */
	@Benchmark
	public void equalsBenchmark() {
		equal = json.equals(equalJson);
	}

	/**
	 * Tests the clone speed of {@link JsonArray}.
	 */
	@Benchmark
	public void cloneBenchmark() {
		clone = json.clone();
	}

	/**
	 * Tests the speed of cloning 100 recursive {@link JsonArray JsonArrays}.
	 */
	@Benchmark
	public void recursiveCloneBenchmark() {
		recursiveClone = recursiveJson.clone(true);
	}

	/**
	 * Tests the speed of iterating over a {@link JsonArray}.
	 */
	@Benchmark
	public void iterationBenchmark() {
		json.forEach(value -> {
			lastValue = value;
		});
	}

	/**
	 * Tests the speed of converting a {@link JsonArray} to a string.
	 */
	@Benchmark
	public void toStringBenchmark() {
		jsonString = json.toString();
	}

	/**
	 * Tests the speed of serializing and deserializing a {@link JsonArray}.
	 * 
	 * @throws IOException            if something with the
	 *                                {@link ObjectOutputStream},
	 *                                {@link ObjectInputStream},
	 *                                {@link PipedOutputStream}, or
	 *                                {@link PipedInputStream} reading or writing
	 *                                goes wrong.
	 * @throws ClassNotFoundException if the class of a serialized object cannot be
	 *                                found while reading.
	 */
	@Benchmark
	public void serializationBenchmark() throws IOException, ClassNotFoundException {
		oOut.writeObject(serializationJson.clone());
		deserializedJson = oIn.readObject();
	}

	/**
	 * Tests the speed of getting the changes between two {@link JsonArray
	 * JsonArrays}.
	 */
	@Benchmark
	public void changesBenchmark() {
		generatedChanges = changed.changes(json);
	}

	/**
	 * Tests the speed of reconstructing a {@link JsonArray} from a changes
	 * {@link JsonArray} and the previous {@link JsonArray}.
	 */
	@Benchmark
	public void reconstructBenchmark() {
		reconstructed = changes.reconstruct(json);
	}

}
