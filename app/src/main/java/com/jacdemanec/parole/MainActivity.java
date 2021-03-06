package com.jacdemanec.parole;


import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jacdemanec.parole.adapters.MainPageAdapter;
import com.jacdemanec.parole.model.Hashtag;
import com.jacdemanec.parole.viewmodel.HashtagViewModel;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.google.GoogleEmojiProvider;

import java.util.Arrays;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    public static final int RC_NEW_HASHTAG = 22;

    private HashtagViewModel mViewModel;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EmojiManager.install(new GoogleEmojiProvider());

        mViewModel = ViewModelProviders.of(this).get(HashtagViewModel.class);


        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                onSingedInInitialize(user);
            } else {
                onSingedOutCleanup();
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setLogo(R.mipmap.ic_launcher)
                                .setTheme(R.style.LoginTheme)
                                .setAvailableProviders(Arrays.asList(
                                        new AuthUI.IdpConfig.EmailBuilder().build()))
                                .build(),
                        RC_SIGN_IN);
            }
        };

    }

    private void startFragmentPageAdapter() {
        ViewPager viewPager = findViewById(R.id.pager);
        FragmentPagerAdapter fragmentPagerAdapter = new MainPageAdapter(getSupportFragmentManager(), this);
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setAdapter(fragmentPagerAdapter);
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
                break;
            case RC_NEW_HASHTAG:
                if (resultCode == RESULT_OK) {
                    String hashtag = data.getStringExtra(getString(R.string.extra_new_hashtag));
                    String text = data.getStringExtra(getString(R.string.extra_new_hashtag_text));
                    String imageUrl = null;
                    if (data.hasExtra(getString(R.string.extra_new_image))) {
                        imageUrl = data.getStringExtra(getString(R.string.extra_new_image));
                    }
                    HashMap<String, Boolean> emptyLikesMap = new HashMap<>();
                    HashMap<String, Boolean> emptyFavoritesMap = new HashMap<>();

                    Hashtag hashtagInstance = new Hashtag(hashtag, text, "@" + mViewModel.getmUsername(),
                            imageUrl, emptyLikesMap, 0, emptyFavoritesMap, 0, 0);

                    mViewModel.getmHashtagDbReference().child(hashtag).setValue(hashtagInstance);
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

    private void onSingedInInitialize(FirebaseUser user) {
        mViewModel.setFirebaseUser(user);
        startFragmentPageAdapter();
    }

    private void onSingedOutCleanup() {
        mViewModel.setFirebaseUser(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sign_out_menu) {
            AuthUI.getInstance().signOut(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
