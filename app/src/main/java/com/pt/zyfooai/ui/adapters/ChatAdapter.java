package com.pt.zyfooai.ui.adapters;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.data.entity.ChatMessageEntity;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<ChatMessageEntity> messages = new ArrayList<>();

    public void setMessages(List<ChatMessageEntity> items) {
        messages.clear();
        if (items != null) {
            messages.addAll(items);
        }
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessageEntity message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessageEntity item = messages.get(position);
        holder.tvMessage.setText(item.message);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) holder.tvMessage.getLayoutParams();
        params.gravity = item.fromUser ? Gravity.END : Gravity.START;
        holder.tvMessage.setLayoutParams(params);
        if (item.fromUser) {
            holder.tvMessage.setBackgroundResource(R.drawable.action_btn_bg_1);
            holder.tvMessage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else {
            holder.tvMessage.setBackgroundResource(R.drawable.rounded_bg);
            holder.tvMessage.getBackground().setTint(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.cardBackgroundColor));
            holder.tvMessage.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.sub_text_color));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }
    }
}
