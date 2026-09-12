package com.pt.zyfooai.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.widget.TextViewCompat;

import com.pt.zyfooai.R;


public class AutoResizeTextView extends AppCompatTextView {

    public TextPaint _paint;
    public float _spacingAdd;
    public float _spacingMult;
    private int mOutlineColor;
    private int mOutlineSize;
    private int mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private int mTextColor;

    public AutoResizeTextView(Context context) {
        this(context, (AttributeSet) null, 16842884);
    }

    public AutoResizeTextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    @SuppressLint("ResourceType")
    public AutoResizeTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOutlineSize = 0;
        this.mOutlineColor = -256;
        this.mTextColor = getCurrentTextColor();
        if (attributeSet != null) {

            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.TextViewOutline);
            if (obtainStyledAttributes.hasValue(5)) {
                this.mOutlineSize = (int) obtainStyledAttributes.getDimension(R.styleable.TextViewOutline_mOutlineSize, 5.0f);
            }
            if (obtainStyledAttributes.hasValue(4)) {
                this.mOutlineColor = obtainStyledAttributes.getColor(R.styleable.TextViewOutline_mOutlineColor, -256);
            }
            if (obtainStyledAttributes.hasValue(3) || obtainStyledAttributes.hasValue(1) || obtainStyledAttributes.hasValue(2) || obtainStyledAttributes.hasValue(0)) {
                this.mShadowRadius = obtainStyledAttributes.getFloat(R.styleable.TextViewOutline_mShadowRadius, 0.0f);
                this.mShadowDx = obtainStyledAttributes.getFloat(R.styleable.TextViewOutline_mShadowDx, 0.0f);
                this.mShadowDy = obtainStyledAttributes.getFloat(R.styleable.TextViewOutline_mShadowDy, 0.0f);
                this.mShadowColor = obtainStyledAttributes.getColor(R.styleable.TextViewOutline_mShadowColor, 0);
            }
            obtainStyledAttributes.recycle();
        }

        this._spacingMult = 1.0f;
        this._spacingAdd = 0.0f;
        this._paint = new TextPaint(getPaint());

        AutoResizeTextView textViewCompat = this;

        int minTextSizePixels = getResources().getDimensionPixelSize(R.dimen._1sdp);
        int maxTextSizePixels = getResources().getDimensionPixelSize(R.dimen._200sdp);
        int stepGranularity = 1;
        int unit = TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM;

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
                textViewCompat,
                minTextSizePixels, // min text size in pixels
                maxTextSizePixels, // max text size in pixels
                stepGranularity, // step granularity
                unit // unit for text size
        );
        setIncludeFontPadding(false);
    }

    private void setPaintToOutline() {
        TextPaint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth((float) this.mOutlineSize);
        super.setTextColor(this.mOutlineColor);
        super.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, this.mShadowColor);
    }

    private void setPaintToRegular() {
        TextPaint paint = getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0.0f);
        super.setTextColor(this.mTextColor);
        if (this.mOutlineSize > 0) {
            super.setShadowLayer(0.0f, 0.0f, 0.0f, 0);
        } else {
            super.setShadowLayer(this.mShadowRadius, this.mShadowDx, this.mShadowDy, this.mShadowColor);
        }
    }

    @Override
    public void onMeasure(int i, int i2) {
        setPaintToOutline();
        super.onMeasure(i, i2);
    }

    @Override
    public void onDraw(Canvas canvas) {
        setPaintToOutline();
        super.onDraw(canvas);
        setPaintToRegular();
        super.onDraw(canvas);
    }

    @Override
    public void setTextColor(int i) {
        super.setTextColor(i);
        this.mTextColor = i;
    }

    @Override
    public void setShadowLayer(float f, float f2, float f3, int i) {
        super.setShadowLayer(f, f2, f3, i);
        this.mShadowRadius = f;
        this.mShadowDx = f2;
        this.mShadowDy = f3;
        this.mShadowColor = i;
    }

    public void setOutlineSize(int i) {
        this.mOutlineSize = i;
    }

    public void setOutlineColor(int i) {
        this.mOutlineColor = i;
    }


}