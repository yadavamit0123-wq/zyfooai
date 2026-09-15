package com.pt.zyfooai.utils;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.makeramen.roundedimageview.RoundedImageView;
import com.pt.zyfooai.R;

/**
 * P0: Instagram Add Yours sticker-style frame polish.
 */
public final class FrameStickerHelper {

    private FrameStickerHelper() {
    }

    public static boolean isStickerFrame(View root) {
        return root != null && root.findViewById(R.id.stickerBottomPanel) != null;
    }

    public static boolean isGoldSticker(View root) {
        return root != null && root.findViewById(R.id.stickerGoldAccent) != null;
    }

    public static void applyBusinessHeader(View itemView, PreferenceManager preferenceManager) {
        if (itemView == null || preferenceManager == null) {
            return;
        }
        boolean isBusiness = !"Personal".equals(preferenceManager.getString(Constant.DEFAULT_TYPE));

        View glassTopPanel = itemView.findViewById(R.id.glassTopPanel);
        if (glassTopPanel != null) {
            glassTopPanel.setVisibility(isBusiness ? View.VISIBLE : View.GONE);
        }

        if (isStickerFrame(itemView)) {
            View topLay = itemView.findViewById(R.id.topLay);
            TextView businessNameTv = itemView.findViewById(R.id.businessNameTv);
            if (topLay != null && businessNameTv != null) {
                android.view.ViewGroup.LayoutParams lp = businessNameTv.getLayoutParams();
                boolean hasBusinessHeader = lp.width > 0 && lp.height > 0;
                if (hasBusinessHeader) {
                    topLay.setVisibility(isBusiness ? View.VISIBLE : View.GONE);
                }
            }
        }
    }

    public static void apply(View root, int frameIndex, PreferenceManager preferenceManager) {
        if (root == null) {
            return;
        }
        boolean isBusiness = preferenceManager != null
                && !"Personal".equals(preferenceManager.getString(Constant.DEFAULT_TYPE));
        boolean gold = isGoldSticker(root);

        View blurRoot = (View) root.getTag(R.id.frost_blur_root);
        FrameFrostHelper.applyFrostPanel(root.findViewById(R.id.stickerBottomPanel), blurRoot, root);
        FrameFrostHelper.applyFrostPanel(root.findViewById(R.id.stickerNameTag), blurRoot, root);

        TextView prompt = root.findViewById(R.id.stickerTopPrompt);
        if (prompt != null) {
            prompt.setVisibility(View.GONE);
        }

        View nameTag = root.findViewById(R.id.stickerNameTag);
        if (nameTag != null) {
            nameTag.setRotation(-3f);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                nameTag.setElevation(nameTag.getResources().getDimension(R.dimen._4sdp));
            }
        }

        int primary = ContextCompat.getColor(root.getContext(),
                gold ? R.color.frame_sticker_text_on_dark : R.color.frame_sticker_text_primary);
        int secondary = ContextCompat.getColor(root.getContext(),
                gold ? R.color.frame_sticker_text_muted_on_dark : R.color.frame_sticker_text_secondary);
        int accent = gold
                ? ContextCompat.getColor(root.getContext(), R.color.frame_sticker_gold_accent)
                : FramePolishHelper.accentColorForFrame(frameIndex);

        applyTextColor(root.findViewById(R.id.userNameTv), gold ? primary : primary);
        applyTextColor(root.findViewById(R.id.businessNameTv), primary);
        applyTextColor(root.findViewById(R.id.userDesTv), secondary);
        applyTextColor(root.findViewById(R.id.businessDesTv), secondary);
        applyTextColor(root.findViewById(R.id.businessAddressTv), gold ? primary : primary);
        applyTextColor(root.findViewById(R.id.businessNumberTv), gold ? primary : primary);
        applyTextColor(root.findViewById(R.id.businessWebsiteTv), gold ? accent : secondary);
        applyTextColor(root.findViewById(R.id.facebookTv), gold ? primary : primary);
        applyTextColor(root.findViewById(R.id.instagramTv), gold ? primary : primary);
        applyTextColor(root.findViewById(R.id.whatsappTv), gold ? primary : primary);

        TextView dateTv = root.findViewById(R.id.dateTv);
        if (dateTv != null && dateTv.getVisibility() == View.VISIBLE) {
            if (gold) {
                dateTv.setBackgroundResource(R.drawable.bg_frame_sticker_chip_gold);
                dateTv.setTextColor(accent);
            } else {
                dateTv.setBackgroundResource(R.drawable.bg_frame_sticker_chip);
                dateTv.setTextColor(accent);
            }
        }

        polishStickerProfile(root.findViewById(R.id.profileLay), gold);
        polishStickerSocialRow(root.findViewById(R.id.stickerSocialRow), gold);

