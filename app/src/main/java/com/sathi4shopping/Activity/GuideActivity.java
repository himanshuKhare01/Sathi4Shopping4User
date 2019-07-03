package com.sathi4shopping.Activity;

import android.content.Intent;

import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sathi4shopping.R;
import com.sathi4shopping.adapter.PageAdapt;

public class GuideActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    ViewPager pager;
    PageAdapt adapt;
    Button stbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        setFindViewById();
        intializePager();
        stbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, SignInSignUpActivity.class));
                finish();
            }
        });
    }

    private void intializePager() {
        adapt = new PageAdapt(this);
        pager.setAdapter(adapt);
        pager.addOnPageChangeListener(this);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(pager, true);
    }

    private void setFindViewById() {
        pager = findViewById(R.id.viewPager);
        stbtn = findViewById(R.id.stbtn);
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 3)
            stbtn.setVisibility(View.VISIBLE);
        else
            stbtn.setVisibility(View.GONE);
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }
}