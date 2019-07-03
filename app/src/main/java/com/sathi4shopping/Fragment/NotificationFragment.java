package com.sathi4shopping.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sathi4shopping.R;
import com.sathi4shopping.templates.Notification;

public class NotificationFragment extends Fragment {
    private RecyclerView notificationRecyclerview;
    private DatabaseReference referencetoNotification;
    private static String cuserId;
    private FirebaseRecyclerAdapter<Notification, NotificationViewHolder> adapter;

    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        notificationRecyclerview = view.findViewById(R.id.notification_recyclerview);

        LinearLayoutManager manager1 = new LinearLayoutManager(getActivity());
        manager1.setStackFromEnd(true);
        manager1.setReverseLayout(true);
        notificationRecyclerview.setLayoutManager(manager1);

        referencetoNotification = FirebaseDatabase.getInstance().getReference().child("Notification");
        cuserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        notificationRecyclerview.setVisibility(View.VISIBLE);
        showAllNotificationToUsers();
        updateRecyclerView();
        return view;
    }

    private void updateRecyclerView() {
        final DatabaseReference reference = referencetoNotification.child(cuserId);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    adapter.notifyDataSetChanged();
                    notificationRecyclerview.smoothScrollToPosition(adapter.getItemCount());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showAllNotificationToUsers() {
        final DatabaseReference reference = referencetoNotification.child(cuserId);
        FirebaseRecyclerOptions<Notification> options = new FirebaseRecyclerOptions.Builder<Notification>()
                .setQuery(reference, Notification.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Notification, NotificationViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, final int position, @NonNull Notification model) {
                if (!model.getNotification().isEmpty() && !model.getTime().isEmpty() && !model.getDate().isEmpty()) {
                    holder.notification.setText(model.getNotification());
                    holder.date.setText(model.getTime() + "\t" + model.getDate());
                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity(), "Swipe to delete", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else
                    holder.itemView.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.notification_template, viewGroup, false);
                return new NotificationViewHolder(view);
            }
        };
        notificationRecyclerview.setAdapter(adapter);
        adapter.startListening();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                        //Remove swiped item from list and notify the RecyclerView
                        int id = notificationRecyclerview.getChildPosition(viewHolder.itemView);
                        DatabaseReference referencetokey = adapter.getRef(id);
                        reference.child(referencetokey.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(getActivity(), getString(R.string.deleted), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(notificationRecyclerview);
//        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
//                notificationRecyclerview,
//                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
//                    @Override
//                    public boolean canDismiss(int position) {
//                        return true;
//                    }
//
//                    @Override
//                    public void onDismiss(View view) {
//                        int id = notificationRecyclerview.getChildPosition(view);
//                        DatabaseReference referencetokey = adapter.getRef(id);
//                        reference.child(referencetokey.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                Toast.makeText(getActivity(), getString(R.string.deleted), Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }).setItemClickCallback(new SwipeDismissRecyclerViewTouchListener.OnItemClickCallBack() {
//            @Override
//            public void onClick(int i) {
//                Toast.makeText(getActivity(), getString(R.string.swipe_to_delete), Toast.LENGTH_SHORT).show();
//            }
//        })
//                .setIsVertical(false)
//                .create();
//        notificationRecyclerview.setOnTouchListener(listener);
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        private TextView notification;
        private TextView date;

        NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification_data);
            date = itemView.findViewById(R.id.notification_date);
        }
    }

}