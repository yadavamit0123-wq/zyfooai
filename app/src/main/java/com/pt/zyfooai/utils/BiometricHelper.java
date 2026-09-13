package com.pt.zyfooai.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.pt.zyfooai.R;

public final class BiometricHelper {

    public static final String BIOMETRIC_LOCK_ENABLED = "biometric_lock_enabled";

    private BiometricHelper() {
    }

    public static boolean isBiometricAvailable(Context context) {
        int result = BiometricManager.from(context)
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK);
        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static boolean isLockEnabled(Context context) {
        return new PreferenceManager(context).getBoolean(BIOMETRIC_LOCK_ENABLED);
    }

    public static void setLockEnabled(Context context, boolean enabled) {
        new PreferenceManager(context).setBoolean(BIOMETRIC_LOCK_ENABLED, enabled);
    }

    public static void authenticate(AppCompatActivity activity, Runnable onSuccess, Runnable onFailure) {
        if (!isBiometricAvailable(activity)) {
            if (onFailure != null) {
                onFailure.run();
            }
            return;
        }

        BiometricPrompt prompt = new BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        if (onFailure != null) {
                            onFailure.run();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                    }
                }
        );

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.app_name))
                .setSubtitle("Confirm your identity")
                .setNegativeButtonText(activity.getString(R.string.app__cancel))
                .build();
        prompt.authenticate(info);
    }
}
