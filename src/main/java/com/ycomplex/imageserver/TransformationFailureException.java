package com.ycomplex.imageserver;

import java.io.IOException;
import java.util.List;

import com.ycomplex.imageserver.dto.UploadResponse.ServedFile;

public class TransformationFailureException extends Exception {
	private static final long serialVersionUID = 351041947875604859L;
	public List<ServedFile> filesToDelete;
	
	public TransformationFailureException(IOException cause, List<ServedFile> filenames) {
		super(cause);
		this.filesToDelete = filenames;
	}
}
