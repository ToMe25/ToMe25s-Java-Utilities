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

import org.junit.Test;

import com.carrotsearch.junitbenchmarks.AbstractBenchmark;
import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.tome25.utils.General;

public class GeneralTest extends AbstractBenchmark {

	/**
	 * Tests the general functionality of
	 * {@link General#reflectiveClone(Cloneable)}.
	 */
	@Test
	public void reflectiveCloneTest() {
		Cloneable original = new CloneTest(123);
		Cloneable clone = General.reflectiveClone(original);
		assertFalse("Reference to original and clone is the same.", original == clone);
		assertEquals(original, clone);
	}

	/**
	 * Tests the performance of {@link General#reflectiveClone(Cloneable)}.
	 */
	@Test
	@BenchmarkOptions(benchmarkRounds = 100000, warmupRounds = 100)
	public void reflectiveCloneSpeedTest() {
		Cloneable original = new CloneTest(123);
		Cloneable clone;
		for (int i = 0; i < 1000; i++) {
			clone = General.reflectiveClone(original);
			assertEquals(original, clone);
		}
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

	}
}
