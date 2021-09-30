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

import com.tome25.utils.json.JsonObject;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class JsonObjectBenchmark {

	private JsonObject addJson;
	private JsonObject json;
	private int jsonSize;
	private boolean contains;
	private JsonObject equalJson;
	private boolean equal;
	private JsonObject clone;
	private JsonObject recursiveJson;
	private JsonObject recursiveClone;
	private Object lastValue;
	private String jsonReferenceString;
	private String jsonString;
	private JsonObject serializationJson;
	private ObjectOutputStream oOut;
	private ObjectInputStream oIn;
	private Object deserializedJson;
	private JsonObject changes;
	private JsonObject changed;
	private JsonObject generatedChanges;
	private JsonObject reconstructed;

	@Setup(Level.Iteration)
	public void setup() throws IOException {
		addJson = new JsonObject();

		json = new JsonObject();
		equalJson = new JsonObject();
		StringBuilder builder = new StringBuilder();
		changed = new JsonObject();
		changes = new JsonObject();
		builder.append('{');
		for (int i = 0; i < 1000; i++) {
			json.put("key" + i, "value" + i);

			equalJson.put("key" + i, "value" + i);

			builder.append("\"key");
			builder.append(i);
			builder.append("\":\"value");
			builder.append(i);
			builder.append("\",");

			if (i % 4 == 0) {
				changed.add("Key" + i, "value" + i);
				changes.add("key" + i, null);
				changes.add("Key" + i, "value" + i);
			} else if (i % 4 == 1) {
				changed.add("key" + i, "value" + (i * 2));
				changes.add("key" + i, "value" + (i * 2));
			} else {
				changed.add("key" + i, "value" + i);
			}
		}
		jsonSize = 1000;
		builder.setCharAt(builder.length() - 1, '}');
		jsonReferenceString = builder.toString();

		JsonObject subJson = recursiveJson = new JsonObject("testString", "Test String");
		for (int i = 0; i < 99; i++) {
			subJson.put("jsonTest", subJson = new JsonObject("testString", "Test String"));
		}

		serializationJson = new JsonObject();
		for (int i = 0; i < 50; i++) {// For jsons any bigger then this reading and writing would have to happen at
										// the same simultaneously.
			serializationJson.add("key" + i, "value" + i);
		}

		PipedOutputStream pOut = new PipedOutputStream();
		PipedInputStream pIn = new PipedInputStream(pOut);
		oOut = new ObjectOutputStream(pOut);
		oIn = new ObjectInputStream(pIn);
	}

	@Setup(Level.Invocation)
	public void update() {
		if (jsonSize < 100) {
			jsonSize = json.size();
			for (int i = 0; i < 1000; i++) {
				if (!json.containsKey("key" + i)) {
					json.add("key" + i, "value" + i);
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
					.format("Adding elements to the JsonObject seems to have failed. Json size is %d.", addJson.size());
		} else if (jsonSize < 1000) {
			assert json.size() < 1000 : String.format(
					"Removing elements from the JsonObject seems to have failed. Json size is %d.", json.size());
		} else if (clone != null) {
			assert json != clone && json.equals(clone) : String.format(
					"Cloning the JsonObject returned '%s' rather then '%s'.", clone.toString(), json.toString());
		} else if (recursiveClone != null) {
			assert recursiveJson != recursiveClone && recursiveJson.equals(recursiveClone)
					&& recursiveJson.get("jsonTest") != recursiveClone.get("jsonTest")
					&& recursiveJson.get("jsonTest").equals(recursiveClone.get("jsonTest"))
					: String.format("Recursively cloning the JsonObject returned '%s' rather then '%s'.",
							recursiveClone.toString(), recursiveJson.toString());
		} else if (lastValue != null) {
			assert json.get("key" + (jsonSize - 1)).equals(lastValue)
					: String.format("The last element in the JsonObject was '%s' rather then '%s'.", lastValue,
							json.get("key" + (jsonSize - 1)));
		} else if (jsonString != null) {
			assert jsonReferenceString.equals(jsonString) : String
					.format("JsonObject#toString returned '%s' rather then '%s'.", jsonString, jsonReferenceString);
		} else if (deserializedJson != null) {
			assert serializationJson != deserializedJson && serializationJson.equals(deserializedJson)
					: String.format("After serializing and deserializing the JsonObject '%s' the result was '%s'.",
							serializationJson.toString(), deserializedJson.toString());
		} else if (generatedChanges != null) {
			assert changes.equals(generatedChanges)
					: String.format("Getting the changes from a JsonObject returned '%s' rather then '%s'.",
							generatedChanges.toString(), changes.toString());
		} else if (reconstructed != null) {
			assert changed.equals(reconstructed)
					: String.format("The reconstructed JsonObject was '%s' rather then '%s'.", reconstructed.toString(),
							changed.toString());
		}
	}

	/**
	 * Tests the speed of adding elements to a {@link JsonObject}.
	 */
	@Benchmark
	public void addBenchmark() {
		addJson.add("key" + ++jsonSize, "value" + jsonSize);
	}

	/**
	 * Tests the speed for removing elements from a {@link JsonObject}.
	 */
	@Benchmark
	public void removeBenchmark() {
		json.remove("key" + --jsonSize / 2);
	}

	/**
	 * Tests the contains key speed of {@link JsonObject}.
	 */
	@Benchmark
	public void containsBenchmark() {
		contains = json.containsKey("key" + jsonSize / 2);
	}

	/**
	 * Tests the equals speed of {@link JsonObject}.
	 */
	@Benchmark
	public void equalsBenchmark() {
		equal = json.equals(equalJson);
	}

	/**
	 * Tests the clone speed of {@link JsonObject}.
	 */
	@Benchmark
	public void cloneBenchmark() {
		clone = json.clone();
	}

	/**
	 * Tests the speed of cloning 100 recursive {@link JsonObject JsonObjects}.
	 */
	@Benchmark
	public void recursiveCloneBenchmark() {
		recursiveClone = recursiveJson.clone(true);
	}

	/**
	 * Tests the speed of iterating over a {@link JsonObject}.
	 */
	@Benchmark
	public void iterationBenchmark() {
		json.forEach((key, value) -> {
			lastValue = value;
		});
	}

	/**
	 * Tests the speed of converting a {@link JsonObject} to a string.
	 */
	@Benchmark
	public void toStringBenchmark() {
		jsonString = json.toString();
	}

	/**
	 * Tests the speed of serializing and deserializing a {@link JsonObject}.
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
	 * Tests the speed of getting the changes between two {@link JsonObject
	 * JsonObjects}.
	 */
	@Benchmark
	public void changesBenchmark() {
		generatedChanges = changed.changes(json);
	}

	/**
	 * Tests the speed of reconstructing a {@link JsonObject} from a changes
	 * {@link JsonObject} and the previous {@link JsonObject}.
	 */
	@Benchmark
	public void reconstructBenchmark() {
		reconstructed = changes.reconstruct(json);
	}

}
