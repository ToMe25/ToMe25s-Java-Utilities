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
package com.tome25.utils.exception;

/**
 * A simple {@link RuntimeException} for invalid types.
 * 
 * @author ToMe25
 */
public class InvalidTypeException extends RuntimeException {

	/**
	 * Generated serial version uid.
	 */
	private static final long serialVersionUID = -6437334691725649768L;
	private String message;
	private String expectedType;
	private String receivedType;

	/**
	 * Creates a new InvalidTypeException.
	 */
	public InvalidTypeException() {
	}

	/**
	 * Creates a new InvalidTypeException with the given message.
	 * 
	 * @param msg the message for this InvalidTypeException.
	 */
	public InvalidTypeException(String msg) {
		message = msg;
	}

	/**
	 * Creates a new InvalidTypeException with the given expected and received type.
	 * The generated message will be 'Received invalid type RECEIVED, expected
	 * EXPECTED.'.
	 * 
	 * @param expected the expected type.
	 * @param received the received type.
	 */
	public InvalidTypeException(String expected, String received) {
		this(String.format("Received invalid type %s, expected %s.", received, expected));
		expectedType = expected;
		receivedType = received;
	}

	@Override
	public String getMessage() {
		return message == null ? "" : message;
	}

	/**
	 * Gets the expected type from this exception. Null if not set.
	 * 
	 * @return the expected type from this exception.
	 */
	public String getExpectedType() {
		return expectedType;
	}

	/**
	 * Gets the received type from this exception. Null if not set.
	 * 
	 * @return the received type from this exception.
	 */
	public String getReceivedType() {
		return receivedType;
	}

}