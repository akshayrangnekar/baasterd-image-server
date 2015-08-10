package com.ycomplex.imageserver.auth;

import javax.servlet.http.HttpServletRequest;

import com.ycomplex.imageserver.config.Config;

public abstract class AuthValidator {
	public abstract String validateRequest(HttpServletRequest request, Config config);
}
