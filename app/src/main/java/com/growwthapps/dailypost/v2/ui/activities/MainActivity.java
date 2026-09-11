package com.growwthapps.dailypost.v2.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.growwthapps.dailypost.v2.ui.adapters.MainAdapter.VIEW_TYPE_VIDEO;
import static com.growwthapps.dailypost.v2.utils.Constant.IS_SUBSCRIBE;
import static com.growwthapps.dailypost.v2.utils.MyUtils.getAppFolder;

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
import com.growwthapps.dailypost.v2.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.AdsUtils.RewardAdsManager;
import com.growwthapps.dailypost.v2.BuildConfig;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.ActivityMainBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.listener.ClickListener;
import com.growwthapps.dailypost.v2.model.CategoryItem;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;
import com.growwthapps.dailypost.v2.ui.Functions;
import com.growwthapps.dailypost.v2.ui.adapters.CategorysAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.MainAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.StoryAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.SubscriptionAdapter;
import com.growwthapps.dailypost.v2.ui.fragments.SelectBusinessFragment;
import com.growwthapps.dailypost.v2.ui.fragments.SelectMusicFragment;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.NetworkConnectivity;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.utils.Util;
import com.growwthapps.dailypost.v2.viewmodel.HomeViewModel;
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

        if (Build.VERSION.SDK_INT >= 33) {
            OneSignal.promptForPushNotifications();
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
        interstitialsAdsManager = new InterstitialsAdsManager(context);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);

        binding.shimmerViewContainer.setVisibility(VISIBLE);
//        festival();
        loadCategories();

        getData();

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
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        });

        binding.createActionButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, CreatePostActivity.class);
            startActivity(intent);
        });

        setUpRecyclerView();

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
            binding.swipeRefresh.setRefreshing(false);
            binding.shimmerViewContainer.setVisibility(VISIBLE);
            binding.main.setVisibility(GONE);
            dailyPost.clear();

            if (adapter != null) {
                adapter.clearData();
            }
