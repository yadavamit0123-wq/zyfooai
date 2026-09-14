package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.pt.zyfooai.AdsUtils.InterstitialsAdsManager;
import com.pt.zyfooai.R;
import com.pt.zyfooai.ui.adapters.CategorysAdapter;
import com.pt.zyfooai.ui.adapters.FestivalPostAdapter;
import com.pt.zyfooai.databinding.ActivityCreatePostBinding;
import com.pt.zyfooai.listener.AdapterClickListener;
import com.pt.zyfooai.listener.ClickListener;
import com.pt.zyfooai.model.CategoryItem;
import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.utils.Constant;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreatePostActivity extends AppCompatActivity {
    private ActivityCreatePostBinding binding;
    Activity context;
    String profileImagePath;

    private String selectedCat = "-1";
    List<CategoryItem> categoryItemList = new ArrayList<>();
    private FestivalPostAdapter festivalAdapter;
    private InterstitialsAdsManager interstitialsAdsManager;
    Uri imageUri;
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Here, no request code
                    if (result.getData() != null) {
                        getImageFromURI(result);
                    }
                }
            });


    private void getImageFromURI(ActivityResult result) {
        Uri selectedImage = result.getData().getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        if (selectedImage != null) {
            Cursor cursor = context.getContentResolver().query(selectedImage,
                    null, null, null, null);

            if (cursor != null) {
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                profileImagePath = cursor.getString(columnIndex);
                cursor.close();

                beginCrop(selectedImage);


            }
        }
    }

    private void beginCrop(Uri uri) {
        if (uri != null) {
            try {
                Uri destinationUri = Uri.fromFile(new File(context.getCacheDir(), new File(uri.getPath()).getName()));
                UCrop.Options options2 = new UCrop.Options();
                options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options2.setFreeStyleCropEnabled(false);

                // Start cropping activity with startActivityForResult
                UCrop.of(uri, destinationUri)
                        .withAspectRatio(1080, 1080)
                        .withOptions(options2)
                        .start(context);  // "this" refers to your activity or fragment
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;

        topIconBar(this);
        interstitialsAdsManager = new InterstitialsAdsManager(context);

        binding.backImg.setOnClickListener(view -> {onBackPressed();});

        binding.uploadImgLy.setOnClickListener(v -> {

            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            someActivityResultLauncher.launch(i);

        });

        loadCategories();

    }


    private void loadCategories() {

        Constant.getHomeViewModel(this).getBackgroundCategory().observe(this, categoryItems -> {
            if (categoryItems != null && !categoryItems.isEmpty()) {
                categoryItemList.addAll(categoryItems);

                selectedCat = categoryItemList.get(0).getId();
                getBackground();

                binding.categoryBgRV.setAdapter(new CategorysAdapter(context, categoryItemList, new AdapterClickListener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {
                        selectedCat = categoryItemList.get(pos).getId();
                        getBackground();
                    }
                }));

            }
        });

    }


    private void getBackground() {
        binding.loaderBar.setVisibility(View.VISIBLE);
        binding.backgroundRv.setVisibility(View.GONE);
        Constant.getHomeViewModel(this).getBackgroundByCategory(selectedCat).observe(this, postItems -> {
            binding.loaderBar.setVisibility(View.GONE);
            if (postItems != null) {
                binding.backgroundRv.setAdapter(new FestivalPostAdapter(context, postItems, new ClickListener<PostItem>() {
                    @Override
                    public void onClick(PostItem data) {
                        beginCrop(Uri.parse(data.image_url));
                    }
                }));
                binding.backgroundRv.setVisibility(View.VISIBLE);

                if (postItems.isEmpty()){
                    binding.noDataLay.setVisibility(View.VISIBLE);
                }else {
                    binding.noDataLay.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                imageUri = data.getExtras().getParcelable(UCrop.EXTRA_OUTPUT_URI);
                if (imageUri == null) {
                    return;
                }
                Uri finalUri = imageUri;
                interstitialsAdsManager.showInterstitialAd(new InterstitialsAdsManager.onAdClosedListener() {
                    @Override
                    public void onAdClosed() {
                        Intent intent = new Intent(CreatePostActivity.this, EditorActivity.class);
                        intent.putExtra("imageUri", finalUri.toString());
                        startActivity(intent);
                    }
                });
            } else if (resultCode == UCrop.RESULT_ERROR) {
                android.widget.Toast.makeText(this, getString(R.string.error), android.widget.Toast.LENGTH_SHORT).show();
            }
        }

    }

}