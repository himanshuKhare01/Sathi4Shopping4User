package com.sathi4shopping.Class;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sathi4shopping.Activity.MainActivity;
import com.sathi4shopping.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FCMService extends FirebaseMessagingService {
    public static String tag = null;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String imageUri = remoteMessage.getData().get("image");
        Bitmap bitmap = getBitmapfromUri(imageUri);
        tag = remoteMessage.getData().get("tag");
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("tag", tag);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = "Default";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        if (bitmap != null) {
            builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setBigContentTitle(remoteMessage.getData().get("title")).setSummaryText(remoteMessage.getData().get("body")));
        }else{
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getData().get("body")).setBigContentTitle(remoteMessage.getData().get("title")).setSummaryText(remoteMessage.getData().get("info")));
        }
        builder.setAutoCancel(true);
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setVibrate(new long[]{10000, 10000, 10000, 10000, 10000});
        builder.setLights(Color.RED, 10000, 10000);
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }

        manager.notify(0, builder.build());
    }

    private Bitmap getBitmapfromUri(String imageUri) {
        URL url;
        HttpURLConnection connection;
        InputStream input;
        Bitmap bitmap = null;
        try {
            url = new URL(imageUri);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}