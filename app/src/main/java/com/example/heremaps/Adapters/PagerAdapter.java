package com.example.heremaps.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.heremaps.Fragments.CategoryFragment;
import com.example.heremaps.Fragments.HistoryFragment;


public class PagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Tab1", "Tab2"};

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = new HistoryFragment();
                break;
            case 1:
                fragment = new CategoryFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
    @Override public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "ИСТОРИЯ";
            case 1:
                return "КАТЕГОРИИ";
        }
        return null;
    }
}
