package com.pt.zyfooai.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.FrameFooterConfig;

/**
 * Entrance + loop motion for server-driven footer profiles ({@code footer.profileAnimation}).
 * Legacy {@code none} / missing → no-op (static).
 */
public final class FrameProfileMotionHelper {

    private FrameProfileMotionHelper() {
    }

    public static void clear(View profileLay) {
        clear(profileLay, true);
    }

    private static void clear(View profileLay, boolean resetTransforms) {
        if (profileLay == null) {
            return;
        }
        Object tag = profileLay.getTag(R.id.profile_motion_animator);
        if (tag instanceof Animator) {
            ((Animator) tag).cancel();
        }
        profileLay.setTag(R.id.profile_motion_animator, null);
        profileLay.animate().cancel();
        if (resetTransforms) {
            profileLay.setTranslationX(0f);
            profileLay.setTranslationY(0f);
            profileLay.setRotation(0f);
            profileLay.setRotationX(0f);
            profileLay.setRotationY(0f);
            profileLay.setScaleX(1f);
            profileLay.setScaleY(1f);
            profileLay.setAlpha(1f);
        }
    }

    public static void apply(View frameRoot, View profileLay, @Nullable FrameFooterConfig footer) {
        apply(frameRoot, profileLay, footer, 0f);
    }

    /**
     * @param baseTranslationX resting X offset (e.g. footer_center canvas align) preserved by slides.
     */
    public static void apply(
            View frameRoot,
            View profileLay,
            @Nullable FrameFooterConfig footer,
            float baseTranslationX
    ) {
        if (profileLay == null || footer == null || !footer.showProfile) {
            clear(profileLay);
            return;
        }
        if (!footer.hasProfileMotion()) {
            clear(profileLay);
            profileLay.setTranslationX(baseTranslationX);
            return;
        }
        clear(profileLay, true);
        profileLay.setTranslationX(baseTranslationX);

        float density = profileLay.getResources().getDisplayMetrics().density;
        profileLay.setCameraDistance(12000f * density);

        Runnable start = () -> startMotion(frameRoot, profileLay, footer.resolvedAnimation());
        if (profileLay.getWidth() > 0 && frameRoot != null && frameRoot.getWidth() > 0) {
            start.run();
        } else {
            profileLay.post(start);
        }
    }

    private static void startMotion(View frameRoot, View profileLay, String animation) {
        int parentW = frameRoot != null && frameRoot.getWidth() > 0
                ? frameRoot.getWidth()
                : (profileLay.getParent() instanceof View
                ? ((View) profileLay.getParent()).getWidth()
                : profileLay.getWidth());
        int parentH = frameRoot != null && frameRoot.getHeight() > 0
                ? frameRoot.getHeight()
                : (profileLay.getParent() instanceof View
                ? ((View) profileLay.getParent()).getHeight()
                : profileLay.getHeight());
        float size = Math.max(profileLay.getWidth(), profileLay.getHeight());
        if (size <= 0) {
            size = 120f;
        }

        Animator animator;
        switch (animation) {
            case "spin_3d":
                animator = spin3d(profileLay);
                break;
            case "slide_ltr":
                animator = slideIn(profileLay, -Math.max(parentW, size * 2f), 0f, 0f, 0f);
                break;
            case "slide_rtl":
                animator = slideIn(profileLay, Math.max(parentW, size * 2f), 0f, 0f, 0f);
                break;
            case "slide_ttb":
                animator = slideIn(profileLay, 0f, 0f, -Math.max(parentH * 0.45f, size * 2f), 0f);
                break;
            case "slide_btt":
                animator = slideIn(profileLay, 0f, 0f, Math.max(size * 1.5f, 120f), 0f);
                break;
            case "orbit_3d":
                animator = orbit3d(profileLay, size * 0.22f);
                break;
            case "float_3d":
                animator = float3d(profileLay);
                break;
            case "flip_3d":
                animator = flip3d(profileLay);
                break;
            case "bounce_in":
                animator = bounceIn(profileLay);
                break;
            case "glow_spin":
                animator = glowSpin(profileLay);
                break;
            default:
                return;
        }
        profileLay.setTag(R.id.profile_motion_animator, animator);
        animator.start();
    }

