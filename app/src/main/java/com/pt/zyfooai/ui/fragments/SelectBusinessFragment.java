package com.pt.zyfooai.ui.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.pt.zyfooai.databinding.FragmentSelectBusinessBinding;
import com.pt.zyfooai.ui.adapters.SelectCategoryAdapter;
import com.pt.zyfooai.utils.Constant;

public class SelectBusinessFragment extends Fragment {

    FragmentSelectBusinessBinding binding;
    SelectCategoryAdapter.OnCategorySelect listner;
    ProgressDialog progressDialog;
    String type;

    public SelectBusinessFragment() {}

    public SelectBusinessFragment(String type, SelectCategoryAdapter.OnCategorySelect listner) {
        this.type = type;
        this.listner = listner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSelectBusinessBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");

        binding.title.setText("Select Political Party");
        binding.searchEt.setHint("Search Political Party");

        binding.searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    getData(binding.searchEt.getText().toString());
                    return true;
                }
                return false;
            }
        });

        binding.backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        getData("");
    }

    private void getData(String search) {
        progressDialog.show();
        Constant.getHomeViewModel(this).getBusinessCategory(search,type).observe(getViewLifecycleOwner(), categoryItems -> {
            progressDialog.dismiss();
            if (categoryItems != null) {

                binding.recycler.setAdapter(new SelectCategoryAdapter(getContext(), categoryItems, model -> {

                    listner.onSelect(model);
                    getActivity().onBackPressed();

                }));

            }else {
                Toast.makeText(getContext(), "No business found !", Toast.LENGTH_SHORT).show();
            }

        });
    }
}