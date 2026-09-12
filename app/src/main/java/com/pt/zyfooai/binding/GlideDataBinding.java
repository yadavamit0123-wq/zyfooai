package com.pt.zyfooai.binding;

import static android.view.View.DRAWING_CACHE_QUALITY_HIGH;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pt.zyfooai.AppConfig;
import com.pt.zyfooai.MyApplication;
import com.pt.zyfooai.R;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

public class GlideDataBinding {

    @BindingAdapter("imageURL")
    public static void bindImage(ImageView imageView, String url) {
        Context mContext = imageView.getContext();

        if (isValid(imageView, url)) {
            if (AppConfig.PRE_LOAD_IMAGE) {
                Glide.with(mContext.getApplicationContext())
                        .load(url)
                        .thumbnail(Glide.with(mContext.getApplicationContext()).load(url))
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .dontAnimate()
                        .into(imageView);
            } else {
                Glide.with(mContext.getApplicationContext())
                        .load(url)
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .dontAnimate()
                        .into(imageView);
            }
        }
    }


    @BindingAdapter("circle_imageURL")
    public static void bindImage(CircularImageView imageView, String url) {
        if (isValid(imageView, url)) {
            if (AppConfig.PRE_LOAD_IMAGE) {
                Glide.with(MyApplication.getAppContext()).load(url).thumbnail(Glide.with(MyApplication.getAppContext()).load(url)).placeholder(R.drawable.placeholder).into(imageView);
            } else {
                Glide.with(MyApplication.getAppContext()).load(url).placeholder(R.drawable.placeholder).into(imageView);
            }
        }
    }

    public static Bitmap viewToBitmap(View view) {
        Bitmap createBitmap = null;
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        try {
            createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            return createBitmap;
        } catch (Exception e) {
            return createBitmap;
        } finally {
            view.destroyDrawingCache();
        }
    }


    public static Bitmap viewToBitmap(View view, String ratio) {
        Bitmap createBitmap = null;
        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
        String[] split = ratio.split(":");
        try {
            createBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.draw(new Canvas(createBitmap));
            return createBitmap;
        } catch (Exception e) {
            return createBitmap;
        } finally {
            view.destroyDrawingCache();
        }
    }

    public static Bitmap resizeBitmap(Bitmap originalBitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
    }

    public static Boolean isValid(ImageView imageView, String url) {
        Context mContext = imageView.getContext();
        return !(url == null
                || imageView == null
                || mContext == null
                || url.equals(""));
    }

}
