package com.fbee.smarthome_wl.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.fbee.smarthome_wl.base.BaseFragment;

import java.util.List;

/**
 *
 * Created by ZhaoLi.Wang on 2017/3/28 16:10
 */
public class MyFragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private String[] mtabs;
    private List<Fragment> mllist;

    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> mllist, String[] tabs) {
        super(fm);
        this.mllist = mllist;
        this.mtabs = tabs;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mtabs[position];
    }

    @Override
    public int getCount() {
        return mtabs.length;
    }

    @Override
    public BaseFragment getItem(int position) {
        return (BaseFragment) mllist.get(position);
    }
}
