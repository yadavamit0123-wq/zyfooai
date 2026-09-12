package com.pt.zyfooai.View;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pt.zyfooai.R;
import com.pt.zyfooai.ui.model.ElementInfo;
import com.pt.zyfooai.ui.utility.ImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cz.msebera.android.httpclient.HttpStatus;

public class StickerView extends RelativeLayout implements MultiTouchListener.TouchCallbackListener {
    public static final String TAG = "StickerView";
    private final String fieldFour = "";
    private final int fieldOne = 0;
    private final String fieldThree = "";
    private final int scaleRotateProg = 0;
    private final int xRotateProg = 0;
    private final int yRotateProg = 0;
    private final int zRotateProg = 0;
    public int currentState = 0;
    public boolean isFrameItem = false;
    public int extraMargin = 35;
    public String fieldTwo = "0,0";
    public int he;
    public boolean isMultiTouchEnabled = true;
    public boolean isUndoRedo = false;
    public int leftMargin = 0;
    public TouchEventListener listener = null;
    public ImageView mainIv;
    public int margin5dp = 2;
    public String stkrPath = "";
    public int topMargin = 0;
    public int wi;
    double angle = 0.0d;
    int baseh;
    int basew;
    int basex;
    int basey;
    float cX = 0.0f;
    float cY = 0.0f;
    double dAngle = 0.0d;
    float heightMain = 0.0f;
    boolean isFisrtAnimation = false;
    int margl;
    int margt;
    private final OnTouchListener mTouchListener1 = (view, motionEvent) -> {


        StickerView stickerView = (StickerView) view.getParent();
        int rawX = (int) motionEvent.getRawX();
        int rawY = (int) motionEvent.getRawY();
        LayoutParams layoutParams = (LayoutParams) StickerView.this.getLayoutParams();
        int action = motionEvent.getAction();

        Log.d("action___",""+action);
        if (action == 0) {
            if (stickerView != null) {
                stickerView.requestDisallowInterceptTouchEvent(true);
            }
            if (StickerView.this.listener != null) {
                StickerView.this.listener.onScaleDown(StickerView.this);
            }
            StickerView.this.invalidate();
            StickerView stickerView2 = StickerView.this;
            stickerView2.basex = rawX;
            stickerView2.basey = rawY;
            stickerView2.basew = stickerView2.getWidth();
            StickerView stickerView3 = StickerView.this;
            stickerView3.baseh = stickerView3.getHeight();
            StickerView.this.getLocationOnScreen(new int[2]);
            StickerView.this.margl = layoutParams.leftMargin;
            StickerView.this.margt = layoutParams.topMargin;
            StickerView.this.currentState = 1;


        } else if (action == 1) {
            StickerView stickerView4 = StickerView.this;
            stickerView4.wi = stickerView4.getLayoutParams().width;
            StickerView stickerView5 = StickerView.this;
            stickerView5.he = stickerView5.getLayoutParams().height;
            StickerView stickerView6 = StickerView.this;
            stickerView6.leftMargin = ((LayoutParams) stickerView6.getLayoutParams()).leftMargin;
            StickerView stickerView7 = StickerView.this;
            stickerView7.topMargin = ((LayoutParams) stickerView7.getLayoutParams()).topMargin;
            stickerView.fieldTwo = StickerView.this.leftMargin + "," + StickerView.this.topMargin;
            if (StickerView.this.listener != null) {
                StickerView.this.listener.onScaleUp(StickerView.this);
                if (StickerView.this.currentState == 3) {
                    StickerView.this.clickToSaveWork();
                }
                StickerView.this.currentState = 2;
            }
        } else if (action == 2) {
            if (stickerView != null) {
                stickerView.requestDisallowInterceptTouchEvent(true);
            }
            if (StickerView.this.listener != null) {
                StickerView.this.listener.onScaleMove(StickerView.this);
            }
            float degrees = (float) Math.toDegrees(Math.atan2(rawY - StickerView.this.basey, rawX - StickerView.this.basex));
            if (degrees < 0.0f) {
                degrees += 360.0f;
            }
            int i = rawX - StickerView.this.basex;
            int i2 = rawY - StickerView.this.basey;
            int i3 = i2 * i2;

            int sqrt = (int) (Math.sqrt((i * i) + i3) * Math.cos(Math.toRadians(degrees - StickerView.this.getRotation())));
            int sqrt2 = (int) (Math.sqrt((sqrt * sqrt) + i3) * Math.sin(Math.toRadians(degrees - StickerView.this.getRotation())));

            int i4 = (sqrt * 2) + StickerView.this.basew;
            int i5 = (sqrt2 * 2) + StickerView.this.baseh;
            if (i4 > (StickerView.this.extraMargin * 2) + StickerView.this.margin5dp) {
                layoutParams.width = i4;
                layoutParams.leftMargin = StickerView.this.margl - sqrt;
            }
            if (i5 > (StickerView.this.extraMargin * 2) + StickerView.this.margin5dp) {
                layoutParams.height = i5;
                layoutParams.topMargin = StickerView.this.margt - sqrt2;
            }
            StickerView.this.setLayoutParams(layoutParams);
            StickerView.this.performLongClick();
            StickerView.this.currentState = 3;
        }
        return true;
    };
    Animation scale;
    int screenHeight = HttpStatus.SC_MULTIPLE_CHOICES;
    int screenWidth = HttpStatus.SC_MULTIPLE_CHOICES;
    double tAngle = 0.0d;
    double vAngle = 0.0d;
    private final OnTouchListener rTouchListener = (view, motionEvent) -> {


        StickerView stickerView = (StickerView) view.getParent();
        int action = motionEvent.getAction();
        if (action == 0) {
            if (stickerView != null) {
                stickerView.requestDisallowInterceptTouchEvent(true);
            }
            if (StickerView.this.listener != null) {
                StickerView.this.listener.onRotateDown(StickerView.this);
            }
            Rect rect = new Rect();
            ((View) view.getParent()).getGlobalVisibleRect(rect);
            StickerView.this.cX = rect.exactCenterX();
            StickerView.this.cY = rect.exactCenterY();
            StickerView.this.vAngle = ((View) view.getParent()).getRotation();
            StickerView stickerView2 = StickerView.this;
            stickerView2.tAngle = (Math.atan2(stickerView2.cY - motionEvent.getRawY(), StickerView.this.cX - motionEvent.getRawX()) * 180.0d) / 3.141592653589793d;
            StickerView stickerView3 = StickerView.this;
            stickerView3.dAngle = stickerView3.vAngle - StickerView.this.tAngle;
            StickerView.this.currentState = 1;
        } else if (action != 1) {
            if (action == 2) {
                if (stickerView != null) {
                    stickerView.requestDisallowInterceptTouchEvent(true);
                }
                if (StickerView.this.listener != null) {
                    StickerView.this.listener.onRotateMove(StickerView.this);
                }
                StickerView stickerView4 = StickerView.this;
                stickerView4.angle = (Math.atan2(stickerView4.cY - motionEvent.getRawY(), StickerView.this.cX - motionEvent.getRawX()) * 180.0d) / 3.141592653589793d;
                float f = (float) (StickerView.this.angle + StickerView.this.dAngle);
                ((View) view.getParent()).setRotation(f);
                ((View) view.getParent()).invalidate();
                ((View) view.getParent()).requestLayout();
                if (Math.abs(90.0f - Math.abs(f)) <= 5.0f) {
                    f = f > 0.0f ? 90.0f : -90.0f;
                }
                if (Math.abs(0.0f - Math.abs(f)) <= 5.0f) {
                    f = f > 0.0f ? 0.0f : -0.0f;
                }
                if (Math.abs(180.0f - Math.abs(f)) <= 5.0f) {
                    f = f > 0.0f ? 180.0f : -180.0f;
                }
                ((View) view.getParent()).setRotation(f);
                StickerView.this.currentState = 3;
            }
        } else if (StickerView.this.listener != null) {
            StickerView.this.listener.onRotateUp(StickerView.this);
            if (StickerView.this.currentState == 3) {
                StickerView.this.clickToSaveWork();
            }
            StickerView.this.currentState = 2;
        }
        return true;
    };
    float widthMain = 0.0f;
    Animation zoomInScale;
    Animation zoomOutScale;
    private ImageView borderIv;
    private Bitmap btmp = null;
    private String colorType = "colored";
    private Context context;
    private ImageView deleteIv;
    private String drawableId;
    private int f26s;
    private ImageView flipIv;
    private int hueProg = 1;
    private int imgAlpha = 255;
    private int imgColor = 0;
    private boolean isBorderVisible = false;
    private boolean isColorFilterEnable = false;
    private boolean isFromAddText = false;
    private Uri resUri = null;
    private ImageView rotateIv;
    private float rotation;
    private ImageView scaleIv;
    private float yRotation;

