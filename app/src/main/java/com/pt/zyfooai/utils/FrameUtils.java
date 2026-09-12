package com.pt.zyfooai.utils;



import com.pt.zyfooai.R;

import java.util.ArrayList;

public class FrameUtils {

    public static String realX = "";
    public static String realY = "";
    public static String calcWidth = "";
    public static String calcHeight = "";

    static int templateRealWidth = 0;
    static int templateRealHeight = 0;


    static PreferenceManager preferenceManager;


    private static ArrayList<String> fontNames = new ArrayList<>();
    private static int downloadId;






    public interface OnLogoDownloadListener {
        void onLogoDownloaded(String logoPath);

        void onLogoDownloadError();
    }



}
