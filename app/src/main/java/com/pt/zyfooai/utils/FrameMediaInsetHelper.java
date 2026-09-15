package com.pt.zyfooai.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pt.zyfooai.R;

/**
 * Fits image/video media inside the visible window of the active frame overlay.
 * Letterbox gaps are filled with a blurred copy of the same media.
 */
public final class FrameMediaInsetHelper {

    public static final String MEDIA_FIT_IN_FRAME = "media_fit_in_frame";

    private static final float BLUR_RADIUS = 22f;

    private FrameMediaInsetHelper() {
    }

    public static boolean isFitInFrame(PreferenceManager preferenceManager) {
        return preferenceManager.getBoolean(MEDIA_FIT_IN_FRAME, false);
    }

    public static void apply(View contentArea, RecyclerView frameRecyclerView, PreferenceManager preferenceManager) {
        if (contentArea == null || frameRecyclerView == null) {
            return;
        }
        View mediaView = findMediaView(contentArea);
        if (mediaView == null) {
            return;
        }
        if (!isFitInFrame(preferenceManager)) {
            clearInset(contentArea, mediaView);
            return;
        }

        View frameRoot = getVisibleFrameRoot(frameRecyclerView);
        if (frameRoot == null) {
            frameRoot = frameRecyclerView;
        }

        View measureTarget = frameRoot;
        Runnable applyInsets = () -> {
            int topInset = 0;
            int bottomInset = 0;
            int rootHeight = measureTarget.getHeight();
            if (rootHeight > 0) {
                topInset = measureTopInset(measureTarget);
                bottomInset = measureBottomInset(measureTarget, rootHeight);
            }
            applyInset(mediaView, topInset, bottomInset, true);
            syncBlurBackground(contentArea, topInset, bottomInset, true);
        };

        if (measureTarget.getHeight() > 0) {
            applyInsets.run();
        } else {
            measureTarget.post(applyInsets);
        }
    }

