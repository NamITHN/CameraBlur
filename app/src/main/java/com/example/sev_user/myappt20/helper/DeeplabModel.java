package com.example.sev_user.myappt20.helper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.FileInputStream;
import java.io.IOException;

public class DeeplabModel {
    private final static String MODEL_FILE = "frozen.pb";
    private final static String INPUT_NAME = "ImageTensor";
    private final static String OUTPUT_NAME = "SemanticPredictions";
    public final static int INPUT_SIZE = 513;
    private volatile TensorFlowInferenceInterface sTFInterface = null;

    private static DeeplabModel instance = null;

    private DeeplabModel(Context context) throws IOException {
        if (sTFInterface == null)
            initialize(context);
    }

    public static DeeplabModel getInstance(Context context) throws IOException {
        if(instance == null)
            instance = new DeeplabModel(context);
        return instance;
    }

    public boolean initialize(Context context) throws IOException {
        FileInputStream graphStream = null;
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
        graphStream = fileDescriptor.createInputStream();
        if(graphStream == null)
            return false;
        sTFInterface = new TensorFlowInferenceInterface(graphStream);
        if (sTFInterface == null)
            return false;
        graphStream.close();
        return true;
    }

    public int[] segment(final Bitmap bitmap)
    {
        if (sTFInterface == null) {
            Log.w("TFmodel","tf model is NOT initialized.");
            return null;
        }

        final int w = bitmap.getWidth();
        final int h = bitmap.getHeight();

        if (w > INPUT_SIZE || h > INPUT_SIZE) {
            Log.w("tag","invalid bitmap size");
            return null;
        }

        int[] mIntValues = new int[w * h];
        byte[] mFlatIntValues = new byte[w * h * 3];
        int[] mOutputs = new int[w * h];

        bitmap.getPixels(mIntValues, 0, w, 0, 0, w, h);
        for (int i = 0; i < mIntValues.length; ++i) {
            final int val = mIntValues[i];
            mFlatIntValues[i * 3 + 0] = (byte)((val >> 16) & 0xFF);
            mFlatIntValues[i * 3 + 1] = (byte)((val >> 8) & 0xFF);
            mFlatIntValues[i * 3 + 2] = (byte)(val & 0xFF);
        }

        sTFInterface.feed(INPUT_NAME, mFlatIntValues, 1, h, w, 3 );

        sTFInterface.run(new String[] { OUTPUT_NAME }, true);

        sTFInterface.fetch(OUTPUT_NAME, mOutputs);

        return mOutputs;
    }
}
