package com.fbee.smarthome_wl.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.response.QueryDeviceUserResponse;
import com.fbee.smarthome_wl.utils.Aes256EncodeUtil;
import com.fbee.smarthome_wl.utils.ByteStringUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.ToastUtils;

import static com.fbee.smarthome_wl.utils.ByteStringUtil.hexStringToBytes;
import static com.fbee.smarthome_wl.utils.ByteStringUtil.string2HexString;

/**
 * @class name：com.fbee.smarthome_wl.widget.dialog
 * @anthor create by Zhaoli.Wang
 * @time 2017/9/27 10:14
 */
public class DialogInputPsw extends Dialog {
    private TextView tvTitleWifidialog;
    private EditText etFirst;
    private EditText etSecond;
    private TextView tvLeftCancelBtnWifidialog;
    private TextView tvRightConfirmBtnWifidialog;
    private QueryDeviceUserResponse deviceInfo;
    private String uuid;
    private DialogListener listener;
    public DialogInputPsw(Context context,QueryDeviceUserResponse deviceInfo,String uuid,DialogListener listener) {
        super(context, R.style.MyDialog);
        this.uuid = uuid;
        this.listener = listener;
        this.deviceInfo = deviceInfo;
    }

    protected DialogInputPsw(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public DialogInputPsw(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_password);

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.mypopwindow_anim_style);
        window.getDecorView().setPadding(0,0,0,0);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        setCanceledOnTouchOutside(false);

        tvTitleWifidialog = (TextView) findViewById(R.id.tv_title_wifidialog);
        etFirst = (EditText) findViewById(R.id.et_first);
        etSecond = (EditText) findViewById(R.id.et_second);
        tvLeftCancelBtnWifidialog = (TextView) findViewById(R.id.tv_left_cancel_btn_wifidialog);
        tvRightConfirmBtnWifidialog = (TextView) findViewById(R.id.tv_right_confirm_btn_wifidialog);

        tvTitleWifidialog.setText("请输入密码");
        //单人
        if(deviceInfo.getBody().getAuth_mode().equals("0")) {
            etSecond.setVisibility(View.GONE);
        }
        //双人
        else{
            etSecond.setVisibility(View.VISIBLE);
        }




        tvLeftCancelBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });


        tvRightConfirmBtnWifidialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String fristpsw = etFirst.getText().toString().trim();
               String secondpsw = etSecond.getText().toString().trim();
                //单人
                if(deviceInfo.getBody().getAuth_mode().equals("0")) {
                    if(fristpsw.length() ==6){
                        String passStr = ByteStringUtil.myStringToString(fristpsw);
                        final byte[] passwd = ByteStringUtil.hexStringToBytes(passStr);
                        byte[] key = aes256VideoDoorKey();
                        if (key == null) return;
                        byte[] aesPas = aes256VideoDoorPass(passwd, key);
                        String aespasStr = ByteStringUtil.bytesToHexString(aesPas);
                        if(null !=listener )
                            listener.onRightClick(aespasStr);
                    }else{
                        ToastUtils.showShort("输入密码格式不正确");
                    }
                }else{
                    if(fristpsw.length() ==6 && secondpsw.length() ==6){
                        String passStr = ByteStringUtil.myStringToString(fristpsw);
                        String passStr02 = ByteStringUtil.myStringToString(secondpsw);
                        String result = passStr + "2c" + passStr02;
                        final byte[] passwd = ByteStringUtil.hexStringToBytes(result);
                        byte[] key = aes256VideoDoorKey();
                        if (key == null) return;
                        byte[] aesPas = aes256VideoDoorPass(passwd, key);
                        String aespasStr = ByteStringUtil.bytesToHexString(aesPas);
                        if(null !=listener )
                            listener.onRightClick(aespasStr);

                    }else{
                        ToastUtils.showShort("输入密码格式不正确");
                    }

                }
                cancel();
            }
        });

    }

    public interface DialogListener {

        void onRightClick(String psw);

    }



    private byte[] aes256VideoDoorKey(){
        byte[] aes=null;
        if(deviceInfo!=null&&deviceInfo.getBody()!=null&&deviceInfo.getBody().getNetwork_ssid()!=null){

            String lockSSID=deviceInfo.getBody().getNetwork_ssid();
//            String version=deviceInfo.getBody().getVersion();
            // String lockSSID="jija-yanfa";
            // String version="1.0.0-1.0.0";
            String content=uuid+lockSSID;
            byte[] contentByte;
            if(content.length() <32){
                for (int i = content.length(); i <32 ; i++) {
                    content =content+"0";
                }
            }
            String content16Str= string2HexString(content);

            LogUtil.e("加密","第一次加密内容："+content16Str);
            contentByte = hexStringToBytes(content16Str);
            int len=contentByte.length;
            byte[] doorPsd = new byte[32];
            //拼接加密前明文
            if(len>32){
                System.arraycopy(contentByte, 0, doorPsd, 0, 32);
            }else {
                System.arraycopy(contentByte, 0, doorPsd, 0, len);
            }


            //加密秘钥
            String key="WONLYAPPOPENSMARTLOCKKEY@@@@2017";
            String key16Str= string2HexString(key);
            LogUtil.e("加密","第一次加密秘钥："+key16Str);
            byte[] keyByte= hexStringToBytes(key16Str);

            try{
                aes= Aes256EncodeUtil.encrypt(doorPsd, keyByte);
            }catch (Exception e){
            }
        }else{

        }
        return aes;
    }


    private byte[] aes256VideoDoorPass(byte[] pas,byte[] key){

        byte[] doorPsd = new byte[32];
        int pasLen=pas.length;
        long time=System.currentTimeMillis()/1000;
        String time16Str=Long.toHexString(time);
        byte[] timeByte=ByteStringUtil.hexStringToBytes(time16Str);
        //拼接加密前明文
        System.arraycopy(pas, 0, doorPsd, 0, pasLen);
        System.arraycopy(timeByte, 0, doorPsd, 28, 4);
        LogUtil.e("加密","第二次加密内容："+ByteStringUtil.bytesToHexString(doorPsd).toUpperCase());
        byte[] aes=null;
        try{
            aes= Aes256EncodeUtil.encrypt(doorPsd, key);
        }catch (Exception e){
        }
        return aes;
    }



}
