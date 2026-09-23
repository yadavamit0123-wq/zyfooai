package com.pt.zyfooai.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.FrameConfig;

/**
 * Sizes the feed frame canvas from server {@link FrameConfig#aspectRatio} (single-box rule).
 */
public final class FrameCanvasHelper {

    private FrameCanvasHelper() {
    }

    @Nullable
    public static FrameConfig getActiveFrameConfig(RecyclerView frameRecyclerView) {
        View frameRoot = FrameMediaInsetHelper.getVisibleFrameRoot(frameRecyclerView);
        if (frameRoot == null) {
            return null;
        }
        return DynamicFrameRenderer.getConfig(frameRoot);
    }

    public static boolean isActiveServerFrame(RecyclerView frameRecyclerView) {
        return getActiveFrameConfig(frameRecyclerView) != null;
    }

    public static void apply(View contentArea, RecyclerView frameRecyclerView) {
        if (contentArea == null || frameRecyclerView == null) {
            return;
        }
        View mainLayout = contentArea.findViewById(R.id.mainLayOut);
        if (mainLayout == null) {
            return;
        }
        Runnable applySize = () -> {
            FrameConfig config = getActiveFrameConfig(frameRecyclerView);
            int width = contentArea.getWidth();
            if (width <= 0) {
                width = mainLayout.getWidth();
            }
            if (width <= 0) {
                return;
            }
            ViewGroup.LayoutParams layoutParams = mainLayout.getLayoutParams();
            if (layoutParams == null) {
                return;
            }
            if (config != null && config.aspectRatio != null && !config.aspectRatio.trim().isEmpty()) {
                int availableHeight = contentArea.getHeight();
                int targetWidth = width;
                int targetHeight = heightForWidth(targetWidth, config.aspectRatio);
                boolean heightCapped = false;
                if (availableHeight > 0 && targetHeight > availableHeight) {
                    targetHeight = availableHeight;
                    targetWidth = widthForHeight(targetHeight, config.aspectRatio);
                    heightCapped = true;
                }
                layoutParams.width = heightCapped
                        ? targetWidth
                        : ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = targetHeight;
                if (layoutParams instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams relative = (RelativeLayout.LayoutParams) layoutParams;
                    relative.addRule(RelativeLayout.CENTER_IN_PARENT);
                }
            } else {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                if (layoutParams instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams relative = (RelativeLayout.LayoutParams) layoutParams;
                    relative.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    relative.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
                }
            }
            mainLayout.setLayoutParams(layoutParams);
            mainLayout.requestLayout();
        };
        if (contentArea.getWidth() > 0) {
            applySize.run();
        } else {
            contentArea.post(applySize);
        }
    }

    /** Height = width × (aspect height / aspect width). Supports 1:1, 4:5, 9:16. */
    public static int widthForHeight(int height, String aspectRatio) {
        if (height <= 0 || aspectRatio == null) {
            return height;
        }
        String normalized = aspectRatio.trim().replace(" ", "");
        String[] parts = normalized.split(":");
        if (parts.length != 2) {
            return height;
        }
        try {
            float aspectW = Float.parseFloat(parts[0]);
            float aspectH = Float.parseFloat(parts[1]);
            if (aspectW <= 0f || aspectH <= 0f) {
                return height;
            }
            return Math.round(height * (aspectW / aspectH));
        } catch (NumberFormatException ignored) {
            return height;
        }
    }

    public static int heightForWidth(int width, String aspectRatio) {
        if (width <= 0 || aspectRatio == null) {
            return width;
        }
        String normalized = aspectRatio.trim().replace(" ", "");
        String[] parts = normalized.split(":");
        if (parts.length != 2) {
            return width;
        }
        try {
            float aspectW = Float.parseFloat(parts[0]);
            float aspectH = Float.parseFloat(parts[1]);
            if (aspectW <= 0f || aspectH <= 0f) {
                return width;
            }
            return Math.round(width * (aspectH / aspectW));
        } catch (NumberFormatException ignored) {
            return width;
        }
    }
}
