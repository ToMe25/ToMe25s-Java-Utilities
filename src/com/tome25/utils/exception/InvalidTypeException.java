package com.tome25.utils.exception;

/**
 * Just a simple Exception for Invalid types.
 * 
 * @author ToMe25
 */
public class InvalidTypeException extends RuntimeException {

	private static final long serialVersionUID = -5981734076788525548L;
	private String message;

	public InvalidTypeException() {
	}

	public InvalidTypeException(String msg) {
		message = msg;
	}

	public InvalidTypeException(String expectedType, String receivedType) {
		message = String.format("Received invalid type %s, expected %s.", receivedType, expectedType);
	}

	@Override
	public String getMessage() {
		return message == null ? "" : message;
	}

}