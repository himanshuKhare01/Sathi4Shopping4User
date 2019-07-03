package com.sathi4shopping.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.R;

import java.util.HashMap;
import java.util.Objects;

public class SignInSignUpActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private SignInButton signInButton;
    private DatabaseReference cUserDataRef;
    private CheckBox checkBox;
    private ProgressDialog waitDialog;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_signup);
        intialize();
        setFindViewById();
        setTextBelowBox();
        seeCheckBox();
        googleSignIn();
    }

    private void googleSignIn() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkBroadcastReceiver.isInternetConnected(SignInSignUpActivity.this)) {
                    waitDialog.setTitle("Authenticating");
                    waitDialog.setCanceledOnTouchOutside(false);
                    waitDialog.setMessage("Please wait ...");
                    waitDialog.show();
                    signIn();
                } else
                    Toast.makeText(SignInSignUpActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void intialize() {
        mAuth = FirebaseAuth.getInstance();
        waitDialog = new ProgressDialog(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
    }

    private void seeCheckBox() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkBox.isChecked()) {
                    signInButton.setEnabled(true);
                } else
                    signInButton.setEnabled(false);
            }
        });
    }

    private void setTextBelowBox() {
        textView.setText(Html.fromHtml(getResources().getString(R.string.agree_to_our_privacy_police)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://sathi4shopping.com"));
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
            }
        });
    }

    private void setFindViewById() {
        signInButton = findViewById(R.id.sign_in_button);
        checkBox = findViewById(R.id.checkboxp);
        textView = findViewById(R.id.textpp);
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null && !account.getEmail().equals("sathi4shopping@gmail.com"))
                    firebaseAuthWithGoogle(account);
                else {
                    Toast.makeText(this, "Use admin app", Toast.LENGTH_SHORT).show();
                    waitDialog.dismiss();
                    mAuth.signOut();
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    GoogleSignIn.getClient(this, gso).signOut();
                }
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                waitDialog.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            storeUserData();
                            waitDialog.dismiss();
                            findActivity();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                waitDialog.dismiss();
                Toast.makeText(SignInSignUpActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
            }
        }).addOnCanceledListener(new OnCanceledListener() {
            @Override
            public void onCanceled() {
                waitDialog.dismiss();
            }
        });
    }

    private void findActivity() {
        cUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        cUserDataRef.child("phone").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !Objects.requireNonNull(dataSnapshot.getValue()).toString().isEmpty()) {
                    startActivity(new Intent(SignInSignUpActivity.this, MainActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SignInSignUpActivity.this, VerifyPhoneNumberActivity.class));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    private void storeUserData() {
        cUserDataRef = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
        cUserDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("name", mAuth.getCurrentUser().getDisplayName().toLowerCase());
                    map.put("email", mAuth.getCurrentUser().getEmail());
                    map.put("phone", "");
                    map.put("image", Objects.requireNonNull(mAuth.getCurrentUser().getPhotoUrl()).toString());
                    map.put("device_token", FirebaseInstanceId.getInstance().getToken());
                    map.put("rewards", "0");
                    cUserDataRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(SignInSignUpActivity.this, "SignIn Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignInSignUpActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("device_token", FirebaseInstanceId.getInstance().getToken());
                    cUserDataRef.updateChildren(map);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}