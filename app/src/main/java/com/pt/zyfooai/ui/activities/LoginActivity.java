package com.pt.zyfooai.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interceptors.HttpLoggingInterceptor;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.UserItem;
import com.pt.zyfooai.ui.Functions;
import com.pt.zyfooai.utils.Constant;
import com.pt.zyfooai.utils.PreferenceManager;
import com.pt.zyfooai.viewmodel.HomeViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 100;

    private EditText etName,etPhoneNumber, etOtp;
    private Button btnSendOtp, btnVerifyOtp, btnGoogleSignIn;
    LinearLayout otpLayout;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCancelable(false);

        etName = findViewById(R.id.et_name);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etOtp = findViewById(R.id.et_otp);
        otpLayout = findViewById(R.id.otpLayout);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnVerifyOtp = findViewById(R.id.btn_verify_otp);
        btnGoogleSignIn = findViewById(R.id.btn_google_sign_in);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.revokeAccess().addOnCompleteListener(task -> {});

        firebaseAuth.signOut();

        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());

        // Phone Authentication
//        btnSendOtp.setOnClickListener(v -> sendOtp());
        btnSendOtp.setOnClickListener(v -> sendOtpWithFirebase());

    }

    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(Exception.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (Exception e) {
                Log.e("Google Sign-In", "Failed: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        if (firebaseAuth.getCurrentUser() != null) {
                            List<? extends UserInfo> userInfoList = firebaseAuth.getCurrentUser().getProviderData();

                            String email = "";
                            String uid = "";
                            String displayName = "";
                            String photoUrl = "";
                            for (int i = 0; i < userInfoList.size(); i++) {

                                email = userInfoList.get(i).getEmail();

                                if (email != null && !email.equals("")) {
                                    uid = userInfoList.get(i).getUid();
                                    displayName = userInfoList.get(i).getDisplayName();
                                    photoUrl = String.valueOf(userInfoList.get(i).getPhotoUrl());
                                    break;
                                }
                            }
                            saveServerData("google",displayName,email,photoUrl,firebaseAuth.getCurrentUser().getPhoneNumber());
                        } else {
                            // Error Message
                            Toast.makeText(this, getString(R.string.login__fail_account), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    private void sendOtpWithFirebase() {

        phoneNumber = etPhoneNumber.getText().toString().trim();

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format to +91XXXXXXXXXX
        if (!phoneNumber.startsWith("+91")) {
            phoneNumber = "+91" + phoneNumber;
        }

        progressDialog.show();

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                // Auto verified (rare)
                                progressDialog.dismiss();
                                signInWithCredential(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String id,
                                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                                progressDialog.dismiss();

                                verificationId = id;
                                resendToken = token;

                                etPhoneNumber.setEnabled(false);
                                otpLayout.setVisibility(View.VISIBLE);
                                btnSendOtp.setVisibility(View.GONE);
                                btnVerifyOtp.setOnClickListener(v -> verifyFirebaseOtp());

                                Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                            }
                        }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }


    private void verifyFirebaseOtp() {

        String otp = etOtp.getText().toString().trim();

        if (otp.isEmpty() || otp.length() < 6) {
            Toast.makeText(this, "Enter valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        String raw = phoneNumber.replace("+91", "").replace(" ", "").trim();

// Ensure only last 10 digits
                        if (raw.length() > 10) {
                            raw = raw.substring(raw.length() - 10);
                        }

                        saveServerData("phone",etName.getText().toString(),"","",raw);

                    } else {
                        Toast.makeText(this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
    }

    String phoneNumber;
    private void sendOtp() {
        phoneNumber = etPhoneNumber.getText().toString();
        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.startsWith("+91")) {
            phoneNumber = phoneNumber.substring(3);
        } else if (phoneNumber.startsWith("91")) {
            phoneNumber = phoneNumber.substring(2);
        }

        String otp = generateOTP(6);
        progressDialog.show();

        Log.d("onResponse__", "sendOtp: "+phoneNumber);

        AndroidNetworking.enableLogging(HttpLoggingInterceptor.Level.BODY);
        AndroidNetworking.get("https://www.fast2sms.com/dev/bulkV2")
                .addHeaders("authorization", "DwbFFWPhbXzHWt4N6s2GFPwy8XvwuxXR4FrvXNxO9dRnigdlewph3INTiLCP")
                .addQueryParameter("authorization", "DwbFFWPhbXzHWt4N6s2GFPwy8XvwuxXR4FrvXNxO9dRnigdlewph3INTiLCP")
                .addQueryParameter("route", "otp")
                .addQueryParameter("variables_values", otp)
                .addQueryParameter("numbers", phoneNumber)
                .addQueryParameter("flash", "0")
                .setPriority(Priority.HIGH) // Set high priority for fast execution
                .setTag("OTP_Request")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d( "onResponse__",response.toString());
                        progressDialog.dismiss();
                        try {
                            if (response.has("return") && response.getBoolean("return")){
                                etPhoneNumber.setEnabled(false);
                                otpLayout.setVisibility(View.VISIBLE);
                                btnSendOtp.setVisibility(View.GONE);
                                btnVerifyOtp.setOnClickListener(v -> verifyOtp(otp));
                            }else {
                                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.d( "onResponse__","JSONException : "+e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Log.d("onResponse__", "Error: " + anError.getErrorDetail());
                        Log.d("onResponse__", "Error Body: " + anError.getErrorBody());
                        Log.d("onResponse__", "Error Code: " + anError.getErrorCode());
                        Toast.makeText(LoginActivity.this, "Error: " + anError.getErrorDetail(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private String generateOTP(int length) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Generates a digit from 0-9
        }
        return otp.toString();
    }

    private void verifyOtp(String OTP) {
        String otp = etOtp.getText().toString().trim();
        if (!otp.isEmpty() && !otp.equals(OTP)) {
            Toast.makeText(this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
            return;
        }
        saveServerData("phone",etName.getText().toString(),"","",phoneNumber);
    }



    HomeViewModel homeViewModel;
    private void saveServerData(String loginType, String displayName, String email, String photoUrl, String phoneNumber) {
        Log.d("Login__", "saveServerData: "+loginType+" "+displayName+" "+email+" "+photoUrl+" "+phoneNumber);
        progressDialog.show();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.login(loginType,displayName,email,photoUrl,phoneNumber).observe(this, new Observer<UserItem>() {
            @Override
            public void onChanged(UserItem userItem) {
                progressDialog.dismiss();
                if (userItem != null && userItem.status == 200){
                    saveUserData(userItem);
                }else {
                    Toast.makeText(LoginActivity.this, userItem != null ? userItem.message : "Null Data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    PreferenceManager preferenceManager;
    private void saveUserData(UserItem userItem) {
        preferenceManager = new PreferenceManager(this);
        preferenceManager.setBoolean(Constant.IS_LOGIN, true);

        Functions.saveUserData(this,userItem);
        preferenceManager.setString(Constant.DEFAULT_TYPE,"Personal");

        boolean isSubscribed = isPlanActive(userItem.planEndDate);
        preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, isSubscribed);

        if (preferenceManager.getString(Constant.USER_LANGUAGE).isEmpty()){
            startActivity(new Intent(LoginActivity.this, LanguageActivity.class).putExtra("from","login"));
        }else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        finish();
    }

    // Helper function to check if the plan is active
    private boolean isPlanActive(String planEndDate) {
        try {
            // Parse the planEndDate string into a LocalDate
            DateTimeFormatter formatter = null; // Adjust format as per your date string
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate endDate = LocalDate.parse(planEndDate, formatter);
                return endDate.isAfter(LocalDate.now()) || endDate.isEqual(LocalDate.now());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false; // If there's an error parsing the date, assume the plan is not active
        }
        return false;
    }
}
