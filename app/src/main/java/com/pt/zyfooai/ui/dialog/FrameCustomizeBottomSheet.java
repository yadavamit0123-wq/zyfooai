package com.pt.zyfooai.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.SeekBar;

import androidx.recyclerview.widget.RecyclerView;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.pt.zyfooai.R;
import com.pt.zyfooai.ui.activities.EditProfileActivity;
import com.pt.zyfooai.utils.FooterSizeHelper;
import com.pt.zyfooai.utils.FrameMediaInsetHelper;
import com.pt.zyfooai.utils.FrameOverlayHelper;
import com.pt.zyfooai.utils.PreferenceManager;

/**
 * Bottom sheet for strip size, frame size, opacity, and media fit controls.
 */
public final class FrameCustomizeBottomSheet {

    private FrameCustomizeBottomSheet() {
    }

    public static void show(
            Activity activity,
            PreferenceManager preferenceManager,
            View footer,
            View contentArea,
            RecyclerView frameRecyclerView
    ) {
        if (activity == null || activity.isFinishing()) {
            return;
        }

        RoundedBottomSheetDialog dialog = new RoundedBottomSheetDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View sheetView = LayoutInflater.from(activity).inflate(R.layout.bottom_sheet_frame_customize, null);
        dialog.setContentView(sheetView);

        SeekBar stripSeekBar = sheetView.findViewById(R.id.footerSizeSeekBar);
        SeekBar frameSizeSeekBar = sheetView.findViewById(R.id.frameSizeSeekBar);
        SeekBar frameAlphaSeekBar = sheetView.findViewById(R.id.frameAlphaSeekBar);
        CompoundButton mediaFitSwitch = sheetView.findViewById(R.id.mediaFitSwitch);
        View updateStripPhotoBtn = sheetView.findViewById(R.id.updateStripPhotoBtn);

        Runnable refreshPreview = () -> {
            FooterSizeHelper.applyFooterScale(footer, preferenceManager);
            FrameOverlayHelper.applyFrameOverlay(frameRecyclerView, preferenceManager);
            FooterSizeHelper.fitContentAboveFooter(contentArea, footer);
            FrameMediaInsetHelper.apply(contentArea, frameRecyclerView, preferenceManager);
        };

        FooterSizeHelper.bindFooterSizeSeekBar(stripSeekBar, preferenceManager, refreshPreview);
        FrameOverlayHelper.bindSeekBar(
                frameSizeSeekBar,
                FrameOverlayHelper.FRAME_SCALE,
                50,
                preferenceManager,
                refreshPreview
        );
        FrameOverlayHelper.bindSeekBar(
                frameAlphaSeekBar,
                FrameOverlayHelper.FRAME_ALPHA,
                100,
                preferenceManager,
                refreshPreview
        );
        FrameMediaInsetHelper.bindFitToggle(
                mediaFitSwitch,
                preferenceManager,
                contentArea,
                frameRecyclerView,
                refreshPreview
        );

        if (updateStripPhotoBtn != null) {
            updateStripPhotoBtn.setOnClickListener(v -> {
                dialog.dismiss();
                activity.startActivity(new Intent(activity, EditProfileActivity.class));
            });
        }

        dialog.setOnDismissListener(d -> refreshPreview.run());
        dialog.show();
    }
}
