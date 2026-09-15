package com.pt.zyfooai.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;

import com.pt.zyfooai.R;

import eightbitlab.com.blurview.BlurView;

/**
 * Safe view capture for download/share — avoids BlurView and zero-size crashes.
 */
public final class FrameCaptureHelper {

    private FrameCaptureHelper() {
    }

    public static Bitmap capture(View view) {
        if (view == null) {
            return null;
        }
        HideState hideState = hideBlurLayers(view);
        try {
            int width = view.getWidth();
            int height = view.getHeight();
            if (width <= 0 || height <= 0) {
                view.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                );
                width = view.getMeasuredWidth();
                height = view.getMeasuredHeight();
            }
            if (width <= 0 || height <= 0) {
                return null;
            }
            if (view.getWidth() != width || view.getHeight() != height) {
                view.layout(0, 0, width, height);
            }
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(bitmap));
            return bitmap;
        } catch (Exception ignored) {
            return null;
        } finally {
            hideState.restore();
        }
    }

    private static HideState hideBlurLayers(View root) {
        HideState state = new HideState();
        collectBlurLayers(root, state);
        return state;
    }

    private static void collectBlurLayers(View view, HideState state) {
        if (view instanceof BlurView || view.getId() == R.id.frostBackdrop) {
            state.hide(view);
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                collectBlurLayers(group.getChildAt(i), state);
            }
        }
    }

    private static final class HideState {
        private final java.util.List<View> views = new java.util.ArrayList<>();
        private final java.util.List<Integer> visibility = new java.util.ArrayList<>();

        void hide(View view) {
            views.add(view);
            visibility.add(view.getVisibility());
            view.setVisibility(View.INVISIBLE);
        }

        void restore() {
            for (int i = 0; i < views.size(); i++) {
                views.get(i).setVisibility(visibility.get(i));
            }
        }
    }
}
