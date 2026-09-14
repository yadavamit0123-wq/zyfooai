package com.pt.zyfooai.utils;

import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pt.zyfooai.R;

/**
 * Phase 3 (Option C): premium gradient styling for business frame layouts.
 */
public final class FrameGradientHelper {

    private FrameGradientHelper() {
    }

    public static boolean isGradientFrame(View root) {
        return root != null && root.findViewById(R.id.gradientBottomPanel) != null;
    }

    public static void apply(View root, int frameIndex) {
        if (root == null) {
            return;
        }
        boolean goldVariant = root.findViewById(R.id.gradientGoldAccent) != null;

        View bottomPanel = root.findViewById(R.id.gradientBottomPanel);
        View topLay = root.findViewById(R.id.topLay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (bottomPanel != null) {
                bottomPanel.setElevation(bottomPanel.getResources().getDimension(R.dimen._6sdp));
            }
            if (topLay != null) {
                topLay.setElevation(topLay.getResources().getDimension(R.dimen._4sdp));
            }
        }

        int primary = ContextCompat.getColor(root.getContext(), R.color.frame_gradient_text_on_gradient);
        int muted = ContextCompat.getColor(root.getContext(), R.color.frame_gradient_text_muted);
        int accent = ContextCompat.getColor(root.getContext(),
                goldVariant ? R.color.frame_gradient_gold_accent : R.color.frame_gradient_purple_end);

        applyTextColor(root.findViewById(R.id.userNameTv), primary);
        applyTextColor(root.findViewById(R.id.businessNameTv), primary);
        applyTextColor(root.findViewById(R.id.userDesTv), muted);
        applyTextColor(root.findViewById(R.id.businessDesTv), muted);
        applyTextColor(root.findViewById(R.id.businessNumberTv), primary);
        applyTextColor(root.findViewById(R.id.facebookTv), primary);
        applyTextColor(root.findViewById(R.id.instagramTv), primary);
        applyTextColor(root.findViewById(R.id.whatsappTv), primary);

        if (goldVariant) {
            applyTextColor(root.findViewById(R.id.businessWebsiteTv),
                    ContextCompat.getColor(root.getContext(), R.color.frame_gradient_gold_accent));
            applyTextColor(root.findViewById(R.id.businessAddressTv),
                    ContextCompat.getColor(root.getContext(), R.color.frame_gradient_gold_accent));
        } else {
            applyTextColor(root.findViewById(R.id.businessWebsiteTv), muted);
            applyTextColor(root.findViewById(R.id.businessAddressTv), primary);
        }

        TextView dateTv = root.findViewById(R.id.dateTv);
        if (dateTv != null) {
            dateTv.setBackgroundResource(R.drawable.bg_frame_gradient_date);
            dateTv.setTextColor(accent);
            dateTv.setTypeface(dateTv.getTypeface(), Typeface.BOLD);
        }

        polishGradientSocialRow(root);
        polishGradientProfile(root.findViewById(R.id.profileLay), goldVariant);
    }

    private static void applyTextColor(TextView textView, int color) {
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    private static void polishGradientSocialRow(View root) {
        LinearLayout strip = root.findViewById(R.id.gradientSocialRow);
        if (strip == null) {
            return;
        }
        polishSocialChip(strip.findViewById(R.id.whatsappLay));
        polishSocialChip(strip.findViewById(R.id.facebookLay));
        polishSocialChip(strip.findViewById(R.id.instagramLay));
    }

    private static void polishSocialChip(View chip) {
        if (chip == null) {
            return;
        }
        chip.setBackgroundResource(R.drawable.bg_frame_gradient_social_chip);
    }

    private static void polishGradientProfile(View profileLay, boolean goldVariant) {
        if (profileLay == null) {
            return;
        }
        RoundedImageView profileImg = profileLay.findViewById(R.id.profileImg);
        if (profileImg != null) {
            int borderColor = ContextCompat.getColor(profileLay.getContext(),
                    goldVariant ? R.color.frame_gradient_gold_accent : R.color.white);
            profileImg.setBorderColor(borderColor);
            profileImg.setBorderWidth((float) profileLay.getResources().getDimensionPixelSize(
                    goldVariant ? R.dimen._3sdp : R.dimen._2sdp));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileLay.setElevation(
                    profileLay.getResources().getDimension(R.dimen.frame_profile_elevation));
        }
    }
}
