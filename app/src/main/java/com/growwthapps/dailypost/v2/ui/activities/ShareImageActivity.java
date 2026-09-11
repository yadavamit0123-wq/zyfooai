package com.growwthapps.dailypost.v2.ui.activities;

import static com.growwthapps.dailypost.v2.utils.MyUtils.buttonClick;
import static com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_WHEN_PLAYING;
import static com.growwthapps.dailypost.v2.utils.MyUtils.topIconBar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.facebook.shimmer.BuildConfig;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.growwthapps.dailypost.v2.AdsUtils.AdsUtils;
import com.growwthapps.dailypost.v2.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.R;

import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;


public class ShareImageActivity extends AppCompatActivity implements View.OnClickListener {

    public ImageView imageView;
    public File pictureFile;
    PreferenceManager preferenceManager;
    private Activity context;
    private String path = null;
    private InterstitialsAdsManager interstitialsAdsManager;
    ImageView poster_iv, iv_image;
    ImageView close;
    FrameLayout status_bar;
    FrameLayout fl_adplaceholder;
    String file_name;
    PlayerView playerview;
    ExoPlayer exoplayer;
    RelativeLayout rlFullView;
    ShimmerFrameLayout shimmer;
    private ReviewManager reviewManager;


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_saved_post);

        context = this;
        topIconBar(this);

        interstitialsAdsManager = new InterstitialsAdsManager(context);
        preferenceManager = new PreferenceManager(context);

        initView();

        new AdsUtils(this).loadNativeAd(this, fl_adplaceholder, shimmer);


        path = getIntent().getStringExtra("uri");

        Log.d("dataexported", "dataexported 0 :" + path);


        if (path != null && path.endsWith(".mp4")) {
            file_name = System.currentTimeMillis() + ".mp4";
            playerview.setVisibility(View.VISIBLE);
            poster_iv.setVisibility(View.GONE);
            initializePlayer();

            pictureFile = new File(path);

        } else {
            file_name = System.currentTimeMillis() + ".jpg";
            playerview.setVisibility(View.GONE);
            poster_iv.setVisibility(View.VISIBLE);
            try {

                pictureFile = new File(path);

                Glide.with(this).load(path).placeholder(R.drawable.placeholder).into(poster_iv);
                Glide.with(this).load(path).placeholder(R.drawable.placeholder).into(iv_image);


                Log.d("dataexported", "dataexported 1 :" + path);

                File root = new File(Environment.getExternalStorageDirectory() + File.separator
                        + Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name) + File.separator + file_name);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        new AdsUtils(context).loadNativeAd(context, findViewById(R.id.fl_adplaceholder),shimmer);

        reviewManager = ReviewManagerFactory.create(this);
        showRateApp();

        initUI();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlFullView.setVisibility(View.GONE);
            }
        });


    }

    private void initView() {
        ImageView btnBack = findViewById(R.id.back);
        ImageView btnMoreApp = findViewById(R.id.more);
        ImageView btnShareFacebook = findViewById(R.id.facebook);
        ImageView btnShareInstagram = findViewById(R.id.instagram);
        ImageView btnShareWhatsapp = findViewById(R.id.whatsapp);
        ImageView ivHome = findViewById(R.id.iv_home);
        ImageView btnFull = findViewById(R.id.iv_full_view);
        btnFull.setOnClickListener(this);

        btnBack.setOnClickListener(this);
        btnMoreApp.setOnClickListener(this);
        btnShareFacebook.setOnClickListener(this);
        btnShareInstagram.setOnClickListener(this);
        btnShareWhatsapp.setOnClickListener(this);
        poster_iv = findViewById(R.id.iv_save_image);
        iv_image = findViewById(R.id.iv_image);
        playerview = findViewById(R.id.videoPlayer);
        status_bar = findViewById(R.id.status_bar);
        rlFullView = findViewById(R.id.rl_full_view);
        shimmer = findViewById(R.id.shimmer);
        close = findViewById(R.id.iv_close);
        fl_adplaceholder = findViewById(R.id.fl_adplaceholder);


        ivHome.setOnClickListener(v -> {

            startActivity(new Intent(context, MainActivity.class));
            finish();
        });


    }


    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Getting the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown.
                });
            }
        });
    }


    public void initUI() {
        this.imageView = findViewById(R.id.iv_save_image);
        this.imageView.setVisibility(View.VISIBLE);

    }

    @Override
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);

    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back) {
            finish();
        } else {

            if (id == R.id.facebook) {
                interstitialsAdsManager.showInterstitialAd(() -> shareToFacebook(pictureFile.getPath()));
                view.startAnimation(buttonClick);

            } else if (id == R.id.instagram) {
                interstitialsAdsManager.showInterstitialAd(() -> shareToInstagram(pictureFile.getPath()));
                view.startAnimation(buttonClick);

            }else if (id == R.id.whatsapp) {
                view.startAnimation(buttonClick);
                interstitialsAdsManager.showInterstitialAd(() -> sendToWhatsaApp(pictureFile.getPath()));

            }else if (id == R.id.more) {
                view.startAnimation(buttonClick);
                interstitialsAdsManager.showInterstitialAd(() -> shareMoreButton());
            }else if (id == R.id.iv_full_view) {
                view.startAnimation(buttonClick);
                rlFullView.setVisibility(View.VISIBLE);

            }


        }
    }

    private void shareMoreButton() {
        try {

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            Uri uriForFile = FileProvider.getUriForFile(getApplicationContext(), "com.growwthapps.dailypost.v2" + ".provider", new File(path));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(Intent.EXTRA_STREAM, uriForFile);
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + getPackageName());
            startActivity(Intent.createChooser(intent, "Share Your Flyer!"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    public void sendToWhatsaApp(String str) {
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.whatsapp") != null) {
            try {
                Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str));
                Intent intent = new Intent();
                intent.setAction("android.intent.action.SEND");
                intent.setType("image/*");
                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + getPackageName());
                intent.setPackage("com.whatsapp");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("WrongConstant")
    public void shareToFacebook(String str) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setPackage("com.facebook.katana");
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.facebook.katana") != null) {
            try {
                intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str)));
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_txt) + getPackageName());
                intent.addFlags(1);
                startActivity(Intent.createChooser(intent, "Share Gif."));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Facebook not installed", Toast.LENGTH_SHORT).show();
        }
    }


    public void shareToInstagram(String str) {
        if (getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.instagram.android") != null) {
            try {
                Uri uriForFile = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", new File(str));
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("image/*");
                new File(str);
                intent.putExtra("android.intent.extra.STREAM", uriForFile);
                intent.setPackage("com.instagram.android");
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Instagram not installed", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (exoplayer != null) {
            exoplayer.setPlayWhenReady(false);
        }
    }

    private void initializePlayer() {
        playerview.setUseController(true);
        playerview.setControllerHideOnTouch(true);
        playerview.setShowBuffering(true);
        TrackSelector trackSelectorDef = new DefaultTrackSelector();
        exoplayer = ExoPlayerFactory.newSimpleInstance(this, trackSelectorDef);


        int appNameStringRes = R.string.app_name;
        String userAgent = Util.getUserAgent(this, this.getString(appNameStringRes));
        DefaultDataSourceFactory defdataSourceFactory = new DefaultDataSourceFactory(this, userAgent);
        Uri uriOfContentUrl = Uri.parse(path);
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(defdataSourceFactory).createMediaSource(uriOfContentUrl);  // creating a media source

        exoplayer.prepare(mediaSource);
        exoplayer.setPlayWhenReady(true); // start loading video and play it at the moment a chunk of it is available offline
        playerview.setPlayer(exoplayer); // attach surface to the view
        playerview.setShowBuffering(SHOW_BUFFERING_WHEN_PLAYING);
        exoplayer.addListener(new Player.EventListener() {

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Player.EventListener.super.onLoadingChanged(isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Player.EventListener.super.onPlayerStateChanged(playWhenReady, playbackState);
                switch (playbackState) {
                    case ExoPlayer.STATE_ENDED:

                        break;
                }
            }
        });

        playerview.setOnTouchListener(new View.OnTouchListener() {

            private final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    super.onSingleTapUp(e);

                    if (!exoplayer.getPlayWhenReady()) {
                        exoplayer.setPlayWhenReady(true);
                        playerview.hideController();


                    } else {

                        new Handler(getMainLooper()).postDelayed(() -> exoplayer.setPlayWhenReady(false), 100);
                    }


                    return true;
                }


                @Override
                public boolean onDoubleTap(MotionEvent e) {

                    if (!exoplayer.getPlayWhenReady()) {
                        exoplayer.setPlayWhenReady(true);
                    }

                    return super.onDoubleTap(e);

                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
        });
    }


}
