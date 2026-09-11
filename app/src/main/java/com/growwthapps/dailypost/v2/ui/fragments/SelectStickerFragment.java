package com.growwthapps.dailypost.v2.ui.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.databinding.FragmentSelectBusinessBinding;
import com.growwthapps.dailypost.v2.databinding.FragmentSelectStickerBinding;
import com.growwthapps.dailypost.v2.listener.AdapterClickListener;
import com.growwthapps.dailypost.v2.listener.ClickListener;
import com.growwthapps.dailypost.v2.model.CategoryItem;
import com.growwthapps.dailypost.v2.model.PostItem;
import com.growwthapps.dailypost.v2.ui.adapters.CategorysAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.FestivalPostAdapter;
import com.growwthapps.dailypost.v2.ui.adapters.StickersAdapter;
import com.growwthapps.dailypost.v2.utils.Constant;

import java.util.ArrayList;
import java.util.List;


public class SelectStickerFragment extends BottomSheetDialogFragment {


    public SelectStickerFragment() {}

    FragmentSelectStickerBinding binding;
    private String selectedCat = "-1";
    List<CategoryItem> categoryItemList = new ArrayList<>();
    Context context;

    public ClickListener<PostItem> listener;

    public SelectStickerFragment(ClickListener<PostItem> listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSelectStickerBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getContext();
        loadCategories();

        binding.closeBtn.setOnClickListener(view1 -> {
            dismiss();
        });
    }


    private void loadCategories() {
        Constant.getHomeViewModel(this).getStickerCategory().observe(getViewLifecycleOwner(), categoryItems -> {
            if (categoryItems != null && !categoryItems.isEmpty()) {
                categoryItemList.addAll(categoryItems);

                selectedCat = categoryItemList.get(0).getId();
                getStickers();

                binding.categoryBgRV.setAdapter(new CategorysAdapter(context, categoryItemList, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        selectedCat = categoryItemList.get(pos).getId();
                        getStickers();
                    }
                }));
            }
        });
    }

    private void getStickers() {
        binding.loadingBar.setVisibility(View.VISIBLE);
        Constant.getHomeViewModel(this).getStickerByCategory(selectedCat).observe(getViewLifecycleOwner(), postItems -> {
            binding.loadingBar.setVisibility(View.GONE);
            if (postItems != null && !postItems.isEmpty()) {
                binding.backgroundRv.setAdapter(new StickersAdapter(context, postItems, new ClickListener<PostItem>() {
                    @Override
                    public void onClick(PostItem data) {
                        listener.onClick(data);
                        dismiss();
                    }
                }));
                binding.noDataLay.setVisibility(View.GONE);
            } else {
                binding.noDataLay.setVisibility(View.VISIBLE);
            }
        });
    }
}