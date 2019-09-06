package com.sathi4shopping.Class;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SendNotification {
    private DatabaseReference referencetoNotification;
    private DatabaseReference notificationReference;
    private DatabaseReference userDataBaseRef;

    public SendNotification() {
        userDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        referencetoNotification = FirebaseDatabase.getInstance().getReference().child("Notification");
        notificationReference = FirebaseDatabase.getInstance().getReference().child("SendNotification");
    }

    public void updateNotificationCount(final String uid, final String message) {
        userDataBaseRef.child(uid).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long notificationcount = (long) dataSnapshot.getValue();
                    notificationcount++;
                    userDataBaseRef.child(uid).child("notification").setValue(notificationcount).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendMessage(uid, message);
                        }
                    });
                } else
                    userDataBaseRef.child(uid).child("notification").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendMessage(uid, message);
                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateNotificationCount(final String uid, final String message, final String tid) {
        userDataBaseRef.child(uid).child("notification").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long notificationcount = (long) dataSnapshot.getValue();
                    notificationcount++;
                    userDataBaseRef.child(uid).child("notification").setValue(notificationcount).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendMessage(uid, message, tid);
                        }
                    });
                } else
                    userDataBaseRef.child(uid).child("notification").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendMessage(uid, message, tid);
                        }
                    });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(String uid, String notification, String tid) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MMM dd");
        String date = format.format(calendar.getTime());

        Calendar calendar1 = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
        String time = format1.format(calendar1.getTime());

        String key = referencetoNotification.child(uid).push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("notification", notification);
        map.put("time", time);
        map.put("date", date);
        new GetCount().updateNotificationcount(tid, uid);
        referencetoNotification.child(uid).child(key).updateChildren(map);
        notificationReference.child(uid).child("notification").setValue(notification);
    }
    private void sendMessage(String uid, String notification) {
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MMM dd");
        String date = format.format(calendar.getTime());

        Calendar calendar1 = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format1 = new SimpleDateFormat("hh:mm a");
        String time = format1.format(calendar1.getTime());

        String key = referencetoNotification.child(uid).push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("notification", notification);
        map.put("time", time);
        map.put("date", date);
        new GetCount().updateNotificationcount(uid);
        referencetoNotification.child(uid).child(key).updateChildren(map);
        notificationReference.child(uid).child("notification").setValue(notification);
    }
}