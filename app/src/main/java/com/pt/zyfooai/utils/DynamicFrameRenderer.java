package com.pt.zyfooai.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.FrameConfig;
import com.pt.zyfooai.model.FrameFooterConfig;

/**
 * Renders admin/server PNG overlay frames with a configurable dynamic footer.
 */
public final class DynamicFrameRenderer {

    private DynamicFrameRenderer() {
    }

    public static boolean isDynamicFrame(View root) {
        return root != null && root.findViewById(R.id.frameOverlayPng) != null;
    }

    @Nullable
    public static FrameConfig getConfig(View root) {
        if (root == null) {
            return null;
        }
        Object tag = root.getTag(R.id.frame_config);
        return tag instanceof FrameConfig ? (FrameConfig) tag : null;
    }

    public static void bind(View root, FrameConfig config, PreferenceManager preferenceManager, int frameIndex) {
        if (root == null || config == null) {
            return;
        }
        root.setTag(R.id.frame_config, config);
        loadOverlay(root, config);
        applyFooter(root, config.footer != null ? config.footer : defaultFooter());
        FrameFooterLayoutHelper.applyEdgeToEdge(root);
    }

    private static FrameFooterConfig defaultFooter() {
        FrameFooterConfig footer = new FrameFooterConfig();
        footer.enabled = true;
        footer.showProfile = true;
        footer.showName = true;
        footer.showPhone = true;
        return footer;
    }

    private static void loadOverlay(View root, FrameConfig config) {
        ImageView overlay = root.findViewById(R.id.frameOverlayPng);
        if (overlay == null) {
            return;
        }
        String source = config.overlaySource();
        if (source == null || source.isEmpty()) {
            overlay.setVisibility(View.GONE);
            overlay.setImageDrawable(null);
            return;
        }
        overlay.setVisibility(View.VISIBLE);
        overlay.setScaleType(ImageView.ScaleType.FIT_XY);
        Object model;
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        if (source.startsWith("http://") || source.startsWith("https://")) {
            model = source;
        } else {
            model = Uri.parse("file:///android_asset/" + source);
        }
        Glide.with(overlay)
                .load(model)
                .apply(options)
                .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                    @Override
                    public boolean onLoadFailed(
                            @Nullable com.bumptech.glide.load.engine.GlideException e,
                            Object model,
                            com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                            boolean isFirstResource
                    ) {
                        overlay.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            android.graphics.drawable.Drawable resource,
                            Object model,
                            com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target,
                            com.bumptech.glide.load.DataSource dataSource,
                            boolean isFirstResource
                    ) {
                        overlay.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(overlay);
    }

    private static void applyFooter(View root, FrameFooterConfig footer) {
        View panel = root.findViewById(R.id.stickerBottomPanel);
        if (!(panel instanceof LinearLayout)) {
            return;
        }
        LinearLayout bottomPanel = (LinearLayout) panel;
        if (footer == null || !footer.enabled) {
            bottomPanel.setVisibility(View.GONE);
            return;
        }
        bottomPanel.setVisibility(View.VISIBLE);
        bottomPanel.setBackground(buildFooterBackground(footer.bgColor));
        applyFooterHeight(root, bottomPanel, footer.heightPercent);

        applyTextSize(root.findViewById(R.id.userNameTv), footer.fontSize, 13f, 11f, 15f);
        applyTextSize(root.findViewById(R.id.userDesTv), footer.fontSize, 9f, 8f, 10f);
        applyTextSize(root.findViewById(R.id.businessAddressTv), footer.fontSize, 9f, 8f, 10f);
        applyTextSize(root.findViewById(R.id.businessNameTv), footer.fontSize, 13f, 11f, 15f);
        applyTextSize(root.findViewById(R.id.businessDesTv), footer.fontSize, 9f, 8f, 10f);
        applyTextSize(root.findViewById(R.id.businessNumberTv), footer.fontSize, 9f, 8f, 10f);

        setTextColor(root.findViewById(R.id.userNameTv), footer.nameColor);
        setTextColor(root.findViewById(R.id.businessNameTv), footer.nameColor);
        setTextColor(root.findViewById(R.id.userDesTv), footer.phoneColor);
        setTextColor(root.findViewById(R.id.businessDesTv), footer.phoneColor);
        setTextColor(root.findViewById(R.id.businessAddressTv), footer.phoneColor);
        setTextColor(root.findViewById(R.id.businessNumberTv), footer.phoneColor);

        setVisibility(root.findViewById(R.id.profileLay), footer.showProfile);
        setVisibility(root.findViewById(R.id.userNameTv), footer.showName);
        setVisibility(root.findViewById(R.id.businessNameTv), footer.showName);
        setVisibility(root.findViewById(R.id.userDesTv), footer.showPhone);
        setVisibility(root.findViewById(R.id.businessAddressTv), footer.showPhone);
        setVisibility(root.findViewById(R.id.businessNumberTv), footer.showPhone);
        setVisibility(root.findViewById(R.id.businessWebsiteTv), footer.showWebsite);

        View socialRow = root.findViewById(R.id.stickerSocialRow);
        if (socialRow != null) {
            socialRow.setVisibility(footer.showPhone || footer.showWebsite ? View.VISIBLE : View.GONE);
        }
    }

    private static void applyFooterHeight(View root, LinearLayout bottomPanel, int heightPercent) {
        if (heightPercent <= 0) {
            bottomPanel.setMinimumHeight(0);
            return;
        }
        Runnable apply = () -> {
            int frameHeight = root.getHeight();
            if (frameHeight <= 0) {
                return;
            }
            int minHeight = Math.round(frameHeight * (heightPercent / 100f));
            bottomPanel.setMinimumHeight(minHeight);
        };
        if (root.getHeight() > 0) {
            apply.run();
        } else {
            root.post(apply);
        }
    }

    private static GradientDrawable buildFooterBackground(String colorHex) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(parseColor(colorHex, 0xCC000000));
        drawable.setCornerRadius(0f);
        return drawable;
    }

    private static int parseColor(String colorHex, int fallback) {
        if (colorHex == null || colorHex.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Color.parseColor(colorHex.trim());
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private static void applyTextSize(TextView textView, String fontSize, float mediumSp, float smallSp, float largeSp) {
        if (textView == null) {
            return;
        }
        float sizeSp = mediumSp;
        if ("small".equalsIgnoreCase(fontSize)) {
            sizeSp = smallSp;
        } else if ("large".equalsIgnoreCase(fontSize)) {
            sizeSp = largeSp;
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, sizeSp);
    }

    private static void setTextColor(TextView textView, String colorHex) {
        if (textView == null) {
            return;
        }
        textView.setTextColor(parseColor(colorHex, textView.getCurrentTextColor()));
    }

    private static void setVisibility(View view, boolean visible) {
        if (view != null) {
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }
}
