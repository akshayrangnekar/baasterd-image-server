package com.ycomplex.imageserver.dto;

public class ErrorResponse extends ApiResponse {
	public String message;
	
	public ErrorResponse(String inMessage) {
		super(false);
		message = inMessage;
	}

}
