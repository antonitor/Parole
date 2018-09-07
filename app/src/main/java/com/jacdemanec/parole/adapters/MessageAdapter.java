package com.jacdemanec.parole.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.jacdemanec.parole.model.ChatMessage;
import com.jacdemanec.parole.R;

public class MessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, MessageAdapter.MessageHolder> {

    public MessageAdapter(FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull ChatMessage message) {

        holder.authorTextView.setVisibility(View.VISIBLE);
        //holder.container.setBackgroundResource(R.drawable.user_message_box);
        if (position > 0) {
            if (getItem(position).getName().equals(getItem(position - 1).getName())) {
                holder.authorTextView.setVisibility(View.GONE);
                //holder.container.setBackgroundResource(R.drawable.message_box);
            }
        }
        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            holder.messageTextView.setVisibility(View.GONE);
            holder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(holder.photoImageView);
        } else {
            holder.messageTextView.setVisibility(View.VISIBLE);
            holder.photoImageView.setVisibility(View.GONE);
            holder.messageTextView.setText(message.getText());
        }
        holder.authorTextView.setText(message.getName());
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);

        return new MessageHolder(view);
    }

    class MessageHolder extends RecyclerView.ViewHolder {

        ImageView photoImageView;
        TextView messageTextView;
        TextView authorTextView;
        LinearLayout container;

        public MessageHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            authorTextView = itemView.findViewById(R.id.nameTextView);
            container = (LinearLayout) itemView;
        }
    }

}