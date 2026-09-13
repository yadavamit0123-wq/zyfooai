package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.ui.adapters.LanguageAdapter;
import com.pt.zyfooai.databinding.ActivityLanguageBinding;
import com.pt.zyfooai.model.LanguageItem;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.LocaleHelper;
import com.pt.zyfooai.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class LanguageActivity extends AppCompatActivity {
    private ActivityLanguageBinding binding;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        topIconBar(this);

        preferenceManager = new PreferenceManager(this);
        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });

        List<LanguageItem> categoryItemList = new ArrayList<>();

//        categoryItemList.add(new LanguageItem(-1, "", "All", true));

        Constant.getHomeViewModel(this).getLanguagess().observe(this, languageItems -> {

            if (languageItems != null) {

                if (languageItems.size() > 0) {
                    categoryItemList.addAll(languageItems);
                    binding.rvLanguage.setLayoutManager(new GridLayoutManager(this, 1));
                    LanguageAdapter languageAdapter = new LanguageAdapter(this, categoryItemList, new AdapterClickListener() {
                        @Override
                        public void onItemClick(View view, int pos, Object object) {
                            LanguageItem languageModel = (LanguageItem) object;

                            preferenceManager.setBoolean(Constant.LOAD_DATA, true);
                            preferenceManager.setString(Constant.USER_LANGUAGE, String.valueOf(languageModel.id));
                            preferenceManager.setString(Constant.LANGUAGE_NAME, languageModel.title);
                            LocaleHelper.setLocale(
                                    LanguageActivity.this,
                                    LocaleHelper.localeCodeFromLanguageTitle(languageModel.title)
                            );

                            Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                    languageAdapter.selectItemById(preferenceManager.getString(Constant.USER_LANGUAGE));

                    binding.rvLanguage.setAdapter(languageAdapter);
                    binding.shimer.setVisibility(View.GONE);
                    binding.mainRecycleview.setVisibility(View.VISIBLE);

                } else {
                    Toast.makeText(this, "data not found", Toast.LENGTH_SHORT).show();
                    binding.shimer.setVisibility(View.VISIBLE);
                }

            }

        });

    }
}