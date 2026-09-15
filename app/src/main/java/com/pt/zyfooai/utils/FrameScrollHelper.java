package com.pt.zyfooai.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;

/**
 * Persists the user's chosen frame across all feed posts until they pick another frame.
 */
public final class FrameScrollHelper {

    private FrameScrollHelper() {
    }

    public static void bindSelectionPersistence(
            RecyclerView recyclerView,
            PreferenceManager preferenceManager,
            View contentArea
    ) {
        if (recyclerView == null || recyclerView.getTag(R.id.frame_selection_bound) != null) {
            return;
        }
        recyclerView.setTag(R.id.frame_selection_bound, Boolean.TRUE);
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
                FrameSelectionHelper.saveUserFrameSelection(preferenceManager, index);
            }
        });
    }

    public static void scrollToSavedFrame(RecyclerView recyclerView, PreferenceManager preferenceManager) {
        if (recyclerView == null) {
            return;
        }
        int index = FrameSelectionHelper.getActiveFeedFramePosition(preferenceManager);
        recyclerView.setTag(R.id.frame_programmatic_scroll, Boolean.TRUE);
        recyclerView.scrollToPosition(index);
        recyclerView.post(() -> recyclerView.setTag(R.id.frame_programmatic_scroll, null));
    }
}
