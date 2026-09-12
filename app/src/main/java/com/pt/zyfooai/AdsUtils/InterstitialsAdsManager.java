package com.pt.zyfooai.AdsUtils;

import static com.pt.zyfooai.utils.Constant.ADS_ENABLE;
import static com.pt.zyfooai.utils.Constant.INTERSTITIAL_AD_ENABLED;
import static com.pt.zyfooai.utils.Constant.IS_SUBSCRIBE;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.ump.ConsentInformation;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;

public class InterstitialsAdsManager {

    private static final String PREF_CLICK_COUNT = "click_count";
    private static int MAX_CLICK_COUNT = 1;
    PreferenceManager preferenceManager;
    private Activity mContext;
    private InterstitialAd mInterstitialAd = null;
    private int mClickCount;

    public InterstitialsAdsManager(Activity context) {
        mContext = context;
        preferenceManager = new PreferenceManager(context);
        MAX_CLICK_COUNT = preferenceManager.getInt(Constant.INTERSTITIAL_AD_CLICK);
        mClickCount = getClickCount();

        loadInterstitialAds();


    }

    private void loadInterstitialAds() {

        if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {


            if (preferenceManager.getBoolean(ADS_ENABLE) && preferenceManager.getBoolean(INTERSTITIAL_AD_ENABLED)) {


                if (mInterstitialAd == null) {

                    AdRequest.Builder builder = new AdRequest.Builder();

                    int request = GDPRChecker.getStatus();
                    if (request == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                        // load non Personalized ads
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                    } // else do nothing , it will load PERSONALIZED ads

                    InterstitialAd.load(mContext, "ca-app-pub-3940256099942544/1033173712", builder.build(), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                            mInterstitialAd = interstitialAd;
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                            mInterstitialAd = null;

                            new Handler(Looper.getMainLooper()).postDelayed(InterstitialsAdsManager.this::loadInterstitialAds, 30000);

                        }
                    });

                }
            }
        }

    }

    private int getClickCount() {
        return preferenceManager.getInt(PREF_CLICK_COUNT, 0);
    }

    private void incrementClickCount() {
        mClickCount++;
        saveClickCount(mClickCount);
    }

    private void resetClickCount() {
        mClickCount = 0;
        saveClickCount(mClickCount);
    }

    private void saveClickCount(int clickCount) {

        preferenceManager.setInt(PREF_CLICK_COUNT, clickCount);

    }

    public void showInterstitialAd(onAdClosedListener onAdClosedListener) {


        if (mInterstitialAd == null) {
            onAdClosedListener.onAdClosed();

            loadInterstitialAds();

            return;
        }

        if (mClickCount >= MAX_CLICK_COUNT || mClickCount == 0) {

            mInterstitialAd.show(mContext);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent();

                    mInterstitialAd = null;
                    onAdClosedListener.onAdClosed();

                    loadInterstitialAds();

                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                    super.onAdFailedToShowFullScreenContent(adError);

                    mInterstitialAd = null;
                    onAdClosedListener.onAdClosed();

                    loadInterstitialAds();


                }

            });

            if (mClickCount != 0) {

                resetClickCount();

            }

        } else {

            onAdClosedListener.onAdClosed();

        }

        incrementClickCount();

    }

    public interface onAdClosedListener {

        void onAdClosed();


    }
}