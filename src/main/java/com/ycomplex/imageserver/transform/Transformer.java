package com.ycomplex.imageserver.transform;

import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.Transform;
import com.ycomplex.imageserver.config.Config;

public abstract class Transformer {
	public abstract Transform getTransform(Image image, Config.TransformConfig transformConfig);
}
