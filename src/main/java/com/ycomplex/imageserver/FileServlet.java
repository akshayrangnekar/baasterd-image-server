package com.ycomplex.imageserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1901444495354128550L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
    	String pathInfo = req.getPathInfo();
    	if (pathInfo != null && pathInfo.length() > 0) {
    		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
			BlobKey blobKey = blobstoreService.createGsBlobKey(pathInfo);
			blobstoreService.serve(blobKey, resp);
    		
    	} else {
    		resp.sendError(500, "No file specified");
    	}
	}
	
}