    public static void bindFitToggle(
            android.widget.CompoundButton toggle,
            PreferenceManager preferenceManager,
            View contentArea,
            RecyclerView frameRecyclerView,
            Runnable onChanged
    ) {
        if (toggle == null) {
            return;
        }
        toggle.setOnCheckedChangeListener(null);
        toggle.setChecked(isFitInFrame(preferenceManager));
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferenceManager.setBoolean(MEDIA_FIT_IN_FRAME, isChecked);
            Runnable applyAfterLayout = () -> {
                apply(contentArea, frameRecyclerView, preferenceManager);
                if (onChanged != null) {
                    onChanged.run();
                }
            };
            if (isChecked) {
                frameRecyclerView.post(() -> frameRecyclerView.post(applyAfterLayout));
            } else {
                clearInset(contentArea, findMediaView(contentArea));
                contentArea.post(applyAfterLayout);
            }
        });
        apply(contentArea, frameRecyclerView, preferenceManager);
    }

    private static View findMediaView(View contentArea) {
        View mediaView = contentArea.findViewById(R.id.image_post);
        if (mediaView == null) {
            mediaView = contentArea.findViewById(R.id.imageP);
        }
        if (mediaView == null) {
            mediaView = contentArea.findViewById(R.id.imageB);
        }
        if (mediaView == null) {
            mediaView = contentArea.findViewById(R.id.playerview);
        }
        return mediaView;
    }

    private static View getVisibleFrameRoot(RecyclerView frameRecyclerView) {
        RecyclerView.LayoutManager layoutManager = frameRecyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            return null;
        }
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
        int position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (position == RecyclerView.NO_POSITION) {
            position = linearLayoutManager.findFirstVisibleItemPosition();
        }
        if (position == RecyclerView.NO_POSITION) {
            return null;
        }
        RecyclerView.ViewHolder holder =
                frameRecyclerView.findViewHolderForAdapterPosition(position);
        return holder != null ? holder.itemView : null;
    }

    private static int measureTopInset(View frameRoot) {
        int topInset = 0;
        topInset = Math.max(topInset, bottomEdge(frameRoot.findViewById(R.id.topLay)));
        topInset = Math.max(topInset, bottomEdge(frameRoot.findViewById(R.id.glassTopPanel)));
        topInset = Math.max(topInset, bottomEdge(frameRoot.findViewById(R.id.stickerTopPrompt)));
        return topInset;
    }

    private static int measureBottomInset(View frameRoot, int rootHeight) {
        int bottomInset = 0;
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.stickerBottomPanel, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.stickerNameTag, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.glassBottomPanel, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.gradientBottomPanel, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.gradientSocialRow, rootHeight));

        View bottomBackView = frameRoot.findViewById(R.id.bottomBackView);
        if (isVisible(bottomBackView)) {
            View profileLay = frameRoot.findViewById(R.id.profileLay);
            if (isVisible(profileLay)) {
                bottomInset = Math.max(bottomInset, rootHeight - profileLay.getTop());
            } else {
                bottomInset = Math.max(bottomInset, rootHeight - bottomBackView.getTop());
            }
        }
        return bottomInset;
    }

    private static int bottomEdge(View view) {
        if (!isVisible(view)) {
            return 0;
        }
        return Math.max(0, view.getBottom());
    }

    private static int insetFromBottom(View frameRoot, int viewId, int rootHeight) {
        View view = frameRoot.findViewById(viewId);
        if (!isVisible(view)) {
            return 0;
        }
        return Math.max(0, rootHeight - view.getTop());
    }

    private static boolean isVisible(View view) {
        return view != null && view.getVisibility() == View.VISIBLE && view.getHeight() > 0;
    }

    private static void applyInset(View mediaView, int topInset, int bottomInset, boolean fitMode) {
        ViewGroup.LayoutParams params = mediaView.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            layoutParams.topMargin = topInset;
            layoutParams.bottomMargin = bottomInset;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            mediaView.setLayoutParams(layoutParams);
        } else if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) params;
            layoutParams.topMargin = topInset;
            layoutParams.bottomMargin = bottomInset;
            mediaView.setLayoutParams(layoutParams);
        }

        if (mediaView instanceof ImageView) {
            ((ImageView) mediaView).setScaleType(
                    fitMode ? ImageView.ScaleType.FIT_CENTER : ImageView.ScaleType.CENTER_CROP);
        } else if (mediaView instanceof PlayerView) {
            PlayerView playerView = (PlayerView) mediaView;
            playerView.setResizeMode(
                    fitMode ? AspectRatioFrameLayout.RESIZE_MODE_FIT
                            : AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            int shutterColor = fitMode ? Color.TRANSPARENT : Color.BLACK;
            playerView.setShutterBackgroundColor(shutterColor);
            playerView.setBackgroundColor(shutterColor);
        }
    }

    private static void clearInset(View contentArea, View mediaView) {
        if (mediaView != null) {
            resetMediaLayout(mediaView);
        }
        syncBlurBackground(contentArea, 0, 0, false);
    }

    private static void resetMediaLayout(View mediaView) {
        ViewGroup.LayoutParams params = mediaView.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            layoutParams.topMargin = 0;
            layoutParams.bottomMargin = 0;
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            mediaView.setLayoutParams(layoutParams);
        } else if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) params;
            layoutParams.topMargin = 0;
            layoutParams.bottomMargin = 0;
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;
            mediaView.setLayoutParams(layoutParams);
        }
        if (mediaView instanceof ImageView) {
            ((ImageView) mediaView).setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (mediaView instanceof PlayerView) {
            PlayerView playerView = (PlayerView) mediaView;
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            playerView.setShutterBackgroundColor(Color.BLACK);
            playerView.setBackgroundColor(Color.BLACK);
        }
    }

    private static void syncBlurBackground(View contentArea, int topInset, int bottomInset, boolean show) {
        ImageView blurBg = contentArea.findViewById(R.id.media_blur_bg);
        if (blurBg == null) {
            return;
        }
        if (!show) {
            blurBg.setVisibility(View.GONE);
            blurBg.setRenderEffect(null);
            blurBg.setTag(R.id.media_blur_source_url, null);
            Glide.with(blurBg).clear(blurBg);
            return;
        }

        Object mediaUrlTag = contentArea.getTag(R.id.media_blur_source_url);
        String mediaUrl = mediaUrlTag instanceof String ? (String) mediaUrlTag : null;
        if (mediaUrl == null || mediaUrl.isEmpty()) {
            blurBg.setVisibility(View.GONE);
            return;
        }

        blurBg.setVisibility(View.VISIBLE);
        applyInset(blurBg, topInset, bottomInset, false);
        blurBg.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Object loadedUrl = blurBg.getTag(R.id.media_blur_source_url);
        if (mediaUrl.equals(loadedUrl)) {
            applyBlurEffect(blurBg);
            return;
        }
        blurBg.setTag(R.id.media_blur_source_url, mediaUrl);

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        if (isVideoUrl(mediaUrl)) {
            options = options.frameOf(1_000_000);
        }

        Glide.with(blurBg)
                .load(mediaUrl)
                .apply(options)
                .into(new com.bumptech.glide.request.target.CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource,
                                                @Nullable com.bumptech.glide.request.transition.Transition<? super Drawable> transition) {
                        blurBg.setImageDrawable(resource);
                        applyBlurEffect(blurBg);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        blurBg.setImageDrawable(placeholder);
                    }
                });
    }

    private static void applyBlurEffect(ImageView imageView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            imageView.setRenderEffect(
                    RenderEffect.createBlurEffect(BLUR_RADIUS, BLUR_RADIUS, Shader.TileMode.CLAMP));
            return;
        }

        Drawable drawable = imageView.getDrawable();
        if (!(drawable instanceof BitmapDrawable)) {
            return;
        }
        Bitmap source = ((BitmapDrawable) drawable).getBitmap();
        if (source == null || source.isRecycled()) {
            return;
        }
        Bitmap blurred = fastBlur(source);
        if (blurred != null) {
            imageView.setImageBitmap(blurred);
        }
        imageView.setRenderEffect(null);
    }

    private static Bitmap fastBlur(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        int scaledWidth = Math.max(1, width / 8);
        int scaledHeight = Math.max(1, height / 8);
        Bitmap scaledDown = Bitmap.createScaledBitmap(source, scaledWidth, scaledHeight, true);
        return Bitmap.createScaledBitmap(scaledDown, width, height, true);
    }

    private static boolean isVideoUrl(String url) {
        if (url == null) {
            return false;
        }
        String lower = url.toLowerCase(Locale.ROOT);
        return lower.endsWith(".mp4")
                || lower.endsWith(".mov")
                || lower.endsWith(".webm")
                || lower.contains(".mp4?")
                || lower.contains("/video/");
    }
}
