package com.sathi4shopping.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sathi4shopping.Activity.UserChatActivity;
import com.sathi4shopping.Class.GetCount;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.Class.PhotoFullPopupWindow;
import com.sathi4shopping.R;
import com.sathi4shopping.Variable.GlobalVariable;
import com.sathi4shopping.templates.Home;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private DatabaseReference homedatabase;
    private RecyclerView homerecyclerview;
    private FirebaseRecyclerAdapter<Home, homeviewholder> adapter;
    private DatabaseReference likedataaseRef;
    private DatabaseReference dislikedatabadeRef;
    private Boolean likecheck = false, dislikecheck = false;
    private static String uid;
    private long countlikes = 0, countdislikes = 0;
    private ProgressBarCircularIndeterminate progressBarIndeterminate;
    private DatabaseReference messageDatabaseReference;
    private View view;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initializeData();
        if (NetworkBroadcastReceiver.isInternetConnected(getActivity()))
            showProgressBar();
        else
            progressBarIndeterminate.setVisibility(View.GONE);
        showAlltheUploadsfromAdmin();
        updateRecyclerView();
        return view;
    }

    private void initializeData() {
        homerecyclerview = view.findViewById(R.id.homerecyclerview);
        progressBarIndeterminate = view.findViewById(R.id.homeprogressBar);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        homerecyclerview.setLayoutManager(manager);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        homedatabase = FirebaseDatabase.getInstance().getReference().child("Home");
        likedataaseRef = FirebaseDatabase.getInstance().getReference().child("Like");
        dislikedatabadeRef = FirebaseDatabase.getInstance().getReference().child("DisLike");
        messageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Query").child(uid);
    }

    private void updateRecyclerView() {
        homedatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.notifyDataSetChanged();
                homerecyclerview.smoothScrollToPosition(adapter.getItemCount());
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

    private void showProgressBar() {
        homedatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressBarIndeterminate.setVisibility(View.VISIBLE);
                } else
                    progressBarIndeterminate.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showAlltheUploadsfromAdmin() {
        FirebaseRecyclerOptions<Home> options = new FirebaseRecyclerOptions.Builder<Home>()
                .setQuery(homedatabase, Home.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Home, homeviewholder>(options) {

            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull final homeviewholder holder, int position, @NonNull final Home model) {
                DatabaseReference reference = getRef(position);
                final String key = reference.getKey();
                holder.title.setText("(" + model.getTitle() + ")");
                holder.subtitle.setText(model.getSubtitle());
                holder.description.setText(model.getDescription());
                holder.time.setText(model.getTime());

                Picasso.get().load(model.getImage()).networkPolicy(NetworkPolicy.OFFLINE)
                        .into(holder.imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarIndeterminate.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(model.getImage()).into(holder.imageView);
                                progressBarIndeterminate.setVisibility(View.GONE);
                            }
                        });
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoFullPopupWindow(getActivity(), holder.imageView, model.getImage(), null);
                    }
                });
                holder.setLikeButtonStatus(key);
                holder.setDisLikeButtonStatus(key);
                holder.interested.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                                .setMessage("Know more about this item.")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (NetworkBroadcastReceiver.isInternetConnected(getActivity())) {
                                            final Map<String, Object> mapinfo = new HashMap<>();
                                            mapinfo.put("Title", model.getTitle());
                                            mapinfo.put("SubTitle", model.getSubtitle());
                                            mapinfo.put("Uri", model.getImage());
                                            mapinfo.put("visible", "true");
                                            final ProgressDialog dialog1 = new ProgressDialog(getActivity());
                                            dialog1.setMessage("Please wait...");
                                            dialog1.show();
                                            dialog1.setCanceledOnTouchOutside(false);
                                            messageDatabaseReference.child(key).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (!dataSnapshot.exists())
                                                        new GetCount().updateTotalQueryCount(GlobalVariable.adminID);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                                }
                                            });
                                            final DatabaseReference reference = messageDatabaseReference.child(key);
                                            reference.updateChildren(mapinfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    dialog1.dismiss();
                                                    Intent intent = new Intent(getActivity(), UserChatActivity.class);
                                                    intent.putExtra("user", new String[]{key, uid, model.getTitle(), model.getSubtitle(), model.getImage()});
                                                    intent.putExtra("home", true);
                                                    startActivity(intent);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog1.dismiss();
                                                    Toast.makeText(getActivity(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else
                                            Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setCancelable(false);

                        builder.show();
                    }
                });
                holder.dislikes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dislikecheck = true;
                        dislikedatabadeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dislikecheck) {
                                    if (dataSnapshot.child(key).hasChild(uid)) {
                                        dislikedatabadeRef.child(key).child(uid).removeValue();
                                        dislikecheck = false;
                                    } else {
                                        dislikedatabadeRef.child(key).child(uid).setValue(true);
                                        dislikecheck = false;
                                        likedataaseRef.child(key).child(uid).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                });
                holder.likes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        likecheck = true;
                        likedataaseRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (likecheck) {
                                    if (dataSnapshot.child(key).hasChild(uid)) {
                                        likedataaseRef.child(key).child(uid).removeValue();
                                        likecheck = false;
                                    } else {
                                        likedataaseRef.child(key).child(uid).setValue(true);
                                        likecheck = false;
                                        dislikedatabadeRef.child(key).child(uid).removeValue();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }

                        });
                    }
                });
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Toast.makeText(getActivity(), getString(R.string.slow_internet_connection), Toast.LENGTH_SHORT).show();
                progressBarIndeterminate.setVisibility(View.GONE);
            }

            @Override
            public void onViewAttachedToWindow(@NonNull HomeFragment.homeviewholder holder) {
                super.onViewAttachedToWindow(holder);
                progressBarIndeterminate.setVisibility(View.GONE);
            }


            @NonNull
            @Override
            public homeviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_templayout, viewGroup, false);
                return new homeviewholder(view);
            }
        };
        homerecyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    public class homeviewholder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subtitle;
        private TextView time;
        private TextView description;
        private ImageView imageView;
        private ImageView likes;
        private TextView nooflikes;
        private TextView interested;
        private ImageView dislikes;
        private TextView no_of_dislikes;

        homeviewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.home_title);
            subtitle = itemView.findViewById(R.id.home_subtitle);
            time = itemView.findViewById(R.id.home_time);
            description = itemView.findViewById(R.id.home_description);
            imageView = itemView.findViewById(R.id.home_image);
            likes = itemView.findViewById(R.id.like);
            nooflikes = itemView.findViewById(R.id.no_of_likes);
            interested = itemView.findViewById(R.id.interested);
            dislikes = itemView.findViewById(R.id.dislike);
            no_of_dislikes = itemView.findViewById(R.id.no_of_dislikes);
            interested.setEnabled(true);
        }

        void setLikeButtonStatus(final String key) {
            likedataaseRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).hasChild(uid)) {
                        countlikes = dataSnapshot.child(key).getChildrenCount();
                        likes.setImageResource(R.drawable.ic_thumb_up_like_24dp);
                        nooflikes.setText("" + countlikes);
                    } else {
                        countlikes = dataSnapshot.child(key).getChildrenCount();
                        likes.setImageResource(R.drawable.ic_thumb_up_white_24dp);
                        nooflikes.setText("" + countlikes);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void setDisLikeButtonStatus(final String key) {
            dislikedatabadeRef.addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).hasChild(uid)) {
                        countdislikes = (int) dataSnapshot.child(key).getChildrenCount();
                        dislikes.setImageResource(R.drawable.ic_thumb_down_dislike_24dp);
                        no_of_dislikes.setText("" + countdislikes);
                    } else {
                        countdislikes = (int) dataSnapshot.child(key).getChildrenCount();
                        dislikes.setImageResource(R.drawable.ic_thumb_down_white_24dp);
                        no_of_dislikes.setText("" + countdislikes);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

}