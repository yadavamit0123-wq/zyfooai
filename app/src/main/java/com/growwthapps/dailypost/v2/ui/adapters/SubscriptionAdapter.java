package com.growwthapps.dailypost.v2.ui.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.ItemPlansBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.model.SubscriptionModel;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    Context context ;
    List<SubscriptionModel> wallet_modelArrayList = new ArrayList<>();
    AdapterClickListener adapter_click_listener;
    private int selectedPosition = 0;

    public SubscriptionAdapter(Context context, List<SubscriptionModel> wallet_modelArrayList, AdapterClickListener adapter_click_listener) {
        this.context = context;
        this.wallet_modelArrayList = wallet_modelArrayList;
        this.adapter_click_listener=adapter_click_listener;
    }

    @NonNull
    @Override
    public SubscriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        ItemPlansBinding binding = ItemPlansBinding.inflate(LayoutInflater.from(context), viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionAdapter.ViewHolder holder, int position) {
        SubscriptionModel model = wallet_modelArrayList.get(position);
        holder.bind(position,wallet_modelArrayList.get(position),adapter_click_listener);

        if (position == selectedPosition) {
            holder.binding.planLayout.setActivated(true);
         //   holder.binding.ivTick.setImageResource(R.drawable.ic_radial_blue_tick);
        }else {
           // holder.binding.ivTick.setImageResource(R.drawable.ic_radial_dark_tick);
            holder.binding.planLayout.setActivated(false);
        }


        holder.binding.planName.setText(""+model.getName());
        holder.binding.discountPrice.setText(context.getString(R.string.currency)+" "+model.getDiscount_price());
        holder.binding.priceTv.setText(context.getString(R.string.currency)+" "+model.getPrice());
        holder.binding.priceTv.setPaintFlags(holder.binding.priceTv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.pricePerDay.setText(model.getValue()+" "+model.getType());

    }

    @Override
    public int getItemCount() {
        return wallet_modelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemPlansBinding binding;

        public ViewHolder(@NonNull ItemPlansBinding itemView) {
            super(itemView.getRoot());

            binding = itemView;
        }

        public void bind(final int postion, final SubscriptionModel item, final AdapterClickListener listener) {

       /*     binding.ivTick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,postion,item);
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                }
            });*/


           itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onItemClick(view,postion,item);
                    notifyItemChanged(selectedPosition);
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                }
            });

        }
    }
}
