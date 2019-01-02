package com.fbee.smarthome_wl.base;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.eques.icvss.api.ICVSSListener;
import com.eques.icvss.api.ICVSSUserInstance;
import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.api.ApiWrapper;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.bean.AddDeviceInfo;
import com.fbee.smarthome_wl.bean.EquesAlarmInfo;
import com.fbee.smarthome_wl.bean.EquesDeviceDelete;
import com.fbee.smarthome_wl.bean.EquesDeviceInfo;
import com.fbee.smarthome_wl.bean.EquesDevicePIRInfo;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.bean.EquesVisitorInfo;
import com.fbee.smarthome_wl.common.ActivityPageManager;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.StatusCode;
import com.fbee.smarthome_wl.eques.ICVSSUserModule;
import com.fbee.smarthome_wl.event.EquesAddLockEvent;
import com.fbee.smarthome_wl.event.EquesAlarmDialogEvent;
import com.fbee.smarthome_wl.event.EquesVideoCallEvent;
import com.fbee.smarthome_wl.event.VideoTime;
import com.fbee.smarthome_wl.ui.equesdevice.videocall.EquesVideoCallActivity;
import com.fbee.smarthome_wl.utils.DialogActivity;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogLoading;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Created by ZhaoLi.Wang on 2016/9/23.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity implements View.OnClickListener, ICVSSListener {
    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    protected CompositeSubscription mCompositeSubscription;
    /**
     * 页面布局的 根view
     */
    protected View mContentView;
    /**
     * 来自哪个 页面
     */
    protected String fromWhere;
    /**
     * 加载dialog
     */
    private DialogLoading loading;
    /**
     * Api类的包装 对象
     */
    protected ApiWrapper mApiWrapper;

    protected Subscription mSubscription;

    public T presenter;

    private List<BaseFragment> fragmentList;
    private BaseFragment currentFragment;
    public ICVSSUserInstance icvss;
    private HashMap<String, String> nickbidMap = new HashMap<String, String>();
    ;
    private List<EquesListInfo.bdylistEntity> bdylist;
    private String nameNick;
    private boolean aBoolean;
    private String string;
    private EquesDevicePIRInfo rmbdyResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        icvss = ICVSSUserModule.getInstance(this).getIcvss();
        // 设置不能横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        //Activity管理
        ActivityPageManager.getInstance().addActivity(this);
        fragmentList = new ArrayList<>();

    }


    /**
     * 初始化 Api
     */
    public void initApi() {
        //创建 CompositeSubscription 对象 使用CompositeSubscription来持有所有的Subscriptions，然后在onDestroy()或者onDestroyView()里取消所有的订阅。
        mCompositeSubscription = new CompositeSubscription();
        // 构建 ApiWrapper 对象
        mApiWrapper = new ApiWrapper();
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mContentView = view;
        initFromWhere();
        initView();
        initData();

    }

    /**
     * 创建相应的 presenter
     */
    public void createPresenter(T presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }


    public CompositeSubscription getCompositeSubscription() {
        if (mCompositeSubscription == null) {
            mCompositeSubscription = new CompositeSubscription();
        }
        return mCompositeSubscription;
    }

    public ApiWrapper getApiWrapper() {
        if (mApiWrapper == null) {
            mApiWrapper = new ApiWrapper();
        }
        return mApiWrapper;
    }


    /**
     * 创建观察者  这里对观察着 过滤一次，过滤出我们想要的信息，错误的信息toast
     *
     * @param onNext
     * @param <T>
     * @return
     */
    protected <T> Subscriber newMySubscriber(final SimpleMyCallBack onNext) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                hideLoadingDialog();
                onNext.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                String msg = e.getMessage();
                if (e instanceof Api.APIException) {
                    Api.APIException exception = (Api.APIException) e;
                    ToastUtils.showShort(exception.message);
                } else if (e instanceof HttpException) {
                    if (e instanceof HttpException) {
                        try {
                            HttpException httpException = (HttpException) e;
                            if (TextUtils.isEmpty(httpException.getMessage())) {
                                ToastUtils.showShort("服务器错误");
                            } else {
                                String errorMsg = httpException.getMessage();
                                if (TextUtils.isEmpty(errorMsg)) {
                                    ToastUtils.showShort("服务器错误");
                                } else {
                                    ToastUtils.showShort(errorMsg);
                                }
                            }
                        } catch (Exception IOe) {
                            ToastUtils.showShort("服务器异常");
                        }

                    }
                } else if (e instanceof SocketTimeoutException) {
                    ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.SERV_NET_TIME_OUT));
                } else if (e instanceof ConnectException) {
                    ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.INTERNAL_SERVER_ERROR));
                } else if (e instanceof SocketException && ("Socket closed".equals(msg) || "sendto failed: EBADF (Bad file number)".equals(msg))) {
                    ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.CANCEL));
                } else if (e instanceof IOException) {
                    if ("Canceled".equals(msg)) {
                        ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.CANCEL));
                    } else {
                        ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.INTERNAL_SERVER_ERROR));
                    }
                } else if (e instanceof IllegalAccessException) {
                    ToastUtils.showShort(StatusCode.getServExceptionMessage(StatusCode.SERV_ILLEGAL_ERROR));
                } else if (e instanceof ClassCastException) {
                    ToastUtils.showShort("数据解析异常");
                } else {
                    ToastUtils.showShort("未知异常");
                }
                if (!mCompositeSubscription.isUnsubscribed()) {
                    onNext.onError(e);
                }
                hideLoadingDialog();
            }

            @Override
            public void onNext(T t) {
                if (!mCompositeSubscription.isUnsubscribed()) {
                    onNext.onNext(t);
                }
            }

        };
    }


    /**
     * findviewbyid 使用泛型
     */
    protected <T extends View> T getViewById(int id) {
        return (T) findViewById(id);
    }


    protected void initFromWhere() {
        if (null != getIntent().getExtras()) {
            if (getIntent().getExtras().containsKey("fromWhere")) {
                fromWhere = getIntent().getExtras().getString("fromWhere").toString();
            }
        }
    }

    /**
     * 从哪个类跳转过来
     *
     * @return 类名
     */
    public String getFromWhere() {
        return fromWhere;
    }


    /**
     * 初始化view
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();


    /**
     * 将 Fragment添加到Acitvtiy
     *
     * @param fragment
     * @param frameId
     */
    protected void addFragmentToActivity(@NonNull BaseFragment fragment, int frameId) {
        checkNotNull(fragment);
        this.currentFragment = fragment;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 切换目标Fragment
     *
     * @param containerViewId 容器id
     * @param targetFragment  目标fragment对象
     */
    protected void replaceFragment(int containerViewId, BaseFragment targetFragment) {
        checkNotNull(targetFragment);
        this.currentFragment = targetFragment;
        FragmentManager manager = getSupportFragmentManager();//得到FragmentManager
        FragmentTransaction transaction = manager.beginTransaction();//开启事务
        transaction.replace(containerViewId, targetFragment);
        //提交事务
        transaction.commitAllowingStateLoss();
    }

    /**
     * 显示fragment，该fragment切换时只会调用onResume()，不会影响生命周期
     *
     * @param containerViewId
     * @param targetFragment
     */
    protected void showFragment(int containerViewId, BaseFragment targetFragment) {
        this.currentFragment = targetFragment;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        for (BaseFragment fragment : fragmentList) {
            ft.hide(fragment);
            fragment.onPause();
        }

        if (targetFragment.isAdded()) {
            targetFragment.onResume();
            ft.show(targetFragment);
        } else {
            ft.add(containerViewId, targetFragment);
            fragmentList.add(targetFragment);
        }

        ft.commitAllowingStateLoss();

    }

    /**
     * 获取当前fragment
     *
     * @return
     */
    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }


    /**
     * 统一调用toast
     */
    public void showToast(String content) {
        if (content != null) {
            ToastUtils.showShort(content);
        }
    }


    /**
     * show加载dialog
     */
    public void showLoadingDialog(String msg) {
        if (null == loading)
            loading = new DialogLoading(this);
        if (msg != null)
            loading.setDialogLabel(msg);
        loading.show();

    }

    /**
     * 隐藏dialog
     */
    public void hideLoadingDialog() {
        if (loading != null)
            loading.dismiss();
    }


    /**
     * 各种跳转页面
     *
     * @param clazz
     */
    public void skipAct(Class clazz) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra("fromWhere", getClass().getSimpleName());
        startActivity(intent);
    }

    /**
     * @param clazz
     * @param bundle bundle参数
     */
    public void skipAct(Class clazz, Bundle bundle) {
        Intent intent = new Intent(this, clazz);
        intent.putExtras(bundle);
        intent.putExtra("fromWhere", getClass().getSimpleName());
        startActivity(intent);
    }

    public void skipAct(Class clazz, Bundle bundle, int flags) {
        Intent intent = new Intent(this, clazz);
        intent.putExtra("fromWhere", getClass().getSimpleName());
        intent.setFlags(flags);
        startActivity(intent);
    }


    public static void cancelSubscription(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Acitvity 释放子view资源
        ActivityPageManager.unbindReferences(mContentView);
        // rxbus 取消订阅
        cancelSubscription(mSubscription);
        //从栈中删除activity
        ActivityPageManager.getInstance().removeActivity(this);
        mContentView = null;
        //一旦调用了 CompositeSubscription.unsubscribe()，这个CompositeSubscription对象就不可用了,
        // 如果还想使用CompositeSubscription，就必须在创建一个新的对象了。
        if (mCompositeSubscription != null) {
            mCompositeSubscription.unsubscribe();
        }
        //解绑 presenter
        if (presenter != null) {
            presenter.unsubscribe();
        }

        if (fragmentList != null) {
            fragmentList.clear();
        }

    }

    private void showUIToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(msg);
            }
        });
    }

    @Override
    public void onDisconnect(int i) {
        if (i == 4003) {
            showUIToast("猫眼网络异常,请重新登录");
        }
        LogUtil.e("移康", "onDisconnect" + i);
    }

    @Override
    public void onPingPong(int i) {

    }

    //怡康sdk 回调设备信息
    @Override
    public void onMeaasgeResponse(JSONObject jsonObject) {

        String Call = jsonObject.optString(Method.METHOD);
        String code = jsonObject.optString(Method.ATTR_ERROR_CODE);
        LogUtil.e("移康设备信息", jsonObject + "");
        if (Call.equals(Method.METHOD_BDYLIST)) {
            AppContext.getBdylist().clear();
            EquesListInfo equesInfo = new Gson().fromJson(jsonObject.toString(), EquesListInfo.class);
            bdylist = equesInfo.getBdylist();
            string = PreferencesUtils.getString(PreferencesUtils.LOCAL_USERNAME);
            PreferencesUtils.saveObject(string + "JpushArlam", bdylist);
            for (int i = 0; i < bdylist.size(); i++) {
                String name = bdylist.get(i).getName();
                String nick = bdylist.get(i).getNick();
                String bid = bdylist.get(i).getBid();
                if (!TextUtils.isEmpty(nick)) {
                    nickbidMap.put(bid, nick);
                } else {
                    nickbidMap.put(bid, name);
                }
            }
            AppContext.getBdylist().clear();
            AppContext.getOnlines().clear();
            AppContext.getBdylist().addAll(equesInfo.getBdylist());
            AppContext.getOnlines().addAll(equesInfo.getOnlines());
            RxBus.getInstance().post(equesInfo);
        } else if (Call.equals(Method.METHOD_ONADDBDY_REQ)) {
            AddDeviceInfo addDeviceInfo = new Gson().fromJson(jsonObject.toString(), AddDeviceInfo.class);
            RxBus.getInstance().post(addDeviceInfo);
        } else if (Call.equals(Method.METHOD_ONADDBDY_RESULT)) {
            if ("4000".equals(code)||"4407".equals(code)) {
                EquesAddLockEvent addDeviceInfo = new Gson().fromJson(jsonObject.toString(), EquesAddLockEvent.class);
                RxBus.getInstance().post(addDeviceInfo);
//                else if ("4407".equals(code)) {
//                    showUIToast("该设备已添加");
//                }
            } else if ("4412".equals(code)) {
                showUIToast("所指定的设备或用户不存在");
            }
        } else if (Call.equals(Method.METHOD_EQUES_SDK_LOGIN)) {
            if ("4000".equals(code)) {
                icvss.equesGetDeviceList();
            } else if ("4111".equals(code)) {
                showUIToast("因登录失败次数过多，被锁定");
            } else if ("4411".equals(code)) {
                showUIToast("设备在其它地方登录");
            }

        } else if (Call.equals(Method.METHOD_DEVICEINFO_RESULT)) {
            EquesDeviceInfo equesDeviceInfo = new Gson().fromJson(jsonObject.toString(), EquesDeviceInfo.class);
            RxBus.getInstance().post(equesDeviceInfo);
        } else if (Call.equals(Method.METHOD_ALARM_GET_RESULT)) {
            EquesDevicePIRInfo equesDevicePIRInfo = new Gson().fromJson(jsonObject.toString(), EquesDevicePIRInfo.class);
            RxBus.getInstance().post(equesDevicePIRInfo);
        } else if (Call.equals(Method.METHOD_ALARM_ALMLIST)) {
            EquesAlarmInfo equesAlarmInfo = new Gson().fromJson(jsonObject.toString(), EquesAlarmInfo.class);
            RxBus.getInstance().post(equesAlarmInfo);
        } else if (Call.equals(Method.METHOD_ALARM_RINGLIST)) {
            EquesVisitorInfo equesVisitorInfo = new Gson().fromJson(jsonObject.toString(), EquesVisitorInfo.class);
            RxBus.getInstance().post(equesVisitorInfo);
        } else if (Call.equals(Method.METHOD_ALARM_NEWALM)) {
            String uid = jsonObject.optString(Method.ATTR_BUDDY_UID);
            String bid = jsonObject.optString(Method.ATTR_BUDDY_BID);
            if ( "".equals(uid)) {
                uid = jsonObject.optString(Method.ATTR_FROM);
            }
            aBoolean = PreferencesUtils.getBoolean(uid);
            if (aBoolean) {
                showDialog(bid);
            } else {
                return;
            }
        } else if (Call.equals(Method.METHOD_CALL) && jsonObject.optString(Method.ATTR_CALL_STATE).equals("open")) {

            String sid = jsonObject.optString(Method.ATTR_CALL_SID);
            String from = jsonObject.optString(Method.ATTR_FROM);
            Bundle bundle = new Bundle();
            bundle.putString(Method.ATTR_FROM, from);
            bundle.putString(Method.ATTR_CALL_SID, sid);
            boolean aBoolean = PreferencesUtils.getRingBoolean(from);
            if (aBoolean) {
                skipAct(EquesVideoCallActivity.class, bundle);
            }
        } else if (Call.equals(Method.METHOD_PREVIEW)) {
            String fid = jsonObject.optString(Method.ATTR_ALARM_FID);
            EquesVideoCallEvent equesVideoCallEvent = new EquesVideoCallEvent();
            equesVideoCallEvent.setFid(fid);
            RxBus.getInstance().post(equesVideoCallEvent);
        } else if (Call.equals(Method.METHOD_RMBDY_RESULT)) {
            if (code.equals("4000")) {
                icvss.equesGetDeviceList();
                EquesDeviceDelete rmbdyResult = new EquesDeviceDelete();
                RxBus.getInstance().post(rmbdyResult);
            } else if ("4005".equals(code)) {
                showUIToast("删除失败");
            }
        } else if (Call.equals(Method.METHOD_VIDEOPLAY_STATUS_PLAYING)) {
            VideoTime videoTime = new VideoTime();
            RxBus.getInstance().post(videoTime);
        }
    }

    private boolean isForeground(String className) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName().contains(className);
    }

    private boolean equesAlarmActivity;

    public void showDialog(String bid) {
        equesAlarmActivity = isForeground("EquesAlarmActivity");
        if (equesAlarmActivity) {
            EquesAlarmDialogEvent equesAlarmDialogEvent = new EquesAlarmDialogEvent();
            RxBus.getInstance().post(equesAlarmDialogEvent);
            return;
        }
        if (nickbidMap != null) {
            for (Map.Entry<String, String> entry : nickbidMap.entrySet()) {
                if (entry.getKey().equals(bid)) {
                    nameNick = entry.getValue();
                }
            }
        }
        Intent intent = new Intent(this, DialogActivity.class);
        if (nameNick != null) {
            intent.putExtra("name", nameNick);
        }
        intent.putExtra(Method.ATTR_BUDDY_UID, bid);
        intent.putExtra("istop", equesAlarmActivity);
        startActivity(intent);
    }
}
