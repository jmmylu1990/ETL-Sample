package com.example.exception;

public class ResourceFormatErrorException extends ResourceException {

	private static final long serialVersionUID = 1L;

	public ResourceFormatErrorException() {
		super();
	}

	public ResourceFormatErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ResourceFormatErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceFormatErrorException(String message) {
		super(message);
	}

	public ResourceFormatErrorException(Throwable cause) {
		super(cause);
	}

}
