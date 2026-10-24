package com.pt.zyfooai.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.model.ReferralListItem;

import java.util.ArrayList;
import java.util.List;

public class ReferralListAdapter extends RecyclerView.Adapter<ReferralListAdapter.Holder> {

    private final List<ReferralListItem> items = new ArrayList<>();

    public void submit(List<ReferralListItem> list) {
        items.clear();
        if (list != null) {
            items.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_referral_user, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ReferralListItem item = items.get(position);
        String name = item.name != null ? item.name : "";
        holder.name.setText(name);
        holder.date.setText(item.date != null ? item.date : "");

        String initial = item.initialOrAvatar;
        if (initial == null || initial.isEmpty() || initial.length() > 2) {
            initial = name.isEmpty() ? "?" : String.valueOf(Character.toUpperCase(name.charAt(0)));
        } else if (initial.length() == 1) {
            initial = initial.toUpperCase();
        } else {
            // likely a URL — show first letter of name
            initial = name.isEmpty() ? "?" : String.valueOf(Character.toUpperCase(name.charAt(0)));
        }
        holder.avatar.setText(initial);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        final TextView avatar;
        final TextView name;
        final TextView date;

        Holder(@NonNull View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.tvAvatarLetter);
            name = itemView.findViewById(R.id.tvReferralName);
            date = itemView.findViewById(R.id.tvReferralDate);
        }
    }
}
