package com.growwthapps.dailypost.v2.ui.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.deishelon.roundedbottomsheet.RoundedBottomSheetDialog;
import com.growwthapps.dailypost.v2.AdsUtils.InterstitialsAdsManager;
import com.growwthapps.dailypost.v2.R;
import com.growwthapps.dailypost.v2.binding.GlideDataBinding;
import com.growwthapps.dailypost.v2.databinding.DialogSocialMediaBinding;
import com.growwthapps.dailypost.v2.databinding.FragmentBusinessDetailsBinding;
import com.growwthapps.dailypost.v2.model.UserItem;
import com.growwthapps.dailypost.v2.ui.Functions;
import com.growwthapps.dailypost.v2.ui.activities.LoginActivity;
import com.growwthapps.dailypost.v2.ui.activities.MainActivity;
import com.growwthapps.dailypost.v2.utils.Constant;
import com.growwthapps.dailypost.v2.utils.ImageCropperFragment;
import com.growwthapps.dailypost.v2.utils.MyUtils;
import com.growwthapps.dailypost.v2.utils.PreferenceManager;
import com.growwthapps.dailypost.v2.viewmodel.HomeViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;

public class FragmentBusiness extends Fragment {

    private FragmentBusinessDetailsBinding binding;
    Activity context;
    private String userImageUrl;
    PreferenceManager preferenceManager;
    private InterstitialsAdsManager interstitialsAdsManager;
    private String mediaType = "instagram";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBusinessDetailsBinding.inflate(getLayoutInflater());
        context = getActivity();
        return binding.getRoot();
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
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
                options2.setFreeStyleCropEnabled(false);

                // Start cropping activity with startActivityForResult
                UCrop.of(uri, destinationUri)
                        .withAspectRatio(Constant.BUSINESS_LOGO_WIDTH, Constant.BUSINESS_LOGO_HEIGHT)
                        .withOptions(options2)
                        .start(context, this);  // "this" refers to your activity or fragment
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        interstitialsAdsManager = new InterstitialsAdsManager(context);
        preferenceManager = new PreferenceManager(context);

//        binding.bLayChangeSocialMedia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog();
//            }
//        });


        if (preferenceManager.getString(Constant.BUSINESS_NAME) != null && !preferenceManager.getString(Constant.BUSINESS_NAME).isEmpty()) {
            binding.etBusinessName.setText(preferenceManager.getString(Constant.BUSINESS_NAME));
        }

        if (preferenceManager.getString(Constant.BUSINESS_DETAIL) != null && !preferenceManager.getString(Constant.BUSINESS_DETAIL).isEmpty()) {
            binding.etBusinessDesignation.setText(preferenceManager.getString(Constant.BUSINESS_DETAIL));
        }

        if (preferenceManager.getString(Constant.BUSINESS_ADDRESS) != null && !preferenceManager.getString(Constant.BUSINESS_ADDRESS).isEmpty()) {
            binding.etBusinessAddress.setText(preferenceManager.getString(Constant.BUSINESS_ADDRESS));
        }

        if (preferenceManager.getString(Constant.BUSINESS_INSTAGRAM) != null && !preferenceManager.getString(Constant.BUSINESS_INSTAGRAM).isEmpty()) {
            binding.etInstagram.setText(preferenceManager.getString(Constant.BUSINESS_INSTAGRAM));
        }

        if (preferenceManager.getString(Constant.BUSINESS_WHATSAPP) != null && !preferenceManager.getString(Constant.BUSINESS_WHATSAPP).isEmpty()) {
            binding.etWhatsapp.setText(preferenceManager.getString(Constant.BUSINESS_WHATSAPP));
        }

        if (preferenceManager.getString(Constant.BUSINESS_IMAGE) != null && !preferenceManager.getString(Constant.BUSINESS_IMAGE).isEmpty()) {
            GlideDataBinding.bindImage(binding.bProfileImg, preferenceManager.getString(Constant.BUSINESS_IMAGE));
            binding.bProfileImg.setVisibility(View.VISIBLE);
        }

//        if (preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE) != null && !preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE).isEmpty()) {
//            mediaType = preferenceManager.getString(Constant.BUSINESS_SOCIAL_MEDIA_TYPE);
//            if (mediaType.equals("instagram")) {
//                binding.bImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
//            } else if (mediaType.equals("facebook")) {
//                binding.bImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
//            } else if (mediaType.equals("website")) {
//                binding.bImgSocialMedia.setImageResource(R.drawable.ep_website_img);
//            } else if (mediaType.equals("email")) {
//                binding.bImgSocialMedia.setImageResource(R.drawable.ep_email_img);
//            } else if (mediaType.equals("twitter")) {
//                binding.bImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
//            } else if (mediaType.equals("youtube")) {
//                binding.bImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
//            }
//        }

        binding.updateTxt.setOnClickListener(v1 -> {
            interstitialsAdsManager.showInterstitialAd(() -> {
                String businessName =  binding.etBusinessName.getText().toString();
                String businessAddress =  binding.etBusinessAddress.getText().toString();
                String businessDetails =  binding.etBusinessDesignation.getText().toString();
                String businessWhatsapp =  binding.etWhatsapp.getText().toString();
                String businessInstagram =  binding.etInstagram.getText().toString();
                updateBusinessProfile(businessName,businessAddress,businessDetails,businessWhatsapp,businessInstagram,userImageUrl);
            });
        });
        binding.bProfileLay.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(i);
        });
    }

    ProgressDialog progressDialog;
    HomeViewModel homeViewModel;
    private void updateBusinessProfile(String businessName, String businessAddress, String businessDetails, String businessWhatsapp, String businessInstagram, String businessLogo) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating..");
        progressDialog.show();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.updateBusiness(preferenceManager.getString(Constant.USER_ID),businessName,businessAddress,businessDetails,businessWhatsapp,businessInstagram,businessLogo).observe(getViewLifecycleOwner(), new Observer<UserItem>() {
            @Override
            public void onChanged(UserItem userItem) {
                progressDialog.dismiss();
                if (userItem != null && userItem.status == 200){
                    saveUserData(userItem);
                }else {
                    Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveUserData(UserItem userItem) {
        Functions.saveUserData(context,userItem);
        Toast.makeText(context, "Business Details Saved", Toast.LENGTH_SHORT).show();
        context.finish();
    }

    private void showDialog() {
        RoundedBottomSheetDialog dialog = new RoundedBottomSheetDialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DialogSocialMediaBinding bindingDialog = DialogSocialMediaBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(bindingDialog.getRoot());

        bindingDialog.dSMEmailLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "email";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_email_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMFacebookLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "facebook";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMInstagramLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "instagram";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMTwitterLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "twitter";
                binding.bImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMYoutubeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "youtube";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMWebsiteLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "website";
                binding.bImgSocialMedia.setImageResource(R.drawable.ep_website_img);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == UCrop.REQUEST_CROP) {
            if (data != null) {
                new ImageCropperFragment(0, MyUtils.getPathFromURI(context, UCrop.getOutput(data)), (id, out) -> {
                    userImageUrl = out;
                    GlideDataBinding.bindImage(binding.bProfileImg, out);
                    binding.bProfileImg.setVisibility(View.VISIBLE);

                }).show(getChildFragmentManager(), "");
            }
        }
    }
}