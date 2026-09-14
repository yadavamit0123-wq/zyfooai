package com.pt.zyfooai.utils;

import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pt.zyfooai.R;

/**
 * Phase 2 (Option B): frosted glass strip styling for glass frame layouts.
 */
public final class FrameGlassHelper {

    private FrameGlassHelper() {
    }

    public static boolean isGlassFrame(View root) {
        return root != null && root.findViewById(R.id.glassBottomPanel) != null;
    }

    public static void apply(View root, int frameIndex, boolean darkBottom) {
        if (root == null) {
            return;
        }
        View bottomPanel = root.findViewById(R.id.glassBottomPanel);
        View topPanel = root.findViewById(R.id.glassTopPanel);
        applyFrostEffect(bottomPanel);
        applyFrostEffect(topPanel);

        int primary = ContextCompat.getColor(root.getContext(),
                darkBottom ? R.color.frame_glass_text_on_dark : R.color.frame_glass_text_primary);
        int secondary = ContextCompat.getColor(root.getContext(),
                darkBottom ? R.color.frame_glass_text_muted_on_dark : R.color.frame_glass_text_secondary);

        applyTextColor(root.findViewById(R.id.userNameTv), primary);
        applyTextColor(root.findViewById(R.id.businessNameTv), primary);
        applyTextColor(root.findViewById(R.id.userDesTv), secondary);
        applyTextColor(root.findViewById(R.id.businessDesTv), secondary);
        applyTextColor(root.findViewById(R.id.businessAddressTv), primary);
        applyTextColor(root.findViewById(R.id.businessNumberTv), primary);
        applyTextColor(root.findViewById(R.id.businessWebsiteTv), secondary);
        applyTextColor(root.findViewById(R.id.facebookTv), darkBottom ? Color.WHITE : primary);
        applyTextColor(root.findViewById(R.id.instagramTv), darkBottom ? Color.WHITE : primary);
        applyTextColor(root.findViewById(R.id.whatsappTv), darkBottom ? Color.WHITE : primary);

        TextView dateTv = root.findViewById(R.id.dateTv);
        if (dateTv != null) {
            dateTv.setBackgroundResource(R.drawable.bg_frame_glass_date);
            dateTv.setTextColor(FramePolishHelper.accentColorForFrame(frameIndex));
        }

        polishGlassSocialRow(root, darkBottom);
        polishGlassProfile(root.findViewById(R.id.profileLay));
    }

    private static void applyFrostEffect(View panel) {
        if (panel == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            panel.setRenderEffect(RenderEffect.createBlurEffect(18f, 18f, Shader.TileMode.CLAMP));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            panel.setElevation(panel.getResources().getDimension(R.dimen._4sdp));
        }
    }

    private static void applyTextColor(TextView textView, int color) {
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    private static void polishGlassSocialRow(View root, boolean darkBottom) {
        View whatsappLay = root.findViewById(R.id.whatsappLay);
        if (whatsappLay == null || !(whatsappLay.getParent() instanceof LinearLayout)) {
            return;
        }
        LinearLayout strip = (LinearLayout) whatsappLay.getParent();
        if (strip.getId() != R.id.glassSocialRow) {
            return;
        }
        strip.setBackgroundResource(darkBottom
                ? R.drawable.bg_frame_glass_panel_dark
                : R.drawable.bg_frame_glass_panel_light);
        int v = strip.getResources().getDimensionPixelSize(R.dimen._3sdp);
        int h = strip.getResources().getDimensionPixelSize(R.dimen._8sdp);
        strip.setPadding(h, v, h, v);
    }

    private static void polishGlassProfile(View profileLay) {
        if (profileLay == null) {
            return;
        }
        RoundedImageView profileImg = profileLay.findViewById(R.id.profileImg);
        if (profileImg != null) {
            profileImg.setBorderColor(ContextCompat.getColor(profileLay.getContext(), R.color.white));
            profileImg.setBorderWidth(
                    (float) profileLay.getResources().getDimensionPixelSize(R.dimen._2sdp));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileLay.setElevation(
                    profileLay.getResources().getDimension(R.dimen.frame_profile_elevation));
        }
    }
}
