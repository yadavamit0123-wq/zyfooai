package com.pt.zyfooai.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.pt.zyfooai.R;

/**
 * Controls frame overlay scale, opacity, and content fill above the footer strip.
 */
public final class FrameOverlayHelper {

    public static final String FRAME_SCALE = "frame_overlay_scale";
    public static final String FRAME_ALPHA = "frame_overlay_alpha";

    private static final int DEFAULT_SCALE = 50;
    private static final int DEFAULT_ALPHA = 100;

    private FrameOverlayHelper() {
    }

    public static float getFrameScale(PreferenceManager preferenceManager) {
        int progress = preferenceManager.getInt(FRAME_SCALE, DEFAULT_SCALE);
        return 0.85f + (0.40f * progress / 100f);
    }

    public static float getFrameAlpha(PreferenceManager preferenceManager) {
        int progress = preferenceManager.getInt(FRAME_ALPHA, DEFAULT_ALPHA);
        return 0.25f + (0.75f * progress / 100f);
    }

    public static void applyFrameOverlay(View frameRecyclerView, PreferenceManager preferenceManager) {
        if (frameRecyclerView == null) {
            return;
        }
        float scale = getFrameScale(preferenceManager);
        frameRecyclerView.setScaleX(scale);
        frameRecyclerView.setScaleY(scale);
        frameRecyclerView.setAlpha(getFrameAlpha(preferenceManager));
    }

    public static void bindSeekBar(
            SeekBar seekBar,
            String preferenceKey,
            int defaultProgress,
            PreferenceManager preferenceManager,
            Runnable onChanged
    ) {
        if (seekBar == null) {
            return;
        }
        if (seekBar.getTag(R.id.frame_seek_bound) != null) {
            return;
        }
        seekBar.setTag(R.id.frame_seek_bound, Boolean.TRUE);
        seekBar.setProgress(preferenceManager.getInt(preferenceKey, defaultProgress));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (fromUser) {
                    preferenceManager.setInt(preferenceKey, progress);
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

    public static void prepareContentForLayout(View mainLayout) {
        if (mainLayout != null) {
            mainLayout.setAlpha(0f);
        }
    }

    public static void fillContentArea(View contentArea, View footer, View mainLayout, View mediaView) {
        if (contentArea == null || footer == null || mainLayout == null) {
            return;
        }
        contentArea.post(() -> {
            int footerHeight = footer.getHeight();
            if (footerHeight <= 0) {
                footerHeight = contentArea.getResources().getDimensionPixelSize(R.dimen._120sdp);
            }
            int available = contentArea.getHeight();
            if (available <= 0) {
                mainLayout.setAlpha(1f);
                return;
            }

            ViewGroup.LayoutParams layoutParams = mainLayout.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.height = Math.max(available, 0);
                mainLayout.setLayoutParams(layoutParams);
            }

            if (mediaView != null) {
                ViewGroup.LayoutParams mediaParams = mediaView.getLayoutParams();
                if (mediaParams != null) {
                    mediaParams.height = Math.max(available, 0);
                    mediaView.setLayoutParams(mediaParams);
                }
            }
            mainLayout.setAlpha(1f);
        });
    }
}
