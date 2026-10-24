package com.pt.zyfooai.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.pt.zyfooai.R;

/**
 * Frame footer strip pinned edge-to-edge (left, right, bottom).
 */
public final class FrameFooterLayoutHelper {

    private FrameFooterLayoutHelper() {
    }

    public static void applyEdgeToEdge(View frameRoot) {
        if (frameRoot == null) {
            return;
        }
        pinBottomPanel(frameRoot.findViewById(R.id.stickerBottomPanel));
        pinBottomPanel(frameRoot.findViewById(R.id.glassBottomPanel));
        pinBottomPanel(frameRoot.findViewById(R.id.gradientBottomPanel));
    }

    private static void pinBottomPanel(View panel) {
        if (panel == null) {
            return;
        }
        ViewGroup.LayoutParams params = panel.getLayoutParams();
        if (!(params instanceof RelativeLayout.LayoutParams)) {
            return;
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) params;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        // Clear rules that can pull the footer upward on some frames.
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, 0);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
        layoutParams.addRule(RelativeLayout.ABOVE, 0);
        layoutParams.addRule(RelativeLayout.BELOW, 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        layoutParams.leftMargin = 0;
        layoutParams.rightMargin = 0;
        layoutParams.topMargin = 0;
        layoutParams.bottomMargin = 0;
        panel.setLayoutParams(layoutParams);
        panel.setTranslationY(0f);
    }
}
