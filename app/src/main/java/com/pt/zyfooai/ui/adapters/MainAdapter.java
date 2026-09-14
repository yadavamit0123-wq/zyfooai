package com.pt.zyfooai.ui.adapters;

import static com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING;
import static com.pt.zyfooai.utils.Constant.ADS_ENABLE;
import static com.pt.zyfooai.utils.Constant.IS_SUBSCRIBE;
import static com.pt.zyfooai.utils.Constant.NATIVE_AD_ENABLED;
import static com.pt.zyfooai.utils.Constant.NATIVE_AD_ID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.ump.ConsentInformation;
import com.pt.zyfooai.AdsUtils.GDPRChecker;
import com.pt.zyfooai.R;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.AdViewBinding;
import com.pt.zyfooai.databinding.ItemMainLayoutBinding;
import com.pt.zyfooai.databinding.ItemMainVideoLayoutBinding;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.ui.activities.EditProfileActivity;
import com.pt.zyfooai.ui.activities.MainActivity;
import com.pt.zyfooai.ui.activities.SubscriptionActivity;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.BillingHelper;
import com.pt.zyfooai.utils.FrameOverlayHelper;
import com.pt.zyfooai.utils.FramePolishHelper;
import com.pt.zyfooai.utils.FooterSizeHelper;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.ReferralHelper;
import com.pt.zyfooai.utils.SnapHelperOneByOne;
import com.pt.zyfooai.utils.VideoCacheHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ListPreloader.PreloadModelProvider<String> {

    Activity context;
    List<PostItem> list;
    private PreferenceManager preferenceManager;

    public void setData(List<PostItem> daily_post) {
        int oldSize = list.size();
        this.list = daily_post;
        int added = daily_post.size() - oldSize;
        if (added > 0) {
            notifyItemRangeInserted(oldSize, added);
        }
    }

    public void replaceData(List<PostItem> daily_post) {
        this.list = daily_post;
        notifyDataSetChanged();
    }

    OnClickEvent onClickEvent;

    public interface OnClickEvent {
        void onClick(View view, View posterview, PostItem postItem);
    }

    private static final int VIEW_TYPE_CONTENT = 1;
    public static final int VIEW_TYPE_AD = 0;
    public static final int VIEW_TYPE_VIDEO = 2;

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        if (list.get(position) != null && list.get(position).image_url != null) {
            return Collections.singletonList(list.get(position).image_url);
        } else {
            return Collections.emptyList(); // Return an empty list instead of null
        }
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull String url) {
        return Glide.with(context).load(url);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_AD) {
            AdViewBinding adBinding = AdViewBinding.inflate(inflater, parent, false);
            return new ViewHolder(adBinding);
        } else if (viewType == VIEW_TYPE_VIDEO) {
            ItemMainVideoLayoutBinding adBinding = ItemMainVideoLayoutBinding.inflate(inflater, parent, false);
            return new ViewHolderVideo(adBinding);
        } else {
            ItemMainLayoutBinding itemBinding = ItemMainLayoutBinding.inflate(inflater, parent, false);
            return new ViewHolder(itemBinding);
        }
    }


    public MainAdapter(Activity context, List<PostItem> list, OnClickEvent onClickEvent) {
        this.context = context;
        this.list = list;
        this.onClickEvent = onClickEvent;
        this.preferenceManager = new PreferenceManager(context);
    }


    public void clearData() {
        list.clear();
        notifyDataSetChanged();
    }

    int layoutWidth = 0;
    int layoutHeight = 0;
    int imageWidth = 0;
    int imageHeight = 0;


    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof ViewHolderVideo) {
            ViewHolderVideo videoHolder = (ViewHolderVideo) holder;
            if (currentHolder == videoHolder) {
                pauseAndDetachPlayer();
            }
            videoHolder.videoLayoutBinding.playerview.setPlayer(null);
        }
    }

    public void releasePlayer(ViewHolderVideo holder) {
        if (holder != null && currentHolder == holder) {
            pauseAndDetachPlayer();
        }
    }

    public void pauseAndDetachPlayer() {
        if (sharedPlayer != null) {
            sharedPlayer.setPlayWhenReady(false);
        }
        if (currentHolder != null && currentHolder.videoLayoutBinding != null) {
            currentHolder.videoLayoutBinding.playerview.setPlayer(null);
        }
    }

    public void stopAndClearPlayer() {
        pauseAndDetachPlayer();
        if (sharedPlayer != null) {
            sharedPlayer.stop();
        }
        currentHolder = null;
        currentPlayingVideo = "";
        currentPlayer = null;
    }

    private boolean shouldHideWatermark() {
        return preferenceManager.getBoolean(IS_SUBSCRIBE)
                || BillingHelper.isWatermarkPurchased(context)
                || ReferralHelper.hasReferralReward(context);
    }

    private void setupFrameRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> frameAdapter) {
        if (recyclerView.getTag(R.id.frame_recycler_setup) == null) {
            recyclerView.setTag(R.id.frame_recycler_setup, Boolean.TRUE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.setAdapter(frameAdapter);
            new SnapHelperOneByOne().attachToRecyclerView(recyclerView);
        } else {
            recyclerView.setAdapter(frameAdapter);
        }
    }

    private void setupFooterControls(
            View footer,
            View contentArea,
            RecyclerView frameRecyclerView,
            android.widget.SeekBar stripSeekBar,
            android.widget.SeekBar frameSizeSeekBar,
            android.widget.SeekBar frameAlphaSeekBar,
            View updatePhotoBtn
    ) {
        Runnable refreshLayout = () -> {
            FooterSizeHelper.applyFooterScale(footer, preferenceManager);
            FrameOverlayHelper.applyFrameOverlay(frameRecyclerView, preferenceManager);
            FooterSizeHelper.fitContentAboveFooter(contentArea, footer);
        };
        FooterSizeHelper.applyFooterScale(footer, preferenceManager);
        FrameOverlayHelper.applyFrameOverlay(frameRecyclerView, preferenceManager);
        if (stripSeekBar != null && stripSeekBar.getTag() == null) {
            stripSeekBar.setTag(Boolean.TRUE);
            FooterSizeHelper.bindFooterSizeSeekBar(stripSeekBar, preferenceManager, refreshLayout);
        }
        FrameOverlayHelper.bindSeekBar(
                frameSizeSeekBar,
                FrameOverlayHelper.FRAME_SCALE,
                50,
                preferenceManager,
                refreshLayout
        );
        FrameOverlayHelper.bindSeekBar(
                frameAlphaSeekBar,
                FrameOverlayHelper.FRAME_ALPHA,
                100,
                preferenceManager,
                refreshLayout
        );
        if (updatePhotoBtn != null && updatePhotoBtn.getTag() == null) {
            updatePhotoBtn.setTag(Boolean.TRUE);
            updatePhotoBtn.setOnClickListener(v ->
                    context.startActivity(new Intent(context, EditProfileActivity.class)));
        }
        FooterSizeHelper.fitContentAboveFooter(contentArea, footer);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder2, int position) {
        int itemViewType = holder2.getItemViewType();
        if (getItemViewType(position) == VIEW_TYPE_CONTENT) {
            if (currentPlayer != null && currentPlayer.isPlaying()) {
                currentPlayer.setPlayWhenReady(false); // pause only
            }
        }
        if (itemViewType == VIEW_TYPE_AD) {
            final ViewHolder holder = (ViewHolder) holder2;
            holder.setIsRecyclable(false);
            Log.d("IS_SUBSCRIBE", "onBindViewHolder: 11 "+preferenceManager.getBoolean(IS_SUBSCRIBE));
            holder.binding1.purchaseBtn.setOnClickListener(view -> {
                context.startActivity(new Intent(context, SubscriptionActivity.class));
            });
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {
                if (preferenceManager.getBoolean(ADS_ENABLE) && preferenceManager.getBoolean(NATIVE_AD_ENABLED)) {
                    Log.d("IS_SUBSCRIBE", "ADS_ENABLE: " + preferenceManager.getBoolean(ADS_ENABLE) + " .. " + preferenceManager.getBoolean(NATIVE_AD_ENABLED));
                    AdLoader.Builder builder = new AdLoader.Builder(context, preferenceManager.getString(NATIVE_AD_ID))
                            .forNativeAd(nativeAd1 -> {
                                holder.binding1.shimmer.stopShimmer();
                                holder.binding1.shimmer.setVisibility(View.GONE);
                                RelativeLayout adView = (RelativeLayout) context.getLayoutInflater().inflate(R.layout.ad_creation, null);
                                populateUnifiedNativeAdView(nativeAd1, adView.findViewById(R.id.unified));
                                holder.binding1.flAdplaceholder.removeAllViews();
                                holder.binding1.flAdplaceholder.addView(adView);

                            });
                    AdRequest.Builder m_builder = new AdRequest.Builder();
                    int request = GDPRChecker.getStatus();
                    if (request == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        m_builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                    }
                    builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(m_builder.build());
                }
            }
        } else if (itemViewType == VIEW_TYPE_VIDEO) {

            final ViewHolderVideo holder = (ViewHolderVideo) holder2;
            holder.setIsRecyclable(false);
            holder.videoLayoutBinding.setPosts(list.get(position));

            holder.videoLayoutBinding.watermarkLayout.setVisibility(shouldHideWatermark() ? View.GONE : View.VISIBLE);


            holder.currentPosition = position;
            FrameOverlayHelper.prepareContentForLayout(holder.videoLayoutBinding.mainLayOut);
            setupFooterControls(
                    holder.videoLayoutBinding.swipeFrames,
                    holder.videoLayoutBinding.relativeLayout4,
                    holder.videoLayoutBinding.recyclerview,
                    holder.videoLayoutBinding.footerSizeSeekBar,
                    holder.videoLayoutBinding.frameSizeSeekBar,
                    holder.videoLayoutBinding.frameAlphaSeekBar,
                    holder.videoLayoutBinding.updateStripPhotoBtn
            );

            CustomPagerVideoAdapter customPagerAdapter = new CustomPagerVideoAdapter(list.get(position).image_url);
            setupFrameRecyclerView(holder.videoLayoutBinding.recyclerview, customPagerAdapter);
            holder.videoLayoutBinding.indicator.attachToRecyclerView(holder.videoLayoutBinding.recyclerview);

            int random = new Random().nextInt(2) + 1;
            holder.videoLayoutBinding.recyclerview.scrollToPosition(random);

            View.OnClickListener onClickListener = view -> {
                holder.videoLayoutBinding.ivWatermark.setImageResource(R.drawable.watermark);
                onClickEvent.onClick(view, holder.videoLayoutBinding.mainLayOut, list.get(position));
            };

            holder.videoLayoutBinding.watermarkLayout.setOnClickListener(onClickListener);
            holder.videoLayoutBinding.downloadBtn.setOnClickListener(onClickListener);
            holder.videoLayoutBinding.shareBtn.setOnClickListener(onClickListener);

            for (LinearLayout linearLayout : Arrays.asList(holder.videoLayoutBinding.shareBtn, holder.videoLayoutBinding.editBtn)) {
                linearLayout.setOnClickListener(onClickListener);
            }

//            holder.videoLayoutBinding.nextBtn.setOnClickListener(view -> next(holder, position, view));

        } else {

            final ViewHolder holder = (ViewHolder) holder2;
            holder.setIsRecyclable(false);
            holder.binding.setPosts(list.get(position));
//            holder.binding.ivPremium.setVisibility(View.GONE);
            Log.d("farukh------------->", "updatePlayer: " + list.get(position).image_url);

            if (!preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Personal")) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.binding.imagePost.getLayoutParams();
                int topMarginInPx = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        55, // Replace 16 with your desired margin value in dp
                        holder.itemView.getResources().getDisplayMetrics()
                );
                layoutParams.topMargin = topMarginInPx;
                holder.binding.imagePost.setLayoutParams(layoutParams);
            }

            FrameOverlayHelper.prepareContentForLayout(holder.binding.mainLayOut);
            setupFooterControls(
                    holder.binding.swipeFrames,
                    holder.binding.relativeLayout4,
                    holder.binding.recyclerview,
                    holder.binding.footerSizeSeekBar,
                    holder.binding.frameSizeSeekBar,
                    holder.binding.frameAlphaSeekBar,
                    holder.binding.updateStripPhotoBtn
            );

            holder.binding.mainLayOut.post(() -> {
                layoutWidth = holder.binding.mainLayOut.getWidth();
                layoutHeight = holder.binding.mainLayOut.getHeight();
            });

            Glide.with(holder.binding.imagePost.getContext())
                    .load(list.get(position).image_url)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .dontAnimate()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(
                                @Nullable GlideException e,
                                Object model,
                                Target<Drawable> target,
                                boolean isFirstResource
                        ) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(
                                Drawable resource,
                                Object model,
                                Target<Drawable> target,
                                DataSource dataSource,
                                boolean isFirstResource
                        ) {

                            imageWidth = resource.getIntrinsicWidth();
                            imageHeight = resource.getIntrinsicHeight();
                            tryCalculateSpace(holder);
                            FooterSizeHelper.fitContentAboveFooter(
                                    holder.binding.relativeLayout4,
                                    holder.binding.swipeFrames
                            );

                            return false; // allow Glide to set image
                        }
                    })
                    .into(holder.binding.imagePost);
