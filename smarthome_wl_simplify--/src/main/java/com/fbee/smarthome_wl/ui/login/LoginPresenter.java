package com.fbee.smarthome_wl.ui.login;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.LocalLoginReq;
import com.fbee.smarthome_wl.request.LoginReq;
import com.fbee.smarthome_wl.request.RegisterReq;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.Serial;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Headers;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.utils.PreferencesUtils.LOCAL_USERNAME;

/**
 * Created by WLPC on 2017/1/6.
 */

public class LoginPresenter extends BaseCommonPresenter<LoginContract.View> implements LoginContract.Presenter {
    private String remoteId;

    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    /**
     * 九彩登录注册，预留代码后面有变动
     */

    @Override
    public void registerAndlogin(final String username, final String psw) {

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<Object>() {
            @Override
            public void onNext(Object obj) {
                view.loginSuccess(obj);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

        //九彩
        final Map<String, String> headers = new HashMap<>();
        String token = PreferencesUtils.getString("JIUCAI_TOKEN");
        if (token == null) {
            token = "";
        }
        headers.put("Token", token);
        headers.put("Content-Type", "application/json; charset=utf-8");
        Observable<JsonObject> jiu = mApiWrapper.loginJiu(headers, new LoginReq(username, encode(psw))).filter(new Func1<JsonObject, Boolean>() {
            @Override
            public Boolean call(JsonObject resLoginbean) {
                boolean isSuccess = true;
                try {
                    if ("1".equals(resLoginbean.get("status").getAsString())) {
                        JSONObject js = new JSONObject(resLoginbean.toString());
                        view.loginSuccess(js);
                        isSuccess = false;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return isSuccess;
            }
        }).flatMap(new Func1<JsonObject, Observable<BaseNetBean>>() {
            @Override
            public Observable<BaseNetBean> call(JsonObject resLoginbean) {
                return mApiWrapper.registerJiu(headers, new RegisterReq(username, encode(psw), username, remoteId));
            }
        }).filter(new Func1<BaseNetBean, Boolean>() {
            @Override
            public Boolean call(BaseNetBean baseNetBean) {
                boolean isSuccess = false;
                try {
                    if ("1".equals(baseNetBean.status)) {
                        isSuccess = true;
                    }
                } catch (JsonIOException e) {
                    e.printStackTrace();
                }
                return isSuccess;
            }
        }).flatMap(new Func1<BaseNetBean, Observable<JsonObject>>() {
            @Override
            public Observable<JsonObject> call(BaseNetBean baseNetBean) {
                return mApiWrapper.loginJiu(headers, new LoginReq(username, encode(psw)));
            }
        });

        Subscription subscription = jiu.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        mCompositeSubscription.add(subscription);

    }

    /**
     * 飞比的登录
     *
     * @param username
     * @param psw
     */
    @Override
    public void loginFbee(final String username, final String psw) {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
//                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                if (username != null && psw != null) {
                    int ret = mSerial.connectRemoteZll(username, psw);
                    if (ret > 0) {
                        AppContext.getmOurDevices().clear();
                    }

                    subscriber.onNext(ret);
                    subscriber.onCompleted();
                }


            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
//                        registerAndlogin(username,psw);
                    }

                    @Override
                    public void onError(Throwable e) {
//                        registerAndlogin(username,psw);
                    }

                    @Override
                    public void onNext(Integer ret) {
                        view.loginFbeeResult(ret);

                    }
                });

        mCompositeSubscription.add(sub);


//        Subscription sub = Observable.create(new Observable.OnSubscribe<String>() {
//            @Override
//            public void call(Subscriber<? super String> subscriber) {
//
//
//                subscriber.onNext("");
//                subscriber.onCompleted();
//            }
//        }).subscribeOn(Schedulers.newThread())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<String>() {
//                    @Override
//                    public void onCompleted() {
//                    }
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onNext(String word) {
//                    }
//
//
//                });
//        mCompositeSubscription.add(sub);


    }

    /**
     * 飞比本地登录
     *
     */
    @Override
    public void loginLocalFbee() {
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int number = AppContext.getInstance().getSerialInstance().connectLANZll();
                int result = -1;
                if (number > 0) {
                    final String[] ips = AppContext.getInstance().getSerialInstance().getGatewayIps(number);
                    final String[] snids = AppContext.getInstance().getSerialInstance().getBoxSnids(number);
                    final LoginResult.BodyBean.GatewayListBean currentGw = (LoginResult.BodyBean.GatewayListBean) PreferencesUtils.getObject(PreferencesUtils.GATEWAY + PreferencesUtils.getString(LOCAL_USERNAME));

                    for (int i = 0; i < ips.length; i++) {
                        if (currentGw.getUuid().equals(snids[i])) {
                            result = AppContext.getInstance().getSerialInstance().connectLANZllByIp(ips[i], snids[i]);
                            break;
                        }
                    }
                }
                subscriber.onNext(result);
                subscriber.onCompleted();
            }
        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        view.hideLoading();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.hideLoading();
                    }

                    @Override
                    public void onNext(Integer ret) {
                        view.loginLocalFbeeResult(ret);
                    }
                });

        mCompositeSubscription.add(sub);
    }


    /**
     * 本地服务器登录
     *
     * @param bodyBean
     */
    @Override
    public void loginLocal(LocalLoginReq bodyBean) {
        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<Response<JsonObject>>() {
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void onNext(Response<JsonObject> response) {
                try {
                    Headers  header= response.headers();
                    Date date=header.getDate("Date");
                    Long time=date.getTime();
                    JsonObject json = response.body();
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0){
                            ToastUtils.showShort("数据解析错误！");
                            return;
                        }
                        LoginResult info = new Gson().fromJson(jsonObj.toString(), LoginResult.class);

                        view.loginLocalsuccess(info, time);
                    }else{
                        ToastUtils.showShort("数据解析错误！");
                    }
                } catch (Exception e) {
                    ToastUtils.showShort("数据解析错误！");
                    e.printStackTrace();
                }
                //                if (null != json) {
//                    JsonObject jsonObj = json.getAsJsonObject("UMS");
//                    if (null == jsonObj || jsonObj.size() == 0)
//                        return;
//                    LoginResult info = new Gson().fromJson(jsonObj.toString(), LoginResult.class);
//                    view.loginLocalsuccess(info);
//
//                }
            }
        });

        Ums ums = getUms("MSG_USER_LOGIN_REQ", bodyBean);
        Subscription sub = mApiWrapper.loginLocal(ums).subscribe(subscriber);
        mCompositeSubscription.add(sub);
    }



    public final static String encode(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] btInput = s.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str).toLowerCase();//全部强制为小写，与ios保持一致
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

}
