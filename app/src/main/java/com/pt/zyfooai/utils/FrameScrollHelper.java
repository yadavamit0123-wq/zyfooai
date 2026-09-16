package com.pt.zyfooai.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;

/**
 * Persists the user's chosen frame across feed posts until they pick another frame.
 * Image and reels selections are tracked separately.
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
                if (contentArea != null) {
                    FrameMediaInsetHelper.apply(contentArea, rv, preferenceManager);
                }
                if (Boolean.TRUE.equals(rv.getTag(R.id.frame_programmatic_scroll))) {
                    return;
                }
                int index = FrameSelectionHelper.visibleFrameIndex(rv);
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
        recyclerView.setTag(R.id.frame_programmatic_scroll, Boolean.TRUE);
        recyclerView.scrollToPosition(index);
        recyclerView.post(() -> recyclerView.setTag(R.id.frame_programmatic_scroll, null));
    }

    private static FrameMediaType resolveMediaType(RecyclerView recyclerView, FrameMediaType fallback) {
        Object tag = recyclerView.getTag(R.id.frame_media_type);
        if (tag instanceof FrameMediaType) {
            return (FrameMediaType) tag;
        }
        return fallback != null ? fallback : FrameMediaType.IMAGE;
    }
}
