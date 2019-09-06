package com.sathi4shopping.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mingle.sweetpick.CustomDelegate;
import com.mingle.sweetpick.SweetSheet;
import com.sathi4shopping.Class.FCMService;
import com.sathi4shopping.Class.GetCount;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.Class.SendNotification;
import com.sathi4shopping.Fragment.ChatFragment;
import com.sathi4shopping.Fragment.EcommerceFragment;
import com.sathi4shopping.Fragment.HomeFragment;
import com.sathi4shopping.Fragment.TrendingFragment;
import com.sathi4shopping.Fragment.WalletFragment;
import com.sathi4shopping.R;
import com.sathi4shopping.Variable.GlobalVariable;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import devlight.io.library.ntb.NavigationTabBar;
import ru.nikartm.support.ImageBadgeView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    TextView tname;
    TextView temail;
    CircleImageView uimage;
    FirebaseAuth mAuth;
    static String current_userId;
    DatabaseReference displayData, referenceToQueryCount;
    FirebaseStorage firebaseStorage;
    StorageReference storageReferenceProfilePic;
    StorageReference userprofilepicreference;
    StorageReference imageRef;
    static DatabaseReference referenceToUsers;
    ProgressDialog loadingBar;
    SweetSheet mSweetSheetPhoneNumber;
    SweetSheet mSweetSheetProfile;
    SweetSheet mSweetSheetAddress;
    DrawerLayout drawer;
    RelativeLayout rl;
    RelativeLayout rl2;
    RelativeLayout rl3;
    CircleImageView circleImageView;
    NavigationTabBar navigationTabBar;
    ArrayList<NavigationTabBar.Model> models;
    Toolbar toolbar;
    NavigationView navigationView;
    View headerView;
    ViewPager viewPager;
    mFragmentAdapter fragmentAdapter;
    String[] colors;
    String tag = null;
    Uri resultUri = null;
    public static final String VERSION_CODE_KEY = "latest_app_version";
    boolean isnewUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFindViewById();
        initializeFirebaseInstances();
        initUI();

        initializeDrawerLayout();
        setUserProfile();
        tag = getIntent().getStringExtra("tag");
        initAllBadge();
        initQueryBadge();
        updateQueryCount();
        initHomeBadge();
        initTrendingBadge();
        initWalletBadge();
        if (FCMService.tag != null) {
            checkFragment(FCMService.tag);
        }
        checkForUpdate();
        if (getIntent().getBooleanExtra("isNewUser", false))
            addCoins(mAuth.getCurrentUser().getUid(), "Congrats 100 coins is added into your wallet as welcome coins you can use it on your next shopping from us.");
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        if (getIntent().getBooleanExtra("isNewUser", false) && pendingDynamicLinkData != null) {
                            Uri deepLink = pendingDynamicLinkData.getLink();
                            String id = deepLink.getQueryParameter("id");
                            addCoins(id, "Congrats 100 coins is added into your wallet for your referral you can use it on your next shopping from us.");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "On Faliure", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addCoins(String uid, String reason) {
        DatabaseReference referenceToUser = FirebaseDatabase.getInstance().getReference().child("Users");
        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("MMM dd");
        String time = format.format(calendar.getTime());
        HashMap<String, Object> map = new HashMap<>();
        map.put("description", reason);
        String amount = "100";
        map.put("change", "+" + 100);
        map.put("amount", amount);
        referenceToUser.child(uid).child("konfettiView").setValue(true);
        referenceToUser.child(uid).child("rewards").setValue(amount);
        map.put("time", time);
        new SendNotification().updateNotificationCount(uid, reason, "3");
        referenceToUser.child(uid).child("coin_history").push().updateChildren(map);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkForUpdate();
    }

    private void checkForUpdate() {

        FirebaseDatabase.getInstance().getReference().child(VERSION_CODE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long latestAppVersion = (long) dataSnapshot.getValue();
                    if (latestAppVersion > getCurrentVersionCode()) {
                        new AlertDialog.Builder(MainActivity.this).setTitle("Update your App")
                                .setMessage("A new version of sathi4shopping is available. Please update it").setPositiveButton(
                                "OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.sathi4shopping")));
                                        } catch (android.content.ActivityNotFoundException anfe) {
                                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + "com.sathi4shopping")));
                                        }
                                    }
                                }).setCancelable(false).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private int getCurrentVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void initAllBadge() {
        for (int i = 0; i <= 3; i++) {
            final int finalI = i;
            referenceToUsers.child(String.valueOf(i)).child("count").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && !dataSnapshot.getValue().toString().equals("0")) {
                        if (finalI == 0 || finalI == 1) {
                            models.get(finalI).showBadge();
                            models.get(finalI).setBadgeTitle(dataSnapshot.getValue().toString());
                        } else {
                            models.get(finalI + 1).showBadge();
                            models.get(finalI + 1).setBadgeTitle(dataSnapshot.getValue().toString());
                        }
                    } else
                        models.get(finalI).hideBadge();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void initWalletBadge() {
        referenceToUsers.child("3").child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("0")) {
                    models.get(4).showBadge();
                    if (dataSnapshot.getValue().toString().equals("1") || tag != null) {
                        models.get(4).setBadgeTitle(dataSnapshot.getValue().toString());
                        tag = null;
                    } else {
                        models.get(4).updateBadgeTitle(dataSnapshot.getValue().toString());
                    }
                } else
                    models.get(4).hideBadge();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void initQueryBadge() {
        referenceToUsers.child("2").child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("0")) {
                    models.get(3).showBadge();
                    if (dataSnapshot.getValue().toString().equals("1") || tag != null) {
                        if (dataSnapshot.getValue().toString().equals("1")) {
                            models.get(3).setBadgeTitle("1");
                            models.get(3).updateBadgeTitle("1");
                        } else
                            models.get(3).setBadgeTitle(dataSnapshot.getValue().toString());
                        tag = null;
                    } else {
                        if (models.get(3).isBadgeShowed())
                            models.get(3).updateBadgeTitle(dataSnapshot.getValue().toString());
                        else
                            models.get(3).setBadgeTitle(dataSnapshot.getValue().toString());
                    }
                } else
                    models.get(3).hideBadge();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void updateQueryCount() {
        referenceToQueryCount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                referenceToUsers.child("2").child("count").setValue(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initTrendingBadge() {
        referenceToUsers.child("1").child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("0")) {
                    models.get(1).showBadge();
                    if (dataSnapshot.getValue().toString().equals("1") || tag != null) {
                        models.get(1).setBadgeTitle(dataSnapshot.getValue().toString());
                        tag = null;
                    } else {
                        models.get(1).updateBadgeTitle(dataSnapshot.getValue().toString());
                    }
                } else
                    models.get(1).hideBadge();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initHomeBadge() {
        referenceToUsers.child("0").child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("0")) {
                    models.get(0).showBadge();
                    if (dataSnapshot.getValue().toString().equals("1") || tag != null) {
                        models.get(0).setBadgeTitle(dataSnapshot.getValue().toString());
                        tag = null;
                    } else {
                        models.get(0).updateBadgeTitle(dataSnapshot.getValue().toString());
                    }
                } else
                    models.get(0).hideBadge();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    public void checkFragment(String tag) {
        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("Users").child(user);

        switch (tag) {
            case "home":
                navigationTabBar.setViewPager(viewPager, 0);
                break;
            case "trending":
                navigationTabBar.setViewPager(viewPager, 1);
                break;
            case "query":
                navigationTabBar.setViewPager(viewPager, 3);
                break;
            case "rewards":
                navigationTabBar.setViewPager(viewPager, 4);
                break;
            case "notification":
                new GetCount().decreaseTotalCount("4", user, "notification");
                rf.child("4").child("count").setValue(0);
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
                break;
        }
    }

    private void initializeDrawerLayout() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void initializeFirebaseInstances() {
        mAuth = FirebaseAuth.getInstance();
        current_userId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        referenceToUsers = FirebaseDatabase.getInstance().getReference().child("Users").child(current_userId);
        referenceToQueryCount = FirebaseDatabase.getInstance().getReference().child("QueryCount").child(current_userId);
        navigationView.setNavigationItemSelectedListener(this);
        displayData = FirebaseDatabase.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReferenceProfilePic = firebaseStorage.getReference();
        userprofilepicreference = storageReferenceProfilePic.child("profile_images");
        imageRef = userprofilepicreference.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + ".jpg");
    }

    private void setFindViewById() {
        drawer = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.nav_view);
        headerView = navigationView.getHeaderView(0);
        tname = headerView.findViewById(R.id.uname);
        temail = headerView.findViewById(R.id.uemail);
        uimage = headerView.findViewById(R.id.userthumbimage);
        circleImageView = findViewById(R.id.updateimage);
        viewPager = findViewById(R.id.vp_horizontal_ntb);
        navigationTabBar = findViewById(R.id.ntb_horizontal);
        loadingBar = new ProgressDialog(this);
        models = new ArrayList<>();
        fragmentAdapter = new mFragmentAdapter(getSupportFragmentManager());
    }

    private void setupCustomView(final SweetSheet sweetSheet) {
        CustomDelegate customDelegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        @SuppressLint("InflateParams") final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_custom_view, null, false);
        final TextView numberuser = view.findViewById(R.id.numberofuser);
        referenceToUsers.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                numberuser.setText("Phone Number :\n" + dataSnapshot.child("phone").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        TextView cancel = view.findViewById(R.id.cancel);
        TextView update = view.findViewById(R.id.update);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetSheet.dismiss();
                mSweetSheetPhoneNumber = null;
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPhoneNumber();
                mSweetSheetPhoneNumber = null;
                sweetSheet.dismiss();
            }
        });
        customDelegate.setCustomView(view);
        sweetSheet.setDelegate(customDelegate);
        sweetSheet.setBackgroundClickEnable(false);
    }

    private void setupCustomView3(final SweetSheet sweetSheet) {
        CustomDelegate customDelegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        @SuppressLint("InflateParams") final View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_custom_view3, null, false);
        final TextView address = view.findViewById(R.id.address);
        referenceToUsers.child("address").addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                address.setText(
                        "My Address :\n\n" +
                                dataSnapshot.child("House No").getValue() + "\t" +
                                dataSnapshot.child("SubLocality").getValue() + "\n" +
                                dataSnapshot.child("SubAdminArea").getValue() + "\n" +
                                dataSnapshot.child("AdminArea").getValue() + "\t" +
                                dataSnapshot.child("Postal Code").getValue() + "\n" +
                                dataSnapshot.child("Country Name").getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        TextView cancel = view.findViewById(R.id.canceladd);
        TextView update = view.findViewById(R.id.updateadd);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetSheet.dismiss();
                mSweetSheetAddress = null;

            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeUserAddress(GlobalVariable.dialogmessage);
                sweetSheet.dismiss();
                mSweetSheetAddress = null;
            }
        });
        customDelegate.setCustomView(view);
        sweetSheet.setDelegate(customDelegate);
        sweetSheet.setBackgroundClickEnable(false);
    }

    private void setupCustomView2(final SweetSheet sweetSheet) {
        CustomDelegate customDelegate = new CustomDelegate(true,
                CustomDelegate.AnimationType.DuangLayoutAnimation);
        @SuppressLint("InflateParams")
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.layout_custom_view2, null, false);
        final TextInputEditText name = view.findViewById(R.id.name);
        referenceToUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                name.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                Picasso.get().load(Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString()).networkPolicy(NetworkPolicy.OFFLINE)
                        .error(R.drawable.thumbnaildefault).into(circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString())
                                .placeholder(R.drawable.thumbnaildefault).into(circleImageView);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        TextView cancelP = view.findViewById(R.id.cancel2);
        TextView updateP = view.findViewById(R.id.updateprofile);
        circleImageView = view.findViewById(R.id.updateimage);
        cancelP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sweetSheet.dismiss();
                mSweetSheetProfile = null;
            }
        });
        updateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = Objects.requireNonNull(name.getText()).toString();
                if (input.isEmpty())
                    name.setError("Name can't be empty");
                else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", input.toLowerCase());
                    referenceToUsers.updateChildren(map);
                    if (resultUri != null) {
                        uploadfile(resultUri);
                        Picasso.get().load(resultUri).placeholder(R.drawable.thumbnaildefault).error(R.drawable.thumbnaildefault).into(uimage);
                    }
                    Toast.makeText(MainActivity.this, getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                    sweetSheet.dismiss();
                    mSweetSheetProfile = null;
                }
            }
        });
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkBroadcastReceiver.isInternetConnected(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else
                    CropImage.activity().start(MainActivity.this);
            }
        });
        customDelegate.setCustomView(view);
        sweetSheet.setDelegate(customDelegate);
        sweetSheet.setBackgroundClickEnable(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                circleImageView.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void uploadfile(final Uri uri) {
        if (uri != null) {
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(false);
            imageRef.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            loadingBar.dismiss();
                            imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    displayData.child("Users").child(current_userId).child("image").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            Toast.makeText(MainActivity.this, getString(R.string.profile_image_update), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText(MainActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception exception) {
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            loadingBar.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        }
    }

    public void setUserProfile() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            referenceToUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        temail.setText(Objects.requireNonNull(dataSnapshot.child("email").getValue()).toString());
                        tname.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                        final String uri = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                        if (uri != null && !uri.isEmpty()) {
                            Picasso.get().load(uri).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.thumbnaildefault).error(R.drawable.thumbnaildefault).into(uimage, new Callback() {
                                @Override
                                public void onSuccess() {
                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(uri).placeholder(R.drawable.thumbnaildefault).error(R.drawable.thumbnaildefault).into(uimage);
                                }
                            });
                        } else
                            Picasso.get().load(R.drawable.thumbnaildefault).into(uimage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mSweetSheetProfile != null || mSweetSheetAddress != null || mSweetSheetPhoneNumber != null) {
            if (mSweetSheetProfile != null && mSweetSheetProfile.isShow()) {
                mSweetSheetProfile.dismiss();
                mSweetSheetProfile = null;
            } else if (mSweetSheetPhoneNumber != null && mSweetSheetPhoneNumber.isShow()) {
                mSweetSheetPhoneNumber.dismiss();
                mSweetSheetPhoneNumber = null;
            } else if (mSweetSheetAddress != null && mSweetSheetAddress.isShow()) {
                mSweetSheetAddress.dismiss();
                mSweetSheetAddress = null;
            }

        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.item_notification);
        final ImageBadgeView imageBadgeView = item.getActionView().findViewById(R.id.notification_badge);
        final String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("Users").child(user);

        referenceToUsers.child("4").child("count").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && !Objects.requireNonNull(dataSnapshot.getValue()).toString().equals("0")) {
                    imageBadgeView.setBadgeValue(Integer.valueOf(dataSnapshot.getValue().toString()));
                } else
                    imageBadgeView.setBadgeValue(0);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        imageBadgeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new GetCount().decreaseTotalCount("4", user, "notification");
                rf.child("4").child("count").setValue(0);
                imageBadgeView.setBadgeValue(0);
                startActivity(new Intent(MainActivity.this, NotificationActivity.class));
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.logout:
                logoutUser();
                break;
            case R.id.rate:
                rateApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void rateApp() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sathi4shopping"));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void shareApp() {
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.sathi4shopping.com?id=" + mAuth.getCurrentUser().getUid()))
                .setDomainUriPrefix("https://s4s.page.link")
                // Open links with this app on Android
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                // Open links with com.example.ios on iOS
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            Uri shortLink = task.getResult().getShortLink();
                            startActivity(new Intent(MainActivity.this, ReferAndEarnActivity.class).putExtra("referLink", "" + shortLink));
                        } else {
                            Toast.makeText(MainActivity.this, "try again: No internet connection" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "try again: No internet connection" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logoutUser() {
        mAuth.signOut();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        GoogleSignIn.getClient(this, gso).signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                startActivity(new Intent(MainActivity.this, SignInSignUpActivity.class));
                finish();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                ((ViewPager) findViewById(R.id.vp_horizontal_ntb)).setCurrentItem(0);
                break;
            case R.id.nav_trending:
                ((ViewPager) findViewById(R.id.vp_horizontal_ntb)).setCurrentItem(1);
                break;
            case R.id.nav_inbox:
                ((ViewPager) findViewById(R.id.vp_horizontal_ntb)).setCurrentItem(3);
                break;
            case R.id.nav_wallet:
                ((ViewPager) findViewById(R.id.vp_horizontal_ntb)).setCurrentItem(4);
                break;
            case R.id.nav_eshopping:
                ((ViewPager) findViewById(R.id.vp_horizontal_ntb)).setCurrentItem(2);
                break;
            case R.id.nav_phone:
                checkPhoneNumber();
                break;
            case R.id.nav_Address:
                checkAddress();
                break;
            case R.id.nav_profile:
                rl2 = findViewById(R.id.rl);
                mSweetSheetProfile = new SweetSheet(rl2);
                setupCustomView2(mSweetSheetProfile);
                mSweetSheetProfile.toggle();
                break;
            case R.id.nav_about:
                sendToWebsite();
                break;
            case R.id.nav_help:
                sendToGmail("Help and feedback", "sathi4shopping@gmail.com");
                break;
            case R.id.nav_privacy:
                sendToPrivacyPolice();
                break;
            case R.id.report_problem:
                sendToGmail("Bugs/Issues in S4S App", "contact@des2dev.com");
                break;
            case R.id.terms:
                sendToTermsCondition();
                break;
            case R.id.membership:
                startActivity(new Intent(this, MemberShipActivity.class));
                break;
            case R.id.refer:
                shareApp();
                break;
            case R.id.contact:
                sendToWhatsApp();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendToWhatsApp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=919213333639&text=&source=&data="));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    private void sendToTermsCondition() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.website)));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    private void sendToPrivacyPolice() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.website)));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    private void sendToWebsite() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.website)));
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }

    private void sendToGmail(String subject, String email) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.CATEGORY_APP_EMAIL, true);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        startActivity(intent);
    }

    private void checkAddress() {
        referenceToUsers.child("address").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    takeUserAddress(GlobalVariable.dialogmessage);
                else {
                    rl3 = findViewById(R.id.rl);
                    mSweetSheetAddress = new SweetSheet(rl3);
                    setupCustomView3(mSweetSheetAddress);
                    mSweetSheetAddress.toggle();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void takeUserAddress(String message) {
        final ProgressDialog verify = new ProgressDialog(this);
        verify.setMessage(message);
        verify.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(MainActivity.this, AddressInputActivity.class));
                    verify.dismiss();
                }
            }
        }.start();
    }

    private void checkPhoneNumber() {
        referenceToUsers.child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.getValue(), ""))
                    verifyPhoneNumber();
                else {
                    rl = findViewById(R.id.rl);
                    mSweetSheetPhoneNumber = new SweetSheet(rl);
                    setupCustomView(mSweetSheetPhoneNumber);
                    mSweetSheetPhoneNumber.toggle();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void verifyPhoneNumber() {
        final ProgressDialog verify = new ProgressDialog(this);
        verify.setMessage(GlobalVariable.dialogmessage);
        verify.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(new Intent(MainActivity.this, VerifyPhoneNumberActivity.class)
                            .putExtra("MainActivity", true));
                    verify.dismiss();
                }
            }
        }.start();
    }

    class mFragmentAdapter extends FragmentPagerAdapter {
        ArrayList<Fragment> fragmentArrayList = new ArrayList<>();

        mFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentArrayList.get(i);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        void addFragment(Fragment fragment) {
            fragmentArrayList.add(fragment);
        }
    }

    private void initUI() {
        fragmentAdapter.addFragment(new HomeFragment());
        fragmentAdapter.addFragment(new TrendingFragment());
        fragmentAdapter.addFragment(new EcommerceFragment());
        fragmentAdapter.addFragment(new ChatFragment());
        fragmentAdapter.addFragment(new WalletFragment());

        viewPager.setAdapter(fragmentAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    //Defining ColorStateList for menu item Text
                    getWindow().setStatusBarColor(getResources().getColor(R.color.color1));
                    findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.color1));
                    ((Toolbar) findViewById(R.id.toolbar)).getWrapper().setTitle("Home");
                    ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.navheaderlayout).setBackgroundColor(getResources().getColor(R.color.color1));
                } else if (i == 1) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.color2));
                    findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.color2));
                    ((Toolbar) findViewById(R.id.toolbar)).getWrapper().setTitle("Trending");
                    ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.navheaderlayout).setBackgroundColor(getResources().getColor(R.color.color2));
                } else if (i == 2) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.color5));
                    findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.color5));
                    ((Toolbar) findViewById(R.id.toolbar)).getWrapper().setTitle("E-Shopping");
                    ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.navheaderlayout).setBackgroundColor(getResources().getColor(R.color.color5));
                } else if (i == 3) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.color3));
                    findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.color3));
                    ((Toolbar) findViewById(R.id.toolbar)).getWrapper().setTitle("Inbox");
                    ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.navheaderlayout).setBackgroundColor(getResources().getColor(R.color.color3));
                } else if (i == 4) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.color4));
                    findViewById(R.id.toolbar).setBackgroundColor(getResources().getColor(R.color.color4));
                    ((Toolbar) findViewById(R.id.toolbar)).getWrapper().setTitle("E-Wallet");
                    ((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0).findViewById(R.id.navheaderlayout).setBackgroundColor(getResources().getColor(R.color.color4));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        colors = getResources().getStringArray(R.array.default_preview);
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(
                                R.drawable.ic_home_white_24dp),
                        Color.parseColor(colors[0]))
                        .title("Home")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_home_white_24dp))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_whatshot_selected_24dp),
                        Color.parseColor(colors[1]))
                        .title("Trending")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_whatshot_selected_24dp))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_shopping_cart),
                        Color.parseColor(colors[2]))
                        .title("E-Shopping")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_shopping_cart))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_forum_white_24dp),
                        Color.parseColor(colors[3]))
                        .title("Inbox")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_forum_white_24dp))
                        .build()
        );
        models.add(
                new NavigationTabBar.Model.Builder(
                        getResources().getDrawable(R.drawable.ic_account_balance_wallet_white_24dp),
                        Color.parseColor(colors[4]))
                        .title("My Wallet")
                        .selectedIcon(getResources().getDrawable(R.drawable.ic_account_balance_wallet_white_24dp))
                        .build()
        );
        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, 2);
        navigationTabBar.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                navigationTabBar = findViewById(R.id.ntb_horizontal);
                models.get(position).hideBadge();
                String user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference rf = FirebaseDatabase.getInstance().getReference().child("Users").child(user);
                switch (position) {
                    case 0:
                        new GetCount().decreaseTotalCount("0", user, "notification");
                        rf.child("0").child("count").setValue(0);
                        break;
                    case 1:
                        new GetCount().decreaseTotalCount("1", user, "notification");
                        rf.child("1").child("count").setValue(0);
                        break;
                    case 3:
                        new GetCount().decreaseTotalCount("2", user, "notification");
                        rf.child("2").child("count").setValue(0);
                        break;
                    case 4:
                        new GetCount().decreaseTotalCount("3", user, "notification");
                        rf.child("3").child("count").setValue(0);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(final int state) {

            }
        });
    }
}