package com.pt.zyfooai.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    public static void bind(View root, FrameConfig config, PreferenceManager preferenceManager, int frameIndex) {
        if (root == null || config == null) {
            return;
        }
        root.setTag(R.id.frame_config, config);
        loadOverlay(root, config);
        applyFooter(root, config.footer);
        FrameFooterLayoutHelper.applyEdgeToEdge(root);
    }

    private static void loadOverlay(View root, FrameConfig config) {
        ImageView overlay = root.findViewById(R.id.frameOverlayPng);
        if (overlay == null) {
            return;
        }
        String source = config.overlaySource();
        if (source == null || source.isEmpty()) {
            overlay.setVisibility(View.GONE);
            return;
        }
        overlay.setVisibility(View.VISIBLE);
        Object model;
        if (source.startsWith("http://") || source.startsWith("https://")) {
            model = source;
        } else {
            model = Uri.parse("file:///android_asset/" + source);
        }
        Glide.with(overlay)
                .load(model)
                .fitCenter()
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
