package com.example.handyproject.ui.common.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompressor {

    private static final int MAX_DIMENSION = 1600;
    private static final int JPEG_QUALITY = 85;

    public static byte[] compress(Context context, Uri uri) throws IOException {
        ContentResolver resolver = context.getContentResolver();

        BitmapFactory.Options boundsOptions = new BitmapFactory.Options();
        boundsOptions.inJustDecodeBounds = true;
        try (InputStream boundsStream = resolver.openInputStream(uri)) {
            BitmapFactory.decodeStream(boundsStream, null, boundsOptions);
        }

        BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
        decodeOptions.inSampleSize = calculateInSampleSize(boundsOptions, MAX_DIMENSION);

        Bitmap sampledBitmap;
        try (InputStream decodeStream = resolver.openInputStream(uri)) {
            sampledBitmap = BitmapFactory.decodeStream(decodeStream, null, decodeOptions);
        }

        if (sampledBitmap == null) {
            throw new IOException("Could not decode image");
        }

        Bitmap scaledBitmap = scaleToMaxDimension(sampledBitmap, MAX_DIMENSION);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, outputStream);

        if (scaledBitmap != sampledBitmap) {
            sampledBitmap.recycle();
        }
        scaledBitmap.recycle();

        return outputStream.toByteArray();
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int maxDimension) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;

        int longestSide = Math.max(height, width);
        while (longestSide / (inSampleSize * 2) >= maxDimension) {
            inSampleSize *= 2;
        }

        return inSampleSize;
    }

    private static Bitmap scaleToMaxDimension(Bitmap bitmap, int maxDimension) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int longestSide = Math.max(width, height);

        if (longestSide <= maxDimension) {
            return bitmap;
        }

        float scale = (float) maxDimension / longestSide;
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