    private static Animator spin3d(View view) {
        ObjectAnimator rotY = ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0f, 360f);
        rotY.setDuration(3200);
        rotY.setRepeatCount(ValueAnimator.INFINITE);
        rotY.setInterpolator(new LinearInterpolator());
        ObjectAnimator tilt = ObjectAnimator.ofFloat(view, View.ROTATION_X, 8f, -8f);
        tilt.setDuration(1600);
        tilt.setRepeatCount(ValueAnimator.INFINITE);
        tilt.setRepeatMode(ValueAnimator.REVERSE);
        tilt.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rotY, tilt);
        return set;
    }

    private static Animator slideIn(View view, float fromX, float toX, float fromY, float toY) {
        // Preserve any anchor offset already applied (e.g. footer_center).
        float baseX = view.getTranslationX();
        float baseY = view.getTranslationY();
        float startX = baseX + fromX;
        float endX = baseX + toX;
        float startY = baseY + fromY;
        float endY = baseY + toY;
        view.setTranslationX(startX);
        view.setTranslationY(startY);
        view.setAlpha(0.35f);
        ObjectAnimator tx = ObjectAnimator.ofFloat(view, View.TRANSLATION_X, startX, endX);
        ObjectAnimator ty = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, startY, endY);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.35f, 1f);
        ObjectAnimator settle = ObjectAnimator.ofFloat(view, View.ROTATION_Y, fromX < 0 ? -35f : 35f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(tx, ty, alpha, settle);
        set.setDuration(780);
        set.setInterpolator(new DecelerateInterpolator(1.4f));
        return set;
    }

    private static Animator orbit3d(View view, float radius) {
        final float baseX = view.getTranslationX();
        final float baseY = view.getTranslationY();
        ValueAnimator orbit = ValueAnimator.ofFloat(0f, 360f);
        orbit.setDuration(2800);
        orbit.setRepeatCount(ValueAnimator.INFINITE);
        orbit.setInterpolator(new LinearInterpolator());
        orbit.addUpdateListener(a -> {
            float deg = (float) a.getAnimatedValue();
            double rad = Math.toRadians(deg);
            view.setTranslationX(baseX + (float) (Math.cos(rad) * radius));
            view.setTranslationY(baseY + (float) (Math.sin(rad) * radius * 0.55f));
            view.setRotationY((float) (Math.sin(rad) * 28f));
            view.setRotationX((float) (Math.cos(rad) * 12f));
        });
        return orbit;
    }

    private static Animator float3d(View view) {
        ObjectAnimator ty = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -dp(view, 10), 0f);
        ty.setDuration(2200);
        ty.setRepeatCount(ValueAnimator.INFINITE);
        ty.setInterpolator(new AccelerateDecelerateInterpolator());
        ObjectAnimator sx = ObjectAnimator.ofFloat(view, View.SCALE_X, 1f, 1.04f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.04f, 1f);
        sx.setDuration(2200);
        sy.setDuration(2200);
        sx.setRepeatCount(ValueAnimator.INFINITE);
        sy.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator rotY = ObjectAnimator.ofFloat(view, View.ROTATION_Y, -12f, 12f);
        rotY.setDuration(2200);
        rotY.setRepeatCount(ValueAnimator.INFINITE);
        rotY.setRepeatMode(ValueAnimator.REVERSE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ty, sx, sy, rotY);
        return set;
    }

    private static Animator flip3d(View view) {
        view.setAlpha(0f);
        view.setRotationY(-90f);
        ObjectAnimator flip = ObjectAnimator.ofFloat(view, View.ROTATION_Y, -90f, 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.7f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.7f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(flip, alpha, scaleX, scaleY);
        set.setDuration(700);
        set.setInterpolator(new DecelerateInterpolator());
        // After entrance, gentle idle spin.
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator idle = ObjectAnimator.ofFloat(view, View.ROTATION_Y, 0f, 360f);
                idle.setDuration(5000);
                idle.setRepeatCount(ValueAnimator.INFINITE);
                idle.setInterpolator(new LinearInterpolator());
                view.setTag(R.id.profile_motion_animator, idle);
                idle.start();
            }
        });
        return set;
    }

    private static Animator bounceIn(View view) {
        view.setScaleX(0.2f);
        view.setScaleY(0.2f);
        view.setAlpha(0f);
        view.setTranslationY(dp(view, 40));
        ObjectAnimator sx = ObjectAnimator.ofFloat(view, View.SCALE_X, 0.2f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(view, View.SCALE_Y, 0.2f, 1f);
        ObjectAnimator ty = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, dp(view, 40), 0f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(sx, sy, ty, alpha);
        set.setDuration(750);
        set.setInterpolator(new OvershootInterpolator(1.6f));
        return set;
    }

    private static Animator glowSpin(View view) {
        ObjectAnimator rot = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, 360f);
        rot.setDuration(2600);
        rot.setRepeatCount(ValueAnimator.INFINITE);
        rot.setInterpolator(new LinearInterpolator());
        ObjectAnimator elev = ObjectAnimator.ofFloat(view, "elevation",
                dp(view, 2), dp(view, 14), dp(view, 2));
        elev.setDuration(1300);
        elev.setRepeatCount(ValueAnimator.INFINITE);
        elev.setRepeatMode(ValueAnimator.REVERSE);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.85f, 1f, 0.85f);
        alpha.setDuration(1300);
        alpha.setRepeatCount(ValueAnimator.INFINITE);
        ObjectAnimator rotY = ObjectAnimator.ofFloat(view, View.ROTATION_Y, -18f, 18f);
        rotY.setDuration(1300);
        rotY.setRepeatCount(ValueAnimator.INFINITE);
        rotY.setRepeatMode(ValueAnimator.REVERSE);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(rot, elev, alpha, rotY);
        return set;
    }

    private static float dp(View view, float value) {
        return value * view.getResources().getDisplayMetrics().density;
    }
}
