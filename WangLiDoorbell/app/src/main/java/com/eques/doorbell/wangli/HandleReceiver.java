package com.eques.doorbell.wangli;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.attr.tag;

public class HandleReceiver extends BroadcastReceiver {

	// 根据不同厂商，需要修改的参数 {
	private static final String TAG = "tp_eques";

	private static final String KEY_ID = "fa30ea12ac7c66c5";

	private static final String EQUES_APPKEY = "ZmfPtrc4SjHXca2AYXmsfjCQPeNPKdFi";

	// 根据不同厂商，需要修改的参数 }

	/**
	 * 推送消息的action
	 */
	public static final String ACTION_EQUES_PUSH_MESSAGE = "com.eques.action.PUSH_MESSAGE"
			+ "." + KEY_ID;

	/**
	 * 系统启动的action，通知第三方应用启动service
	 */
	public static final String ACTION_EQUES_BOOT_COMPLETED = "com.eques.action.BOOT_COMPLETED"
			+ "." + KEY_ID;

	/**
	 * 系统ping的action，保持心跳使用，可以不使用
	 */
	public static final String ACTION_EQUES_PING = "com.eques.action.PING"
			+ "." + KEY_ID;

	/**
	 * 第三方应用发送的验证信息
	 */
	public static final String ACTION_EQUES_ACCOUNT_VALIDATION = "com.eques.action.ACCOUNT_VALIDATION";

	/**
	 * 登录成功后发送设备信息及绑定的用户信息给第三方应用
	 */
	public static final String ACTION_EQUES_LOGIN_INFO = "com.eques.action.LOGIN_INFO"
			+ "." + KEY_ID;

	/**
	 * 消息方法名，用于区分是那种消息类型
	 */
	public static final String EXTRA_METHOD = "method";

	/**
	 * 设备ID
	 */
	public static final String EXTRA_DEVID = "devid";

	/**
	 * 是否已经上传完成。 1. 上传成功 2. 未上传
	 */
	public static final String EXTRA_UPLOAD = "upload";

	/**
	 * 消息的内容类型 1. 文本 2. 图片 3. 视频 4. Zip压缩包 5. 音频
	 */
	public static final String EXTRA_TYPE = "type";

	/**
	 * 上传文件的ID 门铃消息使用，其他消息不使用。
	 */
	public static final String EXTRA_FID = "fid";

	/**
	 * 与设备建立绑定关系的用户的用户名
	 */
	public static final String EXTRA_USERNAME = "username";

	/**
	 * 设备在移康云端服务器上的唯一标识
	 */
	public static final String EXTRA_DEVBID = "devbid";

	public static final String EXTRA_KEY_ID = "key_id";

	public static final String EXTRA_EQUES_APPKEY = "eques_appkey";

	public static final String EXTRA_JPUSH_APPKEY = "jpush_appkey";

	public static final String EXTRA_JPUSH_MASTER_SECRET = "jpush_master_secret";

	public static final String EXTRA_JPUSH_APPKEY_BUS = "jpush_appkey_bus";

	public static final String EXTRA_JPUSH_MASTER_SECRET_BUS = "jpush_master_secret_bus";
	//极光服务器
	private String url ="https://api.jpush.cn/v3/push";

	private int method;
	private String devid;
	private int upload;
	private int type;
	private String fid;
	String username = "USERNAME";

//	private String Name ="{'method':'"+method+"','devid':'"+devid+"','upload':'"+upload+"','type':'"+type+"','fid':'"+fid+"'}";
//	String appid = "5d0aa44b32a6894951f9eab4:5c8972d74be7fad527dc45c8";
	String appid = "6c0ee8d16139dfc5a0ec4660:da63f6a7c065a2ca6cbcef23";
	String enToStr = Base64.encodeToString(appid.getBytes(), Base64.NO_WRAP);
	private String userName;
	private String name;
	private String bid;

	private static ExecutorService singleExecutor = null;
	private Context context;
	private String msg;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		String action = intent.getAction();
		Log.d(TAG, "action: " + action);

