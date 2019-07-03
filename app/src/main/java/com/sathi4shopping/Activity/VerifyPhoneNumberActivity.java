package com.sathi4shopping.Activity;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.R;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneNumberActivity extends AppCompatActivity {
    TextView otp;
    Button generate_otp;
    EditText mobile_number;
    ProgressDialog verify;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            verifyVerificationCode(code);
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneNumberActivity.this, getString(R.string.phone_verification_fail), Toast.LENGTH_LONG).show();
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
            Toast.makeText(VerifyPhoneNumberActivity.this,getString(R.string.otp_send), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (verify.isShowing())
            verify.show();
    }

    private void verifyVerificationCode(String otp) {
        Intent intent = new Intent(getApplicationContext(), OTPVerfiyActivity.class);
        intent.putExtra("number", mobile_number.getText().toString());
        intent.putExtra("otp", otp);
        verify.dismiss();
        boolean newuser = getIntent().getBooleanExtra("MainActivity", false);
        intent.putExtra("MainActivity", newuser);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setFindViewById();
        setverify();
        checkPhoneNumber();
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