package com.pt.zyfooai.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_messages")
public class ChatMessageEntity {

    @PrimaryKey(autoGenerate = true)
    public long id;

    public String message;
    public boolean fromUser;
    public long timestamp;

    public ChatMessageEntity(@NonNull String message, boolean fromUser, long timestamp) {
        this.message = message;
        this.fromUser = fromUser;
        this.timestamp = timestamp;
    }
}
