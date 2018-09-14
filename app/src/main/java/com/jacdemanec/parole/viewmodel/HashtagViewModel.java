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
    private Query mTopRatedgQuery;
    private Query mLastAddedQuery;
    private Query mFavorityQuery;

    public HashtagViewModel(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHashtagDbReference = mFirebaseDatabase.getReference().child("hashtags");
        mLastAddedQuery = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("timestamp");
        mFavorityQuery = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("favorites/"+mUsername).equalTo(true);
        mTopRatedgQuery = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("likes_count");
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

    public Query getmFavorityQuery() {
        return mFavorityQuery;
    }

    public Query getmTopRatedgQuery() {
        return mTopRatedgQuery;
    }

    public Query getmLastAddedQuery() {
        return mLastAddedQuery;
    }
}