//            if (festivalAdapter != null) {
//                festivalAdapter.clearData();
//            }
//            festival();
            loadCategories();
            categoryItemList.clear();

            new Handler().postDelayed(() -> {
                getData();
            }, 2000);

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
                new Handler().postDelayed(() -> {
                    loadCategories();
                    getData();
                }, 100);
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
    protected void onResume() {
        super.onResume();
        isVisible = true;
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
    }

    private void showHandAnimation() {
        if (dailyPost.isEmpty() || !isVisible) {
            return;
        }

        if (musicPlayer != null && musicPlayer.isPlaying()){
            return;
        }

        LinearLayoutManager layoutManager = (LinearLayoutManager) binding.allVideo.getLayoutManager();
        int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
        Log.d("farukh------>firstVisiblePosition", "showHandAnimation: "+firstVisiblePosition);
        Log.d("farukh------>firstVisiblePosition", "showHandAnimation: "+dailyPost.get(firstVisiblePosition));

        if (dailyPost.get(firstVisiblePosition) == null || !dailyPost.get(firstVisiblePosition).is_video) {
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        idleHandler.removeCallbacks(idleRunnable);
        stopMusic(true);
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


    //load categories
    private void loadCategories() {


        Constant.getHomeViewModel(this).getCategories("featured").observe(this, categoryItems -> {

            if (categoryItems != null) {
                categoryItemList.add(new CategoryItem("-1", "All", R.drawable.logo, false));

                categoryItemList.addAll(categoryItems);

//                categoryItemList.add(3, new CategoryItem("-2", "Today Special", R.drawable.logo, false));

                categoryItemList.add(7, new CategoryItem("-3", "My Business", R.drawable.ep_business_name_img, true));

                categoryItemList.add(8, new CategoryItem("-4", "Political", R.drawable.flag_regular, true));

                binding.rvCategory.setAdapter(new CategorysAdapter(context, categoryItemList, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        selectedCat = categoryItemList.get(pos).getId();
                        if(adapter!=null){
                            adapter.releasePlayer(adapter.currentHolder);
                        }
                        if (categoryItemList.get(pos).getName() == "My Business" && preferenceManager.getString(Constant.BUSINESS_ID).equals("0")) {
                            updateBusinessID("business");
                        } else if (categoryItemList.get(pos).getName() == "Political" && preferenceManager.getString(Constant.POLITICAL_ID).equals("0")) {
                            updateBusinessID("political");
                        } else {
                            pageCount = 1;
                            getData();
                        }
                    }
                }));
            }
        });
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
                    new Handler().postDelayed(() -> {
                        loadCategories();
                        getData();
                    }, 100);
                } else {
                    Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
                }
            });
        })).commit();
    }

    //load Post by category
    private void getData() {

        stopAllVideos(binding.allVideo);

        if (!networkConnectivity.isConnected()) {
            binding.noInternetLayout.setVisibility(VISIBLE);
            binding.shimmerViewContainer.setVisibility(GONE);
            binding.main.setVisibility(GONE);
            return;
        }

        binding.noDataLayout.setVisibility(GONE);
        selectedLanguage = preferenceManager.getString(Constant.USER_LANGUAGE);
        if (selectedLanguage.equals("-1")) {
            selectedLanguage = "";
        }

        Log.d("selectedLanguage", "getData: " + selectedLanguage + " " + selectedCat + " " + pageCount);
        dailyPost.clear();

        if (adapter != null) {
            adapter.clearData();
            adapter.notifyDataSetChanged();
        }
        Log.d("IS_SUBSCRIBE", "getData: " + preferenceManager.getBoolean(IS_SUBSCRIBE));

        Log.d("PROFILE_IDS", "BUSINESS: " + preferenceManager.getString(Constant.BUSINESS_ID)+" POLITICAL: "+preferenceManager.getString(Constant.POLITICAL_ID));

        Constant.getHomeViewModel(this).getDailyPosts(pageCount, selectedCat, selectedLanguage, preferenceManager.getString(Constant.BUSINESS_ID),preferenceManager.getString(Constant.POLITICAL_ID)).observe(this, postItems -> {

            if (postItems != null && postItems.size() > 0) {
                int i = 0;
                while (i < postItems.size()) {
                    if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

                        if (i % 6 == 0 && i != 0) {

                            dailyPost.add(null);
                        }
                    }
                    dailyPost.add(postItems.get(i));
                    i++;
                }

                MyUtils.showResponse(dailyPost);

                adapter = new MainAdapter(context, dailyPost, (view, posterew, postItem) -> {
                    currentView = posterew;
                    rewateBtn = currentView.findViewById(R.id.watermarkLayout);
                    remove = currentView.findViewById(R.id.removeWatermark);

//                    premium = currentView.findViewById(R.id.iv_premium);

                    if (preferenceManager.getBoolean(IS_SUBSCRIBE)) {
                        remove.setVisibility(GONE);
//                        premium.setVisibility(GONE);
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
//                        premium.setVisibility(GONE);
                        saveImage(GlideDataBinding.viewToBitmap(currentView), postItem, "download");

                    } else if (view.getId() == R.id.shareBtn) {
                        remove.setVisibility(GONE);
//                        setupDialogPremium(postItem, postItem, "Share");
//                        return;
                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {

                            setupDialogPremium(postItem, postItem, "Share");
                            return;
                        }
//                        premium.setVisibility(GONE);
                        saveImage(GlideDataBinding.viewToBitmap(currentView), postItem, "Share");

                    } else if (view.getId() == R.id.edit_Btn) {

//                        if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {
//
//                            setupDialogPremium(postItem, "edit");
//
//                            return;
//                        }

                        startActivity(new Intent(context, EditProfileActivity.class)
                                .putExtra("imageUrl", postItem.image_url));

                        Log.d("imageUrl", "getData: " + postItem.image_url);

                    } else if (view.getId() == R.id.musicBtn) {
                        musicFrameName = "status_"+postItem.postId + ".mp4";
                        new SelectMusicFragment(new ClickListener<PostItem>() {
                            @Override
                            public void onClick(PostItem data) {
                                playMusic(data.image_url);
                            }
                        }).show(getSupportFragmentManager(),"");
                    }

                });
                adapter.setData(dailyPost);
                binding.allVideo.setAdapter(adapter);
                binding.shimmerViewContainer.setVisibility(GONE);
                binding.main.setVisibility(VISIBLE);
                binding.switchProfile.clRoot.setVisibility(GONE);
                binding.noDataLayout.setVisibility(GONE);
                binding.allVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                            playVisibleVideo(recyclerView);
                        }
                    }
                });
