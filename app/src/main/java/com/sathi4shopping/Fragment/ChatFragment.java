package com.sathi4shopping.Fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;

import android.app.AlertDialog;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sathi4shopping.Choices.UserOptionsInChat;
import com.sathi4shopping.Class.GetCount;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.R;
import com.sathi4shopping.Variable.GlobalVariable;
import com.sathi4shopping.adapter.UserRecyclerAdapter;
import com.sathi4shopping.templates.UserDisplayTemplate;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference displayData;
    private DatabaseReference chatnotificationRef;
    private FloatingActionButton stConv;
    private static String userUid;
    private ArrayList<UserDisplayTemplate> userQueryTemplates;
    private Spinner cat2;
    private CircleImageView cat0;
    private String title;
    private String subtitle;
    private int item = 0;
    private ArrayAdapter dataAdapter1;
    private ProgressBarCircularIndeterminate progressBar;
    private Uri uri = null;
    private StorageReference ref2;
    private ProgressDialog progressDialog;
    private View view;
    private Context context;
    private UserRecyclerAdapter userAdapter;
    private String count;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        intializeData();
        if (NetworkBroadcastReceiver.isInternetConnected(getActivity())) {
            progressBar.setVisibility(View.VISIBLE);
        } else
            progressBar.setVisibility(View.GONE);
        displayData = displayData.child(userUid);
        addQueryTolist();
        showButton();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void showButton() {
        stConv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                @SuppressLint("InflateParams") View alertLayout = inflater.inflate(R.layout.dialog_layout, null);
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Give some details");
                alert.setView(alertLayout);
                alert.setCancelable(false);
                alert.setNegativeButton("Cancel", null);
                alert.setPositiveButton("Done", null);
                AlertDialog dialog = alert.create();
                dialog.show();
                showInputDialog(dialog, alertLayout);
            }
        });
    }

    private void intializeData() {
        recyclerView = view.findViewById(R.id.recyclerView);
        stConv = view.findViewById(R.id.conver);
        progressBar = view.findViewById(R.id.progressBarCircularIndeterminate);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        userUid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        displayData = FirebaseDatabase.getInstance().getReference().child("Query");
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference ref0 = firebaseStorage.getReference();
        StorageReference ref1 = ref0.child("query_logo");
        ref2 = ref1.child(userUid);
        progressDialog = new ProgressDialog(getActivity());
        chatnotificationRef = FirebaseDatabase.getInstance().getReference().child("QueryNotifications");
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
    }

    private void addQueryTolist() {
        displayData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userQueryTemplates = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        if (dataSnapshot1.exists()) {
                            final String type = (dataSnapshot1).getKey();
                            assert type != null;
                            if (dataSnapshot1.child("user").child("count").exists()) {
                                count = dataSnapshot1.child("user").child("count").getValue().toString();
                            } else
                                count = "0";
                            String visible = (String) dataSnapshot1.child("visible").getValue();
                            String lasttext = (String) dataSnapshot1.child("LastMessage").getValue();
                            String title = (String) dataSnapshot1.child("Title").getValue();
                            String subtitle = (String) dataSnapshot1.child("SubTitle").getValue();
                            String imageuri = (String) dataSnapshot1.child("Uri").getValue();
                            if (title != null && !title.isEmpty() && subtitle != null && !subtitle.isEmpty() && imageuri != null) {
                                if (visible != null && visible.equals("true"))
                                    userQueryTemplates.add(new UserDisplayTemplate(title, subtitle, type, userUid, imageuri, lasttext, count, null));
                            } else
                                displayData.child(type).removeValue();
                        }
                    }
                    userAdapter = new UserRecyclerAdapter(context, userQueryTemplates);
                    recyclerView.setAdapter(userAdapter);
                    progressBar.setVisibility(View.GONE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                uri = result.getUri();
                cat0.setImageURI(uri);
            }
        }
    }

    private void showInputDialog(final AlertDialog dialog, final View view) {
        cat0 = view.findViewById(R.id.cat0);
        Spinner cat1 = view.findViewById(R.id.cat1);
        cat2 = view.findViewById(R.id.cat2);
        TextView cattext = view.findViewById(R.id.textopen);
        cat0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkBroadcastReceiver.isInternetConnected(context))
                    openimageActivity();
                else
                    Toast.makeText(getActivity(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
        cattext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openimageActivity();
            }
        });
        cat1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item = position;
                title = new UserOptionsInChat().getCategories().get(position);
                dataAdapter1 = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, new UserOptionsInChat().list[position]);
                dataAdapter1.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                cat2.setAdapter(dataAdapter1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        cat2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subtitle = new UserOptionsInChat().list[item].get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(Objects.requireNonNull(getActivity()), android.R.layout.simple_spinner_item, new UserOptionsInChat().getCategories());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        cat1.setAdapter(dataAdapter);
        Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference userMessageKeyRef = displayData.push();
                final String messagePushID = userMessageKeyRef.getKey();
                new GetCount().updateTotalQueryCount(GlobalVariable.adminID);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
                final DatabaseReference reference = displayData.child(messagePushID);
                final Map<String, Object> mapinfo = new HashMap<>();
                mapinfo.put("Title", title);
                mapinfo.put("SubTitle", subtitle);
                mapinfo.put("visible", "true");

                if (uri != null)
                    uploadImage(messagePushID, uri);
                else {
                    mapinfo.put("Uri", "");
                    progressDialog.cancel();
                }
                reference.updateChildren(mapinfo).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        final HashMap<String, String> notification = new HashMap<>();
                        notification.put("from", userUid);
                        notification.put("title", subtitle);
                        chatnotificationRef.child(GlobalVariable.adminID).child(messagePushID).setValue(notification);
                    }
                });
                dialog.dismiss();
            }
        });
    }

    private void openimageActivity() {
        CropImage.activity()
                .start(Objects.requireNonNull(getContext()), ChatFragment.this);
    }

    private void uploadImage(final String messagePushID, Uri uriToUpload) {
        final StorageReference ref3 = ref2.child(messagePushID);
        ref3.putFile(uriToUpload).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                ref3.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uriupload) {
                        displayData.child(messagePushID).child("Uri").setValue(uriupload.toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getActivity(), getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                                uri = null;
                                progressDialog.cancel();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                                uri = null;
                                progressDialog.cancel();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                uri = null;
                                progressDialog.cancel();
                                addQueryTolist();
                            }
                        });
                    }
                });
            }
        });
        uri = null;
    }

}