package com.pt.zyfooai.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.pt.zyfooai.R;
import com.pt.zyfooai.data.AppDatabase;
import com.pt.zyfooai.data.entity.PostAnalyticsEntity;

import java.util.List;

public class CreatorStatsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creator_stats);

        findViewById(R.id.backImg).setOnClickListener(v -> onBackPressed());
        TextView tvStats = findViewById(R.id.tvStats);

        AsyncTask.execute(() -> {
            List<PostAnalyticsEntity> stats = AppDatabase.getInstance(this).postAnalyticsDao().getAll();
            StringBuilder builder = new StringBuilder();
            if (stats.isEmpty()) {
                builder.append("No post activity tracked yet.");
            } else {
                for (PostAnalyticsEntity stat : stats) {
                    builder.append("Post ").append(stat.postId)
                            .append("\n  Downloads: ").append(stat.downloadCount)
                            .append("  Shares: ").append(stat.shareCount)
                            .append("\n\n");
                }
            }
            runOnUiThread(() -> tvStats.setText(builder.toString()));
        });
    }
}
