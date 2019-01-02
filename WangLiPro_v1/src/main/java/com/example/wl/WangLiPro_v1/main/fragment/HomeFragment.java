package com.example.wl.WangLiPro_v1.main.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.adapter.BaseRecylerAdapter;
import com.example.wl.WangLiPro_v1.adapter.MyRecyclerAdapter;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.devices.AddDevicesActivity;
import com.example.wl.WangLiPro_v1.main.MainActivity;
import com.example.wl.WangLiPro_v1.view.PopwindowChoose;
import com.example.wl.WangLiPro_v1.view.loopswitch.AutoSwitchAdapter;
import com.example.wl.WangLiPro_v1.view.loopswitch.AutoSwitchView;
import com.example.wl.WangLiPro_v1.view.loopswitch.LoopModel;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.Device;
import com.jwl.android.jwlandroidlib.bean.DeviceInfo;
import com.jwl.android.jwlandroidlib.bean.DevicesBean;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wl on 2018/5/15.
 */

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener, BaseRecylerAdapter.OnItemClickLitener {

    Context activity;
    private AutoSwitchView mLoopswitch;
    private TextView mTvDeviceTitle;
    private ViewPager mViewpager;
    private LinearLayout mMainLinear;
    private SwipeRefreshLayout mSwipeToLoadLayout;
    private View mContentView;
    private LoopModel model;
    private List<Device> devices;
    private PopwindowChoose popwindow;
    private ArrayList<Fragment> fragmentArrayList;
    private String userid;
    private String token;
    private Device device;
    private DeviceInfo device1;
    private String deviceName;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = getActivity();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_home, container, false);
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
        initData();
    }


    private void initData() {
        devices = new ArrayList();
        fragmentArrayList = new ArrayList<>();
        ArrayList<LoopModel> datas = new ArrayList<LoopModel>();
        mTvDeviceTitle.setOnClickListener(this);
        mSwipeToLoadLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_blue_bright),
                getResources().getColor(android.R.color.holo_green_light),
                getResources().getColor(android.R.color.holo_orange_light),
                getResources().getColor(android.R.color.holo_red_light));

        LoopModel model = null;
        List<String> slides = AppContext.getMapList();
        if (null != slides) {
            for (int i = 0; i < slides.size(); i++) {
                model = new LoopModel(slides.get(i), R.mipmap.loop);
                datas.add(model);
            }
        } else {
            model = new LoopModel(null, R.mipmap.loop);
            datas.add(model);
            model = new LoopModel(null, R.mipmap.loop);
            datas.add(model);
            model = new LoopModel(null, R.mipmap.loop);
            datas.add(model);
        }
        //轮播图
        AutoSwitchAdapter mAdapter = new AutoSwitchAdapter(activity, datas);
        mAdapter.setListener(new AutoSwitchAdapter.OnIitemClickListener() {
            @Override
            public void onIitemClickListener(int positon) {
                //showToast(datas.get(positon).getTitle());
            }
        });
        mLoopswitch.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setEnabled(false);
        userid = AppContext.getUSERID();
        token = AppContext.getTOKEN();
        Log.e("执行", "userid" + userid + "token" + userid);
        HttpManager.getInstance(activity).getDeviceList(userid, token, new HttpDataCallBack<DevicesBean>() {

            @Override
            public void httpDateCallback(DevicesBean devicesBean) {
                devices = devicesBean.getData().getDevices();
                AppContext.setDevices(devices);
                mTvDeviceTitle.setText(devices.get(0).getDevice().getDeviceName());
                initDeviceMenu();
                for (int i = 0; i < devices.size(); i++) {
                    DeviceFragment fragement = new DeviceFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("EQ", devices.get(i));
                    fragement.setArguments(bundle);
                    fragmentArrayList.add(fragement);
                    addindicator();
                }
                //viewpager
                FragmentManager childFragmentManager = getChildFragmentManager();
                MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(childFragmentManager, activity, fragmentArrayList);
                mViewpager.setAdapter(myFragmentPagerAdapter);
            }

            @Override
            public void httpException(HttpException e) {
                Log.e("执行", "失败");
            }

            @Override
            public void complet() {

            }
        });


        //滑动监听
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private View childAt;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            int mNum = 0;

            @Override
            public void onPageSelected(int position) {
                mTvDeviceTitle.setText(devices.get(position).getDevice().getDeviceName());
                mMainLinear.getChildAt(mNum).setEnabled(false);
                childAt = mMainLinear.getChildAt(position);
                childAt.setEnabled(true);
                mNum = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * 指示器
     */
    private View view;

    private void addindicator() {
        //创建底部指示器(小圆点)
        view = new View(activity);
        view.setBackgroundResource(R.drawable.viewpager_background);
        view.setEnabled(false);
        //设置宽高
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
        layoutParams.leftMargin = 10;
        //添加到LinearLayout
        mMainLinear.addView(view, layoutParams);
        mMainLinear.getChildAt(0).setEnabled(true);
    }

    /**
     * 更新设备选择列表
     */
    private void initDeviceMenu() {
        ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();
        for (int i = 0; i < devices.size(); i++) {
            String alias = devices.get(i).getDevice().getDeviceName();
            PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, alias);
            menulist.add(pop);
        }
        popwindow = new PopwindowChoose(activity, menulist, this, true);
        popwindow.setAnimationStyle(R.style.popwin_anim_style);
    }

    private void initView() {
        TextView title = (TextView) mContentView.findViewById(R.id.title);
        ImageView back = (ImageView) mContentView.findViewById(R.id.back);
        back.setVisibility(View.GONE);
        ImageView addDevice = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        addDevice.setImageResource(R.mipmap.ic_add);
        addDevice.setOnClickListener(this);
        title.setText("首页");
        mLoopswitch = (AutoSwitchView) mContentView.findViewById(R.id.loopswitch);
        mTvDeviceTitle = (TextView) mContentView.findViewById(R.id.tv_device_title);
        mViewpager = (ViewPager) mContentView.findViewById(R.id.viewpager);
        mMainLinear = (LinearLayout) mContentView.findViewById(R.id.main_linear);
        mSwipeToLoadLayout = (SwipeRefreshLayout) mContentView.findViewById(R.id.swipeToLoadLayout);
//        initPop();
    }

    @Override
    public void onRefresh() {
        mSwipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSwipeToLoadLayout.isRefreshing()) {
                    mSwipeToLoadLayout.setRefreshing(false);
                }
            }
        }, 3000);
        Log.e("刷新", "++++");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_device_title:
                if (popwindow != null && popwindow.isShowing()) {
                    popwindow.dismiss();
                } else {
                    showPop();
                }
                break;
            case R.id.iv_right_menu:
                startActivity(new Intent(activity, AddDevicesActivity.class));
                break;
        }
    }

    private void initPop() {
//        myRecyclerview = inflate.findViewById(R.id.rv_menu_list);
//        myRecyclerview.setLayoutManager(new LinearLayoutManager(activity));
//        popwindow = new PopwindowChoose(activity, devices, this, this);
//        popwindow.setAnimationStyle(R.style.popwin_anim_style);
//        myRecyclerAdapter = new MyRecyclerAdapter(activity, devices);
//        myRecyclerAdapter.setOnItemClickLitener(this);
//        myRecyclerview.setAdapter(myRecyclerAdapter);
    }

    private void showPop() {
        if (popwindow != null && !popwindow.isShowing()) {
            View view = popwindow.mContentView;
            //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
            int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
            int[] location = new int[2];

            // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
            mTvDeviceTitle.getLocationOnScreen(location);
            //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
            popwindow.showAtLocation(mTvDeviceTitle, Gravity.NO_GRAVITY, (location[0] + mTvDeviceTitle.getWidth() / 2) - popupWidth / 2, location[1] + mTvDeviceTitle.getHeight());

        }

    }

    /***
     * Popupwindow点击事件
     * @param view
     * @param position
     */
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(activity, "点击了第" + position + "条目", Toast.LENGTH_SHORT).show();
        device = devices.get(position);
        device1 = device.getDevice();
        deviceName = device1.getDeviceName();
        if (device != null && device1 != null && deviceName != null) {
            mTvDeviceTitle.setText(deviceName);
        }
        //选中设备
        mViewpager.setCurrentItem(position, false);
        popwindow.dismiss();
    }


    class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private ArrayList<Fragment> myFragmentList;
        private Context mContext;
        private int size;
        private Fragment fragment;

        public MyFragmentPagerAdapter(FragmentManager fm, Context context, ArrayList<Fragment> lists) {
            super(fm);
            this.mContext = context;
            myFragmentList = lists;
        }

        @Override
        public Fragment getItem(int arg0) {
            fragment = myFragmentList.get(arg0);
            return fragment;
        }

        @Override
        public int getCount() {
            size = myFragmentList.size();
            return size;
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }
    }
}