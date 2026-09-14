package com.pt.zyfooai.utils;

import com.pt.zyfooai.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Modern frame pack only — classic flat bars retired (Phase Next).
 */
public final class ModernFrameCatalog {

    public static final int INDEX_STICKER_PROMPT = 0;
    public static final int INDEX_STICKER_NAMETAG = 1;
    public static final int INDEX_STICKER_GLASS_CHIP = 2;
    public static final int INDEX_STICKER_CORNER = 3;
    public static final int INDEX_STICKER_DARK_GOLD = 4;
    public static final int INDEX_STICKER_QUOTE = 5;
    public static final int INDEX_STICKER_REELS = 6;
    public static final int INDEX_STICKER_QR = 7;

    private ModernFrameCatalog() {
    }

    public static List<Integer> imageFrameLayouts() {
        List<Integer> layouts = new ArrayList<>();
        layouts.add(R.layout.layout_frame_sticker_1);
        layouts.add(R.layout.layout_frame_sticker_2);
        layouts.add(R.layout.layout_frame_sticker_3);
        layouts.add(R.layout.layout_frame_sticker_4);
        layouts.add(R.layout.layout_frame_sticker_5);
        layouts.add(R.layout.layout_frame_sticker_quote);
        layouts.add(R.layout.layout_frame_sticker_reels);
        layouts.add(R.layout.layout_frame_sticker_qr);
        layouts.add(R.layout.layout_frame_glass_1);
        layouts.add(R.layout.layout_frame_glass_2);
        layouts.add(R.layout.layout_frame_gradient_1);
        layouts.add(R.layout.layout_frame_gradient_2);
        return Collections.unmodifiableList(layouts);
    }

    public static List<Integer> videoFrameLayouts() {
        List<Integer> layouts = new ArrayList<>();
        layouts.add(R.layout.layout_video_frame_sticker_1);
        layouts.add(R.layout.layout_video_frame_sticker_2);
        layouts.add(R.layout.layout_video_frame_sticker_3);
        layouts.add(R.layout.layout_video_frame_sticker_4);
        layouts.add(R.layout.layout_video_frame_sticker_5);
        layouts.add(R.layout.layout_video_frame_sticker_quote);
        layouts.add(R.layout.layout_video_frame_sticker_reels);
        layouts.add(R.layout.layout_video_frame_sticker_qr);
        layouts.add(R.layout.layout_video_frame_glass_1);
        layouts.add(R.layout.layout_video_frame_glass_2);
        layouts.add(R.layout.layout_video_frame_gradient_1);
        layouts.add(R.layout.layout_video_frame_gradient_2);
        return Collections.unmodifiableList(layouts);
    }
}
