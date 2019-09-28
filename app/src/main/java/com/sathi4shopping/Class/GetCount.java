package com.sathi4shopping.Class;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GetCount {
    private DatabaseReference userDataBaseRef;

    public GetCount() {
        userDataBaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    public void updateTotalQueryCount(final String adminID) {
        final DatabaseReference adminrf=FirebaseDatabase.getInstance().getReference().child("Users");
        adminrf.child(adminID).child("queryCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long notificationcount = (long) dataSnapshot.getValue();
                    notificationcount++;
                    adminrf.child(adminID).child("queryCount").setValue(notificationcount);
                } else
                    adminrf.child(adminID).child("queryCount").setValue(1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void decreaseTotalCount(final String fragment, final String uid, final String which) {
        userDataBaseRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(fragment).child("count").exists() && dataSnapshot.child(which).exists()) {
                    long count = Long.parseLong(dataSnapshot.child(fragment).child("count").getValue().toString());
                    long totalCount = (long) dataSnapshot.child(which).getValue();
                    if (count > 0 && totalCount > 0) {
                        long notification = (long) dataSnapshot.child(which).getValue();
                        if (notification >= count) {
                            notification = notification - count;
                            userDataBaseRef.child(uid).child(which).setValue(notification);
                            userDataBaseRef.child(uid).child(fragment).child("count").setValue(0);
                        } else {
                            userDataBaseRef.child(uid).child(which).setValue(0);
                            userDataBaseRef.child(uid).child(fragment).child("count").setValue(0);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void updateNotificationcount(final String uid) {
        userDataBaseRef.child(uid).child("4").child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long notificationcount = (long) dataSnapshot.getValue();
                    notificationcount++;
                    userDataBaseRef.child(uid).child("4").child("count").setValue(notificationcount);
                } else
                    userDataBaseRef.child(uid).child("4").child("count").setValue(1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void updateNotificationcount(final String fragment, final String uid) {
        userDataBaseRef.child(uid).child(fragment).child("count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long notificationcount = (long) dataSnapshot.getValue();
                    notificationcount++;
                    userDataBaseRef.child(uid).child(fragment).child("count").setValue(notificationcount);
                } else
                    userDataBaseRef.child(uid).child(fragment).child("count").setValue(1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}