    public StickerView(Context context2) {
        super(context2);
        init(context2);
    }

    public StickerView(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        init(context2);
    }

    public StickerView(Context context2, boolean z) {
        super(context2);
        this.isUndoRedo = z;
        init(context2);
    }

    public StickerView(Context context2, AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        init(context2);
    }

    public StickerView setOnTouchCallbackListener(TouchEventListener touchEventListener) {
        this.listener = touchEventListener;
        return this;
    }

    public void init(Context context2) {
        this.context = context2;
        this.mainIv = new ImageView(this.context);
        this.scaleIv = new ImageView(this.context);
        this.borderIv = new ImageView(this.context);
        this.flipIv = new ImageView(this.context);
        this.rotateIv = new ImageView(this.context);
        this.deleteIv = new ImageView(this.context);
        this.f26s = (int) dpToPx(this.context, 25);
        this.extraMargin = (int) dpToPx(this.context, 5f);
        this.margin5dp = (int) dpToPx(this.context, 5f);

        this.wi = dpToPx(this.context, 200);
        this.he = dpToPx(this.context, 200);

        this.scaleIv.setImageResource(R.drawable.ic_sticker_scale);
//        this.scaleIv.setElevation(150);

        this.borderIv.setImageResource(R.drawable.sticker_border_gray);
        this.flipIv.setImageResource(R.drawable.ic_sticker_flip);
//        this.flipIv.setElevation(150);

        this.rotateIv.setImageResource(R.drawable.ic_sticker_rotate);
//        this.rotateIv.setElevation(150);

        this.deleteIv.setImageResource(R.drawable.ic_sticker_delete);
//        this.deleteIv.setElevation(150);

        LayoutParams layoutParams = new LayoutParams(this.wi, this.he);
        LayoutParams layoutParams2 = new LayoutParams(-1, -1);
        layoutParams2.setMargins(2, 2, 2, 2);

        if (Build.VERSION.SDK_INT >= 17) {
            layoutParams2.addRule(17);
        } else {
            layoutParams2.addRule(1);
        }
        int i = this.f26s;
        LayoutParams layoutParams3 = new LayoutParams(i, i);
        layoutParams3.addRule(12);
        layoutParams3.addRule(11);

        int i2 = this.f26s;
        LayoutParams layoutParams4 = new LayoutParams(i2, i2);
        layoutParams4.addRule(10);
        layoutParams4.addRule(11);

        int i3 = this.f26s;
        LayoutParams layoutParams5 = new LayoutParams(i3, i3);
        layoutParams5.addRule(12);
        layoutParams5.addRule(9);

        int i4 = this.f26s;
        LayoutParams layoutParams6 = new LayoutParams(i4, i4);
        layoutParams6.addRule(10);
        layoutParams6.addRule(9);

        LayoutParams layoutParams7 = new LayoutParams(-1, -1);
        layoutParams7.setMargins(2, 2, 2, 2);
        setLayoutParams(layoutParams);

        addView(this.borderIv);
        this.borderIv.setLayoutParams(layoutParams7);
        this.borderIv.setScaleType(ImageView.ScaleType.FIT_XY);
        this.borderIv.setTag("border_iv");
        addView(this.mainIv);
        this.mainIv.setLayoutParams(layoutParams2);
        addView(this.flipIv);
        this.flipIv.setLayoutParams(layoutParams4);
        this.flipIv.setOnClickListener(view -> {
            ImageView imageView = StickerView.this.mainIv;
            float f = -180.0f;
            if (StickerView.this.mainIv.getRotationY() == -180.0f) {
                f = 0.0f;
            }
            imageView.setRotationY(f);
            StickerView.this.mainIv.invalidate();
            StickerView.this.requestLayout();
        });
        addView(this.rotateIv);
        this.rotateIv.setLayoutParams(layoutParams5);
        this.rotateIv.setOnTouchListener(this.rTouchListener);

        addView(this.deleteIv);
        this.deleteIv.setLayoutParams(layoutParams6);
        this.deleteIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteClick();
            }
        });

        addView(this.scaleIv);
        this.scaleIv.setLayoutParams(layoutParams3);
        this.scaleIv.setOnTouchListener(this.mTouchListener1);
        this.scaleIv.setTag("scale_iv");

        this.rotation = getRotation();
        this.scale = AnimationUtils.loadAnimation(getContext(), R.anim.sticker_scale_anim);
        this.zoomOutScale = AnimationUtils.loadAnimation(getContext(), R.anim.sticker_scale_zoom_out);
        this.zoomInScale = AnimationUtils.loadAnimation(getContext(), R.anim.sticker_scale_zoom_in);
        this.isMultiTouchEnabled = setDefaultTouchListener(false);
    }

    public boolean setDefaultTouchListener(boolean z) {
        if (z) {
            setOnTouchListener(new MultiTouchListener().enableRotation(true).setOnTouchCallbackListener(this));
            return true;
        }
        setOnTouchListener(null);
        return false;
    }

    public void setBorderVisibility(boolean z) {
        this.isBorderVisible = z;
        if (!z) {
            this.borderIv.setVisibility(View.GONE);
            this.scaleIv.setVisibility(View.GONE);
            this.flipIv.setVisibility(View.GONE);
            this.rotateIv.setVisibility(View.GONE);
            this.deleteIv.setVisibility(View.GONE);
            setBackgroundResource(0);
//            if (this.isColorFilterEnable) {
//                this.mainIv.setColorFilter(Color.parseColor("#303828"));
//            }
        } else if (this.borderIv.getVisibility() != View.VISIBLE) {
            this.borderIv.setVisibility(View.VISIBLE);
            this.scaleIv.setVisibility(View.VISIBLE);
            this.flipIv.setVisibility(View.VISIBLE);
            this.rotateIv.setVisibility(View.VISIBLE);
            this.deleteIv.setVisibility(View.VISIBLE);
//            setBackgroundResource(R.drawable.sticker_gray1);
            if (this.isFisrtAnimation || this.isFromAddText) {
                this.mainIv.startAnimation(this.scale);
            }
            this.isFisrtAnimation = true;
        }
    }

    public boolean getBorderVisbilty() {
        return this.isBorderVisible;
    }

    public void opecitySticker(int i) {
        try {
            this.mainIv.setAlpha(i);
            this.imgAlpha = i;
        } catch (Exception e) {
        }
    }

    public int getHueProg() {
        return this.hueProg;
    }

    public void setHueProg(int i) {
        this.hueProg = i;
        int i2 = this.hueProg;
        if (i2 == 0) {
//            this.mainIv.setColorFilter(-1);
        } else if (i2 == 100) {
//            this.mainIv.setColorFilter(ViewCompat.MEASURED_STATE_MASK);
        } else {
//            this.mainIv.setColorFilter(ColorFilterGenerator.adjustHue((float) i));
        }
    }

    public String getColorType() {
        return this.colorType;
    }

    public int getAlphaProg() {
        return this.imgAlpha;
    }

    public void setAlphaProg(int i) {
        opecitySticker(i);
    }

    public int getColor() {
        return this.imgColor;
    }

    public void setColor(int i) {
        try {
            this.mainIv.setColorFilter(i);
            this.imgColor = i;
        } catch (Exception e) {
        }
    }

    public void setBgDrawable(String str) {
        Glide.with(this.context).load(getResources().getIdentifier(str, "drawable", this.context.getPackageName())).apply(new RequestOptions().dontAnimate().placeholder(R.drawable.placeholder).error(R.drawable.placeholder)).into(this.mainIv);
        this.drawableId = str;
        if (this.isFisrtAnimation || this.isFromAddText) {
            this.mainIv.startAnimation(this.zoomOutScale);
        }
        this.isFisrtAnimation = true;
    }

    public void setStrPath(String str) {
        Glide.with(context)
                .asBitmap()
                .load(str)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        StickerView.this.btmp = resource;
                        StickerView.this.mainIv.setImageBitmap(resource);
                        Log.d("setStrPath__", "onResourceReady: ");
                    }
                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        Log.d("setStrPath__", "onLoadCleared: ");
                    }
                });
        this.stkrPath = str;
        if (this.isFisrtAnimation || this.isFromAddText) {
            this.mainIv.startAnimation(this.zoomOutScale);
        }
        this.isFisrtAnimation = true;
    }

    public Bitmap getMainImageBitmap() {
        return this.btmp;
    }

    public void optimize(float f, float f2) {
        setX(getX() * f);
        setY(getY() * f2);
        getLayoutParams().width = (int) (((float) this.wi) * f);
        getLayoutParams().height = (int) (((float) this.he) * f2);
    }

    public void optimizeScreen(float f, float f2) {
        this.screenHeight = (int) f2;
        this.screenWidth = (int) f;
    }

    public void setViewWH(float f, float f2) {
        this.widthMain = f;
        this.heightMain = f2;
    }

    public float getMainWidth() {
        return this.widthMain;
    }

    public float getMainHeight() {
        return this.heightMain;
    }

    public void incrX() {
        setX(getX() + 2.0f);
    }

    public void decX() {
        setX(getX() - 2.0f);
    }

    public void incrY() {
        setY(getY() + 2.0f);
    }

    public void decY() {
        setY(getY() - 2.0f);
    }

    public ElementInfo getComponentInfo() {
        Bitmap bitmap = this.btmp;
        if (bitmap != null) {
            this.stkrPath = saveBitmapObject1(bitmap);
        }
        ElementInfo elementInfo = new ElementInfo();
        elementInfo.setPOS_X(getX());
        elementInfo.setPOS_Y(getY());
        elementInfo.setWIDTH(this.wi);
        elementInfo.setHEIGHT(this.he);
        elementInfo.setRES_ID(this.drawableId);
        elementInfo.setSTC_COLOR(this.imgColor);
//        elementInfo.setRES_URI(this.resUri);
        elementInfo.setSTC_OPACITY(this.imgAlpha);
//        elementInfo.setCOLORTYPE(this.colorType);
//        elementInfo.setBITMAP(this.btmp);
        elementInfo.setROTATION(getRotation());
        elementInfo.setY_ROTATION(this.mainIv.getRotationY());
        elementInfo.setXRotateProg(this.xRotateProg);
        elementInfo.setYRotateProg(this.yRotateProg);
        elementInfo.setZRotateProg(this.zRotateProg);
        elementInfo.setScaleProg(this.scaleRotateProg);
        elementInfo.setSTKR_PATH(this.stkrPath);
        elementInfo.setSTC_HUE(this.hueProg);
//        elementInfo.setFIELD_ONE(this.fieldOne);
//        elementInfo.setFIELD_TWO(this.fieldTwo);
//        elementInfo.setFIELD_THREE(this.fieldThree);
//        elementInfo.setFIELD_FOUR(this.fieldFour);
        return elementInfo;
    }

    public void setComponentInfo(ElementInfo elementInfo) {
        this.wi = elementInfo.getWIDTH();
        this.he = elementInfo.getHEIGHT();
//        this.drawableId = elementInfo.getRES_ID();
//        this.resUri = elementInfo.getRES_URI();
//        this.btmp = elementInfo.getBITMAP();
        this.rotation = elementInfo.getROTATION();
        this.imgColor = elementInfo.getSTC_COLOR();
        this.yRotation = elementInfo.getY_ROTATION();
        this.imgAlpha = elementInfo.getSTC_OPACITY();
        this.stkrPath = elementInfo.getSTKR_PATH();
//        this.colorType = elementInfo.getCOLORTYPE();
        this.hueProg = elementInfo.getSTC_HUE();
//        this.fieldTwo = elementInfo.getFIELD_TWO();
        if (!this.stkrPath.equals("")) {
            setStrPath(this.stkrPath);
        }
//        else if (this.drawableId.equals("")) {
//            this.mainIv.setImageBitmap(this.btmp);
//        } else {
//            setBgDrawable(this.drawableId);
//        }
        if (this.colorType.equals("white")) {
            setColor(this.imgColor);
        } else {
            setHueProg(this.hueProg);
        }
        setRotation(this.rotation);
        opecitySticker(this.imgAlpha);
        if (this.fieldTwo.equals("")) {
            getLayoutParams().width = this.wi;
            getLayoutParams().height = this.he;
            setX(elementInfo.getPOS_X());
            setY(elementInfo.getPOS_Y());
        } else {
            try {
                String[] split = this.fieldTwo.split(",");
                int parseInt = Integer.parseInt(split[0]);
                int parseInt2 = Integer.parseInt(split[1]);
                ((LayoutParams) getLayoutParams()).leftMargin = parseInt;
                ((LayoutParams) getLayoutParams()).topMargin = parseInt2;
                getLayoutParams().width = this.wi;
                getLayoutParams().height = this.he;
                setX(elementInfo.getPOS_X() + ((float) (parseInt * -1)));
                setY(elementInfo.getPOS_Y() + ((float) (parseInt2 * -1)));
            } catch (ArrayIndexOutOfBoundsException e) {
                getLayoutParams().width = this.wi;
                getLayoutParams().height = this.he;
                setX(elementInfo.getPOS_X());
                setY(elementInfo.getPOS_Y());
                e.printStackTrace();
            }
        }
        if (elementInfo.getTYPE() == "SHAPE") {
            this.flipIv.setVisibility(View.GONE);
        }
        if (elementInfo.getTYPE() == "STICKER") {
            this.flipIv.setVisibility(View.VISIBLE);
        }
        this.mainIv.setRotationY(this.yRotation);
    }

    public String saveBitmapObject1(Bitmap bitmap) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), ".Poster Maker Stickers/category1");
        file.mkdirs();
        File file2 = new File(file, "raw1-" + System.currentTimeMillis() + ".png");
        String absolutePath = file2.getAbsolutePath();
        if (file2.exists()) {
            file2.delete();
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file2);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.close();
            return absolutePath;
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("testing", "Exception" + e.getMessage());
            return "";
        }
    }

    public int dpToPx(Context context2, int i) {
        context2.getResources();
        return (int) (Resources.getSystem().getDisplayMetrics().density * ((float) i));
    }

    public float dpToPx(Context context2, float f) {
        context2.getResources();
        return (float) Math.round(f * Resources.getSystem().getDisplayMetrics().density);
    }

    public void onTouchCallback(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchDown(view);
        }
    }

    public void onTouchUpCallback(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchUp(view);
        }
    }

    public void onTouchMoveCallback(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMove(view);
        }
    }

    public void onMidX(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onMidX(view);
        }
    }

    public void onMidY(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onMidY(view);
        }
    }

    public void onMidXY(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onMidXY(view);
        }
    }

    public void onXY(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onXY(view);
        }
    }

    public void onTouchUpClick(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMoveUpClick(view);
        }
    }

    public void clickToSaveWork() {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMoveUpClick(this);
        }
    }

    public boolean isFrameItem() {
        return isFrameItem;
    }

    public void setFrameItem(boolean b) {
        isFrameItem = b;
    }

    public void deleteView() {
        DeleteClick();
    }


    public interface TouchEventListener {
        void onDelete();

        void onEdit(View view, Uri uri);

        void onMidX(View view);

        void onMidXY(View view);

        void onMidY(View view);

        void onRotateDown(View view);

        void onRotateMove(View view);

        void onRotateUp(View view);

        void onScaleDown(View view);

        void onScaleMove(View view);

        void onScaleUp(View view);

        void onTouchDown(View view);

        void onTouchMove(View view);

        void onTouchMoveUpClick(View view);

        void onTouchUp(View view);

        void onXY(View view);
    }

    public void DeleteClick() {
        final ViewGroup viewGroup = (ViewGroup) StickerView.this.getParent();
        StickerView.this.zoomInScale.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(StickerView.this);
            }
        });
        StickerView.this.mainIv.startAnimation(StickerView.this.zoomInScale);
        StickerView stickerView = StickerView.this;
        stickerView.isFisrtAnimation = true;
        stickerView.setBorderVisibility(false);
        if (StickerView.this.listener != null) {
            StickerView.this.listener.onDelete();
        }
    }


}
