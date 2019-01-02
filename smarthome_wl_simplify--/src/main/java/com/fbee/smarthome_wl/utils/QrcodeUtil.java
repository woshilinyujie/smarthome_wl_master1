package com.fbee.smarthome_wl.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

import static android.R.attr.path;

/**
 * @class name：com.fbee.smarthome_wl.utils
 * @anthor create by Zhaoli.Wang
 * @time 2017/7/6 16:28
 */
public class QrcodeUtil {

    /**
     * 生成二维码图片
     * @param url
     * @param width
     * @param height
     * @return
     */
    public static Bitmap createQRImage(String url ,String name,String str,int width,int height,Context context)
    {
        try
        {
            //判断URL合法性
            if (url == null || "".equals(url) || url.length() < 1)
            {
                return null;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换
            BitMatrix bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            //下面这里按照二维码的算法，逐个生成二维码的图片，
            //两个for循环是图片横列扫描的结果
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    if (bitMatrix.get(x, y))
                    {
                        pixels[y * width + x] = 0xff000000;
                    }
                    else
                    {
                        pixels[y * width + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            Canvas canvas = new Canvas(bitmap);
            canvas.drawBitmap(bitmap, 0,0, null);
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(AppUtil.dp2px(context,16));
            float tX = ( bitmap.getWidth() - getFontlength(textPaint, name))/2;
            canvas.drawText(name,tX,AppUtil.dp2px(context,15),textPaint);
            textPaint.setTextSize(AppUtil.dp2px(context,14));
            StaticLayout sl= new StaticLayout(str, textPaint, bitmap.getWidth()-8, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            canvas.translate(0, bitmap.getHeight()-AppUtil.dp2px(context,20));
            sl.draw(canvas);

            return bitmap;
        }
        catch (WriterException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return 返回指定笔和指定字符串的长度
     */
    public static float getFontlength(Paint paint, String str) {
        if(null ==str)
            return 0;
        return paint.measureText(str);
    }

    public static boolean  saveImageToGallery(Context context, Bitmap bmp,String name) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "WL_GATEWAY");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
        return true;
    }


}
