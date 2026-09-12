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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.pt.zyfooai.BuildConfig;
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
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private EditText etName, etPhoneNumber, etOtp;
    private Button btnSendOtp, btnVerifyOtp, btnGoogleSignIn;
    LinearLayout otpLayout;

    ProgressDialog progressDialog;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String phoneNumber;
    private String smsGatewayOtp;
    private boolean usingSmsGatewayOtp;

    HomeViewModel homeViewModel;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(this);

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
                .requestIdToken(getString(R.string.google_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK) {
                        Toast.makeText(this, "Google Sign-In cancelled", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.getData() == null) {
                        Toast.makeText(this, "Google Sign-In failed. Please try again.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleGoogleSignInResult(task);
                }
        );

        firebaseAuth.signOut();
        googleSignInClient.signOut();

        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        btnSendOtp.setOnClickListener(v -> sendOtpWithFirebase());
    }

    private void signInWithGoogle() {
        progressDialog.show();
        googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account == null) {
                progressDialog.dismiss();
                Toast.makeText(this, "Google account not found", Toast.LENGTH_SHORT).show();
                return;
            }

            String idToken = account.getIdToken();
            if (idToken == null || idToken.isEmpty()) {
                progressDialog.dismiss();
                Log.e(TAG, "Google idToken is null for account: " + account.getEmail());
                Toast.makeText(this, "Google Sign-In configuration error. Please update the app.", Toast.LENGTH_LONG).show();
                return;
            }

            firebaseAuthWithGoogle(idToken, account);
        } catch (ApiException e) {
            progressDialog.dismiss();
            Log.e(TAG, "Google Sign-In ApiException: " + e.getStatusCode(), e);
            Toast.makeText(this, getGoogleSignInErrorMessage(e), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            progressDialog.dismiss();
            Log.e(TAG, "Google Sign-In failed", e);
            Toast.makeText(this, "Google Sign-In failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private String getGoogleSignInErrorMessage(ApiException e) {
        switch (e.getStatusCode()) {
            case 10:
                return "Google Sign-In setup error. Please reinstall the latest app build.";
            case 12501:
                return "Google Sign-In cancelled";
            case 7:
                return "Network error. Check your internet connection.";
            case 8:
                return "Google Sign-In failed. Please try again.";
            default:
                return "Google Sign-In failed (" + e.getStatusCode() + ")";
        }
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    progressDialog.dismiss();
                    if (!task.isSuccessful()) {
                        Exception exception = task.getException();
                        Log.e(TAG, "Firebase Google auth failed", exception);
                        Toast.makeText(this,
                                exception != null ? exception.getMessage() : "Google Sign-In Failed",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    String email = account.getEmail() != null ? account.getEmail() : "";
                    String displayName = account.getDisplayName() != null ? account.getDisplayName() : "";
                    String photoUrl = account.getPhotoUrl() != null ? account.getPhotoUrl().toString() : "";
                    String phone = firebaseAuth.getCurrentUser() != null && firebaseAuth.getCurrentUser().getPhoneNumber() != null
                            ? firebaseAuth.getCurrentUser().getPhoneNumber()
                            : "";

                    if (email.isEmpty()) {
                        Toast.makeText(this, getString(R.string.login__fail_account), Toast.LENGTH_LONG).show();
                        return;
                    }

                    saveServerData("google", displayName, email, photoUrl, phone);
                });
    }

    private void sendOtpWithFirebase() {
        phoneNumber = etPhoneNumber.getText().toString().trim();
        usingSmsGatewayOtp = false;
        smsGatewayOtp = null;

        if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
            Toast.makeText(this, "Enter valid phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!phoneNumber.startsWith("+91")) {
            phoneNumber = "+91" + phoneNumber.replaceAll("^0+", "");
        }

        progressDialog.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        progressDialog.dismiss();
                        signInWithCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Log.e(TAG, "Firebase phone verification failed", e);
                        sendOtpViaSmsGateway();
                    }

                    @Override
                    public void onCodeSent(@NonNull String id,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        progressDialog.dismiss();
                        verificationId = id;
                        resendToken = token;
                        showOtpInput(false);
                        Toast.makeText(LoginActivity.this, "OTP Sent", Toast.LENGTH_SHORT).show();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void sendOtpViaSmsGateway() {
        String localPhone = phoneNumber.replace("+91", "").replace(" ", "").trim();
        if (localPhone.length() > 10) {
            localPhone = localPhone.substring(localPhone.length() - 10);
        }

        smsGatewayOtp = generateOTP(6);
        usingSmsGatewayOtp = true;

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Fallback SMS OTP requested for: " + localPhone);
        }

        AndroidNetworking.get("https://www.fast2sms.com/dev/bulkV2")
                .addHeaders("authorization", "DwbFFWPhbXzHWt4N6s2GFPwy8XvwuxXR4FrvXNxO9dRnigdlewph3INTiLCP")
                .addQueryParameter("route", "otp")
                .addQueryParameter("variables_values", smsGatewayOtp)
                .addQueryParameter("numbers", localPhone)
                .addQueryParameter("flash", "0")
                .setPriority(Priority.HIGH)
                .setTag("OTP_Request")
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            if (response.has("return") && response.getBoolean("return")) {
                                showOtpInput(true);
                                Toast.makeText(LoginActivity.this, getString(R.string.otp_send_success), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "OTP send failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(LoginActivity.this, "OTP send failed. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressDialog.dismiss();
                        Log.e(TAG, "SMS gateway error: " + anError.getErrorDetail());
                        Toast.makeText(LoginActivity.this,
                                "OTP failed: " + (anError.getErrorDetail() != null ? anError.getErrorDetail() : "Please try again"),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showOtpInput(boolean smsGatewayMode) {
        etPhoneNumber.setEnabled(false);
        otpLayout.setVisibility(View.VISIBLE);
        btnSendOtp.setVisibility(View.GONE);
        btnVerifyOtp.setOnClickListener(v -> {
            if (smsGatewayMode || usingSmsGatewayOtp) {
                verifySmsGatewayOtp();
            } else {
                verifyFirebaseOtp();
            }
        });
    }

    private void verifyFirebaseOtp() {
        String otp = etOtp.getText().toString().trim();

        if (otp.isEmpty() || otp.length() < 6) {
            Toast.makeText(this, "Enter valid 6-digit OTP", Toast.LENGTH_SHORT).show();
            return;
        }

        if (verificationId == null) {
            Toast.makeText(this, "Please request OTP again", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredential(credential);
    }

    private void verifySmsGatewayOtp() {
        String otp = etOtp.getText().toString().trim();
        if (otp.isEmpty() || smsGatewayOtp == null || !otp.equals(smsGatewayOtp)) {
            Toast.makeText(this, getString(R.string.wrong_otp), Toast.LENGTH_SHORT).show();
            return;
        }

        String raw = phoneNumber.replace("+91", "").replace(" ", "").trim();
        if (raw.length() > 10) {
            raw = raw.substring(raw.length() - 10);
        }
        saveServerData("phone", etName.getText().toString(), "", "", raw);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (!task.isSuccessful()) {
                        Toast.makeText(this, "Enter a valid OTP", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String raw = phoneNumber.replace("+91", "").replace(" ", "").trim();
                    if (raw.length() > 10) {
                        raw = raw.substring(raw.length() - 10);
                    }
                    saveServerData("phone", etName.getText().toString(), "", "", raw);
                });
    }

    private String generateOTP(int length) {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private void saveServerData(String loginType, String displayName, String email, String photoUrl, String phoneNumber) {
        Log.d(TAG, "saveServerData: " + loginType + " " + displayName + " " + email + " " + phoneNumber);
        progressDialog.show();
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        homeViewModel.login(loginType, displayName, email, photoUrl, phoneNumber).observe(this, userItem -> {
            progressDialog.dismiss();
            if (userItem != null && userItem.status == 200) {
                saveUserData(userItem);
            } else {
                String message = userItem != null && userItem.message != null && !userItem.message.isEmpty()
                        ? userItem.message
                        : "Login failed. Please try again.";
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveUserData(UserItem userItem) {
        preferenceManager.setBoolean(Constant.IS_LOGIN, true);
        Functions.saveUserData(this, userItem);
        preferenceManager.setString(Constant.DEFAULT_TYPE, "Personal");

        boolean isSubscribed = isPlanActive(userItem.planEndDate);
        preferenceManager.setBoolean(Constant.IS_SUBSCRIBE, isSubscribed);

        if (preferenceManager.getString(Constant.USER_LANGUAGE).isEmpty()) {
            startActivity(new Intent(LoginActivity.this, LanguageActivity.class).putExtra("from", "login"));
        } else {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        }
        finish();
    }

    private boolean isPlanActive(String planEndDate) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate endDate = LocalDate.parse(planEndDate, formatter);
                return endDate.isAfter(LocalDate.now()) || endDate.isEqual(LocalDate.now());
            }
        } catch (Exception e) {
            Log.e(TAG, "Plan date parse failed", e);
        }
        return false;
    }
}
