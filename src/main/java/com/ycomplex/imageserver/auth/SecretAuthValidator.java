package com.ycomplex.imageserver.auth;

import javax.servlet.http.HttpServletRequest;

import com.ycomplex.imageserver.config.Config;

public class SecretAuthValidator extends AuthValidator {

	@Override
	public String validateRequest(HttpServletRequest request, Config conf) {
		String token = request.getParameter("token");
		if (token == null || !token.equals(conf.authSecret)) {
			return null;
		}
		return "";
	}

}
