package com.sathi4shopping.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sathi4shopping.R;

import java.net.URL;

public class MemberShipActivity extends AppCompatActivity {
    Button pay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_ship);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorGold));
        findViewById(R.id.home_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        pay=findViewById(R.id.membership_pay);
        changeBackgroundColor();
    }


    void changeBackgroundColor(){
        final CardView[] allCardView = {findViewById(R.id.cardView1), findViewById(R.id.cardView2), findViewById(R.id.cardView3), findViewById(R.id.cardView4), findViewById(R.id.cardView5), findViewById(R.id.cardView6), findViewById(R.id.cardView7), findViewById(R.id.cardView8), findViewById(R.id.cardView9)};
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i <= 9; i++) {
                    super.run();
                    try {
                        sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if(i!=9){
                            allCardView[i].setCardBackgroundColor(getResources().getColor(R.color.colorChange));
                            if (i != 0)
                                allCardView[i - 1].setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        }else {
                            allCardView[i-1].setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                            findViewById(R.id.membership_pay).setBackgroundColor(getResources().getColor(R.color.colorGold));

                        }
                    }
                }
            }
        };
    thread.start();
  }
}