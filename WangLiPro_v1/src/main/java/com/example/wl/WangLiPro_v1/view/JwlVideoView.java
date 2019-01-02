package com.example.wl.WangLiPro_v1.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.jwlkj.idc.jni.JwlJni;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Video Play Window
 */
public class JwlVideoView extends SurfaceView implements Callback, Runnable {

    public static boolean runBoo = false;

    public static int frame_width = 640;
    public static int frame_hight = 480;
    public static int COUNT = frame_width * frame_hight * 2;
    public static int keysize = 2;

    private Bitmap bitmap, dstbmp;
    private Paint p;
    private ByteBuffer buffer;
    private Rect playRect = null;
    Timer timer = null;
    TimerTask taskHeart = null;
    private Matrix matrix;
    private static JwlVideoView vv;
    public boolean loadImgBoo = false;

    /**
     * ===============================
     */
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    public static ConcurrentLinkedQueue<byte[]> list = new ConcurrentLinkedQueue<byte[]>();
    /**
     * ===============================
     */

    public static Handler videoHander;

    @SuppressWarnings("unused")
    public JwlVideoView(Context context) {
        this(context, null);
    }

    public void setLoadBoo(boolean boo) {
        this.loadImgBoo = boo;
    }

    public JwlVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        playRect = new Rect();
        vv = this;
        mHolder = getHolder();
        mHolder.addCallback(this);
        list.clear();
    }

    public void play() {
        matrix = new Matrix();
        matrix.postRotate(90);
        bitmap = Bitmap.createBitmap(frame_width, frame_hight,
                Bitmap.Config.RGB_565);
        runBoo = true;
        new Thread(this).start();

    }


    @Override
    public void destroyDrawingCache() {
        timer = null;
        taskHeart = null;
        videoHander = null;
        super.destroyDrawingCache();
    }





    private Object object = new Object();

    public void addData(byte[] data) {
        list.add(data);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        synchronized (object) {
            object.notify();
        }
        runBoo = false;

    }

    @Override
    public void run() {
        byte[] datas = new byte[COUNT];
        byte[] data = null;
        while (runBoo) {


            if (!list.isEmpty()) {
                data = list.poll();
                if (data == null) {
                    return;
                }
                JwlJni.decodeH264(data, data.length, datas);
                buffer = ByteBuffer.wrap(datas);
                bitmap.copyPixelsFromBuffer(buffer);
                dstbmp = Bitmap
                        .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                bitmap.getHeight(), matrix, true);
                playRect.set(0, 0, getWidth(), getHeight());
                try {
                    mCanvas = mHolder.lockCanvas();
                    if (mCanvas != null) {
                        // mCanvas.drawColor(Color.BLACK);
                        mCanvas.drawBitmap(dstbmp, null, playRect, p);
                        if (loadImgBoo) {
                            loadImgBoo = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (mCanvas != null)
                        mHolder.unlockCanvasAndPost(mCanvas);
                }
            }

        }
        if (mHolder != null) {
            mHolder.removeCallback(this);
            mHolder = null;
            bitmap = null;
            dstbmp = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }


}
