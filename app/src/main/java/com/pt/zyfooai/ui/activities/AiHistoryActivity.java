package com.pt.zyfooai.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Locale;

public class AiHistoryActivity extends AppCompatActivity {

    private final AiRepository repository = new AiRepository();
    private final ArrayList<AiConversation> allItems = new ArrayList<>();
    private final ArrayList<AiConversation> filtered = new ArrayList<>();
    private HistoryAdapter adapter;
    private ProgressBar progress;
    private TextView emptyTv;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_history);

        findViewById(R.id.backImg).setOnClickListener(v -> finish());
        progress = findViewById(R.id.progress);
        emptyTv = findViewById(R.id.emptyTv);
        etSearch = findViewById(R.id.etHistorySearch);
        RecyclerView rv = findViewById(R.id.rvHistory);
        adapter = new HistoryAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilter(s != null ? s.toString() : "");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reload();
    }

    private void reload() {
        progress.setVisibility(View.VISIBLE);
        repository.listConversations(this).observe(this, response -> {
            progress.setVisibility(View.GONE);
            allItems.clear();
            if (response != null && response.data != null) {
                allItems.addAll(response.data);
            }
            applyFilter(etSearch.getText() != null ? etSearch.getText().toString() : "");
        });
    }

    private void applyFilter(String query) {
        filtered.clear();
        String q = query != null ? query.trim().toLowerCase(Locale.getDefault()) : "";
        if (q.isEmpty()) {
            filtered.addAll(allItems);
        } else {
            for (AiConversation c : allItems) {
                if (c == null) {
                    continue;
                }
                String title = c.title != null ? c.title.toLowerCase(Locale.getDefault()) : "";
                String provider = c.provider != null ? c.provider.toLowerCase(Locale.getDefault()) : "";
                if (title.contains(q) || provider.contains(q)) {
                    filtered.add(c);
                }
            }
        }
        emptyTv.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        adapter.notifyDataSetChanged();
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

    private void confirmDelete(AiConversation c, int adapterPosition) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.ai_delete_title)
                .setMessage(R.string.ai_delete_message)
                .setPositiveButton(R.string.ai_delete, (d, w) ->
                        repository.deleteConversation(this, c.id).observe(this, ok -> {
                            if (Boolean.TRUE.equals(ok)) {
                                Toast.makeText(this, R.string.ai_deleted, Toast.LENGTH_SHORT).show();
                                allItems.remove(c);
                                if (adapterPosition >= 0 && adapterPosition < filtered.size()) {
                                    filtered.remove(adapterPosition);
                                    adapter.notifyItemRemoved(adapterPosition);
                                } else {
                                    applyFilter(etSearch.getText() != null
                                            ? etSearch.getText().toString() : "");
                                }
                                emptyTv.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
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
            AiConversation c = filtered.get(position);
            holder.title.setText(!TextUtils.isEmpty(c.title)
                    ? c.title
                    : getString(R.string.ai_chat_title));
            String meta = "";
            if (!TextUtils.isEmpty(c.provider)) {
                meta = c.provider;
            }
            if (!TextUtils.isEmpty(c.updatedAt)) {
                meta = meta.isEmpty() ? c.updatedAt : meta + " · " + c.updatedAt;
            }
            holder.meta.setText(meta);
            holder.itemView.setOnClickListener(v -> openConversation(c));
            holder.btnDelete.setOnClickListener(v ->
                    confirmDelete(c, holder.getBindingAdapterPosition()));
            holder.itemView.setOnLongClickListener(v -> {
                confirmDelete(c, holder.getBindingAdapterPosition());
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return filtered.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView meta;
            final ImageView btnDelete;

            Holder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.tvTitle);
                meta = itemView.findViewById(R.id.tvMeta);
                btnDelete = itemView.findViewById(R.id.btnDelete);
            }
        }
    }
}
