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
    public FrameSafeZoneConfig safeZone;
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

    /** Top inset for photo/video inside the PNG window. */
    public int mediaTopInsetPercent() {
        if (safeZone != null && safeZone.topPercent > 0) {
            return safeZone.topPercent;
        }
        if (footer != null && !footer.enabled) {
            return resolvedMediaType() == FrameMediaType.REELS ? 5 : 16;
        }
        return resolvedMediaType() == FrameMediaType.REELS ? 0 : 0;
    }

    /** Bottom inset for photo/video inside the PNG window. */
    public int mediaBottomInsetPercent() {
        if (safeZone != null && safeZone.bottomPercent > 0) {
            return safeZone.bottomPercent;
        }
        if (footer != null && footer.enabled) {
            return footer.heightPercent > 0 ? footer.heightPercent : 12;
        }
        return resolvedMediaType() == FrameMediaType.REELS ? 12 : 20;
    }

    public int mediaLeftInsetPercent() {
        if (safeZone != null && safeZone.leftPercent > 0) {
            return safeZone.leftPercent;
        }
        return 0;
    }

    public int mediaRightInsetPercent() {
        if (safeZone != null && safeZone.rightPercent > 0) {
            return safeZone.rightPercent;
        }
        return 0;
    }
}

