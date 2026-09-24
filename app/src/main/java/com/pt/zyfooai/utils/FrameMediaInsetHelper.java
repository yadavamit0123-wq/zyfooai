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
import com.pt.zyfooai.model.FrameConfig;

/**
 * Fits image/video media inside the visible window of the active frame overlay.
 * Letterbox gaps are filled with a blurred copy of the same media.
 */
public final class FrameMediaInsetHelper {

    public static final String MEDIA_FIT_IN_FRAME = "media_fit_in_frame";

    private static final float BLUR_RADIUS = 22f;
    /** Top chrome taller than this fraction of the frame is treated as a layout stretch bug. */
    private static final float MAX_TOP_CHROME_HEIGHT_RATIO = 0.22f;

    private FrameMediaInsetHelper() {
    }

    public static boolean isFitInFrame(PreferenceManager preferenceManager) {
        if (preferenceManager == null) {
            return true;
        }
        if (!preferenceManager.contains(MEDIA_FIT_IN_FRAME)) {
            return true;
        }
        return preferenceManager.getBoolean(MEDIA_FIT_IN_FRAME, true);
    }

    public static void apply(View contentArea, RecyclerView frameRecyclerView, PreferenceManager preferenceManager) {
        if (contentArea == null || frameRecyclerView == null) {
            return;
        }
        View mediaView = findMediaView(contentArea);
        if (mediaView == null) {
            return;
        }

        FrameMediaType mediaType = detectMediaType(contentArea, mediaView);
        if (!shouldApplyFit(preferenceManager, mediaType)) {
            clearInset(contentArea, mediaView, mediaType);
            return;
        }

        View frameRoot = getVisibleFrameRoot(frameRecyclerView);
        if (frameRoot == null) {
            frameRoot = frameRecyclerView;
        }

        FrameMediaType finalMediaType = mediaType;
        Runnable applyInsets = () -> applyInsetsOnLayout(
                contentArea,
                frameRecyclerView,
                mediaView,
                preferenceManager,
                finalMediaType
        );

        FrameCanvasHelper.apply(contentArea, frameRecyclerView);
        View mainLayout = contentArea.findViewById(R.id.mainLayOut);
        if (mainLayout != null) {
            mainLayout.post(applyInsets);
        } else if (frameRoot.getHeight() > 0) {
            applyInsets.run();
        } else {
            frameRoot.post(applyInsets);
        }
    }

