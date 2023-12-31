package com.example.exception;

public class ResourceException extends Exception {

	private static final long serialVersionUID = 1L;

	public ResourceException() {
		super();
	}

	public ResourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ResourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceException(String message) {
		super(message);
	}

	public ResourceException(Throwable cause) {
		super(cause);
	}

}
