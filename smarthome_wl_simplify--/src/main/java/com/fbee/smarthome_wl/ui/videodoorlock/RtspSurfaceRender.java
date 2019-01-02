package com.fbee.smarthome_wl.ui.videodoorlock;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.widget.Toast;

import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.vlcutil.BitmapUtils;
import com.fbee.vlcutil.RGBProgram;
import com.fbee.vlcutil.TaskUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;


public class RtspSurfaceRender implements GLSurfaceView.Renderer, RtspHelper.RtspCallback {

    private final Activity mycontext;
    private ByteBuffer mBuffer;

    private GLSurfaceView mGLSurfaceView;

    private RGBProgram mProgram;

    private String mRtspUrl;

    private String uid;

    private volatile boolean mCapturePending;
    private CaptureCallback mCaptureCallback;
    private int width_surface;
    private int height_surface;

    public RtspSurfaceRender(GLSurfaceView glSurfaceView, Activity context) {
        mGLSurfaceView = glSurfaceView;
        this.mycontext = context;
    }

    public void setRtspUrl(String url, String fileurl) {
        mRtspUrl = url;
        uid = fileurl;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        width_surface = width;
        height_surface = height;
        mProgram = new RGBProgram(mGLSurfaceView.getContext(), width, height);
        mBuffer = ByteBuffer.allocateDirect(width * height * 4).order(ByteOrder.nativeOrder());
        RtspHelper.getInstance().createPlayer(mRtspUrl, width, height, this);
    }

    public void onSurfaceDestoryed() {
        RtspHelper.getInstance().releasePlayer();
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(1f, 1f, 1f, 1f);

        mProgram.useProgram();
        mProgram.setUniforms(mBuffer.array());
        mProgram.draw();

        try {
            if (RtspPlayerActivity.printOptionEnable) {
                RtspPlayerActivity.printOptionEnable = false;
                int w = width_surface;
                int h = height_surface;


                int b[] = new int[(int) (w * h)];
                int bt[] = new int[(int) (w * h)];
                IntBuffer buffer = IntBuffer.wrap(b);
                buffer.position(0);
                GLES20.glReadPixels(0, 0, w, h, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buffer);
                for (int i = 0; i < h; i++) {
                    //remember, that OpenGL bitmap is incompatible with Android bitmap
                    //and so, some correction need.
                    for (int j = 0; j < w; j++) {
                        int pix = b[i * w + j];
                        int pb = (pix >> 16) & 0xff;
                        int pr = (pix << 16) & 0x00ff0000;
                        int pix1 = (pix & 0xff00ff00) | pr | pb;
                        bt[(h - i - 1) * w + j] = pix1;
                    }
                }
                Bitmap inBitmap = null;
                if (inBitmap == null || !inBitmap.isMutable()
                        || inBitmap.getWidth() != w || inBitmap.getHeight() != h) {
                    inBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                }
                //Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                inBitmap.copyPixelsFromBuffer(buffer);
                //return inBitmap ;
                // return Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);
                inBitmap = Bitmap.createBitmap(bt, w, h, Bitmap.Config.ARGB_8888);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                inBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream fis = new ByteArrayInputStream(bitmapdata);

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = df.format(new Date());
                String myfile = format + ".jpg";

                File dir_image = new File(uid);
                dir_image.mkdirs();

                try {
                    final File tmpFile = new File(dir_image, myfile);
                    FileOutputStream fos = new FileOutputStream(tmpFile);

                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = fis.read(buf)) > 0) {
                        fos.write(buf, 0, len);
                    }
                    mycontext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mycontext, "地址" + tmpFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    fis.close();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//        glClearColor(1f, 1f, 1f, 1f);
//
//        mProgram.useProgram();
//        mProgram.setUniforms(mBuffer.array());
//        mProgram.draw();
    }


    @Override
    public void onPreviewFrame(ByteBuffer buffer, int width, int height) {
        mBuffer.rewind();
        buffer.rewind();
        mBuffer.put(buffer);

        if (mCapturePending) {
            mCapturePending = false;
            onCapture(width, height);
        }

        mGLSurfaceView.requestRender();
    }

    private void onCapture(int width, int height) {
        final Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mBuffer.rewind();
        bmp.copyPixelsFromBuffer(mBuffer);
        TaskUtils.execute(new Runnable() {
            @Override
            public void run() {
//                File file = ImageUtils.getNewImageFile();
                File dir = new File(uid);
                if (!dir.exists() && !dir.mkdirs()) {
                    return;
                }
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String format = df.format(new Date());
                File file = new File(dir, format + ".jpg");
                if (file.exists() && file.isFile()) {
                    file.delete();
                }

                Bitmap resultbmp = adjustPhotoRotation(bmp, 90);

                BitmapUtils.saveBitmap(bmp, file);
                BitmapUtils.recycle(resultbmp);
                BitmapUtils.recycle(bmp);
                mCaptureCallback.onCapture(file);
//                mCaptureCallback = null;
            }
        });
    }


    private Bitmap adjustPhotoRotation(Bitmap bitmap, int orientationDegree) {

        Matrix matrix = new Matrix();
        matrix.setRotate(orientationDegree, (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bitmap.getHeight();
            targetY = 0;
        } else {
            targetX = bitmap.getHeight();
            targetY = bitmap.getWidth();
        }


        final float[] values = new float[9];
        matrix.getValues(values);
        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        matrix.postTranslate(targetX - x1, targetY - y1);

        Bitmap canvasBitmap = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getWidth(),
                Bitmap.Config.ARGB_8888);

        Paint paint = new Paint();
        Canvas canvas = new Canvas(canvasBitmap);
        canvas.drawBitmap(bitmap, matrix, paint);

        return canvasBitmap;
    }


    public interface CaptureCallback {
        void onCapture(File path);
    }

    public void capture(CaptureCallback callback) {
        mCaptureCallback = callback;
        mCapturePending = true;
    }
}
