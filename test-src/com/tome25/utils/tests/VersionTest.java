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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.tome25.utils.version.VersionControl;

public class VersionTest {

	@Test
	public void testVersion() {
		// Check default version. Always 1.0 if not built as a jar.
		assertEquals("1.0", VersionControl.getVersionString());
		assertArrayEquals(new int[] { 1, 0 }, VersionControl.getVersionArray());
		assertEquals(1, VersionControl.getMajor());
		assertEquals(0, VersionControl.getMinor());
		assertEquals(0, VersionControl.getBuild());
		assertEquals(0, VersionControl.getPatch());
		// Check tome25s-java-utilities version
		String library = "tome25s-java-utilities";
		assertEquals("1.0", VersionControl.getVersionString(library));
		assertArrayEquals(new int[] { 1, 0 }, VersionControl.getVersionArray(library));
		assertEquals(1, VersionControl.getMajor(library));
		assertEquals(0, VersionControl.getMinor(library));
		assertEquals(0, VersionControl.getBuild(library));
		assertEquals(0, VersionControl.getPatch(library));
		// test cusom version
		library = "test_library";
		assertEquals(null, VersionControl.getVersionString(library));
		assertArrayEquals(new int[0], VersionControl.getVersionArray(library));
		VersionControl.setVersionString(library, "3.7.10");
		assertEquals("3.7.10", VersionControl.getVersionString(library));
		assertArrayEquals(new int[] { 3, 7, 10 }, VersionControl.getVersionArray(library));
		assertEquals(3, VersionControl.getMajor(library));
		assertEquals(7, VersionControl.getMinor(library));
		assertEquals(10, VersionControl.getBuild(library));
		assertEquals(10, VersionControl.getPatch(library));
	}

}
