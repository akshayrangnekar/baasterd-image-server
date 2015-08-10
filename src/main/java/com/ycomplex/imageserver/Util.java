package com.ycomplex.imageserver;

import javax.servlet.http.HttpServletRequest;

import com.ycomplex.imageserver.config.Config;
import com.ycomplex.imageserver.config.ConfigurationLoader;
import com.ycomplex.imageserver.dto.ApiResponse;
import com.ycomplex.imageserver.dto.ErrorResponse;

public class Util {

	public static Config findConfig(HttpServletRequest req) {
    	ConfigurationLoader confLoader = ConfigurationLoader.getConfigurationLoader();
		Config conf = null; 
    	String pathInfo = req.getPathInfo();
    	if (pathInfo == null || pathInfo.length() <= 1) conf = confLoader.getDefaultConfig();
    	else {
    		String requestedConf = pathInfo.substring(1);
    		conf = confLoader.getConfig(requestedConf);
    	}
		return conf;
	}
	
	public static ApiResponse invalidConfiguration() {
		return new ErrorResponse("Invalid configuration");
	}

}
