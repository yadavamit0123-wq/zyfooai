package com.growwthapps.dailypost.v2.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.listener.ClickListener;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    private ArrayList<Integer> colorList;
    private Context context;
    private int selectPosition =0;
    public ClickListener<Integer> listener;

    public ColorAdapter(ArrayList<Integer> colorList, Context context, ClickListener<Integer> listener) {
        this.colorList = colorList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        int color = colorList.get(position);
        holder.colorView.setBackgroundColor(color);

        if (position==0){
            holder.noneTxt.setVisibility(View.VISIBLE);
        }else {
            holder.noneTxt.setVisibility(View.GONE);
        }

        if (selectPosition==position){

            holder.colorSelectImg.setVisibility(View.VISIBLE);

        }else {
            holder.colorSelectImg.setVisibility(View.GONE);
        }


        // Set a click listener to show the toast
        holder.itemView.setOnClickListener(v -> {

            selectPosition = position;
            listener.onClick(colorList.get(position));
            notifyDataSetChanged();
         //   Toast.makeText(context, "Selected color: " + String.format("#%06X", (0xFFFFFF & color)), Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    static class ColorViewHolder extends RecyclerView.ViewHolder {
        FrameLayout colorView;
        ImageView colorSelectImg;
        TextView noneTxt;

        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.color_section);
            noneTxt = itemView.findViewById(R.id.noneTxt);
            colorSelectImg = itemView.findViewById(R.id.colorSelectImg);
        }
    }
}
