package com.pt.zyfooai.model;

/**
 * Footer styling for server-driven / PNG overlay frames.
 */
public class FrameFooterConfig {

    public boolean enabled = true;
    public int heightPercent = 12;
    public boolean showProfile = true;
    public boolean showName = true;
    public boolean showPhone = true;
    public boolean showWebsite = false;
    public String nameColor = "#FFFFFF";
    public String phoneColor = "#CCCCCC";
    public String bgColor = "#CC000000";
    public String fontSize = "medium";
    /** small | medium | large — avatar size in app-drawn footer (e.g. profile clear reels). */
    public String profileSize = "medium";
}
