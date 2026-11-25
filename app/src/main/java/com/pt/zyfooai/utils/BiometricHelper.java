package com.pt.zyfooai.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.pt.zyfooai.R;

/**
 * App-wide biometric lock. When enabled, user must unlock on every cold/warm app open.
 */
public final class BiometricHelper {

    public static final String BIOMETRIC_LOCK_ENABLED = "biometric_lock_enabled";

    /** Fingerprint / face, with device PIN/pattern fallback. */
    private static final int AUTHENTICATORS =
            BiometricManager.Authenticators.BIOMETRIC_WEAK
                    | BiometricManager.Authenticators.DEVICE_CREDENTIAL;

    private static volatile boolean sessionUnlocked;
    private static volatile boolean lifecycleHooked;
    private static volatile boolean promptShowing;

    private BiometricHelper() {
    }

    public static void initAppLifecycle() {
        if (lifecycleHooked) {
            return;
        }
        lifecycleHooked = true;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStop(@NonNull LifecycleOwner owner) {
                // App went to background → require biometric again next open.
                lockSession();
            }
        });
    }

    public static boolean isBiometricAvailable(Context context) {
        if (context == null) {
            return false;
        }
        int result = BiometricManager.from(context).canAuthenticate(AUTHENTICATORS);
        return result == BiometricManager.BIOMETRIC_SUCCESS;
    }

    public static String unavailableReason(Context context) {
        if (context == null) {
            return "Biometric not available";
        }
        int result = BiometricManager.from(context).canAuthenticate(AUTHENTICATORS);
        switch (result) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                return null;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                return context.getString(R.string.biometric_none_enrolled);
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                return context.getString(R.string.biometric_no_hardware);
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                return context.getString(R.string.biometric_hw_unavailable);
            default:
                return context.getString(R.string.biometric_not_available);
        }
    }

    public static boolean isLockEnabled(Context context) {
        return context != null
                && new PreferenceManager(context).getBoolean(BIOMETRIC_LOCK_ENABLED);
    }

    public static void setLockEnabled(Context context, boolean enabled) {
        if (context == null) {
            return;
        }
        new PreferenceManager(context).setBoolean(BIOMETRIC_LOCK_ENABLED, enabled);
        if (!enabled) {
            sessionUnlocked = true;
        }
    }

    public static boolean needsAppUnlock(Context context) {
        return isLockEnabled(context) && !sessionUnlocked;
    }

    public static void markSessionUnlocked() {
        sessionUnlocked = true;
    }

    public static void lockSession() {
        sessionUnlocked = false;
    }

    /**
     * Turn lock ON only after a successful biometric confirmation.
     */
    public static void enableWithVerification(
            AppCompatActivity activity,
            Runnable onEnabled,
            Runnable onCancelled
    ) {
        if (activity == null) {
            return;
        }
        String reason = unavailableReason(activity);
        if (reason != null) {
            Toast.makeText(activity, reason, Toast.LENGTH_LONG).show();
            if (onCancelled != null) {
                onCancelled.run();
            }
            return;
        }
        authenticate(activity, () -> {
            setLockEnabled(activity, true);
            markSessionUnlocked();
            Toast.makeText(activity, R.string.biometric_enabled, Toast.LENGTH_SHORT).show();
            if (onEnabled != null) {
                onEnabled.run();
            }
        }, () -> {
            if (onCancelled != null) {
                onCancelled.run();
            }
        });
    }

    /**
     * Gate entry to the main app. On cancel, {@code onFailure} is invoked (usually finish).
     */
    public static void requireUnlockIfNeeded(
            AppCompatActivity activity,
            Runnable onUnlocked,
            Runnable onFailure
    ) {
        if (activity == null) {
            return;
        }
        if (!needsAppUnlock(activity)) {
            if (onUnlocked != null) {
                onUnlocked.run();
            }
            return;
        }
        authenticate(activity, () -> {
            markSessionUnlocked();
            if (onUnlocked != null) {
                onUnlocked.run();
            }
        }, onFailure);
    }

    public static void authenticate(AppCompatActivity activity, Runnable onSuccess, Runnable onFailure) {
        if (activity == null || activity.isFinishing()) {
            if (onFailure != null) {
                onFailure.run();
            }
            return;
        }
        String reason = unavailableReason(activity);
        if (reason != null) {
            Toast.makeText(activity, reason, Toast.LENGTH_LONG).show();
            if (onFailure != null) {
                onFailure.run();
            }
            return;
        }
        if (promptShowing) {
            return;
        }
        promptShowing = true;

        BiometricPrompt prompt = new BiometricPrompt(
                activity,
                ContextCompat.getMainExecutor(activity),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        promptShowing = false;
                        if (onSuccess != null) {
                            onSuccess.run();
                        }
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        promptShowing = false;
                        // User cancelled / locked out → treat as failure for app gate.
                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED
                                || errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                                || errorCode == BiometricPrompt.ERROR_CANCELED
                                || errorCode == BiometricPrompt.ERROR_LOCKOUT
                                || errorCode == BiometricPrompt.ERROR_LOCKOUT_PERMANENT
                                || errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE
                                || errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS
                                || errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT
                                || errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL) {
                            if (onFailure != null) {
                                onFailure.run();
                            }
                        } else if (onFailure != null) {
                            onFailure.run();
                        }
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        // Wrong fingerprint — prompt stays open for retry.
                    }
                }
        );

        BiometricPrompt.PromptInfo info = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(activity.getString(R.string.biometric_prompt_title))
                .setSubtitle(activity.getString(R.string.biometric_prompt_subtitle))
                .setAllowedAuthenticators(AUTHENTICATORS)
                .build();
        try {
            prompt.authenticate(info);
        } catch (Exception e) {
            promptShowing = false;
            Toast.makeText(activity, R.string.biometric_not_available, Toast.LENGTH_SHORT).show();
            if (onFailure != null) {
                onFailure.run();
            }
        }
    }
}
