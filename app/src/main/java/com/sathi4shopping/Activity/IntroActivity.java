package com.sathi4shopping.Activity;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sathi4shopping.R;

import java.util.Objects;

public class IntroActivity extends AppCompatActivity {
    ImageView textanim;
    Animation animation;
    FirebaseAuth mAuth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        intialize();
        startThread();
    }

    private void intialize() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");
        textanim = findViewById(R.id.appname);
        animation = AnimationUtils.loadAnimation(this, R.anim.move);
        textanim.setVisibility(View.VISIBLE);
        textanim.startAnimation(animation);
    }

    private void startThread() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if ((mAuth.getCurrentUser()) == null) {
                        startGuideActivity();
                    } else {
                        checkActivity();
                    }
                }
            }
        };
        thread.start();
    }

    private void startGuideActivity() {
        startActivity(new Intent(IntroActivity.this, GuideActivity.class));
        finish();
    }

    private void checkActivity() {
        reference.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).child("phone")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && !Objects.requireNonNull(dataSnapshot.getValue()).toString().isEmpty()) {
                            startActivity(new Intent(IntroActivity.this, MainActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(IntroActivity.this, VerifyPhoneNumberActivity.class));
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(IntroActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}