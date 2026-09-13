package com.pt.zyfooai.utils

import android.content.Context

/**
 * Kotlin utility for Remote Config driven feature flags (A/B testing).
 */
object FeatureFlags {

    @JvmStatic
    fun isNativeAdsEnabled(context: Context): Boolean {
        val prefs = PreferenceManager(context)
        return prefs.getBoolean(Constant.ADS_ENABLE) && prefs.getBoolean(Constant.NATIVE_AD_ENABLED)
    }

    @JvmStatic
    fun nativeAdInterval(context: Context): Int {
        return RemoteConfigHelper.getNativeAdInterval(context)
    }

    @JvmStatic
    fun isHomeLayoutB(context: Context): Boolean {
        return RemoteConfigHelper.isLayoutVariantB(context)
    }

    @JvmStatic
    fun tierLabel(planType: String?, planName: String?): String {
        val source = (planType ?: planName ?: "").lowercase()
        return when {
            source.contains("business") -> "Business"
            source.contains("pro") -> "Pro"
            source.contains("basic") -> "Basic"
            else -> planName ?: "Plan"
        }
    }
}
