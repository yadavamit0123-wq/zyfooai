package com.pt.zyfooai.ui.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.pt.zyfooai.R;
import com.pt.zyfooai.respository.ReferralRepository;
import com.pt.zyfooai.ui.adapters.ReferralFaqAdapter;
import com.pt.zyfooai.ui.adapters.ReferralListAdapter;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.ReferEarnPrefs;
import com.pt.zyfooai.utils.VideoCacheHelper;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ReferralProgramActivity extends AppCompatActivity {

    private PreferenceManager prefs;
    private ReferralRepository repository;
    private String serverCode = "";
    private String userId;

    private NestedScrollView referTabScroll;
    private View myReferralsContainer;
    private TextView tvHeadline;
    private TextView tvSubtext;
    private TextView tvCode;
    private TextView tvWallet;
    private ImageView ivBanner;
    private PlayerView playerBanner;
    private ProgressBar bannerLoader;
    private ProgressBar progressReferrals;
    private View emptyReferrals;
    private View errorReferrals;
    private RecyclerView rvReferrals;
    private RecyclerView rvFaq;

    private ReferralListAdapter listAdapter;
    private ReferralFaqAdapter faqAdapter;

    private ExoPlayer bannerPlayer;
    private String currentBannerVideoUrl;
    private boolean referTabVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_program);

        prefs = new PreferenceManager(this);
        repository = new ReferralRepository();
        userId = prefs.getString(Constant.USER_ID);

        findViewById(R.id.backImg).setOnClickListener(v -> onBackPressed());

        referTabScroll = findViewById(R.id.referTabScroll);
        myReferralsContainer = findViewById(R.id.myReferralsContainer);
        tvHeadline = findViewById(R.id.tvReferHeadline);
        tvSubtext = findViewById(R.id.tvReferSubtext);
        tvCode = findViewById(R.id.tvReferralCode);
        tvWallet = findViewById(R.id.tvWalletBalance);
        ivBanner = findViewById(R.id.ivReferralBanner);
        playerBanner = findViewById(R.id.playerReferralBanner);
        bannerLoader = findViewById(R.id.bannerLoader);
        progressReferrals = findViewById(R.id.progressReferrals);
        emptyReferrals = findViewById(R.id.emptyReferrals);
        errorReferrals = findViewById(R.id.errorReferrals);
        rvReferrals = findViewById(R.id.rvReferrals);
        rvFaq = findViewById(R.id.rvFaq);

        listAdapter = new ReferralListAdapter();
        rvReferrals.setLayoutManager(new LinearLayoutManager(this));
        rvReferrals.setAdapter(listAdapter);

        faqAdapter = new ReferralFaqAdapter();
        rvFaq.setLayoutManager(new LinearLayoutManager(this));
        rvFaq.setAdapter(faqAdapter);

        bindConfigTexts();
        // Cached banner until fresh /api/referral/me returns
        applyBannerMedia(
                prefs.getString(Constant.REFER_BANNER_TYPE),
                prefs.getString(Constant.REFER_BANNER_URL),
                prefs.getString(Constant.REFER_BANNER_POSTER_URL)
        );

        findViewById(R.id.btnCopyCode).setOnClickListener(v -> copyCode());
        findViewById(R.id.btnWhatsAppShare).setOnClickListener(v -> shareWhatsApp());
        findViewById(R.id.btnSystemShare).setOnClickListener(v -> shareSystem());
        findViewById(R.id.btnRetryReferrals).setOnClickListener(v -> loadReferrals());

        TabLayout tabs = findViewById(R.id.referralTabs);
        tabs.addTab(tabs.newTab().setText(R.string.referral_tab_refer));
        tabs.addTab(tabs.newTab().setText(R.string.referral_tab_my));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                referTabVisible = tab.getPosition() == 0;
                referTabScroll.setVisibility(referTabVisible ? View.VISIBLE : View.GONE);
                myReferralsContainer.setVisibility(referTabVisible ? View.GONE : View.VISIBLE);
                if (referTabVisible) {
                    resumeBannerVideo();
                } else {
                    pauseBannerVideo();
                    loadReferrals();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        loadMe();
        loadFaq();
    }

    private void bindConfigTexts() {
        String referrer = prefs.getString(Constant.REFER_REFERRER_AMOUNT);
        String signup = prefs.getString(Constant.REFER_SIGNUP_BONUS);
        if (referrer == null || referrer.isEmpty()) {
            referrer = "—";
        }
        if (signup == null || signup.isEmpty()) {
            signup = "—";
        }
        tvHeadline.setText(getString(R.string.referral_headline, referrer));
        String appName = getString(R.string.app_name);
        tvSubtext.setText(getString(R.string.referral_subtext, appName, referrer, signup));

        String cached = prefs.getString(Constant.REFER_SERVER_CODE);
        if (cached != null && !cached.isEmpty()) {
            serverCode = cached;
            tvCode.setText(serverCode);
        } else {
            tvCode.setText("…");
        }

        String balance = prefs.getString(Constant.REFER_WALLET_BALANCE);
        if (balance != null && !balance.isEmpty()) {
            tvWallet.setVisibility(View.VISIBLE);
            tvWallet.setText(getString(R.string.referral_wallet_label, balance));
        }
    }

    private void loadMe() {
        if (userId == null || userId.isEmpty()) {
            showPlaceholderBanner();
            return;
        }
        repository.getMe(userId).observe(this, me -> {
            if (me == null) {
                // keep cached / placeholder
                return;
            }
            ReferEarnPrefs.saveFromMe(this, me);
            if (me.referralCode != null && !me.referralCode.isEmpty()) {
                serverCode = me.referralCode;
                tvCode.setText(serverCode);
            }
            bindConfigTexts();
            if (me.walletBalance != null && !me.walletBalance.isEmpty()) {
                tvWallet.setVisibility(View.VISIBLE);
                tvWallet.setText(getString(R.string.referral_wallet_label, me.walletBalance));
            }
            applyBannerMedia(me.bannerType, me.bannerUrl, me.bannerPosterUrl);
        });
    }

    /**
     * banner_type: image | video | none. Empty URL / none → local placeholder (no crash).
     */
    private void applyBannerMedia(String type, String url, String posterUrl) {
        String t = type == null ? "none" : type.trim().toLowerCase();
        String mediaUrl = url == null ? "" : url.trim();

        if (mediaUrl.isEmpty() || "none".equals(t)) {
            showPlaceholderBanner();
            return;
        }

        if ("video".equals(t)) {
            showVideoBanner(mediaUrl, posterUrl);
            return;
        }

        // default: image (and unknown types with URL)
        showImageBanner(mediaUrl);
    }

    private void showPlaceholderBanner() {
        releaseBannerPlayer();
        playerBanner.setVisibility(View.GONE);
        bannerLoader.setVisibility(View.GONE);
        ivBanner.setVisibility(View.VISIBLE);
        ivBanner.setImageResource(R.drawable.referral_earn_banner);
    }

    private void showImageBanner(String url) {
        releaseBannerPlayer();
        playerBanner.setVisibility(View.GONE);
        bannerLoader.setVisibility(View.VISIBLE);
        ivBanner.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.referral_earn_banner)
                .error(R.drawable.referral_earn_banner)
                .into(ivBanner);
        bannerLoader.setVisibility(View.GONE);
    }

    private void showVideoBanner(String url, String posterUrl) {
        ivBanner.setVisibility(View.VISIBLE);
        playerBanner.setVisibility(View.VISIBLE);
        bannerLoader.setVisibility(View.VISIBLE);

        if (posterUrl != null && !posterUrl.trim().isEmpty()) {
            Glide.with(this)
                    .load(posterUrl.trim())
                    .centerCrop()
                    .placeholder(R.drawable.referral_earn_banner)
                    .into(ivBanner);
        } else {
            ivBanner.setImageResource(R.drawable.referral_earn_banner);
        }

        if (bannerPlayer == null) {
            bannerPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
            bannerPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            bannerPlayer.setVolume(0f);
            bannerPlayer.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == ExoPlayer.STATE_READY) {
                        bannerLoader.setVisibility(View.GONE);
                        ivBanner.setVisibility(View.GONE);
                    } else if (playbackState == ExoPlayer.STATE_BUFFERING) {
                        bannerLoader.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onPlayerError(com.google.android.exoplayer2.ExoPlaybackException error) {
                    showPlaceholderBanner();
                }
            });
            playerBanner.setUseController(false);
            playerBanner.setPlayer(bannerPlayer);
        }

        if (!url.equals(currentBannerVideoUrl)) {
            currentBannerVideoUrl = url;
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                    VideoCacheHelper.buildCacheDataSourceFactory(this))
                    .createMediaSource(Uri.parse(url));
            bannerPlayer.stop();
            bannerPlayer.prepare(mediaSource);
        }
        bannerPlayer.setPlayWhenReady(referTabVisible);
    }

    private void pauseBannerVideo() {
        if (bannerPlayer != null) {
            bannerPlayer.setPlayWhenReady(false);
        }
    }

    private void resumeBannerVideo() {
        if (bannerPlayer != null && currentBannerVideoUrl != null) {
            bannerPlayer.setPlayWhenReady(true);
        }
    }

    private void releaseBannerPlayer() {
        if (bannerPlayer != null) {
            playerBanner.setPlayer(null);
            bannerPlayer.release();
            bannerPlayer = null;
        }
        currentBannerVideoUrl = null;
    }

    private void loadFaq() {
        repository.getFaq().observe(this, response -> {
            if (response != null && response.data != null) {
                faqAdapter.submit(response.data);
            }
        });
    }

    private void loadReferrals() {
        if (userId == null || userId.isEmpty()) {
            return;
        }
        progressReferrals.setVisibility(View.VISIBLE);
        emptyReferrals.setVisibility(View.GONE);
        errorReferrals.setVisibility(View.GONE);
        rvReferrals.setVisibility(View.GONE);

        repository.getList(userId, 1).observe(this, response -> {
            progressReferrals.setVisibility(View.GONE);
            if (response == null) {
                errorReferrals.setVisibility(View.VISIBLE);
                return;
            }
            if (response.data == null || response.data.isEmpty()) {
                emptyReferrals.setVisibility(View.VISIBLE);
                listAdapter.submit(null);
                return;
            }
            rvReferrals.setVisibility(View.VISIBLE);
            listAdapter.submit(response.data);
        });
    }

    private void copyCode() {
        if (serverCode == null || serverCode.isEmpty()) {
            Toast.makeText(this, R.string.referral_load_error, Toast.LENGTH_SHORT).show();
            return;
        }
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText("referral_code", serverCode));
        }
        View root = findViewById(android.R.id.content);
        Snackbar.make(root, R.string.referral_code_copied, Snackbar.LENGTH_SHORT).show();
    }

    private void shareWhatsApp() {
        String text = ReferEarnPrefs.buildShareMessage(this, serverCode);
        try {
            String url = "https://wa.me/?text=" + URLEncoder.encode(text, "UTF-8");
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (UnsupportedEncodingException | android.content.ActivityNotFoundException e) {
            shareSystem();
        }
    }

    private void shareSystem() {
        String text = ReferEarnPrefs.buildShareMessage(this, serverCode);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(Intent.createChooser(intent, getString(R.string.referral_share)));
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseBannerVideo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (referTabVisible) {
            resumeBannerVideo();
        }
    }

    @Override
    protected void onDestroy() {
        releaseBannerPlayer();
        super.onDestroy();
    }
}
