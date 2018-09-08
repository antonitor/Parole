package com.jacdemanec.parole;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

public class MainActivity extends AppCompatActivity implements HashtagAdapter.HashtagOnClickListener
        ,AddHashtagDialogFragment.AddHasthagListener {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 1;

    private RecyclerView mHashtagRecyclerView;
    private FirebaseRecyclerAdapter mHashtagAdapter;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;

    private String mUsername;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHashtagDbReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;
        EmojiManager.install(new GoogleEmojiProvider());
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mHashtagDbReference = mFirebaseDatabase.getReference().child("hashtags");

        // Initialize references to views
        mProgressBar = findViewById(R.id.hashtag_progressbar);
        mHashtagRecyclerView = findViewById(R.id.hashtag_recyclerview);
        mFab = findViewById(R.id.fab);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    onSingedInInitialize(user.getDisplayName());
                } else {
                    onSingedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialogFragment = new AddHashtagDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "AddHashtagDialogFragment");
            }
        });
    }

    // Initialize RecyclerView and its adapter
    private void initializeRecyclerView() {

        Query query = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("timestamp");
        FirebaseRecyclerOptions<Hashtag> options =
                new FirebaseRecyclerOptions.Builder<Hashtag>()
                        .setQuery(query, Hashtag.class)
                        .build();

        /* FAVORITES QUERY!!!!!!!!
        Query query = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("favorites/"+mUsername).equalTo(true);
        FirebaseRecyclerOptions<Hashtag> options =
                new FirebaseRecyclerOptions.Builder<Hashtag>()
                        .setQuery(query, Hashtag.class)
                        .build();
        */
        mHashtagAdapter = new HashtagAdapter(options, this, mUsername);
        mHashtagRecyclerView.setAdapter(mHashtagAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mHashtagRecyclerView.setLayoutManager(linearLayoutManager);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        /*
        //I order to enable this feature, implement OnQueryTextListener and override its methods
        SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView search = (SearchView) menu.findItem(R.id.search).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(this);
        */
        return true;
    }

    private void onSingedInInitialize(String username) {
        mUsername = username;
        initializeRecyclerView();
        onStart();
    }

    private void onSingedOutCleanup() {
        mUsername = ANONYMOUS;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
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

    @Override
    public void onDialogPositivieClick(String hashtag, String text) {
        HashMap<String, Boolean> emptyLikesMap = new HashMap<>();
        HashMap<String, Boolean> emptyFavoritesMap = new HashMap<>();
        Hashtag testHashtag = new Hashtag(hashtag, text, "@" + mUsername, emptyLikesMap, 0, emptyFavoritesMap, 0, 0);
        mHashtagDbReference.child(hashtag).setValue(testHashtag);
        mHashtagRecyclerView.smoothScrollToPosition(mHashtagAdapter.getItemCount());
    }

}
