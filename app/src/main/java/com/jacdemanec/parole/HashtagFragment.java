package com.jacdemanec.parole;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class HashtagFragment extends Fragment implements HashtagAdapter.HashtagOnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private String mParam;

    @BindView(R.id.hashtag_recyclerview)
    RecyclerView mHashtagRecyclerView;
    @BindView(R.id.hashtag_progressbar)
    ProgressBar mProgressBar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;


    private FirebaseRecyclerAdapter mHashtagAdapter;
    private HashtagViewModel mViewModel;

    public HashtagFragment() {
        // Required empty public constructor
    }

    public static HashtagFragment newInstance(String param) {
        HashtagFragment hashtagFragment = new HashtagFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param);
        hashtagFragment.setArguments(args);
        return hashtagFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM1);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hashtag, container, false);
        ButterKnife.bind(this, view);
        mViewModel = ViewModelProviders.of(getActivity()).get(HashtagViewModel.class);
        // Initialize references to views
        mProgressBar.setVisibility(View.INVISIBLE);
        mFab = view.findViewById(R.id.fab);
        if(mParam.equals(getString(R.string.args_top_rated))) {
            mFab.setOnClickListener(view1 -> {
                Intent newHashtagIntent = new Intent(getActivity(), NewHashtagActivity.class);
                getActivity().startActivityForResult(newHashtagIntent, MainActivity.RC_NEW_HASHTAG);
            });
        } else {
            mFab.setVisibility(View.INVISIBLE);
        }

        initializeRecyclerView();
        return view;
    }

    private void initializeRecyclerView() {
        Query query = null;
        if (mParam.equals(getString(R.string.args_top_rated))) {
            query = mViewModel.getmTopRatedgQuery();
        } else if (mParam.equals(getString(R.string.args_last_added))) {
            query = mViewModel.getmLastAddedQuery();
        } else if (mParam.equals(getString(R.string.args_favorites))){
            query = mViewModel.getmFavorityQuery();
        }
        FirebaseRecyclerOptions<Hashtag> options =
                new FirebaseRecyclerOptions.Builder<Hashtag>()
                        .setQuery(query, Hashtag.class)
                        .build();

        mHashtagAdapter = new HashtagAdapter(options, this, mViewModel.getmUsername(), mHashtagRecyclerView);
        mHashtagRecyclerView.setAdapter(mHashtagAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mHashtagRecyclerView.setLayoutManager(linearLayoutManager);
        mHashtagAdapter.startListening();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHashtagAdapter != null)
            mHashtagAdapter.stopListening();
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

    @Override
    public void onImageClicked(String imageUrl) {
        Intent imageIntent = new Intent(getActivity(), ImageActvity.class);
        imageIntent.putExtra(getString(R.string.extra_image), imageUrl);
        startActivity(imageIntent);
    }
}







