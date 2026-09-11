package com.growwthapps.dailypost.v2;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ToggleButton;
import com.google.android.gms.ads.RequestConfiguration;

/* loaded from: classes.dex */
public class ToggleImageButton extends ToggleButton {

    /* renamed from: u  reason: collision with root package name */
    public Drawable f13517u;

    /* renamed from: v  reason: collision with root package name */
    public Drawable f13518v;

    public ToggleImageButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, new int[]{R.attr.tib_drawable_off, R.attr.tib_drawable_on}, 0, 0);
        this.f13518v = obtainStyledAttributes.getDrawable(1);
        this.f13517u = obtainStyledAttributes.getDrawable(0);
        obtainStyledAttributes.recycle();
        setTextOn(RequestConfiguration.MAX_AD_CONTENT_RATING_UNSPECIFIED);
        setTextOff(RequestConfiguration.MAX_AD_CONTENT_RATING_UNSPECIFIED);
        super.setChecked(!isChecked());
        super.setChecked(!isChecked());
    }

    @Override // android.widget.TextView, android.view.View
    public final void onLayout(boolean z10, int i10, int i11, int i12, int i13) {
        Drawable drawable;
        Drawable drawable2;
        super.onLayout(z10, i10, i11, i12, i13);
        boolean isChecked = isChecked();
        if (isChecked && (drawable2 = this.f13518v) != null) {
            setBackgroundDrawable(drawable2);
        } else if (!isChecked && (drawable = this.f13517u) != null) {
            setBackgroundDrawable(drawable);
        }
    }

    @Override // android.widget.ToggleButton, android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z10) {
        Drawable drawable;
        Drawable drawable2;
        super.setChecked(z10);
        boolean isChecked = isChecked();
        if (isChecked && (drawable2 = this.f13518v) != null) {
            setBackgroundDrawable(drawable2);
        } else if (!isChecked && (drawable = this.f13517u) != null) {
            setBackgroundDrawable(drawable);
        }
    }
}
