package com.sathi4shopping.Class;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.firebase.database.FirebaseDatabase;

public class NetworkBroadcastReceiver extends BroadcastReceiver {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
      if(!isInternetConnected(context)) {
           FirebaseDatabase.getInstance().goOffline();
      }
      else {
          FirebaseDatabase.getInstance().goOnline();
      }
    }

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