//            GlideDataBinding.bindImage(holder.binding.imagePost, list.get(position).image_url);


            CustomPagerAdapter customPagerAdapter = new CustomPagerAdapter(list.get(position).image_url);
            setupFrameRecyclerView(holder.binding.recyclerview, customPagerAdapter);
            holder.binding.indicator.attachToRecyclerView(holder.binding.recyclerview);

            int random = new Random().nextInt(3) + 1;
            holder.binding.recyclerview.scrollToPosition(random);

            holder.binding.watermarkLayout.setVisibility(shouldHideWatermark() ? View.GONE : View.VISIBLE);

            View.OnClickListener onClickListener = view -> {
                LinearLayoutManager layoutManager = (LinearLayoutManager) holder.binding.recyclerview.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    RecyclerView.ViewHolder viewHolder = holder.binding.recyclerview.findViewHolderForAdapterPosition(firstVisiblePosition);
                    if (viewHolder != null) {
                        View itemView = viewHolder.itemView;
                        MainActivity.currentFrameImageView = itemView.findViewById(R.id.profileLay);
                        MainActivity.currentFrameBusinessImageView = itemView.findViewById(R.id.businesslogoImg);
                    }
                }
                holder.binding.ivWatermark.setImageResource(R.drawable.watermark);
                onClickEvent.onClick(view, holder.binding.mainLayOut, list.get(position));
            };

            holder.binding.watermarkLayout.setOnClickListener(onClickListener);
            holder.binding.downloadBtn.setOnClickListener(onClickListener);
            holder.binding.shareBtn.setOnClickListener(onClickListener);

            for (LinearLayout linearLayout : Arrays.asList(holder.binding.shareBtn, holder.binding.editBtn)) {
                linearLayout.setOnClickListener(onClickListener);
            }

            holder.binding.musicBtn.setOnClickListener(view -> next(holder, position, view));
        }
    }
    private ExoPlayer currentPlayer;
    private ExoPlayer sharedPlayer;
    public ViewHolderVideo currentHolder;
    public String currentPlayingVideo = "";

    public void updatePlayer(ViewHolderVideo holder, PostItem item) {
        if (item == null || item.image_url == null || item.image_url.isEmpty()) {
            return;
        }
        String newUrl = item.image_url;

        if (currentHolder != null && currentHolder != holder) {
            currentHolder.videoLayoutBinding.playerview.setPlayer(null);
        }

        if (sharedPlayer == null) {
            TrackSelector trackSelectorDef = new DefaultTrackSelector();
            sharedPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);
            sharedPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            sharedPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (currentHolder != null && playbackState == ExoPlayer.STATE_READY) {
                        currentHolder.videoLayoutBinding.loader.setVisibility(View.GONE);
                        FooterSizeHelper.fitContentAboveFooter(
                                currentHolder.videoLayoutBinding.relativeLayout4,
                                currentHolder.videoLayoutBinding.swipeFrames
                        );
                    }
                }
            });
        }

        holder.videoLayoutBinding.loader.setVisibility(View.VISIBLE);
        holder.videoLayoutBinding.playerview.setUseController(false);
        holder.videoLayoutBinding.playerview.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING);

        if (!newUrl.equals(currentPlayingVideo)) {
            currentPlayingVideo = newUrl;
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                    VideoCacheHelper.buildCacheDataSourceFactory(context))
                    .createMediaSource(Uri.parse(newUrl));
            sharedPlayer.stop();
            sharedPlayer.prepare(mediaSource);
        }

        holder.videoLayoutBinding.playerview.setPlayer(sharedPlayer);
        sharedPlayer.setPlayWhenReady(true);
        holder.exoplayer = sharedPlayer;
        currentPlayer = sharedPlayer;
        currentHolder = holder;
