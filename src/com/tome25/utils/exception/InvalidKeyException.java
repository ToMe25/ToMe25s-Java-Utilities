package com.tome25.utils.exception;

/**
 * Just a simple Exception for Invalid keys.
 * 
 * @author ToMe25
 */
public class InvalidKeyException extends RuntimeException {

	private static final long serialVersionUID = -5981734076788525548L;
	private String message;

	public InvalidKeyException() {
	}

	public InvalidKeyException(String msg) {
		message = msg;
	}

	@Override
	public String getMessage() {
		return message == null ? "" : message;
	}

}