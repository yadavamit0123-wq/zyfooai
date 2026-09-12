package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.pt.zyfooai.R;
import com.pt.zyfooai.databinding.ActivityPrivacyBinding;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;

public class PrivacyActivity extends AppCompatActivity {

    ActivityPrivacyBinding binding;
    String type;
    String privacy;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topIconBar(this);

        preferenceManager = new PreferenceManager(this);


        if (getIntent().getExtras() != null) {

            type = getIntent().getExtras().getString("type");

            if (type.equals(Constant.PRIVACY_POLICY)) {
                privacy = preferenceManager.getString(Constant.PRIVACY_POLICY);
                binding.toolbar.toolName.setText(getResources().getString(R.string.menu_privacy_policy));
            } else if (type.equals(Constant.TERM_CONDITION)) {
                privacy = preferenceManager.getString(Constant.TERM_CONDITION);
                binding.toolbar.toolName.setText(getResources().getString(R.string.terms_and_service));
            } else {
                privacy = preferenceManager.getString(Constant.REFUND_POLICY);
                binding.toolbar.toolName.setText(getResources().getString(R.string.refund_policy));
            }

            setData();
        }
    }

    private void setData() {
        binding.toolbar.back.setOnClickListener(v -> onBackPressed());
        binding.wvPrivacy.getSettings().setJavaScriptEnabled(true);

        binding.wvPrivacy.loadData(privacy, "", null);

        binding.wvPrivacy.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                binding.progreee.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                binding.progreee.setVisibility(View.GONE);
            }
        });
    }

}