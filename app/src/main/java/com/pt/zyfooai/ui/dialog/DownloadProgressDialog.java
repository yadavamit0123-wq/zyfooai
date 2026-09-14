package com.pt.zyfooai.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pt.zyfooai.R;

public class DownloadProgressDialog {

    private final Dialog dialog;
    private final TextView titleView;
    private final TextView messageView;
    private final TextView percentView;
    private final ProgressBar progressBar;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean isDestroyed;
    private boolean percentOnlyMode;

    public DownloadProgressDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_download_progress, null));
        titleView = dialog.findViewById(R.id.tvDownloadTitle);
        messageView = dialog.findViewById(R.id.tvDownloadMessage);
        percentView = dialog.findViewById(R.id.tvDownloadPercent);
        progressBar = dialog.findViewById(R.id.downloadProgressBar);
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
            titleView.setVisibility(android.view.View.VISIBLE);
            messageView.setVisibility(android.view.View.VISIBLE);
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
            titleView.setVisibility(android.view.View.GONE);
            messageView.setVisibility(android.view.View.GONE);
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
            int safePercent = Math.max(0, Math.min(100, percent));
            progressBar.setIndeterminate(false);
            progressBar.setProgress(safePercent);
            percentView.setText(safePercent + "%");
            if (!percentOnlyMode) {
                messageView.setText(message);
            }
        });
    }

    public void showIndeterminate(String message) {
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            if (!dialog.isShowing()) {
                dialog.show();
            }
            progressBar.setIndeterminate(true);
            messageView.setText(message);
            percentView.setText("");
        });
    }

    public void dismiss() {
        runSafely(() -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        });
    }

    private void runSafely(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mainHandler.post(runnable);
        }
    }
}
