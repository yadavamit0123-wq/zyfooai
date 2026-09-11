package com.growwthapps.dailypost.v2.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.animation.content.Content;
import com.growwthapps.dailypost.v2.databinding.ItemMusicBinding;
import com.growwthapps.dailypost.v2.model.PostItem;

import java.util.List;


public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.ViewHolder> {

    private List<PostItem> list;
    private OnMusicSelect listener;
    Context context;
    int selectedPosition = 500;

    public MusicAdapter(Context context, List<PostItem> list, OnMusicSelect listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public interface OnMusicSelect{
        void onSelect(PostItem postItem);
        void onPlay(String path);
        void onStop();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMusicBinding binding = ItemMusicBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PostItem model = list.get(position);

        holder.binding.titleTv.setText(model.title);

        if (this.selectedPosition == position) {
            holder.binding.playBtn.setIcon(context.getDrawable(com.google.android.exoplayer2.R.drawable.exo_controls_pause));
        } else {
            holder.binding.playBtn.setIcon(context.getDrawable(com.google.android.exoplayer2.R.drawable.exo_controls_play));
        }

        if (model.is_premium){
            holder.binding.premiumTag.setVisibility(View.VISIBLE);
        }else{
            holder.binding.premiumTag.setVisibility(View.GONE);
        }

        holder.binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition == position){
                    selectedPosition = 500;
                    listener.onStop();
                    holder.binding.playBtn.setIcon(context.getDrawable(com.google.android.exoplayer2.R.drawable.exo_controls_play));
                }else{
                    listener.onPlay(model.image_url);
                    MusicAdapter recyclerOverLayAdapter = MusicAdapter.this;
                    recyclerOverLayAdapter.notifyItemChanged(recyclerOverLayAdapter.selectedPosition);
                    MusicAdapter recyclerOverLayAdapter2 = MusicAdapter.this;
                    selectedPosition = position;
                    recyclerOverLayAdapter2.notifyItemChanged(recyclerOverLayAdapter2.selectedPosition);
                }
            }
        });

        holder.binding.selecBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onSelect(model);
            }
        });
    }



    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        ItemMusicBinding binding;

        ViewHolder(@NonNull ItemMusicBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;

        }
    }

}