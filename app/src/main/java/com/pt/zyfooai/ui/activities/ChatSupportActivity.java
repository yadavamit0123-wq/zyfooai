package com.pt.zyfooai.ui.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pt.zyfooai.R;
import com.pt.zyfooai.data.AppDatabase;
import com.pt.zyfooai.data.entity.ChatMessageEntity;
import com.pt.zyfooai.ui.adapters.ChatAdapter;
import com.pt.zyfooai.utils.AnalyticsHelper;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.viewmodel.UserViewModel;

import java.util.List;

public class ChatSupportActivity extends AppCompatActivity {

    private ChatAdapter adapter;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);
        preferenceManager = new PreferenceManager(this);
        AnalyticsHelper.logScreen(this, "support_chat");

        findViewById(R.id.backImg).setOnClickListener(v -> onBackPressed());

        adapter = new ChatAdapter();
        androidx.recyclerview.widget.RecyclerView rvChat = findViewById(R.id.rvChat);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        loadMessages();

        findViewById(R.id.btnSend).setOnClickListener(v -> sendMessage());
    }

    private void loadMessages() {
        AsyncTask.execute(() -> {
            List<ChatMessageEntity> messages = AppDatabase.getInstance(this).chatMessageDao().getAll();
            runOnUiThread(() -> {
                if (messages.isEmpty()) {
                    ChatMessageEntity welcome = new ChatMessageEntity(
                            getString(R.string.support_chat_welcome), false, System.currentTimeMillis());
                    adapter.addMessage(welcome);
                } else {
                    adapter.setMessages(messages);
                }
            });
        });
    }

    private void sendMessage() {
        android.widget.EditText etMessage = findViewById(R.id.etMessage);
        String text = etMessage.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }
        etMessage.setText("");

        ChatMessageEntity userMessage = new ChatMessageEntity(text, true, System.currentTimeMillis());
        adapter.addMessage(userMessage);
        AsyncTask.execute(() -> AppDatabase.getInstance(this).chatMessageDao().insert(userMessage));

        UserViewModel userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userViewModel.contactUsMessage(
                preferenceManager.getString(Constant.USER_ID),
                preferenceManager.getString(Constant.USER_NAME),
                preferenceManager.getString(Constant.USER_EMAIL),
                preferenceManager.getString(Constant.USER_PHONE),
                text
        ).observe(this, response -> {
            ChatMessageEntity reply = new ChatMessageEntity(
                    getString(R.string.support_chat_reply), false, System.currentTimeMillis());
            adapter.addMessage(reply);
            AsyncTask.execute(() -> AppDatabase.getInstance(this).chatMessageDao().insert(reply));
            Toast.makeText(this, R.string.support_chat_sent, Toast.LENGTH_SHORT).show();
        });
    }
}
