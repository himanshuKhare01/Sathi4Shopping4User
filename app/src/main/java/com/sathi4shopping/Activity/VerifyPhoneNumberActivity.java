package com.sathi4shopping.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.sathi4shopping.Class.GetCount;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.Class.SendNotification;
import com.sathi4shopping.R;
import com.sathi4shopping.Variable.GlobalVariable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    TextView otp;
    Button generate_otp;
    EditText mobile_number;
    ProgressDialog verify;
    private DatabaseReference database;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.otp_send), Toast.LENGTH_SHORT).show();
            database.child("phone").onDisconnect().setValue("+91" + mobile_number.getText().toString());
            database.child("phone").setValue("+91" + mobile_number.getText().toString());
            Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.thanks_for_verification), Toast.LENGTH_SHORT).show();
            sendWelcomeNotification();
            startActivity(new Intent(VerifyPhoneNumberActivity.this, MainActivity.class).putExtra("isNewUser", "true"));
            finish();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.phone_verification_fail) + e.getMessage(), Toast.LENGTH_LONG).show();
            verify.dismiss();
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(String s) {
            super.onCodeAutoRetrievalTimeOut(s);
            verify.dismiss();
            Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.time_out), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (verify.isShowing())
            verify.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        database.keepSynced(true);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setFindViewById();
        setverify();
        checkPhoneNumber();
    }

    private void addCoins(String uid, String reason) {
        DatabaseReference referenceToUser = FirebaseDatabase.getInstance().getReference().child("Users");
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MMM dd");
        String time = format.format(calendar.getTime());
        HashMap<String, Object> map = new HashMap<>();
        map.put("description", reason);
        String amount = "100";
        map.put("change", "+" + 100);
        map.put("amount", amount);
        referenceToUser.child(uid).child("konfettiView").setValue(true);
        referenceToUser.child(uid).child("rewards").setValue(amount);
        map.put("time", time);
        new SendNotification().updateNotificationCount(uid, reason, "3");
        new GetCount().updateNotificationcount("4", uid);
        referenceToUser.child(uid).child("coin_history").push().updateChildren(map);
    }

    private void checkPhoneNumber() {
        generate_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NetworkBroadcastReceiver.isInternetConnected(VerifyPhoneNumberActivity.this)) {
                    if (mobile_number.getText().toString().equals(""))
                        Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.enter_mobile_number), Toast.LENGTH_SHORT).show();
                    else if (mobile_number.getText().length() < 10)
                        Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.enter_correct_no), Toast.LENGTH_SHORT).show();
                    else {
                        verify.setMessage(getString(R.string.please_wait));
                        verify.setCanceledOnTouchOutside(false);
                        verify.setCancelable(false);
                        verify.show();
                        sendVerificationCode(mobile_number.getText().toString());
                    }
                } else
                    Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendWelcomeNotification() {
        boolean mainActivity = getIntent().getBooleanExtra("MainActivity", false);
        if (!mainActivity) {
            FirebaseAuth mauth = FirebaseAuth.getInstance();
            new SendNotification().updateNotificationCount(mauth.getCurrentUser().getUid(), GlobalVariable.welcomemessage);
            addCoins(mauth.getUid(), "Congrats 100 coins is added into your wallet as welcome coins you can use it on your next shopping from us.");
            referandEarn();
        }
    }

    private void referandEarn() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (pendingDynamicLinkData != null) {
                            Uri deepLink = pendingDynamicLinkData.getLink();
                            String id = deepLink.getQueryParameter("id");
                            addCoins(id, "Congrats 100 coins is added into your wallet for your referral you can use it on your next shopping from us.");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VerifyPhoneNumberActivity.this, "On Faliure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setverify() {
        otp.setText(Html.fromHtml(getResources().getString(R.string.otp)));
        verify = new ProgressDialog(this);
        verify.setCanceledOnTouchOutside(false);
        verify.setCancelable(false);
    }

    private void setFindViewById() {
        otp = findViewById(R.id.otp);
        generate_otp = findViewById(R.id.generate_otp);
        mobile_number = findViewById(R.id.mobile_number);
    }

    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
}