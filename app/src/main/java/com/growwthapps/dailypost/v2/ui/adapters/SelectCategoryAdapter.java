package com.growwthapps.dailypost.v2.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.databinding.ItemBusinessListBinding;
import com.growwthapps.dailypost.v2.model.CategoryItem;

import java.util.List;


public class SelectCategoryAdapter extends RecyclerView.Adapter<SelectCategoryAdapter.ViewHolder> {

    public interface OnCategorySelect{
        void onSelect(CategoryItem model);
    }

    private List<CategoryItem> list;
    private OnCategorySelect listener;
    Context context;

    public SelectCategoryAdapter(Context context, List<CategoryItem> list, OnCategorySelect listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBusinessListBinding binding = ItemBusinessListBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryItem model = list.get(position);
        holder.binding.setCategory(model);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
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

        ItemBusinessListBinding binding;

        ViewHolder(@NonNull ItemBusinessListBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;

        }
    }

}