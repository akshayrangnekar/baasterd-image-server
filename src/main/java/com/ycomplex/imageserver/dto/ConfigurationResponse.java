package com.ycomplex.imageserver.dto;

import com.ycomplex.imageserver.config.Config;

public class ConfigurationResponse extends ApiResponse {
	public Config config;
	
	public ConfigurationResponse(Config conf) {
		super(true);
		config = conf;
	}

}
