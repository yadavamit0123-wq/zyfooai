package com.pt.zyfooai.model;

/**
 * Footer styling for server-driven / PNG overlay frames.
 * <p>
 * {@link #heightPercent} = bar thickness (independent of profile size).
 * {@link #profileScalePercent} = avatar width as % of canvas width (preferred over {@link #profileSize}).
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
    /** poppins | montserrat | raleway | playfair | bebas */
    public String fontFamily;
    /** small | medium | large — used when {@link #profileScalePercent} is null. */
    public String profileSize = "medium";
    /**
     * Profile width as percent of canvas width (e.g. 20). Null → fall back to {@link #profileSize}.
     */
    public Integer profileScalePercent;
    /** in_footer | above_footer (Crafto float) */
    public String profilePlacement = "in_footer";
    /**
     * circle | ring_gold | ring_white | rounded | soft_shadow |
     * ring_3d_gold | ring_3d_chrome | ring_3d_neon | ring_3d_glass | ring_3d_rose
     */
    public String profileStyle = "circle";
    /**
     * none | spin_3d | slide_ltr | slide_rtl | slide_ttb | slide_btt |
     * orbit_3d | float_3d | flip_3d | bounce_in | glow_spin
     * Missing / none → static (legacy safe).
     */
    public String profileAnimation = "none";
    /** footer_left | footer_center | footer_right — resting position after motion. */
    public String profileAnchor = "footer_left";
    /** When in_footer: avatar ≤ this % of footer bar height. */
    public Integer profileMaxHeightPercent;
    public boolean clipOverflow = true;
    public FrameFooterContentInset contentInset;

    public boolean isAboveFooter() {
        return "above_footer".equalsIgnoreCase(profilePlacement);
    }

    public boolean hasProfileScale() {
        return profileScalePercent != null && profileScalePercent > 0;
    }

    public boolean hasProfileMotion() {
        if (profileAnimation == null || profileAnimation.trim().isEmpty()) {
            return false;
        }
        return !"none".equalsIgnoreCase(profileAnimation.trim());
    }

    public String resolvedAnimation() {
        return profileAnimation != null ? profileAnimation.trim().toLowerCase() : "none";
    }

    public String resolvedAnchor() {
        if (profileAnchor == null || profileAnchor.trim().isEmpty()) {
            return "footer_left";
        }
        return profileAnchor.trim().toLowerCase();
    }
}
