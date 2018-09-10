package com.jacdemanec.parole.adapters;

import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.jacdemanec.parole.model.Hashtag;
import com.jacdemanec.parole.R;

import java.util.Date;
import java.util.HashMap;

public class HashtagAdapter extends FirebaseRecyclerAdapter<Hashtag, HashtagAdapter.HashtagHolder> {

    HashtagOnClickListener hashtagOnClickListener;
    private String mUsername;
    private RecyclerView recyclerView;

    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public HashtagAdapter(@NonNull FirebaseRecyclerOptions<Hashtag> options, HashtagOnClickListener hashtagOnClickListener, String mUsername, RecyclerView recyclerView) {
        super(options);
        this.hashtagOnClickListener = hashtagOnClickListener;
        this.mUsername = mUsername;
        this.recyclerView = recyclerView;
    }

    public void updateUser(String username){
        mUsername = username;
        notifyDataSetChanged();
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        recyclerView.smoothScrollToPosition(getItemCount());
    }

    public interface HashtagOnClickListener {
        public void onHashtagClicked(String hashtag);

        public void onLikeClicked(String hashtag);

        public void onFavoriteClicked(String hashtag);

        public void onUnFavoriteClicked(String hashtag);
    }

    @Override
    protected void onBindViewHolder(@NonNull final HashtagHolder holder, int position, @NonNull final Hashtag model) {
        boolean liked = false;
        holder.hashtagTextView.setText("#" + model.getTitle());
        holder.descriptionTextView.setText(model.getText());
        setHashtagClicked(holder.hashtagTextView, model.getTitle());
        setHashtagClicked(holder.descriptionTextView, model.getTitle());
        if (model.getLikes() != null) {
            holder.likesTextView.setText(String.valueOf(model.getLikes().size()));
        } else {
            holder.likesTextView.setText("0");
        }
        holder.userTextView.setText(model.getOwner());
        holder.thumbUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashtagOnClickListener.onLikeClicked(model.getTitle());
            }
        });
        Date date = new Date(Long.parseLong(model.getTimestamp().toString()));
        Log.d("TIMESTAMP", date.toString());
        if (model.getFavorites() != null && mUsername !=null) {
            if (model.getFavorites().containsKey(mUsername)) {
                holder.favButton.setImageResource(R.drawable.ic_favorite_black_24dp);
                holder.favButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hashtagOnClickListener.onUnFavoriteClicked(model.getTitle());
                        holder.favButton.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                });
            } else {
                setFavoriteClicker(holder.favButton, model.getTitle());
            }
        } else {
            setFavoriteClicker(holder.favButton, model.getTitle());
        }
    }

    private void setFavoriteClicker(ImageButton button, final String hashtag) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashtagOnClickListener.onFavoriteClicked(hashtag);
            }
        });
    }

    private void setHashtagClicked(TextView textView, final String hashtag){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashtagOnClickListener.onHashtagClicked(hashtag);
            }
        });
    }

    @NonNull
    @Override
    public HashtagHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hashtag, parent, false);
        return new HashtagHolder(view);
    }

    class HashtagHolder extends RecyclerView.ViewHolder {

        TextView hashtagTextView;
        TextView descriptionTextView;
        ImageButton thumbUpButton;
        TextView likesTextView;
        TextView userTextView;
        ImageButton favButton;

        public HashtagHolder(View itemView) {
            super(itemView);
            hashtagTextView = itemView.findViewById(R.id.tv_hashtag);
            descriptionTextView = itemView.findViewById(R.id.text);
            thumbUpButton = itemView.findViewById(R.id.thumb_up);
            likesTextView = itemView.findViewById(R.id.likes_count_tv);
            userTextView = itemView.findViewById(R.id.user_tv);
            favButton = itemView.findViewById(R.id.favorite_button);
        }
    }

    public void setUsername(String username){

    }

}