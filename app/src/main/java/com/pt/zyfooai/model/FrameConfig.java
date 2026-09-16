package com.pt.zyfooai.model;

import com.pt.zyfooai.utils.FrameMediaType;

/**
 * Server/admin frame definition consumed by the PNG overlay engine.
 */
public class FrameConfig {

    public String id;
    public int version = 1;
    public String name;
    public String description;
    public String mediaType;
    public String aspectRatio;
    public String overlayAsset;
    public String overlayUrl;
    public FrameFooterConfig footer;
    public String status;
    public int sortOrder;
    public String updatedAt;

    public FrameMediaType resolvedMediaType() {
        if ("reels".equalsIgnoreCase(mediaType)) {
            return FrameMediaType.REELS;
        }
        return FrameMediaType.IMAGE;
    }

    public String overlaySource() {
        if (overlayUrl != null && !overlayUrl.trim().isEmpty()) {
            return overlayUrl.trim();
        }
        if (overlayAsset != null && !overlayAsset.trim().isEmpty()) {
            return overlayAsset.trim();
        }
        return "";
    }

    public boolean hasRenderableOverlay() {
        return !overlaySource().isEmpty();
    }
}
