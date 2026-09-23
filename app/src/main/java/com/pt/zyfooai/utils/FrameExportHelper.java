package com.pt.zyfooai.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

/**
 * Scales a captured frame canvas bitmap onto video output dimensions (full-bleed overlay).
 */
public final class FrameExportHelper {

    private FrameExportHelper() {
    }

    public static Bitmap scaleOverlayToVideo(Bitmap source, int videoWidth, int videoHeight) {
        if (source == null || videoWidth <= 0 || videoHeight <= 0) {
            return source;
        }
        if (source.getWidth() == videoWidth && source.getHeight() == videoHeight) {
            return source;
        }
        Bitmap output = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        float scaleX = videoWidth / (float) source.getWidth();
        float scaleY = videoHeight / (float) source.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        canvas.drawBitmap(source, matrix, new Paint(Paint.FILTER_BITMAP_FLAG));
        return output;
    }
}
