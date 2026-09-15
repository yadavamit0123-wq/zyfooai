package com.pt.zyfooai.utils;

import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.pt.zyfooai.R;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderEffectBlur;
import eightbitlab.com.blurview.RenderScriptBlur;

/**
 * Backdrop frost for frame panels: blurs media behind the panel while keeping text/icons sharp.
 */
public final class FrameFrostHelper {

    private static final float STICKER_BLUR_RADIUS_DP = 14f;
    private static final float GLASS_BLUR_RADIUS_DP = 18f;

    private FrameFrostHelper() {
    }

    public static void applyFrostPanel(View panel, View blurRoot, View frameRoot) {
        if (panel == null || panel.getVisibility() != View.VISIBLE) {
            return;
        }

        panel.setRenderEffect(null);
        clearRenderEffects(panel);

        if (blurRoot == null || !(blurRoot instanceof ViewGroup)) {
            applyFallbackPanelBackground(panel, frameRoot);
            return;
        }

        int overlayColor = resolveOverlayColor(panel, frameRoot);
        float[] cornerRadii = resolveCornerRadii(panel, frameRoot);
        float blurRadiusDp = isGlassPanel(panel) ? GLASS_BLUR_RADIUS_DP : STICKER_BLUR_RADIUS_DP;

        ensureBlurBackdrop(panel, blurRoot, overlayColor, cornerRadii, blurRadiusDp);
    }

    private static void ensureBlurBackdrop(
            View panel,
            View blurRoot,
            int overlayColor,
            float[] cornerRadii,
            float blurRadiusDp
    ) {
        if (Boolean.TRUE.equals(panel.getTag(R.id.frost_backdrop_bound))) {
            BlurView existing = panel.findViewById(R.id.frostBackdrop);
            if (existing != null) {
                configureBlurView(existing, blurRoot, overlayColor, cornerRadii, blurRadiusDp);
                clearRenderEffects(panel);
                return;
            }
        }

        if (panel instanceof FrameLayout) {
            BlurView existing = panel.findViewById(R.id.frostBackdrop);
            if (existing != null) {
                configureBlurView(existing, blurRoot, overlayColor, cornerRadii, blurRadiusDp);
                panel.setTag(R.id.frost_backdrop_bound, Boolean.TRUE);
                clearRenderEffects(panel);
                return;
            }
        }

        if (!(panel instanceof ViewGroup) || !(panel.getParent() instanceof ViewGroup)) {
            applyFallbackPanelBackground(panel, null);
            return;
        }

        ViewGroup contentGroup = (ViewGroup) panel;
        ViewGroup parent = (ViewGroup) panel.getParent();
        int index = parent.indexOfChild(panel);
        ViewGroup.LayoutParams originalLp = panel.getLayoutParams();
        int panelId = panel.getId();

        panel.setBackground(null);

        FrameLayout wrapper = new FrameLayout(panel.getContext());
        wrapper.setId(panelId);
        wrapper.setLayoutParams(originalLp);
        wrapper.setClipChildren(true);
        wrapper.setClipToPadding(true);
        wrapper.setPadding(
                panel.getPaddingLeft(),
                panel.getPaddingTop(),
                panel.getPaddingRight(),
                panel.getPaddingBottom()
        );
        panel.setPadding(0, 0, 0, 0);

        BlurView blurView = new BlurView(panel.getContext());
        blurView.setId(R.id.frostBackdrop);
        blurView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        FrameLayout.LayoutParams contentLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        contentGroup.setLayoutParams(contentLp);

        parent.removeViewAt(index);
        wrapper.addView(blurView);
        wrapper.addView(contentGroup);
        parent.addView(wrapper, index);

        configureBlurView(blurView, blurRoot, overlayColor, cornerRadii, blurRadiusDp);
        wrapper.setTag(R.id.frost_backdrop_bound, Boolean.TRUE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            wrapper.setElevation(wrapper.getResources().getDimension(R.dimen._4sdp));
        }
        clearRenderEffects(wrapper);
    }

    private static void configureBlurView(
            BlurView blurView,
            View blurRoot,
            int overlayColor,
            float[] cornerRadii,
            float blurRadiusDp
    ) {
        if (blurView == null || blurRoot == null || !(blurRoot instanceof ViewGroup)) {
            return;
        }

        float radiusPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                blurRadiusDp,
                blurView.getResources().getDisplayMetrics()
        );

        Drawable windowBackground = blurRoot.getBackground();
        if (windowBackground == null) {
            windowBackground = new ColorDrawable(Color.TRANSPARENT);
        }