//                RecyclerViewPreloader<String> preloader = new RecyclerViewPreloader<>(Glide.with(this), adapter, new FixedPreloadSizeProvider<>(100, 100), 5);
//
//                binding.allVideo.addOnScrollListener(preloader);


//                try {
//                    RecyclerView.ViewHolder holder = binding.allVideo.findViewHolderForAdapterPosition(0);
//                    if (holder instanceof MainAdapter.ViewHolder && holder.getItemViewType() == VIEW_TYPE_VIDEO) {
//                        MainAdapter.ViewHolder videoHolder = (MainAdapter.ViewHolder) holder;
//                        videoHolder.videoLayoutBinding.playerview.getPlayer().setPlayWhenReady(true);
//                    }
//                } catch (Exception e) {
//                    throw new RuntimeException(e);
//                }


//                binding.allVideo.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                    private final RecyclerViewPreloader<String> preloader =
//                            new RecyclerViewPreloader<>(Glide.with(context), adapter, new FixedPreloadSizeProvider<>(100, 100), 5);
//
//                    @Override
//                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                        super.onScrollStateChanged(recyclerView, newState);
//                        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                            playVisibleVideos(recyclerView);
//                        } else {
//                            stopMusic(true);
//                            stopAllVideos(recyclerView);
//                        }
//                    }
//
//                    @Override
//                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                        super.onScrolled(recyclerView, dx, dy);
//
//                        preloader.onScrolled(recyclerView, dx, dy);
//
//                        // Call resetIdleTimer
//                        resetIdleTimer();
//
//                        //Pagination
//                        int childCount = layoutManager.getChildCount();
//                        int itemCount = layoutManager.getItemCount();
//                        int findFirstVisibleItemPosition = 0;
//
//                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
//                        findFirstVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
//
//                        if (!loading && (childCount + findFirstVisibleItemPosition) >= itemCount) {
//
//                            loading = true;
//                            pageCount = pageCount + 1;
//                            //   binding.progreee.setVisibility(View.VISIBLE);
//                            new Handler().postDelayed(() -> loadDataMore(), 100);
//
//                        }
//
//                    }
//                });

                idleRunnable = () -> showHandAnimation();
            } else {

                binding.shimmerViewContainer.setVisibility(GONE);
                binding.main.setVisibility(VISIBLE);
                binding.switchProfile.clRoot.setVisibility(GONE);
                binding.noDataLayout.setVisibility(VISIBLE);

            }

        });
    }


    private void playVisibleVideo(RecyclerView recyclerView) {

        LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
        int pos = lm.findFirstCompletelyVisibleItemPosition();

        if (pos == RecyclerView.NO_POSITION) return;

        RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(pos);

        if (vh instanceof MainAdapter.ViewHolderVideo) {

            MainAdapter.ViewHolderVideo holder = (MainAdapter.ViewHolderVideo) vh;

            if (adapter.currentHolder == holder) return;

            // Stop previous
            if (adapter.currentHolder != null && adapter.currentHolder.exoplayer != null) {
                adapter.currentHolder.exoplayer.setPlayWhenReady(false);
                adapter.currentHolder.exoplayer.stop();
                adapter.currentHolder.videoLayoutBinding.playerview.setPlayer(null);
            }

            // Start new
            adapter.currentHolder = holder;
            adapter.updatePlayer(holder, dailyPost.get(pos));
        }
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
        if (musicPlayer != null){
            musicPlayer.setPlayWhenReady(false);
            if (destroy){
                musicPath = "";
                musicPlayer.release();
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
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
            if (holder instanceof MainAdapter.ViewHolder && holder.getItemViewType() == VIEW_TYPE_VIDEO) {
                MainAdapter.ViewHolder videoHolder = (MainAdapter.ViewHolder) holder;
                videoHolder.videoLayoutBinding.playerview.getPlayer().setPlayWhenReady(false);
//                videoHolder.videoLayoutBinding.videoView.stopPlayback();
            }
        }
    }

    private void loadDataMore() {

        Constant.getHomeViewModel(this).getDailyPosts(pageCount, selectedCat, selectedLanguage, preferenceManager.getString(Constant.BUSINESS_ID), preferenceManager.getString(Constant.POLITICAL_ID)).observe(this, postItems -> {

            if (postItems != null) {

                int i = 0;

                while (i < postItems.size()) {

                    if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

                        if (i % 3 == 0 && i != 0) {

                            dailyPost.add(null);
                        }


                    }
                    dailyPost.add(postItems.get(i));
                    i++;
                }
                if (adapter != null) {
                    adapter.setData(dailyPost);
                }


                loading = false;

            }


        });
    }


    ProgressDialog progressDialog;
    private void downloadMp3(String framePath, String musicUrl, String type) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Downloading Music File...");
        progressDialog.show();

        String fileName = musicUrl.substring(musicUrl.lastIndexOf('/') + 1);
        ;
        File cacheDir = context.getExternalCacheDir(); // External cache directory
        if (cacheDir == null) {
            cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
        }

        File finalCacheFile = new File(cacheDir, fileName);
        if (!finalCacheFile.exists()) {
            AndroidNetworking.download(musicUrl, cacheDir.getPath(), fileName).build().startDownload(new DownloadListener() {
                public void onDownloadComplete() {
                    applyMp3OnFrame(framePath, finalCacheFile.getAbsolutePath(), type);
                }

                public void onError(ANError aNError) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "" + aNError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            applyMp3OnFrame(framePath, finalCacheFile.getAbsolutePath(), type);
        }
    }

    private void saveImage(Bitmap bitmap, PostItem postItem, String type) {

        interstitialsAdsManager.showInterstitialAd(() -> {

            if (postItem.is_video) {

                stopAllVideos(binding.allVideo);

                File directory = new File(getAppFolder(context) + "/FameBanao");
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

        builder =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_download)
                        .setContentTitle("Video Processing")
                        .setContentText("Downloading video...")
                        .setOnlyAlertOnce(true)
                        .setOngoing(true);

        notificationManager.notify(101, builder.build());
    }

    private void  updateNotification(String text){
        builder.setContentText(text);
        notificationManager.notify(101, builder.build());

    };
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


                    progressDialog.setMessage("Applying Music..");
                    FFmpeg.executeAsync(cmd, new ExecuteCallback() {
                        @Override
                        public void apply(long executionId, int returnCode) {
                            progressDialog.dismiss();
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

                    progressDialog.dismiss();

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
//        createNotificationChannel();
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage("Downloading Video File...");
//        progressDialog.show();
        createNotificationChannel();
        String fileName = videoUrl.substring(videoUrl.lastIndexOf('/') + 1);
        ;
        File cacheDir = context.getExternalCacheDir(); // External cache directory
        if (cacheDir == null) {
            cacheDir = context.getCacheDir(); // Fallback to internal cache directory if external is not available
        }

        File finalCacheFile = new File(cacheDir, fileName);
        if (!finalCacheFile.exists()) {
            AndroidNetworking.download(videoUrl, cacheDir.getPath(), fileName).build().startDownload(new DownloadListener() {
                public void onDownloadComplete() {
                    applyFrameOnVideo(finalCacheFile.getAbsolutePath(),framePath,type);
                }

                public void onError(ANError aNError) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "" + aNError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            applyFrameOnVideo(finalCacheFile.getAbsolutePath(),framePath, type);
        }
    }

    private void applyFrameOnVideo(String videoath, String framePath, String type) {
//        progressDialog.setMessage("Making video...");
        updateNotification("Making video...");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                String outputDir = Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                        + File.separator + System.currentTimeMillis() + ".mp4";

                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(videoath);
                int width = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                int height = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                try {
                    retriever.release();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap b = BitmapFactory.decodeFile(framePath);
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
//                        progressDialog.dismiss();
                        if (returnCode == 1) {
                            FFmpeg.cancel(executionId);
                            Toast.makeText(context, "Try Again", Toast.LENGTH_SHORT).show();
                        }
                        if (returnCode == 0) {
                            FFmpeg.cancel(executionId);
                            MediaScannerConnection.scanFile(context, new String[]{outputDir},
                                    (String[]) null, (str, uri) -> {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("-> uri=");
                                        sb.append(uri);
                                        sb.append("-> FILE=");
                                        sb.append(outputDir);
                                        Uri muri = Uri.parse(outputDir);
                                    });

                            if (type.equals("download")) {
                                Intent intent = new Intent(context, ShareImageActivity.class);
                                intent.putExtra("uri", outputDir);
                                startActivity(intent);
                                updateNotification("Download Complate....");
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