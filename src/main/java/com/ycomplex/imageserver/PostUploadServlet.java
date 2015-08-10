package com.ycomplex.imageserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesService.OutputEncoding;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.OutputSettings;
import com.google.appengine.api.images.Transform;
import com.ycomplex.imageserver.config.Config;
import com.ycomplex.imageserver.config.Config.TransformConfig;
import com.ycomplex.imageserver.dto.ApiResponse;
import com.ycomplex.imageserver.dto.ErrorResponse;
import com.ycomplex.imageserver.dto.UploadResponse;
import com.ycomplex.imageserver.dto.UploadResponse.ServedFile;
import com.ycomplex.imageserver.service.FileService;
import com.ycomplex.imageserver.service.FileService.UploadedFile;
import com.ycomplex.imageserver.transform.TransformationManager;
import com.ycomplex.imageserver.transform.Transformer;

public class PostUploadServlet extends ApiServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = Logger.getLogger(PostUploadServlet.class.getCanonicalName());

	private FileService fs;
	private ImagesService imagesService;
	private BlobstoreService blobstoreService;

	private static class FutureHolder {
		public Future<Image> futureImage;
		public TransformConfig transformConfig;
		
		public FutureHolder(Future<Image> futureImage, TransformConfig transformConfig) {
			this.futureImage = futureImage;
			this.transformConfig = transformConfig;
		}
	}
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
    	this.fs = new FileService();
    	this.imagesService = ImagesServiceFactory.getImagesService();
    	this.blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	}

	@Override
	protected ApiResponse handleApiRequest(Config conf, HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		UploadResponse uploadResponse = new UploadResponse();
    	List<UploadedFile> uploadedFiles = fs.findUploadedFiles(req);
    	List<String> createdFiles = new ArrayList<>();
    	
    	try {
	    	for (UploadedFile upFile : uploadedFiles) {
	    		boolean deleteOriginal = true;
	    		if (isAllowed(conf, upFile)) {
	    			processUploadedFile(conf, uploadResponse, upFile);
	    		} else {
	    			deleteOriginal = true;
	    		}
	    		if (!conf.original.preserve) deleteOriginal = true;
	    		if (deleteOriginal) deleteOriginalFile(conf, upFile);
	    	}
	    	return uploadResponse;
    	}
    	catch (InvalidUploadException e) {
    		log.log(Level.INFO, "Invalid upload:", e);
    		removeAllFiles(uploadedFiles, createdFiles);
    		return new ErrorResponse(e.getMessage());
    	}
    	catch (TransformationFailureException e) {
    		log.log(Level.WARNING, "Transformation failure", e);
    		List<ServedFile> filesToDelete = e.filesToDelete;
    		for (ServedFile file : filesToDelete) {
    			createdFiles.add(file.fileName);
    		}
    		removeAllFiles(uploadedFiles, createdFiles);
    		return new ErrorResponse(e.getMessage());
    	}
    	catch (Exception e) {
    		log.log(Level.WARNING, "Exception during processing of upload.", e);
    		removeAllFiles(uploadedFiles, createdFiles);
    		return new ErrorResponse("Unable to process uploads");
    	}
	}

	protected void deleteOriginalFile(Config conf, UploadedFile upFile) throws IOException {
		String gsFileName = upFile.fileInfo.getGsObjectName().substring(conf.bucket.length() + 5);
		fs.deleteFileFromCloudStorage(conf.bucket, gsFileName);
	}

	protected void processUploadedFile(Config conf,
			UploadResponse uploadResponse, UploadedFile upFile)
			throws TransformationFailureException {
		ServedFile uploadedFile;
		if (conf.original.preserve) {
			String servingUrl = getServingUrl(upFile, conf);
			String cloudLocation = upFile.fileInfo.getGsObjectName();
			uploadedFile = uploadResponse.addUploadedFile(servingUrl, cloudLocation, upFile.fileInfo.getContentType());
		} else {
			uploadedFile = uploadResponse.addBlankFile();
		}
		List<ServedFile> transformedFiles = applyTransforms(conf, upFile);
		if (transformedFiles != null) uploadedFile.transformed = transformedFiles;
	}

	protected List<ServedFile> applyTransforms(Config conf, UploadedFile upFile) throws TransformationFailureException {
		List<ServedFile> filenames = null;
		if (conf.transforms != null && conf.transforms.length > 0) {
			filenames = new ArrayList<>();
			
			try {
				List<FutureHolder> transformFutures = new ArrayList<>();
				for (Config.TransformConfig transConf : conf.transforms) {
					applyTransformAsync(upFile, transConf, transformFutures);
				}
				processAsyncTransformedImages(transformFutures, conf, filenames);
			} catch (IOException e) {
				log.warning("IO Exception while transforming. Wrapping.");
				throw new TransformationFailureException(e, filenames);
			}
			
		}
		return filenames;
	}

	protected void processAsyncTransformedImages(
			List<FutureHolder> transformFutures, Config conf,
			List<ServedFile> filenames) throws IOException {
		for (FutureHolder fut : transformFutures) {
			log.fine("Getting image file for " + fut.transformConfig.name);
			Image transformedImage;
			try {
				transformedImage = fut.futureImage.get();
				String newFilename = writeImageToFile(transformedImage, conf, fut.transformConfig.encoding);
				log.fine("Saved to file: " + newFilename);
				filenames.add(buildServedFile(newFilename, fut.transformConfig, conf));
			} catch (InterruptedException e) {
				log.log(Level.WARNING, "Interrupt to transformation.", e);
			} catch (ExecutionException e) {
				log.log(Level.WARNING, "Execution error during transformation.", e);
			}
		}
	}

	protected void applyTransformAsync(UploadedFile upFile,
			Config.TransformConfig transConf,
			List<FutureHolder> transformFutures) {
		Transformer trans = TransformationManager.getTransformer(transConf.type);
		if (trans != null) {
			Image image = ImagesServiceFactory.makeImageFromBlob(upFile.blobKey);
			Transform transform = trans.getTransform(image, transConf);
			OutputEncoding enc = (transConf.encoding != null && transConf.encoding.equals("jpeg")) ? OutputEncoding.JPEG : OutputEncoding.PNG; 
			OutputSettings outSet = new OutputSettings(enc);
			if (transConf.quality != null) outSet.setQuality(transConf.quality);
			log.fine("Applying transform " + transConf.name);
			Future<Image> transformedImageFuture = imagesService.applyTransformAsync(transform, image, outSet);
			transformFutures.add(new FutureHolder(transformedImageFuture, transConf));
			log.fine("Applied transform (async)");
		} else {
			log.warning("Unable to find transform for " + transConf.type);
		}
	}

	protected ServedFile buildServedFile(String newFilename,
			TransformConfig transConf, Config conf) {
		String servingUrl = "http://storage.googleapis.com/" + conf.bucket + "/" + newFilename;

		return ServedFile.newTransformedFile(servingUrl, transConf.name, newFilename, "image/" + transConf.encoding);
	}

	protected String writeImageToFile(Image transformedImage, Config conf, String encoding) throws IOException {
		byte[] imageData = transformedImage.getImageData();
		String newFileName = UUID.randomUUID().toString();
		fs.writeFileToCloudStorage(conf.bucket, newFileName, "image/"+encoding, imageData);
		return newFileName;
	}

	public String getServingUrl(UploadedFile upFile, Config conf) {
		String gsObjectName = upFile.fileInfo.getGsObjectName();
		String fileName = gsObjectName.substring(("/gs/" + conf.bucket).length());
		String servingUrl = "http://storage.googleapis.com/" + conf.bucket + fileName;
		return servingUrl;
	}
	
	protected void removeAllFiles(List<UploadedFile> uploadedFiles, List<String> createdFiles) {
		for (UploadedFile file : uploadedFiles) {
			if (file.blobKey != null) {
				blobstoreService.delete(file.blobKey);
			}
		}
	}

	protected boolean isAllowed(Config conf, UploadedFile file) throws InvalidUploadException {
		String type = conf.original.type;
		String contentType = file.fileInfo.getContentType();
		if (type.equals("image")) {
			if (isImage(file)) return true;
		} 
		else if (type.equals("pdf")) {
			if (contentType.equals("application/pdf")) return true;
		}
		else if (type.equals("any")) {
			return true;
		}
		throw new InvalidUploadException("Unacceptable file type: " + contentType);
	}
	
	protected boolean isImage(UploadedFile file) {
		String contentType = file.fileInfo.getContentType();
		if (contentType.equals("image/png") || contentType.equals("image/jpg") || contentType.equals("image/jpeg") || contentType.equals("image/gif")) return true;
		return false;
	}

	@Override
	protected boolean handleGet() {
		return false;
	}


	@Override
	protected boolean handlePost() {
		return true;
	}

}
