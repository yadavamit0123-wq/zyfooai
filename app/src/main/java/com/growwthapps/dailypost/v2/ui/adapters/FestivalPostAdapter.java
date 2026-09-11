package com.growwthapps.dailypost.v2.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.growwthapps.dailypost.v2.databinding.ItemFestivalPostBinding;
import com.growwthapps.dailypost.v2.listener.ClickListener;
import com.growwthapps.dailypost.v2.model.PostItem;

import java.util.List;

public class FestivalPostAdapter extends RecyclerView.Adapter<FestivalPostAdapter.MyViewHolder> {

    public Context context;
    public ClickListener<PostItem> listener;
    public List<PostItem> postItems;

    public FestivalPostAdapter(Context context, List<PostItem> postItems, ClickListener<PostItem> listener) {
        this.context = context;
        this.listener = listener;
        this.postItems = postItems;
    }

    public void setFestivalPost(List<PostItem> categories) {
        this.postItems = categories;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFestivalPostBinding binding = ItemFestivalPostBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (postItems != null) {

            holder.binding.setCategoryData(postItems.get(position));
        }

        holder.itemView.setOnClickListener(v -> {

            listener.onClick(postItems.get(position));

        });

    }

    @Override
    public int getItemCount() {
        if (postItems != null && postItems.size() > 0) {
                return postItems.size();
        } else {
            return 0;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemFestivalPostBinding binding;

        public MyViewHolder(@NonNull ItemFestivalPostBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
