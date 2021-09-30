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

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

import com.tome25.utils.StringUtils;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class StringUtilsBenchmark {

	private String toEscape;
	private Object[] toConvert;
	private StringBuilder builder;
	private String escaped;
	private String converted;

	@Setup
	public void setup() {
		builder = new StringBuilder();
		toEscape = "Some\" String\\ to\\\" escape\\$";
		toConvert = new String[] { "Some", "Random", "Test", "String", "Array", "!" };
	}

	@TearDown
	public void check() {
		assert escaped != null || converted != null
				: "Failed to determine which test was run because all results are null.";
		if (escaped != null) {
			assert "\"Some\\\" String\\\\ to\\\\\\\" escape\\\\$\"".equals(escaped)
					: String.format("Escaping the string returned '%s'!", escaped);
		} else if (converted != null) {
			assert "Some Random Test String Array !".equals(converted)
					: String.format("Converting the array to a string returned '%s'!", converted);
		}
	}

	/**
	 * Tests the speed of
	 * {@link StringUtils#toEscapedString(StringBuilder, Object)}.
	 */
	@Benchmark
	public void toEscapedStringBenchmark() {
		StringUtils.toEscapedString(builder, toEscape);
		escaped = builder.toString();
		builder.setLength(0);
	}

	/**
	 * Tests the speed of
	 * {@link StringUtils#arrayToString(StringBuilder, Object[], String)}.
	 */
	@Benchmark
	public void arrayToStringBenchmark() {
		StringUtils.arrayToString(builder, toConvert, " ");
		converted = builder.toString();
		builder.setLength(0);
	}

}
