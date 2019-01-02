package com.example.wl.WangLiPro_v1.devices;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wl.WangLiPro_v1.R;
import com.example.wl.WangLiPro_v1.api.AppContext;
import com.example.wl.WangLiPro_v1.base.BaseActivity;
import com.example.wl.WangLiPro_v1.main.MainActivity;
import com.example.wl.WangLiPro_v1.service.BgService;
import com.jwl.android.jwlandroidlib.Exception.HttpException;
import com.jwl.android.jwlandroidlib.bean.DeviceBean;
import com.jwl.android.jwlandroidlib.bean.IncallBean;
import com.jwl.android.jwlandroidlib.http.HttpManager;
import com.jwl.android.jwlandroidlib.httpInter.HttpDataCallBack;
import com.jwl.android.jwlandroidlib.udp.UdpManager;
import com.jwl.android.jwlandroidlib.udp.inter.CommInter;
import com.jwl.android.jwlandroidlib.udpbean.BaseUdpBean;
import com.jwl.android.jwlandroidlib.udpbean.LockParameter;

public class CommConfigActivity extends BaseActivity implements View.OnClickListener {

    private LayoutInflater inflater;
    private TextView mIrIoSwc;
    private TextView mPwdIoSwc;
    private TextView mIrWarningSwc;
    private TextView mIrMoniterSwc;
    private TextView mSafeSwc;
    private TextView mVideo;
    private TextView mVolumSizeTv;
    private RelativeLayout mVolumRlt;
    private TextView mIrTimeTv;
    private RelativeLayout mIrTimeRlt;
    private Button mAddAdminUserBt;
    private Button mAddNormalUserBt;
    private Button mAddTempUserBt;
    private Button mLockUsersBt;
    private Button mOpenLockBt;
    private Button mCheckAdminBt;
    private Button mDelUserBt;
    private Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comm_config);
