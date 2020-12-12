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
