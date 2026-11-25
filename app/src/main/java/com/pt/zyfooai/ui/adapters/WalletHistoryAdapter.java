package com.pt.zyfooai.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.WalletHistoryItem;

import java.util.ArrayList;
import java.util.List;

public class WalletHistoryAdapter extends RecyclerView.Adapter<WalletHistoryAdapter.Holder> {

    private final List<WalletHistoryItem> items = new ArrayList<>();

    public void submit(List<WalletHistoryItem> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wallet_history, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        WalletHistoryItem item = items.get(position);
        boolean credit = item.isCredit();
        holder.badge.setText(credit ? "CR" : "DR");
        if (holder.badge.getBackground() != null) {
            holder.badge.getBackground().setTint(
                    credit ? Color.parseColor("#2BB673") : Color.parseColor("#E53935"));
        }

        String who = item.displayWhoOrWhat();
        if (credit) {
            holder.title.setText(holder.itemView.getContext()
                    .getString(R.string.wallet_credit_from, who));
            holder.subtitle.setText(holder.itemView.getContext()
                    .getString(R.string.wallet_credit));
            holder.amount.setText(holder.itemView.getContext()
                    .getString(R.string.wallet_amount_credit, item.displayAmount()));
            holder.amount.setTextColor(Color.parseColor("#2BB673"));
        } else {
            holder.title.setText(holder.itemView.getContext()
                    .getString(R.string.wallet_debit_for, who));
            holder.subtitle.setText(holder.itemView.getContext()
                    .getString(R.string.wallet_debit));
            holder.amount.setText(holder.itemView.getContext()
                    .getString(R.string.wallet_amount_debit, item.displayAmount()));
            holder.amount.setTextColor(Color.parseColor("#E53935"));
        }
        holder.date.setText(item.displayDateTime());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView badge;
        final TextView title;
        final TextView subtitle;
        final TextView date;
        final TextView amount;

        Holder(@NonNull View itemView) {
            super(itemView);
            badge = itemView.findViewById(R.id.tvTypeBadge);
            title = itemView.findViewById(R.id.tvHistoryTitle);
            subtitle = itemView.findViewById(R.id.tvHistorySubtitle);
            date = itemView.findViewById(R.id.tvHistoryDate);
            amount = itemView.findViewById(R.id.tvHistoryAmount);
        }
    }
}
