package com.growwthapps.dailypost.v2.View;

import android.content.res.Resources;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class MultiTouchListener implements View.OnTouchListener {

    public boolean isRotateEnabled = true;
    public boolean isRotationEnabled = false;
    public boolean isTranslateEnabled = true;
    public float maximumScale = 8.0f;
    public float minimumScale = 0.5f;
    boolean bt = false;
    int currentMode = 0;
    GestureDetector gd = null;
    private TouchCallbackListener listener = null;
    private int mActivePointerId = -1;
    private float mPrevX;
    private float mPrevY;
    private ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(new ScaleGestureListener());

    private static float adjustAngle(float f) {
        return f > 180.0f ? f - 360.0f : f < -180.0f ? f + 360.0f : f;
    }

    private void adjustTranslation(View view, float f, float f2) {
        float f3;
        boolean z = true;
        float[] fArr = {f, f2};
        view.getMatrix().mapVectors(fArr);
        float translationY = view.getTranslationY() + fArr[1];
        view.setTranslationX(view.getTranslationX() + fArr[0]);
        view.setTranslationY(translationY);
        StickerView stickerView = (StickerView) view;
        float mainWidth = stickerView.getMainWidth();
        float mainHeight = stickerView.getMainHeight();
        float width = (float) (view.getWidth() / 2);
        float height = (float) (view.getHeight() / 2);
        int y = (int) (view.getY() + height);
        float x = (float) ((int) (view.getX() + width));
        float f4 = mainWidth / 2.0f;
        float f5 = (float) ((int) (Resources.getSystem().getDisplayMetrics().density * 5.0f));
        if (x <= f4 - f5 || x >= f4 + f5) {
            z = false;
        } else {
            view.setX(f4 - width);
        }
        float f6 = (float) y;
        float f7 = mainHeight / 2.0f;
        if (f6 <= f7 - f5 || f6 >= f5 + f7) {
            f3 = 0.0f;
        } else {
            view.setY(f7 - height);
            f3 = Float.MIN_VALUE;
        }
        if (z && f3 != 0.0f) {
            TouchCallbackListener touchCallbackListener = this.listener;
            if (touchCallbackListener != null) {
                touchCallbackListener.onMidXY(view);
            }
        } else if (z) {
            TouchCallbackListener touchCallbackListener2 = this.listener;
            if (touchCallbackListener2 != null) {
                touchCallbackListener2.onMidX(view);
            }
        } else if (f3 != 0.0f) {
            TouchCallbackListener touchCallbackListener3 = this.listener;
            if (touchCallbackListener3 != null) {
                touchCallbackListener3.onMidY(view);
            }
        } else {
            TouchCallbackListener touchCallbackListener4 = this.listener;
            if (touchCallbackListener4 != null) {
                touchCallbackListener4.onXY(view);
            }
        }
        float rotation = view.getRotation();
        if (Math.abs(90.0f - Math.abs(rotation)) <= 5.0f) {
            rotation = rotation > 0.0f ? 90.0f : -90.0f;
        }
        if (Math.abs(0.0f - Math.abs(rotation)) <= 5.0f) {
            rotation = rotation > 0.0f ? 0.0f : -0.0f;
        }
        if (Math.abs(180.0f - Math.abs(rotation)) <= 5.0f) {
            rotation = rotation > 0.0f ? 180.0f : -180.0f;
        }
        view.setRotation(rotation);
    }

    public MultiTouchListener setOnTouchCallbackListener(TouchCallbackListener touchCallbackListener) {
        this.listener = touchCallbackListener;
        return this;
    }

    public MultiTouchListener enableRotation(boolean z) {
        this.isRotationEnabled = z;
        return this;
    }

    public void move(View view, TransformInfo transformInfo) {
        if (this.isRotationEnabled) {
            view.setRotation(adjustAngle(view.getRotation() + transformInfo.deltaAngle));
        }
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        TouchCallbackListener touchCallbackListener;
        this.mScaleGestureDetector.onTouchEvent(view, motionEvent);
        RelativeLayout relativeLayout = (RelativeLayout) view.getParent();
        GestureDetector gestureDetector = this.gd;
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(motionEvent);
        }
        if (this.isTranslateEnabled) {
            int action = motionEvent.getAction();
            int actionMasked = motionEvent.getActionMasked() & action;
            int i = 0;
            if (actionMasked == 0) {
                this.currentMode = 0;
                if (relativeLayout != null) {
                    relativeLayout.requestDisallowInterceptTouchEvent(true);
                }
                TouchCallbackListener touchCallbackListener2 = this.listener;
                if (touchCallbackListener2 != null) {
                    touchCallbackListener2.onTouchCallback(view);
                }


//                view.bringToFront();


                if (view instanceof StickerView) {
                    ((StickerView) view).setBorderVisibility(true);
                }
                this.mPrevX = motionEvent.getX();
                this.mPrevY = motionEvent.getY();
                this.mActivePointerId = motionEvent.getPointerId(0);
            } else if (actionMasked == 1) {
                this.mActivePointerId = -1;
                TouchCallbackListener touchCallbackListener3 = this.listener;
                if (touchCallbackListener3 != null) {
                    touchCallbackListener3.onTouchUpCallback(view);
                }
                if (this.currentMode == 2 && (touchCallbackListener = this.listener) != null) {
                    touchCallbackListener.onTouchUpClick(view);
                }
                this.currentMode = 1;
                float rotation = view.getRotation();
                if (Math.abs(90.0f - Math.abs(rotation)) <= 5.0f) {
                    rotation = rotation > 0.0f ? 90.0f : -90.0f;
                }
                if (Math.abs(0.0f - Math.abs(rotation)) <= 5.0f) {
                    rotation = rotation > 0.0f ? 0.0f : -0.0f;
                }
                if (Math.abs(180.0f - Math.abs(rotation)) <= 5.0f) {
                    rotation = rotation > 0.0f ? 180.0f : -180.0f;
                }
                view.setRotation(rotation);
            } else if (actionMasked == 2) {
                this.currentMode = 2;
                if (relativeLayout != null) {
                    relativeLayout.requestDisallowInterceptTouchEvent(true);
                }
                TouchCallbackListener touchCallbackListener4 = this.listener;
                if (touchCallbackListener4 != null) {
                    touchCallbackListener4.onTouchMoveCallback(view);
                }
                int findPointerIndex = motionEvent.findPointerIndex(this.mActivePointerId);
                if (findPointerIndex != -1) {
                    float x = motionEvent.getX(findPointerIndex);
                    float y = motionEvent.getY(findPointerIndex);
                    if (!this.mScaleGestureDetector.isInProgress()) {
                        adjustTranslation(view, x - this.mPrevX, y - this.mPrevY);
                    }
                }
            } else if (actionMasked == 3) {
                this.mActivePointerId = -1;
            } else if (actionMasked == 6) {
                int i2 = (65280 & action) >> 8;
                if (motionEvent.getPointerId(i2) == this.mActivePointerId) {
                    if (i2 == 0) {
                        i = 1;
                    }
                    this.mPrevX = motionEvent.getX(i);
                    this.mPrevY = motionEvent.getY(i);
                    this.mActivePointerId = motionEvent.getPointerId(i);
                }
            }
        }
        return true;
    }

    public interface TouchCallbackListener {
        void onMidX(View view);

        void onMidXY(View view);

        void onMidY(View view);

        void onTouchCallback(View view);

        void onTouchMoveCallback(View view);

        void onTouchUpCallback(View view);

        void onTouchUpClick(View view);

        void onXY(View view);
    }

    private class TransformInfo {
        public float deltaAngle;
        public float deltaScale;
        public float deltaX;
        public float deltaY;
        public float maximumScale;
        public float minimumScale;
        public float pivotX;
        public float pivotY;

        private TransformInfo() {
        }
    }

    private class ScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private float mPivotX;
        private float mPivotY;
        private Vector2D mPrevSpanVector;

        private ScaleGestureListener() {
            this.mPrevSpanVector = new Vector2D();
        }

        @Override
        public boolean onScaleBegin(View view, ScaleGestureDetector scaleGestureDetector) {
            this.mPivotX = scaleGestureDetector.getFocusX();
            this.mPivotY = scaleGestureDetector.getFocusY();
            this.mPrevSpanVector.set(scaleGestureDetector.getCurrentSpanVector());
            return true;
        }

        @Override
        public boolean onScale(View view, ScaleGestureDetector scaleGestureDetector) {
            TransformInfo transformInfo = new TransformInfo();
            float f = 0.0f;
            transformInfo.deltaAngle = MultiTouchListener.this.isRotateEnabled ? Vector2D.getAngle(this.mPrevSpanVector, scaleGestureDetector.getCurrentSpanVector()) : 0.0f;
            transformInfo.deltaX = MultiTouchListener.this.isTranslateEnabled ? scaleGestureDetector.getFocusX() - this.mPivotX : 0.0f;
            if (MultiTouchListener.this.isTranslateEnabled) {
                f = scaleGestureDetector.getFocusY() - this.mPivotY;
            }
            transformInfo.deltaY = f;
            transformInfo.pivotX = this.mPivotX;
            transformInfo.pivotY = this.mPivotY;
            transformInfo.minimumScale = MultiTouchListener.this.minimumScale;
            transformInfo.maximumScale = MultiTouchListener.this.maximumScale;
            MultiTouchListener.this.move(view, transformInfo);
            return false;
        }
    }
}
