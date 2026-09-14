package com.pt.zyfooai.utils;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.HashMap;
import java.util.Map;

public final class QrCodeHelper {

    private QrCodeHelper() {
    }

    public static String payloadForProfile(PreferenceManager preferenceManager) {
        if (preferenceManager == null) {
            return "https://zyfooai.com";
        }
        boolean isBusiness = !"Personal".equals(preferenceManager.getString(Constant.DEFAULT_TYPE));
        if (isBusiness) {
            String whatsapp = digitsOnly(preferenceManager.getString(Constant.BUSINESS_WHATSAPP));
            if (!whatsapp.isEmpty()) {
                return "https://wa.me/" + whatsapp;
            }
            String website = preferenceManager.getString(Constant.BUSINESS_WEBSITE);
            if (website != null && !website.trim().isEmpty()) {
                website = website.trim();
                return website.startsWith("http") ? website : "https://" + website;
            }
            String phone = digitsOnly(preferenceManager.getString(Constant.BUSINESS_NUMBER));
            if (!phone.isEmpty()) {
                return "tel:+" + phone;
            }
        }
        String phone = digitsOnly(preferenceManager.getString(Constant.USER_PHONE));
        if (!phone.isEmpty()) {
            return "tel:+" + phone;
        }
        String name = preferenceManager.getString(Constant.USER_NAME);
        return name != null && !name.isEmpty() ? name : "ZyFoo AI";
    }

    public static Bitmap generateBitmap(String content, int sizePx) {
        if (content == null || content.isEmpty() || sizePx <= 0) {
            return null;
        }
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints);
            Bitmap bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
            for (int x = 0; x < sizePx; x++) {
                for (int y = 0; y < sizePx; y++) {
                    bitmap.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException ignored) {
            return null;
        }
    }

    private static String digitsOnly(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^0-9]", "");
    }
}
