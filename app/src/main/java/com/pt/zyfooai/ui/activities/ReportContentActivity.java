package com.pt.zyfooai.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.ContentReportRequest;
import com.pt.zyfooai.model.ContentReportResponse;
import com.pt.zyfooai.respository.ContentReportRepository;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.ContentReportHelper;
import com.pt.zyfooai.utils.ContentReportPrefs;
import com.pt.zyfooai.utils.PreferenceManager;

public class ReportContentActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etReason;
    private TextView btnSubmit;
    private ProgressBar progressBar;

    private PreferenceManager prefs;
    private ContentReportRepository repository;

    private String userId;
    private String postId;
    private String postType;
    private String thumbnailUrl;
    private String frameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_content);

        prefs = new PreferenceManager(this);
        repository = new ContentReportRepository();
        userId = prefs.getString(Constant.USER_ID);

        postId = getIntent().getStringExtra(ContentReportHelper.EXTRA_POST_ID);
        postType = getIntent().getStringExtra(ContentReportHelper.EXTRA_POST_TYPE);
        thumbnailUrl = getIntent().getStringExtra(ContentReportHelper.EXTRA_THUMBNAIL);
        frameId = getIntent().getStringExtra(ContentReportHelper.EXTRA_FRAME_ID);
        if (TextUtils.isEmpty(postType)) {
            postType = "image";
        }

        findViewById(R.id.backImg).setOnClickListener(v -> onBackPressed());
        etEmail = findViewById(R.id.etEmail);
        etReason = findViewById(R.id.etReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

        String savedEmail = prefs.getString(Constant.USER_EMAIL);
        if (!TextUtils.isEmpty(savedEmail)) {
            etEmail.setText(savedEmail);
        }

        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(postId)) {
            Toast.makeText(this, R.string.report_login_required, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (ContentReportPrefs.wasReportedRecently(this, userId, postId)) {
            Toast.makeText(this, R.string.report_already_submitted, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        btnSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String reason = etReason.getText() != null ? etReason.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, R.string.report_invalid_email, Toast.LENGTH_SHORT).show();
            return;
        }
        if (reason.length() < 10) {
            Toast.makeText(this, R.string.report_reason_min, Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContentReportPrefs.wasReportedRecently(this, userId, postId)) {
            Toast.makeText(this, R.string.report_already_submitted, Toast.LENGTH_LONG).show();
            return;
        }

        setLoading(true);
        ContentReportRequest request = new ContentReportRequest(
                userId,
                postId,
                postType,
                email,
                reason,
                thumbnailUrl,
                frameId
        );
        LiveData<ContentReportResponse> live = repository.submit(request);
        live.observe(this, new Observer<ContentReportResponse>() {
            @Override
            public void onChanged(ContentReportResponse response) {
                live.removeObserver(this);
                onResult(response);
            }
        });
    }

    private void onResult(ContentReportResponse response) {
        setLoading(false);
        if (response == null) {
            Toast.makeText(this, R.string.report_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        if (response.isSuccess()) {
            ContentReportPrefs.markReported(this, userId, postId);
            Toast.makeText(this, R.string.report_submitted, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (response.httpCode == 429) {
            ContentReportPrefs.markReported(this, userId, postId);
            Toast.makeText(this, R.string.report_already_submitted, Toast.LENGTH_LONG).show();
            return;
        }
        String msg = !TextUtils.isEmpty(response.message)
                ? response.message
                : getString(R.string.report_failed);
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSubmit.setEnabled(!loading);
        btnSubmit.setAlpha(loading ? 0.6f : 1f);
    }
}
