package com.pt.zyfooai.utils;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pt.zyfooai.R;

public final class FooterSizeHelper {

    public static final String FOOTER_SIZE_SCALE = "footer_size_scale";
    private static final float MIN_SCALE = 0.75f;
    private static final float MAX_SCALE = 1.35f;
    private static final int DEFAULT_PROGRESS = 50;

    private FooterSizeHelper() {
    }

    public static float getScale(PreferenceManager preferenceManager) {
        int progress = preferenceManager.getInt(FOOTER_SIZE_SCALE, DEFAULT_PROGRESS);
        return MIN_SCALE + ((MAX_SCALE - MIN_SCALE) * progress / 100f);
    }

    public static void applyFooterScale(View swipeFrames, PreferenceManager preferenceManager) {
        if (swipeFrames == null) {
            return;
        }
        float scale = getScale(preferenceManager);
        int horizontal = swipeFrames.getResources().getDimensionPixelSize(R.dimen._10sdp);
        int vertical = swipeFrames.getResources().getDimensionPixelSize(R.dimen._10sdp);
        swipeFrames.setPadding(
                Math.round(horizontal * scale),
                Math.round(vertical * scale),
                Math.round(horizontal * scale),
                Math.round(vertical * scale)
        );

        scaleText(swipeFrames.findViewById(R.id.footerSizeLabel), 10, scale);
        scaleViewHeight(swipeFrames.findViewById(R.id.btnLay), 30, scale);
        scaleViewHeight(swipeFrames.findViewById(R.id.edit_Btn), 30, scale);
        scaleViewHeight(swipeFrames.findViewById(R.id.downloadBtn), 30, scale);
        scaleViewHeight(swipeFrames.findViewById(R.id.shareBtn), 30, scale);
    }

    public static void bindFooterSizeSeekBar(SeekBar seekBar, PreferenceManager preferenceManager, Runnable onChanged) {
        if (seekBar == null) {
            return;
        }
        seekBar.setProgress(preferenceManager.getInt(FOOTER_SIZE_SCALE, DEFAULT_PROGRESS));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    preferenceManager.setInt(FOOTER_SIZE_SCALE, progress);
                    if (onChanged != null) {
                        onChanged.run();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
            }
        });
    }

    public static void fitContentAboveFooter(View contentContainer, View footer) {
        if (contentContainer == null || footer == null) {
            return;
        }
        View mainLayout = contentContainer.findViewById(R.id.mainLayOut);
        View mediaView = contentContainer.findViewById(R.id.image_post);
        if (mediaView == null) {
            mediaView = contentContainer.findViewById(R.id.playerview);
        }
        FrameOverlayHelper.fillContentArea(contentContainer, footer, mainLayout, mediaView);
    }

    private static void scaleText(TextView textView, float baseSp, float scale) {
        if (textView == null) {
            return;
        }
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, baseSp * scale);
    }

    private static void scaleViewHeight(View view, float baseDp, float scale) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            return;
        }
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                baseDp * scale,
                view.getResources().getDisplayMetrics()
        );
        params.height = height;
        view.setLayoutParams(params);
    }
}
