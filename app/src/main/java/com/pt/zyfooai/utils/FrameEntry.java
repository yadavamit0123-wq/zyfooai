package com.pt.zyfooai.utils;

import com.pt.zyfooai.model.FrameConfig;

/**
 * One slot in the frame carousel — legacy XML layout or PNG overlay frame.
 */
public final class FrameEntry {

    private final int layoutResId;
    private final FrameConfig dynamicConfig;

    private FrameEntry(int layoutResId, FrameConfig dynamicConfig) {
        this.layoutResId = layoutResId;
        this.dynamicConfig = dynamicConfig;
    }

    public static FrameEntry xml(int layoutResId) {
        return new FrameEntry(layoutResId, null);
    }

    public static FrameEntry dynamic(int layoutResId, FrameConfig config) {
        return new FrameEntry(layoutResId, config);
    }

    public boolean isDynamic() {
        return dynamicConfig != null;
    }

    public int getLayoutResId() {
        return layoutResId;
    }

    public FrameConfig getDynamicConfig() {
        return dynamicConfig;
    }
}
