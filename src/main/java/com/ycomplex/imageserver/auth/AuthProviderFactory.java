package com.ycomplex.imageserver.auth;

public class AuthProviderFactory {
	public static AuthValidator getValidator(String type) {
		if (type == null || type.equals("none")) {
			return new PublicAuthValidator();
		}
		else if (type.equals("secret")) return new SecretAuthValidator();
		
		return null;
	}
}
