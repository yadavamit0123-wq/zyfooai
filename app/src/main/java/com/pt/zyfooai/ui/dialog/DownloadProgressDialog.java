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

/**
 * Bottom download UI. Dismissible while a download continues in the background
 * so the user can scroll and start other downloads.
 */
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
    /** Which post's progress/success is currently shown (if any). */
    private String showingPostKey;

    public DownloadProgressDialog(Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(LayoutInflater.from(context).inflate(R.layout.dialog_download_progress, null));

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.dimAmount = 0.25f;
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

        dialog.setOnDismissListener(d -> {
            // Dismiss only hides UI — download jobs keep running in tracker.
        });
    }

    public void markDestroyed() {
        isDestroyed = true;
        dismiss();
    }

    public String getShowingPostKey() {
        return showingPostKey;
    }

    public boolean isShowingFor(String postKey) {
        return dialog.isShowing()
                && postKey != null
                && postKey.equals(showingPostKey);
    }

    public void show(String title) {
        percentOnlyMode = false;
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            showProgressPhase();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
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

    public void showPercentOnly(String postKey) {
        percentOnlyMode = true;
        showingPostKey = postKey;
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            showProgressPhase();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(R.string.downloading_title);
            messageView.setVisibility(View.VISIBLE);
            messageView.setText(R.string.download_scroll_hint);
            progressBar.setIndeterminate(false);
            progressBar.setProgress(0);
            percentView.setText("0%");
            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
    }

    /** @deprecated use {@link #showPercentOnly(String)} */
    public void showPercentOnly() {
        showPercentOnly(showingPostKey);
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
            if (message != null && !message.isEmpty()) {
                messageView.setVisibility(View.VISIBLE);
                messageView.setText(message);
            } else if (percentOnlyMode) {
                messageView.setVisibility(View.VISIBLE);
                messageView.setText(R.string.download_scroll_hint);
            }
            if (!dialog.isShowing()) {
                dialog.show();
            }
        });
    }

    /** Update UI only if this dialog is currently showing {@code postKey}. */
    public void updateProgressIfMatching(String postKey, int percent) {
        if (isShowingFor(postKey) && successPhase.getVisibility() != View.VISIBLE) {
            updateProgress(percent, null);
        }
    }

    public void showIndeterminate(String message) {
        runSafely(() -> {
            if (isDestroyed) {
                return;
            }
            showProgressPhase();
            dialog.setCancelable(true);
            dialog.setCanceledOnTouchOutside(true);
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
    public void showSuccess(String postKey, String path, Runnable onOpen, Runnable onShare) {
        resultPath = path;
        showingPostKey = postKey;
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

    /** Dismiss only if currently showing this post's UI. */
    public void dismissIfMatching(String postKey) {
        if (isShowingFor(postKey)) {
            dismiss();
        }
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
