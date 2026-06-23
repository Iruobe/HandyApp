package com.example.handyproject.ui.common.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.example.handyproject.R;

public class ImageUtils {

    public static void loadAvatar(ImageView iv, String url) {
        Glide.with(iv)
                .load(url)
                .placeholder(R.drawable.defaultprofile)
                .error(R.drawable.defaultprofile)
                .fallback(R.drawable.defaultprofile)
                .transform(new CircleCrop())
                .into(iv);
    }

    public static void loadImage(ImageView iv, String url) {
        Glide.with(iv)
                .load(url)
                .placeholder(R.drawable.defaultprofile)
                .error(R.drawable.defaultprofile)
                .fallback(R.drawable.defaultprofile)
                .into(iv);
    }
}
