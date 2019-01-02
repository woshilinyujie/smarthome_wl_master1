package com.fbee.smarthome_wl.ui.login;


import android.support.v4.util.ArrayMap;

import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseCommonPresenter;
import com.fbee.smarthome_wl.bean.ResAnychatLogin;
import com.fbee.smarthome_wl.bean.Ums;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.request.LocalLoginReq;
import com.fbee.smarthome_wl.request.LoginReq;
import com.fbee.smarthome_wl.request.RegisterReq;
import com.fbee.smarthome_wl.response.BaseNetBean;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.PreferencesUtils;
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

/**
 * Created by WLPC on 2017/1/6.
 */

public class LoginPresenter extends BaseCommonPresenter<LoginContract.View> implements LoginContract.Presenter {


    public LoginPresenter(LoginContract.View view) {
        super(view);
    }

    /**
     * 九彩登录注册，预留代码后面有变动
     */

    @Override
    public void registerAndlogin(final String username, final String psw) {

        final Map<String,String> map=new HashMap<String,String>();
        map.put("mobile",username);
        map.put("password",psw);

        final Map<String,String> regmap=new HashMap<String,String>();
        regmap.put("name",username);
        regmap.put("mobile", username);
        regmap.put("password", psw);

        Subscriber subscriber = newMySubscriber(new SimpleMyCallBack<Object>() {
            @Override
            public void onNext(Object obj) {
                //  LogUtil.e("onNext","=============");
                view.loginSuccess(obj);
            }
            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }
        });

        final  Observable<ResAnychatLogin> anychat = mApiWrapper.loginAnychat(map).filter(new Func1<ResAnychatLogin, Boolean>() {
            @Override
            public Boolean call(ResAnychatLogin resAnychatLogin) {
                String status = resAnychatLogin.getStatus();
                boolean isneedReg = false;
                if (null != status) {
                    if ("failure".equals(status)) {
                        isneedReg = true;
                    } else if ("success".equals(status)) {

                    }
                }
                if(!isneedReg)
                    view.loginSuccess(resAnychatLogin);
                return isneedReg;
            }
        }).flatMap(new Func1<ResAnychatLogin, Observable<ResAnychatLogin>>() {
            @Override
            public Observable<ResAnychatLogin> call(ResAnychatLogin resAnychatLogin) {

                return mApiWrapper.registerAnychat(regmap);
            }
        }).filter(new Func1<ResAnychatLogin, Boolean>() {
            @Override
            public Boolean call(ResAnychatLogin resAnychatLogin) {
                String status = resAnychatLogin.getStatus();
                boolean isSuccess = true;
                String remoteId = resAnychatLogin.getUser().getId() + "";
                if (null != status){
                    if ("001".equals(status)) {

                    } else if ("002".equals(status)) {
                        isSuccess = false;
                    }
                }
                return isSuccess;
            }
        }).flatMap(new Func1<ResAnychatLogin, Observable<ResAnychatLogin>>() {
            @Override
            public Observable<ResAnychatLogin> call(ResAnychatLogin resAnychatLogin) {
                return mApiWrapper.loginAnychat(map);
            }
        });

        //九彩

        final  Map<String, String> headers = new HashMap<>();
        String token=PreferencesUtils.getString("JIUCAI_TOKEN");
        if(token==null){
            token="";
        }
        headers.put("Content-Type","application/json; charset=utf-8");
        ArrayMap loginReq = new ArrayMap();
        loginReq.put("mobile", username);
        loginReq.put("password", psw);
        Observable<JsonObject> jiu = mApiWrapper.loginJiu(loginReq).filter(new Func1<JsonObject, Boolean>() {
            @Override
            public Boolean call(JsonObject resLoginbean) {
                boolean isSuccess = true;
                // LogUtil.e("jiucaiJson",resLoginbean.toString());
                try {
                    if ("success".equals(resLoginbean.get("status").getAsString())) {
                        JSONObject js=new JSONObject(resLoginbean.toString());
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
                ArrayMap registerreq = new ArrayMap<String, String>();
                registerreq.put("mobile", username);
                registerreq.put("password", psw);
                registerreq.put("name", username);
                registerreq.put("city", AppContext.getLocation());
                registerreq.put("device", username);
                registerreq.put("email", "56464545@qq.com");
                return mApiWrapper.registerJiu(registerreq);
            }
        }).filter(new Func1<BaseNetBean, Boolean>() {
            @Override
            public Boolean call(BaseNetBean baseNetBean) {
                boolean isSuccess = false;
                LogUtil.e("zhuce",baseNetBean.msg);
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

            private LoginReq loginReq;

            @Override
            public Observable<JsonObject> call(BaseNetBean baseNetBean) {
                ArrayMap loginReq = new ArrayMap();
                loginReq.put("mobile", username);
                loginReq.put("password", psw);
                return mApiWrapper.loginJiu(loginReq);
            }
        });

        Subscription subscription = Observable.merge(anychat,jiu)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);

        //  mApiWrapper.registerJiu(headers,new RegisterReq(username, psw, username, remoteId)) .subscribe(subscriber);

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

                    Headers header = response.headers();
                    Date date = header.getDate("Date");
                    Long time = date.getTime();
                    JsonObject json = response.body();
                    if (null != json) {
                        JsonObject jsonObj = json.getAsJsonObject("UMS");
                        if (null == jsonObj || jsonObj.size() == 0)
                            return;
                        LoginResult info = new Gson().fromJson(jsonObj.toString(), LoginResult.class);

                        view.loginLocalsuccess(info, time);
                    }
                } catch (Exception e) {
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
