package com.ycomplex.imageserver.dto;

import java.util.ArrayList;
import java.util.List;

public class UploadResponse extends ApiResponse {
	public static class ServedFile {
		public String servingUrl;
		public String cloudUrl;
		public String type;
		public String transformName;
		public String fileName;
		public List<ServedFile> transformed;
		
		public ServedFile() {
			
		}
		
		public static ServedFile newTransformedFile(String servingUrl, String transformName, String fileName, String mimeType) {
			ServedFile sf = new ServedFile();
			sf.servingUrl = servingUrl;
			sf.transformName = transformName;
			sf.type = mimeType;
			sf.fileName = fileName;
			return sf;
		}
		
		public static ServedFile newUploadedFile(String servingUrl, String cloudUrl, String type) {
			ServedFile sf = new ServedFile();
			sf.servingUrl = servingUrl;
			sf.cloudUrl = cloudUrl;
			sf.type = type;
			return sf;
		}
	}
	
	private List<ServedFile> files;
	
	public UploadResponse() {
		super(true);
		files = new ArrayList<>();
	}

	public ServedFile addUploadedFile(String servingUrl, String cloudUrl, String type) {
		ServedFile f = ServedFile.newUploadedFile(servingUrl, cloudUrl, type);
		files.add(f);
		return f;
	}
	
	public List<ServedFile> getFiles() {
		return files;
	}

	public void setFiles(List<ServedFile> files) {
		this.files = files;
	}

	public ServedFile addBlankFile() {
		ServedFile f = ServedFile.newUploadedFile(null, null, null);
		files.add(f);
		return f;
	}
	
}
