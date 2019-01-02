package com.example.wl.WangLiPro_v1.devices;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wl.WangLiPro_v1.AudioPlay;
import com.example.wl.WangLiPro_v1.MyAudioRecord;
import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.adapter.BaseRecylerAdapter;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.example.wl.WangLiPro_v1.login.LoginActivity;
import com.example.wl.WangLiPro_v1.utils.DialogUtils;
import com.example.wl.WangLiPro_v1.view.JwlVideoView;
import com.example.wl.WangLiPro_v1.view.PopwindowChoose;
import com.jwl.android.jwlandroidlib.udp.UdpManager;
import com.jwl.android.jwlandroidlib.udp.inter.CommInter;
import com.jwl.android.jwlandroidlib.udp.inter.UiUdpDataCallback;
import com.jwl.android.jwlandroidlib.udpbean.BaseUdpBean;
import com.jwl.android.jwlandroidlib.utils.LogHelper;
import com.jwlkj.idc.jni.JwlJni;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class LockVideoActivity extends BaseActivity implements BaseRecylerAdapter.OnItemClickLitener, View.OnClickListener {

    private Dialog dialog = null;
    private static UiUdpDataCallback callback;
    private String incallId = "01234567890123456789012345678912";
    private String deviceId;
    private MyAudioRecord record;
    private AudioPlay play;
    public static boolean isAlive = false;
    public static String ip = "";
    private JwlVideoView vv;
    private String userid;
    private PopwindowChoose popwindow;
    private ImageView ivRightMenu;
    private LinearLayout doorlock;
    private LinearLayout capture;
    private ImageView btnSpeak;
    private TextView TVtime;
    private ImageView btnMute;
    private ImageView hangupCall;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_video);
