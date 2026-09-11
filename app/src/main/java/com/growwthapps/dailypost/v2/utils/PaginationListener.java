package com.growwthapps.dailypost.v2.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {

    @NonNull
    private final RecyclerView.LayoutManager layoutManager;

    public PaginationListener(@NonNull RecyclerView.LayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

    public abstract void loadMoreItems();

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int childCount = layoutManager.getChildCount();
        int itemCount = layoutManager.getItemCount();

        int findFirstVisibleItemPosition = 0;

        if (layoutManager instanceof LinearLayoutManager) {

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
            findFirstVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();

            Log.d("onScrolled", "scroll :" + itemCount);


        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
            int[] firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(null);
            if (firstVisibleItems != null && firstVisibleItems.length > 0) {
                findFirstVisibleItemPosition = firstVisibleItems[0];
            }
        }

        if (!isLoading() && (childCount + findFirstVisibleItemPosition) >= itemCount) {


            loadMoreItems();

        }
    }
}