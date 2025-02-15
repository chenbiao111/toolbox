package com.shixin.app;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.gyf.immersionbar.ImmersionBar;
import com.shixin.app.adapter.TxdqPagerAdapter;
import com.shixin.app.fragment.Txdq1Fragment;
import com.shixin.app.fragment.Txdq2Fragment;
import com.shixin.app.fragment.Txdq3Fragment;
import com.shixin.app.fragment.Txdq4Fragment;
import com.shixin.app.fragment.Txdq5Fragment;
import com.shixin.app.fragment.Txdq6Fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvatarActivity extends AppCompatActivity {

    @BindView(R.id.root)
    ViewGroup root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.view_pager)
    ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        ButterKnife.bind(this);

        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.appbarColor)
                .navigationBarColor(R.color.backgroundColor)
                .autoDarkModeEnable(true)
                .init();

        toolbar.setTitle(getString(R.string.头像大全));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        viewPager.setOffscreenPageLimit(6);

        List<Fragment> mFragments = new ArrayList<>();
        mFragments.add(new Txdq1Fragment());
        mFragments.add(new Txdq2Fragment());
        mFragments.add(new Txdq3Fragment());
        mFragments.add(new Txdq4Fragment());
        mFragments.add(new Txdq5Fragment());
        mFragments.add(new Txdq6Fragment());
        TxdqPagerAdapter mAdapter = new TxdqPagerAdapter(this, mFragments);
        viewPager.setAdapter(mAdapter);

        List<String> titles = new ArrayList<>();
        titles.add("情侣");
        titles.add("男生");
        titles.add("女生");
        titles.add("卡通动漫");
        titles.add("风景静物");
        titles.add("微信");

        new TabLayoutMediator(tabs, viewPager, (tab, position) -> {
            tab.setText(titles.get(position));
        }).attach();

    }
}