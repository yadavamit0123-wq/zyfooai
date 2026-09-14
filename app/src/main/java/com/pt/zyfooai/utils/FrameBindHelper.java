package com.pt.zyfooai.utils;

import android.view.View;
import android.widget.TextView;

/**
 * Null-safe helpers for frame overlay binding.
 */
public final class FrameBindHelper {

    private FrameBindHelper() {
    }

    public static void setVisibilityIfEmpty(TextView textView) {
        if (textView == null) {
            return;
        }
        if (textView.getText().toString().trim().isEmpty()) {
            textView.setVisibility(View.GONE);
        }
    }

    public static void setVisibilityIfEmpty(TextView textView, View container) {
        if (textView == null || container == null) {
            return;
        }
        if (textView.getText().toString().trim().isEmpty()) {
            container.setVisibility(View.GONE);
        }
    }

    public static void hideIfPresent(View root, int viewId) {
        if (root == null) {
            return;
        }
        View view = root.findViewById(viewId);
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setTextIfPresent(TextView textView, String value) {
        if (textView != null && value != null) {
            textView.setText(value);
        }
    }
}
