package com.pt.zyfooai.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pt.zyfooai.R;

public class DownloadProgressDialog {

    private final Dialog dialog;
    private final View progressPhase;
    private final View successPhase;
    private final TextView titleView;
    private final TextView messageView;
    private final TextView percentView;
    private final ProgressBar progressBar;
    private final TextView openBtn;
    private final TextView shareBtn;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isDestroyed;
    private boolean percentOnlyMode;
    private String resultPath;

    public DownloadProgressDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_download_progress, null));

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.35f;
            window.setAttributes(lp);
            window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        }

        progressPhase = dialog.findViewById(R.id.downloadProgressPhase);
        successPhase = dialog.findViewById(R.id.downloadSuccessPhase);
        titleView = dialog.findViewById(R.id.tvDownloadTitle);
        messageView = dialog.findViewById(R.id.tvDownloadMessage);
        percentView = dialog.findViewById(R.id.tvDownloadPercent);
        progressBar = dialog.findViewById(R.id.downloadProgressBar);
        openBtn = dialog.findViewById(R.id.btnDownloadOpen);
        shareBtn = dialog.findViewById(R.id.btnDownloadShare);
    }

    public void markDestroyed() {
        isDestroyed = true;
        dismiss();
    }

    public void show(String title) {
        percentOnlyMode = false;
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            showProgressPhase();
            dialog.setCancelable(false);
            titleView.setVisibility(View.VISIBLE);
            messageView.setVisibility(View.VISIBLE);
            titleView.setText(title);
            messageView.setText("Starting...");
            updateProgress(0, "Starting...");
            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
    }

    public void showPercentOnly() {
        percentOnlyMode = true;
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            showProgressPhase();
            dialog.setCancelable(false);
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(R.string.downloading_title);
            messageView.setVisibility(View.GONE);
            progressBar.setIndeterminate(false);
            progressBar.setProgress(0);
            percentView.setText("0%");
            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
    }

    public void updateProgress(int percent, String message) {
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            if (successPhase.getVisibility() == View.VISIBLE) {
                return;
            }
            showProgressPhase();
            int safePercent = Math.max(0, Math.min(100, percent));
            progressBar.setIndeterminate(false);
            progressBar.setProgress(safePercent);
            percentView.setText(safePercent + "%");
            if (!percentOnlyMode && message != null) {
                messageView.setVisibility(View.VISIBLE);
                messageView.setText(message);
            }
            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
    }

    public void showIndeterminate(String message) {
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            showProgressPhase();
            dialog.setCancelable(false);
            if (!dialog.isShowing()) {
                dialog.show();
            }
            progressBar.setIndeterminate(true);
            titleView.setVisibility(View.VISIBLE);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(message);
            percentView.setText("");
        });
    }

    /**
     * 100% complete — show "Download successful" with Open / Share on screen.
     */
    public void showSuccess(String path, Runnable onOpen, Runnable onShare) {
        resultPath = path;
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            progressBar.setIndeterminate(false);
            progressBar.setProgress(100);
            percentView.setText("100%");
            progressPhase.setVisibility(View.GONE);
            successPhase.setVisibility(View.VISIBLE);
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);

            openBtn.setOnClickListener(v -> {
                dismiss();
                if (onOpen != null) {
                    onOpen.run();
                }
            });
            shareBtn.setOnClickListener(v -> {
                dismiss();
                if (onShare != null) {
                    onShare.run();
                }
            });

            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
    }

    public String getResultPath() {
        return resultPath;
    }

    public void dismiss() {
        runSafely(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
    }

    private void showProgressPhase() {
        progressPhase.setVisibility(View.VISIBLE);
        successPhase.setVisibility(View.GONE);
    }

    private void runSafely(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
    }
}
