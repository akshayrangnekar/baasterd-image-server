package com.ycomplex.imageserver.transform;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.Transform;
import com.ycomplex.imageserver.config.Config.TransformConfig;

public class MaxWidthTransformer extends Transformer {

	@Override
	public Transform getTransform(Image image, TransformConfig transformConfig) {
		Integer width = transformConfig.width;
		
		return ImagesServiceFactory.makeResize(width, 0);
	}

}