//        EventBus.get
//        EventBus.getDefault().register(this);
        isAlive = true;
        vv = (JwlVideoView) findViewById(R.id.vv);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("视频通话");
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        ivRightMenu.setVisibility(View.VISIBLE);
        ivRightMenu.setImageResource(R.mipmap.ic_add);
        ivRightMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popwindow != null && popwindow.isShowing()) {
                    colsePop();
                } else {
                    showPop();
                }
            }
        });
        deviceId = getIntent().getExtras().getString("deviceId");
        incallId = getIntent().getExtras().getString("incall");
        ip = getIntent().getExtras().getString("ip");
        dialog = DialogUtils.getDialog(LockVideoActivity.this);
        dialog.show();
        callback = new UiUdpDataCallback() {
            @Override
            public void commandData(int command) {
                dialog.dismiss();
                if (command == 200) {
                    JwlJni.openH264(640, 480);
                    vv.play();
                    play = new AudioPlay();
                    play.start();
                    //按住说话
                    getTimimg();
                } else {
                    DialogUtils.showErroMsg(LockVideoActivity.this, "指令 " + command + " 没有接收到");
                }
            }

            @Override
            public void videoData(byte[] data) {

                vv.addData(data);
            }

            @Override
            public void audioData(byte[] data) {
                AudioPlay.list.add(data);
            }
        };

        //Z12345670003��������     d28eda01041243958dc942986b6f7107
        //0c7c677b03c74a3392b2a0229d829cf3    114.215.81.71
        userid = AppContext.getUSERID();
        UdpManager.getinstance().start(deviceId, userid, incallId, ip, callback);
    }

    int timeJianGe = 0;

    private void getTimimg() {

        Observable.interval(0, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        ++timeJianGe;
                        String time = getTime(timeJianGe);
                        TVtime.setText(time);
                    }
                });
    }

    /**
     * 猫眼视频计时
     */
    public String getTime(int time) {
        String hFormat = null;
        String minutesFormat = null;
        String secondsFormat = null;
        int hour = time / 3600;
        if (hour < 10) {
            hFormat = "0" + String.format("%d", hour);
        } else {
            hFormat = String.format("%d", hour);
        }
        int minutes = time % 3600 / 60;
        if (minutes < 10) {
            minutesFormat = "0" + String.format("%d", minutes);
        } else {
            minutesFormat = String.format("%d", minutes);
        }
        int seconds = time % 60;
        if (seconds < 10) {
            secondsFormat = "0" + String.format("%d", seconds);
        } else {
            secondsFormat = String.format("%d", seconds);
        }

        String timeFormat = hFormat + ":" + minutesFormat + ":" + secondsFormat;
        return timeFormat;

    }

    private void colsePop() {
        if (popwindow != null && popwindow.isShowing())
            popwindow.dismiss();
    }

    private void showPop() {
        if (popwindow != null && !popwindow.isShowing()) {

            View view = popwindow.mContentView;
            //测量view 注意这里，如果没有测量  ，下面的popupHeight高度为-2  ,因为LinearLayout.LayoutParams.WRAP_CONTENT这句自适应造成的
            view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int popupWidth = view.getMeasuredWidth();    //  获取测量后的宽度
            int popupHeight = view.getMeasuredHeight();  //获取测量后的高度
            int[] location = new int[2];

            // 获得位置 这里的v是目标控件，就是你要放在这个v的上面还是下面
            ivRightMenu.getLocationOnScreen(location);
            //这里就可自定义在上方和下方了 ，这种方式是为了确定在某个位置，某个控件的左边，右边，上边，下边都可以
            popwindow.showAtLocation(ivRightMenu, Gravity.NO_GRAVITY, (location[0] + ivRightMenu.getWidth() / 2) - popupWidth / 2, location[1] + ivRightMenu.getHeight());

        }

    }


    @Override
    protected void initData() {
        doorlock.setOnClickListener(this);
        btnMute.setOnClickListener(this);
        hangupCall.setOnClickListener(this);
        capture.setOnClickListener(this);
        back.setOnClickListener(this);
        if (popwindow == null) {
            ArrayList<PopwindowChoose.Menu> menulist = new ArrayList<PopwindowChoose.Menu>();

            PopwindowChoose.Menu pop = new PopwindowChoose.Menu(R.mipmap.add, "门锁配置");
            menulist.add(pop);
            PopwindowChoose.Menu pop1 = new PopwindowChoose.Menu(R.mipmap.add, "用户管理");
            menulist.add(pop1);
//            PopwindowChoose.Menu pop2 = new PopwindowChoose.Menu(R.mipmap.add, "操作记录");
//            menulist.add(pop2);
            popwindow = new PopwindowChoose(this, menulist, this, true);
            popwindow.setAnimationStyle(R.style.popwin_anim_style);
        }

        //按住说话
        btnSpeak.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        record = new MyAudioRecord(new MyAudioRecord.OpenRecordInter() {
                            @Override
                            public void recorderFail() {

                            }
                        });
                        record.start();
                        break;
                    case MotionEvent.ACTION_UP:
//                        record.exit = true;
                        record.pause();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    protected void initView() {
        doorlock = (LinearLayout) findViewById(R.id.doorlock_imageicon);
        capture = (LinearLayout) findViewById(R.id.tv_screen_capture);
        TVtime = (TextView) findViewById(R.id.call_time);
        btnSpeak = (ImageView) findViewById(R.id.btn_speak);
        btnMute = (ImageView) findViewById(R.id.btn_mute);
        hangupCall = (ImageView) findViewById(R.id.btn_hangupCall);
        back = (ImageView) findViewById(R.id.back);
    }


//    @Subscribe
//    public void onEventMainThread(CloseType ev) {
//        LogHelper.print("ClosePassUI------", "收到广播------"+ev.getMsg());
//        if (ev.getMsg() ==1) {
//            eventsmart();
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    UdpManager.getinstance().close();
//                }
//            }, 2000);
//            finish();
//        }
//    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        eventsmart();
    }

    @Override
    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LogHelper.print("LockVideoActivity", "LockVideoActivity   ------onDetachedFromWindow() ");
        eventsmart();
    }

    @Override
    public void finish() {
        isAlive = false;
        super.finish();
        LogHelper.print("LockVideoActivity", "LockVideoActivity   ------onDetachedFromWindow() ");
        eventsmart();
    }


    private void eventsmart() {
        UdpManager.getinstance().tearDown(new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                if (baseUdpBean.getErroCode() == 200) {
                    eventsmart();
                }
            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {

            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UdpManager.getinstance().close();
            }
        }, 2000);
    }


    @Override
    public void onItemClick(View view, int position) {
//配置
        if (position == 0) {
            startActivity(new Intent(this, CommTestActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        colsePop();
    }

    boolean falg = true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //截屏
            case R.id.tv_screen_capture:
                Bitmap bitmap = convertViewToBitmap(vv);
                boolean b = saveImageToGallery(this, bitmap);
                if (b) {
                    Toast.makeText(this, "截图成功", Toast.LENGTH_SHORT).show();
                }
                break;
            //开锁
            case R.id.doorlock_imageicon:
                setDoorLockState();
                break;
            //静音
            case R.id.btn_mute:
                if (falg) {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager != null) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                        audioManager.getStreamVolume(AudioManager.STREAM_RING);
                        Log.d("Silent:", "RINGING 已被静音");
                        falg = false;
                    }
                } else {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    if (audioManager != null) {
                        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                        audioManager.getStreamVolume(AudioManager.STREAM_RING);
                        Log.d("SilentListenerService", "RINGING 取消静音");
                        falg = true;
                    }
                }

                break;
            //挂断
            case R.id.btn_hangupCall:
            case R.id.back:
                finish();
                break;
        }
    }

    public Bitmap convertViewToBitmap(View view) {
        if (view == null) {
            return null;
        }
        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(screenshot);
        c.translate(-view.getScrollX(), -view.getScrollY());
        view.draw(c);
        return screenshot;
    }

    /**
     * 保存文件到指定路径
     */
    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Button btn_1;
    private Button btn_2;
    private Button btn_3;
    private Button btn_4;
    private Button btn_5;
    private Button btn_6;
    private Button btn_7;
    private Button btn_8;
    private Button btn_9;
    private Button btn_10;
    private EditText et_password;


    /**
     * 设置锁的状态
     *
     */
    private void setDoorLockState() {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去除对话框的标题
        dialog.setContentView(R.layout.unlock_password_dialog);
        // 在代码中设置界面大小的方法:
        Display display = getWindowManager().getDefaultDisplay();
        // 获取屏幕宽、高
        Window window = dialog.getWindow();
        window.setGravity(Gravity.BOTTOM);// 对话框底对齐
        window.setBackgroundDrawableResource(R.drawable.news_home_dialog_border);
        ViewGroup.LayoutParams windowLayoutParams = window.getAttributes(); // 获取对话框当前的参数值
        windowLayoutParams.width = (int) (display.getWidth()); // 宽度设置为屏幕的0.85
        windowLayoutParams.height = (int) (display.getHeight() * 0.6); // 高度设置为屏幕的0.24
        dialog.show();
        dialog.setCancelable(false);// 点击外面和返回建无法隐藏
        et_password = (EditText) dialog.findViewById(R.id.et_password);
        btn_1 = (Button) dialog.findViewById(R.id.btn_1);
        btn_1.setText("1");
        btn_2 = (Button) dialog.findViewById(R.id.btn_2);
        btn_2.setText("2");
        btn_3 = (Button) dialog.findViewById(R.id.btn_3);
        btn_3.setText("3");
        btn_4 = (Button) dialog.findViewById(R.id.btn_4);
        btn_4.setText("4");
        btn_5 = (Button) dialog.findViewById(R.id.btn_5);
        btn_5.setText("5");
        btn_6 = (Button) dialog.findViewById(R.id.btn_6);
        btn_6.setText("6");
        btn_7 = (Button) dialog.findViewById(R.id.btn_7);
        btn_7.setText("7");
        btn_8 = (Button) dialog.findViewById(R.id.btn_8);
        btn_8.setText("8");
        btn_9 = (Button) dialog.findViewById(R.id.btn_9);
        btn_9.setText("9");
        btn_10 = (Button) dialog.findViewById(R.id.btn_10);
        btn_10.setText("0");

        // ========================================================================关闭键盘====================
        dialog.findViewById(R.id.btn_close).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

        dialog.findViewById(R.id.iv_delete).setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int index = et_password.getSelectionStart();
                        Editable editable = et_password.getText();
                        if (editable != null && index > 0) {
                            editable.delete(index - 1, index);
                        }
                    }
                });
        dialog.findViewById(R.id.btn_positive).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 对话框确定按钮
                        final String password = et_password.getText().toString();
                        if (password.length() == 6) {
                            openLock(password);
                            dialog.dismiss();
                            // //-----------------------------------------------华丽分割线------5.7
                        } else if (TextUtils.isEmpty(password)) {
                            Toast.makeText(LockVideoActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第1个按钮
                et_password.append(btn_1.getText().toString());
            }
        });
        btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第2个按钮
                et_password.append(btn_2.getText().toString());
            }
        });
        btn_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第3个按钮
                et_password.append(btn_3.getText().toString());
            }
        });
        btn_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第4个按钮
                et_password.append(btn_4.getText().toString());
            }
        });
        btn_5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 对话框第5个按钮
                et_password.append(btn_5.getText().toString());
            }
        });
        btn_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第6个按钮
                et_password.append(btn_6.getText().toString());
            }
        });
        btn_7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第7个按钮
                et_password.append(btn_7.getText().toString());
            }
        });
        btn_8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第8个按钮
                et_password.append(btn_8.getText().toString());
            }
        });
        btn_9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第9个按钮
                et_password.append(btn_9.getText().toString());
            }
        });
        btn_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 对话框第10个按钮
                et_password.append(btn_10.getText().toString());
            }
        });
    }


    public void openLock(String pwd) {

        dialog.show();
        UdpManager.getinstance().openLock(pwd, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                if (baseUdpBean.getErroCode() == 100) {
                    Toast.makeText(LockVideoActivity.this, "开门成功", Toast.LENGTH_SHORT).show();
//                    sendBroadcast(new Intent(BgService.LOGIN_ACTION));
                } else {
                    Toast.makeText(LockVideoActivity.this, "开门失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void erro(String msg) {
                DialogUtils.showErroMsg(LockVideoActivity.this, msg);
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });
    }
}
