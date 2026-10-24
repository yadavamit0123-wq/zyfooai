package com.pt.zyfooai.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pt.zyfooai.R;

/**
 * Persists the user's chosen frame across feed posts until they pick another frame.
 * Image and reels selections are tracked separately.
 * Eager fit: apply canvas/insets after programmatic scroll so media is fitted before user touch.
 */
public final class FrameScrollHelper {

    private FrameScrollHelper() {
    }

    public static void bindSelectionPersistence(
            RecyclerView recyclerView,
            PreferenceManager preferenceManager,
            View contentArea,
            FrameMediaType mediaType
    ) {
        if (recyclerView == null || recyclerView.getTag(R.id.frame_selection_bound) != null) {
            return;
        }
        recyclerView.setTag(R.id.frame_selection_bound, Boolean.TRUE);
        recyclerView.setTag(R.id.frame_media_type, mediaType);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                    return;
                }
                int index = FrameSelectionHelper.visibleFrameIndex(rv);
                if (index >= 0) {
                    rv.setTag(R.id.frame_target_index, index);
                }
                if (contentArea != null) {
                    FrameCanvasHelper.apply(contentArea, rv);
                    FrameMediaInsetHelper.apply(contentArea, rv, preferenceManager);
                    rv.invalidateItemDecorations();
                    // Frame change must not pause reels — keep PlayerView playing.
                    keepReelsPlaying(contentArea);
                }
                if (Boolean.TRUE.equals(rv.getTag(R.id.frame_programmatic_scroll))) {
                    return;
                }
                FrameMediaType type = resolveMediaType(rv, mediaType);
                if (index >= 0) {
                    FrameSelectionHelper.saveUserFrameSelection(preferenceManager, index, type);
                    FrameAnalyticsHelper.trackSelection(
                            rv.getContext().getApplicationContext(),
                            type,
                            index
                    );
                }
            }
        });
    }

    public static void scrollToSavedFrame(
            RecyclerView recyclerView,
            PreferenceManager preferenceManager,
            FrameMediaType mediaType
    ) {
        if (recyclerView == null) {
            return;
        }
        int index = FrameSelectionHelper.getActiveFramePosition(preferenceManager, mediaType);
        recyclerView.setTag(R.id.frame_target_index, index);
        recyclerView.setTag(R.id.frame_programmatic_scroll, Boolean.TRUE);
        recyclerView.scrollToPosition(index);
        recyclerView.post(() -> recyclerView.setTag(R.id.frame_programmatic_scroll, null));
    }

    /**
     * After {@link #scrollToSavedFrame}, apply fit once the target frame holder is laid out.
     * Call on bind so image/reels are auto-fitted without waiting for user touch.
     */
    public static void applyFitAfterScroll(
            View contentArea,
            RecyclerView recyclerView,
            PreferenceManager preferenceManager
    ) {
        if (contentArea == null || recyclerView == null) {
            return;
        }
        Runnable apply = () -> {
            FrameCanvasHelper.apply(contentArea, recyclerView);
            FrameMediaInsetHelper.apply(contentArea, recyclerView, preferenceManager);
            recyclerView.invalidateItemDecorations();
            keepReelsPlaying(contentArea);
        };
        // Double post: wait for scrollToPosition to bind + measure the target frame.
        recyclerView.post(() -> recyclerView.post(apply));
    }

    /** While user swipes frames horizontally, stop the vertical feed from intercepting. */
    public static void blockParentInterceptWhileSwiping(RecyclerView frameRecyclerView) {
        if (frameRecyclerView == null
                || frameRecyclerView.getTag(R.id.frame_parent_intercept_bound) != null) {
            return;
        }
        frameRecyclerView.setTag(R.id.frame_parent_intercept_bound, Boolean.TRUE);
        frameRecyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                ViewParent parent = rv.getParent();
                switch (e.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(true);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (parent != null) {
                            parent.requestDisallowInterceptTouchEvent(false);
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    private static void keepReelsPlaying(View contentArea) {
        if (contentArea == null) {
            return;
        }
        View pv = contentArea.findViewById(R.id.playerview);
        if (!(pv instanceof PlayerView)) {
            return;
        }
        Player player = ((PlayerView) pv).getPlayer();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    /** Scroll to saved frame then eagerly apply cover/fit (feed + save-post). */
    public static void scrollToSavedFrameAndApplyFit(
            View contentArea,
            RecyclerView recyclerView,
            PreferenceManager preferenceManager,
            FrameMediaType mediaType
    ) {
        scrollToSavedFrame(recyclerView, preferenceManager, mediaType);
        applyFitAfterScroll(contentArea, recyclerView, preferenceManager);
    }

    /** Prefetch next few frame pages so swipe is ready without layout hitch. */
    public static void enableFramePrefetch(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.setItemViewCacheSize(8);
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof LinearLayoutManager) {
            ((LinearLayoutManager) lm).setInitialPrefetchItemCount(4);
        }
        recyclerView.setHasFixedSize(true);
    }

    private static FrameMediaType resolveMediaType(RecyclerView recyclerView, FrameMediaType fallback) {
        Object tag = recyclerView.getTag(R.id.frame_media_type);
        if (tag instanceof FrameMediaType) {
            return (FrameMediaType) tag;
        }
        return fallback != null ? fallback : FrameMediaType.IMAGE;
    }
}
