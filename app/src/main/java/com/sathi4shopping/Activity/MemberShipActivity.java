package com.sathi4shopping.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.razorpay.Razorpay;
import com.sathi4shopping.R;

public class MemberShipActivity extends AppCompatActivity implements PaymentResultListener {
    Button pay;
    Razorpay razorpay4payment;
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
        pay = findViewById(R.id.membership_pay);
        Checkout.preload(this);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MemberShipActivity.this, "This feature is currently unavailable.", Toast.LENGTH_LONG).show();
            }
        });
        changeBackgroundColor();
    }

//    public void startPayment() {    /**   * Instantiate Checkout   */
//        razorpay4payment = new Razorpay(this,"rzp_test_H7PrB8ned2Tync");
//        Checkout checkout = new Checkout();  /**   * Set your logo here   */
//        checkout.setImage(R.mipmap.ic_launcher);  /**   * Reference to current activity   */
//        final Activity activity = MemberShipActivity.this;  /**   * Pass your payment options to the Razorpay Checkout as a JSONObject   */
//        try {
//            JSONObject options = new JSONObject();
//            options.put("amount", 50000); // amount in the smallest currency unit
//            options.put("currency", "INR");
//            options.put("receipt", "order_rcptid_11");
//            options.put("name", "Sathi 4 Shopping LLP");    /**     * Description can be anything     * eg: Order #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.     *     Invoice Payment     *     etc.     */
//            options.put("description", "member ship");
//            Order order = razorpay4payment.Orders.create(options);
//            options.put("order_id", "order1");
//
//            checkout.open(activity, options);
//        } catch (Exception e) {
//            Log.e("MemberShipActivity", "Error in starting Razorpay Checkout", e);
//        }
//    }


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
    public void onPaymentSuccess(String s) {

    }

    @Override
    public void onPaymentError(int i, String s) {
        Log.d("MemberShip", s);
        Toast.makeText(this, "" + s, Toast.LENGTH_SHORT).show();

    }
}