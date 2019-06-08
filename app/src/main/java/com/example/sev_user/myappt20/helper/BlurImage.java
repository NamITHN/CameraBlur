package com.example.sev_user.myappt20.helper;
import android.content.Context;
import android.graphics.Bitmap;

import java.io.IOException;

public class BlurImage {

    public static Bitmap getBluredImage(Bitmap rawImage, Context context, int blurLevel) throws IOException {
        int rawWidth = rawImage.getWidth();
        int rawHeight = rawImage.getHeight();
        float resizeRatio = (float) DeeplabModel.INPUT_SIZE / Math.max(rawWidth, rawHeight);
        int resizeWidth = Math.round(rawWidth * resizeRatio);
        int resizeHeight = Math.round(rawHeight * resizeRatio);
        Bitmap resizedImage = BitmapUtil.tfResizeBilinear(rawImage, resizeWidth, resizeHeight);
        int[] segmentImage = DeeplabModel.getInstance(context).segment(resizedImage);
        Bitmap resultImage = GaussianBlur.blur(resizedImage, segmentImage, resizeWidth, blurLevel);
        return resultImage;
    }
}