package com.pt.zyfooai.ui.activities;

import static com.pt.zyfooai.utils.MyUtils.topIconBar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.pt.zyfooai.R;
import com.pt.zyfooai.binding.GlideDataBinding;
import com.pt.zyfooai.databinding.ActivitySettingBinding;
import com.pt.zyfooai.ui.Functions;
import com.pt.zyfooai.ui.fragments.SelectBusinessFragment;
import com.pt.zyfooai.utils.AnalyticsHelper;
import com.pt.zyfooai.utils.BiometricHelper;
import com.pt.zyfooai.utils.ClickDebouncer;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.utils.ReferralHelper;
import com.pt.zyfooai.utils.ThemeHelper;
import com.pt.zyfooai.viewmodel.HomeViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding binding;
    Activity context;
    private PreferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;
    private final ClickDebouncer clickDebouncer = new ClickDebouncer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        AnalyticsHelper.logScreen(context, "settings");
        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(context);

        if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
            GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.USER_IMAGE));
        }
        binding.nameTv.setText(preferenceManager.getString(Constant.USER_NAME));


        if (preferenceManager.getBoolean(Constant.IS_SUBSCRIBE)) {
            binding.premiumProgressLay.setVisibility(View.VISIBLE);
            binding.planName.setText("Plan - "+preferenceManager.getString(Constant.PLAN_NAME));
            binding.startDate.setText(preferenceManager.getString(Constant.PLAN_START_DATE));
            binding.endDate.setText(preferenceManager.getString(Constant.PLAN_END_DATE));
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            try {
                Date startDate = df.parse(preferenceManager.getString(Constant.PLAN_START_DATE));
                Date endDate = df.parse(preferenceManager.getString(Constant.PLAN_END_DATE));
                binding.durationProgress.setProgress(getSubsIntervel(startDate,endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            binding.premiumProgressLay.setVisibility(View.GONE);
        }

        topIconBar(this);

        binding.switchDarkMode.setChecked(ThemeHelper.isDarkModeEnabled(context));
        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) ->
                ThemeHelper.setDarkMode(context, isChecked));

        binding.switchBiometric.setEnabled(BiometricHelper.isBiometricAvailable(context));
        binding.switchBiometric.setChecked(BiometricHelper.isLockEnabled(context));
        binding.switchBiometric.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !BiometricHelper.isBiometricAvailable(context)) {
                binding.switchBiometric.setChecked(false);
                Toast.makeText(context, "Biometric not available on this device", Toast.LENGTH_SHORT).show();
                return;
            }
            BiometricHelper.setLockEnabled(context, isChecked);
        });

        binding.tvReferralCode.setText(ReferralHelper.getOrCreateReferralCode(context));
        binding.llReferral.setOnClickListener(v -> startActivity(ReferralHelper.buildShareIntent(context)));
        binding.llChatSupport.setOnClickListener(v -> startActivity(new Intent(this, ChatSupportActivity.class)));
        binding.llCreatorStats.setOnClickListener(v -> startActivity(new Intent(this, CreatorStatsActivity.class)));

        binding.backImg.setOnClickListener(v -> {
            onBackPressed();
        });

        if (preferenceManager.getString(Constant.LANGUAGE_NAME) != null && !preferenceManager.getString(Constant.LANGUAGE_NAME).isEmpty()) {

            binding.tvLanguage.setText(preferenceManager.getString(Constant.LANGUAGE_NAME));

        }

        binding.upgradeBtn.setOnClickListener(view -> {
            if (clickDebouncer.shouldIgnore()) return;
            startActivity(new Intent(context, SubscriptionActivity.class));
        });

        binding.tvLanguage.setText(preferenceManager.getString(Constant.LANGUAGE_NAME));

        binding.llDownload.setOnClickListener(view -> {
            if (clickDebouncer.shouldIgnore()) return;
            Runnable openDownloads = () -> startActivity(new Intent(this, DownloadActivity.class));
            if (BiometricHelper.isLockEnabled(this)) {
                BiometricHelper.authenticate(this, openDownloads, null);
            } else {
                openDownloads.run();
            }
        });

        binding.llPolitical.setOnClickListener(view -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_down, R.anim.slide_down);
            transaction.addToBackStack(null);
            transaction.replace(android.R.id.content, new SelectBusinessFragment("political", model -> {
                HomeViewModel homeViewModel = new ViewModelProvider(SettingActivity.this).get(HomeViewModel.class);
                homeViewModel.updateBusinessProfile(preferenceManager.getString(Constant.USER_ID),"",model.id,"","","","","","","","","","","","","").observe(SettingActivity.this, userItem -> {
                    if (userItem != null && userItem.status == 200){
                        Functions.saveUserData(context,userItem);
                        Toast.makeText(context, "Political category update successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
                    }
                });
            })).commit();
        });

        binding.llBusiness.setOnClickListener(view -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_bottom, R.anim.slide_down, R.anim.slide_down);
            transaction.addToBackStack(null);
            transaction.replace(android.R.id.content, new SelectBusinessFragment("business", model -> {
                HomeViewModel homeViewModel = new ViewModelProvider(SettingActivity.this).get(HomeViewModel.class);
                homeViewModel.updateBusinessProfile(preferenceManager.getString(Constant.USER_ID),"","",model.id,"","","","","","","","","","","","").observe(SettingActivity.this, userItem -> {
                    if (userItem != null && userItem.status == 200){
                        Functions.saveUserData(context,userItem);
                        Toast.makeText(context, "Business category update successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
                    }
                });
            })).commit();
        });

        binding.logoutBtn.setOnClickListener(view -> {
            preferenceManager.getSharedPreference(context).edit().clear().apply();
            Toast.makeText(context, "Logout Successfully", Toast.LENGTH_SHORT).show();
            firebaseAuth.signOut();
            startActivity(new Intent(context, CustomSplashActivity.class));
            finish();
        });

        binding.llEdit.setOnClickListener(view -> {
            if (clickDebouncer.shouldIgnore()) return;
            startActivity(new Intent(this, EditProfileActivity.class));
        });

        binding.llPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyActivity.class);
            intent.putExtra("type", Constant.PRIVACY_POLICY);
            startActivity(intent);
        });

        binding.llTc.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyActivity.class);
            intent.putExtra("type", Constant.TERM_CONDITION);
            startActivity(intent);
        });


        binding.llLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(this, LanguageActivity.class);
            startActivity(intent);
        });

        binding.contactUsLy.setOnClickListener(v -> {
            Intent intent = new Intent(this, ContactUsActivity.class);
            startActivity(intent);
        });

        binding.llRefund.setOnClickListener(v -> {
            Intent intent = new Intent(this, PrivacyActivity.class);
            intent.putExtra("type", Constant.REFUND_POLICY);
            startActivity(intent);
        });

        binding.llFeedback.setOnClickListener(v -> {
            try {
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
            } catch (android.content.ActivityNotFoundException anfe) {
                this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferenceManager.getString(Constant.USER_IMAGE) != null && !preferenceManager.getString(Constant.USER_IMAGE).isEmpty()) {
            GlideDataBinding.bindImage(binding.circularImageView, preferenceManager.getString(Constant.USER_IMAGE));
        }
        binding.nameTv.setText(preferenceManager.getString(Constant.USER_NAME));
    }

    public static int getSubsIntervel(Date startDate, Date endDate) {
        long totalDuration = endDate.getTime() - startDate.getTime();
        Date currentTime = new Date();
        long currentDuration = currentTime.getTime() - startDate.getTime();

        final double percentage = currentDuration / ((double) totalDuration);
        int finalPr = (int) (percentage * 100);

        return finalPr;
    }
}