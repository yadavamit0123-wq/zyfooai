package com.pt.zyfooai.AdsUtils;

import static com.pt.zyfooai.utils.Constant.ADS_ENABLE;
import static com.pt.zyfooai.utils.Constant.BANNER_AD_ENABLED;
import static com.pt.zyfooai.utils.Constant.BANNER_AD_ID;
import static com.pt.zyfooai.utils.Constant.IS_SUBSCRIBE;
import static com.pt.zyfooai.utils.Constant.NATIVE_AD_ENABLED;
import static com.pt.zyfooai.utils.Constant.NATIVE_AD_ID;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.pt.zyfooai.R;
import com.pt.zyfooai.utils.PreferenceManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.ump.ConsentInformation;

public class AdsUtils {

    PreferenceManager preferenceManager;

    public AdsUtils(Activity context) {
        preferenceManager = new PreferenceManager(context);

    }

    public void loadNativeAd(Activity context, final FrameLayout frameLayout, ShimmerFrameLayout shimmerFrameLayout) {
        shimmerFrameLayout.startShimmer();
        if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {

        if (preferenceManager.getBoolean(ADS_ENABLE) && preferenceManager.getBoolean(NATIVE_AD_ENABLED)) {



            AdLoader.Builder builder = new AdLoader.Builder(context, preferenceManager.getString(NATIVE_AD_ID))
                    .forNativeAd(nativeAd1 -> {


                        shimmerFrameLayout.stopShimmer();
                        shimmerFrameLayout.setVisibility(View.GONE);


                        RelativeLayout adView = (RelativeLayout) context.getLayoutInflater()
                                .inflate(R.layout.ad_creation, null);


                        populateUnifiedNativeAdView(nativeAd1, adView.findViewById(R.id.unified));
                        frameLayout.removeAllViews();
                        frameLayout.addView(adView);

                    });


            AdRequest.Builder m_builder = new AdRequest.Builder();

            int request = GDPRChecker.getStatus();
            if (request == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                // load non Personalized ads
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                m_builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
            }

            builder.withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build().loadAd(m_builder.build());


        }

      }
    }


    private AdSize getAdSize(Activity activity, View adContainerView) {
        // Determine the screen width (less decorations) to use for the ad width.
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = adContainerView.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void showBannerAds(Activity context) {
        if (!preferenceManager.getBoolean(IS_SUBSCRIBE)) {
            if (preferenceManager.getBoolean(ADS_ENABLE) && preferenceManager.getBoolean(BANNER_AD_ENABLED)) {
                // Find the adContainerView within the activity's layout
                RelativeLayout adContainerView = context.findViewById(R.id.adViewContainer);

                // Create the AdView
                com.google.android.gms.ads.AdView adView = new AdView(context);
                adView.setAdUnitId(preferenceManager.getString(BANNER_AD_ID));

                // Remove any existing views from the adContainerView
                adContainerView.removeAllViews();

                // Add the AdView to the adContainerView
                adContainerView.addView(adView);

                // Set the ad size
                AdSize adSize = getAdSize(context, adContainerView);
                adView.setAdSize(adSize);

                // Build the ad request
                AdRequest.Builder adRequest = new AdRequest.Builder();

                // Check GDPR status and load non-personalized ads if necessary
                int request = GDPRChecker.getStatus();
                if (request == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                    // Load non-personalized ads
                    Bundle extras = new Bundle();
                    extras.putString("npa", "1");
                    adRequest.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                }

                // Start loading the ad in the background
                adView.loadAd(adRequest.build());

                // Set AdListener to handle ad loading events
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Hide adContainerView if ad fails to load
                        adContainerView.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAdLoaded() {
                        // Show adContainerView when ad is loaded
                        adContainerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }

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