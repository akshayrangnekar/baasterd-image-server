package com.ycomplex.imageserver.dto;


public class UploadUrlResponse extends ApiResponse {
	public String url;
	public Long maxSize;
	
	public UploadUrlResponse(String url, Long maxSize) {
		super(true);
		
		this.url = url;
		this.maxSize = maxSize;
	}
}
