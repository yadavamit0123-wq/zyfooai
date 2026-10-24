package com.pt.zyfooai.ui.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
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
    private TextView hintTv;
    private final UserStoryRepository repository = new UserStoryRepository();

    /** System Photo Picker — multi-select up to {@link #MAX_IMAGES}. */
    private final ActivityResultLauncher<PickVisualMediaRequest> pickImages =
            registerForActivityResult(
                    new ActivityResultContracts.PickMultipleVisualMedia(MAX_IMAGES),
                    uris -> {
                        if (uris == null || uris.isEmpty()) {
                            return;
                        }
                        int added = 0;
                        for (Uri uri : uris) {
                            if (uri == null) {
                                continue;
                            }
                            if (selected.size() >= MAX_IMAGES) {
                                Toast.makeText(this, getString(R.string.add_story_max, MAX_IMAGES),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            }
                            if (!containsUri(uri)) {
                                selected.add(uri);
                                added++;
                            }
                        }
                        if (added == 0 && selected.size() >= MAX_IMAGES) {
                            Toast.makeText(this, getString(R.string.add_story_max, MAX_IMAGES),
                                    Toast.LENGTH_SHORT).show();
                        }
                        updateHint();
                        previewAdapter.notifyDataSetChanged();
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_story);

        progress = findViewById(R.id.progress);
        btnPost = findViewById(R.id.btnPost);
        hintTv = findViewById(R.id.hintTv);
        RecyclerView previewRecycler = findViewById(R.id.previewRecycler);
        previewAdapter = new PreviewAdapter();
        previewRecycler.setLayoutManager(new GridLayoutManager(this, 3));
        previewRecycler.setAdapter(previewAdapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnPick).setOnClickListener(v -> openPicker());
        btnPost.setOnClickListener(v -> postStory());
        updateHint();
    }

    private void openPicker() {
        if (selected.size() >= MAX_IMAGES) {
            Toast.makeText(this, getString(R.string.add_story_max, MAX_IMAGES), Toast.LENGTH_SHORT).show();
            return;
        }
        pickImages.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()
        );
    }

    private boolean containsUri(Uri uri) {
        for (Uri existing : selected) {
            if (existing != null && existing.equals(uri)) {
                return true;
            }
        }
        return false;
    }

    private void updateHint() {
        if (hintTv == null) {
            return;
        }
        hintTv.setText(getString(R.string.add_story_hint_count, selected.size(), MAX_IMAGES));
    }

    private void postStory() {
        if (selected.isEmpty()) {
            Toast.makeText(this, R.string.add_story_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        setUploading(true);
        List<Uri> toUpload = new ArrayList<>(selected);
        repository.createStory(this, toUpload).observe(this, response -> {
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
            holder.btnRemove.setOnClickListener(v -> {
                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION || pos >= selected.size()) {
                    return;
                }
                selected.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, selected.size() - pos);
                updateHint();
            });
        }

        @Override
        public int getItemCount() {
            return selected.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final ImageView image;
            final View btnRemove;

            Holder(@NonNull View itemView) {
                super(itemView);
                image = itemView.findViewById(R.id.previewImage);
                btnRemove = itemView.findViewById(R.id.btnRemove);
            }
        }
    }
}
