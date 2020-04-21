package com.jacdemanec.parole.adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jacdemanec.parole.HashtagFragment;
import com.jacdemanec.parole.R;

public class MainPageAdapter extends FragmentPagerAdapter {

    private static final int NUM_ITEMS = 3;
    private String[] tabTitles = new String[]{"Top Rated", "Last Added", "Favorites"};
    private Context mContext;

    public MainPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return HashtagFragment.newInstance(mContext.getString(R.string.args_top_rated));
            case 1:
                return HashtagFragment.newInstance(mContext.getString(R.string.args_last_added));
            case 2:
                return HashtagFragment.newInstance(mContext.getString(R.string.args_favorites));
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
