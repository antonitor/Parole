package com.jacdemanec.parole.viewmodel;

import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class HashtagViewModel extends ViewModel {

    private final static String ANONIMUS = "anonimus";
    private FirebaseUser firebaseUser;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mHashtagDbReference;
    private Query mTopRatedgQuery;
    private Query mLastAddedQuery;


    public HashtagViewModel(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mHashtagDbReference = mFirebaseDatabase.getReference().child("hashtags");
        mLastAddedQuery = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("timestamp");
        mTopRatedgQuery = mFirebaseDatabase.getReference().child("/hashtags").orderByChild("likes_count");
    }

    public String getmUsername() {
        if (firebaseUser!=null) {
            return firebaseUser.getDisplayName();
        } else {
            Log.d("VIEW MODEL", ANONIMUS);
            return ANONIMUS;
        }
    }

    public FirebaseDatabase getmFirebaseDatabase() {
        return mFirebaseDatabase;
    }

    public DatabaseReference getmHashtagDbReference() {
        return mHashtagDbReference;
    }

    public Query getmFavorityQuery() {
        return mFirebaseDatabase.getReference().child("/hashtags").orderByChild("favorites/"+getmUsername()).equalTo(true);
    }

    public Query getmTopRatedgQuery() {
        return mTopRatedgQuery;
    }

    public Query getmLastAddedQuery() {
        return mLastAddedQuery;
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public void setFirebaseUser(FirebaseUser firebaseUser) {
        this.firebaseUser = firebaseUser;
    }
}
