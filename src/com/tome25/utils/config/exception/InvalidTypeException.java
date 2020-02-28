package com.tome25.utils.config.exception;
/**
 * 
 * @author ToMe25
 * 
 *         Just a simple Exception for Invalid types
 */
public class InvalidTypeException extends Exception {

	/**
	 * useless or not?
	 */
	private static final long serialVersionUID = -5981734076788525548L;
	private String message;

	public InvalidTypeException() {

	}

	public InvalidTypeException(String msg) {
		message = msg;
	}

	@Override
	public String getMessage() {
		return message == null ? "" : message;
	}

}