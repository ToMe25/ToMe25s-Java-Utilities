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

import org.junit.Test;

import com.tome25.utils.StringUtils;

public class StringUtilsTest {

	/**
	 * Test the functionality of {@link StringUtils#toEscapedString(Object)} and
	 * {@link StringUtils#toEscapedString(StringBuilder, Object)}.
	 */
	@Test
	public void toEscapedStringTest() {
		// test converting a few numbers to a string.
		assertEquals("123", StringUtils.toEscapedString(123));// int
		assertEquals("4294967294", StringUtils.toEscapedString(Integer.MAX_VALUE * 2l));// long
		assertEquals("456", StringUtils.toEscapedString((short) 456));// short
		assertEquals("458012.12315", StringUtils.toEscapedString(458012.12315));// double
		assertEquals("653.135", StringUtils.toEscapedString(653.135f));// float
		// test converting true and false to a string.
		assertEquals("true", StringUtils.toEscapedString(true));
		assertEquals("false", StringUtils.toEscapedString(false));
		// test converting a simple string.
		assertEquals("\"Some Simple Test String\"", StringUtils.toEscapedString("Some Simple Test String"));
		// test converting a string that needs escaping.
		assertEquals("\"String to Escape\\\": \\\\\"", StringUtils.toEscapedString("String to Escape\": \\"));
		// test the overload taking a StringBuilder with a string that needs escaping.
		StringBuilder builder = new StringBuilder();
		StringUtils.toEscapedString(builder, "Some random String 024895\" \\:;54.2!");
		assertEquals("\"Some random String 024895\\\" \\\\:;54.2!\"", builder.toString());
	}

	/**
	 * Tests the functionality of
	 * {@link StringUtils#arrayToString(Object[], String)} and
	 * {@link StringUtils#arrayToString(StringBuilder, Object[], String)}.
	 */
	@Test
	public void arrayToStringTest() {
		// test converting a string array to a string.
		assertEquals("hello, world, test", StringUtils.arrayToString(new String[] { "hello", "world", "test" }, ", "));
		// test converting a Float array to a string.
		assertEquals("3123.0;542.234;651.4;17245.0",
				StringUtils.arrayToString(new Float[] { 3123f, 542.234f, 651.4f, 17245f }, ";"));
		// test converting a mixed type object array to a string.
		assertEquals("Test, 5123, 514.123, 6134.63, a, String",
				StringUtils.arrayToString(new Object[] { "Test", 5123, 514.123, 6134.63f, 'a', "String" }, ", "));
		// test converting a mixed type object array to a string with a given
		// StringBuilder.
		StringBuilder builder = new StringBuilder();
		StringUtils.arrayToString(builder, new Object[] { "Some", 736, 514.43, 264.234f, 'a', "String" }, ", ");
		assertEquals("Some, 736, 514.43, 264.234, a, String", builder.toString());
	}

}
