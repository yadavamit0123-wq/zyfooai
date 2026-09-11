package com.growwthapps.dailypost.v2.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ItemLanguageBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.model.LanguageItem;

import java.util.List;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.MyViewHolder> {

    public Context context;
    private AdapterClickListener listener;
    private int selectedPosition = -1; // To track the selected position

    public List<LanguageItem> languageItemList;

    public LanguageAdapter(Context context, List<LanguageItem> list, AdapterClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.languageItemList = list;
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLanguageBinding binding = ItemLanguageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        if (languageItemList != null){

            int index = 0;
            for (int i = 0; i < position; i++) {
                index++;
                if (index == 10) {
                    index = 0;
                }
            }

            holder.binding.languageName.setText(languageItemList.get(position).title);

        }
        // Update the background based on the selected position
        if (position == selectedPosition) {
            holder.binding.languageContainer.setActivated(true);
            holder.binding.languageName.setTextColor(ContextCompat.getColor(context, R.color.languages_color));
            holder.binding.langImg1English.setImageResource(R.drawable.language_selected_img);
        } else {
            holder.binding.languageContainer.setActivated(false);

            holder.binding.langImg1English.setImageResource(R.drawable.language_unselected_img);

            holder.binding.languageName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }






        holder.bind(position, languageItemList.get(position), listener);
    }

    @Override
    public int getItemCount() {
        if (languageItemList != null && languageItemList.size() > 0) {
            return languageItemList.size();
        } else {
            return 0;
        }
    }


    public void selectItemById(String id) {
        if (languageItemList != null && !id.isEmpty()) {
            int idInt = Integer.parseInt(id);
            for (int i = 0; i < languageItemList.size(); i++) {
                if (languageItemList.get(i).getId() == idInt) { // Assuming 'getId()' returns the unique identifier
                    selectedPosition = i;
                    notifyDataSetChanged();
                    break;
                }
            }
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ItemLanguageBinding binding;

        public MyViewHolder(ItemLanguageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final int pos, final LanguageItem model, final AdapterClickListener listener) {
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedPosition = pos; // Update the selected position
                    notifyDataSetChanged(); // Notify adapter to refresh the UI
                    listener.onItemClick(v, pos, model); // Pass the event to the listener
                }
            });
        }
    }
}
