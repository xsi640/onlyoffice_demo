package com.suyang.exceptions;

public class APIException extends Exception {

	private static final long serialVersionUID = 1L;

	private APIExceptionType type;

	public APIException(APIExceptionType type) {
		this.setType(type);
	}

	public APIExceptionType getType() {
		return type;
	}

	public void setType(APIExceptionType type) {
		this.type = type;
	}	
}
