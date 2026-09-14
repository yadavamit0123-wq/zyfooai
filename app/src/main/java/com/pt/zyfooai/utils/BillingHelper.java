package com.pt.zyfooai.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.pt.zyfooai.R;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;

import java.util.Collections;
import java.util.List;

public class BillingHelper implements PurchasesUpdatedListener {

    public static final String WATERMARK_PRODUCT_ID = "watermark_remove_lifetime";
    public static final String WATERMARK_IAP_PURCHASED = "watermark_iap_purchased";

    private static final String TAG = "BillingHelper";

    private final Activity activity;
    private final PreferenceManager preferenceManager;
    private BillingClient billingClient;
    private Runnable onPurchaseSuccess;

    public BillingHelper(Activity activity) {
        this.activity = activity;
        this.preferenceManager = new PreferenceManager(activity);
    }

    public void connect(Runnable onReady) {
        if (billingClient != null && billingClient.isReady()) {
            if (onReady != null) {
                onReady.run();
            }
            return;
        }
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases()
                .build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && onReady != null) {
                    onReady.run();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected");
            }
        });
    }

    public void purchaseWatermarkRemove(Runnable onSuccess) {
        this.onPurchaseSuccess = onSuccess;
        connect(() -> queryAndLaunch(WATERMARK_PRODUCT_ID));
    }

    private void queryAndLaunch(String productId) {
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(Collections.singletonList(
                        QueryProductDetailsParams.Product.newBuilder()
                                .setProductId(productId)
                                .setProductType(BillingClient.ProductType.INAPP)
                                .build()
                ))
                .build();
        billingClient.queryProductDetailsAsync(params, (billingResult, productDetailsList) -> {
            if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK
                    || productDetailsList == null || productDetailsList.isEmpty()) {
                Log.w(TAG, "Product not found: " + productId);
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, R.string.watermark_product_unavailable, Toast.LENGTH_LONG).show());
                return;
            }
            ProductDetails details = productDetailsList.get(0);
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(Collections.singletonList(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                    .setProductDetails(details)
                                    .build()
                    ))
                    .build();
            billingClient.launchBillingFlow(activity, flowParams);
        });
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK || purchases == null) {
            return;
        }
        for (Purchase purchase : purchases) {
            if (purchase.getProducts().contains(WATERMARK_PRODUCT_ID)
                    && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                preferenceManager.setBoolean(WATERMARK_IAP_PURCHASED, true);
                if (!purchase.isAcknowledged()) {
                    billingClient.acknowledgePurchase(
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build(),
                            result -> {
                            }
                    );
                }
                if (onPurchaseSuccess != null) {
                    onPurchaseSuccess.run();
                }
            }
        }
    }

    public static boolean isWatermarkPurchased(Context context) {
        return new PreferenceManager(context).getBoolean(WATERMARK_IAP_PURCHASED);
    }

    public void destroy() {
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }
}
