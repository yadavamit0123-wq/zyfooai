package com.pt.zyfooai.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.pt.zyfooai.ui.adapters.MainAdapter.VIEW_TYPE_VIDEO;
import static com.pt.zyfooai.utils.Constant.IS_SUBSCRIBE;
import static com.pt.zyfooai.utils.MyUtils.getAppFolder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.FixedPreloadSizeProvider;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.material.tabs.TabLayout;
import com.pt.zyfooai.AdsUtils.InterstitialsAdsManager;
import com.pt.zyfooai.AdsUtils.RewardAdsManager;
import com.pt.zyfooai.BuildConfig;
import com.pt.zyfooai.R;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.ActivityMainBinding;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.listener.ClickListener;
import com.pt.zyfooai.model.CategoryItem;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.model.SubscriptionModel;
import com.pt.zyfooai.ui.Functions;
import com.pt.zyfooai.ui.adapters.CategorysAdapter;
import com.pt.zyfooai.ui.adapters.MainAdapter;
import com.pt.zyfooai.ui.adapters.StoryAdapter;
import com.pt.zyfooai.ui.adapters.SubscriptionAdapter;
import com.pt.zyfooai.ui.dialog.DownloadProgressDialog;
import com.pt.zyfooai.ui.fragments.SelectBusinessFragment;
import com.pt.zyfooai.ui.fragments.SelectMusicFragment;
import com.pt.zyfooai.utils.AnalyticsHelper;
import com.pt.zyfooai.utils.AppUpdateHelper;
import com.pt.zyfooai.utils.BillingHelper;
import com.pt.zyfooai.utils.ClickDebouncer;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.CreatorAnalyticsHelper;
import com.pt.zyfooai.utils.MyUtils;
import com.pt.zyfooai.utils.NetworkConnectivity;
import com.pt.zyfooai.utils.OfflinePostsCache;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.RemoteConfigHelper;
import com.pt.zyfooai.utils.Util;
import com.pt.zyfooai.viewmodel.HomeViewModel;
import com.makeramen.roundedimageview.RoundedImageView;
import com.onesignal.OneSignal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    StoryAdapter festivalAdapter;
    Activity context;
    private InterstitialsAdsManager interstitialsAdsManager;
    private String selectedLanguage = "";
    PreferenceManager preferenceManager;
    private String selectedCat = "-1";
    int pageCount = 1;
    List<PostItem> dailyPost = new ArrayList<>();
    MainAdapter adapter;
    View currentView;
    RelativeLayout rewateBtn;
    boolean loading = false;
    private Dialog dialogWatermarkOption;
    private Dialog dialogPremium;
    LinearLayoutManager layoutManager;
    List<CategoryItem> categoryItemList = new ArrayList<>();
    NetworkConnectivity networkConnectivity;
    ImageView remove;
    //    ImageView premium;
    public static List<SubscriptionModel> plan_list = new ArrayList<>();
    private final ClickDebouncer clickDebouncer = new ClickDebouncer();
    private boolean scrollListenerAdded = false;
    private boolean preloaderAdded = false;
    private boolean dataObserversRegistered = false;
    private DownloadProgressDialog downloadProgressDialog;
    private HomeViewModel homeViewModel;
    private String pendingDeepLinkPostId;
    private boolean showingOfflineCache;
    private BillingHelper billingHelper;
    private PostItem lastActionPostItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        int i;

        Window window = getWindow();
        window.addFlags(Integer.MIN_VALUE);
        window.clearFlags(67108864);
        window.setStatusBarColor(0);
        int i3 = Build.VERSION.SDK_INT;
        View decorView = window.getDecorView();
        if (i3 >= 26) {
            Log.d("farukh----------->1", "onCreate: "+i3);
            i = 1296;
        } else {
            Log.d("farukh----------->", "onCreate: "+i3);

            i = 1280;
        }

        decorView.setSystemUiVisibility(i);

        if (!BuildConfig.DEBUG) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }

        networkConnectivity = new NetworkConnectivity(this);

        setContentView(binding.getRoot());
        context = this;
        preferenceManager = new PreferenceManager(context);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        binding.shimmerViewContainer.setVisibility(VISIBLE);
        AnalyticsHelper.logScreen(context, "home");
        billingHelper = new BillingHelper(this);
        handleDeepLinkIntent(getIntent());
        registerDataObservers();
        loadCategories();
        getData();
        binding.getRoot().post(() -> AppUpdateHelper.checkForUpdate(this));

        binding.editProfileTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) { // Use getPosition() instead of getId()
                    case 0:
                        setProfileType("Personal");
                        break;
                    case 1:
                        setProfileType("Business");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Business")) {
            binding.editProfileTabLayout.getTabAt(1).select();
        } else {
            binding.editProfileTabLayout.getTabAt(0).select();
        }

        binding.circularImageView.setOnClickListener(view -> {
            if (clickDebouncer.shouldIgnore()) return;
            startActivity(new Intent(this, SettingActivity.class));
        });

        binding.createActionButton.setOnClickListener(view -> {
            if (clickDebouncer.shouldIgnore()) return;
            startActivity(new Intent(this, CreatePostActivity.class));
        });

        setUpRecyclerView();
        ensureAdapter();

        binding.getRoot().post(() -> {
            if (Build.VERSION.SDK_INT >= 33) {
                OneSignal.promptForPushNotifications();
            }
        });

        if (preferenceManager.getString("DataType").equals("Business")) {
//            binding.changeProfileImg.setImageResource(R.drawable.db_personal_img);
            if (preferenceManager.getString(Constant.BUSINESS_IMAGE) != null && !preferenceManager.getString(Constant.BUSINESS_IMAGE).isEmpty()) {
                GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.BUSINESS_IMAGE));
            }

        } else {

//            binding.changeProfileImg.setImageResource(R.drawable.ic_union);
            if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
                GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.USER_IMAGE));
            }

        }

