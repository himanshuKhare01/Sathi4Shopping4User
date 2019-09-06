package com.sathi4shopping.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sathi4shopping.Activity.WebViewActivity;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.R;
import com.sathi4shopping.templates.Trending;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import io.github.ponnamkarthik.richlinkpreview.MetaData;
import io.github.ponnamkarthik.richlinkpreview.RichLinkListener;
import io.github.ponnamkarthik.richlinkpreview.RichLinkView;
import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class TrendingFragment extends Fragment {
    private DatabaseReference trendingdatabase;
    private RecyclerView trendingrecyclerview;
    private ProgressBarCircularIndeterminate progressBarIndeterminate;
    private View view;
    private Context context;
    private FirebaseRecyclerAdapter<Trending, trendingviewholder> adapter;

    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_trending, container, false);
        initializeVariables();

        if (NetworkBroadcastReceiver.isInternetConnected(getActivity()))
            showProgressBar();
        else
            progressBarIndeterminate.setVisibility(View.GONE);

        showAlltheUploadsfromAdmin();
        upadateRecyclerView();
        return view;
    }

    private void upadateRecyclerView() {
        trendingdatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                adapter.notifyDataSetChanged();
                trendingrecyclerview.smoothScrollToPosition(adapter.getItemCount());
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

    @SuppressLint("WrongConstant")
    private void initializeVariables() {
        trendingrecyclerview = view.findViewById(R.id.trendingrecyclerview);
        progressBarIndeterminate = view.findViewById(R.id.trendingprogressBar);
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setStackFromEnd(true);
        manager.setReverseLayout(true);
        trendingrecyclerview.setLayoutManager(manager);
        trendingdatabase = FirebaseDatabase.getInstance().getReference().child("Trending");
        DatabaseReference trendingnotificationref = FirebaseDatabase.getInstance().getReference().child("TrendingNotifications");
        trendingdatabase.keepSynced(true);
        trendingnotificationref.keepSynced(true);
    }

    private void showAlltheUploadsfromAdmin() {

        FirebaseRecyclerOptions<Trending> options = new FirebaseRecyclerOptions.Builder<Trending>()
                .setQuery(trendingdatabase, Trending.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Trending, trendingviewholder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final trendingviewholder holder, @SuppressLint("RecyclerView") final int position, @NonNull final Trending model) {
                holder.title.setText(model.getTitle());
                holder.description.setText(model.getDescription());
                holder.time.setText(model.getTime());
                try {
                    if (!model.getLink().isEmpty()) {
                        URL url = new URL(model.getLink());
                        holder.richLinkView.setLink(String.valueOf(url), new ViewListener() {
                                    @Override
                                    public void onSuccess(boolean status) {
                                        holder.cardView.setVisibility(View.VISIBLE);
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                holder.richLinkView.setVisibility(View.GONE);
                                            }
                                        });
                                    }
                                }
                        );
                    } else
                        holder.cardView.setVisibility(View.GONE);
                } catch (MalformedURLException e) {
                    holder.cardView.setVisibility(View.GONE);
                    e.printStackTrace();
                }
                holder.richLinkView.setDefaultClickListener(false);
                holder.richLinkView.setClickListener(new RichLinkListener() {
                    @Override
                    public void onClicked(View view, MetaData meta) {
                        startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", model.getLink()));
                    }
                });
                holder.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, model.getDescription() + "\n" + model.getLink());
                        intent.setType("text/plain");
                        if (intent.resolveActivity(context.getPackageManager()) != null)
                            startActivity(intent);
                    }
                });
                progressBarIndeterminate.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                Toast.makeText(context, getString(R.string.slow_internet_connection), Toast.LENGTH_SHORT).show();
                progressBarIndeterminate.setVisibility(View.GONE);
            }


            @Override
            public void onViewAttachedToWindow(@NonNull trendingviewholder holder) {
                super.onViewAttachedToWindow(holder);
                progressBarIndeterminate.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public trendingviewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(context).inflate(R.layout.trending_templayout, viewGroup, false);
                return new trendingviewholder(view);
            }
        };
        trendingrecyclerview.setAdapter(adapter);
        adapter.startListening();
    }

    private void showProgressBar() {
        trendingdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
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

    public class trendingviewholder extends RecyclerView.ViewHolder {
        private TextView title;
        private RichLinkView richLinkView;
        private TextView time;
        private TextView description;
        private TextView share;
        private CardView cardView;

        trendingviewholder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.trending_title);
            richLinkView = itemView.findViewById(R.id.richLinkView);
            time = itemView.findViewById(R.id.trending_time);
            description = itemView.findViewById(R.id.trending_description);
            share = itemView.findViewById(R.id.trending_share);
            cardView = itemView.findViewById(R.id.cardtrending);
        }
    }

}