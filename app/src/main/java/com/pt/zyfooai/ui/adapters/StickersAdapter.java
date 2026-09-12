package com.pt.zyfooai.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.ItemStickerBinding;
import com.pt.zyfooai.listener.ClickListener;
import com.pt.zyfooai.model.PostItem;

import java.util.List;

public class StickersAdapter extends RecyclerView.Adapter<StickersAdapter.ViewHolder> {


    Context context;
    private List<PostItem> list;
    public ClickListener<PostItem> listener;

    public StickersAdapter(Context context, List<PostItem> list, ClickListener<PostItem> listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStickerBinding binding = ItemStickerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            GlideDataBinding.bindImage(holder.binding.imgSticker,list.get(position).image_url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemStickerBinding binding;

        ViewHolder(@NonNull ItemStickerBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

}