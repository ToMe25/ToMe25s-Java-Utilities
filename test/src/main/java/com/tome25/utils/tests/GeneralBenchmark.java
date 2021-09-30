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

import com.tome25.utils.General;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.SampleTime)
public class GeneralBenchmark {

	private CloneTest original;
	private CloneTest clone;

	@Setup
	public void setup() {
		original = new CloneTest(123);
	}

	/**
	 * Tests the speed of {@link General#reflectiveClone(Cloneable)}.
	 */
	@Benchmark
	public void reflectiveCloneBenchmark() {
		clone = General.reflectiveClone(original);
	}

	@TearDown
	public void check() {
		assert original != clone && original.equals(clone)
				: String.format("Cloning somehow went wrong, the clone is %s!", clone.toString());
	}

	/**
	 * A minimal {@link Cloneable} implementation to test whether
	 * {@link General#reflectiveClone(Cloneable)} works.
	 */
	private class CloneTest implements Cloneable {

		private int test;

		public CloneTest(int test) {
			this.test = test;
		}

		@Override
		public Object clone() {
			try {
				return super.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		public int hashCode() {
			return test;
		}

		@Override
		public boolean equals(Object obj) {
			return test == ((CloneTest) obj).test;
		}

		@Override
		public String toString() {
			return String.format("CloneTest{%d}", test);
		}

	}

}
