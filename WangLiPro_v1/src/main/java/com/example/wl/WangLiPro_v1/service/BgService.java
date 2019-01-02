package com.example.wl.WangLiPro_v1.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.wl.WangLiPro_v1.devices.LockVideoActivity;
import com.example.wl.WangLiPro_v1.devices.WangliCallActivity;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.Incall;
import com.jwl.android.jwlandroidlib.bean.IncallBean;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;
import com.jwl.android.jwlandroidlib.tcp.JwlTcpSocket;
import com.jwl.android.jwlandroidlib.tcp.TCPSocketCallback;
import com.jwl.android.jwlandroidlib.udp.UdpConfig;
import com.jwl.android.jwlandroidlib.utils.LogHelper;

/**
 * Created by fanliang on 17/7/31.
 */

public class BgService extends Service {

    private JwlTcpSocket socket;
    public static final String LOGINRES = "JWLLoginRes";
    public static final String INCALL = "JWLIncall";
    public static final String JWLRINGOFF = "JWLRingOff";
    public static String USERID ="" ;
    public static String TOKEN ="" ;
    //        public static final String IP = "192.168.1.200";//内网
    public static final String IP = "118.190.73.151";//外网
    public static final int PORT = 9001;
    private SharedPreferences sPre;
    public static final String LOGIN_ACTION = "com.jwl.login.action";
    private MyBro myBs;
    String serviceVoipIp ;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogHelper.print(this, "bgservice...oncreate");

//       TODO   2018年7月16日 14:32:43
//        serviceVoipIp = sPre.getString("IP", serviceVoipIp);
//        //创建TCP长连接
//        createTcpSocket(serviceVoipIp, PORT);
        LogHelper.print("==", TOKEN);
        LogHelper.print("==", USERID);

        myBs = new MyBro();
        IntentFilter inf = new IntentFilter();
        inf.addAction(LOGIN_ACTION);
        registerReceiver(myBs, inf);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.closeTcp();
        }
        if (myBs != null)
            unregisterReceiver(myBs);
    }

    void createTcpSocket(String ip, int port) {
//        if(socket != null){
//            socket.close();
//            socket = null;
//        }
        socket = JwlTcpSocket.getInstance();
        socket.setDataCallBack(new TCPSocketCallback() {
            @Override
            public void connected() {
                LogHelper.print("", "开始发送login数据");
                socket.sendBgTcpLogin(USERID, TOKEN);
            }

            @Override
            public void receive(byte[] buffer) {
                //接收后台服务器的消息
                initDataCallBack(buffer);
            }

            @Override
            public void disconnect() {
                if (socket != null) {
                    socket.sendBgTcpLogin(USERID, TOKEN);
                } else {
                    createTcpSocket(serviceVoipIp, PORT);
                }
//                socket=null;
            }
        });
        socket.startTcpConnect(ip, port);
        sPre = getSharedPreferences("myuser", Context.MODE_MULTI_PROCESS);
        USERID = sPre.getString("userId", USERID);
        TOKEN = sPre.getString("token", TOKEN);
    }

    void initDataCallBack(byte[] buff) {
        String callBackDate = new String(buff);
        if (callBackDate.contains(LOGINRES)) {
            LogHelper.print(this, "后台登录");
            //后台登录
        } else if (callBackDate.contains(INCALL)) {
            LogHelper.print(this, "门铃事件建立连接");
            //门铃已经响应了
            String incallId = getTcpIncallId(callBackDate);
            socket.sendIncallIdToServer(incallId);
            getIncall(incallId);
        } else if (callBackDate.contains(JWLRINGOFF)) {
            //门铃失效了
            LogHelper.print(this, "门铃已经失效");
        } else if (callBackDate.contains("JWLIdle")) {
            //后台长连接心跳
            socket.writeDate("JWLIdle".getBytes());
            LogHelper.print(this, "后台长连接心跳");
        }
    }


    String getTcpIncallId(String data) {
        String incallId = "";
        incallId = data.split(",")[0];
        incallId = data.substring(20, incallId.length());
        return incallId;
    }

    public void getIncall(final String incallId) {
        HttpManager.getInstance(this).getIncall(USERID, TOKEN, incallId, new HttpDataCallBack<IncallBean>() {
            @Override
            public void httpDateCallback(IncallBean bean) {
                Incall incall = bean.getData().getIncall();
                String sNumber = bean.getData().getIncall().getDeviceSerialNumber();
                try {
                    //初始化加密秘钥
                    UdpConfig.initEnc(incall.getDeviceVersion(), incall.getRandomKey(), sNumber, incall.getSsid());
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                if (LockVideoActivity.this.isDestroyed())
                Intent intent = new Intent(BgService.this, WangliCallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("incall", incallId);
                intent.putExtra("deviceId", sNumber);
                intent.putExtra("ip", bean.getData().getIncall().getServerIp());
                startActivity(intent);
            }

            @Override
            public void httpException(HttpException e) {

            }

            @Override
            public void complet() {

            }
        });
    }


    public void close() {
        JwlTcpSocket.getInstance().close();
    }

    class MyBro extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(LOGIN_ACTION)) {
                LogHelper.print("BgService", "接收到广播");
//                createTcpSocket(IP,PORT);
                if (socket != null) {
                    LogHelper.print("BgService", "11111111111111111    接收到广播");
                    sPre = getSharedPreferences("myuser", Context.MODE_MULTI_PROCESS);
                    USERID = sPre.getString("userId", USERID);
                    TOKEN = sPre.getString("token", TOKEN);
                    LogHelper.print("==", TOKEN);
                    LogHelper.print("==", USERID);
                    socket.sendBgTcpLogin(USERID, TOKEN);
                } else {
                    LogHelper.print("BgService", "2222222    接收到广播");
                    createTcpSocket(serviceVoipIp, PORT);
                }
            }
        }
    }
}
