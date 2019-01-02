package com.example.wl.WangLiPro_v1.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;


/**
 *
 * Created by ZhaoLi.Wang on 2017/3/30 16:27
 */
public class SendCodeButton extends Button implements Runnable {

    /**
     * 总倒计时（秒）
     */
    private static final int MAX_TIME = 60;
    /**
     * 当前倒计时
     */
    private int time = 0;
    /**
     * 是否正在倒计时
     */
    private boolean isRunning = false;

    public SendCodeButton(Context context) {
        this(context, null);
    }

    public SendCodeButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SendCodeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void sendCode() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        time = MAX_TIME;
        post(this);
    }

    @Override
    public void run() {
        setText(time + "s");
        time--;
        if (time >= 0) {
            postDelayed(this, 1000);
        } else {
            isRunning = false;
            setText("重发验证码");
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
//
//    public void Destroy() {
//        removeCallbacks(this);
//    }
}
