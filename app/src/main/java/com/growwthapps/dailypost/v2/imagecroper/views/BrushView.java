package com.growwthapps.dailypost.v2.imagecroper.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ImageView;

/* renamed from: ab.mp4u.cutpastephoto.helper.BrushView */
public class BrushView extends ImageView {
    public final float target_offset;
    public float centerx;
    public float centery;
    public float largeRadious;
    public Path lessoLineDrawingPath = new Path();
    public float offset;
    public float smallRadious;
    public float width;
    int alpga = 200;
    int density;
    DisplayMetrics metrics;
    private int ERASE = 1;
    private int LASSO = 3;
    private int MODE = 1;
    private int NONE = 0;
    private int TARGET = 2;

    public BrushView(Context context) {
        super(context);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.metrics = displayMetrics;
        int i = (int) displayMetrics.density;
        this.density = i;
        this.centerx = (float) (i * 166);
        this.centery = (float) (i * 200);
        this.largeRadious = (float) (i * 33);
        this.offset = (float) (i * 100);
        this.smallRadious = (float) (i * 3);
        this.target_offset = (float) (i * 66);
        this.width = (float) (i * 33);
    }

    public BrushView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.metrics = displayMetrics;
        int i = (int) displayMetrics.density;
        this.density = i;
        this.centerx = (float) (i * 166);
        this.centery = (float) (i * 200);
        this.largeRadious = (float) (i * 33);
        this.offset = (float) (i * 100);
        this.smallRadious = (float) (i * 3);
        this.target_offset = (float) (i * 66);
        this.width = (float) (i * 33);
    }

    public void setMode(int i) {
        this.MODE = i;
    }

    public void resetLessoLineDrawingPath(float f, float f2) {
        this.lessoLineDrawingPath.reset();
        this.lessoLineDrawingPath.moveTo(f, f2 - this.offset);
    }

    public void addLineToLessoLineDrawingPath(float f, float f2) {
        this.lessoLineDrawingPath.lineTo(f, f2 - this.offset);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        int i = this.MODE;
        if (i != this.NONE) {
            if (i == this.LASSO) {
                Paint paint = new Paint();
                paint.setColor(Color.argb(this.alpga, 255, 255, 0));
                paint.setStrokeWidth((float) (this.density * 3));
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawPath(this.lessoLineDrawingPath, paint);
            }
            if (this.offset > 0.0f || this.MODE == this.TARGET) {
                Paint paint2 = new Paint();
                paint2.setColor(Color.argb(255, 255, 0, 0));
                paint2.setAntiAlias(true);
                canvas.drawCircle(this.centerx, this.centery, this.smallRadious, paint2);
            }
            int i2 = this.MODE;
            if (i2 == this.ERASE) {
                Paint paint3 = new Paint();
                paint3.setColor(Color.argb(this.alpga, 255, 255, 0));
                paint3.setAntiAlias(true);
                paint3.setStrokeWidth((float) (this.density * 3));
                paint3.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(this.centerx, this.centery - this.offset, this.width, paint3);
            } else if (i2 == this.LASSO) {
                Paint paint4 = new Paint();
                paint4.setColor(Color.argb(this.alpga, 255, 0, 0));
                paint4.setStyle(Paint.Style.STROKE);
                paint4.setStrokeWidth((float) (this.density * 4));
                paint4.setAntiAlias(true);
                paint4.setStrokeWidth((float) (this.density * 1));
                float f = this.centerx;
                float f2 = this.largeRadious;
                float f3 = this.centery;
                float f4 = this.offset;
                canvas.drawLine(f - f2, f3 - f4, f + f2, f3 - f4, paint4);
                float f5 = this.centerx;
                float f6 = this.centery;
                float f7 = this.largeRadious;
                float f8 = this.offset;
                canvas.drawLine(f5, (f6 - f7) - f8, f5, (f6 + f7) - f8, paint4);
            } else if (i2 == this.TARGET) {
                Paint paint5 = new Paint();
                paint5.setColor(Color.argb(this.alpga, 255, 0, 0));
                paint5.setStyle(Paint.Style.STROKE);
                paint5.setStrokeWidth((float) (this.density * 4));
                paint5.setAntiAlias(true);
                paint5.setStrokeWidth((float) (this.density * 1));
                float f9 = this.centerx;
                float f10 = this.largeRadious;
                float f11 = this.centery;
                float f12 = this.target_offset;
                canvas.drawLine(f9 - f10, f11 - f12, f9 + f10, f11 - f12, paint5);
                float f13 = this.centerx;
                float f14 = this.centery;
                float f15 = this.largeRadious;
                float f16 = this.target_offset;
                canvas.drawLine(f13, (f14 - f15) - f16, f13, (f14 + f15) - f16, paint5);
            }
        } else {
            canvas.drawColor(0);
        }
    }
}
