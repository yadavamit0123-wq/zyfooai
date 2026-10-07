package com.pt.zyfooai.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pt.zyfooai.R;
import com.pt.zyfooai.respository.UserStoryRepository;

import java.util.ArrayList;
import java.util.List;

public class AddUserStoryActivity extends AppCompatActivity {

    public static final int MAX_IMAGES = 10;

    private final ArrayList<Uri> selected = new ArrayList<>();
    private PreviewAdapter previewAdapter;
    private View progress;
    private View btnPost;
    private final UserStoryRepository repository = new UserStoryRepository();

    private final ActivityResultLauncher<String> pickImages = registerForActivityResult(
            new ActivityResultContracts.GetMultipleContents(),
            uris -> {
                if (uris == null || uris.isEmpty()) {
                    return;
                }
                selected.clear();
                int limit = Math.min(uris.size(), MAX_IMAGES);
                for (int i = 0; i < limit; i++) {
                    selected.add(uris.get(i));
                }
                if (uris.size() > MAX_IMAGES) {
                    Toast.makeText(this, "Max " + MAX_IMAGES + " photos", Toast.LENGTH_SHORT).show();
                }
                previewAdapter.notifyDataSetChanged();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_story);

        progress = findViewById(R.id.progress);
        btnPost = findViewById(R.id.btnPost);
        RecyclerView previewRecycler = findViewById(R.id.previewRecycler);
        previewAdapter = new PreviewAdapter();
        previewRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        previewRecycler.setAdapter(previewAdapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPick).setOnClickListener(v -> pickImages.launch("image/*"));
        btnPost.setOnClickListener(v -> postStory());
    }

    private void postStory() {
        if (selected.isEmpty()) {
            Toast.makeText(this, R.string.add_story_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        setUploading(true);
        repository.createStory(this, selected).observe(this, response -> {
            setUploading(false);
            if (response != null && response.data != null) {
                Toast.makeText(this, R.string.add_story_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else if (response != null && response.success) {
                Toast.makeText(this, R.string.add_story_success, Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else {
                String msg = response != null && response.message != null
                        ? response.message
                        : getString(R.string.add_story_failed);
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUploading(boolean uploading) {
        progress.setVisibility(uploading ? View.VISIBLE : View.GONE);
        btnPost.setEnabled(!uploading);
        findViewById(R.id.btnPick).setEnabled(!uploading);
    }

    private class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.Holder> {
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_story_preview, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Glide.with(holder.image).load(selected.get(position)).centerCrop().into(holder.image);
        }

        @Override
        public int getItemCount() {
            return selected.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final ImageView image;

            Holder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.previewImage);
            }
        }
    }
}
