package com.pt.zyfooai.AdsUtils;

import static com.pt.zyfooai.utils.Constant.ADS_ENABLE;
import static com.pt.zyfooai.utils.Constant.IS_SUBSCRIBE;
import static com.pt.zyfooai.utils.Constant.REWARD_AD_ENABLED;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.ump.ConsentInformation;

public class RewardAdsManager {

    private Activity mContext;
    private RewardedAd mRewardedAd;
    private PreferenceManager preferenceManager;
    private OnAdLoaded listener;

    public RewardAdsManager(Activity context, OnAdLoaded listener) {
        this.mContext = context;
        this.listener = listener;
        preferenceManager = new PreferenceManager(context);
    }

    public void loadRewardAd() {

        Log.d("farukh------->", "loadRewardAd: test-1");
        if (preferenceManager.getBoolean(IS_SUBSCRIBE)) {
            listener.onAdWatched();
            return;
        }
        Log.d("farukh------->", "loadRewardAd: test-2");

//        if (!preferenceManager.getBoolean(ADS_ENABLE)
//                || !preferenceManager.getBoolean(REWARD_AD_ENABLED)) {
//            listener.onAdClosed();
//            return;
//        }
        Log.d("farukh------->", "loadRewardAd: test-3");

        AdRequest.Builder builder = new AdRequest.Builder();

        if (GDPRChecker.getStatus() == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
        }

        RewardedAd.load(
                mContext,
                "ca-app-pub-3940256099942544/5224354917",
                builder.build(),
                new RewardedAdLoadCallback() {

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        showRewardAd();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        mRewardedAd = null;
                        listener.onAdClosed();
                    }
                }
        );
    }

    private void showRewardAd() {

        if (mRewardedAd == null) {
            listener.onAdClosed();
            return;
        }

        mRewardedAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        mRewardedAd = null;
                        listener.onAdClosed();
                    }
                });

        mRewardedAd.show(mContext, rewardItem -> {
            mRewardedAd = null;
            listener.onAdWatched();
        });
    }

    public interface OnAdLoaded {
        void onAdClosed();
        void onAdWatched();
    }
}