//        binding.changeProfileLay.setOnClickListener(view -> {
//
//            pageCount = 1;
//            loading = false;
//            selectedCat = "-1";
//
//            if (binding.switchProfile.clRoot.getVisibility() == GONE) {
//
//                if (!preferenceManager.getString("DataType").equals("Business")) {
//
//                    preferenceManager.setString("DataType", "Business");
//                    binding.switchProfile.dialogMessageTxt.setText("Switching to Business");
//                    binding.changeProfileImg.setImageResource(R.drawable.db_personal_img);
//                    binding.switchProfile.dialogProfileImg.setImageResource(R.drawable.dialog_business_img);
//
//                    if (preferenceManager.getString(Constant.BUSINESS_IMAGE) != null && !preferenceManager.getString(Constant.BUSINESS_IMAGE).isEmpty()) {
//
//                        GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.BUSINESS_IMAGE));
//                    }
//                    getData();
//                } else {
//
//                    preferenceManager.setString("DataType", "Personal");
//                    binding.switchProfile.dialogMessageTxt.setText("Switching to Personal");
//                    binding.changeProfileImg.setImageResource(R.drawable.ic_union);
//                    binding.switchProfile.dialogProfileImg.setImageResource(R.drawable.dialog_personal_img);
//
//                    if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
//
//                        GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.USER_IMAGE));
//
//                    }
//
//                    getData();
//                }
//
//                binding.switchProfile.clRoot.setVisibility(VISIBLE);
//
//            }
//
//        });

