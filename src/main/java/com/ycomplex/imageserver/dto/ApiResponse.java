package com.ycomplex.imageserver.dto;

public class ApiResponse {
	public String status;
	
	public ApiResponse(boolean ok) {
		setStatus(ok);
	}
	
	protected void setStatus(boolean ok) {
		if (ok) status = "ok";
		else status = "error";
	}
}
