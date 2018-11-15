package com.library.base.imageLoader;

import android.content.Context;
import android.widget.ImageView;


public class GlideImageEngine extends com.youth.banner.loader.ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        ImageLoader.load(((String) path))
                .into(imageView);
    }
}