//        String newUrl = Functions.getItemBaseUrl(item.item_url);
//        Log.d("farukh------>", "updatePlayer: "+newUrl);
        // Only set new media if the URL has changed or player is newly initialized
//        if (!newUrl.equals(holder.currentVideoUrl) || holder.player.getMediaItemCount() == 0) {
//            holder.currentVideoUrl = newUrl;
//
//            String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
//            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, userAgent);
//            MediaItem mediaItem = MediaItem.fromUri(newUrl);
//            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
//                    .createMediaSource(mediaItem);
//
//            holder.player.setMediaSource(mediaSource);
//            holder.player.prepare();
////            holder.player.setPlayWhenReady(true);
//        } else {
//            // If the URL is the same, just ensure it's playing
////            holder.player.setPlayWhenReady(true);
//        }
    }


    public void onPauseVideo() {
        if (sharedPlayer != null) {
            sharedPlayer.setPlayWhenReady(false);
        } else if (currentPlayer != null) {
            currentPlayer.setPlayWhenReady(false);
        }
    }

    public void onResumeVideo() {
        if (sharedPlayer != null && currentHolder != null) {
            sharedPlayer.setPlayWhenReady(true);
        } else if (currentPlayer != null && currentHolder != null) {
            currentPlayer.setPlayWhenReady(true);
        }
    }

    public void releaseSharedPlayer() {
        pauseAndDetachPlayer();
        if (sharedPlayer != null) {
            sharedPlayer.release();
            sharedPlayer = null;
        }
    }

    int horizontalSpace = 0;
    int bottomSpace = 0;

    private void tryCalculateSpace(ViewHolder holder) {


        Log.d("farukh-------->layout", "tryCalculateSpace: width : " + layoutWidth + ", height : " + layoutHeight);
        Log.d("farukh-------->image", "tryCalculateSpace: width : " + imageWidth + ", height : " + imageHeight);
        if (layoutWidth == 0 ||
                layoutHeight == 0 ||
                imageHeight == 0) {
            return; // not ready yet
        }

        if (layoutHeight < imageHeight) {

            double ratio =
                    (double) layoutHeight / (double) imageHeight;

            double finalWidth =
                    (double) layoutWidth * ratio;

            double margin =
                    (double) layoutWidth - finalWidth;

            horizontalSpace = (int) Math.round(margin / 2.0);
        } else {
            horizontalSpace = 0;
        }
        if (imageHeight < layoutHeight) {
            bottomSpace = layoutHeight - imageHeight;
        } else {
            bottomSpace = 0;
        }
        applyFrameSpacingDecoration(holder);
        holder.binding.mainLayOut.setAlpha(1f);
    }

    private void applyFrameSpacingDecoration(ViewHolder holder) {
        if (holder.binding.recyclerview.getTag(R.id.frame_spacing_applied) == Boolean.TRUE) {
            return;
        }
        holder.binding.recyclerview.setTag(R.id.frame_spacing_applied, Boolean.TRUE);
        holder.binding.recyclerview.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view,
                                       RecyclerView parent, RecyclerView.State state) {
                outRect.left = horizontalSpace;
                outRect.right = horizontalSpace;
                outRect.bottom = bottomSpace;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        PostItem item = list.get(position);
//        Log.d("getItemViewType_", " : "+item.is_video);
        if (item == null) {
            return VIEW_TYPE_AD;
        } else if (item.is_video) { // Assuming PostItem has a method isVideo() to check if it's a video
            return VIEW_TYPE_VIDEO;
        } else {
            return VIEW_TYPE_CONTENT;
        }
//        return list.get(position) != null ? VIEW_TYPE_CONTENT : VIEW_TYPE_AD;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemMainLayoutBinding binding;
        public ItemMainVideoLayoutBinding videoLayoutBinding;
        AdViewBinding binding1;

        public ViewHolder(@NonNull ItemMainLayoutBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public ViewHolder(@NonNull AdViewBinding binding) {
            super(binding.getRoot());
            this.binding1 = binding;
        }

        public ViewHolder(ItemMainVideoLayoutBinding videoLayoutBinding) {
            super(videoLayoutBinding.getRoot());
            this.videoLayoutBinding = videoLayoutBinding;
        }
    }


    public class ViewHolderVideo extends RecyclerView.ViewHolder {

        ItemMainLayoutBinding binding;
        public ItemMainVideoLayoutBinding videoLayoutBinding;
        AdViewBinding binding1;
        public ExoPlayer exoplayer;

        int currentPosition;

        public ViewHolderVideo(ItemMainVideoLayoutBinding videoLayoutBinding) {
            super(videoLayoutBinding.getRoot());
            this.videoLayoutBinding = videoLayoutBinding;
        }
    }

    public class CustomPagerAdapter extends RecyclerView.Adapter<CustomPagerAdapter.ViewHolder> {

        String item_url;
        List<Integer> list = new ArrayList<>();

        public CustomPagerAdapter(String str) {
            this.item_url = str;

            list.add(R.layout.layout_frame_1_1);
            list.add(R.layout.layout_frame_1_2);
            list.add(R.layout.layout_frame_1_3);
            list.add(R.layout.layout_frame_1_4);
            list.add(R.layout.layout_frame_1_5);
            list.add(R.layout.layout_frame_1_6);
            list.add(R.layout.layout_frame_glass_1);
            list.add(R.layout.layout_frame_glass_2);
            list.add(R.layout.layout_frame_gradient_1);
            list.add(R.layout.layout_frame_gradient_2);

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(list.get(viewType), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);


            holder.userNameTv.setText(preferenceManager.getString(Constant.USER_NAME));
            holder.userDesTv.setText(preferenceManager.getString(Constant.USER_DESIGNATION));

            if (preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Personal")) {
                holder.facebookTv.setText(preferenceManager.getString(Constant.USER_FACEBOOK));
                holder.instagramTv.setText(preferenceManager.getString(Constant.USER_INSTAGRAM));

                holder.businessAddressTv.setText(preferenceManager.getString(Constant.USER_PHONE));
                Drawable[] drawables = holder.businessAddressTv.getCompoundDrawables();
                Drawable leftDrawable = drawables[0]; // Left position
                Drawable rightDrawable = drawables[2]; // Right position
                if (leftDrawable != null) {
                    holder.businessAddressTv.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_phone_number), null, null, null);
                } else if (rightDrawable != null) {
                    holder.businessAddressTv.setCompoundDrawableTintList(null);
                    holder.businessAddressTv.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.frame_6_call_icon), null);
                }

                holder.itemView.findViewById(R.id.topLay).setVisibility(View.GONE);

                holder.itemView.findViewById(R.id.whatsappLay).setVisibility(View.GONE);

                Glide.with(context)
                        .load(preferenceManager.getString(Constant.USER_IMAGE))
                        .placeholder(R.drawable.ic_add_images)
                        .into(holder.userImgView);
            } else {
                holder.businessNameTv.setText(preferenceManager.getString(Constant.BUSINESS_NAME));
                holder.businessDesTv.setText(preferenceManager.getString(Constant.BUSINESS_DETAIL));
                holder.businessNumberTv.setText(preferenceManager.getString(Constant.BUSINESS_NUMBER));
                holder.businessWebsiteTv.setText(preferenceManager.getString(Constant.BUSINESS_WEBSITE));
                holder.businessAddressTv.setText(preferenceManager.getString(Constant.BUSINESS_ADDRESS));
                holder.whatsapptv.setText(preferenceManager.getString(Constant.BUSINESS_WHATSAPP));
                holder.facebookTv.setText(preferenceManager.getString(Constant.BUSINESS_FACEBOOK));
                holder.instagramTv.setText(preferenceManager.getString(Constant.BUSINESS_INSTAGRAM));

                // Load business image
                Glide.with(context)
                        .load(preferenceManager.getString(Constant.USER_IMAGE))
                        .placeholder(R.drawable.ic_add_images)
                        .into(holder.userImgView);
                GlideDataBinding.bindImage(holder.businessImgView, preferenceManager.getString(Constant.BUSINESS_IMAGE));
            }
