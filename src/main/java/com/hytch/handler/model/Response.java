package com.hytch.handler.model;

/**
 * 数据响应
 */
public class Response<T> {
	
	private int result;
	
	private String message;
	
	private T data;
	
	public int getResult() {
		return result;
	}
	
	public void setResult(int result) {
		this.result = result;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public T getData() {
		return data;
	}
	
	public void setData(T data) {
		this.data = data;
	}
}
