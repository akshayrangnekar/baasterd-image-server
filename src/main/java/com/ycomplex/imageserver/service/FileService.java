package com.ycomplex.imageserver.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

public class FileService {
    
    public static class UploadedFile {
    	public BlobKey blobKey;
    	public FileInfo fileInfo;
    	
    	public UploadedFile(BlobKey blobKey, FileInfo fileInfo) {
    		this.blobKey = blobKey;
    		this.fileInfo = fileInfo;
    	}
    }
    
	public List<UploadedFile> findUploadedFiles(HttpServletRequest req)
	{
		List<UploadedFile> rval = new ArrayList<>();
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        Map<String, List<BlobKey>> uploadedBlobs = blobstoreService.getUploads(req);
        Map<String, List<FileInfo>> fileInfos = blobstoreService.getFileInfos(req);

        Set<String> keySet = fileInfos.keySet();
        for (String key : keySet) {
        	List<FileInfo> files = fileInfos.get(key);
        	List<BlobKey> blobs = uploadedBlobs.get(key);

        	for (int i = 0; i < files.size(); i++) {
        		FileInfo file = files.get(i);
        		BlobKey blobKey = blobs.get(i);
        		UploadedFile upFile = new UploadedFile(blobKey, file);
        		rval.add(upFile);
        	}
        }
        return rval;
	}
	
	public void writeFileToCloudStorage(String bucketName, String filename, String mimetype, byte[] bytes) throws IOException {
		GcsFilename gcsFilename = new GcsFilename(bucketName, filename);
		GcsFileOptions.Builder fileOptionsBuilder = new GcsFileOptions.Builder();
		GcsFileOptions gcsFileOptions = fileOptionsBuilder.mimeType(mimetype).build();
		GcsOutputChannel outputChannel = GcsServiceFactory.createGcsService().createOrReplace(gcsFilename, gcsFileOptions);
		outputChannel.write(ByteBuffer.wrap(bytes));
		outputChannel.close();
	}
	
	public boolean deleteFileFromCloudStorage(String bucketName, String filename) throws IOException {
		GcsFilename gcsFilename = new GcsFilename(bucketName, filename);
		return GcsServiceFactory.createGcsService().delete(gcsFilename);
	}

}
