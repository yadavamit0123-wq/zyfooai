package com.growwthapps.dailypost.v2.ui.fragments;

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
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.FragmentSelectStickerBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.listener.ClickListener;
import com.growwthapps.dailypost.v2.model.CategoryItem;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.ui.adapters.CategorysAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.MusicAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.StickersAdapter;
import com.growwthapps.dailypost.v2.utils.Constant;

import java.util.ArrayList;
import java.util.List;


public class SelectMusicFragment extends BottomSheetDialogFragment {


    public SelectMusicFragment() {}

    FragmentSelectStickerBinding binding;
    private String selectedCat = "-1";
    List<CategoryItem> categoryItemList = new ArrayList<>();
    Context context;

    public ClickListener<PostItem> listener;

    public SelectMusicFragment(ClickListener<PostItem> listener) {
        this.listener = listener;
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
                        listener.onClick(postItem);
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
        if (musicPlayer != null){
            musicPlayer.setPlayWhenReady(false);
            musicPlayer.release();
        }
    }

    ExoPlayer musicPlayer;
    private void playMusic(String path) {
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.setPlayWhenReady(false);
            musicPlayer.release();
        }
        TrackSelector trackSelectorDef = new DefaultTrackSelector();
        musicPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);

        int appNameStringRes = R.string.app_name;
        String userAgent = Util.getUserAgent(context, context.getString(appNameStringRes));
        DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
        Uri uriOfContentUrl = Uri.parse(path);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source

        musicPlayer.prepare(mediaSource);
        musicPlayer.setPlayWhenReady(true);
        musicPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
        musicPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

            }
        });
    }
}