package com.doc360.api.exception;

public class CreateDocException extends Exception{

	private static final long serialVersionUID = 692872901266543660L;

	public CreateDocException(String message, Throwable cause) {
		super(message,cause);
	}
	
	public CreateDocException(String message) {
		super(message);
	}
	
}
