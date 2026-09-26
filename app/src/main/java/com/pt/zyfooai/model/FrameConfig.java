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

    /**
     * Canvas aspect from API, with interim defaults: image → 1:1, reels → 9:16.
     */
    public String resolvedAspectRatio() {
        if (aspectRatio != null && !aspectRatio.trim().isEmpty()) {
            return aspectRatio.trim();
        }
        return resolvedMediaType() == FrameMediaType.REELS ? "9:16" : "1:1";
    }

    /** Top inset from API {@link #safeZone} only (percent of frame height). */
    public int mediaTopInsetPercent() {
        return safeZone != null ? Math.max(0, safeZone.topPercent) : 0;
    }

    /** Bottom inset from API safe zone only — use {@link #reservedMediaBottomPercent()} for layout. */
    public int mediaBottomInsetPercent() {
        return safeZone != null ? Math.max(0, safeZone.bottomPercent) : 0;
    }

    public int mediaLeftInsetPercent() {
        return safeZone != null ? Math.max(0, safeZone.leftPercent) : 0;
    }

    public int mediaRightInsetPercent() {
        return safeZone != null ? Math.max(0, safeZone.rightPercent) : 0;
    }

    /**
     * Single bottom reserve for user media: max(safeZone.bottom, footer.heightPercent when enabled).
     * Avoids stacking safe zone and footer strip separately.
     */
    public int reservedMediaBottomPercent() {
        int bottom = mediaBottomInsetPercent();
        if (footer != null && footer.enabled && footer.heightPercent > 0) {
            bottom = Math.max(bottom, footer.heightPercent);
        }
        return bottom;
    }
}
