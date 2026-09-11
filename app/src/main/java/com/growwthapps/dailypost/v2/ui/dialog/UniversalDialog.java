package com.growwthapps.dailypost.v2.ui.dialog;


import static com.growwthapps.dailypost.v2.utils.Constant.DARK_MODE_ON;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;

import com.airbnb.lottie.LottieAnimationView;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;

public class UniversalDialog {

    public TextView msgTextView;
    public TextView titleTextView;
    public TextView descriptionTextView;
    public Activity activity;
    public LottieAnimationView successAnim;
    public LottieAnimationView errorAnim;
    public LottieAnimationView confirmAnim;
    public Button okBtn;
    public Button cancelBtn;
    public LottieAnimationView deleteAnim;

    PreferenceManager preferenceManager;
    private Dialog dialog;

    private boolean cancelable;
    private boolean attached = false;


    public UniversalDialog(Activity activity, Boolean cancelable) {
        this.activity = activity;
        this.dialog = new Dialog(activity);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.cancelable = cancelable;

        preferenceManager = new PreferenceManager(activity);
        if (preferenceManager.getString(DARK_MODE_ON).equals("yes")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

    }

   private WindowManager.LayoutParams getLayoutParams(@NonNull Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (dialog.getWindow() != null) {
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
        }
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        return layoutParams;
    }

    public void show() {
        if (dialog != null) {
            dialog.show();
        }
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void cancel() {
        if (dialog != null) {
            dialog.cancel();
        }
    }



    public void showSuccessDialog(String message, String okTitle) {
        this.dialog.setContentView(R.layout.dialog_message);
        successAnim = dialog.findViewById(R.id.success_animation);
        successAnim.setVisibility(View.VISIBLE);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okBtn = dialog.findViewById(R.id.dialogOkButton);

      //  okBtn.setBackgroundColor(dialog.getContext().getResources().getColor(R.color.green_A700));

        titleTextView.setText(dialog.getContext().getString(R.string.success));
        msgTextView.setText(message);
        okBtn.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okBtn.setOnClickListener(view -> UniversalDialog.this.cancel());
        }
    }

    public void showErrorDialog(String message, String okTitle) {
        this.dialog.setContentView(R.layout.dialog_message);
        errorAnim = dialog.findViewById(R.id.error_animation);
        errorAnim.setVisibility(View.VISIBLE);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);

        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okBtn = dialog.findViewById(R.id.dialogOkButton);
        cancelBtn = dialog.findViewById(R.id.dialogCancelButton);


        titleTextView.setText(dialog.getContext().getString(R.string.error));
        msgTextView.setText(message);
        okBtn.setText(okTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            this.dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            this.dialog.setCancelable(cancelable);
            okBtn.setOnClickListener(view -> UniversalDialog.this.cancel());
            cancelBtn.setOnClickListener(view -> UniversalDialog.this.cancel());
        }
    }

    public void showDeleteDialog(String title, String message, String okTitle, String cancelTitle) {
        this.dialog.setContentView(R.layout.dialog_message);
        deleteAnim = dialog.findViewById(R.id.delete_animation);
        deleteAnim.setVisibility(View.VISIBLE);
        titleTextView = dialog.findViewById(R.id.dialogTitleTextView);
        cancelBtn = dialog.findViewById(R.id.dialogCancelButton);
        cancelBtn.setVisibility(View.VISIBLE);
        msgTextView = dialog.findViewById(R.id.dialogMessageTextView);
        okBtn = dialog.findViewById(R.id.dialogOkButton);

        titleTextView.setText(title);
        titleTextView.setAllCaps(true);
        msgTextView.setText(message);
        okBtn.setText(okTitle);
        cancelBtn.setText(cancelTitle);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setAttributes(getLayoutParams(dialog));

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(cancelable);
            okBtn.setOnClickListener(view -> {


                UniversalDialog.this.cancel();
            });
            cancelBtn.setOnClickListener(view -> UniversalDialog.this.cancel());
        }
    }


}
