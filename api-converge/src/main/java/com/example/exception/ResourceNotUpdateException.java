package com.example.exception;

/**
 * @author Stanley
 * @version 1.0
 */
public class ResourceNotUpdateException extends ResourceException {

	private static final long serialVersionUID = 1L;

	public ResourceNotUpdateException() {
		super();
	}

	public ResourceNotUpdateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ResourceNotUpdateException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResourceNotUpdateException(String message) {
		super(message);
	}

	public ResourceNotUpdateException(Throwable cause) {
		super(cause);
	}

}
