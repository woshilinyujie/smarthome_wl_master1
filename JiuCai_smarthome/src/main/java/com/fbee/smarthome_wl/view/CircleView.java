package com.fbee.smarthome_wl.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.fbee.smarthome_wl.utils.AppUtil;


/**
 * @class name：com.fbee.smarthome_wl.view
 * @anthor create by Zhaoli.Wang
 * @time 2017/4/13 16:53
 */
public class CircleView extends View {
    Paint p;
    Context context;
    public CircleView(Context context) {
        super(context);
        this.context = context;
        // 创建画笔
        p = new Paint();
        p.setColor(Color.RED);// 设置红色
        p.setAntiAlias(true);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        // 创建画笔
        p = new Paint();
        p.setColor(Color.RED);// 设置红色
        p.setAntiAlias(true);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2,getHeight()/2, AppUtil.dp2px(context,10),p);
    }


    public  void  setColor(int color){
        p.setColor(color);
        invalidate();
    }


}
