package com.fbee.smarthome_wl.ui.devicemanager;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.BindScenceInfoAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.BindDevSenceEvent;
import com.fbee.smarthome_wl.event.BindSenceInfoEvent;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class BindSenceActivity extends BaseActivity {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ListView lvBindDeciceSence;
    private List<SenceInfo>  datas;
    private BindScenceInfoAdapter adapter;
    private int tartUid=-1;
    private Serial serial;
    private int deviceId=-1;
    private DeviceInfo deviceInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_device_sence);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        lvBindDeciceSence = (ListView) findViewById(R.id.lv_bind_decice_sence);
    }

    @Override
    protected void initData() {
        initApi();
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("绑定场景");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        tvRightMenu.setText("完成");
        //tartUid=getIntent().getIntExtra("senceUid",-1);
        deviceId=getIntent().getIntExtra("deviceUid",-1);
        List<DeviceInfo> deviceInfos=AppContext.getmOurDevices();
        for (int i = 0; i <deviceInfos.size() ; i++) {
            if(deviceId==deviceInfos.get(i).getUId()){
                deviceInfo=deviceInfos.get(i);
            }
        }
        showLoadingDialog(null);
        datas=new ArrayList<>();
        checkTimer();
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };

        //接收到Serial中
        Subscription subscription= RxBus.getInstance().toObservable(SenceInfo.class)
                .onBackpressureBuffer(10000)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SenceInfo>() {
                    @Override
                    public void call(SenceInfo senceInfo) {
                        if (senceInfo==null)return;

                        for (int i = 0; i <datas.size() ; i++) {
                            if (datas.get(i)==null)
                                continue;
                            if(datas.get(i).getSenceId()==senceInfo.getSenceId()){
                                if (datas.get(i).getSenceName().equals(senceInfo.getSenceName())){
                                    return;
                                }else{
                                    datas.remove(i);
                                    datas.add(senceInfo);
                                    adapter.notifyDataSetChanged();
                                    return;
                                }
                            }
                        }
                        datas.add(senceInfo);
                        LogUtil.e("绑定列表",""+senceInfo.getSenceId());
                        adapter.notifyDataSetChanged();
                    }
                },onErrorAction);
        mCompositeSubscription.add(subscription);

        Subscription mSubscription1 = RxBus.getInstance().toObservable(BindSenceInfoEvent.class)
                .compose(TransformUtils.<BindSenceInfoEvent>defaultSchedulers())
                .subscribe(new Action1<BindSenceInfoEvent>() {
                    @Override
                    public void call(BindSenceInfoEvent event) {
                        //接收绑定场景信息
                        if(event==null)return;
                        if(event.getUid()==deviceId){
                            tartUid=event.getSceneId();
                            for (int i = 0; i <datas.size() ; i++) {
                                if(tartUid==datas.get(i).getSenceId()){
                                    adapter.setItemisSelectedMap(i,true);
                                    break;
                                }
                            }
                        }
                        LogUtil.e("返回","绑定场景信息返回");
                        hideLoadingDialog();
                    }
                });
        mCompositeSubscription.add(mSubscription1);

        //获取场景
        AppContext.getInstance().getSerialInstance().getSences();

        adapter =new BindScenceInfoAdapter(this,datas);
        lvBindDeciceSence.setAdapter(adapter);
        lvBindDeciceSence.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.setItemisSelectedMap(position,true);
//                if(adapter.getisSelectedAt(position)){
//                    adapter.clearMap();
//                }else{
//                    adapter.setItemisSelectedMap(position,true);
//                }
            }
        });

    }
    private Subscription subscription;
    private void checkTimer() {

       subscription = Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(Long aLong) {

                        if(datas.size()>0){
                            AppContext.getInstance().getSerialInstance().getBindInfo();
                        }
                        if(aLong==5){
                            if (subscription != null && !subscription.isUnsubscribed()) {
                                subscription.unsubscribe();
                            }
                            hideLoadingDialog();
                        }
                    }
                });
        mCompositeSubscription.add(subscription);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                int senceid=-1;
                String name=null;
                for (int i = 0; i <datas.size() ; i++) {
                    if(adapter.getisSelectedAt(i)){
                        senceid=datas.get(i).getSenceId();
                        name=datas.get(i).getSenceName();
                        break;
                    }
                }
                if(senceid==-1){
                    ToastUtils.showShort("请选择绑定场景");
                    return;
                }
                showLoadingDialog(null);
                bindSence(senceid,name);
                break;
        }
    }

    private void bindSence(final int  mbindSenceId,final String name) {
        Subscription sub=Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret=AppContext.getInstance().getSerialInstance().ModifySceneSwitchZCL(deviceInfo,(byte)4,(byte) mbindSenceId);
                subscriber.onNext(ret);
                subscriber.onCompleted();
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(Integer ret) {
                        if(ret>=0){
                            hideLoadingDialog();
                            ToastUtils.showShort("绑定成功");
                            RxBus.getInstance().post(new BindDevSenceEvent(2,mbindSenceId,name));
                            finish();

                        }else{
                            hideLoadingDialog();
                            ToastUtils.showShort("绑定失败请重试");
                        }

                    }
                });

        mCompositeSubscription.add(sub);

    }
}
