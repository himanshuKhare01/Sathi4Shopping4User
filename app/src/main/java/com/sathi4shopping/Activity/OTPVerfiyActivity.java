package com.sathi4shopping.Activity;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sathi4shopping.Class.SendNotification;
import com.sathi4shopping.R;
import com.sathi4shopping.Variable.GlobalVariable;

import java.util.Objects;

public class OTPVerfiyActivity extends AppCompatActivity {
    TextView otp;
    EditText otp_box_1, otp_box_2, otp_box_3, otp_box_4, otp_box_5, otp_box_6;
    String phonenumber, otpsend;
    Button verfify;
    TextView recheck;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverfiy);
        setFindViewById();
        intialize();
        checkverify();
        getotp();
    }

    private void getotp() {
        otp_box_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    if (editable.length() == 1)
                        otp_box_2.requestFocus();
                }
            }
        });
        otp_box_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    if (editable.length() == 1)
                        otp_box_3.requestFocus();
                }
            }
        });
        otp_box_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    if (editable.length() == 1)
                        otp_box_4.requestFocus();
                }
            }
        });
        otp_box_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    if (editable.length() == 1)
                        otp_box_5.requestFocus();
                }
            }
        });
        otp_box_5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable != null) {
                    if (editable.length() == 1)
                        otp_box_6.requestFocus();
                }
            }
        });
    }

    private void checkverify() {
        verfify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterotp = otp_box_1.getText().toString() + otp_box_2.getText().toString() + otp_box_3.getText().toString() + otp_box_4.getText().toString() + otp_box_5.getText().toString() + otp_box_6.getText().toString();
                if (!enterotp.isEmpty()) {
                    if (enterotp.equals(otpsend)) {
                        database.child("phone").onDisconnect().setValue("+91" + phonenumber);
                        database.child("phone").setValue("+91" + phonenumber);
                        Toast.makeText(OTPVerfiyActivity.this, getString(R.string.thanks_for_verification), Toast.LENGTH_SHORT).show();
                        sendWelcomeNotification();
                        startActivity(new Intent(OTPVerfiyActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(OTPVerfiyActivity.this, getString(R.string.enter_correct_otp), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OTPVerfiyActivity.this, getString(R.string.enter_otp_send), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendWelcomeNotification() {
        boolean mainActivity=getIntent().getBooleanExtra("MainActivity",false);
        if(!mainActivity) {
            FirebaseAuth mauth = FirebaseAuth.getInstance();
           new SendNotification().updateNotificationCount(mauth.getCurrentUser().getUid(), GlobalVariable.welcomemessage);
        }
    }

    private void intialize() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        recheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        phonenumber = getIntent().getStringExtra("number");
        otpsend = getIntent().getStringExtra("otp");
        database = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        database.keepSynced(true);
        otp.setText(Html.fromHtml(getResources().getString(R.string.otp1).replace("+91–9876–564–656", "+91-" + phonenumber)));
    }

    private void setFindViewById() {
        otp = findViewById(R.id.otp);
        verfify = findViewById(R.id.verify);
        recheck = findViewById(R.id.recheck);
        otp_box_1 = findViewById(R.id.otp_box_1);
        otp_box_2 = findViewById(R.id.otp_box_2);
        otp_box_3 = findViewById(R.id.otp_box_3);
        otp_box_4 = findViewById(R.id.otp_box_4);
        otp_box_5 = findViewById(R.id.otp_box_5);
        otp_box_6 = findViewById(R.id.otp_box_6);
    }


}