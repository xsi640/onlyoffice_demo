package com.suyang.exceptions;

public enum ErrorType {
	API(400), System(500), Database(600);

	private int value;

	private ErrorType(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return String.valueOf(this.value);
	}

	public int getValue() {
		return this.value;
	}

	public static ErrorType valueOf(int value) {
		ErrorType result = API;
		switch (value) {
		case 400:
			result = ErrorType.API;
			break;
		case 500:
			result = ErrorType.System;
			break;
		case 600:
			result = ErrorType.Database;
			break;
		}
		return result;
	}
}
