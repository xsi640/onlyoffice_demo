package com.suyang.domain;

public class ResponseMessage {
	private int code = 0;
	private String message = "";
	private Object body;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public static ResponseMessage success(Object body) {
		ResponseMessage result = new ResponseMessage();
		result.setBody(body);
		return result;
	}
}