//        binding.premium.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(context, SubscriptionActivity.class));
//            }
//        });

        binding.swipeRefresh.setOnRefreshListener(() -> {
            pageCount = 1;
            loading = false;
            selectedCat = "-1";
            showingOfflineCache = false;
            binding.offlineBanner.setVisibility(GONE);
            binding.shimmerViewContainer.setVisibility(VISIBLE);
            binding.main.setVisibility(GONE);
            dailyPost.clear();

            if (adapter != null) {
                adapter.clearData();
            }
            loadCategories();
            categoryItemList.clear();
            getData();
        });


        Constant.getHomeViewModel(this).getSubscriptionPlan().observe(this, new Observer<List<SubscriptionModel>>() {
            @Override
            public void onChanged(List<SubscriptionModel> subscriptionModels) {
                if (subscriptionModels != null && !subscriptionModels.isEmpty()) {
                    plan_list = subscriptionModels;
                }
            }
        });
    }

    private void setProfileType(String business) {
        pageCount = 1;
        loading = false;
        selectedCat = "-1";
        binding.swipeRefresh.setRefreshing(false);
        binding.shimmerViewContainer.setVisibility(VISIBLE);
        binding.main.setVisibility(GONE);
        dailyPost.clear();
        categoryItemList.clear();
        if (adapter != null) {
            adapter.clearData();
        }

        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.updateProfile(preferenceManager.getString(Constant.USER_ID), "", "", "", "", "", "", business).observe(this, userItem -> {
            if (userItem != null && userItem.status == 200) {
                Functions.saveUserData(context, userItem);
                loadCategories();
                getData();
            } else {
                Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Handler idleHandler = new Handler(Looper.getMainLooper());
    private Runnable idleRunnable;
    private int IDLE_TIME = 10000;

    private void resetIdleTimer() {
        // Hide the animation if it's visible
        hideHandAnimation();
        // Remove any pending callbacks
        idleHandler.removeCallbacks(idleRunnable);
        // Post a new delayed callback
        idleHandler.postDelayed(idleRunnable, IDLE_TIME);
    }

    boolean isVisible = true;

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
        stopAllVideos(binding.allVideo);
        stopMusic(false);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleDeepLinkIntent(intent);
        if (pendingDeepLinkPostId != null && adapter != null && !dailyPost.isEmpty()) {
            scrollToDeepLinkPost();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppUpdateHelper.handleActivityResult(this, requestCode, resultCode);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        AppUpdateHelper.checkForUpdate(this);
        if (preferenceManager.getString("DataType").equals("Business")) {
            if (preferenceManager.getString(Constant.BUSINESS_IMAGE) != null && !preferenceManager.getString(Constant.BUSINESS_IMAGE).isEmpty()) {
                GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.BUSINESS_IMAGE));
            }
        } else {
            if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
                GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.USER_IMAGE));
            }
        }
        if (adapter != null) {
            adapter.onResumeVideo();
        }
        playMusic();
        binding.allVideo.post(() -> playVisibleVideo(binding.allVideo));
    }

    private void showHandAnimation() {
        if (dailyPost.isEmpty() || !isVisible) {
            return;
        }

        if (musicPlayer != null && musicPlayer.isPlaying()) {
            return;
        }

        LinearLayoutManager lm = (LinearLayoutManager) binding.allVideo.getLayoutManager();
        if (lm == null) {
            return;
        }
        int firstVisiblePosition = lm.findFirstCompletelyVisibleItemPosition();
        if (firstVisiblePosition == RecyclerView.NO_POSITION) {
            firstVisiblePosition = lm.findFirstVisibleItemPosition();
        }
        if (firstVisiblePosition == RecyclerView.NO_POSITION
                || firstVisiblePosition < 0
                || firstVisiblePosition >= dailyPost.size()) {
            return;
        }

        PostItem item = dailyPost.get(firstVisiblePosition);
        if (item == null || !item.is_video) {
            binding.handAnimation.setVisibility(VISIBLE);
            binding.handAnimation.playAnimation();
        }

        IDLE_TIME = IDLE_TIME + 10000;
    }

    private void hideHandAnimation() {
        binding.handAnimation.cancelAnimation();
        binding.handAnimation.setVisibility(GONE);
    }

    private void setUpRecyclerView() {
        binding.allVideo.setLayoutManager(layoutManager);
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.allVideo);

        if (!scrollListenerAdded) {
            binding.allVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE
                            || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                        playVisibleVideo(recyclerView);
                    }
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (idleRunnable != null) {
                        resetIdleTimer();
                    }
                    if (dy <= 0 || loading || layoutManager == null) {
                        return;
                    }
                    int visibleCount = layoutManager.getChildCount();
                    int totalCount = layoutManager.getItemCount();
                    int lastVisible = layoutManager.findLastVisibleItemPosition();
                    if (visibleCount + lastVisible >= totalCount - 2) {
                        loading = true;
                        pageCount++;
                        loadDataMore();
                    }
                }
            });
            scrollListenerAdded = true;
        }

        if (!preloaderAdded && adapter != null) {
            RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<>(
                    Glide.with(this),
                    adapter,
                    new FixedPreloadSizeProvider<>(512, 512),
                    8
            );
            binding.allVideo.addOnScrollListener(preloader);
            preloaderAdded = true;
        }
    }

    private void ensureAdapter() {
        if (adapter != null) {
            return;
        }
        adapter = new MainAdapter(context, dailyPost, this::handlePostClick);
        binding.allVideo.setAdapter(adapter);
    }

    private void handlePostClick(View view, View posterew, PostItem postItem) {
        lastActionPostItem = postItem;
        currentView = posterew;
        rewateBtn = currentView.findViewById(R.id.watermarkLayout);
        remove = currentView.findViewById(R.id.removeWatermark);

        if (preferenceManager.getBoolean(IS_SUBSCRIBE)) {
            remove.setVisibility(GONE);
            rewateBtn.setVisibility(GONE);
        }

        if (view.getId() == R.id.watermarkLayout) {
            setupDialogWatermarkOption();
        } else if (view.getId() == R.id.downloadBtn) {
            remove.setVisibility(GONE);
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {
                setupDialogPremium(postItem, postItem, "download");
                return;
            }
            saveImage(GlideDataBinding.viewToBitmap(currentView), postItem, "download");
        } else if (view.getId() == R.id.shareBtn) {
            remove.setVisibility(GONE);
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {
                setupDialogPremium(postItem, postItem, "Share");
                return;
            }
            saveImage(GlideDataBinding.viewToBitmap(currentView), postItem, "Share");
        } else if (view.getId() == R.id.edit_Btn) {
            if (clickDebouncer.shouldIgnore()) return;
            startActivity(new Intent(context, EditProfileActivity.class)
                    .putExtra("imageUrl", postItem.image_url));
        } else if (view.getId() == R.id.musicBtn) {
            musicFrameName = "status_" + postItem.postId + ".mp4";
            new SelectMusicFragment(new ClickListener<PostItem>() {
                @Override
                public void onClick(PostItem data) {
                    playMusic(data.image_url);
                }
            }).show(getSupportFragmentManager(), "");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        idleHandler.removeCallbacks(idleRunnable);
        stopMusic(true);
        if (adapter != null) {
            adapter.releaseSharedPlayer();
        }
        if (downloadProgressDialog != null) {
            downloadProgressDialog.markDestroyed();
        }
        if (billingHelper != null) {
            billingHelper.destroy();
        }
    }

    private void handleDeepLinkIntent(Intent intent) {
        if (intent == null) {
            return;
        }
        if (intent.getBooleanExtra(Constant.INTENT_IS_FROM_NOTIFICATION, false)) {
            pendingDeepLinkPostId = intent.getStringExtra(Constant.INTENT_POST_ID);
            String categoryId = intent.getStringExtra(Constant.INTENT_CATEGORY_ID);
            if (categoryId != null && !categoryId.isEmpty()) {
                selectedCat = categoryId;
            }
        }
    }

    private void scrollToDeepLinkPost() {
        if (pendingDeepLinkPostId == null || adapter == null) {
            return;
        }
        for (int i = 0; i < dailyPost.size(); i++) {
            PostItem item = dailyPost.get(i);
            if (item != null && pendingDeepLinkPostId.equals(item.postId)) {
                final int target = i;
                binding.allVideo.post(() -> {
                    binding.allVideo.scrollToPosition(target);
                    playVisibleVideo(binding.allVideo);
                });
                pendingDeepLinkPostId = null;
                return;
            }
        }
    }

    private void registerDataObservers() {
        if (dataObserversRegistered) {
            return;
        }
        dataObserversRegistered = true;
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        homeViewModel.observeCategories().observe(this, categoryItems -> {
            if (categoryItems == null) {
                return;
            }
            categoryItemList.clear();
            categoryItemList.add(new CategoryItem("-1", "All", R.drawable.logo, false));
            categoryItemList.addAll(categoryItems);

            int businessIndex = Math.min(7, categoryItemList.size());
            categoryItemList.add(businessIndex, new CategoryItem("-3", "My Business", R.drawable.ep_business_name_img, true));

            int politicalIndex = Math.min(businessIndex + 1, categoryItemList.size());
            categoryItemList.add(politicalIndex, new CategoryItem("-4", "Political", R.drawable.flag_regular, true));

            binding.rvCategory.setAdapter(new CategorysAdapter(context, categoryItemList, new AdapterClickListener() {
                @Override
                public void onItemClick(View view, int pos, Object object) {
                    selectedCat = categoryItemList.get(pos).getId();
                    AnalyticsHelper.logCategorySelect(context, selectedCat, categoryItemList.get(pos).getName());
                    CreatorAnalyticsHelper.trackCategoryView(context, selectedCat, categoryItemList.get(pos).getName());
                    if (adapter != null) {
                        adapter.stopAndClearPlayer();
                    }
                    if ("My Business".equals(categoryItemList.get(pos).getName())
                            && preferenceManager.getString(Constant.BUSINESS_ID).equals("0")) {
                        updateBusinessID("business");
                    } else if ("Political".equals(categoryItemList.get(pos).getName())
                            && preferenceManager.getString(Constant.POLITICAL_ID).equals("0")) {
                        updateBusinessID("political");
                    } else {
                        pageCount = 1;
                        loading = false;
                        binding.shimmerViewContainer.setVisibility(VISIBLE);
                        binding.main.setVisibility(GONE);
                        getData();
                    }
                }
            }));
        });

        homeViewModel.observeDailyPosts().observe(this, postItems -> {
            loading = false;
            if (pageCount == 1) {
                handleFirstPagePosts(postItems);
            } else {
                handleMorePosts(postItems);
            }
        });
    }

    private List<PostItem> buildFeedWithAds(List<PostItem> postItems) {
        List<PostItem> feed = new ArrayList<>();
        if (postItems == null) {
            return feed;
        }
        int adInterval = RemoteConfigHelper.getNativeAdInterval(context);
        if (pageCount > 1) {
            adInterval = Math.max(3, adInterval / 2);
        }
        for (int i = 0; i < postItems.size(); i++) {
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && i % adInterval == 0 && i != 0) {
                feed.add(null);
            }
            feed.add(postItems.get(i));
        }
        return feed;
    }

    private void handleFirstPagePosts(List<PostItem> postItems) {
        if (postItems != null && !postItems.isEmpty()) {
            dailyPost.clear();
            dailyPost.addAll(buildFeedWithAds(postItems));
            OfflinePostsCache.save(context, postItems);

            ensureAdapter();
            adapter.replaceData(dailyPost);
            binding.shimmerViewContainer.setVisibility(GONE);
            binding.main.setVisibility(VISIBLE);
            binding.switchProfile.clRoot.setVisibility(GONE);
            binding.noDataLayout.setVisibility(GONE);
            binding.noInternetLayout.setVisibility(GONE);
            binding.swipeRefresh.setRefreshing(false);
            binding.allVideo.post(() -> {
                playVisibleVideo(binding.allVideo);
                scrollToDeepLinkPost();
            });
            if (!preloaderAdded) {
                setUpRecyclerView();
            }
            idleRunnable = () -> showHandAnimation();
        } else {
            binding.shimmerViewContainer.setVisibility(GONE);
            binding.main.setVisibility(VISIBLE);
            binding.switchProfile.clRoot.setVisibility(GONE);
            binding.noDataLayout.setVisibility(VISIBLE);
            binding.swipeRefresh.setRefreshing(false);
        }
    }

    private void handleMorePosts(List<PostItem> postItems) {
        if (postItems == null || postItems.isEmpty()) {
            return;
        }
        List<PostItem> moreItems = buildFeedWithAds(postItems);
        dailyPost.addAll(moreItems);
        if (adapter != null) {
            adapter.setData(dailyPost);
        }
    }

    private void showCachedPosts(List<PostItem> cachedPosts) {
        pageCount = 1;
        showingOfflineCache = true;
        dailyPost.clear();
        dailyPost.addAll(buildFeedWithAds(cachedPosts));
        ensureAdapter();
        adapter.replaceData(dailyPost);
        binding.noInternetLayout.setVisibility(GONE);
        binding.offlineBanner.setVisibility(VISIBLE);
        binding.shimmerViewContainer.setVisibility(GONE);
        binding.main.setVisibility(VISIBLE);
        binding.noDataLayout.setVisibility(GONE);
        binding.swipeRefresh.setRefreshing(false);
        if (!preloaderAdded) {
            setUpRecyclerView();
        }
    }

    //load festival
    private void festival() {

        Constant.getHomeViewModel(this).getFestival().observe(this, featureItems -> {
            if (featureItems != null && featureItems.size() > 0) {

                festivalAdapter = new StoryAdapter(context, item -> {

                    Intent intent = new Intent(context, DailyPostActivity.class);
                    intent.putExtra(Constant.INTENT_FEST_NAME, item.title);
                    intent.putExtra(Constant.INTENT_FEST_ID, item.festivalId);

                    startActivity(intent);
                });
                festivalAdapter.setItemList(featureItems);
                binding.rvStory.setAdapter(festivalAdapter);
                binding.llStory.setVisibility(VISIBLE);


            } else {
                binding.llStory.setVisibility(GONE);
            }
        });

    }


    private void loadCategories() {
        if (homeViewModel == null) {
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        }
        homeViewModel.loadCategories("featured");
    }

    String businessID = "", politicalId = "";

    private void updateBusinessID(String type) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_down, R.anim.slide_down);
        transaction.addToBackStack(null);
        transaction.replace(android.R.id.content, new SelectBusinessFragment(type, model -> {

            businessID = "";
            politicalId = "";
            if (type.equals("business")) {
                businessID = model.id;
            } else {
                politicalId = model.id;
            }

            HomeViewModel homeViewModel = new ViewModelProvider(MainActivity.this).get(HomeViewModel.class);
            homeViewModel.updateBusinessProfile(preferenceManager.getString(Constant.USER_ID), "", "" + politicalId, "" + businessID, "", "", "", "", "", "", "", "", "", "", "", "").observe(MainActivity.this, userItem -> {
                if (userItem != null && userItem.status == 200) {
                    Functions.saveUserData(context, userItem);
                    pageCount = 1;
                    loading = false;
                    selectedCat = "-1";
                    binding.swipeRefresh.setRefreshing(false);
                    binding.shimmerViewContainer.setVisibility(VISIBLE);
                    binding.main.setVisibility(GONE);
                    dailyPost.clear();
                    categoryItemList.clear();
                    if (adapter != null) {
                        adapter.clearData();
                    }
                    loadCategories();
                    getData();
                } else {
                    Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
                }
            });
        })).commit();
    }

    private void getData() {
        stopAllVideos(binding.allVideo);

        selectedLanguage = preferenceManager.getString(Constant.USER_LANGUAGE);
        if (selectedLanguage.equals("-1")) {
            selectedLanguage = "";
        }

        if (!networkConnectivity.isConnected()) {
            List<PostItem> cachedPosts = OfflinePostsCache.load(context);
            if (!cachedPosts.isEmpty()) {
                showCachedPosts(cachedPosts);
            } else {
                binding.noInternetLayout.setVisibility(VISIBLE);
                binding.shimmerViewContainer.setVisibility(GONE);
                binding.main.setVisibility(GONE);
            }
            return;
        }

        binding.noDataLayout.setVisibility(GONE);
        binding.noInternetLayout.setVisibility(GONE);

        if (pageCount == 1) {
            dailyPost.clear();
            ensureAdapter();
            adapter.notifyDataSetChanged();
            binding.shimmerViewContainer.setVisibility(VISIBLE);
            binding.main.setVisibility(GONE);
        }

        if (homeViewModel == null) {
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        }
        homeViewModel.loadDailyPosts(
                pageCount,
                selectedCat,
                selectedLanguage,
                preferenceManager.getString(Constant.BUSINESS_ID),
                preferenceManager.getString(Constant.POLITICAL_ID)
        );
    }


    private void playVisibleVideo(RecyclerView recyclerView) {
        if (adapter == null || dailyPost.isEmpty()) {
            return;
        }

        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (lm == null) {
            return;
        }

        int targetPos = findMostVisibleVideoPosition(lm, recyclerView);
        if (targetPos == RecyclerView.NO_POSITION) {
            return;
        }

        PostItem targetItem = dailyPost.get(targetPos);
        RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(targetPos);
        if (vh instanceof MainAdapter.ViewHolderVideo) {
            MainAdapter.ViewHolderVideo holder = (MainAdapter.ViewHolderVideo) vh;
            if (adapter.currentHolder == holder
                    && targetItem.image_url != null
                    && targetItem.image_url.equals(adapter.currentPlayingVideo)) {
                adapter.onResumeVideo();
                return;
            }
            adapter.updatePlayer(holder, targetItem);
        }
    }

    private int findMostVisibleVideoPosition(LinearLayoutManager lm, RecyclerView recyclerView) {
        int first = lm.findFirstVisibleItemPosition();
        int last = lm.findLastVisibleItemPosition();
        if (first == RecyclerView.NO_POSITION) {
            return RecyclerView.NO_POSITION;
        }

        int targetPos = RecyclerView.NO_POSITION;
        int bestVisibleArea = 0;
        for (int i = first; i <= last; i++) {
            if (i < 0 || i >= dailyPost.size()) {
                continue;
            }
            PostItem item = dailyPost.get(i);
            if (item == null || !item.is_video) {
                continue;
            }
            View child = lm.findViewByPosition(i);
            if (child == null) {
                continue;
            }
            int visibleTop = Math.max(child.getTop(), 0);
            int visibleBottom = Math.min(child.getBottom(), recyclerView.getHeight());
            int visibleArea = Math.max(0, visibleBottom - visibleTop);
            if (visibleArea > bestVisibleArea) {
                bestVisibleArea = visibleArea;
                targetPos = i;
            }
        }
        return targetPos;
    }


    String musicPath = "";
    ExoPlayer musicPlayer;
    private void playMusic(String path) {
        hideHandAnimation();
        musicPath = path;
        if (musicPlayer != null && musicPlayer.isPlaying()) {
            musicPlayer.setPlayWhenReady(false);
            musicPlayer.release();
        }
        TrackSelector trackSelectorDef = new DefaultTrackSelector();
        musicPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelectorDef);

        int appNameStringRes = R.string.app_name;
        String userAgent = com.google.android.exoplayer2.util.Util.getUserAgent(context, context.getString(appNameStringRes));
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

    private void stopMusic(boolean destroy) {
        if (musicPlayer != null) {
            musicPlayer.setPlayWhenReady(false);
            if (destroy) {
                musicPath = "";
                musicPlayer.release();
                musicPlayer = null;
            }
        }
    }

    private void playMusic() {
        if (musicPlayer != null){
            musicPlayer.setPlayWhenReady(true);
        }
    }
    protected void onPause() {
        super.onPause();
        if (adapter != null) {
            adapter.onPauseVideo();
        }

    }


    private void showBitmapDialog(File bitmap, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_bitmap_preview, null);

        ImageView imageView = dialogView.findViewById(R.id.imageView);
        Glide.with(context).load(bitmap).into(imageView);

        builder.setView(dialogView);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void playVisibleVideos(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        if (layoutManager != null) {
            int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();

            for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(i);
                if (holder instanceof MainAdapter.ViewHolder && holder.getItemViewType() == VIEW_TYPE_VIDEO) {
                    MainAdapter.ViewHolder videoHolder = (MainAdapter.ViewHolder) holder;
                    videoHolder.videoLayoutBinding.playerview.getPlayer().setPlayWhenReady(true);
//                    videoHolder.videoLayoutBinding.videoView.start();
                }
            }
        }
    }

    private void stopAllVideos(RecyclerView recyclerView) {
        if (adapter != null) {
            adapter.pauseAndDetachPlayer();
        }
    }

    private DownloadProgressDialog getDownloadProgressDialog() {
        if (downloadProgressDialog == null) {
            downloadProgressDialog = new DownloadProgressDialog(context);
        }
        return downloadProgressDialog;
    }

    private void loadDataMore() {
        if (!networkConnectivity.isConnected()) {
            loading = false;
            return;
        }
        if (homeViewModel == null) {
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        }
        homeViewModel.loadDailyPosts(
                pageCount,
                selectedCat,
                selectedLanguage,
                preferenceManager.getString(Constant.BUSINESS_ID),
                preferenceManager.getString(Constant.POLITICAL_ID)
        );
    }


    ProgressDialog progressDialog;
    private void downloadMp3(String framePath, String musicUrl, String type) {
        getDownloadProgressDialog().show("Downloading");
        getDownloadProgressDialog().updateProgress(5, "Preparing music download...");

        String fileName = musicUrl.substring(musicUrl.lastIndexOf('/') + 1);
        ;
        File cacheDir = context.getExternalCacheDir(); // External cache directory
        if (cacheDir == null) {
            cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
        }

        File finalCacheFile = new File(cacheDir, fileName);
        if (!finalCacheFile.exists()) {
            AndroidNetworking.download(musicUrl, cacheDir.getPath(), fileName)
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            if (totalBytes > 0) {
                                int percent = (int) ((bytesDownloaded * 60) / totalBytes);
                                runOnUiThread(() -> getDownloadProgressDialog()
                                        .updateProgress(percent, "Downloading music..."));
                            }
                        }
                    })
                    .startDownload(new DownloadListener() {
                        public void onDownloadComplete() {
                            getDownloadProgressDialog().updateProgress(65, "Applying music...");
                            applyMp3OnFrame(framePath, finalCacheFile.getAbsolutePath(), type);
                        }

                        public void onError(ANError aNError) {
                            getDownloadProgressDialog().dismiss();
                            Toast.makeText(context, "" + aNError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            getDownloadProgressDialog().updateProgress(65, "Applying music...");
            applyMp3OnFrame(framePath, finalCacheFile.getAbsolutePath(), type);
        }
    }

    private InterstitialsAdsManager getInterstitialsAdsManager() {
        if (interstitialsAdsManager == null) {
            interstitialsAdsManager = new InterstitialsAdsManager(context);
        }
        return interstitialsAdsManager;
    }

    private void saveImage(Bitmap bitmap, PostItem postItem, String type) {
        if (bitmap == null) {
            Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
            return;
        }

        getInterstitialsAdsManager().showInterstitialAd(() -> {
            if ("Share".equals(type)) {
                CreatorAnalyticsHelper.trackShare(context, postItem.postId, selectedCat);
            } else {
                AnalyticsHelper.logDownload(context, postItem.is_video ? "video" : "image");
                CreatorAnalyticsHelper.trackDownload(context, postItem.postId, selectedCat);
            }
            stopMusic(true);
            stopAllVideos(binding.allVideo);
            if (adapter != null) {
                adapter.onPauseVideo();
            }

            if (postItem.is_video) {

                File directory = new File(getAppFolder(context) + "/ZyfooAi");
                String imagefilename = System.currentTimeMillis() + ".png";
                try {
                    if (!directory.exists()) {
                        directory.mkdir();
                        directory.mkdir();
                    }
                    File file2 = new File(directory.getAbsolutePath() + "/" + imagefilename);
                    FileOutputStream fileOutputStream = new FileOutputStream(file2);

                    // Ensure bitmap has ARGB_8888 config for transparency
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(createBitmap);

                    // Remove or change this line to ensure transparency
                    // canvas.drawColor(-1); // REMOVE THIS LINE

                    // Draw the original bitmap onto the new one
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, null);

                    // Save as PNG to preserve transparency
                    createBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    createBitmap.recycle();

                    fileOutputStream.flush();
                    fileOutputStream.close();

//                    showBitmapDialog(file2, context);

                    downloadVideo(file2.getAbsolutePath(), postItem.image_url, type);


                } catch (Exception e) {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                String fileName = System.currentTimeMillis() + ".png";
                String filePath = Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                        + File.separator + fileName;

                if (!musicPath.isEmpty()){
                    File cacheDir = context.getExternalCacheDir(); // External cache directory
                    if (cacheDir == null) {
                        cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
                    }

                    filePath = cacheDir + File.separator + fileName;
                }

                boolean success = false;
                if (!new File(filePath).exists()) {
                    try {
                        File file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES
                        ), "/" + getResources().getString(R.string.app_name));

                        if (!file.exists()) {
                            if (!file.mkdirs()) {
                                Toast.makeText(context, getResources().getString(R.string.create_dir_err), Toast.LENGTH_LONG).show();
                                success = false;
                            }
                        }

                        File file2 = new File(filePath);
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file2);
                            Bitmap createBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                                    bitmap.getHeight(), bitmap.getConfig());
                            Canvas canvas = new Canvas(createBitmap);
                            canvas.drawColor(-1);
                            canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                            createBitmap.compress(Bitmap.CompressFormat.PNG,
                                    100, fileOutputStream);
                            createBitmap.recycle();
                            fileOutputStream.flush();
                            fileOutputStream.close();

                            if (musicPath.isEmpty()){
                                MediaScannerConnection.scanFile(context, new String[]{file2.getAbsolutePath()},
                                        (String[]) null, (str, uri) -> {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("-> uri=");
                                            sb.append(uri);
                                            sb.append("-> FILE=");
                                            sb.append(file2.getAbsolutePath());
                                            Uri muri = Uri.fromFile(file2);
                                        });
                            }

                            success = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            success = false;
                        }

                    } catch (Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (success) {
                        if (!musicPath.isEmpty()){
                            downloadMp3(filePath, musicPath, type);
                        }else {
                            if (type.equals("download")) {
                                getDownloadProgressDialog().show("Saving Image");
                                getDownloadProgressDialog().updateProgress(100, "Image saved successfully");
                                getDownloadProgressDialog().dismiss();
                                Util.showToast(context, getString(R.string.image_saved));
                                Intent intent = new Intent(context, ShareImageActivity.class);
                                intent.putExtra("uri", filePath);
                                startActivity(intent);
                            } else {
                                shareFileImageUri(getImageContentUri(new File(filePath)), type);
                            }
                        }
                    } else {
                        Util.showToast(context, getString(R.string.error));
                    }

                }
            }

        });
    }

    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    private static final String CHANNEL_ID = "video_process";

    private void createNotificationChannel() {
        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Video Processing",
                    NotificationManager.IMPORTANCE_LOW
            );
            notificationManager.createNotificationChannel(channel);
        }

        builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_download)
                        .setContentTitle("Video Processing")
                        .setContentText("Downloading video...")
                        .setOnlyAlertOnce(true)
                        .setOngoing(true);

        notificationManager.notify(101, builder.build());
    }

    private void updateNotification(String text) {
        if (builder == null || notificationManager == null) {
            return;
        }
        builder.setContentText(text);
        notificationManager.notify(101, builder.build());
    }
    String musicFrameName = System.currentTimeMillis() + ".mp4";
    private void applyMp3OnFrame(String framePath, String musicPath, String type) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String musicFramePath = Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                        + File.separator + musicFrameName;

                Log.e("applyMp3__", "Music Path: " + musicPath);
                Log.e("applyMp3__", "Frame Path: " + framePath);
                Log.e("applyMp3__", "OUT Path: " + musicFramePath);

                File frameFile = new File(framePath);
                File musicFile = new File(musicPath);

                if (!frameFile.exists()) {
                    Log.e("applyMp3__", "Image file not found: " + framePath);
                }
                if (!musicFile.exists()) {
                    Log.e("applyMp3__", "Music file not found: " + musicPath);
                }

                if (!new File(musicFramePath).exists()) {

                    String outputDir = musicFramePath;

                    // Now create the video with the image and audio
                    String[] cmd = {
                            "-loop", "1",
                            "-framerate", "1",
                            "-i", framePath,
                            "-i", musicPath,
                            "-vf", "scale=trunc(iw/2)*2:trunc(ih/2)*2", // Resize to even height
                            "-c:v", "libx264",
                            "-tune", "stillimage",
                            "-c:a", "aac",
                            "-b:a", "192k",
                            "-shortest",
                            "-pix_fmt", "yuv420p",
                            "-y",
                            outputDir
                    };


                    getDownloadProgressDialog().showIndeterminate("Creating video with music...");
                    FFmpeg.executeAsync(cmd, new ExecuteCallback() {
                        @Override
                        public void apply(long executionId, int returnCode) {
                            getDownloadProgressDialog().dismiss();
                            if (returnCode == 1) {
                                FFmpeg.cancel(executionId);
                                Toast.makeText(MainActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                            }
                            if (returnCode == 0) {
                                MediaScannerConnection.scanFile(context, new String[]{musicFramePath},
                                        (String[]) null, (str, uri) -> {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append("-> uri=");
                                            sb.append(uri);
                                            sb.append("-> FILE=");
                                            sb.append(musicFramePath);
                                            Uri muri = Uri.parse(musicFramePath);
                                        });

                                if (type.equals("download")) {
                                    Util.showToast(context, getString(R.string.video_saved));
                                    Intent intent = new Intent(context, ShareImageActivity.class);
                                    intent.putExtra("uri", musicFramePath);
                                    startActivity(intent);
                                } else {
                                    shareFileImageUri(getImageContentUri(new File(musicFramePath)), type);
                                }
                            } else if (returnCode == 255) {
                                Log.e("applyMp3__", "Command execution cancelled by user.");
                            } else {
                                String str = String.format("Command execution failed with rc=%d and the output below.", Arrays.copyOf(new Object[]{Integer.valueOf(returnCode)}, 1));
                                Log.i("applyMp3__", str);
                            }
                        }
                    });

                } else {
                    getDownloadProgressDialog().dismiss();
                    if (type.equals("download")) {
                        Util.showToast(context, getString(R.string.video_saved));
                        Intent intent = new Intent(context, ShareImageActivity.class);
                        intent.putExtra("uri", musicFramePath);
                        startActivity(intent);
                    } else {
                        shareFileImageUri(getImageContentUri(new File(musicFramePath)), type);
                    }

                }

            }
        });
    }



    private void downloadVideo(String framePath, String videoUrl, String type) {
        createNotificationChannel();
        getDownloadProgressDialog().show("Downloading Video");
        getDownloadProgressDialog().updateProgress(2, "Starting video download...");
        String fileName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1);
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }

        File finalCacheFile = new File(cacheDir, fileName);
        if (!finalCacheFile.exists()) {
            AndroidNetworking.download(videoUrl, cacheDir.getPath(), fileName)
                    .build()
                    .setDownloadProgressListener(new DownloadProgressListener() {
                        @Override
                        public void onProgress(long bytesDownloaded, long totalBytes) {
                            if (totalBytes > 0) {
                                int percent = (int) ((bytesDownloaded * 70) / totalBytes);
                                runOnUiThread(() -> getDownloadProgressDialog()
                                        .updateProgress(Math.max(percent, 3), "Downloading video..."));
                            }
                        }
                    })
                    .startDownload(new DownloadListener() {
                        public void onDownloadComplete() {
                            getDownloadProgressDialog().updateProgress(72, "Processing video...");
                            applyFrameOnVideo(finalCacheFile.getAbsolutePath(), framePath, type);
                        }

                        public void onError(ANError aNError) {
                            getDownloadProgressDialog().dismiss();
                            Toast.makeText(context, "" + aNError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            getDownloadProgressDialog().updateProgress(72, "Processing video...");
            applyFrameOnVideo(finalCacheFile.getAbsolutePath(), framePath, type);
        }
    }

    private void applyFrameOnVideo(String videoath, String framePath, String type) {
        getDownloadProgressDialog().showIndeterminate("Processing video with frame...");
        updateNotification("Making video...");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String outputDir = Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                        + File.separator + System.currentTimeMillis() + ".mp4";

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoath);
                String widthMeta = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                String heightMeta = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                try {
                    retriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (widthMeta == null || heightMeta == null) {
                    getDownloadProgressDialog().dismiss();
                    Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }

                int width = Integer.parseInt(widthMeta);
                int height = Integer.parseInt(heightMeta);
                if (width <= 0 || height <= 0) {
                    getDownloadProgressDialog().dismiss();
                    Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap b = BitmapFactory.decodeFile(framePath);
                if (b == null) {
                    getDownloadProgressDialog().dismiss();
                    Toast.makeText(context, getString(R.string.error), Toast.LENGTH_SHORT).show();
                    return;
                }

                Bitmap out = Bitmap.createScaledBitmap(b, width, height, false);

                File cacheDir = context.getExternalCacheDir(); // External cache directory
                if (cacheDir == null) {
                    cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
                }

                File file = new File(cacheDir.getAbsolutePath(), System.currentTimeMillis()+".png");
                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(file);
                    out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    b.recycle();
                    out.recycle();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FFmpeg.executeAsync(new String[]{"-i", videoath, "-i", file.getAbsolutePath(),
                        "-filter_complex",
                        "overlay", "-r", "150",
                        "-vb", "20M",
                        "-y", outputDir}, new ExecuteCallback() {
                    @Override
                    public void apply(long executionId, int returnCode) {
                        getDownloadProgressDialog().dismiss();
                        if (returnCode == 1) {
                            FFmpeg.cancel(executionId);
                            Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                        if (returnCode == 0) {
                            FFmpeg.cancel(executionId);
                            MediaScannerConnection.scanFile(context, new String[]{outputDir},
                                    (String[]) null, (str, uri) -> {
                                    });

                            if (type.equals("download")) {
                                Toast.makeText(context, getString(R.string.video_saved), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(context, ShareImageActivity.class);
                                intent.putExtra("uri", outputDir);
                                startActivity(intent);
                                updateNotification("Download complete");
                            } else {
                                shareFileImageUri(getImageContentUri(new File(outputDir)), type);
                            }
                        } else if (returnCode == 255) {
                            Log.e("finalProcess__", "Command execution cancelled by user.");
                        } else {
                            String str = String.format("Command execution failed with rc=%d and the output below.",
                                    Arrays.copyOf(new Object[]{Integer.valueOf(returnCode)}, 1));
                            Log.i("finalProcess__", str);
                        }
                    }
                });
            }
        });
    }



    public Uri getImageContentUri(File imageFile) {
        return Uri.parse(imageFile.getAbsolutePath());
    }

    public void shareFileImageUri(Uri path, String shareTo) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        switch (shareTo) {
            case "whtsapp":
                shareIntent.setPackage("com.whatsapp");
                break;
            case "fb":
                shareIntent.setPackage("com.facebook.katana");
                break;
            case "insta":
                shareIntent.setPackage("com.instagram.android");
                break;
            case "twter":
                shareIntent.setPackage("com.twitter.android");
                break;
        }

        String mimeType = "image/*"; // Default MIME type
        if (path.getPath().endsWith(".mp4")) {
            mimeType = "video/*"; // Set video MIME type if it's an MP4 file
        }
        shareIntent.setDataAndType(path, mimeType);

        shareIntent.putExtra(Intent.EXTRA_STREAM, path);

        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + getPackageName());

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_txt) + getPackageName()));
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            // If there are fragments in the back stack, pop the last one
            fragmentManager.popBackStack();
        } else {
            // Show the exit confirmation dialog
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure you want to exit?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }


    public void setupDialogWatermarkOption() {


        dialogWatermarkOption = new Dialog(context);
        dialogWatermarkOption.requestWindowFeature(1);
        dialogWatermarkOption.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogWatermarkOption.setContentView(R.layout.dialog_layout_watermark_option);
        dialogWatermarkOption.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        ImageView close = dialogWatermarkOption.findViewById(R.id.cancel);
        LinearLayout subscription = dialogWatermarkOption.findViewById(R.id.cv_no);

        ProgressBar progressBar = dialogWatermarkOption.findViewById(R.id.pb_loading);
        close.setOnClickListener(view -> dialogWatermarkOption.dismiss());
        subscription.setOnClickListener(view -> startActivity(new Intent(context, SubscriptionActivity.class)));

        LinearLayout iapPurchase = dialogWatermarkOption.findViewById(R.id.cv_iap);
        if (BillingHelper.isWatermarkPurchased(context)) {
            iapPurchase.setVisibility(GONE);
        } else {
            iapPurchase.setOnClickListener(view -> billingHelper.purchaseWatermarkRemove(() -> {
                rewateBtn.setVisibility(GONE);
                Toast.makeText(context, "Watermark removed permanently", Toast.LENGTH_SHORT).show();
                dialogWatermarkOption.dismiss();
            }));
        }

        LinearLayout withouWatermark = dialogWatermarkOption.findViewById(R.id.cv_yes);

        withouWatermark.setOnClickListener(view -> {

            progressBar.setVisibility(VISIBLE);

            new RewardAdsManager(context, new RewardAdsManager.OnAdLoaded() {
                @Override
                public void onAdClosed() {

                    Toast.makeText(context, "Ad not loaded, Try Again", Toast.LENGTH_SHORT).show();
                    rewateBtn.setVisibility(VISIBLE);
                    dialogWatermarkOption.dismiss();

                }

                @Override
                public void onAdWatched() {

                    rewateBtn.setVisibility(GONE);
                    Toast.makeText(context, "Congratulations, Remove Watermark", Toast.LENGTH_SHORT).show();
                    dialogWatermarkOption.dismiss();

                }
            });


        });

        dialogWatermarkOption.show();

    }

    public static RelativeLayout currentFrameImageView;
    public static RoundedImageView currentFrameBusinessImageView;
    Bitmap freeBit, premiumBit;

    public void setupDialogPremium(PostItem postItem, PostItem item, String type) {

        premiumBit = GlideDataBinding.viewToBitmap(currentView);
        if (currentFrameImageView != null) {
            currentFrameImageView.setVisibility(GONE);
        }
        if (currentFrameBusinessImageView != null) {
            currentFrameBusinessImageView.setVisibility(GONE);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                freeBit = GlideDataBinding.viewToBitmap(currentView);

                if (currentFrameImageView != null) {
                    currentFrameImageView.setVisibility(VISIBLE);
                }

                if (currentFrameBusinessImageView != null) {
                    currentFrameBusinessImageView.setVisibility(VISIBLE);
                }

                Dialog dialogPremium = new Dialog(context);
                dialogPremium.setContentView(R.layout.dialog_download_with_img);

                Window window = dialogPremium.getWindow();
                if (window != null) {
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                }

                ImageView premiumImg = dialogPremium.findViewById(R.id.img_premium);
                premiumImg.setImageBitmap(premiumBit);
                ImageView freeImg = dialogPremium.findViewById(R.id.img_free);
                freeImg.setImageBitmap(freeBit);

                dialogPremium.findViewById(R.id.closeBtn).setOnClickListener(view -> dialogPremium.dismiss());

                dialogPremium.findViewById(R.id.downloadBtn).setOnClickListener(view -> {
                    dialogPremium.dismiss();
                    saveImage(freeBit, postItem, "download");
                });

                dialogPremium.findViewById(R.id.adsWatch).setOnClickListener(view -> {
                    RewardAdsManager rewardAdsManager =
                            new RewardAdsManager(context, new RewardAdsManager.OnAdLoaded() {

                                @Override
                                public void onAdClosed() {
                                    // user closed ad or failed
                                    dialogPremium.dismiss();
                                }

                                @Override
                                public void onAdWatched() {
                                    // ✅ user watched full ad
                                    dialogPremium.dismiss();

                                    saveImage(
                                            GlideDataBinding.viewToBitmap(currentView),
                                            postItem,
                                            "download"
                                    );
                                }
                            });

                    rewardAdsManager.loadRewardAd();
                });

                dialogPremium.findViewById(R.id.shareBtn).setOnClickListener(view -> {
                    dialogPremium.dismiss();
                    saveImage(freeBit, postItem, "Share");
                });

                RecyclerView recyclerView = dialogPremium.findViewById(R.id.recycler);
                recyclerView.setAdapter(new SubscriptionAdapter(context, plan_list, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {

                        SubscriptionModel model = (SubscriptionModel) object;
                        startActivity(new Intent(context, SubscriptionActivity.class).putExtra("model", model));

                    }
                }));

                dialogPremium.show();
            }
        }, 500);


