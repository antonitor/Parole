package com.jacdemanec.parole;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jacdemanec.parole.adapters.HashtagAdapter;
import com.jacdemanec.parole.model.Hashtag;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.Arrays;
import java.util.HashMap;

public class FavoriteActivity extends AppCompatActivity implements HashtagAdapter.HashtagOnClickListener {

    private static final String TAG = "FavoriteActivity";

    public static final String ANONYMOUS = "anonymous";

    private RecyclerView mHashtagRecyclerView;
    private FirebaseRecyclerAdapter mHashtagAdapter;
    private BottomNavigationView mBottomNavigationView;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHashtagDbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        mUsername = getIntent().getStringExtra("extra_username");
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHashtagDbReference = mFirebaseDatabase.getReference().child("hashtags");

        // Initialize references to views
        mHashtagRecyclerView = findViewById(R.id.hashtag_recyclerview);
        mBottomNavigationView = findViewById(R.id.bottom_navigation);

        // Initialize progress bar

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_hashtags:
                        Intent intent = new Intent(FavoriteActivity.this, MainActivity.class);
                        intent.putExtra("extra_username", mUsername);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.action_favorites:
                        break;
                    case R.id.action_profile:
                        return false;
                }
                return true;
            }
        });

        initializeRecyclerView();

    }

    // Initialize RecyclerView and its adapter
    private void initializeRecyclerView() {

        Query query = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("favorites/"+mUsername).equalTo(true);
        FirebaseRecyclerOptions<Hashtag> options =
                new FirebaseRecyclerOptions.Builder<Hashtag>()
                        .setQuery(query, Hashtag.class)
                        .build();

        mHashtagAdapter = new HashtagAdapter(options, this, mUsername);
        mHashtagRecyclerView.setAdapter(mHashtagAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mHashtagRecyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mHashtagAdapter != null)
            mHashtagAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mHashtagAdapter != null)
            mHashtagAdapter.stopListening();
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("EXTRA_USERNAME", mUsername);
        intent.putExtra("EXTRA_HASHTAG", hashtag);
        startActivity(intent);
    }

    @Override
    public void onLikeClicked(String hashtag) {
        DatabaseReference likesReference = mHashtagDbReference.child(hashtag);
        likesReference.child("likes").child(mUsername).setValue(true);
    }

    @Override
    public void onFavoriteClicked(String hashtag) {
        DatabaseReference likesReference = mHashtagDbReference.child(hashtag);
        likesReference.child("favorites").child(mUsername).setValue(true);
    }

    @Override
    public void onUnFavoriteClicked(String hashtag) {
        DatabaseReference likesReference = mHashtagDbReference.child(hashtag);
        likesReference.child("favorites").child(mUsername).removeValue();
    }
}
