package com.pt.zyfooai.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "post_analytics")
public class PostAnalyticsEntity {

    @NonNull
    @PrimaryKey
    public String postId;

    public int downloadCount;
    public int shareCount;
    public String categoryId;
    public long lastUpdated;

    public PostAnalyticsEntity(@NonNull String postId, String categoryId) {
        this.postId = postId;
        this.categoryId = categoryId != null ? categoryId : "";
        this.downloadCount = 0;
        this.shareCount = 0;
        this.lastUpdated = System.currentTimeMillis();
    }
}
