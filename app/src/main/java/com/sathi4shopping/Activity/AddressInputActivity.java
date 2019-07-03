package com.sathi4shopping.Activity;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sathi4shopping.R;

import java.util.HashMap;
import java.util.Objects;

public class AddressInputActivity extends AppCompatActivity {
    TextInputEditText inputEditText, inputEditText1, inputEditText2, inputEditText3, inputEditText4, inputEditText5;
    Button done;
    TextView cancel;
    DatabaseReference userreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setFindViewById();
        setToolbar();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        userreference = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        getAddress();
        setAddress();
    }

    private void setAddress() {
        userreference.child("address").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("House No") != null && dataSnapshot.child("SubLocality") != null && dataSnapshot.child("SubAdminArea") != null && dataSnapshot.child("AdminArea") != null && dataSnapshot.child("Postal Code") != null && dataSnapshot.child("Country Name") != null) {
                        if (!dataSnapshot.child("House No").getValue().toString().isEmpty() && !dataSnapshot.child("SubLocality").getValue().toString().isEmpty() && !dataSnapshot.child("SubAdminArea").getValue().toString().isEmpty() && !dataSnapshot.child("AdminArea").getValue().toString().isEmpty() && !dataSnapshot.child("Postal Code").getValue().toString().isEmpty() && !dataSnapshot.child("Country Name").getValue().toString().isEmpty()) {
                            inputEditText.setText(Objects.requireNonNull(dataSnapshot.child("House No").getValue()).toString());
                            inputEditText1.setText(Objects.requireNonNull(dataSnapshot.child("SubLocality").getValue()).toString());
                            inputEditText2.setText(Objects.requireNonNull(dataSnapshot.child("SubAdminArea").getValue()).toString());
                            inputEditText3.setText(Objects.requireNonNull(dataSnapshot.child("AdminArea").getValue()).toString());
                            inputEditText4.setText(Objects.requireNonNull(dataSnapshot.child("Postal Code").getValue()).toString());
                            inputEditText5.setText(Objects.requireNonNull(dataSnapshot.child("Country Name").getValue()).toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getAddress() {
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAddress()) {
                    HashMap<String, Object> mapaddress = new HashMap<>();
                    mapaddress.put("House No", Objects.requireNonNull(inputEditText.getText()).toString());
                    mapaddress.put("SubLocality", Objects.requireNonNull(inputEditText1.getText()).toString());
                    mapaddress.put("SubAdminArea", Objects.requireNonNull(inputEditText2.getText()).toString());
                    mapaddress.put("AdminArea", Objects.requireNonNull(inputEditText3.getText()).toString());
                    mapaddress.put("Postal Code", Objects.requireNonNull(inputEditText4.getText()).toString());
                    mapaddress.put("Country Name", Objects.requireNonNull(inputEditText5.getText()).toString());
                    userreference.child("address").updateChildren(mapaddress).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(AddressInputActivity.this, getString(R.string.thanks_for_verification), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }
        });
    }

    private void setFindViewById() {
        inputEditText = findViewById(R.id.house_child);
        inputEditText1 = findViewById(R.id.locality_child);
        inputEditText2 = findViewById(R.id.city_child);
        inputEditText3 = findViewById(R.id.state_child);
        inputEditText4 = findViewById(R.id.zip_child);
        inputEditText5 = findViewById(R.id.country_child);
        done = findViewById(R.id.done);
        cancel = findViewById(R.id.canceladdress);
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_address);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Back");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean checkAddress() {
        String input = Objects.requireNonNull(inputEditText.getText()).toString().trim();
        String input1 = Objects.requireNonNull(inputEditText1.getText()).toString().trim();
        String input2 = Objects.requireNonNull(inputEditText2.getText()).toString().trim();
        String input3 = Objects.requireNonNull(inputEditText3.getText()).toString().trim();
        String input4 = Objects.requireNonNull(inputEditText4.getText()).toString().trim();
        String input5 = Objects.requireNonNull(inputEditText5.getText()).toString().trim();
        if (input.isEmpty()) {
            inputEditText.setError("Field can't be empty");
            return false;
        } else if (input1.isEmpty()) {
            inputEditText1.setError("Field can't be empty");
            return false;
        } else if (input2.isEmpty()) {
            inputEditText2.setError("Field can't be empty");
            return false;
        } else if (input3.isEmpty()) {
            inputEditText3.setError("Field can't be empty");
            return false;
        } else if (input4.isEmpty()) {
            inputEditText4.setError("Field can't be empty");
            return false;
        } else if (input4.length() != 6) {
            inputEditText4.setError("Invalid Pincode");
            return false;
        } else if (input5.isEmpty()) {
            inputEditText5.setError("Field can't be empty");
            return false;
        } else {
            return true;
        }
    }
}