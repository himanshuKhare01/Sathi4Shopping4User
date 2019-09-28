package com.sathi4shopping.Activity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.razorpay.Checkout;
import com.razorpay.Order;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.razorpay.RazorpayClient;
import com.sathi4shopping.R;

import org.json.JSONObject;

public class MemberShipActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    Button pay;
    private RazorpayClient razorpayClient;
    private Checkout checkout;
    private JSONObject options;

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
        Checkout.preload(this);
        pay = findViewById(R.id.membership_pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MemberShipActivity.this, "This feature is currently unavailable", Toast.LENGTH_SHORT).show();

//                checkout = new Checkout();
//                checkout.setImage(R.mipmap.ic_launcher);
//                options = new JSONObject();
//                try {
//                    razorpayClient = new RazorpayClient("rzp_test_q13bZbTyw3yrBf", "Odg0fHGIB7D5haD81FFcRS4G");
//                    options.put("amount", 5000);
//                    options.put("currency", "INR");
//                    options.put("receipt", "txn_1");
//                    new DoPayment().execute(options);
//                } catch (RazorpayException | JSONException e) {
//                    e.printStackTrace();
//                    Log.e("MemberShip error 3:", "" + e);
//                }

            }
        });
        changeBackgroundColor();
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Toast.makeText(this, "Payment success", Toast.LENGTH_SHORT).show();
    }


    void changeBackgroundColor() {
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
                        if (i != 9) {
                            allCardView[i].setCardBackgroundColor(getResources().getColor(R.color.colorChange));
                            if (i != 0)
                                allCardView[i - 1].setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                        } else {
                            allCardView[i - 1].setCardBackgroundColor(getResources().getColor(R.color.colorWhite));
                            findViewById(R.id.membership_pay).setBackgroundColor(getResources().getColor(R.color.colorGold));

                        }
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.e("MemberShip error 4:", "" + s);
    }

    @SuppressLint("StaticFieldLeak")
    class DoPayment extends AsyncTask<JSONObject, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(JSONObject... options) {
            try {
                Order order = razorpayClient.Orders.create(options[0]);
                options[0].put("name", "Sathi 4 Shopping LLP");
                options[0].put("description", "Reference No. 1");
                options[0].put("order_id", order.toJson().getString("id"));
                checkout.open(MemberShipActivity.this, options[0]);
                return options[0];
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("MemberShip error 2:", "" + e);
            }
            return null;
        }


        @Override
        protected void onPostExecute(JSONObject options) {
            super.onPostExecute(options);

        }
    }
}