package com.pt.zyfooai.ui.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.pt.zyfooai.ui.activities.MainActivity.plan_list;
import static com.pt.zyfooai.utils.Constant.INTENT_FEST_ID;
import static com.pt.zyfooai.utils.Constant.INTENT_FEST_NAME;
import static com.pt.zyfooai.utils.Constant.IS_SUBSCRIBE;
import static com.pt.zyfooai.utils.Constant.NATIVE_AD_COUNT;
import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.AdsUtils.AdsUtils;
import com.pt.zyfooai.AdsUtils.InterstitialsAdsManager;
import com.pt.zyfooai.AdsUtils.RewardAdsManager;
import com.pt.zyfooai.R;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.ActivityPersonalPostBinding;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.model.SubscriptionModel;
import com.pt.zyfooai.ui.adapters.MainAdapter;
import com.pt.zyfooai.ui.adapters.SubscriptionAdapter;
import com.pt.zyfooai.utils.ClickDebouncer;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.MyUtils;
import com.pt.zyfooai.utils.PaginationListener;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.Util;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class DailyPostActivity extends AppCompatActivity {

    ActivityPersonalPostBinding binding;
    Activity context;
    int pageCount = 1;
    GridLayoutManager layoutManager;
    boolean loading = false;
    MainAdapter adapter;
    List<PostItem> dailyPost = new ArrayList<>();
    PreferenceManager preferenceManager;
    private String selectedLanguage = "";
    private String selectedCat = "-1";
    private InterstitialsAdsManager interstitialsAdsManager;
    public static Dialog dialogPremium;
    private Dialog dialogWatermarkOption;

    View currentView;
    RelativeLayout rewateBtn;
    ImageView remove, premium;
    private final ClickDebouncer clickDebouncer = new ClickDebouncer();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPersonalPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        topIconBar(this);

        new AdsUtils(context).showBannerAds(context);

        preferenceManager = new PreferenceManager(context);

        shimmer(View.VISIBLE);

        String name = getIntent().getStringExtra(INTENT_FEST_NAME);
        selectedCat = getIntent().getStringExtra(INTENT_FEST_ID);

        binding.toolbar.rlToolbar.setVisibility(View.VISIBLE);
        layoutManager = new GridLayoutManager(context, 1);
        binding.toolbar.back.setOnClickListener(view -> finish());
        binding.toolbar.toolName.setText(name);

        binding.swipeRefresh.setOnRefreshListener(() -> {
            pageCount = 1;
            loading = false;

            binding.swipeRefresh.setRefreshing(false);
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.allVideos.setVisibility(View.VISIBLE);
            binding.noDataLayout.setVisibility(View.GONE);

            getData();

        });

        setUpRecyclerView();
        ensureAdapter();
        getData();
    }

    private InterstitialsAdsManager getInterstitialsAdsManager() {
        if (interstitialsAdsManager == null) {
            interstitialsAdsManager = new InterstitialsAdsManager(context);
        }
        return interstitialsAdsManager;
    }

    private void ensureAdapter() {
        if (adapter != null) {
            return;
        }
        adapter = new MainAdapter(context, dailyPost, this::handlePostClick);
        binding.allVideos.setAdapter(adapter);
    }

    private void handlePostClick(View view, View posterew, PostItem postItem) {
        currentView = posterew;
        rewateBtn = currentView.findViewById(R.id.watermarkLayout);
        remove = currentView.findViewById(R.id.removeWatermark);
        premium = currentView.findViewById(R.id.iv_premium);

        if (preferenceManager.getBoolean(IS_SUBSCRIBE)) {
            remove.setVisibility(View.GONE);
            premium.setVisibility(View.GONE);
            rewateBtn.setVisibility(View.GONE);
        }

        if (view.getId() == R.id.watermarkLayout) {
            setupDialogWatermarkOption();
        } else if (view.getId() == R.id.downloadBtn) {
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {
                setupDialogPremium(postItem, "download");
                return;
            }
            saveImage(GlideDataBinding.viewToBitmap(currentView), "download");
        } else if (view.getId() == R.id.shareBtn) {
            if (!preferenceManager.getBoolean(IS_SUBSCRIBE) && postItem.is_premium) {
                setupDialogPremium(postItem, "Share");
                return;
            }
            saveImage(GlideDataBinding.viewToBitmap(currentView), "Share");
        } else if (view.getId() == R.id.edit_Btn) {
            if (clickDebouncer.shouldIgnore()) return;
            startActivity(new Intent(context, EditProfileActivity.class).putExtra("imageUrl", postItem.image_url));
        }
    }

    private void setUpRecyclerView() {


        binding.allVideos.setLayoutManager(layoutManager);

        binding.allVideos.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return loading;
            }

            @Override
            public void loadMoreItems() {
                loading = true;
                pageCount = pageCount + 1;

                new Handler().postDelayed(() -> loadDataMore(), 100);


            }
        });


    }

    private void getData() {
        binding.noDataLayout.setVisibility(View.GONE);

        selectedLanguage = preferenceManager.getString(Constant.USER_LANGUAGE);
        if (selectedLanguage.equals("-1")) {
            selectedLanguage = "";
        }

        dailyPost.clear();
        ensureAdapter();
        adapter.notifyDataSetChanged();

        Constant.getHomeViewModel(this).getFestivalPost(pageCount, selectedCat, selectedLanguage).observe(this, postItems -> {
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

                ensureAdapter();
                adapter.replaceData(dailyPost);
                binding.allVideos.setVisibility(View.VISIBLE);
                binding.noDataLayout.setVisibility(View.GONE);
                binding.shimmerViewContainer.setVisibility(View.GONE);
            } else {
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.allVideos.setVisibility(View.VISIBLE);
                binding.noDataLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadDataMore() {

        Constant.getHomeViewModel(this).getFestivalPost(pageCount, selectedCat, selectedLanguage).observe(this, postItems -> {

            if (postItems != null) {

                int i = 0;

                while (i < postItems.size()) {

                    if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

                        if (i % preferenceManager.getInt(NATIVE_AD_COUNT) == 0 && i != 0) {

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

    private void shimmer(int gone) {

        binding.shimmerViewContainer.setVisibility(gone);
    }

    private void saveImage(Bitmap bitmap, String type) {

        remove.setVisibility(View.GONE);
        premium.setVisibility(View.GONE);

        getInterstitialsAdsManager().showInterstitialAd(new InterstitialsAdsManager.onAdClosedListener() {
            @Override
            public void onAdClosed() {

                String fileName = System.currentTimeMillis() + ".png";
                String filePath = Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name)
                        + File.separator + fileName;


                boolean success = false;

                if (!new File(filePath).exists()) {
                    try {
                        File file = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES
                        ), "/" + getResources().getString(R.string.app_name));
                        if (!file.exists()) {
                            if (!file.mkdirs()) {
                                Toast.makeText(context,
                                        getResources().getString(R.string.create_dir_err),
                                        Toast.LENGTH_LONG).show();
                                success = false;
                            }
                        }
                        File file2 = new File(file.getAbsolutePath() + "/" + fileName);

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


                            MediaScannerConnection.scanFile(context, new String[]{file2.getAbsolutePath()},
                                    (String[]) null, (str, uri) -> {
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("-> uri=");
                                        sb.append(uri);
                                        sb.append("-> FILE=");
                                        sb.append(file2.getAbsolutePath());
                                        Uri muri = Uri.fromFile(file2);
                                    });
                            success = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                            success = false;
                        }

                    } catch (Exception e) {
                        Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (success) {
                        if (type.equals("download")) {


                            Util.showToast(context, getString(R.string.image_saved));
                            Intent intent = new Intent(context, ShareImageActivity.class);
                            intent.putExtra("uri", filePath);
                            startActivity(intent);
                        } else {
                            shareFileImageUri(getImageContentUri(new File(filePath)), type);
                        }
                    } else {
                        Util.showToast(context, getString(R.string.error));
                    }

                }
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
        shareIntent.setDataAndType(path, "image/*");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);

        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + getPackageName());

        startActivity(Intent.createChooser(shareIntent, getString(R.string.share_txt) + getPackageName()));
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

            progressBar.setVisibility(View.VISIBLE);

            new RewardAdsManager(context, new RewardAdsManager.OnAdLoaded() {
                @Override
                public void onAdClosed() {

                    Toast.makeText(context, "Ad not loaded, Try Again", Toast.LENGTH_SHORT).show();
                    rewateBtn.setVisibility(View.VISIBLE);
                    dialogWatermarkOption.dismiss();

                }

                @Override
                public void onAdWatched() {

                    rewateBtn.setVisibility(View.GONE);
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

    public void setupDialogPremium(PostItem postItem, String type) {

        premiumBit = GlideDataBinding.viewToBitmap(currentView);
        if (currentFrameImageView != null) {
            currentFrameImageView.setVisibility(GONE);
        }
        if (currentFrameBusinessImageView != null) {
            currentFrameBusinessImageView.setVisibility(GONE);
        }
        new Handler().postDelayed(() -> {
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
                saveImage(freeBit, "download");
            });

            dialogPremium.findViewById(R.id.shareBtn).setOnClickListener(view -> {
                dialogPremium.dismiss();
                saveImage(freeBit, "Share");
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
        }, 500);
    }
}