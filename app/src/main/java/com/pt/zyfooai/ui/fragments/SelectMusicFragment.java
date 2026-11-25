package com.pt.zyfooai.ui.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pt.zyfooai.R;
import com.pt.zyfooai.databinding.FragmentSelectStickerBinding;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.listener.ClickListener;
import com.pt.zyfooai.model.CategoryItem;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.ui.adapters.CategorysAdapter;
import com.pt.zyfooai.ui.adapters.MusicAdapter;
import com.pt.zyfooai.ui.adapters.StickersAdapter;
import com.pt.zyfooai.utils.Constant;

import java.util.ArrayList;
import java.util.List;


public class SelectMusicFragment extends BottomSheetDialogFragment {


    public SelectMusicFragment() {}

    FragmentSelectStickerBinding binding;
    private String selectedCat = "-1";
    List<CategoryItem> categoryItemList = new ArrayList<>();
    Context context;

    public ClickListener<PostItem> listener;
    private Runnable onDismissExtra;
    private String currentPreviewPath = "";

    public SelectMusicFragment(ClickListener<PostItem> listener) {
        this.listener = listener;
    }

    public void setOnDismissListener(Runnable onDismiss) {
        this.onDismissExtra = onDismiss;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSelectStickerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        loadCategories();

        binding.closeBtn.setOnClickListener(view1 -> {
            stopMusic();
            dismiss();
        });
        binding.title.setText("Music");

        binding.backgroundRv.setLayoutManager(new LinearLayoutManager(context));
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        stopMusic();
        if (onDismissExtra != null) {
            onDismissExtra.run();
            onDismissExtra = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMusic();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopMusic();
    }

    private void loadCategories() {
        Constant.getHomeViewModel(this).getMusicCategory().observe(getViewLifecycleOwner(), categoryItems -> {
            if (categoryItems != null && !categoryItems.isEmpty()) {
                categoryItemList.addAll(categoryItems);

                selectedCat = categoryItemList.get(0).getId();
                getMusics();

                binding.categoryBgRV.setAdapter(new CategorysAdapter(context, categoryItemList, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        selectedCat = categoryItemList.get(pos).getId();
                        getMusics();
                    }
                }));
            }
        });
    }


    private void getMusics() {
        binding.loadingBar.setVisibility(View.VISIBLE);
        Constant.getHomeViewModel(this).getMusicByCategory(selectedCat).observe(getViewLifecycleOwner(), postItems -> {
            binding.loadingBar.setVisibility(View.GONE);
            if (postItems != null && !postItems.isEmpty()) {
                binding.backgroundRv.setAdapter(new MusicAdapter(context, postItems, new MusicAdapter.OnMusicSelect() {
                    @Override
                    public void onSelect(PostItem postItem) {
                        stopMusic();
                        if (listener != null) {
                            listener.onClick(postItem);
                        }
                        dismiss();
                    }

                    @Override
                    public void onPlay(String path) {
                        playMusic(path);
                    }

                    @Override
                    public void onStop() {
                        stopMusic();
                    }
                }));
                binding.noDataLay.setVisibility(View.GONE);
            } else {
                binding.noDataLay.setVisibility(View.VISIBLE);
            }
        });
    }

    private void stopMusic() {
        currentPreviewPath = "";
        if (musicPlayer != null) {
            try {
                musicPlayer.setPlayWhenReady(false);
                musicPlayer.stop(true);
                musicPlayer.release();
            } catch (Exception ignored) {
            }
            musicPlayer = null;
        }
    }

    ExoPlayer musicPlayer;
    private void playMusic(String path) {
        // Always stop previous preview first — only one track at a time.
        if (musicPlayer != null) {
            try {
                musicPlayer.setPlayWhenReady(false);
                musicPlayer.stop(true);
                musicPlayer.release();
            } catch (Exception ignored) {
            }
            musicPlayer = null;
        }
        if (path == null || path.isEmpty() || context == null) {
            return;
        }
        currentPreviewPath = path;
        TrackSelector trackSelectorDef = new DefaultTrackSelector();
        musicPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);

        String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
        DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        Uri uriOfContentUrl = Uri.parse(path);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(defdataSourceFactory)
                .createMediaSource(uriOfContentUrl);

        musicPlayer.prepare(mediaSource);
        musicPlayer.setPlayWhenReady(true);
        musicPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
    }
}