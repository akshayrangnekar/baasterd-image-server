package com.ycomplex.imageserver.auth;

import javax.servlet.http.HttpServletRequest;

import com.ycomplex.imageserver.config.Config;

public class PublicAuthValidator extends AuthValidator {

	@Override
	public String validateRequest(HttpServletRequest request, Config conf) {
		return "";
	}

}
