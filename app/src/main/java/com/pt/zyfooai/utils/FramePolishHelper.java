package com.pt.zyfooai.utils;

import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pt.zyfooai.R;

/**
 * Phase 1 (Option A): minimal professional polish applied to all frame overlays.
 */
public final class FramePolishHelper {

    private static final int[] DATE_ACCENTS = {
            0xFFF08606,
            0xFF1B5CC9,
            0xFF9122FF,
            0xFF662DA7,
            0xFF3F51B5,
            0xFF502727
    };

    private FramePolishHelper() {
    }

    public static void apply(View frameRoot, int frameIndex) {
        if (frameRoot == null) {
            return;
        }
        if (FrameStickerHelper.isStickerFrame(frameRoot)) {
            applyTypography(frameRoot);
            PreferenceManager pm = new PreferenceManager(frameRoot.getContext());
            FrameStickerHelper.apply(frameRoot, frameIndex, pm);
            FrameFooterLayoutHelper.applyEdgeToEdge(frameRoot);
            return;
        }
        if (FrameGradientHelper.isGradientFrame(frameRoot)) {
            applyTypography(frameRoot);
            FrameGradientHelper.apply(frameRoot, frameIndex);
            FrameFooterLayoutHelper.applyEdgeToEdge(frameRoot);
            return;
        }
        if (FrameGlassHelper.isGlassFrame(frameRoot)) {
            boolean darkGlass = frameRoot.findViewById(R.id.glassTopPanel) != null;
            applyTypography(frameRoot);
            FrameGlassHelper.apply(frameRoot, frameIndex, darkGlass);
            FrameFooterLayoutHelper.applyEdgeToEdge(frameRoot);
            return;
        }
        applyTypography(frameRoot);
        polishDateBadge(frameRoot.findViewById(R.id.dateTv), frameIndex);
        polishSocialStrip(frameRoot);
        polishProfile(frameRoot.findViewById(R.id.profileLay));
        polishTopBar(frameRoot.findViewById(R.id.topLay));
        polishSecondaryText(frameRoot);
        FrameFooterLayoutHelper.applyEdgeToEdge(frameRoot);
    }

    public static int accentColorForFrame(int frameIndex) {
        if (frameIndex < 0) {
            frameIndex = 0;
        }
        return DATE_ACCENTS[frameIndex % DATE_ACCENTS.length];
    }

    private static void applyTypography(View root) {
        setFont(root.findViewById(R.id.userNameTv), R.font.inter_semi_bold, true);
        setFont(root.findViewById(R.id.businessNameTv), R.font.inter_semi_bold, true);
        setFont(root.findViewById(R.id.userDesTv), R.font.inter_regular, false);
        setFont(root.findViewById(R.id.businessDesTv), R.font.inter_regular, false);
        setFont(root.findViewById(R.id.businessAddressTv), R.font.inter_regular, false);
        setFont(root.findViewById(R.id.businessNumberTv), R.font.inter_semi_bold, false);
        setFont(root.findViewById(R.id.businessWebsiteTv), R.font.inter_regular, false);
        setFont(root.findViewById(R.id.facebookTv), R.font.inter_regular, false);
        setFont(root.findViewById(R.id.instagramTv), R.font.inter_regular, false);
        setFont(root.findViewById(R.id.whatsappTv), R.font.inter_regular, false);
    }

    private static void setFont(TextView textView, int fontRes, boolean bold) {
        if (textView == null) {
            return;
        }
        Typeface typeface = ResourcesCompat.getFont(textView.getContext(), fontRes);
        if (typeface != null) {
            textView.setTypeface(typeface, bold ? Typeface.BOLD : Typeface.NORMAL);
        }
        textView.setIncludeFontPadding(false);
        textView.setLetterSpacing(0.02f);
    }

    private static void polishDateBadge(TextView dateTv, int frameIndex) {
        if (dateTv == null) {
            return;
        }
        dateTv.setBackgroundResource(R.drawable.bg_frame_date_badge);
        int accent = accentColorForFrame(frameIndex);
        dateTv.setTextColor(accent);
        dateTv.setTypeface(dateTv.getTypeface(), Typeface.BOLD);
        dateTv.setIncludeFontPadding(false);
        int hPad = dateTv.getResources().getDimensionPixelSize(R.dimen._8sdp);
        int vPad = dateTv.getResources().getDimensionPixelSize(R.dimen._3sdp);
        dateTv.setPadding(hPad, vPad, hPad, vPad);
    }

    private static void polishSocialStrip(View root) {
        View whatsappLay = root.findViewById(R.id.whatsappLay);
        if (whatsappLay != null && whatsappLay.getParent() instanceof LinearLayout) {
            LinearLayout strip = (LinearLayout) whatsappLay.getParent();
            if (strip.getOrientation() == LinearLayout.HORIZONTAL && strip.getChildCount() >= 2) {
                applySocialStripStyle(strip);
                return;
            }
        }
        polishSocialChip(root.findViewById(R.id.facebookLay));
        polishSocialChip(root.findViewById(R.id.instagramLay));
        polishSocialChip(root.findViewById(R.id.whatsappLay));
    }

    private static void applySocialStripStyle(LinearLayout strip) {
        strip.setBackgroundResource(R.drawable.bg_frame_social_strip);
        int v = strip.getResources().getDimensionPixelSize(R.dimen.frame_social_padding_v);
        int h = strip.getResources().getDimensionPixelSize(R.dimen.frame_social_padding_h);
        strip.setPadding(h, v, h, v);
    }

    private static void polishSocialChip(View chip) {
        if (chip == null) {
            return;
        }
        chip.setBackgroundResource(R.drawable.bg_frame_social_strip);
        int v = chip.getResources().getDimensionPixelSize(R.dimen._2sdp);
        int h = chip.getResources().getDimensionPixelSize(R.dimen._6sdp);
        chip.setPadding(h, v, h, v);
    }

    private static void polishProfile(View profileLay) {
        if (profileLay == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileLay.setElevation(
                    profileLay.getResources().getDimension(R.dimen.frame_profile_elevation));
        }
        RoundedImageView profileImg = profileLay.findViewById(R.id.profileImg);
        if (profileImg != null) {
            profileImg.setBorderColor(ContextCompat.getColor(profileLay.getContext(), R.color.white));
            profileImg.setBorderWidth(
                    (float) profileLay.getResources().getDimensionPixelSize(R.dimen._2sdp));
        }
    }

    private static void polishTopBar(View topLay) {
        if (topLay == null || topLay.getVisibility() != View.VISIBLE) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            topLay.setForeground(ContextCompat.getDrawable(topLay.getContext(), R.drawable.bg_frame_top_shadow));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            topLay.setElevation(topLay.getResources().getDimension(R.dimen._2sdp));
        }
    }

    private static void polishSecondaryText(View root) {
        int muted = ContextCompat.getColor(root.getContext(), R.color.frame_text_on_accent_muted);
        TextView userDes = root.findViewById(R.id.userDesTv);
        if (userDes != null && userDes.getCurrentTextColor() == 0xFFEDEDED) {
            userDes.setTextColor(muted);
        }
    }
}
