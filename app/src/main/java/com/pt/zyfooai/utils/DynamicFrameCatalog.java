package com.pt.zyfooai.utils;

import android.content.Context;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.FrameConfig;

import java.util.List;

/**
 * Bundled PNG overlay prototypes — replaced by admin/API frames in Phase 4.
 */
public final class DynamicFrameCatalog {

    public static final boolean INCLUDE_TEST_FRAMES = true;

    private static final String TEST_IMAGE_JSON = "frames/test_image.json";
    private static final String TEST_REELS_JSON = "frames/test_reels.json";

    private DynamicFrameCatalog() {
    }

    public static void appendTestFrames(Context context, List<FrameEntry> target, FrameMediaType mediaType) {
        if (!INCLUDE_TEST_FRAMES || context == null || target == null) {
            return;
        }
        FrameConfig config = FrameConfigLoader.fromAsset(
                context,
                mediaType == FrameMediaType.REELS ? TEST_REELS_JSON : TEST_IMAGE_JSON
        );
        if (config == null) {
            return;
        }
        int layoutResId = mediaType == FrameMediaType.REELS
                ? R.layout.layout_video_frame_dynamic
                : R.layout.layout_frame_dynamic;
        target.add(FrameEntry.dynamic(layoutResId, config));
    }

    public static int testFrameCount() {
        return INCLUDE_TEST_FRAMES ? 1 : 0;
    }
}
