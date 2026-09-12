package com.pt.zyfooai.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.databinding.ItemCategoryBinding;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.model.CategoryItem;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;

import java.util.List;

public class CategorysAdapter extends RecyclerView.Adapter<CategorysAdapter.MyViewHolder> {

    public Context context;
    private AdapterClickListener listener;

    private List<CategoryItem> titles;
    PreferenceManager preferenceManager;
    int selectedPosition = 0;

    public CategorysAdapter(Context context, List<CategoryItem> list, AdapterClickListener listener) {
        this.context = context;
        this.titles = list;
        this.listener = listener;
        preferenceManager = new PreferenceManager(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding binding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if (this.selectedPosition == position) {
            holder.binding.llCategory.setActivated(true);
            holder.binding.catName.setTextColor(Color.WHITE);
        } else {
            holder.binding.llCategory.setActivated(false);
            holder.binding.catName.setTextColor(Color.BLACK);
        }

        if (titles.get(position).video){
            holder.binding.img.setImageDrawable(context.getDrawable(titles.get(position).logo));
            holder.binding.img.setVisibility(View.VISIBLE);
        }

       holder.binding.catName.setText("" + titles.get(position).getName());

        holder.binding.getRoot().setOnClickListener(view -> {
            if (titles.get(position).getName() == "My Business" && preferenceManager.getString(Constant.BUSINESS_ID).equals("0")){
                listener.onItemClick(view, position, null);
            }else if (titles.get(position).getName() == "Political" && preferenceManager.getString(Constant.POLITICAL_ID).equals("0")){
                listener.onItemClick(view, position, null);
            }else {
                CategorysAdapter recyclerOverLayAdapter = CategorysAdapter.this;
                recyclerOverLayAdapter.notifyItemChanged(recyclerOverLayAdapter.selectedPosition);
                CategorysAdapter recyclerOverLayAdapter2 = CategorysAdapter.this;
                recyclerOverLayAdapter2.selectedPosition = position;
                recyclerOverLayAdapter2.notifyItemChanged(recyclerOverLayAdapter2.selectedPosition);
                listener.onItemClick(view, position, null);
            }
        });




    }
    @Override
    public int getItemCount() {
        return titles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ItemCategoryBinding binding;

        public MyViewHolder(@NonNull ItemCategoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
