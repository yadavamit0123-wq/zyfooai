package com.growwthapps.dailypost.v2.View.text;

import static com.growwthapps.dailypost.v2.utils.MyUtils.getAppFolder;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.ScaleXSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;


import com.google.firebase.BuildConfig;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.View.AutoResizeTextView;

import java.io.File;
import java.util.Objects;

public class AutofitTextRel extends RelativeLayout implements MultiTouchListener.TouchCallbackListener {

    private static final String TAG = "AutofitTextRel";
    public ImageView background_iv;
    public String bgDrawable = "0";
    public String NAME = "";
    public int f27s;
    public String field_two = "0,0";
    public int he;
    public boolean isMultiTouchEnabled = true;
    public int leftMargin = 0;
    public TouchEventListener listener = null;
    public AutoResizeTextView autoResizeTextView;
    public int topMargin = 0;
    public int wi;
    double angle = 0.0d;
    int baseh;
    int basew;
    int basex;
    int basey;
    float cX = 0.0f;
    float cY = 0.0f;
    int currentState = 0;
    double dAngle = 0.0d;
    int height;
    float heightMain = 0.0f;
    boolean playAnimation = true;
    int margl;
    int margt;
    float ratio;
    int sh = 1794;
    int sw = 1080;
    double tAngle = 0.0d;
    double vAngle = 0.0d;
    int width;
    float widthMain = 0.0f;
    Animation zoomInScale;
    Animation zoomOutScale;
    private int bgAlpha = 255;
    private int bgColor = 0;
    private ImageView border_iv;

    private Context context;
    private ImageView delete_iv;
    private String justification = "";
    private String fontName = "";
    private GestureDetector gd = null;
    private boolean isBold;
    private boolean isBorderVisible = false;
    private boolean isItalic = false;
    private boolean isUnderLine = false;
    private float leftRightShadow = 0.0f;
    private OnTouchListener mTouchListener1 = new Touch();
    private int outercolor = 0;
    private int outersize = 0;
    private int progress = 0;
    private int latterSpacing = 0;
    private int lineSpacing = 0;
    public boolean isFrameItem = false;
    RelativeLayout relativeLayout;

