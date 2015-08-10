package com.ycomplex.imageserver;

public class InvalidUploadException extends Exception {
	
	private static final long serialVersionUID = 1781326445130436989L;

	public InvalidUploadException(String cause){
		super(cause);
	}
	
}
