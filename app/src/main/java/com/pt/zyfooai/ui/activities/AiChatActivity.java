package com.pt.zyfooai.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.AiChatMessage;
import com.pt.zyfooai.model.AiChatResponse;
import com.pt.zyfooai.model.AiConfig;
import com.pt.zyfooai.model.AiImageResponse;
import com.pt.zyfooai.respository.AiRepository;
import com.pt.zyfooai.utils.AiPrefs;
import com.pt.zyfooai.utils.AiUiHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AiChatActivity extends AppCompatActivity {

    public static final String EXTRA_CREATE_IMAGE_MODE = "create_image_mode";
    public static final String EXTRA_CONVERSATION_ID = "conversation_id";

    private final AiRepository repository = new AiRepository();
    private final ArrayList<AiChatMessage> messages = new ArrayList<>();
    private MessageAdapter adapter;

    private String provider;
    private String conversationId;
    private boolean createImageMode;
    private Uri pendingImage;

    private EditText etMessage;
    private ProgressBar progress;
    private ImageView btnAttach;
    private ImageView btnMic;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickImage =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    pendingImage = uri;
                    Toast.makeText(this, R.string.ai_image_attached, Toast.LENGTH_SHORT).show();
                }
            });

    private final ActivityResultLauncher<Intent> speechLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                    return;
                }
                ArrayList<String> texts = result.getData()
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (texts != null && !texts.isEmpty()) {
                    etMessage.setText(texts.get(0));
                    etMessage.setSelection(etMessage.getText().length());
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        provider = getIntent().getStringExtra(AiHomeActivity.EXTRA_PROVIDER);
        if (TextUtils.isEmpty(provider)) {
            provider = AiPrefs.defaultProvider(this);
        }
        conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);
        createImageMode = getIntent().getBooleanExtra(EXTRA_CREATE_IMAGE_MODE, false);

        TextView title = findViewById(R.id.tvTitle);
        title.setText(createImageMode
                ? getString(R.string.ai_create_image)
                : getString(R.string.ai_chat_title_provider, displayProvider(provider)));

        findViewById(R.id.backImg).setOnClickListener(v -> finish());
        etMessage = findViewById(R.id.etMessage);
        progress = findViewById(R.id.progress);
        btnAttach = findViewById(R.id.btnAttach);
        btnMic = findViewById(R.id.btnMic);

        RecyclerView rv = findViewById(R.id.rvMessages);
        adapter = new MessageAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        AiConfig cfg = AiPrefs.get(this);
        boolean files = cfg != null && (cfg.hasTool("files") || cfg.hasTool("camera") || cfg.hasTool("image"));
        boolean mic = cfg != null && cfg.hasTool("mic");
        btnAttach.setVisibility(!createImageMode && files ? View.VISIBLE : View.GONE);
        btnMic.setVisibility(mic ? View.VISIBLE : View.GONE);

        btnAttach.setOnClickListener(v -> pickImage.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
        btnMic.setOnClickListener(v -> startSpeech());
        findViewById(R.id.btnSend).setOnClickListener(v -> send());

        if (!TextUtils.isEmpty(conversationId)) {
            loadConversation();
        }
    }

    private void loadConversation() {
        progress.setVisibility(View.VISIBLE);
        repository.getConversation(this, conversationId).observe(this, response -> {
            progress.setVisibility(View.GONE);
            if (response == null || response.data == null || response.data.messages == null) {
                return;
            }
            if (!TextUtils.isEmpty(response.data.provider)) {
                provider = AiConfig.normalizeProvider(response.data.provider);
            }
            messages.clear();
            for (AiChatMessage m : response.data.messages) {
                if (m != null) {
                    messages.add(m);
                }
            }
            adapter.notifyDataSetChanged();
        });
    }

    private void startSpeech() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            speechLauncher.launch(intent);
        } catch (Exception e) {
            Toast.makeText(this, R.string.ai_mic_unavailable, Toast.LENGTH_SHORT).show();
        }
    }

    private void send() {
        String text = etMessage.getText() != null ? etMessage.getText().toString().trim() : "";
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, R.string.ai_message_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        messages.add(AiChatMessage.user(text));
        adapter.notifyItemInserted(messages.size() - 1);
        etMessage.setText("");
        progress.setVisibility(View.VISIBLE);

        if (createImageMode) {
            repository.createImage(this, provider, text, conversationId).observe(this, this::onImageResult);
        } else {
            Uri img = pendingImage;
            pendingImage = null;
            repository.chat(this, provider, conversationId, text, img).observe(this, this::onChatResult);
        }
    }

    private void onChatResult(AiChatResponse response) {
        progress.setVisibility(View.GONE);
        if (response == null) {
            Toast.makeText(this, R.string.ai_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        if (AiUiHelper.handleQuotaError(this, response.resolvedCode(), response.resolvedMessage())) {
            return;
        }
        if (!response.isSuccess()) {
            String msg = response.resolvedMessage();
            Toast.makeText(this,
                    !TextUtils.isEmpty(msg) ? msg : getString(R.string.ai_failed),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(response.resolvedConversationId())) {
            conversationId = response.resolvedConversationId();
        }
        String reply = response.resolvedReply();
        messages.add(AiChatMessage.assistant(
                !TextUtils.isEmpty(reply) ? reply : getString(R.string.ai_empty_reply)));
        adapter.notifyItemInserted(messages.size() - 1);
        findViewById(R.id.rvMessages).post(() ->
                ((RecyclerView) findViewById(R.id.rvMessages)).scrollToPosition(messages.size() - 1));
    }

    private void onImageResult(AiImageResponse response) {
        progress.setVisibility(View.GONE);
        if (response == null) {
            Toast.makeText(this, R.string.ai_failed, Toast.LENGTH_SHORT).show();
            return;
        }
        if (AiUiHelper.handleQuotaError(this, response.resolvedCode(), response.resolvedMessage())) {
            return;
        }
        if (!response.isSuccess() || TextUtils.isEmpty(response.resolvedImageUrl())) {
            String msg = response.resolvedMessage();
            Toast.makeText(this,
                    !TextUtils.isEmpty(msg) ? msg : getString(R.string.ai_failed),
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (!TextUtils.isEmpty(response.conversationId)) {
            conversationId = response.conversationId;
        }
        AiChatMessage m = AiChatMessage.assistant(getString(R.string.ai_image_ready));
        m.imageUrl = response.resolvedImageUrl();
        messages.add(m);
        adapter.notifyItemInserted(messages.size() - 1);
        startActivity(new Intent(this, EditProfileActivity.class)
                .putExtra("imageUrl", response.resolvedImageUrl()));
    }

    private static String displayProvider(String provider) {
        if ("gemini".equalsIgnoreCase(provider)) {
            return "Gemini";
        }
        return "ChatGPT";
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.Holder> {
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ai_chat_bubble, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            AiChatMessage msg = messages.get(position);
            boolean mine = msg.resolveIsMine();
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) holder.tv.getLayoutParams();
            lp.gravity = mine ? android.view.Gravity.END : android.view.Gravity.START;
            holder.tv.setLayoutParams(lp);
            holder.tv.setBackgroundResource(mine ? R.drawable.gradintround_shape : R.drawable.edit_bg);
            holder.tv.setTextColor(mine ? getResources().getColor(R.color.white) : getResources().getColor(R.color.black));
            String text = msg.content != null ? msg.content : "";
            if (!TextUtils.isEmpty(msg.imageUrl)) {
                text = text + "\n" + msg.imageUrl;
            }
            holder.tv.setText(text);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final TextView tv;

            Holder(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tvBubble);
            }
        }
    }
}
