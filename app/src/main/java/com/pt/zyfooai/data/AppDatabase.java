package com.pt.zyfooai.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pt.zyfooai.data.dao.ChatMessageDao;
import com.pt.zyfooai.data.dao.PostAnalyticsDao;
import com.pt.zyfooai.data.entity.ChatMessageEntity;
import com.pt.zyfooai.data.entity.PostAnalyticsEntity;

@Database(entities = {ChatMessageEntity.class, PostAnalyticsEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract ChatMessageDao chatMessageDao();

    public abstract PostAnalyticsDao postAnalyticsDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            com.pt.zyfooai.utils.AppConstants.DB_NAME
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return instance;
    }
}
