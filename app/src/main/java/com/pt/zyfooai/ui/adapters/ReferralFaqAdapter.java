package com.pt.zyfooai.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.FaqItem;

import java.util.ArrayList;
import java.util.List;

public class ReferralFaqAdapter extends RecyclerView.Adapter<ReferralFaqAdapter.Holder> {

    private final List<FaqItem> items = new ArrayList<>();

    public void submit(List<FaqItem> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_referral_faq, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        FaqItem item = items.get(position);
        holder.question.setText(item.question != null ? item.question : "");
        holder.answer.setText(item.answer != null ? item.answer : "");
        holder.answer.setVisibility(item.expanded ? View.VISIBLE : View.GONE);
        holder.arrow.setRotation(item.expanded ? 180f : 0f);
        holder.header.setOnClickListener(v -> {
            item.expanded = !item.expanded;
            notifyItemChanged(holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final View header;
        final TextView question;
        final TextView answer;
        final ImageView arrow;

        Holder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.faqHeader);
            question = itemView.findViewById(R.id.tvFaqQuestion);
            answer = itemView.findViewById(R.id.tvFaqAnswer);
            arrow = itemView.findViewById(R.id.ivFaqArrow);
        }
    }
}
