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

    /**
     * Largest width/height box with the given aspect ratio that fits inside maxWidth × maxHeight.
     */
    public static int[] fitAspectBox(int maxWidth, int maxHeight, String aspectRatio) {
        if (maxWidth <= 0 || maxHeight <= 0) {
            return new int[]{maxWidth, maxHeight};
        }
        int targetWidth = maxWidth;
        int targetHeight = heightForWidth(targetWidth, aspectRatio);
        if (targetHeight > maxHeight) {
            targetHeight = maxHeight;
            targetWidth = widthForHeight(targetHeight, aspectRatio);
        }
        return new int[]{targetWidth, targetHeight};
    }

    public static void apply(View contentArea, RecyclerView frameRecyclerView) {
        if (contentArea == null || frameRecyclerView == null) {
            return;
        }
        View mainLayout = contentArea.findViewById(R.id.mainLayOut);
        if (mainLayout == null) {
            return;
        }
        View canvasShell = mainLayout.getParent() instanceof ViewGroup
                ? (View) mainLayout.getParent()
                : mainLayout;

        Runnable applySize = () -> {
            FrameConfig config = getActiveFrameConfig(frameRecyclerView);
            int areaWidth = contentArea.getWidth();
            int areaHeight = contentArea.getHeight();
            if (areaWidth <= 0) {
                areaWidth = contentArea.getMeasuredWidth();
            }
            if (areaHeight <= 0) {
                areaHeight = contentArea.getMeasuredHeight();
            }
            if (areaWidth <= 0) {
                return;
            }

            int marginW = 0;
            int marginH = 0;
            ViewGroup.LayoutParams shellParams = canvasShell.getLayoutParams();
            if (shellParams instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) shellParams;
                marginW = marginParams.leftMargin + marginParams.rightMargin;
                marginH = marginParams.topMargin + marginParams.bottomMargin;
            }

            ViewGroup.LayoutParams mainParams = mainLayout.getLayoutParams();
            if (mainParams == null || shellParams == null) {
                return;
            }

            if (config != null) {
                String aspect = config.resolvedAspectRatio();
                int maxW = Math.max(1, areaWidth - marginW);
                int maxH = areaHeight > 0 ? Math.max(1, areaHeight - marginH) : maxW * 16 / 9;
                int[] box = fitAspectBox(maxW, maxH, aspect);

                shellParams.width = box[0];
                shellParams.height = box[1];
                applyShellRules(shellParams, config);

                mainParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mainParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                clearMainLayoutRules(mainParams);
            } else {
                shellParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                shellParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                applyShellRules(shellParams, null);

                mainParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                mainParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                clearMainLayoutRules(mainParams);
            }

            canvasShell.setLayoutParams(shellParams);
            mainLayout.setLayoutParams(mainParams);
            canvasShell.requestLayout();
            mainLayout.requestLayout();
        };

        if (contentArea.getWidth() > 0) {
            applySize.run();
        } else {
            contentArea.post(applySize);
        }
    }

    /**
     * Server canvases pin to the bottom of the content area (same anchor as bundled sticker/gradient frames).
     * Bundled XML frames keep a full-height card.
     */
    private static void applyShellRules(ViewGroup.LayoutParams layoutParams, @Nullable FrameConfig config) {
        if (!(layoutParams instanceof RelativeLayout.LayoutParams)) {
            return;
        }
        RelativeLayout.LayoutParams relative = (RelativeLayout.LayoutParams) layoutParams;
        if (config != null) {
            relative.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            relative.addRule(RelativeLayout.CENTER_HORIZONTAL);
            relative.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            relative.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
        } else {
            relative.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            relative.addRule(RelativeLayout.CENTER_HORIZONTAL);
            relative.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            relative.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        }
    }

    private static void clearMainLayoutRules(ViewGroup.LayoutParams layoutParams) {
        if (layoutParams instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams relative = (RelativeLayout.LayoutParams) layoutParams;
            relative.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            relative.addRule(RelativeLayout.ALIGN_PARENT_TOP);
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