		// 注意，正式版本不要启动Activity，否则将影响可视门铃的正常使用！！！
		if (ACTION_EQUES_PUSH_MESSAGE.equals(action)) {
			// 根据不同的消息类型，获取不同的参数
			method = intent.getIntExtra(EXTRA_METHOD, 0);

			if(method  ==1){
				msg = "警报推送";
			}else if(method == 2){
				msg = "门铃推送";
			}
			Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
			Log.e("123456","method"+method);
			devid = intent.getStringExtra(EXTRA_DEVID);
			upload = intent.getIntExtra(EXTRA_UPLOAD, 0);
			type = intent.getIntExtra(EXTRA_TYPE, 0);
			fid = intent.getStringExtra(EXTRA_FID);

			SharedPreferences sp = context.getSharedPreferences(username, 0);
			name = sp.getString("name", "");
			bid = sp.getString("bid", "");

            runOnQueue(runnable);

			Log.d(TAG, "method: " + method + "\t devid: " + devid
					+ "\t upload: " + upload + "\t type: " + type + "\t fid: "
					+ fid);

		} else if (ACTION_EQUES_BOOT_COMPLETED.equals(action)) {
			// 在监听到该广播后，需要将专属的APPKEY发送给可视门铃端，只有获取到正确的APPKEY后，可视门铃才可以正常的登录。
			Log.d(TAG, "set eques appkey: " + EQUES_APPKEY);

			Intent in = new Intent(ACTION_EQUES_ACCOUNT_VALIDATION);
			// 传入key_id作为验证，要和厂商所使用的二维码中的key_id对应
			in.putExtra(EXTRA_KEY_ID, KEY_ID);
			in.putExtra(EXTRA_EQUES_APPKEY, EQUES_APPKEY);
			context.sendBroadcast(in);

		} else if (ACTION_EQUES_PING.equals(action)) {

		} else if (ACTION_EQUES_LOGIN_INFO.equals(action)) {
			userName = intent.getStringExtra(EXTRA_USERNAME);
			Log.e("userName",userName);
			String bid = intent.getStringExtra(EXTRA_DEVBID);

			SharedPreferences sp = context.getSharedPreferences(username, 0);
			SharedPreferences.Editor edit = sp.edit();
			edit.putString("name", userName);
			edit.putString("bid",bid);
			edit.commit();

			Log.d(TAG, "userName: " + userName + "\t bid: " + bid);
		} else {
			Log.d(TAG, "error action");
		}
	}
	public final static int CONNECT_TIMEOUT = 60;
	public final static int READ_TIMEOUT = 100;
	public final static int WRITE_TIMEOUT = 60;
	SimpleDateFormat formatter=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	Date curDate=new Date(System.currentTimeMillis());//获取当前时间
	String str=formatter.format(curDate);

	String messag;
	String tag;
	String sound;
	private void Jpush(int method, String devid, int upload, int type, String fid) throws IOException {

		OkHttpClient okHttp = new OkHttpClient.Builder()
				.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)//设置读取超时时间
				.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)//设置写的超时时间
				.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
				.build();
		//添加请求体
		if (method==1){
			messag = "警报消息"+str;
			sound ="default";
			tag = "__DBAlarm";
		}if (method==2){
			messag="有人按门铃"+str;
			sound ="test.caf";
			tag = "__DBCall";
		}
		String mentoh = "{'android':{'alert':'"+messag+"','title':'王力智能','builder_id':'1','extras':{'bid':'"+bid+"','name':'"+name+"','method':'"+method+"','devid':'"+devid+"','upload':'"+upload+"','type':'"+type+"','fid':'"+fid+"'}},'ios':{'alert':'"+messag+"','sound':'"+sound+"','badge':'+1','extras':{'uuid':'"+bid+"','From':'"+name+"','method':'"+method+"','vendor':'Eques'}}}";
		RequestBody requestBody = new FormBody.Builder()
				.add("platform","all")
				.add("audience","{'tag':['"+name+tag+"']}")//{'tag':['"+name+"']}
				.add("options","{'time_to_live':300,'apns_production':true}")
				.add("notification",(mentoh)).build();
		Log.e("123456","{'tag':['"+name+"__"+tag+"']}");
		//添加请求头
		Request request = new Request.Builder()
				.url(url)
				.addHeader("Content-Type", "application/json")
				.addHeader("Authorization","Basic "+enToStr)
				.post(requestBody)
				.build();
		Log.e("123456", "Basic "+enToStr);

		okhttp3.Call call = okHttp.newCall(request);
		call.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
                Log.e("123456", "失败"+e.getMessage());//400
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
				Log.e("123456", "成功"+messag);
				handler.sendEmptyMessage(1);
			}
		});

	}
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1){
				Toast.makeText(context,"推送成功",Toast.LENGTH_SHORT).show();
			}
		}
	};


    Runnable runnable =new Runnable() {
        @Override
        public void run() {
            try {
                if (name!=null){
                    Jpush(method, devid, upload, type, fid);
                }else {

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    public void runOnQueue (Runnable runnable){
        if (singleExecutor == null) {
            singleExecutor = Executors.newSingleThreadExecutor();
        }
        singleExecutor.submit(runnable);
    }

}