        eightbitlab.com.blurview.BlurAlgorithm algorithm;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            algorithm = new RenderEffectBlur();
        } else {
            algorithm = new RenderScriptBlur(blurView.getContext());
        }

        blurView.setupWith((ViewGroup) blurRoot, algorithm)
                .setFrameClearDrawable(windowBackground)
                .setBlurRadius(radiusPx)
                .setOverlayColor(overlayColor)
                .setBlurAutoUpdate(true);

        applyCornerOutline(blurView, cornerRadii);
    }

    private static void applyCornerOutline(BlurView blurView, float[] cornerRadii) {
        if (blurView == null || cornerRadii == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        float cornerRadius = Math.max(cornerRadii[0], cornerRadii[2]);
        blurView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), cornerRadius);
            }
        });
        blurView.setClipToOutline(true);
    }

    private static void applyFallbackPanelBackground(View panel, View frameRoot) {
        if (panel == null) {
            return;
        }
        if (panel.getBackground() != null) {
            return;
        }
        int panelId = panel.getId();
        if (panelId == R.id.stickerBottomPanel) {
            if (frameRoot != null && FrameStickerHelper.isGoldSticker(frameRoot)) {
                panel.setBackgroundResource(R.drawable.bg_frame_sticker_gold_border);
            } else {
                panel.setBackgroundResource(R.drawable.bg_frame_sticker_chip);
            }
        } else if (panelId == R.id.stickerNameTag) {
            panel.setBackgroundResource(R.drawable.bg_frame_sticker_nametag);
        } else if (panelId == R.id.glassBottomPanel) {
            boolean dark = frameRoot != null && frameRoot.findViewById(R.id.glassTopPanel) != null;
            panel.setBackgroundResource(dark
                    ? R.drawable.bg_frame_glass_panel_dark
                    : R.drawable.bg_frame_glass_panel_light);
        } else if (panelId == R.id.glassTopPanel) {
            panel.setBackgroundResource(R.drawable.bg_frame_glass_top_pill);
        }
    }

    private static int resolveOverlayColor(View panel, View frameRoot) {
        int panelId = panel.getId();
        if (panelId == R.id.stickerNameTag) {
            return ContextCompat.getColor(panel.getContext(), R.color.frame_sticker_nametag_fill);
        }
        if (panelId == R.id.glassTopPanel) {
            return ContextCompat.getColor(panel.getContext(), R.color.frame_glass_dark_fill);
        }
        if (panelId == R.id.glassBottomPanel) {
            boolean dark = frameRoot != null && frameRoot.findViewById(R.id.glassTopPanel) != null;
            return ContextCompat.getColor(
                    panel.getContext(),
                    dark ? R.color.frame_glass_dark_fill : R.color.frame_glass_light_fill
            );
        }
        if (panelId == R.id.stickerBottomPanel
                && frameRoot != null
                && FrameStickerHelper.isGoldSticker(frameRoot)) {
            return ContextCompat.getColor(panel.getContext(), R.color.frame_sticker_gold_fill);
        }
        return ContextCompat.getColor(panel.getContext(), R.color.frame_sticker_chip_fill);
    }

    private static float[] resolveCornerRadii(View panel, View frameRoot) {
        float pill = panel.getResources().getDimension(R.dimen.frame_sticker_corner_pill);
        float glassTop = panel.getResources().getDimension(R.dimen.frame_glass_corner_top);

        int panelId = panel.getId();
        if (panelId == R.id.stickerNameTag) {
            return new float[]{pill, pill, pill, pill, pill, pill, pill, pill};
        }
        if (panelId == R.id.glassBottomPanel || panelId == R.id.glassTopPanel) {
            if (panelId == R.id.glassTopPanel) {
                return new float[]{pill, pill, pill, pill, pill, pill, pill, pill};
            }
            return new float[]{glassTop, glassTop, glassTop, glassTop, 0f, 0f, 0f, 0f};
        }
        return new float[]{pill, pill, pill, pill, pill, pill, pill, pill};
    }

    private static boolean isGlassPanel(View panel) {
        int id = panel.getId();
        return id == R.id.glassBottomPanel || id == R.id.glassTopPanel;
    }

    private static void clearRenderEffects(View root) {
        if (root == null) {
            return;
        }
        root.setRenderEffect(null);
        if (!(root instanceof ViewGroup)) {
            return;
        }
        ViewGroup group = (ViewGroup) root;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child instanceof BlurView) {
                continue;
            }
            child.setRenderEffect(null);
            if (child instanceof ViewGroup) {
                clearRenderEffects(child);
            }
        }
    }
}