//        ImageView close = dialogPremium.findViewById(R.id.cancel);
//        LinearLayout subscription = dialogPremium.findViewById(R.id.cv_no);
//        ProgressBar progressBar = dialogPremium.findViewById(R.id.pb_loading);
//        close.setOnClickListener(view -> dialogPremium.dismiss());
//        subscription.setOnClickListener(view -> context.startActivity(new Intent(context, SubscriptionActivity.class)));
//
//        LinearLayout withouWatermark = dialogPremium.findViewById(R.id.cv_yes);
//        withouWatermark.setOnClickListener(view -> {
//
//            progressBar.setVisibility(VISIBLE);
//
//            new RewardAdsManager(context, new RewardAdsManager.OnAdLoaded() {
//                @Override
//                public void onAdClosed() {
//                    Toast.makeText(context, "Ad not loaded, Try Again", Toast.LENGTH_SHORT).show();
//                    dialogPremium.dismiss();
//
//                }
//
//                @Override
//                public void onAdWatched() {
//                    Toast.makeText(context, "Congratulations, Unlock Your Post", Toast.LENGTH_SHORT).show();
//                    dialogPremium.dismiss();
//
//                    if (type.equals("edit")) {
//
//                        startActivity(new Intent(context, EditProfileActivity.class).putExtra("imageUrl", postItem.image_url));
//                    } else {
//                        remove.setVisibility(GONE);
//                        premium.setVisibility(GONE);
//                        saveImage(GlideDataBinding.viewToBitmap(currentView), type);
//
//                    }
//                }
//
//            });
//
//
//        });

    }

}