//        mIrIoSwc.setChecked(false);
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        getLockAllIo();
    }

    private String wifiSSID;
    private String serialNumber;
    private String randomKey;
    private String openDoorKey;
    private String deviceName;
    private String deviceId1;
    private String batteryPower;
    private String bloothName;
    private String bloothMacaddress;
    private String deviceUserManager;
    private String version;

    @Override
    protected void initData() {
        String deviceId = getIntent().getStringExtra("ID");
        HttpManager.getInstance(this).getDevice(AppContext.getUSERID(), AppContext.getTOKEN(), deviceId, new HttpDataCallBack<DeviceBean>() {



            @Override
            public void httpDateCallback(DeviceBean deviceBean) {
                //版本号
                version = deviceBean.getMeta().getVersion();
                //管理员名称
                deviceUserManager = deviceBean.getData().getDevice().getDeviceUserManager();
                //蓝牙地址?
                bloothMacaddress = deviceBean.getData().getDevice().getDevice().getBloothMacaddress();
                //蓝牙名称?
                bloothName = deviceBean.getData().getDevice().getDevice().getBloothName();
                //电量
                batteryPower = deviceBean.getData().getDevice().getDevice().getBatteryPower();
                //获取到的门锁ID
                deviceId1 = deviceBean.getData().getDevice().getDevice().getDeviceId();
                //门锁名称
                deviceName = deviceBean.getData().getDevice().getDevice().getDeviceName();
                //开锁密钥?
                openDoorKey = deviceBean.getData().getDevice().getDevice().getOpenDoorKey();
                //随机数
                randomKey = deviceBean.getData().getDevice().getDevice().getRandomKey();
                //序列号
                serialNumber = deviceBean.getData().getDevice().getDevice().getSerialNumber();

                int status = deviceBean.getData().getDevice().getDevice().getStatus();
                //wifi名称
                wifiSSID = deviceBean.getData().getDevice().getDevice().getWifiSSID();
                SetData();
            }

            @Override
            public void httpException(HttpException e) {

            }

            @Override
            public void complet() {

            }
        });


//        mIrIoSwc.setOnClickListener(this);
//        mPwdIoSwc.setOnClickListener(this);
//        mSafeSwc.setOnClickListener(this);
//        mIrMoniterSwc.setOnClickListener(this);
//        mVideo.setOnClickListener(this);
//
//        mIrWarningSwc.setOnClickListener(this);
//        mAddAdminUserBt.setOnClickListener(this);
//        mAddNormalUserBt.setOnClickListener(this);
//        mAddTempUserBt.setOnClickListener(this);
//        mLockUsersBt.setOnClickListener(this);
//        mOpenLockBt.setOnClickListener(this);
//        mCheckAdminBt.setOnClickListener(this);
//        mDelUserBt.setOnClickListener(this);
//        dialog = new Dialog(this, R.style.Dialog);
//        dialog.setContentView(R.layout.dialog_loading_layout);

    }

    private void SetData() {
        mIrIoSwc.setText(deviceName);
        mPwdIoSwc.setText(version);
        mIrWarningSwc.setText(batteryPower);
        mIrMoniterSwc.setText(deviceUserManager);
        mSafeSwc.setText(serialNumber);
        mVideo.setText(bloothMacaddress);
        mVolumSizeTv.setText(deviceId1.substring(0,12));
        mIrTimeTv.setText(wifiSSID);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video:
//                setShowType(mVideo.isChecked() ? 1 : 2);
                break;
//            case R.id.addAdminUserBt:
//                addLockUser(1);
//                break;
//            case R.id.addNormalUserBt:
//                addLockUser(2);
//                break;
//            case R.id.addTempUserBt:
//                addLockUser(3);
//                break;
//            case R.id.LockUsersBt:
//                getLockUsers();
//                break;
            case R.id.irIoSwc:
//                setIrIo(mIrIoSwc.isChecked() ? 2 : 1);
                break;
            case R.id.pwdIoSwc:
//                setIrPwd(mPwdIoSwc.isChecked() ? 2 : 1);
                break;
            case R.id.irWarningSwc:
//                setIrWarning(mIrWarningSwc.isChecked() ? 2 : 1);
                break;
            case R.id.irMoniterSwc:
//                setIrMoniter(mIrMoniterSwc.isChecked() ? 2 : 1);
                break;
            case R.id.safeSwc:
//                setShowType(mSafeSwc.isChecked() ? 2 : 1);
                break;
            case R.id.volumRlt:
                showAudiosize();
                break;
            case R.id.irTimeRlt:
                showIrTime();
                break;
//            case R.id.openLockBt:
//                AlertDialog.Builder b = new AlertDialog.Builder(this);
//                b.setTitle("密码输入");
//                View v = inflater.inflate(R.layout.dialog_openlock, null);
//                final EditText et = (EditText) v.findViewById(R.id.pwdEt);
//                b.setView(v);
//                b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final String pwd = et.getText().toString();
//                        openLock(pwd);
//                    }
//                });
//                b.show();
//                break;
//            case R.id.checkAdminBt:
//                checkAdminUser();
//                break;
//            case R.id.delUserBt:
//                b = new AlertDialog.Builder(this);
//                b.setTitle("用户删除");
//                v = inflater.inflate(R.layout.dialog_openlock, null);
//                final EditText myet = (EditText) v.findViewById(R.id.pwdEt);
//                myet.setHint("请输入用户ID号");
//
//                b.setView(v);
//                b.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        final String userId = myet.getText().toString();
//                        if (userId.isEmpty()) {
//                            Toast.makeText(CommConfigActivity.this, "请输入用户ID号", Toast.LENGTH_SHORT).show();
//                            return;
//                        }
//                        delUser(Integer.valueOf(userId));
//                    }
//                });
//                b.show();
//                break;
        }
    }

    public void checkAdminUser() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        UdpManager.getinstance().verifyAdmin(new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                dialog.dismiss();
            }

            @Override
            public void erro(String msg) {
                dialog.dismiss();
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });
    }

    public void getLockAllIo() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        UdpManager.getinstance().getAllIo(new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                initAllIoUI((LockParameter) baseUdpBean);
                dialog.dismiss();
            }

            @Override
            public void erro(String msg) {
                dialog.dismiss();
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });
    }

    void initAllIoUI(LockParameter bean) {
//        mIrIoSwc.setChecked(bean.getIrIo() == 2 ? true : false);
//        mIrMoniterSwc.setChecked(bean.getIrMonitor() == 2 ? true : false);
//        mIrWarningSwc.setChecked(bean.getIrWarning() == 2 ? true : false);
//        mPwdIoSwc.setChecked(bean.getPwdMode() == 2 ? true : false);
//        mSafeSwc.setChecked(bean.getIrWarningType() == 2 ? true : false);
//        mIrTimeTv.setText(bean.getIrWarningTime() + "秒");
//        mIrTimeTv.setText(bean.getVolumeSize() + "级");
//        mVideo.setChecked(bean.getIrWarningType() == 1 ? true : false);


    }

    public void setIrIo(int size) {
        if (!dialog.isShowing()) {
            dialog.show();
        }
        UdpManager.getinstance().setIrIo(size, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                dialog.dismiss();
                getLockAllIo();
            }

            @Override
            public void erro(String msg) {
                dialog.dismiss();
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });
    }

    public void setIrPwd(int size) {
        dialog.show();
        UdpManager.getinstance().setPwdModel(size, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                getLockAllIo();
                dialog.dismiss();
            }

            @Override
            public void erro(String msg) {
                dialog.dismiss();
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });

    }

    public void setIrWarning(int size) {
        dialog.show();
        UdpManager.getinstance().setIrWarning(size, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                getLockAllIo();
                dialog.dismiss();
            }

            @Override
            public void erro(String msg) {
                dialog.dismiss();
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });

    }

    public void setIrMoniter(int size) {
        dialog.show();
        UdpManager.getinstance().setIrMonitor(size, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                dialog.dismiss();
                getLockAllIo();
            }

            @Override
            public void erro(String msg) {
                dialog.dismiss();
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });

    }

    public void setShowType(int size) {
        dialog.show();
        UdpManager.getinstance().setShowType(size, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                dialog.dismiss();
                getLockAllIo();
            }

            @Override
            public void erro(String msg) {
                dialog.dismiss();
            }

            @Override
            public void complet() {
                dialog.dismiss();
            }
        });
    }

    public void setVolumSize(int size) {
        dialog.show();
        UdpManager.getinstance().setLockVolum(size, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                getLockAllIo();
            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {
            }
        });
    }

    public void setWarningTime(int size) {
        UdpManager.getinstance().setIrWarningTime(size, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                getLockAllIo();
            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {
            }
        });
    }

    public void addLockUser(int key) {
        UdpManager.getinstance().addLockUser(key, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {
            }
        });

    }

    public void getLockUsers() {
        UdpManager.getinstance().queryLockUsers(new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {
            }
        });

    }

    public void openLock(String pwd) {

        UdpManager.getinstance().openLock(pwd, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                if (baseUdpBean.getErroCode() == 100) {
                    Toast.makeText(CommConfigActivity.this, "开门成功", Toast.LENGTH_SHORT).show();
//                    EventBus.getDefault().post(new CloseType(1));
//                    sendBroadcast(new Intent(BgService.LOGIN_ACTION));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent INTENT = new Intent(CommConfigActivity.this, MainActivity.class);
                            startActivity(INTENT);
                            finish();
                        }
                    }, 2000);

                } else {
                    Toast.makeText(CommConfigActivity.this, "开门失败", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {
            }
        });
    }

    public void delUser(int userId) {

        UdpManager.getinstance().deleteLockUser(userId, new CommInter<BaseUdpBean>() {
            @Override
            public void getDateComm(BaseUdpBean baseUdpBean) {
                Toast.makeText(CommConfigActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void erro(String msg) {
            }

            @Override
            public void complet() {
            }
        });
    }

    void showAudiosize() {
        str = new String[]{"1", "2", "3"};
        lv = (ListView) inflater.inflate(R.layout.transfer_layout,
                null);
        lv.setAdapter(new CustomAdapter());
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mDialog.dismiss();
                setVolumSize(Integer.valueOf(str[arg2]));
//                transfer(transferList.get(arg2).getUser().getId());
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(lv).setTitle("请选择声音大小");
        mDialog = builder.show();
    }

    void showIrTime() {
        str = new String[]{"60", "120", "180", "240"};
        lv = (ListView) inflater.inflate(R.layout.transfer_layout,
                null);
        lv.setAdapter(new CustomAdapter());
        lv.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                mDialog.dismiss();
                setWarningTime(Integer.valueOf(str[arg2]));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(lv).setTitle("请选择红外报警时长");
        mDialog = builder.show();
    }

    private String[] str;
    CustomAdapter mAdapter;
    ListView lv;
    private Dialog mDialog;

    public void initView() {
        mIrIoSwc = (TextView) findViewById(R.id.irIoSwc);
        mPwdIoSwc = (TextView) findViewById(R.id.pwdIoSwc);
        mIrWarningSwc = (TextView) findViewById(R.id.irWarningSwc);
        mIrMoniterSwc = (TextView) findViewById(R.id.irMoniterSwc);
        mSafeSwc = (TextView) findViewById(R.id.safeSwc);
        mVideo = (TextView) findViewById(R.id.video);
        mVolumSizeTv = (TextView) findViewById(R.id.volumSizeTv);
        mVolumRlt = (RelativeLayout) findViewById(R.id.volumRlt);
        mIrTimeTv = (TextView) findViewById(R.id.irTimeTv);
        mIrTimeRlt = (RelativeLayout) findViewById(R.id.irTimeRlt);
//        mAddAdminUserBt = (Button) findViewById(R.id.addAdminUserBt);
//        mAddNormalUserBt = (Button) findViewById(R.id.addNormalUserBt);
//        mAddTempUserBt = (Button) findViewById(R.id.addTempUserBt);
//        mLockUsersBt = (Button) findViewById(R.id.LockUsersBt);
//        mOpenLockBt = (Button) findViewById(R.id.openLockBt);
//        mCheckAdminBt = (Button) findViewById(R.id.checkAdminBt);
//        mDelUserBt = (Button) findViewById(R.id.delUserBt);
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return str.length;
        }

        @Override
        public Object getItem(int arg0) {
            return str[arg0];
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int arg0, View view, ViewGroup arg2) {
            // TODO Auto-generated method stub
            view = inflater.inflate(R.layout.transfer_item, null);
            ((TextView) view.findViewById(R.id.nameTv)).setText(str[arg0]);
            return view;
        }

    }
}
