package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.pt.zyfooai.AdsUtils.InterstitialsAdsManager;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.ActivityEditProfileBinding;
import com.pt.zyfooai.model.UserItem;
import com.pt.zyfooai.ui.Functions;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.ImageCropperFragment;
import com.pt.zyfooai.utils.MyUtils;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.viewmodel.HomeViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class EditProfileActivity extends AppCompatActivity {

    private ActivityEditProfileBinding binding;
    Activity context;
    PreferenceManager preferenceManager;
    boolean personal = true;
    boolean pickPersonal = true;
    String defaultType = "Personal";
    String businessID = "";
    String politicalID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        topIconBar(this);
        preferenceManager = new PreferenceManager(context);

//        binding.settingImg.setOnClickListener(view -> {
//            Intent intent = new Intent(this, SettingActivity.class);
//            startActivity(intent);
//        });

        binding.backImg.setOnClickListener(view -> {
            onBackPressed();
        });

        interstitialsAdsManager = new InterstitialsAdsManager(context);

        if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
            GlideDataBinding.bindImage(binding.personalLogoImg, preferenceManager.getString(Constant.USER_IMAGE));
            binding.personalLogoImg.setVisibility(View.VISIBLE);
            binding.personalLogoUpload.setVisibility(View.GONE);
        }

        Log.d("Check__", "onCreate: "+preferenceManager.getString(Constant.BUSINESS_IMAGE));
        if (preferenceManager.getString(Constant.BUSINESS_IMAGE) != null && !preferenceManager.getString(Constant.BUSINESS_IMAGE).isEmpty()) {
            GlideDataBinding.bindImage(binding.businessLogoImg, preferenceManager.getString(Constant.BUSINESS_IMAGE));
            binding.businessLogoImg.setVisibility(View.VISIBLE);
            binding.businessLogoUpload.setVisibility(View.GONE);
        }

        binding.updateTxt.setOnClickListener(v1 -> {

            interstitialsAdsManager.showInterstitialAd(() -> {

                String name = binding.etname.getText().toString();
                String businessname = binding.etBusinessName.getText().toString();
                String businessemail = binding.etBusinessEmail.getText().toString();
                String businessdesignation = binding.etBusinessDesignation.getText().toString();
                String businessAddress = binding.etBusinessAddress.getText().toString();
                String designation = binding.etDesignation.getText().toString();
                String number = binding.etMobile.getText().toString();
                String website = binding.etBusinessWebsite.getText().toString();
                String instagram = binding.etInstagram.getText().toString();
                String facebook = binding.etFacebook.getText().toString();

                if (personal)
                    updateProfile(name,designation,number,instagram,facebook,userImageUrl);
                else
                    updateBusinessProfile(name,businessname,businessemail,businessdesignation,designation,number,website,instagram,facebook,businessAddress,userImageUrl,businessImageUrl);
            });
        });

        binding.personalLogoLay2.setOnClickListener(v -> {
            pickPersonal = true;
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(i);
        });

        binding.businessLogoLay2.setOnClickListener(v -> {
            pickPersonal = false;
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(i);
        });

        binding.editProfileTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) { // Use getPosition() instead of getId()
                    case 0:
                        setPersonalLay();
                        break;
                    case 1:
                        setBusinessLay();
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        if (preferenceManager.getString(Constant.DEFAULT_TYPE).equals("Business")){
            binding.editProfileTabLayout.getTabAt(1).select();
            setBusinessLay();
        }else {
            binding.editProfileTabLayout.getTabAt(0).select();
            setPersonalLay();
        }
    }

    private void setBusinessLay() {
        binding.etname.setText(preferenceManager.getString(Constant.USER_NAME));
        binding.etDesignation.setText(preferenceManager.getString(Constant.USER_DESIGNATION));
        binding.etBusinessName.setText(preferenceManager.getString(Constant.BUSINESS_NAME));
        binding.etBusinessEmail.setText(preferenceManager.getString(Constant.BUSINESS_EMAIL));
        binding.etMobile.setText(preferenceManager.getString(Constant.BUSINESS_NUMBER));
        binding.etBusinessDesignation.setText(preferenceManager.getString(Constant.BUSINESS_DETAIL));
        binding.etBusinessWebsite.setText(preferenceManager.getString(Constant.BUSINESS_WEBSITE));
        binding.etBusinessAddress.setText(preferenceManager.getString(Constant.BUSINESS_ADDRESS));
        binding.etInstagram.setText(preferenceManager.getString(Constant.BUSINESS_INSTAGRAM));
        binding.etFacebook.setText(preferenceManager.getString(Constant.BUSINESS_FACEBOOK));

        defaultType = "Business";
        personal = false;
        binding.mainlayout.animate()
                .alpha(0f) // Fade out
                .setDuration(100) // Animation duration (300ms)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.mainlayout.setVisibility(View.GONE); // Hide after fade-out

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.businessLogoLay.setVisibility(View.VISIBLE);
                                binding.businessNameLay.setVisibility(View.VISIBLE);
                                binding.businessEmailLay.setVisibility(View.VISIBLE);
                                binding.businessAboutLay.setVisibility(View.VISIBLE);
                                binding.businessWebsiteLay.setVisibility(View.VISIBLE);
                                binding.businessAddressLay.setVisibility(View.VISIBLE);
                                binding.mainlayout.setVisibility(View.VISIBLE);
                                binding.mainlayout.animate()
                                        .alpha(1f) // Fade in
                                        .setDuration(200) // Animation duration (500ms)
                                        .start();
                            }
                        }, 300); // Delay before showing again
                    }
                })
                .start();

    }

    private void setPersonalLay() {
        binding.etname.setText(preferenceManager.getString(Constant.USER_NAME));
        binding.etDesignation.setText(preferenceManager.getString(Constant.USER_DESIGNATION));
        binding.etMobile.setText(preferenceManager.getString(Constant.USER_PHONE));
        binding.etInstagram.setText(preferenceManager.getString(Constant.USER_INSTAGRAM));
        binding.etFacebook.setText(preferenceManager.getString(Constant.USER_FACEBOOK));

        defaultType = "Personal";
        personal = true;
        binding.mainlayout.animate()
                .alpha(0f) // Fade out
                .setDuration(100) // Animation duration (300ms)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.mainlayout.setVisibility(View.GONE); // Hide after fade-out

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                binding.businessLogoLay.setVisibility(View.GONE);
                                binding.businessNameLay.setVisibility(View.GONE);
                                binding.businessEmailLay.setVisibility(View.GONE);
                                binding.businessAddressLay.setVisibility(View.GONE);
                                binding.businessAboutLay.setVisibility(View.GONE);
                                binding.businessWebsiteLay.setVisibility(View.GONE);
                                binding.mainlayout.setVisibility(View.VISIBLE);
                                binding.mainlayout.animate()
                                        .alpha(1f) // Fade in
                                        .setDuration(200) // Animation duration (200ms)
                                        .start();
                            }
                        }, 300); // Delay before showing again
                    }
                })
                .start();
    }

    ProgressDialog progressDialog;
    HomeViewModel homeViewModel;
    private void updateProfile(String name, String designation, String number, String instagram,String facebook, String profile) {

        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }
        // Validate number (should be numeric and within length)
        if (number != null && !number.matches("\\d{7,15}")) {
            Toast.makeText(context, "Invalid Number: Should be 7-15 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate image URLs (basic check)
        if (userImageUrl == null && preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
            Toast.makeText(context, "Please select your photo", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating..");
        progressDialog.show();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.updateProfile(preferenceManager.getString(Constant.USER_ID),name,designation,number,instagram,facebook,profile,defaultType).observe(this, userItem -> {
            progressDialog.dismiss();
            if (userItem != null && userItem.status == 200){
                saveUserData(userItem);
            }else {
                Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBusinessProfile(String name, String businessName, String businessEmail, String businessdesignation, String designation, String number, String website, String instagram, String facebook,String businessAddress, String userImageUrl, String businessImageUrl) {
        // Validate required fields
        if (name == null || name.trim().isEmpty()) {
            Toast.makeText(context, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (businessName == null || businessName.trim().isEmpty()) {
            Toast.makeText(context, "Business Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (businessEmail != null && !android.util.Patterns.EMAIL_ADDRESS.matcher(businessEmail).matches()) {
            Toast.makeText(context, "Invalid Business Email", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate number (should be numeric and within length)
        if (number != null && !number.matches("\\d{7,15}")) {
            Toast.makeText(context, "Invalid Number: Should be 7-15 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate image URLs (basic check)
        if (userImageUrl == null && preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
            Toast.makeText(context, "Please select your photo", Toast.LENGTH_SHORT).show();
            return;
        }

        if (businessImageUrl == null && preferenceManager.getString(Constant.BUSINESS_IMAGE).isEmpty()) {
            Toast.makeText(context, "Please select business logo", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating..");
        progressDialog.show();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.updateBusinessProfile(preferenceManager.getString(Constant.USER_ID),name,politicalID,businessID,businessName,businessEmail,businessdesignation,designation,number,website,instagram,facebook,defaultType,businessAddress,userImageUrl,businessImageUrl).observe(this, userItem -> {
            progressDialog.dismiss();
            if (userItem != null && userItem.status == 200){
                saveUserData(userItem);
            }else {
                Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveUserData(UserItem userItem) {
        Functions.saveUserData(context,userItem);
        context.finish();
        Toast.makeText(context, "Details Saved", Toast.LENGTH_SHORT).show();
    }

    private InterstitialsAdsManager interstitialsAdsManager;
    private String userImageUrl;
    private String businessImageUrl;
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
        beginCrop(selectedImage);
    }

    private void beginCrop(Uri uri) {
        if (uri != null) {
            try {
                Uri destinationUri = Uri.fromFile(new File(context.getCacheDir(), new File(uri.getPath()).getName()));
                UCrop.Options options2 = new UCrop.Options();
                options2.setCompressionFormat(Bitmap.CompressFormat.PNG);
                options2.setFreeStyleCropEnabled(true);

                // Start cropping activity with startActivityForResult
                UCrop.of(uri, destinationUri)
                        .withAspectRatio(Constant.BUSINESS_LOGO_WIDTH, Constant.BUSINESS_LOGO_HEIGHT)
                        .withOptions(options2)
                        .start(context);  // "this" refers to your activity or fragment
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                new ImageCropperFragment(0, MyUtils.getPathFromURI(context, UCrop.getOutput(data)), (id, out) -> {
                    if (pickPersonal){
                        userImageUrl = out;
                        GlideDataBinding.bindImage(binding.personalLogoImg, out);
                        binding.personalLogoImg.setVisibility(View.VISIBLE);
                    }else{
                        businessImageUrl = out;
                        GlideDataBinding.bindImage(binding.businessLogoImg, out);
                        binding.businessLogoImg.setVisibility(View.VISIBLE);
                    }
                }).show(getSupportFragmentManager(), "");
            }
        }
    }
}