    private OnTouchListener rTouchListener = (view, motionEvent) -> {


        AutofitTextRel autofitTextRel = (AutofitTextRel) view.getParent();

        int action = motionEvent.getAction();
        if (action == 0) {
            if (autofitTextRel != null) {
                autofitTextRel.requestDisallowInterceptTouchEvent(true);
            }
            if (AutofitTextRel.this.listener != null) {
                AutofitTextRel.this.listener.onRotateDown(AutofitTextRel.this);
            }
            Rect rect = new Rect();
            ((View) view.getParent()).getGlobalVisibleRect(rect);
            AutofitTextRel.this.cX = rect.exactCenterX();
            AutofitTextRel.this.cY = rect.exactCenterY();
            AutofitTextRel.this.vAngle = (double) ((View) view.getParent()).getRotation();
            AutofitTextRel autofitTextRel2 = AutofitTextRel.this;
            autofitTextRel2.tAngle = (Math.atan2((double) (autofitTextRel2.cY - motionEvent.getRawY()), (double) (AutofitTextRel.this.cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
            AutofitTextRel autofitTextRel3 = AutofitTextRel.this;
            autofitTextRel3.dAngle = autofitTextRel3.vAngle - AutofitTextRel.this.tAngle;
            AutofitTextRel.this.currentState = 1;
        } else if (action != 1) {
            if (action == 2) {
                if (autofitTextRel != null) {
                    autofitTextRel.requestDisallowInterceptTouchEvent(true);
                }
                if (AutofitTextRel.this.listener != null) {
                    AutofitTextRel.this.listener.onRotateMove(AutofitTextRel.this);
                }
                AutofitTextRel autofitTextRel4 = AutofitTextRel.this;
                autofitTextRel4.angle = (Math.atan2((double) (autofitTextRel4.cY - motionEvent.getRawY()), (double) (AutofitTextRel.this.cX - motionEvent.getRawX())) * 180.0d) / 3.141592653589793d;
                ((View) view.getParent()).setRotation((float) (AutofitTextRel.this.angle + AutofitTextRel.this.dAngle));
                ((View) view.getParent()).invalidate();
                ((View) view.getParent()).requestLayout();
                AutofitTextRel.this.currentState = 3;
            }
        } else if (AutofitTextRel.this.listener != null) {
            AutofitTextRel.this.listener.onRotateUp(AutofitTextRel.this);
            if (AutofitTextRel.this.currentState == 3) {
                AutofitTextRel.this.clickToSaveWork();
            }
            AutofitTextRel.this.currentState = 2;
        }
        return true;
    };

    private ImageView rotate_iv;

    private ImageView scale_iv;
    private int shadowColor = ViewCompat.MEASURED_STATE_MASK;
    private int shadowColorProgress = 255;
    private int shadowProg = 0;

    private int tAlpha = 100;
    private int tColor = ViewCompat.MEASURED_STATE_MASK;
    private String text = "";
    private Path textPath;
    private float topBottomShadow = 0.0f;
    private int xRotateProg = 0;
    private int yRotateProg = 0;
    private int zRotateProg = 0;


    public AutofitTextRel(Context context2) {
        super(context2);
        init(context2);
    }

    public AutofitTextRel(Context context2, boolean z) {
        super(context2);
        this.playAnimation = z;
        init(context2);
    }

    public AutofitTextRel(Context context2, AttributeSet attributeSet) {
        super(context2, attributeSet);
        init(context2);
    }

    public AutofitTextRel(Context context2, AttributeSet attributeSet, int i) {
        super(context2, attributeSet, i);
        init(context2);
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

    public AutofitTextRel setOnTouchCallbackListener(TouchEventListener touchEventListener) {
        this.listener = touchEventListener;
        return this;
    }

    public void setDrawParams() {
        invalidate();
    }

    public void init(Context context2) {
        try {
            this.context = context2;
            Display defaultDisplay = ((Activity) this.context).getWindowManager().getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getSize(point);
            this.width = point.x;
            this.height = point.y;
            this.ratio = ((float) this.width) / ((float) this.height);

            this.relativeLayout = new RelativeLayout(this.context);
            LayoutParams relativeParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            this.autoResizeTextView = new AutoResizeTextView(this.context);

            this.scale_iv = new ImageView(this.context);
            this.border_iv = new ImageView(this.context);
            this.background_iv = new ImageView(this.context);
            this.delete_iv = new ImageView(this.context);
            this.rotate_iv = new ImageView(this.context);
            this.f27s = dpToPx(this.context, 20);
            this.wi = dpToPx(this.context, 200);
            this.he = dpToPx(this.context, 200);

            setBackgroundResource(R.drawable.sticker_border_gray);
            this.scale_iv.setImageResource(R.drawable.ic_sticker_scale);
            this.background_iv.setImageResource(0);
            this.rotate_iv.setImageResource(R.drawable.ic_sticker_rotate);
            this.delete_iv.setImageResource(R.drawable.ic_sticker_delete);

            if (BuildConfig.DEBUG){
//                this.autoResizeTextView.setBackgroundColor(Color.GREEN);
            }

            LayoutParams layoutParams = new LayoutParams(this.wi, this.he);
            setLayoutParams(layoutParams);

            LayoutParams layoutParams2 = new LayoutParams(this.f27s, this.f27s);
            layoutParams2.addRule(12);
            layoutParams2.addRule(11);

            LayoutParams layoutParams3 = new LayoutParams(this.f27s, this.f27s);
            layoutParams3.addRule(12);
            layoutParams3.addRule(9);

            addView(this.background_iv);
            LayoutParams layoutParams7 = new LayoutParams(-1, -1);
            this.background_iv.setLayoutParams(layoutParams7);
            this.background_iv.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(this.border_iv);

            LayoutParams layoutParams6 = new LayoutParams(-1, -1);
            this.border_iv.setLayoutParams(layoutParams6);
            this.border_iv.setTag("border_iv");

            addView(this.autoResizeTextView);

            this.autoResizeTextView.setTextSize(400.0f);
            this.autoResizeTextView.setText(this.text);
            this.autoResizeTextView.setTextColor(this.tColor);
            this.autoResizeTextView.setOutlineSize(0);
            this.autoResizeTextView.setOutlineColor(0);
            this.autoResizeTextView.setShadowLayer(10.6f, 5.5f, 5.3f, ViewCompat.MEASURED_STATE_MASK);

            LayoutParams layoutParams4 = new LayoutParams(-1, -1);
            layoutParams4.addRule(17);
            this.autoResizeTextView.setLayoutParams(layoutParams4);
            this.autoResizeTextView.setGravity(17);

            addView(this.delete_iv);
            LayoutParams deletelayoutParams = new LayoutParams(this.f27s, this.f27s);
            deletelayoutParams.addRule(ALIGN_PARENT_TOP);
            deletelayoutParams.addRule(ALIGN_PARENT_LEFT);

            this.delete_iv.setLayoutParams(deletelayoutParams);
            this.delete_iv.setOnClickListener(view -> {
                DeleteView();
            });
            addView(this.rotate_iv);
            this.rotate_iv.setLayoutParams(layoutParams3);
            this.rotate_iv.setOnTouchListener(this.rTouchListener);
            addView(this.scale_iv);
            this.scale_iv.setLayoutParams(layoutParams2);
            this.scale_iv.setTag("scale_iv");
            this.scale_iv.setOnTouchListener(this.mTouchListener1);
            this.zoomOutScale = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
            this.zoomInScale = AnimationUtils.loadAnimation(getContext(), R.anim.textlib_scale_zoom_in);
            initGD();
            this.isMultiTouchEnabled = setDefaultTouchListener(true);


        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void DeleteView() {
        final ViewGroup viewGroup = (ViewGroup) AutofitTextRel.this.getParent();
        zoomInScale.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                viewGroup.removeView(AutofitTextRel.this);
            }
        });
        startAnimation(AutofitTextRel.this.zoomInScale);
        background_iv.startAnimation(AutofitTextRel.this.zoomInScale);
        setBorderVisibility(false);
        if (listener != null) {
            listener.onDelete();
        }
    }

    public void applyLetterSpacing(float f) {
        latterSpacing = (int) f;
        if (this.text != null) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < this.text.length()) {
                sb.append("" + this.text.charAt(i));
                i++;
                if (i < this.text.length()) {
                    sb.append(" ");
                }
            }
            SpannableString spannableString = new SpannableString(sb.toString());
            if (sb.toString().length() > 1) {
                for (int i2 = 1; i2 < sb.toString().length(); i2 += 2) {
                    spannableString.setSpan(new ScaleXSpan((1.0f + f) / 10.0f), i2, i2 + 1, 33);
                }
            }
            this.autoResizeTextView.setText(spannableString, TextView.BufferType.SPANNABLE);
        }
    }

    public void applyLineSpacing(float f) {
        lineSpacing = (int) f;
        this.autoResizeTextView.setLineSpacing(f, 1.0f);
    }

    public void setFontSize(float f) {
        //this.autoResizeTextView.setTextSize(f);
    }

    public float getFontSize(){
        return autoResizeTextView.getTextSize();
    }

    public boolean isBold() {
        return isBold;
    }

    public void setBoldFont() {
        try {
            if (fontName != null && !fontName.equals("")){
                String fontPath = getAppFolder(context)+"font";
              //  String fontPath = "font";
                File file1 = new File(fontPath);
                if (new File(fontPath,fontName).exists()){
                    Log.d("setTextFont___","Bold File => "+file1.getAbsolutePath() + "/" + fontName);
                    this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromFile(file1.getAbsolutePath() + "/" + fontName), Typeface.BOLD));
                }else{
                    Log.d("setTextFont___","Bold Asset => "+file1.getAbsolutePath() + "/" + fontName);
                    this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromAsset(this.context.getAssets(), "font/"+fontName),Typeface.BOLD));
                }
            }else {
                this.autoResizeTextView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
            isBold = true;
            Log.d("setTextFont___","Bold");
        } catch (Exception e) {
            Log.d("setTextFont___","Bold => "+e.getMessage());
        }
    }

    public void setNormalFont() {
        try {
            if (fontName != null && !fontName.isEmpty()){
               String fontPath = getAppFolder(context)+"font";
               // String fontPath = "font";
                File file1 = new File(fontPath);
                if (new File(fontPath,fontName).exists()){
                    this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromFile(file1.getAbsolutePath() + "/" + fontName), Typeface.NORMAL));
                }else{
                    this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromAsset(this.context.getAssets(), "font/"+fontName),Typeface.NORMAL));
                }
            }else{
                this.autoResizeTextView.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            }
            isBold = false;
            Log.d("setTextFont___","Normal");
        } catch (Exception e) {
            Log.d("setTextFont___","Normal => "+e.getMessage());
        }
    }

    public static String toTitleCase(String string) {
        // Check if String is null
        if (string == null) {
            return null;
        }
        boolean whiteSpace = true;
        StringBuilder builder = new StringBuilder(string); // String builder to store string
        final int builderLength = builder.length();
        // Loop through builder
        for (int i = 0; i < builderLength; ++i) {
            char c = builder.charAt(i); // Get character at builders position
            if (whiteSpace) {
                // Check if character is not white space
                if (!Character.isWhitespace(c)) {
                    // Convert to title case and leave whitespace mode.
                    builder.setCharAt(i, Character.toTitleCase(c));
                    whiteSpace = false;
                }
            } else if (Character.isWhitespace(c)) {
                whiteSpace = true; // Set character is white space
            } else {
                builder.setCharAt(i, Character.toLowerCase(c)); // Set character to lowercase
            }
        }

        return builder.toString(); // Return builders text
    }

    public void setItalicFont() {
        if (isItalic){
            if (isBold){
                setBoldFont();
            }else {
                setNormalFont();
            }
            isItalic = false;
        }else {
            try {
                String fontPath = getAppFolder(context)+"font";
             //   String fontPath = "font";
                File file1 = new File(fontPath);
                if (new File(fontPath,fontName).exists()){
                    if (isBold){
                        this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromFile(file1.getAbsolutePath() + "/" + fontName), Typeface.BOLD_ITALIC));
                    }else{
                        this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromFile(file1.getAbsolutePath() + "/" + fontName), Typeface.ITALIC));
                    }

                }else{
                    if (isBold){
                        this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromAsset(this.context.getAssets(), "font/"+fontName),Typeface.BOLD_ITALIC));
                    }else{
                        this.autoResizeTextView.setTypeface(Typeface.create(Typeface.createFromAsset(this.context.getAssets(), "font/"+fontName),Typeface.ITALIC));
                    }
                }
                isItalic = true;
            } catch (Exception e) {
                Log.d("setTextFont___","Bold => "+e.getMessage());
            }
        }
    }


    public void setLeftAlignMent() {
        justification = "left";
        this.autoResizeTextView.setGravity(19);
    }

    public void setCenterAlignMent() {
        justification = "center";
        this.autoResizeTextView.setGravity(17);
    }

    public void setRightAlignMent() {
        justification = "right";
        this.autoResizeTextView.setGravity(21);
    }

    public void setDefaultTouchListenerNew(boolean z) {
        setOnTouchListener(null);
    }

    public boolean setDefaultTouchListener(boolean z) {
        if (z) {
            setOnTouchListener(new MultiTouchListener().enableRotation(true).setOnTouchCallbackListener(this).setGestureListener(this.gd));
            return true;
        }else{
            setOnTouchListener(new OnTouchListener() {
                private static final long DOUBLE_CLICK_TIME_DELTA = 300; // Time in milliseconds between two clicks to consider as a double-click
                private long lastClickTime = 0;
                private boolean waitingForSecondClick = false;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (waitingForSecondClick && System.currentTimeMillis() - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                                // Double click detected
                                onDoubleClick();
                                waitingForSecondClick = false;
                            } else {
                                // Not a double click, waiting for the second click
                                waitingForSecondClick = true;
                                lastClickTime = System.currentTimeMillis();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!waitingForSecondClick) {
                                // Single click detected
                                onSingleClick();
                            }
                            break;
                    }
                    return true;
                }

                private void onSingleClick() {
                    // Handle single click
                }

                private void onDoubleClick() {
                    isMultiTouchEnabled = setDefaultTouchListener(true);
                }
            });
        }
        return false;
    }

    public boolean getBorderVisibility() {
        return this.isBorderVisible;
    }

    public void setBorderVisibility(boolean z) {
        this.isBorderVisible = z;
        if (!z) {
            this.border_iv.setVisibility(GONE);
            this.scale_iv.setVisibility(GONE);
            this.delete_iv.setVisibility(GONE);
            this.rotate_iv.setVisibility(GONE);
            setBackgroundResource(0);
        } else if (this.border_iv.getVisibility() != VISIBLE) {
            this.border_iv.setVisibility(VISIBLE);
            this.scale_iv.setVisibility(VISIBLE);
            this.delete_iv.setVisibility(VISIBLE);
            this.rotate_iv.setVisibility(VISIBLE);
            setBackgroundResource(R.drawable.sticker_border_gray);
        }
    }

    public String getText() {
        return this.autoResizeTextView.getText().toString();
    }

    public void setText(String str) {
        this.autoResizeTextView.setText(str);
        this.text = str;
        if (playAnimation) {
            this.autoResizeTextView.startAnimation(this.zoomOutScale);
        }
    }

    public void setTextFont(String str) {
        try {
           String fontPath = getAppFolder(context)+"font";
         //   String fontPath = "font";
            File file1 = new File(fontPath);
            if (new File(fontPath,str).exists()){
                this.autoResizeTextView.setTypeface(Typeface.createFromFile(file1.getAbsolutePath() + "/" + str));
                Log.d("setTextFont___","File -> "+file1.getAbsolutePath() + "/" + str);
            }else{
                Log.d("setTextFont___","Asset -> "+str);
                this.autoResizeTextView.setTypeface(Typeface.createFromAsset(this.context.getAssets(), "font/"+str));
            }
            this.fontName = str;
        } catch (Exception e) {
            Log.d("setTextFont___","E => "+e.getMessage());
        }
    }

    public int getTextColor() {
        return this.tColor;
    }

    public void setTextColor(int i) {
        this.autoResizeTextView.setTextColor(i);
        this.tColor = i;
    }

    public void setTextOutlLine(int i) {
        this.outersize = i;
        this.autoResizeTextView.setOutlineSize(i);
    }

    public void setTextOutlineColor(int i) {
        this.outercolor = i;
        this.autoResizeTextView.setOutlineColor(i);
    }

    public int getTextAlpha() {
        return this.tAlpha;
    }

    public void setTextAlpha(int i) {
        this.autoResizeTextView.setAlpha(((float) i) / 100.0f);
        this.tAlpha = i;
    }

    public boolean isFrameItem() {
        return isFrameItem;
    }

    public void setFrameItem(boolean b) {
        isFrameItem = b;
    }

    public void deleteView() {
        DeleteView();
    }

    public int getTextShadowColor() {
        return this.shadowColor;
    }

    public void setTextShadowColor(int i) {
        this.shadowColor = i;
        this.shadowColor = ColorUtils.setAlphaComponent(this.shadowColor, this.shadowColorProgress);
        this.autoResizeTextView.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public int getTextShadowOpacity() {
        return this.shadowColorProgress;
    }

    public int getTextShadowProg() {
        return this.shadowProg;
    }

    public void setTextShadowOpacity(int i) {
        this.shadowColorProgress = i;
        this.shadowColor = ColorUtils.setAlphaComponent(this.shadowColor, i);
        this.autoResizeTextView.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public void setLeftRightShadow(float f) {
        this.leftRightShadow = f;
        this.autoResizeTextView.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public void setTopBottomShadow(float f) {
        this.topBottomShadow = f;
        this.autoResizeTextView.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public float getLeftRightShadow() {
        return this.leftRightShadow;
    }

    public void setTextShadowProg(int i) {
        this.shadowProg = i;
        this.autoResizeTextView.setShadowLayer((float) this.shadowProg, this.leftRightShadow, this.topBottomShadow, this.shadowColor);
    }

    public String getBgDrawable() {
        return this.bgDrawable;
    }

    public void setBgDrawable(String str) {
        this.bgDrawable = str;
        this.bgColor = 0;
        this.background_iv.setImageBitmap(getTiledBitmap(this.context, getResources().getIdentifier(str, "drawable", this.context.getPackageName()), this.wi, this.he));
        this.background_iv.setBackgroundColor(this.bgColor);
    }

    public int getBgColor() {
        return this.bgColor;
    }

    public void setBgColor(int i) {
        this.bgDrawable = "0";
        this.bgColor = i;
        this.background_iv.setImageBitmap((Bitmap) null);
        this.background_iv.setBackgroundColor(i);
        Log.d("bgColor", "setBgColor: "+bgColor);
    }

    public int getLeftSadow() {
        return (int) this.leftRightShadow;
    }

    public int getTopBottomSadow() {
        return (int) this.topBottomShadow;
    }

    public int getOutercolor() {
        return this.outercolor;
    }

    public int getOutersize() {
        return this.outersize;
    }

    public int getBgAlpha() {
        return this.bgAlpha;
    }

    public void setBgAlpha(int i) {
        this.background_iv.setAlpha(((float) i) / 100.0f);
        this.bgAlpha = i;
    }

    public AutofitTextInfo getTextInfo() {
//        Log.d( "getTextInfo__",NAME);

        AutofitTextInfo autofitTextInfo = new AutofitTextInfo();
        autofitTextInfo.setNAME(this.NAME);
        autofitTextInfo.setPOS_X(getX());
        autofitTextInfo.setPOS_Y(getY());
        autofitTextInfo.setWIDTH(this.wi);
        autofitTextInfo.setHEIGHT(this.he);
        autofitTextInfo.setTEXT(this.text);
        autofitTextInfo.setFONT_NAME(this.fontName);
        autofitTextInfo.setTEXT_COLOR(this.tColor);
        autofitTextInfo.setTEXT_ALPHA(this.tAlpha);
        autofitTextInfo.setSHADOW_COLOR(this.shadowColor);
        autofitTextInfo.setSHADOW_PROG(this.shadowProg);
        autofitTextInfo.setSHADOW_COLOR_PROG(this.shadowColorProgress);
        autofitTextInfo.setBG_COLOR(this.bgColor);
        autofitTextInfo.setBG_DRAWABLE(this.bgDrawable);
        autofitTextInfo.setBG_ALPHA(this.bgAlpha);
        autofitTextInfo.setROTATION(getRotation());
        autofitTextInfo.setXRotateProg(this.xRotateProg);
        autofitTextInfo.setYRotateProg(this.yRotateProg);
        autofitTextInfo.setZRotateProg(this.zRotateProg);
        autofitTextInfo.setCurveRotateProg(this.progress);
        autofitTextInfo.setFONT_JUSTIFY(this.justification);
        autofitTextInfo.setFONT_WEIGHT(isBold ? "bold" : "");
        autofitTextInfo.setLeftRighShadow(this.leftRightShadow);
        autofitTextInfo.setTopBottomShadow(this.topBottomShadow);
        autofitTextInfo.setOutLineSize(this.outersize);
        autofitTextInfo.setOutLineColor(this.outercolor);
        autofitTextInfo.setLeftRighShadow(this.outercolor);
        autofitTextInfo.setLineSpacing(this.lineSpacing);
        autofitTextInfo.setLetterSpacing(this.latterSpacing);
        return autofitTextInfo;
    }

    public void setTextInfo(AutofitTextInfo autofitTextInfo, boolean z) {
//        Log.d( "getTextInfo__",autofitTextInfo.getNAME());
        this.wi = autofitTextInfo.getWIDTH();
        this.he = autofitTextInfo.getHEIGHT();
        this.text = autofitTextInfo.getTEXT();
        this.fontName = autofitTextInfo.getFONT_NAME();
        this.tColor = autofitTextInfo.getTEXT_COLOR();
        this.tAlpha = autofitTextInfo.getTEXT_ALPHA();
        this.shadowColor = autofitTextInfo.getSHADOW_COLOR();
        this.shadowProg = autofitTextInfo.getSHADOW_PROG();
        this.bgColor = autofitTextInfo.getBG_COLOR();
        this.bgDrawable = autofitTextInfo.getBG_DRAWABLE();
        this.bgAlpha = autofitTextInfo.getBG_ALPHA();
        this.justification = autofitTextInfo.getFONT_JUSTIFY();
        this.NAME = autofitTextInfo.getNAME();
        this.isBold = Objects.equals(autofitTextInfo.getFONT_WEIGHT(), "bold");
        this.shadowColorProgress = autofitTextInfo.getSHADOW_COLOR_PROG();
        this.lineSpacing = autofitTextInfo.getLineSpacing();
        this.latterSpacing = autofitTextInfo.getLetterSpacing();

        setText(this.text);

        if (fontName != null && !fontName.isEmpty()){
            setTextFont(this.fontName);
        }
        if (isBold){
            setBoldFont();
        }

        setTextColor(this.tColor);
        setTextAlpha(this.tAlpha);

        this.outersize = autofitTextInfo.getOutLineSize();
        setTextOutlLine(this.outersize);
        this.outercolor = autofitTextInfo.getOutLineColor();
        setTextOutlineColor(this.outercolor);
        setTextShadowColor(this.shadowColor);
        this.leftRightShadow = autofitTextInfo.getLeftRighShadow();
        this.topBottomShadow = autofitTextInfo.getTopBottomShadow();
        setTextShadowProg(this.shadowProg);
        setTextShadowOpacity(this.shadowColorProgress);
        int i = this.bgColor;
        if (i != 0) {
            setBgColor(i);
        } else {
            this.background_iv.setBackgroundColor(0);
        }
        if (this.bgDrawable.equals("0")) {
            this.background_iv.setImageBitmap((Bitmap) null);
        } else {
            setBgDrawable(this.bgDrawable);
        }
        setBgAlpha(this.bgAlpha);
        setRotation(autofitTextInfo.getROTATION());

        if (this.field_two.equals("")) {
            getLayoutParams().width = this.wi;
            getLayoutParams().height = this.he;
            setX(autofitTextInfo.getPOS_X());
            setY(autofitTextInfo.getPOS_Y());
            return;
        }
        String[] split = this.field_two.split(",");
        int parseInt = Integer.parseInt(split[0]);
        int parseInt2 = Integer.parseInt(split[1]);
        ((LayoutParams) getLayoutParams()).leftMargin = parseInt;
        ((LayoutParams) getLayoutParams()).topMargin = parseInt2;
        getLayoutParams().width = this.wi;
        getLayoutParams().height = this.he;

        setX(autofitTextInfo.getPOS_X() + ((float) (parseInt * -1)));
        setY(autofitTextInfo.getPOS_Y() + ((float) (parseInt2 * -1)));

        if (lineSpacing != 0){
            applyLineSpacing(lineSpacing);
        }

        if (latterSpacing != 0){
            applyLetterSpacing(latterSpacing);
        }
    }

    public void optimize(float f, float f2) {
        setX(getX() * f);
        setY(getY() * f2);
        getLayoutParams().width = (int) (((float) this.wi) * f);
        getLayoutParams().height = (int) (((float) this.he) * f2);
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

    public int dpToPx(Context context2, int i) {
        context2.getResources();
        return (int) (Resources.getSystem().getDisplayMetrics().density * ((float) i));
    }

    private Bitmap getTiledBitmap(Context context2, int i, int i2, int i3) {
        Rect rect = new Rect(0, 0, i2, i3);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(BitmapFactory.decodeResource(context2.getResources(), i, new BitmapFactory.Options()), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        Bitmap createBitmap = Bitmap.createBitmap(i2, i3, Bitmap.Config.ARGB_8888);
        new Canvas(createBitmap).drawRect(rect, paint);
        return createBitmap;
    }

    private void initGD() {
        this.gd = new GestureDetector(this.context, new SimpleListner());
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

    public void onTouchUpClick(View view) {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMoveUpClick(view);
        }
    }

    public float getNewX(float f) {
        return ((float) this.width) * (f / ((float) this.sw));
    }

    public float getNewY(float f) {
        return ((float) this.height) * (f / ((float) this.sh));
    }

    public void clickToSaveWork() {
        TouchEventListener touchEventListener = this.listener;
        if (touchEventListener != null) {
            touchEventListener.onTouchMoveUpClick(this);
        }
    }

    public interface TouchEventListener {
        void onDelete();

        void onDoubleTap();

        void onEdit(View view, Uri uri);

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
    }

    class Touch implements OnTouchListener {

        Touch() {}

        public boolean onTouch(View view, MotionEvent motionEvent) {

            AutofitTextRel autofitTextRel = (AutofitTextRel) view.getParent();
            LayoutParams layoutParams = (LayoutParams) AutofitTextRel.this.getLayoutParams();
            int rawX = (int) motionEvent.getRawX();
            int rawY = (int) motionEvent.getRawY();
            int action = motionEvent.getAction();
            Log.d("OnTouchListener -> ", "" + action);

            if (action == 0) {
                if (autofitTextRel != null) {
                    autofitTextRel.requestDisallowInterceptTouchEvent(true);
                }
                if (AutofitTextRel.this.listener != null) {
                    AutofitTextRel.this.listener.onScaleDown(AutofitTextRel.this);
                }
                AutofitTextRel.this.invalidate();
                AutofitTextRel.this.basex = rawX;
                AutofitTextRel.this.basey = rawY;
                AutofitTextRel.this.basew = AutofitTextRel.this.getWidth();
                AutofitTextRel.this.baseh = AutofitTextRel.this.getHeight();
                AutofitTextRel.this.getLocationOnScreen(new int[2]);
                AutofitTextRel.this.margl = layoutParams.leftMargin;
                AutofitTextRel.this.margt = layoutParams.topMargin;
                AutofitTextRel.this.currentState = 0;
            } else if (action == 1) {
                AutofitTextRel.this.wi = AutofitTextRel.this.getLayoutParams().width;
                AutofitTextRel.this.he = AutofitTextRel.this.getLayoutParams().height;
                AutofitTextRel.this.leftMargin = ((LayoutParams) AutofitTextRel.this.getLayoutParams()).leftMargin;
                AutofitTextRel.this.topMargin = ((LayoutParams) AutofitTextRel.this.getLayoutParams()).topMargin;
                if (AutofitTextRel.this.currentState == 3) {
                    AutofitTextRel.this.clickToSaveWork();
                }
                AutofitTextRel.this.currentState = 2;
                if (AutofitTextRel.this.listener != null) {
                    AutofitTextRel.this.listener.onScaleUp(AutofitTextRel.this);
                }
            } else if (action == 2) {
                if (autofitTextRel != null) {
                    autofitTextRel.requestDisallowInterceptTouchEvent(true);
                }
                if (AutofitTextRel.this.listener != null) {
                    AutofitTextRel.this.listener.onScaleMove(AutofitTextRel.this);
                }
                float degrees = (float) Math.toDegrees(Math.atan2((double) (rawY - AutofitTextRel.this.basey), (double) (rawX - AutofitTextRel.this.basex)));
                if (degrees < 0.0f) {
                    degrees += 360.0f;
                }
                int i = rawX - AutofitTextRel.this.basex;
                int i2 = rawY - AutofitTextRel.this.basey;
                int i3 = i2 * i2;
                int sqrt = (int) (Math.sqrt((double) ((i * i) + i3)) * Math.cos(Math.toRadians((double) (degrees - AutofitTextRel.this.getRotation()))));
                int sqrt2 = (int) (Math.sqrt((double) ((sqrt * sqrt) + i3)) * Math.sin(Math.toRadians((double) (degrees - AutofitTextRel.this.getRotation()))));
                int i4 = (sqrt * 2) + AutofitTextRel.this.basew;
                int i5 = (sqrt2 * 2) + AutofitTextRel.this.baseh;
                if (i4 > AutofitTextRel.this.f27s) {
                    layoutParams.width = i4;
                    layoutParams.leftMargin = AutofitTextRel.this.margl - sqrt;
                }
                if (i5 > AutofitTextRel.this.f27s) {
                    layoutParams.height = i5;
                    layoutParams.topMargin = AutofitTextRel.this.margt - sqrt2;
                }
                AutofitTextRel.this.setLayoutParams(layoutParams);
                AutofitTextRel.this.currentState = 3;
                if (!AutofitTextRel.this.bgDrawable.equals("0")) {
                    AutofitTextRel.this.wi = AutofitTextRel.this.getLayoutParams().width;
                    AutofitTextRel.this.he = AutofitTextRel.this.getLayoutParams().height;
                    AutofitTextRel.this.setBgDrawable(AutofitTextRel.this.bgDrawable);
                }
            }
            return true;
        }
    }


    class SimpleListner extends GestureDetector.SimpleOnGestureListener {
        SimpleListner() {
        }

        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return true;
        }

        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        public boolean onDoubleTap(MotionEvent motionEvent) {
            if (AutofitTextRel.this.listener == null) {
                return true;
            }
            AutofitTextRel.this.listener.onDoubleTap();
            return true;
        }

        public void onLongPress(MotionEvent motionEvent) {
            super.onLongPress(motionEvent);
        }
    }
}
