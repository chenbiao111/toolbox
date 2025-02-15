package com.shixin.app.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.shixin.app.fragment.Home1Fragment;
import com.shixin.app.fragment.Home2Fragment;
import com.shixin.app.fragment.Home3Fragment;

public class MainPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    int tabCount;

    public MainPagerAdapter(Context context, FragmentManager fm, int tabCount) {
        super(fm);
        this.context = context;
        this.tabCount = tabCount;
    }

    @Override
    public int getCount(){
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int _position) {
        if (_position == 0) {
            return "首页";
        }
        if (_position == 1) {
            return "功能";
        }
        if (_position == 2) {
            return "发现";
        }
        return null;
    }

    @Override
    public Fragment getItem(int _position) {
        if (_position == 0) {
            return new Home1Fragment();
        }
        if (_position == 1) {
            return new Home2Fragment();
        }
        if (_position == 2) {
            return new Home3Fragment();
        }
        return null;
    }


}
