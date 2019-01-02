package com.example.wl.WangLiPro_v1.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.example.wl.WangLiPro_v1.main.fragment.FindFragment;
import com.example.wl.WangLiPro_v1.main.fragment.HomeFragment;
import com.example.wl.WangLiPro_v1.main.fragment.MyFragment;
import com.example.wl.WangLiPro_v1.service.BgService;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private RadioButton mMainFindBtn;
    private RadioButton mMainMeBtn;
    private FrameLayout mFramelayout;
    private RadioButton mMainHomeBtn;
    private RadioGroup mMainTab;
    private FragmentTransaction fragmentTransaction;
    private HomeFragment homeFragment;
    private FindFragment findFragment;
    private MyFragment myFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        homeFragment = new HomeFragment();
        findFragment = new FindFragment();
        myFragment = new MyFragment();
        fragmentTransaction.add(R.id.framelayout, homeFragment);
        fragmentTransaction.commit();
        mMainHomeBtn.setChecked(true);

        startService(new Intent(this, BgService.class));
    }

    @Override
    protected void initView() {
        mMainFindBtn = (RadioButton) findViewById(R.id.main_find_btn);
        mMainFindBtn.setOnClickListener(this);
        mMainMeBtn = (RadioButton) findViewById(R.id.main_me_btn);
        mMainMeBtn.setOnClickListener(this);
        mFramelayout = (FrameLayout) findViewById(R.id.framelayout);
        mMainHomeBtn = (RadioButton) findViewById(R.id.main_home_btn);
        mMainHomeBtn.setOnClickListener(this);
        mMainTab = (RadioGroup) findViewById(R.id.main_tab);
        mMainTab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_home_btn:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, homeFragment);
                fragmentTransaction.commit();
                break;
            case R.id.main_find_btn:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, findFragment);
                fragmentTransaction.commit();
                break;
            case R.id.main_me_btn:
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.framelayout, myFragment);
                fragmentTransaction.commit();
                break;
        }

    }
}