//            if (!preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Personal")) {
//                int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, holder.itemView.getResources().getDisplayMetrics());
//
//                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.userDesTv.getLayoutParams();
//                int topMarginInPx = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP,
//                        2,
//                        holder.itemView.getResources().getDisplayMetrics()
//                );
//                layoutParams.topMargin = topMarginInPx;
//                holder.userDesTv.setLayoutParams(layoutParams);
//                holder.userDesTv.setMaxLines(2);
//                holder.userNameTv.setTextSize(textSize);
//            }
//
//            if (holder.businessAddressTv.getText().toString().isEmpty()){
//                int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, holder.itemView.getResources().getDisplayMetrics());
//                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) holder.userDesTv.getLayoutParams();
//                int topMarginInPx = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP,
//                        2,
//                        holder.itemView.getResources().getDisplayMetrics()
//                );
//                layoutParams.topMargin = topMarginInPx;
//                holder.userDesTv.setLayoutParams(layoutParams);
//                holder.userDesTv.setMaxLines(2);
//                holder.userNameTv.setTextSize(textSize);
//            }

            setVisibilityIfEmpty(holder.facebookTv, holder.itemView.findViewById(R.id.facebookLay));
            setVisibilityIfEmpty(holder.instagramTv, holder.itemView.findViewById(R.id.instagramLay));
            setVisibilityIfEmpty(holder.whatsapptv, holder.itemView.findViewById(R.id.whatsappLay));

            setVisibilityIfEmpty(holder.businessNameTv);
            setVisibilityIfEmpty(holder.businessDesTv);
            setVisibilityIfEmpty(holder.businessNumberTv);
            setVisibilityIfEmpty(holder.businessWebsiteTv);
            setVisibilityIfEmpty(holder.businessAddressTv);
            setVisibilityIfEmpty(holder.userDesTv);

            if (holder.dateTv != null) {
                String currentDate = new SimpleDateFormat("dd MMM", Locale.getDefault())
                        .format(new Date()).toUpperCase(Locale.ROOT);
                holder.dateTv.setText(currentDate);
            }
            applyGlassBusinessHeader(holder.itemView);
            FramePolishHelper.apply(holder.itemView, position);
        }

        private <T extends View> void setVisibilityIfEmpty(TextView textView, T view) {
            if (textView.getText().toString().trim().isEmpty()) {
                view.setVisibility(View.GONE);
            }
        }

        private void setVisibilityIfEmpty(TextView textView) {
            if (textView.getText().toString().trim().isEmpty()) {
                textView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView businessNameTv, businessDesTv, businessNumberTv, businessWebsiteTv, businessAddressTv;
            TextView userNameTv, userDesTv, facebookTv, instagramTv, whatsapptv;
            TextView dateTv;
            RelativeLayout framLay;
            ImageView userImgView, businessImgView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                businessNameTv = itemView.findViewById(R.id.businessNameTv);
                businessDesTv = itemView.findViewById(R.id.businessDesTv);
                businessNumberTv = itemView.findViewById(R.id.businessNumberTv);
                businessWebsiteTv = itemView.findViewById(R.id.businessWebsiteTv);
                businessAddressTv = itemView.findViewById(R.id.businessAddressTv);
                framLay = itemView.findViewById(R.id.framLay);

                userNameTv = itemView.findViewById(R.id.userNameTv);
                userDesTv = itemView.findViewById(R.id.userDesTv);
                facebookTv = itemView.findViewById(R.id.facebookTv);
                instagramTv = itemView.findViewById(R.id.instagramTv);
                whatsapptv = itemView.findViewById(R.id.whatsappTv);

                dateTv = itemView.findViewById(R.id.dateTv);

                userImgView = itemView.findViewById(R.id.profileImg);
                businessImgView = itemView.findViewById(R.id.businesslogoImg);

            }
        }
    }

    public class CustomPagerVideoAdapter extends RecyclerView.Adapter<CustomPagerVideoAdapter.ViewHolder> {

        String item_url;
        List<Integer> list = new ArrayList<>();

        public CustomPagerVideoAdapter(String str) {
            this.item_url = str;

            list.add(R.layout.layout_video_frame_1);
            list.add(R.layout.layout_video_frame_2);
            list.add(R.layout.layout_video_frame_3);
            list.add(R.layout.layout_video_frame_glass_1);
            list.add(R.layout.layout_video_frame_glass_2);
            list.add(R.layout.layout_video_frame_gradient_1);
            list.add(R.layout.layout_video_frame_gradient_2);

        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(list.get(viewType), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            holder.userNameTv.setText(preferenceManager.getString(Constant.USER_NAME));
            holder.userDesTv.setText(preferenceManager.getString(Constant.USER_DESIGNATION));

            if (preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Personal")) {
                holder.facebookTv.setText(preferenceManager.getString(Constant.USER_FACEBOOK));
                holder.instagramTv.setText(preferenceManager.getString(Constant.USER_INSTAGRAM));

                holder.businessAddressTv.setText(preferenceManager.getString(Constant.USER_PHONE));
                Drawable[] drawables = holder.businessAddressTv.getCompoundDrawables();
                Drawable leftDrawable = drawables[0]; // Left position
                Drawable rightDrawable = drawables[2]; // Right position
                if (leftDrawable != null) {
                    holder.businessAddressTv.setCompoundDrawablesWithIntrinsicBounds(context.getDrawable(R.drawable.ic_phone_number), null, null, null);
                } else if (rightDrawable != null) {
                    holder.businessAddressTv.setCompoundDrawableTintList(null);
                    holder.businessAddressTv.setCompoundDrawablesWithIntrinsicBounds(null, null, context.getDrawable(R.drawable.frame_6_call_icon), null);
                }

                
                
                holder.itemView.findViewById(R.id.topLay).setVisibility(View.GONE);

                holder.itemView.findViewById(R.id.whatsappLay).setVisibility(View.GONE);

                Glide.with(context)
                        .load(preferenceManager.getString(Constant.USER_IMAGE))
                        .placeholder(R.drawable.ic_add_images)
                        .into(holder.userImgView);
            } else {
                holder.businessNameTv.setText(preferenceManager.getString(Constant.BUSINESS_NAME));
                holder.businessDesTv.setText(preferenceManager.getString(Constant.BUSINESS_DETAIL));
                holder.businessNumberTv.setText(preferenceManager.getString(Constant.BUSINESS_NUMBER));
                holder.businessWebsiteTv.setText(preferenceManager.getString(Constant.BUSINESS_WEBSITE));
                holder.businessAddressTv.setText(preferenceManager.getString(Constant.BUSINESS_ADDRESS));
                holder.whatsapptv.setText(preferenceManager.getString(Constant.BUSINESS_WHATSAPP));
                holder.facebookTv.setText(preferenceManager.getString(Constant.BUSINESS_FACEBOOK));
                holder.instagramTv.setText(preferenceManager.getString(Constant.BUSINESS_INSTAGRAM));

                // Load business image
                Glide.with(context)
                        .load(preferenceManager.getString(Constant.USER_IMAGE))
                        .placeholder(R.drawable.ic_add_images)
                        .into(holder.userImgView);
                GlideDataBinding.bindImage(holder.businessImgView, preferenceManager.getString(Constant.BUSINESS_IMAGE));
            }

            setVisibilityIfEmpty(holder.facebookTv, holder.itemView.findViewById(R.id.facebookLay));
            setVisibilityIfEmpty(holder.instagramTv, holder.itemView.findViewById(R.id.instagramLay));
            setVisibilityIfEmpty(holder.whatsapptv, holder.itemView.findViewById(R.id.whatsappLay));
            setVisibilityIfEmpty(holder.businessNameTv);
            setVisibilityIfEmpty(holder.businessDesTv);
            setVisibilityIfEmpty(holder.userDesTv);
            setVisibilityIfEmpty(holder.businessWebsiteTv);
            setVisibilityIfEmpty(holder.businessAddressTv);
            setVisibilityIfEmpty(holder.businessAddressTv);
            applyGlassBusinessHeader(holder.itemView);
            FramePolishHelper.apply(holder.itemView, position);
        }

        private <T extends View> void setVisibilityIfEmpty(TextView textView, T view) {
            if (textView.getText().toString().trim().isEmpty()) {
                view.setVisibility(View.GONE);
            }
        }

        private void setVisibilityIfEmpty(TextView textView) {
            if (textView.getText().toString().trim().isEmpty()) {
                textView.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView businessNameTv, businessDesTv, businessNumberTv, businessWebsiteTv, businessAddressTv;
            TextView userNameTv, userDesTv, facebookTv, instagramTv, whatsapptv;
            ImageView userImgView, businessImgView;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                businessNameTv = itemView.findViewById(R.id.businessNameTv);
                businessDesTv = itemView.findViewById(R.id.businessDesTv);
                businessNumberTv = itemView.findViewById(R.id.businessNumberTv);
                businessWebsiteTv = itemView.findViewById(R.id.businessWebsiteTv);
                businessAddressTv = itemView.findViewById(R.id.businessAddressTv);

                userNameTv = itemView.findViewById(R.id.userNameTv);
                userDesTv = itemView.findViewById(R.id.userDesTv);
                facebookTv = itemView.findViewById(R.id.facebookTv);
                instagramTv = itemView.findViewById(R.id.instagramTv);
                whatsapptv = itemView.findViewById(R.id.whatsappTv);

                userImgView = itemView.findViewById(R.id.profileImg);
                businessImgView = itemView.findViewById(R.id.businesslogoImg);

            }
        }
    }

    private void applyGlassBusinessHeader(View itemView) {
        if (!com.pt.zyfooai.utils.FrameGlassHelper.isGlassFrame(itemView)) {
            return;
        }
        boolean isBusiness = !preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Personal");
        View glassTopPanel = itemView.findViewById(R.id.glassTopPanel);
        if (glassTopPanel != null) {
            glassTopPanel.setVisibility(isBusiness ? View.VISIBLE : View.GONE);
        }
    }

    public void next(ViewHolder viewHolder, int i, View view) {
        onClickEvent.onClick(view, viewHolder.binding.mainLayOut, list.get(i));
    }

    public void populateUnifiedNativeAdView(NativeAd ad, NativeAdView adView) {
        TextView headlineView = adView.findViewById(R.id.ad_headline);
        headlineView.setText(ad.getHeadline());
        adView.setHeadlineView(headlineView);

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        mediaView.setMediaContent(ad.getMediaContent());

        TextView install = adView.findViewById(R.id.ad_call_to_action);
        install.setText(ad.getCallToAction());

        adView.setCallToActionView(install);

        adView.setMediaView(mediaView);

        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        ((TextView) adView.getHeadlineView()).setText(ad.getHeadline());
        adView.getMediaView().setMediaContent(ad.getMediaContent());

        if (ad.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((TextView) adView.getCallToActionView()).setText(ad.getCallToAction());
        }

        if (ad.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    ad.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (ad.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(ad.getPrice());
        }

        if (ad.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(ad.getStore());
        }

        if (ad.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(ad.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (ad.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(ad.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(ad);


    }

}
