package com.ycomplex.imageserver;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ycomplex.imageserver.config.Config;
import com.ycomplex.imageserver.config.ConfigurationLoader;
import com.ycomplex.imageserver.dto.ApiResponse;
import com.ycomplex.imageserver.transform.TransformationManager;

public abstract class ApiServlet extends HttpServlet {
	private static final long serialVersionUID = -5240543209928597378L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		ConfigurationLoader.initializeConfiguration(config);
		TransformationManager.init();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handleGet()) handle(req, resp);
		else super.doGet(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handlePost()) handle(req, resp);
		else super.doPost(req, resp);
	}
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handleDelete()) handle(req, resp);
		else super.doDelete(req, resp);
	}
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handleHead()) handle(req, resp);
		else super.doHead(req, resp);
	}
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handleOptions()) handle(req, resp);
		else super.doOptions(req, resp);
	}
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handlePut()) handle(req, resp);
		else super.doPut(req, resp);
	}
	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (handleTrace()) handle(req, resp);
		else super.doTrace(req, resp);
	}
	
	protected void handle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		ApiResponse apiResponse = null;
    	Config conf = Util.findConfig(req);
    	if (conf == null) apiResponse = Util.invalidConfiguration();
    	else apiResponse = handleApiRequest(conf, req, resp);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String json = gson.toJson(apiResponse);
		resp.setContentType("application/json");
		resp.getWriter().println(json);
	}
	
	protected abstract ApiResponse handleApiRequest(Config conf, HttpServletRequest req, HttpServletResponse resp) throws IOException;
	
	protected boolean handleGet() { return true; }
	protected boolean handlePost() { return false; }
	protected boolean handlePut() { return false; }
	protected boolean handleDelete() { return false;}
	protected boolean handleTrace() { return false;}
	protected boolean handleOptions() { return false;}
	protected boolean handleHead() { return false;}
}
