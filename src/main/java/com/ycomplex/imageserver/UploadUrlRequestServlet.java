/**
 * 
 */
package com.ycomplex.imageserver;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.UploadOptions;
import com.ycomplex.imageserver.config.Config;
import com.ycomplex.imageserver.dto.ApiResponse;
import com.ycomplex.imageserver.dto.UploadUrlResponse;

/**
 * @author akshay
 *
 */
public class UploadUrlRequestServlet extends ApiServlet {
	private static final long serialVersionUID = -9191202364028516799L;

	@Override
	protected ApiResponse handleApiRequest(Config conf, HttpServletRequest req,
			HttpServletResponse resp) {
        String uploadUrl = BlobstoreServiceFactory.getBlobstoreService().
        		createUploadUrl("/postUpload/" + (conf.name == null ? "" : conf.name) + "?failTime=" + (new Date()).getTime(),
        				buildUploadOptions(conf));
        
        return new UploadUrlResponse(uploadUrl, conf.original.maxSize);
	}
	
    private UploadOptions buildUploadOptions(Config config) {
    	String bucketName = config.bucket;
    	UploadOptions opts = UploadOptions.Builder.withGoogleStorageBucketName(bucketName);
    	
    	Long maxSize = config.original.maxSize;
    	if (maxSize != null) {
    		opts.maxUploadSizeBytes(maxSize);
    	}
    	
    	return opts;
    }
}

