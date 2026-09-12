package com.pt.zyfooai.ui.fragments;

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
import com.pt.zyfooai.AdsUtils.InterstitialsAdsManager;
import com.pt.zyfooai.R;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.DialogSocialMediaBinding;
import com.pt.zyfooai.databinding.FragmentPersonalDetailBinding;
import com.pt.zyfooai.model.UserItem;
import com.pt.zyfooai.ui.Functions;
import com.pt.zyfooai.ui.activities.LoginActivity;
import com.pt.zyfooai.ui.activities.MainActivity;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.ImageCropperFragment;
import com.pt.zyfooai.utils.MyUtils;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.viewmodel.HomeViewModel;
import com.yalantis.ucrop.UCrop;

import java.io.File;


public class FragmentPersonal extends Fragment {

    private FragmentPersonalDetailBinding binding;
    Activity context;
    PreferenceManager preferenceManager;
    private InterstitialsAdsManager interstitialsAdsManager;
    private String userImageUrl;

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
    private String mediaType = "instagram";

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
                        .start(context, this);  // "this" refers to your activity or fragment
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPersonalDetailBinding.inflate(getLayoutInflater());
        context = getActivity();
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        interstitialsAdsManager = new InterstitialsAdsManager(context);
        preferenceManager = new PreferenceManager(context);

//        binding.pLayChangeSocialMedia.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog();
//            }
//        });

        binding.etname.setText(preferenceManager.getString(Constant.USER_NAME));
        binding.etDesignation.setText(preferenceManager.getString(Constant.USER_DESIGNATION));
        binding.etMobile.setText(preferenceManager.getString(Constant.USER_PHONE));
        binding.etInstagram.setText(preferenceManager.getString(Constant.USER_INSTAGRAM));

        if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
            GlideDataBinding.bindImage(binding.pProfileImg, preferenceManager.getString(Constant.USER_IMAGE));
            binding.pProfileImg.setVisibility(View.VISIBLE);
            binding.llUploadImg.setVisibility(View.GONE);
        }

//        if (preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE) != null && !preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE).isEmpty()) {
//
//            mediaType = preferenceManager.getString(Constant.USER_SOCIAL_MEDIA_TYPE);
//            if (mediaType.equals("instagram")) {
//                binding.pImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
//            } else if (mediaType.equals("facebook")) {
//                binding.pImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
//            } else if (mediaType.equals("website")) {
//                binding.pImgSocialMedia.setImageResource(R.drawable.ep_website_img);
//            } else if (mediaType.equals("email")) {
//                binding.pImgSocialMedia.setImageResource(R.drawable.ep_email_img);
//            } else if (mediaType.equals("twitter")) {
//                binding.pImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
//            } else if (mediaType.equals("youtube")) {
//                binding.pImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
//            }
//
//        }

        binding.updateTxt.setOnClickListener(v1 -> {

            interstitialsAdsManager.showInterstitialAd(() -> {

                String name = binding.etname.getText().toString();
                String designation = binding.etDesignation.getText().toString();
                String number = binding.etMobile.getText().toString();
                String instagram = binding.etInstagram.getText().toString();

                updateProfile(name,designation,number,instagram,userImageUrl);

            });
        });

        binding.pProfileLay.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            someActivityResultLauncher.launch(i);
        });
    }

    ProgressDialog progressDialog;
    HomeViewModel homeViewModel;
    private void updateProfile(String name, String designation, String number, String instagram, String profile) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Updating..");
        progressDialog.show();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.updateProfile(preferenceManager.getString(Constant.USER_ID),name,designation,number,instagram,"",profile).observe(getViewLifecycleOwner(), new Observer<UserItem>() {
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
        context.finish();
        Toast.makeText(context, "Details Saved", Toast.LENGTH_SHORT).show();
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
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_email_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMFacebookLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "facebook";
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_facebook_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMInstagramLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "instagram";
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_instagram_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMTwitterLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "twitter";
                binding.pImgSocialMedia.setImageResource(R.drawable.share_twitter_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMYoutubeLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "youtube";
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_youtube_img);
                dialog.dismiss();
            }
        });
        bindingDialog.dSMWebsiteLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaType = "website";
                binding.pImgSocialMedia.setImageResource(R.drawable.ep_website_img);
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
                    //  imageUri = Uri.parse(out);
                    GlideDataBinding.bindImage(binding.pProfileImg, out);
                    binding.pProfileImg.setVisibility(View.VISIBLE);
                }).show(getChildFragmentManager(), "");
            }

        }

    }
}