package com.ycomplex.imageserver.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import com.google.gson.Gson;

public class ConfigurationLoader {
	private static Logger log = Logger.getLogger(ConfigurationLoader.class.getCanonicalName());
	private static ConfigurationLoader singleton;
	
	public Config[] configs;
	private Config defaultConfig;
	private ConfigurationFile configFile;
	private Map<String, Config> configMap;
	
	private ConfigurationLoader(ServletConfig servletConfig) {
		ServletContext context = servletConfig.getServletContext();
		InputStream in = context.getResourceAsStream("/WEB-INF/config.json"); 
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Gson gson = new Gson();
		setConfigs(gson.fromJson(reader, ConfigurationFile.class));
	}
	
	private void setConfigs(ConfigurationFile inConfigFile) {
		this.configFile = inConfigFile;
		this.configs = this.configFile.configs;
		this.configMap = new HashMap<>();
		
		for (int i = 0; i < configs.length; i++) {
			Config config = configs[i];
			if (config.name == null) setDefaultConfig(validateConfig(config));
			else {
				configMap.put(config.name, config);
			}
		}
	}
	
	private Config validateConfig(Config config) {
		if (config.bucket != null &&
				config.auth != null &&
				config.original != null &&
				config.original.type != null && 
				config.original.preserve != null && 
				config.original.maxSize != null
				) return config;
		
		log.severe("Invalid default configuration. Nothing will work.");
		return null;
	}

	public Config getDefaultConfig() {
		return defaultConfig;
	}

	private void setDefaultConfig(Config defaultConfig) {
		this.defaultConfig = defaultConfig;
	}
	
	public Config getConfig(String configName) {
		return applyDefaultConfig(this.configMap.get(configName));
	}

	private Config applyDefaultConfig(Config config) {
		Config def = getDefaultConfig();
		if (def == null) return null;
		if (def != null && config != null) {
			if (config.bucket == null) config.bucket = def.bucket;
			if (config.transforms == null) config.transforms = def.transforms;
			if (config.auth == null) config.auth = def.auth;
			if (config.original == null) config.original = def.original;
			else {
				if (config.original.maxSize == null) config.original.maxSize = def.original.maxSize;
				if (config.original.type == null) config.original.type = def.original.type;
				if (config.original.preserve == null) config.original.preserve = def.original.preserve;
			}
		}
		return config;
	}

	public Config[] getConfigs() {
		return configs;
	}

	public static synchronized void initializeConfiguration(ServletConfig servletConfig) {
		if (singleton == null) singleton = new ConfigurationLoader(servletConfig);
	}
	
	public static ConfigurationLoader getConfigurationLoader() {
		return singleton;
	}
}
