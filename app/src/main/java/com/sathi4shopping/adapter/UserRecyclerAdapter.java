package com.sathi4shopping.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sathi4shopping.Activity.UserChatActivity;
import com.sathi4shopping.Class.PhotoFullPopupWindow;
import com.sathi4shopping.R;
import com.sathi4shopping.templates.UserDisplayTemplate;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder> {
    private Context context;
    private ArrayList<UserDisplayTemplate> list;
    private DatabaseReference referenceToUser;
    private String cuserid;

    public UserRecyclerAdapter(Context context, ArrayList<UserDisplayTemplate> list) {
        this.context = context;
        this.list = list;
        referenceToUser = FirebaseDatabase.getInstance().getReference().child("Query").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        cuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.chatusertemplate, viewGroup, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, @SuppressLint("RecyclerView") final int i) {
            viewHolder.title.setText("(" + list.get(i).getTitle() + ")");
            viewHolder.subtitle.setText(list.get(i).getSubtitle());
            viewHolder.circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (list.get(i).getUri() != null && !list.get(i).getUri().isEmpty())
                        new PhotoFullPopupWindow(context, viewHolder.circleImageView, list.get(i).getUri(), null);
                    else
                        new PhotoFullPopupWindow(context, viewHolder.circleImageView, null, null);
                }
            });

            if (list.get(i).getUri() != null && !list.get(i).getUri().isEmpty())
                Picasso.get().load(list.get(i).getUri()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.circleImageView, new Callback() {
                    @Override
                    public void onSuccess() {

                    }
                    @Override
                    public void onError(Exception e) {
                        Picasso.get().load(list.get(i).getUri()).into(viewHolder.circleImageView);
                    }
                });
            else
                viewHolder.circleImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.logo));

            if (list.get(i).getLasttext() != null) {
                viewHolder.lastMessage.setVisibility(View.VISIBLE);
                viewHolder.lastMessage.setText(list.get(i).getLasttext());
            } else
                viewHolder.lastMessage.setVisibility(View.GONE);

            if (list.get(i).getCount().equals("0")) {
                viewHolder.number.setVisibility(View.GONE);
                viewHolder.lastMessage.setTypeface(null, Typeface.NORMAL);
            } else {
                viewHolder.number.setVisibility(View.VISIBLE);
                viewHolder.number.setText(list.get(i).getCount());
                viewHolder.lastMessage.setTypeface(null, Typeface.BOLD);
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkCount(list.get(i).getType());
                    Intent intent = new Intent(context, UserChatActivity.class);
                    intent.putExtra("user", new String[]{list.get(i).getType(), list.get(i).getUid(), list.get(i).getTitle(), list.get(i).getSubtitle(), list.get(i).getUri()});
                    intent.putExtra("image", true);
                    context.startActivity(intent);
                }
            });
            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setMessage("Delete Conversation");
                    alert.setCancelable(false);
                    alert.setNegativeButton("Cancel", null);
                    alert.setPositiveButton("Yes", null);
                    final AlertDialog dialog = alert.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkCount(list.get(i).getType());
                            DatabaseReference reference = referenceToUser.child(list.get(i).getType());
                            reference.child("visible").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    viewHolder.itemView.setVisibility(View.GONE);
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    return true;
                }
            });
    }

    private void checkCount(String type) {
        FirebaseDatabase.getInstance().getReference().child("QueryCount").
                child(cuserid).child(type).removeValue();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView subtitle;
        CircleImageView circleImageView;
        TextView lastMessage;
        TextView number;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.username);
            subtitle = itemView.findViewById(R.id.category);
            circleImageView = itemView.findViewById(R.id.Userimage);
            lastMessage = itemView.findViewById(R.id.lastmesage);
            number = itemView.findViewById(R.id.unread_messages);
        }
    }

}