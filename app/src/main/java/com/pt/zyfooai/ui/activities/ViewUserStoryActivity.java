package com.pt.zyfooai.ui.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.UserStoryDetail;
import com.pt.zyfooai.model.UserStoryMedia;
import com.pt.zyfooai.respository.UserStoryRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ViewUserStoryActivity extends AppCompatActivity {

    public static final String EXTRA_STORY_ID = "story_id";

    private static final long PAGE_MS = 4500L;

    private ViewPager2 pager;
    private LinearLayout progressRow;
    private ProgressBar loader;
    private TextView nameTv;
    private CircularImageView avatar;
    private final List<UserStoryMedia> media = new ArrayList<>();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final UserStoryRepository repository = new UserStoryRepository();
    private Runnable autoAdvance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_story);

        pager = findViewById(R.id.pager);
        progressRow = findViewById(R.id.progressRow);
        loader = findViewById(R.id.loader);
        nameTv = findViewById(R.id.nameTv);
        avatar = findViewById(R.id.avatar);
        findViewById(R.id.btnClose).setOnClickListener(v -> finish());

        String storyId = getIntent().getStringExtra(EXTRA_STORY_ID);
        if (storyId == null || storyId.isEmpty()) {
            Toast.makeText(this, R.string.view_story_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loader.setVisibility(View.VISIBLE);
        repository.getDetail(storyId).observe(this, response -> {
            loader.setVisibility(View.GONE);
            if (response == null || !response.success || response.data == null
                    || response.data.media == null || response.data.media.isEmpty()) {
                int msg = "EXPIRED".equals(response != null ? response.code : null)
                        ? R.string.view_story_expired
                        : R.string.view_story_error;
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            bindStory(response.data);
            repository.markSeen(storyId);
        });

        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateProgress(position);
                scheduleAdvance();
            }
        });

        // Tap left/right thirds via pager item overlay is handled in adapter click zones below.
        pager.setOnClickListener(null);
    }

    private void bindStory(UserStoryDetail detail) {
        nameTv.setText(detail.userName != null ? detail.userName : "");
        if (detail.userAvatar != null && !detail.userAvatar.isEmpty()) {
            Glide.with(avatar).load(detail.userAvatar).into(avatar);
        }
        media.clear();
        media.addAll(detail.media);
        Collections.sort(media, Comparator.comparingInt(m -> m.sortOrder));
        buildProgressBars(media.size());
        pager.setAdapter(new MediaAdapter());
        updateProgress(0);
        scheduleAdvance();
    }

    private void buildProgressBars(int count) {
        progressRow.removeAllViews();
        for (int i = 0; i < count; i++) {
            View bar = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(3), 1f);
            params.setMarginEnd(dp(3));
            bar.setLayoutParams(params);
            bar.setBackgroundColor(0x66FFFFFF);
            progressRow.addView(bar);
        }
    }

    private void updateProgress(int active) {
        for (int i = 0; i < progressRow.getChildCount(); i++) {
            progressRow.getChildAt(i).setBackgroundColor(i <= active ? 0xFFFFFFFF : 0x66FFFFFF);
        }
    }

    private void scheduleAdvance() {
        if (autoAdvance != null) {
            handler.removeCallbacks(autoAdvance);
        }
        autoAdvance = () -> {
            int next = pager.getCurrentItem() + 1;
            if (next < media.size()) {
                pager.setCurrentItem(next, true);
            } else {
                finish();
            }
        };
        handler.postDelayed(autoAdvance, PAGE_MS);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDestroy() {
        if (autoAdvance != null) {
            handler.removeCallbacks(autoAdvance);
        }
        super.onDestroy();
    }

    private class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.Holder> {
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            FrameTapView frame = new FrameTapView(parent);
            return new Holder(frame.root);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            UserStoryMedia item = media.get(position);
            Glide.with(holder.image).load(item.url).into(holder.image);
            holder.left.setOnClickListener(v -> {
                int prev = pager.getCurrentItem() - 1;
                if (prev >= 0) {
                    pager.setCurrentItem(prev, true);
                } else {
                    finish();
                }
            });
            holder.right.setOnClickListener(v -> {
                int next = pager.getCurrentItem() + 1;
                if (next < media.size()) {
                    pager.setCurrentItem(next, true);
                } else {
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return media.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final ImageView image;
            final View left;
            final View right;

            Holder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.storyImage);
                left = itemView.findViewById(R.id.tapLeftZone);
                right = itemView.findViewById(R.id.tapRightZone);
            }
        }
    }

    /** Builds a full-screen image page with left/right tap zones. */
    private static class FrameTapView {
        final View root;

        FrameTapView(ViewGroup parent) {
            root = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_story_page, parent, false);
        }
    }
}
