package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.pt.zyfooai.R;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.model.CategoryItem;
import com.pt.zyfooai.ui.adapters.CategorysAdapter;
import com.pt.zyfooai.utils.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private Activity context;
    private final List<CategoryItem> allCategories = new ArrayList<>();
    private final List<CategoryItem> filteredCategories = new ArrayList<>();
    private CategorysAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        context = this;
        topIconBar(this);

        findViewById(R.id.backImg).setOnClickListener(v -> onBackPressed());

        RecyclerView results = findViewById(R.id.searchResults);
        adapter = new CategorysAdapter(context, filteredCategories, new AdapterClickListener() {
            @Override
            public void onItemClick(android.view.View view, int pos, Object object) {
                CategoryItem item = filteredCategories.get(pos);
                Intent result = new Intent();
                result.putExtra(Constant.INTENT_CATEGORY_ID, item.getId());
                result.putExtra("category_name", item.getName());
                setResult(RESULT_OK, result);
                finish();
            }
        });
        results.setAdapter(adapter);

        android.widget.EditText searchInput = findViewById(R.id.searchInput);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCategories(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        loadCategories();
    }

    private void loadCategories() {
        Constant.getHomeViewModel(this).observeCategories().observe(this, categoryItems -> {
            allCategories.clear();
            allCategories.add(new CategoryItem("-1", "All", R.drawable.logo, false));
            if (categoryItems != null) {
                allCategories.addAll(categoryItems);
            }
            allCategories.add(new CategoryItem("-3", "My Business", R.drawable.ep_business_name_img, true));
            allCategories.add(new CategoryItem("-4", "Political", R.drawable.flag_regular, true));
            filterCategories("");
        });
    }

    private void filterCategories(String query) {
        filteredCategories.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredCategories.addAll(allCategories);
        } else {
            String lower = query.toLowerCase(Locale.getDefault());
            for (CategoryItem item : allCategories) {
                if (item.getName() != null
                        && item.getName().toLowerCase(Locale.getDefault()).contains(lower)) {
                    filteredCategories.add(item);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}
