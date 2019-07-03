package com.sathi4shopping.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.ProgressDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sathi4shopping.Class.NetworkBroadcastReceiver;
import com.sathi4shopping.R;
import com.sathi4shopping.Variable.GlobalVariable;
import com.sathi4shopping.adapter.MessagesAdapter;
import com.sathi4shopping.templates.Messages;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserChatActivity extends AppCompatActivity {

    private Button sndBtn;
    private EditText editText;
    private static DatabaseReference cUserref;
    private androidx.appcompat.widget.Toolbar toolbar;
    private CircleImageView queryImage;
    private TextView title;
    private TextView subtitle;
    private ImageView backbutton;
    private String senderUID;
    private String receiverUID;
    private final ArrayList<Messages> messagesList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private RecyclerView userMessagesList;
    private String getType;
    private String getUid;
    private String getTitle;
    private String getSubTitle;
    private String getUri;
    private FloatingActionButton floatingActionButton;
    private StorageReference reference2;
    private static String cuserId;
    private String message;
    private ProgressDialog progressDialog;
    private DatabaseReference referenceToUser;
    private DatabaseReference reference;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userchat);
        setfindViewById();
        initialize();
        checkReceiverId();
        initializeDatabase();
        setCountToZero();
        checkUserPresenceSystem();
        setToolbarData();
        displayAllChats(senderUID, receiverUID);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendImageMessage();
        sendTextMessage();
        checkSendImageSendTodatabase();
    }

    private void setfindViewById() {
        editText = findViewById(R.id.sendmessage);
        sndBtn = findViewById(R.id.sendBtn);
        toolbar = findViewById(R.id.toolbar);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        backbutton = findViewById(R.id.backbutton);
        queryImage = findViewById(R.id.queryImage);
        floatingActionButton = findViewById(R.id.send_image_btn);
    }

    @SuppressLint("SetTextI18n")
    private void setToolbarData() {
        title.setText("(" + getTitle + ")");
        subtitle.setText(getSubTitle);
        if (getUri != null && !getUri.isEmpty())
            Picasso.get().load(getUri).into(queryImage);
        else
            queryImage.setImageDrawable(getResources().getDrawable(R.drawable.logo));
    }

    private void checkReceiverId() {
        senderUID = cuserId;
        receiverUID = GlobalVariable.adminID;
    }

    private void initializeDatabase() {
        referenceToUser = FirebaseDatabase.getInstance().getReference().child("Users").child(cuserId);
        reference = FirebaseDatabase.getInstance().getReference().child("QueryCount").child(receiverUID);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference1 = storage.getReference();
        reference2 = reference1.child("chat_image");
        cUserref = FirebaseDatabase.getInstance().getReference().child("Query").child(getUid).child(getType);
    }

    private void initialize() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        cuserId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final String[] strings = getIntent().getStringArrayExtra("user");
        getType = strings[0];
        getUid = strings[1];
        getTitle = strings[2];
        getSubTitle = strings[3];
        getUri = strings[4];
        progressDialog = new ProgressDialog(this);
        messagesAdapter = new MessagesAdapter(messagesList, this);
        userMessagesList = findViewById(R.id.message_query_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void displayAllChats(String senderUID, String receiverUID) {
        if (senderUID != null && receiverUID != null) {
            cUserref.child("Messages").child(senderUID).child(receiverUID).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesList.add(messages);
                    messagesAdapter.notifyDataSetChanged();
                    userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
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
    }

    private void sendTextMessage() {
        sndBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("InlinedApi")
            @Override
            public void onClick(View v) {
                message = editText.getText().toString();
                if (!message.isEmpty()) {
                    message = editText.getText().toString();
                    checkReceiverId();
                    cUserref.child("visible").setValue("true");
                    cUserref.child("LastMessage").setValue(message);
                    updateCount(null);
                    storeMessage(message);
                    editText.setInputType(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                    editText.setText("");
                    sendReply();
                } else
                    editText.setError("Field can't empty");
            }
        });
    }

    private void sendReply() {
        cUserref.child("Messages").child(GlobalVariable.adminID).child(getUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() == 1) {
                            if (!cuserId.equals(GlobalVariable.adminID)) {
                                message = "check inbox";
                                cUserref.child("visible").setValue("true");
                                cUserref.child("LastMessage").setValue(GlobalVariable.message);
                                senderUID = GlobalVariable.adminID;
                                receiverUID = getUid;
                                storeMessage(GlobalVariable.message);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void storeMessage(final String msg) {

        Calendar date = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd,yyyy");
        String currentDate = dateFormat.format(date.getTime());

        Calendar time = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = timeFormat.format(time.getTime());

        String messageSenderRef = "Messages/" + senderUID + "/" + receiverUID;
        String messageReceiverRef = "Messages/" + receiverUID + "/" + senderUID;

        DatabaseReference userMessageKeyRef = cUserref.child("Messages")
                .child(senderUID).child(receiverUID).push();
        String messagePushID = userMessageKeyRef.getKey();


        final HashMap<String, Object> messageTextBody = new HashMap<>();
        messageTextBody.put("message", msg);
        messageTextBody.put("type", "text");
        messageTextBody.put("from", senderUID);
        messageTextBody.put("time", currentTime);
        messageTextBody.put("date", currentDate);

        Map<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);
        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, messageTextBody);
        cUserref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
    }

    private void checkSendImageSendTodatabase() {
        if (getIntent().getBooleanExtra("home", false)) {
            cUserref.child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists())
                        sendImageTodatabase();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    private void sendImageMessage() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkBroadcastReceiver.isInternetConnected(UserChatActivity.this))
                    CropImage.activity().start(UserChatActivity.this);
                else
                    Toast.makeText(UserChatActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToDatabase(Uri uri) {
        checkReceiverId();
        message = "image";
        updateCount(null);

        final String messageSenderRef = "Messages/" + senderUID + "/" + receiverUID;
        final String messageReceiverRef = "Messages/" + receiverUID + "/" + senderUID;
        DatabaseReference userMessageKeyRef = cUserref.child("Messages")
                .child(senderUID).child(receiverUID).push();
        final String messagePushID = userMessageKeyRef.getKey();
        StorageReference reference3 = reference2.child(getType);
        assert messagePushID != null;
        final StorageReference reference = reference3.child(messagePushID);
        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri urisavetodatabase) {
                        cUserref.child("LastMessage").setValue("image");
                        cUserref.child("visible").setValue("true");
                        HashMap<String, Object> imageBody = new HashMap<>();
                        imageBody.put("image", urisavetodatabase.toString());
                        imageBody.put("type", "image");
                        imageBody.put("from", senderUID);
                        Map<String, Object> messageBodyDetails = new HashMap<>();
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, imageBody);
                        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID, imageBody);
                        cUserref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                sendReply();
                                progressDialog.dismiss();
                            }
                        });
                    }
                });
            }
        });
    }

    private void sendImageTodatabase() {
        checkReceiverId();
        cUserref.child("LastMessage").setValue("image");
        cUserref.child("visible").setValue("true");
        message = "image";
        updateCount(getUri);
        final String messageSenderRef = "Messages/" + senderUID + "/" + receiverUID;
        final String messageReceiverRef = "Messages/" + receiverUID + "/" + senderUID;
        HashMap<String, Object> imageBody = new HashMap<>();
        imageBody.put("image", getUri);
        imageBody.put("type", "image");
        imageBody.put("from", senderUID);
        Map<String, Object> messageBodyDetails = new HashMap<>();
        messageBodyDetails.put(messageSenderRef + "/" + getType, imageBody);
        messageBodyDetails.put(messageReceiverRef + "/" + getType, imageBody);
        cUserref.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        });
        sendReply();
    }

    private void checkUserPresenceSystem() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    cUserref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.child("title").exists() && dataSnapshot.child("SubTitle").exists() && dataSnapshot.child("user").child("status").exists())
                                    cUserref.child("user").child("status").onDisconnect().setValue(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserChatActivity.this, getString(R.string.cancel), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog.setMessage("Please wait...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                assert result != null;
                Uri uri = result.getUri();
                messagesAdapter.notifyDataSetChanged();
                userMessagesList.smoothScrollToPosition(Objects.requireNonNull(userMessagesList.getAdapter()).getItemCount());
                saveImageToDatabase(uri);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUserStatus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        setUserStatus(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setUserStatus(true);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        setUserStatus(false);
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

    private void setCountToZero() {
        cUserref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("user").child("count").exists()) {
                        long num = (long) dataSnapshot.child("user").child("count").getValue();
                        decreaseMessageCount(num);
                    }
                    cUserref.child("user").child("count").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void decreaseMessageCount(final long num) {
        referenceToUser.child("messageCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long result = (long) dataSnapshot.getValue();
                    result = result - num;
                    referenceToUser.child("messageCount").setValue(result);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateCount(final String image) {
        cUserref.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.child("status").exists()) {
                    finallyUpdate(dataSnapshot, image);
                } else if (!(boolean) dataSnapshot.child("status").getValue()) {
                    finallyUpdate(dataSnapshot, image);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void finallyUpdate(DataSnapshot dataSnapshot, String image) {
        checkReceiverId();
        if (dataSnapshot.child("count").exists()) {
            int notificationCount = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("count").getValue()).toString());
            notificationCount++;
            cUserref.child("admin").child("count").setValue(notificationCount);
        } else {
            cUserref.child("admin").child("count").setValue(1);
        }
        sendNotification(image);
        reference.child(getType).setValue("");
    }

    private void sendNotification(String image) {
        checkReceiverId();
        String key = cUserref.child("notification").push().getKey();
        HashMap<String, Object> map = new HashMap<>();
        map.put("last_text", message);
        map.put("to", receiverUID);
        map.put("update", key);
        if (image != null)
            map.put("image", image);
        else
            map.put("image", "");
        cUserref.child("notification").updateChildren(map);
    }

    public static void setUserStatus(final boolean checkStatus) {
        cUserref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                        cUserref.child("user").child("status").setValue(checkStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}