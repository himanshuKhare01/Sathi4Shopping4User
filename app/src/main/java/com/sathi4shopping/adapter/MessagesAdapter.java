package com.sathi4shopping.adapter;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.sathi4shopping.Class.PhotoFullPopupWindow;
import com.sathi4shopping.R;
import com.sathi4shopping.templates.Messages;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private ArrayList<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private Context context;
    public MessagesAdapter(ArrayList<Messages> userMessagesList, Context context) {
        this.userMessagesList = userMessagesList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.custom_message_layout, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageholder, int i) {
        String messengeSenderId = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        final Messages messages = userMessagesList.get(i);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        String timedate = messages.getTime();

        if (fromMessageType.equals("text")) {
            messageholder.receiverlayout.setVisibility(View.GONE);
            messageholder.senderlayout.setVisibility(View.GONE);
            messageholder.cardViewreceiver.setVisibility(View.GONE);
            messageholder.cardViewsender.setVisibility(View.GONE);
            if (fromUserID.equals(messengeSenderId)) {
                messageholder.senderlayout.setVisibility(View.VISIBLE);
                messageholder.senderMessageText.setText(messages.getMessage());
                messageholder.sendtimedate.setText(timedate);
            } else {
                messageholder.receiverlayout.setVisibility(View.VISIBLE);
                messageholder.receiverMessageText.setText(messages.getMessage());
                messageholder.receivetimedate.setText(timedate);
            }
        } else if (fromMessageType.equals("image")) {
            messageholder.receiverlayout.setVisibility(View.GONE);
            messageholder.senderlayout.setVisibility(View.GONE);
            messageholder.cardViewreceiver.setVisibility(View.GONE);
            messageholder.cardViewsender.setVisibility(View.GONE);
            if (fromUserID.equals(messengeSenderId)) {
                messageholder.cardViewsender.setVisibility(View.VISIBLE);
                messageholder.senderImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoFullPopupWindow(context, messageholder.senderImage, messages.getImage(), null);
                    }
                });
                Picasso.get().load(messages.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(messageholder.senderImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(messages.getImage()).into(messageholder.senderImage);
                    }
                });
            } else {
                messageholder.cardViewreceiver.setVisibility(View.VISIBLE);
                messageholder.receiverImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoFullPopupWindow(context, messageholder.senderImage, messages.getImage(), null);
                    }
                });
                Picasso.get().load(messages.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(messageholder.receiverImage, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(messages.getImage()).into(messageholder.receiverImage);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout receiverlayout;
        private LinearLayout senderlayout;
        private TextView receiverMessageText;
        private TextView senderMessageText;
        private TextView receivetimedate;
        private TextView sendtimedate;
        private ImageView senderImage;
        private ImageView receiverImage;
        private CardView cardViewsender;
        private CardView cardViewreceiver;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMessageText = itemView.findViewById(R.id.receiver_message_txt);
            senderMessageText = itemView.findViewById(R.id.sender_messages_txt);
            receiverlayout = itemView.findViewById(R.id.receive_layout);
            senderlayout = itemView.findViewById(R.id.send_layout);
            receivetimedate = itemView.findViewById(R.id.receive_time_txt);
            sendtimedate = itemView.findViewById(R.id.send_time_txt);
            senderImage = itemView.findViewById(R.id.sender_image);
            receiverImage = itemView.findViewById(R.id.receive_image);
            cardViewsender = itemView.findViewById(R.id.card_sender);
            cardViewreceiver = itemView.findViewById(R.id.card_receiver);
        }
    }

}