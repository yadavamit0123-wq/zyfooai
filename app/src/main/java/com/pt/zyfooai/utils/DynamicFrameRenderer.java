package com.pt.zyfooai.utils;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.FrameConfig;
import com.pt.zyfooai.model.FrameFooterConfig;
import com.pt.zyfooai.model.FrameFooterContentInset;

/**
 * Renders admin/server PNG overlay frames with a configurable dynamic footer.
 */
public final class DynamicFrameRenderer {

    private static final float ABOVE_FOOTER_OVERLAP_FRACTION = 0.45f;
    private static final int RING_GOLD = 0xFFE8D59A;
    private static final int RING_WHITE = 0xFFFFFFFF;

    private DynamicFrameRenderer() {
    }

    public static boolean isDynamicFrame(View root) {
        return root != null && root.findViewById(R.id.frameOverlayPng) != null;
    }

    /**
     * App-drawn footer only when API sends {@code footer.enabled: true}.
     * Missing footer or {@code enabled: false} → PNG owns branding (Tiranga rule).
     */
    public static boolean shouldShowAppFooter(@Nullable FrameConfig config) {
        return config != null && config.footer != null && config.footer.enabled;
    }

    public static void suppressAppFooter(View root) {
        if (root == null) {
            return;
        }
        View panel = root.findViewById(R.id.stickerBottomPanel);
        if (panel != null) {
            panel.setVisibility(View.GONE);
        }
        View socialRow = root.findViewById(R.id.stickerSocialRow);
        if (socialRow != null) {
            socialRow.setVisibility(View.GONE);
        }
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
        loadOverlay(root, config, preferenceManager);
        if (shouldShowAppFooter(config)) {
            applyFooter(root, config.footer);
            FrameFooterLayoutHelper.applyEdgeToEdge(root);
        } else {
            suppressAppFooter(root);
        }
        scheduleLayoutRefresh(root, preferenceManager);
    }

    private static void scheduleLayoutRefresh(View frameRoot, PreferenceManager preferenceManager) {
        if (frameRoot == null) {
            return;
        }
        Runnable refresh = () -> {
            Object blurRoot = frameRoot.getTag(R.id.frost_blur_root);
            if (!(blurRoot instanceof View)) {
                return;
            }
            View contentArea = (View) blurRoot;
            RecyclerView frameRecyclerView = contentArea.findViewById(R.id.recyclerview);
            if (frameRecyclerView == null) {
                return;
            }
            FrameCanvasHelper.apply(contentArea, frameRecyclerView);
            FrameMediaInsetHelper.apply(contentArea, frameRecyclerView, preferenceManager);
        };
        frameRoot.post(refresh);
    }

