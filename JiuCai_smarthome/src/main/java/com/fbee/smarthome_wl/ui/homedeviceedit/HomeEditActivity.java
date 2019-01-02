package com.fbee.smarthome_wl.ui.homedeviceedit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.MyFragmentPagerAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.event.UpdateHomeSetEvent;
import com.fbee.smarthome_wl.request.UpdateUserConfigurationReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.TopTabsView;

import java.util.ArrayList;
import java.util.List;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * 首页设备场景编辑
 * Created by ZhaoLi.Wang on 2017/3/28 17:54
 */
public class HomeEditActivity extends BaseActivity<HomeEditContract.Presenter> implements HomeEditContract.View{
    private final String[] tabs = {"设备", "场景"};
    private List<Fragment> mList = null;
    private EquipEditFragment equipEditFragment;
    private ScenarioEditFragment scenarioEditFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_edit);

    }

    @Override
    protected void initView() {
        createPresenter(new HomeEditPresenter(this));
        mList = new ArrayList<>();
        equipEditFragment=EquipEditFragment.newInstance(1);
        scenarioEditFragment=ScenarioEditFragment.newInstance(1);
        mList.add(equipEditFragment);
        mList.add(scenarioEditFragment);
        ImageView back= (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("首页展示编辑");
        TextView saveText = (TextView) findViewById(R.id.tv_right_menu);
        saveText.setVisibility(View.VISIBLE);
        saveText.setText("保存");
        saveText.setOnClickListener(this);

        TopTabsView topTabsView = (TopTabsView) findViewById(R.id.topTabsView);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);

        topTabsView.setShouldExpand(true);
        topTabsView.setTextColor(Color.parseColor("#FF333333"));
        topTabsView.setTextCheckColor(Color.parseColor("#2BD591"));
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mList, tabs);
        viewPager.setAdapter(myFragmentPagerAdapter);
        topTabsView.setViewPager(viewPager);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right_menu:
                saveUserSetting();
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    /**
     * 保存用户配置
     */
    List<HomePageResponse.BodyBean.DeviceListBean> devices;
    List<HomePageResponse.BodyBean.SceneListBean> scenas;
    private void saveUserSetting(){
        showLoadingDialog("保存用户配置");
        //设备
        devices= equipEditFragment.getmSelectDatas();
        //场景
        scenas= scenarioEditFragment.getmSelectDatas();

        UpdateUserConfigurationReq body=new UpdateUserConfigurationReq();
        LoginResult.BodyBean.GatewayListBean gw= (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY+PreferencesUtils.getString(LOCAL_USERNAME));

//        HomePageResponse.BodyBean homebody = AppContext.getInstance().getmHomebody();
//        if(null == homebody){
//            HomePageResponse.BodyBean.HomepageCameraBean cameraBean= new HomePageResponse.BodyBean.HomepageCameraBean();
//            body.setHomepage_camera(cameraBean);
//        }else{
//            body.setHomepage_camera(homebody.getHomepage_camera());
//        }
        if(gw  !=null){
            body.setGateway_uuid(gw.getUuid());
            body.setGateway_vendor_name(gw.getVendor_name());
            body.setIsfull("true");
            body.setDevice_list(devices);
            body.setScene_list(scenas);
            presenter.setUserConfig(body);
        }

    }


    /**
     * 设置配置返回
     */
    @Override
    public void setCallBack(BaseResponse info) {
        if("200".equals(info.getHeader().getHttp_code())){
            if(AppContext.getInstance().getmHomebody() == null){
                AppContext.getInstance().setmHomebody( new  HomePageResponse.BodyBean());
            }
            if(null != devices)
            AppContext.getInstance().getmHomebody().setDevice_list(devices);
            if(null != scenas)
            AppContext.getInstance().getmHomebody().setScene_list(scenas);
            RxBus.getInstance().post(new UpdateHomeSetEvent());
            finish();
        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(info.getHeader().getReturn_string()));

        }
    }

    @Override
    public void hideLoading() {
        hideLoadingDialog();
    }

    @Override
    public void showLoadingDialog() {

    }
}
