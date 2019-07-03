package com.sathi4shopping.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sathi4shopping.Activity.CoinsHistory;
import com.sathi4shopping.R;
public class WalletFragment extends Fragment {
    private static DatabaseReference referenceToUser;
    private TextView rewardsCount;
    private static String uid;
    private View view;
    private TextView coinshistory;
    private KonfettiView konfettiView;

    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_wallet, container, false);
        initializeData();
        showornot();
        setRewardsAmount();
        coinshistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CoinsHistory.class));
            }
        });
        return view;
    }

    private void setRewardsAmount() {
        referenceToUser.child(uid).child("rewards").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&!dataSnapshot.getValue().toString().isEmpty())
                rewardsCount.setText(dataSnapshot.getValue().toString());
                else
                    rewardsCount.setText("0");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void initializeData() {
        rewardsCount = view.findViewById(R.id.rewardscount);
        coinshistory = view.findViewById(R.id.coinshistory);
        konfettiView=view.findViewById(R.id.viewKonfetti);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        referenceToUser = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getActivity() != null){
            showornot();
            referenceToUser.child(uid).child("konfettiView").setValue(false);
        }
    }

    private void showornot() {
        referenceToUser.child(uid).child("konfettiView").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&(Boolean)dataSnapshot.getValue()){
                   konfettiView.build().
                    addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA).
                    setDirection(0.0, 2000.0).
                    setSpeed(1f, 5f).
                    setFadeOutEnabled(true).
                    setTimeToLive(10000L).
                    addShapes(Shape.RECT, Shape.CIRCLE).
                    addSizes(new Size(12, 5)).
                    setPosition(-10f, konfettiView.getWidth() + 500f, -10f, -10f).
                    streamFor(50, 10000L);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}