    private static void loadOverlay(View root, FrameConfig config, PreferenceManager preferenceManager) {
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
                        scheduleLayoutRefresh(root, preferenceManager);
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
            suppressAppFooter(root);
            return;
        }
        bottomPanel.setVisibility(View.VISIBLE);
        bottomPanel.setClipChildren(false);
        bottomPanel.setClipToPadding(false);
        if (root instanceof ViewGroup) {
            ((ViewGroup) root).setClipChildren(false);
            ((ViewGroup) root).setClipToPadding(false);
        }
        if (bottomPanel.getParent() instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) bottomPanel.getParent();
            parent.setClipChildren(false);
            parent.setClipToPadding(false);
        }

        bottomPanel.setBackground(buildFooterBackground(footer.bgColor));
        applyFooterHeight(root, bottomPanel, footer);
        applyContentInset(root, bottomPanel, footer);
        applyProfile(root, bottomPanel, footer);

        applyTextSize(root.findViewById(R.id.userNameTv), footer.fontSize, 13f, 11f, 15f);
        applyTextSize(root.findViewById(R.id.userDesTv), footer.fontSize, 9f, 8f, 10f);
        applyTextSize(root.findViewById(R.id.businessAddressTv), footer.fontSize, 9f, 8f, 10f);
        applyTextSize(root.findViewById(R.id.businessNameTv), footer.fontSize, 13f, 11f, 15f);
        applyTextSize(root.findViewById(R.id.businessDesTv), footer.fontSize, 9f, 8f, 10f);
        applyTextSize(root.findViewById(R.id.businessNumberTv), footer.fontSize, 9f, 8f, 10f);

        applyFontFamily(root, footer.fontFamily, true,
                R.id.userNameTv, R.id.businessNameTv);
        applyFontFamily(root, footer.fontFamily, false,
                R.id.userDesTv, R.id.businessDesTv, R.id.businessAddressTv,
                R.id.businessNumberTv, R.id.businessWebsiteTv);

        setTextColor(root.findViewById(R.id.userNameTv), footer.nameColor);
        setTextColor(root.findViewById(R.id.businessNameTv), footer.nameColor);
        setTextColor(root.findViewById(R.id.userDesTv), footer.phoneColor);
        setTextColor(root.findViewById(R.id.businessDesTv), footer.phoneColor);
        setTextColor(root.findViewById(R.id.businessAddressTv), footer.phoneColor);
        setTextColor(root.findViewById(R.id.businessNumberTv), footer.phoneColor);

        applyClipOverflow(root, footer.clipOverflow,
                R.id.userNameTv, R.id.userDesTv, R.id.businessNameTv,
                R.id.businessDesTv, R.id.businessAddressTv, R.id.businessNumberTv,
                R.id.businessWebsiteTv);

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

    private static void applyFooterHeight(View root, LinearLayout bottomPanel, FrameFooterConfig footer) {
        int heightPercent = footer != null ? footer.heightPercent : 0;
        Runnable apply = () -> {
            int frameHeight = root.getHeight();
            if (frameHeight <= 0 || heightPercent <= 0) {
                ViewGroup.LayoutParams params = bottomPanel.getLayoutParams();
                if (params != null && heightPercent <= 0) {
                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    bottomPanel.setLayoutParams(params);
                }
                bottomPanel.setMinimumHeight(0);
                return;
            }
            int barHeight = Math.round(frameHeight * (heightPercent / 100f));
            ViewGroup.LayoutParams params = bottomPanel.getLayoutParams();
            if (params != null) {
                // Fixed bar height — independent of profile size (thin footer + large avatar).
                params.height = barHeight;
                bottomPanel.setLayoutParams(params);
            }
            bottomPanel.setMinimumHeight(barHeight);
        };
        if (root.getHeight() > 0) {
            apply.run();
        } else {
            root.post(apply);
        }
    }

    private static void applyContentInset(View root, LinearLayout bottomPanel, FrameFooterConfig footer) {
        FrameFooterContentInset inset = footer != null ? footer.contentInset : null;
        Runnable apply = () -> {
            int barH = bottomPanel.getHeight();
            int barW = bottomPanel.getWidth();
            if (barH <= 0) {
                barH = bottomPanel.getMinimumHeight();
            }
            if (barH <= 0 && root.getHeight() > 0 && footer != null && footer.heightPercent > 0) {
                barH = Math.round(root.getHeight() * (footer.heightPercent / 100f));
            }
            if (barW <= 0) {
                barW = root.getWidth();
            }
            if (inset == null || barH <= 0) {
                return;
            }
            int top = Math.round(barH * (Math.max(0, inset.topPercent) / 100f));
            int bottom = Math.round(barH * (Math.max(0, inset.bottomPercent) / 100f));
            int left = Math.round(barW * (Math.max(0, inset.leftPercent) / 100f));
            int right = Math.round(barW * (Math.max(0, inset.rightPercent) / 100f));
            bottomPanel.setPadding(left, top, right, bottom);
        };
        if (root.getWidth() > 0) {
            apply.run();
            bottomPanel.post(apply);
        } else {
            root.post(apply);
        }
    }

    private static void applyProfile(View root, LinearLayout bottomPanel, FrameFooterConfig footer) {
        if (footer == null || !footer.showProfile) {
            return;
        }
        View profileLay = root.findViewById(R.id.profileLay);
        View profileImg = root.findViewById(R.id.profileImg);
        if (profileLay == null) {
            return;
        }
        if (profileLay.getParent() instanceof ViewGroup) {
            ((ViewGroup) profileLay.getParent()).setClipChildren(false);
            ((ViewGroup) profileLay.getParent()).setClipToPadding(false);
        }

        Runnable apply = () -> {
            int canvasW = root.getWidth();
            int canvasH = root.getHeight();
            int barH = bottomPanel.getHeight();
            if (barH <= 0) {
                barH = bottomPanel.getMinimumHeight();
            }
            if (barH <= 0 && canvasH > 0 && footer.heightPercent > 0) {
                barH = Math.round(canvasH * (footer.heightPercent / 100f));
            }

            int outerPx;
            int imgPx;
            if (footer.hasProfileScale() && canvasW > 0) {
                outerPx = Math.round(canvasW * (footer.profileScalePercent / 100f));
                imgPx = Math.max(1, Math.round(outerPx * 0.88f));
            } else {
                int[] legacy = legacyProfilePx(root, footer.profileSize);
                outerPx = legacy[0];
                imgPx = legacy[1];
            }

            // in_footer + max height: avatar cannot exceed % of footer bar.
            if (!footer.isAboveFooter()
                    && footer.profileMaxHeightPercent != null
                    && footer.profileMaxHeightPercent > 0
                    && barH > 0) {
                int maxOuter = Math.round(barH * (footer.profileMaxHeightPercent / 100f));
                if (outerPx > maxOuter) {
                    float scale = maxOuter / (float) outerPx;
                    outerPx = maxOuter;
                    imgPx = Math.max(1, Math.round(imgPx * scale));
                }
            }

            ViewGroup.LayoutParams outerParams = profileLay.getLayoutParams();
            if (outerParams != null) {
                outerParams.width = outerPx;
                outerParams.height = outerPx;
                if (outerParams instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) outerParams;
                    if (footer.isAboveFooter()) {
                        // ~45% of avatar floats above the footer bar top edge (Crafto).
                        marginParams.topMargin = -Math.round(outerPx * ABOVE_FOOTER_OVERLAP_FRACTION);
                        marginParams.bottomMargin = 0;
                    } else {
                        marginParams.topMargin = 0;
                        marginParams.bottomMargin = 0;
                    }
                }
                profileLay.setLayoutParams(outerParams);
            }

            if (profileImg != null) {
                ViewGroup.LayoutParams imgParams = profileImg.getLayoutParams();
                if (imgParams != null) {
                    imgParams.width = imgPx;
                    imgParams.height = imgPx;
                    profileImg.setLayoutParams(imgParams);
                }
                applyProfileStyle(profileImg, footer.profileStyle, imgPx);
            }
        };

        if (root.getWidth() > 0) {
            apply.run();
        } else {
            root.post(apply);
        }
        bottomPanel.post(apply);
    }

    private static int[] legacyProfilePx(View root, String size) {
        String normalized = size != null ? size.trim() : "medium";
        int outerRes;
        int imgRes;
        if ("large".equalsIgnoreCase(normalized)) {
            outerRes = R.dimen.frame_footer_profile_outer_wide;
            imgRes = R.dimen.frame_footer_profile_img_wide;
        } else {
            outerRes = R.dimen.frame_footer_profile_outer;
            imgRes = R.dimen.frame_footer_profile_img;
        }
        int outerPx = root.getResources().getDimensionPixelSize(outerRes);
        int imgPx = root.getResources().getDimensionPixelSize(imgRes);
        if ("small".equalsIgnoreCase(normalized)) {
            outerPx = Math.round(outerPx * 0.85f);
            imgPx = Math.round(imgPx * 0.85f);
        }
        return new int[]{outerPx, imgPx};
    }

    private static void applyProfileStyle(View profileImg, String style, int imgPx) {
        if (!(profileImg instanceof RoundedImageView)) {
            return;
        }
        RoundedImageView riv = (RoundedImageView) profileImg;
        String normalized = style != null ? style.trim().toLowerCase() : "circle";
        float circleRadius = imgPx / 2f;
        float border = profileImg.getResources().getDimensionPixelSize(R.dimen._2sdp);
        riv.setBorderWidth(0f);
        riv.setElevation(0f);

        switch (normalized) {
            case "ring_gold":
                riv.setCornerRadius(circleRadius);
                riv.setBorderWidth(border * 1.25f);
                riv.setBorderColor(RING_GOLD);
                break;
            case "ring_white":
                riv.setCornerRadius(circleRadius);
                riv.setBorderWidth(border);
                riv.setBorderColor(RING_WHITE);
                break;
            case "rounded":
                riv.setCornerRadius(imgPx * 0.22f);
                riv.setBorderWidth(border * 0.75f);
                riv.setBorderColor(RING_WHITE);
                break;
            case "soft_shadow":
                riv.setCornerRadius(circleRadius);
                riv.setBorderWidth(border * 0.5f);
                riv.setBorderColor(0x66FFFFFF);
                riv.setElevation(profileImg.getResources().getDimension(R.dimen.frame_profile_elevation));
                break;
            case "circle":
            default:
                riv.setCornerRadius(circleRadius);
                riv.setBorderWidth(border);
                riv.setBorderColor(RING_WHITE);
                break;
        }
    }

    private static void applyClipOverflow(View root, boolean clipOverflow, int... viewIds) {
        for (int id : viewIds) {
            View view = root.findViewById(id);
            if (!(view instanceof TextView)) {
                continue;
            }
            TextView textView = (TextView) view;
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            if (clipOverflow) {
                textView.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                textView.setEllipsize(null);
            }
        }
    }

    private static void applyFontFamily(View root, String fontFamily, boolean bold, int... viewIds) {
        Typeface typeface = resolveFont(root, fontFamily, bold);
        if (typeface == null) {
            return;
        }
        for (int id : viewIds) {
            View view = root.findViewById(id);
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    @Nullable
    private static Typeface resolveFont(View root, String fontFamily, boolean bold) {
        String family = fontFamily != null ? fontFamily.trim().toLowerCase() : "";
        try {
            switch (family) {
                case "bebas":
                    return Typeface.createFromAsset(root.getContext().getAssets(), "fonts/Teko-SemiBold.ttf");
                case "playfair":
                    return Typeface.createFromAsset(root.getContext().getAssets(), "fonts/Laila-SemiBold.ttf");
                case "montserrat":
                case "raleway":
                case "poppins":
                default:
                    int res = bold ? R.font.inter_semi_bold : R.font.inter_regular;
                    return ResourcesCompat.getFont(root.getContext(), res);
            }
        } catch (RuntimeException ignored) {
            try {
                int res = bold ? R.font.inter_semi_bold : R.font.inter_regular;
                return ResourcesCompat.getFont(root.getContext(), res);
            } catch (RuntimeException ignored2) {
                return null;
            }
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
