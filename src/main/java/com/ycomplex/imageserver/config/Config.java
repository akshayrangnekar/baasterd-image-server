package com.ycomplex.imageserver.config;

public class Config {
	public String name;
	public String bucket;
	public Boolean serveDirect;
	public Boolean returnCloudPath;
	public Original original;
	public TransformConfig[] transforms;
	
	public static class Original {
		public String type; // Should be 'pdf' or 'image' or 'all'
		public Long maxSize;
		public Boolean preserve;
	}
	
	public static class TransformConfig {
		public String name;
		public String type;
		public Integer width;
		public Integer height;
		public String encoding;
		public Integer quality;
	}
}
