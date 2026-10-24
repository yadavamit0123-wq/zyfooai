package com.pt.zyfooai.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.AiConversation;
import com.pt.zyfooai.respository.AiRepository;

import java.util.ArrayList;

public class AiHistoryActivity extends AppCompatActivity {

    private final AiRepository repository = new AiRepository();
    private final ArrayList<AiConversation> items = new ArrayList<>();
    private HistoryAdapter adapter;
    private ProgressBar progress;
    private TextView emptyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_history);

        findViewById(R.id.backImg).setOnClickListener(v -> finish());
        progress = findViewById(R.id.progress);
        emptyTv = findViewById(R.id.emptyTv);
        RecyclerView rv = findViewById(R.id.rvHistory);
        adapter = new HistoryAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        reload();
    }

    private void reload() {
        progress.setVisibility(View.VISIBLE);
        repository.listConversations(this).observe(this, response -> {
            progress.setVisibility(View.GONE);
            items.clear();
            if (response != null && response.data != null) {
                items.addAll(response.data);
            }
            emptyTv.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
            adapter.notifyDataSetChanged();
        });
    }

    private void openConversation(AiConversation c) {
        if (c == null || TextUtils.isEmpty(c.id)) {
            return;
        }
        Intent i = new Intent(this, AiChatActivity.class);
        i.putExtra(AiChatActivity.EXTRA_CONVERSATION_ID, c.id);
        if (!TextUtils.isEmpty(c.provider)) {
            i.putExtra(AiHomeActivity.EXTRA_PROVIDER, c.provider);
        }
        startActivity(i);
    }

    private void confirmDelete(AiConversation c) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.ai_delete_title)
                .setMessage(R.string.ai_delete_message)
                .setPositiveButton(R.string.ai_delete, (d, w) ->
                        repository.deleteConversation(this, c.id).observe(this, ok -> {
                            if (Boolean.TRUE.equals(ok)) {
                                reload();
                            } else {
                                Toast.makeText(this, R.string.ai_failed, Toast.LENGTH_SHORT).show();
                            }
                        }))
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.Holder> {
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_ai_conversation, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            AiConversation c = items.get(position);
            holder.title.setText(!TextUtils.isEmpty(c.title) ? c.title : getString(R.string.ai_chat_title));
            String meta = "";
            if (!TextUtils.isEmpty(c.provider)) {
                meta = c.provider;
            }
            if (!TextUtils.isEmpty(c.updatedAt)) {
                meta = meta.isEmpty() ? c.updatedAt : meta + " · " + c.updatedAt;
            }
            holder.meta.setText(meta);
            holder.itemView.setOnClickListener(v -> openConversation(c));
            holder.itemView.setOnLongClickListener(v -> {
                confirmDelete(c);
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView meta;

            Holder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.tvTitle);
                meta = itemView.findViewById(R.id.tvMeta);
            }
        }
    }
}
