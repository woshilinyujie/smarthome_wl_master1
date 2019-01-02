package com.fbee.smarthome_wl.ui.mainsimplify.my;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eques.icvss.api.ICVSSUserInstance;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.bean.ModifyAccountInfo;
import com.fbee.smarthome_wl.bean.ModifyAliars;
import com.fbee.smarthome_wl.eques.ICVSSUserModule;
import com.fbee.smarthome_wl.event.HomePagerUpMain;
import com.fbee.smarthome_wl.event.SwitchFragmentEvent;
import com.fbee.smarthome_wl.event.UserHaderIconChange;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.ui.aboutinfo.AboutInfoActivity;
import com.fbee.smarthome_wl.ui.accountinformation.PersonAccountInfoActivity;
import com.fbee.smarthome_wl.ui.devicemanager.DeviceManagerActivity;
import com.fbee.smarthome_wl.ui.gateway.GatewayListActicity;
import com.fbee.smarthome_wl.ui.jpush.MenuJpushActivity;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;

import java.util.HashMap;

import rx.Subscription;
import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends BaseFragment implements View.OnClickListener{
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private RelativeLayout rlUserPictureSimplify;
    private ImageView userPictureSimplify;
    private TextView userNickSimplify;
    private TextView userPhoneSimplify;
    private RelativeLayout gatewaylistSimplify;
    private RelativeLayout mainEquipmentManagementSimplify;
    private RelativeLayout mainPushSimplify;
    private RelativeLayout mainAboutInfoSimplify;
    private RelativeLayout mainChangeSimplify;
    private TextView textView7Simplify;


    private ICVSSUserInstance icvss;


    public MyFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_my;
    }

    @Override
    public void initView() {
        headerRl = (RelativeLayout)mContentView.findViewById(R.id.header_rl);
        back = (ImageView) mContentView.findViewById(R.id.back);
        title = (TextView) mContentView.findViewById(R.id.title);
        title.setText("我的");
        ivRightMenu = (ImageView) mContentView.findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) mContentView.findViewById(R.id.tv_right_menu);
        rlUserPictureSimplify = (RelativeLayout) mContentView.findViewById(R.id.rl_user_picture_simplify);
        rlUserPictureSimplify.setOnClickListener(this);
        userPictureSimplify = (ImageView) mContentView.findViewById(R.id.user_picture_simplify);
        userNickSimplify = (TextView) mContentView.findViewById(R.id.user_nick_simplify);
        userPhoneSimplify = (TextView) mContentView.findViewById(R.id.user_phone_simplify);
        gatewaylistSimplify = (RelativeLayout) mContentView.findViewById(R.id.gatewaylist_simplify);
        gatewaylistSimplify.setOnClickListener(this);
        mainEquipmentManagementSimplify = (RelativeLayout) mContentView.findViewById(R.id.main_equipment_management_simplify);
        mainEquipmentManagementSimplify.setOnClickListener(this);
        mainPushSimplify = (RelativeLayout) mContentView.findViewById(R.id.main_push_simplify);
        mainPushSimplify.setOnClickListener(this);
        textView7Simplify = (TextView) mContentView.findViewById(R.id.textView7_simplify);
        mainAboutInfoSimplify = (RelativeLayout) mContentView.findViewById(R.id.main_about_info_simplify);
        mainAboutInfoSimplify.setOnClickListener(this);
        mainChangeSimplify = (RelativeLayout) mContentView.findViewById(R.id.main_change_simplify);
        mainChangeSimplify.setOnClickListener(this);
    }

    @Override
    public void bindEvent() {
        initApi();
        icvss = ICVSSUserModule.getInstance(getBaseActivity()).getIcvss();
        if (PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS) != null) {
            userNickSimplify.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS));
        }
        userPhoneSimplify.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
        //初始化头像
        initHeaderIcon();
        //接收昵称改变
        receiveAliarsChange();
        //接收用户名改变
        receiveUserNameChange();
        //接收头像改变
        receiveHeaderIconChange();
        //获取网关
        receiveGateWays();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //用户
            case R.id.rl_user_picture_simplify:
                skipAct(PersonAccountInfoActivity.class);
                break;
            //当前网关
            case R.id.gatewaylist_simplify:
                skipAct(GatewayListActicity.class);
                break;
            //设备管理
            case R.id.main_equipment_management_simplify:
                icvss.equesGetDeviceList();
                skipAct(DeviceManagerActivity.class);
                break;
            //推送设置
            case R.id.main_push_simplify:
                skipAct(MenuJpushActivity.class);
                break;
            //关于帮助
            case R.id.main_about_info_simplify:
                skipAct(AboutInfoActivity.class);
                break;
            //切换
            case R.id.main_change_simplify:
                RxBus.getInstance().post(new SwitchFragmentEvent());
                break;
        }
    }
    /**
     * 初始化头像
     */
    private void initHeaderIcon() {
        HashMap<String, String> map = (HashMap<String, String>) PreferencesUtils.getObject(PreferencesUtils.HEAD_ICON);
        if (map != null) {
            String uri = map.get(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
            if (uri != null) {
                ImageLoader.loadCropCircle(getActivity(), uri, userPictureSimplify, R.mipmap.default_user_picture);
            }
        }
    }
    /**
     * 接收昵称改变
     */
    private void receiveAliarsChange() {
        Subscription aliarSucription = RxBus.getInstance().toObservable(ModifyAliars.class)
                .compose(TransformUtils.<ModifyAliars>defaultSchedulers())
                .subscribe(new Action1<ModifyAliars>() {
                    @Override
                    public void call(ModifyAliars event) {
                        userNickSimplify.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_ALAIRS));
                    }
                });
        mCompositeSubscription.add(aliarSucription);
    }
    /**
     * 接用户名称改变
     */
    private void receiveUserNameChange() {
        Subscription accountSucription = RxBus.getInstance().toObservable(ModifyAccountInfo.class)
                .compose(TransformUtils.<ModifyAccountInfo>defaultSchedulers())
                .subscribe(new Action1<ModifyAccountInfo>() {
                    @Override
                    public void call(ModifyAccountInfo event) {
                        userPhoneSimplify.setText(PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME));
                    }
                });
        mCompositeSubscription.add(accountSucription);
    }
    /**
     * 接收头像改变
     */
    private void receiveHeaderIconChange() {
        //获取区域列表
        Subscription subscription = RxBus.getInstance().toObservable(UserHaderIconChange.class).compose(TransformUtils.<UserHaderIconChange>defaultSchedulers()).subscribe(new Action1<UserHaderIconChange>() {
            @Override
            public void call(UserHaderIconChange userHaderIconChange) {
                // userPicture
                if (userHaderIconChange.getUri() != null) {
                    ImageLoader.loadCropCircle(mContext, userHaderIconChange.getUri(), userPictureSimplify, R.mipmap.default_user_picture);
                }
            }
        });
        mCompositeSubscription.add(subscription);
    }
    /**
     * 获取网关
     */
    private void receiveGateWays() {
        mSubscription = RxBus.getInstance().toObservable(LoginResult.BodyBean.GatewayListBean.class)
                .compose(TransformUtils.<LoginResult.BodyBean.GatewayListBean>defaultSchedulers())
                .subscribe(new Action1<LoginResult.BodyBean.GatewayListBean>() {
                    @Override
                    public void call(LoginResult.BodyBean.GatewayListBean event) {
                        if (event.getNote() != null && event.getNote().length() > 0) {
                            textView7Simplify.setText(event.getNote());
                        } else {
                            textView7Simplify.setText(event.getUsername());
                        }
                    }
                });

      Subscription  mSubscription01 = RxBus.getInstance().toObservable(HomePagerUpMain.class)
                .compose(TransformUtils.<HomePagerUpMain>defaultSchedulers())
                .subscribe(new Action1<HomePagerUpMain>() {
                               private LoginResult.BodyBean.GatewayListBean gatewayListBean;

                               @Override
                               public void call(HomePagerUpMain updataName) {
                                   textView7Simplify.setText(updataName.getName());
                               }
                           }
                );
        mCompositeSubscription.add(mSubscription);
        mCompositeSubscription.add(mSubscription01);
    }
}
