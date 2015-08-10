package com.ycomplex.imageserver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ycomplex.imageserver.config.Config;
import com.ycomplex.imageserver.dto.ApiResponse;
import com.ycomplex.imageserver.dto.ConfigurationResponse;

public class ShowConfigurationServlet extends ApiServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected ApiResponse handleApiRequest(Config conf, HttpServletRequest req,
			HttpServletResponse resp) {
		return new ConfigurationResponse(conf);
	}
	
	

}
