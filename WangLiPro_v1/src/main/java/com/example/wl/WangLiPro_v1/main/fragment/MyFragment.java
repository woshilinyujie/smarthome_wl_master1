package com.example.wl.WangLiPro_v1.main.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.devices.DevicesManageActivity;

/**
 * Created by wl on 2018/5/15.
 */

public class MyFragment extends Fragment implements View.OnClickListener {

    private View inflate;
    private ImageView mBack;
    private TextView mTitle;
    private ImageView mIvRightMenu;
    private RelativeLayout mHeaderRl;
    private RelativeLayout mDeviceManage;
    private RelativeLayout mPushSetting;
    private RelativeLayout mAboutInfo;
    private ImageView back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_my, container, false);
        initView();
        initData();
        return inflate;
    }

    private void initData() {
        back.setVisibility(View.GONE);
        mTitle.setText("个人中心");
        mDeviceManage.setOnClickListener(this);
        mAboutInfo.setOnClickListener(this);
        mPushSetting.setOnClickListener(this);
    }

    private void initView() {
        back = (ImageView) inflate.findViewById(R.id.back);
        mTitle = (TextView) inflate.findViewById(R.id.title);
        mDeviceManage = (RelativeLayout) inflate.findViewById(R.id.device_manage);
        mPushSetting = (RelativeLayout) inflate.findViewById(R.id.push_setting);
        mAboutInfo = (RelativeLayout) inflate.findViewById(R.id.about_info);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.device_manage:
            startActivity(new Intent(getActivity(),DevicesManageActivity.class));
                break;
            case R.id.push_setting:

                break;
            case R.id.about_info:

                break;
        }

    }
}
