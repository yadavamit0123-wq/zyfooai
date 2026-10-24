package com.pt.zyfooai.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.AiConfig;
import com.pt.zyfooai.model.AiQuota;
import com.pt.zyfooai.respository.AiRepository;
import com.pt.zyfooai.utils.AiPrefs;
import com.pt.zyfooai.utils.AiUiHelper;

import java.util.List;

public class AiHomeActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "post_id";
    public static final String EXTRA_IMAGE_URL = "image_url";
    public static final String EXTRA_PROVIDER = "provider";

    private final AiRepository repository = new AiRepository();
    private String selectedProvider = "";
    private String postId;
    private String imageUrl;

    private TextView btnOpenai;
    private TextView btnGemini;
    private TextView btnChat;
    private TextView btnCreateImage;
    private TextView btnEnhance;
    private TextView btnBgRemove;
    private TextView tvQuota;
    private ProgressBar progress;
    private ImageView ivResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_home);

        postId = getIntent().getStringExtra(EXTRA_POST_ID);
        imageUrl = getIntent().getStringExtra(EXTRA_IMAGE_URL);

        findViewById(R.id.backImg).setOnClickListener(v -> finish());
        btnOpenai = findViewById(R.id.btnProviderOpenai);
        btnGemini = findViewById(R.id.btnProviderGemini);
        btnChat = findViewById(R.id.btnChat);
        btnCreateImage = findViewById(R.id.btnCreateImage);
        btnEnhance = findViewById(R.id.btnEnhance);
        btnBgRemove = findViewById(R.id.btnBgRemove);
        tvQuota = findViewById(R.id.tvQuota);
        progress = findViewById(R.id.progress);
        ivResult = findViewById(R.id.ivResult);

        findViewById(R.id.btnHistory).setOnClickListener(v ->
                startActivity(new Intent(this, AiHistoryActivity.class)));

        btnOpenai.setOnClickListener(v -> selectProvider("openai"));
        btnGemini.setOnClickListener(v -> selectProvider("gemini"));
        btnChat.setOnClickListener(v -> openChat(false));
        btnCreateImage.setOnClickListener(v -> openChat(true));
        btnEnhance.setOnClickListener(v -> runProcess("enhance"));
        btnBgRemove.setOnClickListener(v -> runProcess("bg_remove"));

        loadConfig();
    }

    private void loadConfig() {
        progress.setVisibility(View.VISIBLE);
        repository.fetchConfig(this).observe(this, cfg -> {
            progress.setVisibility(View.GONE);
            AiConfig config = cfg != null ? cfg : AiPrefs.get(this);
            if (config == null || !config.isEnabled()) {
                Toast.makeText(this, R.string.ai_coming_soon, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            bindConfig(config);
        });
    }

    private void bindConfig(AiConfig config) {
        List<String> providers = config.resolvedProviders();
        btnOpenai.setVisibility(providers.contains("openai") ? View.VISIBLE : View.GONE);
        btnGemini.setVisibility(providers.contains("gemini") ? View.VISIBLE : View.GONE);

        selectedProvider = config.resolveDefaultProvider();
        if (TextUtils.isEmpty(selectedProvider) && !providers.isEmpty()) {
            selectedProvider = providers.get(0);
        }
        highlightProvider();

        boolean chat = config.hasTool("chat") || config.hasTool("type");
        boolean createImage = config.hasTool("create_image") || config.hasTool("image");
        boolean enhance = config.hasTool("enhance");
        boolean bg = config.hasTool("bg_remove") || config.hasTool("bgremove");

        btnChat.setVisibility(chat ? View.VISIBLE : View.GONE);
        btnCreateImage.setVisibility(createImage ? View.VISIBLE : View.GONE);

        boolean hasPostImage = !TextUtils.isEmpty(imageUrl);
        btnEnhance.setVisibility(enhance && hasPostImage ? View.VISIBLE : View.GONE);
        btnBgRemove.setVisibility(bg && hasPostImage ? View.VISIBLE : View.GONE);

        AiQuota q = config.quota;
        if (q != null) {
            StringBuilder sb = new StringBuilder();
            if (q.remainingChat != null) {
                sb.append(getString(R.string.ai_quota_chat, q.remainingChat));
            }
            if (q.remainingImage != null) {
                if (sb.length() > 0) {
                    sb.append(" · ");
                }
                sb.append(getString(R.string.ai_quota_image, q.remainingImage));
            }
            tvQuota.setText(sb.toString());
        } else {
            tvQuota.setText("");
        }
    }

    private void selectProvider(String provider) {
        selectedProvider = provider;
        highlightProvider();
    }

    private void highlightProvider() {
        float openaiAlpha = "openai".equals(selectedProvider) ? 1f : 0.45f;
        float geminiAlpha = "gemini".equals(selectedProvider) ? 1f : 0.45f;
        btnOpenai.setAlpha(openaiAlpha);
        btnGemini.setAlpha(geminiAlpha);
    }

    private void openChat(boolean createImageMode) {
        if (TextUtils.isEmpty(selectedProvider)) {
            Toast.makeText(this, R.string.ai_pick_provider, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(this, AiChatActivity.class);
        i.putExtra(EXTRA_PROVIDER, selectedProvider);
        i.putExtra(AiChatActivity.EXTRA_CREATE_IMAGE_MODE, createImageMode);
        startActivity(i);
    }

    private void runProcess(String tool) {
        if (TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(this, R.string.ai_no_image, Toast.LENGTH_SHORT).show();
            return;
        }
        progress.setVisibility(View.VISIBLE);
        repository.process(this, tool, imageUrl, postId, null).observe(this, response -> {
            progress.setVisibility(View.GONE);
            if (response == null) {
                Toast.makeText(this, R.string.ai_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            if (AiUiHelper.handleQuotaError(this, response.resolvedCode(), response.resolvedMessage())) {
                return;
            }
            if (!response.isSuccess() || TextUtils.isEmpty(response.resolvedUrl())) {
                String msg = response.resolvedMessage();
                Toast.makeText(this,
                        !TextUtils.isEmpty(msg) ? msg : getString(R.string.ai_failed),
                        Toast.LENGTH_LONG).show();
                return;
            }
            ivResult.setVisibility(View.VISIBLE);
            Glide.with(this).load(response.resolvedUrl()).into(ivResult);
            Toast.makeText(this, R.string.ai_process_done, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, EditProfileActivity.class)
                    .putExtra("imageUrl", response.resolvedUrl()));
        });
    }
}
