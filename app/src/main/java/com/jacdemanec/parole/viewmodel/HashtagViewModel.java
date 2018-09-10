package com.jacdemanec.parole.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.jacdemanec.parole.model.Hashtag;

public class HashtagViewModel extends ViewModel {

    private String mUsername;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHashtagDbReference;
    private Query mHashtagQuery;
    private Query mFavorityQuery;

    public HashtagViewModel(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHashtagDbReference = mFirebaseDatabase.getReference().child("hashtags");
        mHashtagQuery = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("timestamp");
        mFavorityQuery = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("favorites/"+mUsername).equalTo(true);
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public FirebaseDatabase getmFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    public DatabaseReference getmHashtagDbReference() {
        return mHashtagDbReference;
    }

    public Query getmHashtagQuery() {
        return mHashtagQuery;
    }

    public Query getmFavorityQuery() {
        return mFavorityQuery;
    }
}
