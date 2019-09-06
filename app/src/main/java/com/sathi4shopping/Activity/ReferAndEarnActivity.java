package com.sathi4shopping.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.sathi4shopping.R;

public class ReferAndEarnActivity extends AppCompatActivity {
    String link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.refer_earn);
        link = getIntent().getStringExtra("referLink");
        TextView shortLink = findViewById(R.id.referLink);
        shortLink.setText(link);
    }

    public void refer(View view) {
        link = getIntent().getStringExtra("referLink");
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Get free 100 coins on SignUp in Sathi4Shopping App I'm using it to make my all problems solved related to shopping :):):).\nClick the link below for more info.!!!!!\n" + link);
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void back(View view) {
        finish();
    }
}