        View topLay = root.findViewById(R.id.topLay);
        if (topLay != null && topLay.getVisibility() == View.VISIBLE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            topLay.setElevation(topLay.getResources().getDimension(R.dimen._4sdp));
        }

        if (gold) {
            applyDarkGoldPremiumPolish(root);
        }
    }

    public static void bindDynamicContent(View root, PreferenceManager preferenceManager) {
        if (root == null || preferenceManager == null) {
            return;
        }
        bindQuoteContent(root, preferenceManager);
        bindQrCode(root, preferenceManager);
    }

    private static void bindQuoteContent(View root, PreferenceManager preferenceManager) {
        if (root.findViewById(R.id.stickerQuoteMarker) == null) {
            return;
        }
        TextView quoteText = root.findViewById(R.id.stickerQuoteText);
        if (quoteText == null) {
            return;
        }
        String designation = preferenceManager.getString(Constant.USER_DESIGNATION);
        if (designation != null && !designation.trim().isEmpty()) {
            quoteText.setText(designation.trim());
        } else {
            quoteText.setText(root.getContext().getString(R.string.sticker_quote_default));
        }

        boolean isPersonal = "Personal".equals(preferenceManager.getString(Constant.DEFAULT_TYPE));
        if (isPersonal) {
            TextView userDesTv = root.findViewById(R.id.userDesTv);
            TextView businessAddressTv = root.findViewById(R.id.businessAddressTv);
            String phone = preferenceManager.getString(Constant.USER_PHONE);
            if (userDesTv != null && phone != null && !phone.trim().isEmpty()) {
                userDesTv.setText(phone.trim());
                userDesTv.setVisibility(View.VISIBLE);
            }
            if (businessAddressTv != null) {
                businessAddressTv.setVisibility(View.GONE);
            }
        }
    }

    private static void bindQrCode(View root, PreferenceManager preferenceManager) {
        ImageView qrView = root.findViewById(R.id.stickerQrCode);
        if (qrView == null) {
            return;
        }
        int size = qrView.getResources().getDimensionPixelSize(R.dimen._72sdp);
        String payload = QrCodeHelper.payloadForProfile(preferenceManager);
        Bitmap bitmap = QrCodeHelper.generateBitmap(payload, size);
        if (bitmap != null) {
            qrView.setImageBitmap(bitmap);
            qrView.setVisibility(View.VISIBLE);
        } else {
            qrView.setVisibility(View.GONE);
        }
    }

    private static void applyDarkGoldPremiumPolish(View root) {
        View vignette = root.findViewById(R.id.stickerPhotoVignette);
        if (vignette != null) {
            vignette.setVisibility(View.VISIBLE);
        }

        applyGoldBorder(root.findViewById(R.id.stickerTopPrompt));
        applyGoldBorder(root.findViewById(R.id.stickerBottomPanel));
        applyGoldBorder(root.findViewById(R.id.topLay));
        applyGoldBorder(root.findViewById(R.id.dateTv));

        int premiumNameColor = ContextCompat.getColor(root.getContext(), R.color.frame_sticker_premium_name);
        applyBoldWhiteName(root.findViewById(R.id.userNameTv), premiumNameColor);
        applyBoldWhiteName(root.findViewById(R.id.businessNameTv), premiumNameColor);
    }

    private static void applyGoldBorder(View view) {
        if (view != null) {
            view.setBackgroundResource(R.drawable.bg_frame_sticker_gold_border);
        }
    }

    private static void applyBoldWhiteName(TextView textView, int color) {
        if (textView == null) {
            return;
        }
        textView.setTextColor(color);
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);
    }

    private static void applyTextColor(TextView textView, int color) {
        if (textView != null) {
            textView.setTextColor(color);
        }
    }

    private static void polishStickerProfile(View profileLay, boolean gold) {
        if (profileLay == null) {
            return;
        }
        RoundedImageView profileImg = profileLay.findViewById(R.id.profileImg);
        if (profileImg != null) {
            profileImg.setBorderColor(ContextCompat.getColor(profileLay.getContext(),
                    gold ? R.color.frame_sticker_gold_accent : R.color.white));
            profileImg.setBorderWidth(
                    (float) profileLay.getResources().getDimensionPixelSize(R.dimen._2sdp));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileLay.setElevation(
                    profileLay.getResources().getDimension(R.dimen.frame_profile_elevation));
        }
    }

    private static void polishStickerSocialRow(View row, boolean gold) {
        if (row == null || row.getVisibility() != View.VISIBLE) {
            return;
        }
        if (row instanceof LinearLayout) {
            int v = row.getResources().getDimensionPixelSize(R.dimen._2sdp);
            int h = row.getResources().getDimensionPixelSize(R.dimen._6sdp);
            row.setPadding(h, v, h, v);
        }
    }
}
