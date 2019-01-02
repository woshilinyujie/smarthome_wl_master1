package com.example.wl.WangLiPro_v1.devices;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.adapter.DeviceManageAdapter;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.BaseBean;
import com.jwl.android.jwlandroidlib.bean.Device;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;

import java.util.List;

public class DevicesManageActivity extends BaseActivity {

    private ImageView mBack;
    private TextView mTitle;
    private ImageView mIvRightMenu;
    private SwipeMenuListView mLsGateway;
    private List<Device> deviceS;
    private DeviceManageAdapter deviceManageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_manage);
    }

    @Override
    protected void initData() {
        deviceS = AppContext.getDeviceS();
        mTitle.setText("设备管理");
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        deviceManageAdapter = new DeviceManageAdapter(this, deviceS);
        mLsGateway.setAdapter(deviceManageAdapter);

        mLsGateway.setMenuCreator(new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 75,
                        DevicesManageActivity.this.getResources().getDisplayMetrics()));
                // set a icon
                deleteItem.setIcon(R.mipmap.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        });
        mLsGateway.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        HttpManager.getInstance(DevicesManageActivity.this).deleteDevice(AppContext.getUSERID(), AppContext.getTOKEN(), deviceS.get(position).getDevice().getDeviceId(), new HttpDataCallBack<BaseBean>() {
                            @Override
                            public void httpDateCallback(BaseBean baseBean) {
                                if (baseBean.getResponse().getCode() == 200) {
                                    deviceS.remove(position);
                                    deviceManageAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void httpException(HttpException e) {

                            }

                            @Override
                            public void complet() {

                            }
                        });
                        break;


                }
                return false;
            }
        });
    }

    @Override
    protected void initView() {
        mBack = (ImageView) findViewById(R.id.back);
        mTitle = (TextView) findViewById(R.id.title);
        mIvRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        mLsGateway = (SwipeMenuListView) findViewById(R.id.ls_gateway);
    }
}