    private static void applyInsetsOnLayout(
            View contentArea,
            RecyclerView frameRecyclerView,
            View mediaView,
            PreferenceManager preferenceManager,
            FrameMediaType mediaType
    ) {
        View measureTarget = getVisibleFrameRoot(frameRecyclerView);
        if (measureTarget == null) {
            measureTarget = frameRecyclerView;
        }
        int topInset = 0;
        int bottomInset = 0;
        int leftInset = 0;
        int rightInset = 0;
        int rootHeight = measureTarget.getHeight();
        int rootWidth = measureTarget.getWidth();
        FrameConfig dynamicConfig = readDynamicConfig(measureTarget);
        if (rootHeight > 0) {
            if (dynamicConfig != null) {
                topInset = insetFromPercent(dynamicConfig.mediaTopInsetPercent(), rootHeight);
                bottomInset = insetFromPercent(dynamicConfig.mediaBottomInsetPercent(), rootHeight);
                if (rootWidth > 0) {
                    leftInset = insetFromPercent(dynamicConfig.mediaLeftInsetPercent(), rootWidth);
                    rightInset = insetFromPercent(dynamicConfig.mediaRightInsetPercent(), rootWidth);
                }
                if (dynamicConfig.footer != null && dynamicConfig.footer.enabled) {
                    bottomInset = Math.max(bottomInset, measureBottomInset(measureTarget, rootHeight));
                }
            } else {
                topInset = measureTopInset(measureTarget, mediaType, rootHeight);
                bottomInset = measureBottomInset(measureTarget, rootHeight);
            }
        }
        if (dynamicConfig == null) {
            float overlayScale = preferenceManager != null
                    ? FrameOverlayHelper.getFrameScale(preferenceManager)
                    : 1f;
            topInset = Math.round(topInset * overlayScale);
            bottomInset = Math.round(bottomInset * overlayScale);
            leftInset = Math.round(leftInset * overlayScale);
            rightInset = Math.round(rightInset * overlayScale);
        }
        boolean serverFrame = dynamicConfig != null;
        boolean cropMedia = serverFrame || shouldApplyFit(preferenceManager, mediaType);
        applyInset(
                mediaView,
                topInset,
                bottomInset,
                leftInset,
                rightInset,
                cropMedia,
                serverFrame,
                mediaType
        );
        boolean showBlur = !serverFrame || mediaType == FrameMediaType.IMAGE;
        syncBlurBackground(contentArea, topInset, bottomInset, leftInset, rightInset, showBlur);
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
                clearInset(contentArea, findMediaView(contentArea), detectMediaType(contentArea, findMediaView(contentArea)));
                contentArea.post(applyAfterLayout);
            }
        });
        apply(contentArea, frameRecyclerView, preferenceManager);
    }

    private static boolean shouldApplyFit(PreferenceManager preferenceManager, FrameMediaType mediaType) {
        if (mediaType == FrameMediaType.REELS) {
            return true;
        }
        return isFitInFrame(preferenceManager);
    }

    private static FrameMediaType detectMediaType(View contentArea, View mediaView) {
        if (mediaView instanceof PlayerView) {
            return FrameMediaType.REELS;
        }
        View playerView = contentArea.findViewById(R.id.playerview);
        if (playerView != null && playerView.getVisibility() == View.VISIBLE) {
            return FrameMediaType.REELS;
        }
        return FrameMediaType.IMAGE;
    }

    private static View findMediaView(View contentArea) {
        View mediaView = contentArea.findViewById(R.id.image_post);
        if (mediaView == null) {
            mediaView = contentArea.findViewById(R.id.imageP);
        }
        if (mediaView == null || mediaView.getVisibility() != View.VISIBLE) {
            View imageB = contentArea.findViewById(R.id.imageB);
            if (imageB != null && imageB.getVisibility() == View.VISIBLE) {
                mediaView = imageB;
            }
        }
        if (mediaView == null) {
            mediaView = contentArea.findViewById(R.id.imageB);
        }
        if (mediaView == null) {
            mediaView = contentArea.findViewById(R.id.playerview);
        }
        return mediaView;
    }

    public static View getVisibleFrameRoot(RecyclerView frameRecyclerView) {
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

    @Nullable
    private static FrameConfig readDynamicConfig(View frameRoot) {
        if (!DynamicFrameRenderer.isDynamicFrame(frameRoot)) {
            return null;
        }
        Object tag = frameRoot.getTag(R.id.frame_config);
        return tag instanceof FrameConfig ? (FrameConfig) tag : null;
    }

    private static int insetFromPercent(int percent, int rootHeight) {
        if (percent <= 0 || rootHeight <= 0) {
            return 0;
        }
        return Math.round(rootHeight * (percent / 100f));
    }

    private static int measureTopInset(View frameRoot, FrameMediaType mediaType, int rootHeight) {
        if (mediaType == FrameMediaType.REELS) {
            return 0;
        }
        int topInset = 0;
        topInset = Math.max(topInset, topChromeBottom(frameRoot.findViewById(R.id.glassTopPanel), rootHeight));
        topInset = Math.max(topInset, topChromeBottom(frameRoot.findViewById(R.id.stickerTopPrompt), rootHeight));
        topInset = Math.max(topInset, topChromeBottom(frameRoot.findViewById(R.id.topLay), rootHeight));
        return topInset;
    }

    private static int topChromeBottom(View view, int rootHeight) {
        if (!isVisible(view)) {
            return 0;
        }
        int height = view.getHeight();
        if (rootHeight > 0 && height > rootHeight * MAX_TOP_CHROME_HEIGHT_RATIO) {
            return 0;
        }
        return Math.max(0, view.getBottom());
    }

    private static int measureBottomInset(View frameRoot, int rootHeight) {
        int bottomInset = 0;
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.stickerBottomPanel, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.stickerNameTag, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.glassBottomPanel, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.gradientBottomPanel, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.gradientSocialRow, rootHeight));
        bottomInset = Math.max(bottomInset, insetFromBottom(frameRoot, R.id.stickerSocialRow, rootHeight));

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

    private static void applyInset(
            View mediaView,
            int topInset,
            int bottomInset,
            int leftInset,
            int rightInset,
            boolean fitMode,
            boolean serverSafeZone,
            FrameMediaType mediaType
    ) {
        ViewGroup.LayoutParams params = mediaView.getLayoutParams();
        if (params instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
            layoutParams.topMargin = topInset;
            layoutParams.bottomMargin = bottomInset;
            layoutParams.leftMargin = leftInset;
            layoutParams.rightMargin = rightInset;
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
            mediaView.setLayoutParams(layoutParams);
        } else if (params instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) params;
            layoutParams.topMargin = topInset;
            layoutParams.bottomMargin = bottomInset;
            layoutParams.leftMargin = leftInset;
            layoutParams.rightMargin = rightInset;
            mediaView.setLayoutParams(layoutParams);
        }

        if (mediaView instanceof ImageView) {
            ImageView imageView = (ImageView) mediaView;
            imageView.setAdjustViewBounds(false);
            if (serverSafeZone && mediaType == FrameMediaType.IMAGE) {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else if (serverSafeZone || !fitMode) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else {
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        } else if (mediaView instanceof PlayerView) {
            PlayerView playerView = (PlayerView) mediaView;
            if (serverSafeZone || !fitMode) {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                playerView.setShutterBackgroundColor(Color.BLACK);
                playerView.setBackgroundColor(Color.BLACK);
            } else {
                playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                playerView.setShutterBackgroundColor(Color.TRANSPARENT);
                playerView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private static void clearInset(View contentArea, View mediaView, FrameMediaType mediaType) {
        if (mediaView != null) {
            resetMediaLayout(mediaView, mediaType);
        }
        syncBlurBackground(contentArea, 0, 0, 0, 0, false);
    }

    private static void resetMediaLayout(View mediaView, FrameMediaType mediaType) {
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
            ImageView imageView = (ImageView) mediaView;
            if (isSavePostImage(imageView)) {
                imageView.setAdjustViewBounds(true);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            } else {
                imageView.setAdjustViewBounds(false);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
        } else if (mediaView instanceof PlayerView) {
            PlayerView playerView = (PlayerView) mediaView;
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            playerView.setShutterBackgroundColor(Color.BLACK);
            playerView.setBackgroundColor(Color.BLACK);
        }
    }

    private static boolean isSavePostImage(ImageView imageView) {
        int id = imageView.getId();
        return id == R.id.imageP || id == R.id.imageB;
    }

    private static void syncBlurBackground(
            View contentArea,
            int topInset,
            int bottomInset,
            int leftInset,
            int rightInset,
            boolean show
    ) {
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
        applyInset(blurBg, topInset, bottomInset, leftInset, rightInset, false, false, FrameMediaType.IMAGE);
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
