package com.jacdemanec.parole.adapters;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.jacdemanec.parole.ImageActvity;
import com.jacdemanec.parole.model.ChatMessage;
import com.jacdemanec.parole.R;

public class MessageAdapter extends FirebaseRecyclerAdapter<ChatMessage, MessageAdapter.MessageHolder> {

    public MessageAdapter(FirebaseRecyclerOptions<ChatMessage> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull final ChatMessage message) {

        holder.authorTextView.setVisibility(View.VISIBLE);
        holder.view.setBackgroundResource(R.drawable.triangle_chat_box);
        if (position > 0) {
            if (getItem(position).getName().equals(getItem(position - 1).getName())) {
                holder.authorTextView.setVisibility(View.GONE);
                holder.view.setBackgroundResource(R.drawable.round_chat_box);
            }
        }
        boolean isPhoto = message.getPhotoUrl() != null;
        if (isPhoto) {
            holder.messageTextView.setVisibility(View.GONE);
            holder.photoImageView.setVisibility(View.VISIBLE);
            Glide.with(holder.photoImageView.getContext())
                    .load(message.getPhotoUrl())
                    .into(holder.photoImageView);
            holder.photoImageView.setOnClickListener(view -> {
                Intent intent = new Intent(view.getContext(), ImageActvity.class);
                intent.putExtra(view.getContext().getString(R.string.extra_image), message.getPhotoUrl());
                view.getContext().startActivity(intent);
            });
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

    static class MessageHolder extends RecyclerView.ViewHolder {

        ImageView photoImageView;
        TextView messageTextView;
        TextView authorTextView;
        View view;

        MessageHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.photoImageView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            authorTextView = itemView.findViewById(R.id.nameTextView);
            view = itemView.findViewById(R.id.view);
        }
    }

}