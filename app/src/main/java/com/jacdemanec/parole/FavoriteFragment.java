package com.jacdemanec.parole;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.jacdemanec.parole.adapters.HashtagAdapter;
import com.jacdemanec.parole.model.Hashtag;
import com.jacdemanec.parole.viewmodel.HashtagViewModel;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment implements HashtagAdapter.HashtagOnClickListener
        ,AddHashtagDialogFragment.AddHasthagListener{

    private RecyclerView mHashtagRecyclerView;
    private FirebaseRecyclerAdapter mHashtagAdapter;
    private ProgressBar mProgressBar;

    private HashtagViewModel mViewModel;


    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(HashtagViewModel.class);
        // Initialize references to views
        mProgressBar = view.findViewById(R.id.hashtag_progressbar);
        mProgressBar.setVisibility(View.INVISIBLE);
        mHashtagRecyclerView = view.findViewById(R.id.hashtag_recyclerview);

        initializeRecyclerView();
        return view;
    }

    private void initializeRecyclerView() {
        FirebaseRecyclerOptions<Hashtag> options =
                new FirebaseRecyclerOptions.Builder<Hashtag>()
                        .setQuery(mViewModel.getmFavorityQuery(), Hashtag.class)
                        .build();

        mHashtagAdapter = new HashtagAdapter(options, this, mViewModel.getmUsername(), mHashtagRecyclerView);
        mHashtagRecyclerView.setAdapter(mHashtagAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mHashtagRecyclerView.setLayoutManager(linearLayoutManager);
        mHashtagAdapter.startListening();;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHashtagAdapter != null)
            mHashtagAdapter.stopListening();
    }

    @Override
    public void onDialogPositivieClick(String hashtag, String text) {
        HashMap<String, Boolean> emptyLikesMap = new HashMap<>();
        HashMap<String, Boolean> emptyFavoritesMap = new HashMap<>();
        Hashtag hashtagInstance = new Hashtag(hashtag, text, "@" + mViewModel.getmUsername(), emptyLikesMap, 0, emptyFavoritesMap, 0, 0);
        mViewModel.getmHashtagDbReference().child(hashtag).setValue(hashtagInstance);
        mHashtagRecyclerView.smoothScrollToPosition(mHashtagAdapter.getItemCount());
    }

    @Override
    public void onHashtagClicked(String hashtag) {
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("EXTRA_USERNAME", mViewModel.getmUsername());
        intent.putExtra("EXTRA_HASHTAG", hashtag);
        startActivity(intent);
    }

    @Override
    public void onLikeClicked(String hashtag) {
        DatabaseReference likesReference = mViewModel.getmHashtagDbReference().child(hashtag);
        likesReference.child("likes").child(mViewModel.getmUsername()).setValue(true);
    }

    @Override
    public void onFavoriteClicked(String hashtag) {
        DatabaseReference likesReference = mViewModel.getmHashtagDbReference().child(hashtag);
        likesReference.child("favorites").child(mViewModel.getmUsername()).setValue(true);
    }

    @Override
    public void onUnFavoriteClicked(String hashtag) {
        DatabaseReference likesReference = mViewModel.getmHashtagDbReference().child(hashtag);
        likesReference.child("favorites").child(mViewModel.getmUsername()).removeValue();
    }

}
