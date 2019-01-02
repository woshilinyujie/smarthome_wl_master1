package com.fbee.smarthome_wl.ui.mainsimplify.home.eques;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.bean.EquesDeviceInfo;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.ui.equesdevice.EquesDeviceInfoActivity;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.EquesAlarmActivity;
import com.fbee.smarthome_wl.ui.equesdevice.equesaddlock.EquesAddlockActivity;
import com.fbee.smarthome_wl.ui.equesdevice.videocall.EquesCallActivity;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;

import rx.functions.Action1;

/**
 * A simple {@link Fragment} subclass.
 */
public class EQfragment extends BaseFragment implements View.OnClickListener {

    private ImageView imageBindLock;
    private TextView tvEqOnlineState;
    private ImageView imageVidoeTalk;
    private TextView tvEqPowerState;
    private ImageView imageArlmRecord;
    private TextView tvEqDeviceInfo;
    private EquesListInfo.bdylistEntity bdylistEntity;
    private String bid;
    private String uid;
    private int status;
    private String  nick;
    private String name;

    public EQfragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return  super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public int onSetLayoutId() {
        return R.layout.fragment_eqfragment;
    }

    @Override
    public void initView() {
        imageBindLock = (ImageView) mContentView.findViewById(R.id.image_bind_lock);
        tvEqOnlineState = (TextView) mContentView.findViewById(R.id.tv_eq_online_state);
        imageVidoeTalk = (ImageView) mContentView.findViewById(R.id.image_vidoe_talk);
        tvEqPowerState = (TextView) mContentView.findViewById(R.id.tv_eq_power_state);
        imageArlmRecord = (ImageView) mContentView.findViewById(R.id.image_arlm_record);
        tvEqDeviceInfo = (TextView) mContentView.findViewById(R.id.tv_eq_device_info);
        imageBindLock.setOnClickListener(this);
        imageVidoeTalk.setOnClickListener(this);
        imageArlmRecord.setOnClickListener(this);
        tvEqDeviceInfo.setOnClickListener(this);
        tvEqOnlineState.setOnClickListener(this);
        tvEqPowerState.setOnClickListener(this);
    }

    @Override
    public void bindEvent() {
        initApi();
        bdylistEntity= (EquesListInfo.bdylistEntity) getArguments().getSerializable("EQ");
        if(bdylistEntity!=null){
            bid = bdylistEntity.getBid();
            name=bdylistEntity.getName();
            nick=bdylistEntity.getNick();
            if (AppContext.getOnlines().size() > 0) {
                for (int i = 0; i < AppContext.getOnlines().size(); i++) {
                    String bid = AppContext.getOnlines().get(i).getBid();
                    if (bdylistEntity.getBid().equals(bid)) {
                        uid=AppContext.getOnlines().get(i).getUid();
                        status=AppContext.getOnlines().get(i).getStatus();
                    }
                }
            }

            receiveDeviceInfo();
            if(uid!=null){
                //获取设备详情
                getBaseActivity().icvss.equesGetDeviceInfo(uid);
            }

            if (status == 0) {
                tvEqOnlineState.setText("离线");
                tvEqOnlineState.setTextColor(getResources().getColor(R.color.red));
            } else if (status == 1) {
                tvEqOnlineState.setText("在线");
                tvEqOnlineState.setTextColor(getResources().getColor(R.color.colorAccent));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //绑定门锁
            case R.id.image_bind_lock:
                Bundle bundle5 = new Bundle();
                bundle5.putString(Method.ATTR_BUDDY_BID, bid);
                skipAct(EquesAddlockActivity.class, bundle5);
                break;

            //视频监控
            case R.id.image_vidoe_talk:
                if (uid != null) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(Method.ATTR_BUDDY_UID, uid);
                    skipAct(EquesCallActivity.class, bundle1);
                }
                break;

            //报警消息
            case R.id.image_arlm_record:
                Bundle bundle2 = new Bundle();
                if (nick == null) {
                    bundle2.putString(Method.ATTR_BUDDY_NICK, name);
                }else{
                    bundle2.putString(Method.ATTR_BUDDY_NICK, nick);
                }
                bundle2.putString(Method.ATTR_BUDDY_BID, bid);
                skipAct(EquesAlarmActivity.class, bundle2);
                break;

            //门锁详情
            case R.id.tv_eq_device_info:
                Bundle bundle = new Bundle();
                bundle.putString(Method.ATTR_BUDDY_NICK, nick);
                bundle.putString(Method.ATTR_BUDDY_BID, bid);
                bundle.putString(Method.ATTR_BUDDY_UID, uid);
                bundle.putInt(Method.ATTR_BUDDY_STATUS, status);
                bundle.putString(Method.ATTR_BUDDY_NAME, name);

                skipAct(EquesDeviceInfoActivity.class, bundle);
                break;
            case R.id.tv_eq_power_state:
                Bundle bundle1 = new Bundle();
                bundle1.putString(Method.ATTR_BUDDY_NICK, nick);
                bundle1.putString(Method.ATTR_BUDDY_BID, bid);
                bundle1.putString(Method.ATTR_BUDDY_UID, uid);
                bundle1.putInt(Method.ATTR_BUDDY_STATUS, status);
                bundle1.putString(Method.ATTR_BUDDY_NAME, name);

                skipAct(EquesDeviceInfoActivity.class, bundle1);
                break;
            case R.id.tv_eq_online_state:
                Bundle bundle3 = new Bundle();
                bundle3.putString(Method.ATTR_BUDDY_NICK, nick);
                bundle3.putString(Method.ATTR_BUDDY_BID, bid);
                bundle3.putString(Method.ATTR_BUDDY_UID, uid);
                bundle3.putInt(Method.ATTR_BUDDY_STATUS, status);
                bundle3.putString(Method.ATTR_BUDDY_NAME, name);

                skipAct(EquesDeviceInfoActivity.class, bundle3);
                break;
        }
    }

    private void receiveDeviceInfo(){
        mSubscription = RxBus.getInstance().toObservable(EquesDeviceInfo.class)
                .compose(TransformUtils.<EquesDeviceInfo>defaultSchedulers())
                .subscribe(new Action1<EquesDeviceInfo>() {

                    @Override
                    public void call(EquesDeviceInfo equesDeviceInfo) {

                       int battery_level = equesDeviceInfo.getBattery_level();
                        if (battery_level <= 10) {
                            tvEqPowerState.setText("低电量");
                        } else if (battery_level > 10 && battery_level <= 25) {
                            tvEqPowerState.setText("25%");
                        } else if (battery_level > 25 && battery_level <= 50) {
                            tvEqPowerState.setText("50%");
                        } else if (battery_level > 50 && battery_level <= 75) {
                            tvEqPowerState.setText("75%");
                        } else if (battery_level > 75) {
                            tvEqPowerState.setText("100%");
                        }
                    }
                });
        mCompositeSubscription.add(mSubscription);
    }

}
