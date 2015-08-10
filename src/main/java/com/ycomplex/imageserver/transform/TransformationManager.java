package com.ycomplex.imageserver.transform;

public class TransformationManager {
	public static void init() {
		// We won't cache the Transformers yet.
	}
	
	public static Transformer getTransformer(String type) {
		switch(type) {
			case "max-dim":
				return new MaxDimensionsTransformer();
			case "max-width":
				return new MaxWidthTransformer();
			case "max-height":
				return new MaxHeightTransformer();
			case "scale-crop":
				return new ScaleCropTransformer();
		}
		return null;
	}
}
