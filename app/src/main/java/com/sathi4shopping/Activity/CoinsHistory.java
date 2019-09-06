package com.sathi4shopping.Activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.R;
import com.sathi4shopping.templates.RewardHistory;

import java.util.Objects;

public class CoinsHistory extends AppCompatActivity {
    RecyclerView recyclerView;
    Toolbar toolbar;
    DatabaseReference referencetorewards;
    ProgressBarCircularIndeterminate progressBarCircularIndeterminate;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coinshistory);
        intialize();
        setToolbar();
        setManager();
        showRewardsHistory();
    }

    private void setManager() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
    }

    private void intialize() {
        getWindow().setStatusBarColor(getResources().getColor(R.color.color4));
        recyclerView = findViewById(R.id.coinsHistoryRecyclerView);
        toolbar = findViewById(R.id.toolbar_coins);
        progressBarCircularIndeterminate = findViewById(R.id.progressBarCoins);

        if (NetworkBroadcastReceiver.isInternetConnected(this))
            progressBarCircularIndeterminate.setVisibility(View.VISIBLE);
        else
            progressBarCircularIndeterminate.setVisibility(View.GONE);

        referencetorewards = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("coin_history");
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Rewards History");
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showRewardsHistory() {
        FirebaseRecyclerOptions<RewardHistory> options = new FirebaseRecyclerOptions.Builder<RewardHistory>()
                .setQuery(referencetorewards, RewardHistory.class)
                .build();
        final FirebaseRecyclerAdapter<RewardHistory, ViewHolder> adapter = new FirebaseRecyclerAdapter<RewardHistory, ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull RewardHistory model) {
                holder.desciption.setText(model.getDescription());
                holder.time.setText(model.getTime());
                holder.change.setText(model.getChange());
                if (Integer.parseInt(model.getChange()) > 0)
                    holder.change.setTextColor(getResources().getColor(R.color.color4));
                else
                    holder.change.setTextColor(getResources().getColor(R.color.color5));


            }

            @Override
            public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
                super.onAttachedToRecyclerView(recyclerView);
                progressBarCircularIndeterminate.setVisibility(View.GONE);
            }

            @Override
            public void onError(@NonNull DatabaseError error) {
                super.onError(error);
                progressBarCircularIndeterminate.setVisibility(View.GONE);
            }

            @Override
            public void onViewAttachedToWindow(@NonNull ViewHolder holder) {
                super.onViewAttachedToWindow(holder);
                progressBarCircularIndeterminate.setVisibility(View.GONE);
            }

            @Override
            public boolean onFailedToRecycleView(@NonNull ViewHolder holder) {
                progressBarCircularIndeterminate.setVisibility(View.GONE);
                return super.onFailedToRecycleView(holder);
            }

            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(CoinsHistory.this).inflate(R.layout.coins_history, viewGroup, false);
                return new ViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView desciption;
        TextView time;
        TextView change;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            desciption = itemView.findViewById(R.id.description);
            time = itemView.findViewById(R.id.time_coins);
            change = itemView.findViewById(R.id.change_coins);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
