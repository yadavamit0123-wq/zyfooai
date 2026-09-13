package com.pt.zyfooai.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pt.zyfooai.data.entity.PostAnalyticsEntity;

import java.util.List;

@Dao
public interface PostAnalyticsDao {

    @Query("SELECT * FROM post_analytics ORDER BY lastUpdated DESC")
    List<PostAnalyticsEntity> getAll();

    @Query("SELECT * FROM post_analytics WHERE postId = :postId LIMIT 1")
    PostAnalyticsEntity getByPostId(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(PostAnalyticsEntity entity);

    @Query("UPDATE post_analytics SET downloadCount = downloadCount + 1, lastUpdated = :ts WHERE postId = :postId")
    void incrementDownload(String postId, long ts);

    @Query("UPDATE post_analytics SET shareCount = shareCount + 1, lastUpdated = :ts WHERE postId = :postId")
    void incrementShare(String postId, long ts);
}
