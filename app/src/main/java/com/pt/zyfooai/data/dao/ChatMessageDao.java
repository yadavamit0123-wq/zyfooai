package com.pt.zyfooai.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.pt.zyfooai.data.entity.ChatMessageEntity;

import java.util.List;

@Dao
public interface ChatMessageDao {

    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    List<ChatMessageEntity> getAll();

    @Insert
    void insert(ChatMessageEntity message);

    @Query("DELETE FROM chat_messages")
    void clear();
}
