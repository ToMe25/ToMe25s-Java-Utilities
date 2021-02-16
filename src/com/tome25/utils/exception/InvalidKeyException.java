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
 * A simple {@link RuntimeException} for invalid keys.
 * 
 * @author ToMe25
 */
public class InvalidKeyException extends RuntimeException {

	/**
	 * Generated serial version uid.
	 */
	private static final long serialVersionUID = -6418300025186126303L;
	private String message;
	private String key;
	private String reason;

	/**
	 * Creates a new empty InvalidKeyException.
	 */
	public InvalidKeyException() {
	}

	/**
	 * Creates a new InvalidKeyException with the given message.
	 * 
	 * @param msg the message for this InvalidKeyException.
	 */
	public InvalidKeyException(String msg) {
		message = msg;
	}

	/**
	 * Creates a new InvalidKeyException with the given key and reason. The
	 * generated message will be 'The received key "KEY" is invalid because REASON".
	 * 
	 * @param key    the received key.
	 * @param reason the reason why the key is invalid.
	 */
	public InvalidKeyException(String key, String reason) {
		this(String.format("The received key \"%s\" is invalid because %s", key, reason));
		this.key = key;
		this.reason = reason;
	}

	@Override
	public String getMessage() {
		return message == null ? "" : message;
	}

	/**
	 * Gets the received key. Null if not set.
	 * 
	 * @return the received key.
	 */
	public String getKey() {
		return key;
	}

	/**
	 * Gets the reason why the key is invalid. Null if not set.
	 * 
	 * @return the reason why the key is invalid.
	 */
	public String getReason() {
		return reason;
	}

}