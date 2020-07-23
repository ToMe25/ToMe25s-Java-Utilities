package com.tome25.utils.exception;

/**
 * A simple {@link Exception} for invalid keys.
 * 
 * @author ToMe25
 */
public class InvalidKeyException extends RuntimeException {

	private static final long serialVersionUID = -5981734076788525548L;
	private String message;

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

	@Override
	public String getMessage() {
		return message == null ? "" : message;
	}

}