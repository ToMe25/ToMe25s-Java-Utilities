package com.tome25.utils.exception;

/**
 * A simple {@link Exception} for invalid types.
 * 
 * @author ToMe25
 */
public class InvalidTypeException extends RuntimeException {

	private static final long serialVersionUID = -5981734076788525548L;
	private String message;

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
	 * Creates a new InvalidTypeException with a message about the given expected and received type.
	 * 
	 * @param expectedType the expected type.
	 * @param receivedType the received type.
	 */
	public InvalidTypeException(String expectedType, String receivedType) {
		this(String.format("Received invalid type %s, expected %s.", receivedType, expectedType));
	}

	@Override
	public String getMessage() {
		return message == null ? "" : message;
	}

}