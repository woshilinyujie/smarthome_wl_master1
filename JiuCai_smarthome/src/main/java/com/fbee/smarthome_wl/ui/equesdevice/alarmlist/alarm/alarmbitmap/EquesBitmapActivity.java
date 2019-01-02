package com.fbee.smarthome_wl.ui.equesdevice.alarmlist.alarm.alarmbitmap;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.EquesBitmapPageAdapter;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.utils.FileUtils;
import com.fbee.smarthome_wl.utils.HttpsURLConnectionHelp;
import com.fbee.smarthome_wl.utils.ImageLoader;
import com.fbee.smarthome_wl.utils.TransformUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class EquesBitmapActivity extends BaseActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private ProgressBar progressBar1;
    private ViewPager viewPager;
    private TextView picturecount;
    private ImageView alamss;
    private LinearLayout videoViewLinear;
    private VideoView videoView;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private String fid;
    private String bid;
    private int type;
    private Bitmap bitmap;
    private EquesBitmapPageAdapter adapter;
    private URL alarmurl;
    private MediaController mediaController;
    private String camPath;

    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private OutputStream out;
    private InputStream in;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_bitmap);
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            verifyStoragePermissions(this);
    }

    private List<Bitmap> bitmaps = new ArrayList();

    @Override
    protected void initView() {

        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);

        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        picturecount = (TextView) findViewById(R.id.picturecount);
        alamss = (ImageView) findViewById(R.id.alamss);
        videoViewLinear = (LinearLayout) findViewById(R.id.videoViewLinear);
        videoView = (VideoView) findViewById(R.id.videoView);
    }

    @Override
    protected void initData() {

        title.setText("图片详情");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (null == bundle)
            return;
        fid = bundle.getString("FID");
        bid = bundle.getString("BID");
        type = bundle.getInt("TYPE");
        camPath = Api.getCamPath();
        mediaController = new MediaController(this);
        adapter = new EquesBitmapPageAdapter();
        alarmurl = icvss.equesGetAlarmfileUrl(fid, bid);
        if (camPath != null) {
            File file = new File(camPath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }


        switch (type) {
            case 0:
                URL visitorurl = icvss.equesGetRingPicture(fid, bid);
                showSignlepic(visitorurl);
                break;
            //图片单张
            case 3:
                showSignlepic(alarmurl);
                break;
            //多张图片
            case 4:
                ondonet();
                break;
            //视频
            case 5:
                showVideo();
                break;
        }
    }

    private List<ImageView> imageViews = new ArrayList();

    @Override
    public void onClick(View v) {

    }

    public void ondonet() {
        Subscription sub = Observable.create(new Observable.OnSubscribe<List<Bitmap>>() {
            @Override
            public void call(Subscriber<? super List<Bitmap>> subscriber) {
                if (picturesPath() != null && FileUtils.isFolderExist(picturesPath())) {
                    final File zipFile = new File(picturesPath());
                    File[] listFile = zipFile.listFiles();
                    if (listFile.length > 0) {
                        for (int i = 0; i < listFile.length; i++) {
                            File f = listFile[i];
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = false;
                            options.inPreferredConfig = Bitmap.Config.RGB_565;
                            Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
                            bitmaps.add(bitmap);
                        }

                    } else {
                        requestPicZip();
                    }

                } else {
                    requestPicZip();
                }
                subscriber.onNext(bitmaps);
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<List<Bitmap>>defaultSchedulers())
                .subscribe(new Subscriber<List<Bitmap>>() {
                    @Override
                    public void onCompleted() {
//                        registerAndlogin(username,psw);
                    }

                    @Override
                    public void onError(Throwable e) {
//                        registerAndlogin(username,psw);
                    }

                    @Override
                    public void onNext(List<Bitmap> ret) {
                        if (bitmaps != null && bitmaps.size() > 0) {
                            for (int i = 0; i < bitmaps.size(); i++) {
                                ImageView imageView = new ImageView(EquesBitmapActivity.this);
                                imageView.setImageBitmap(bitmaps.get(i));
                                imageViews.add(imageView);
                            }
                            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                                @Override
                                public void onPageSelected(int arg0) {
                                    picturecount.setText((arg0 + 1) + "/" + bitmaps.size());
                                }

                                @Override
                                public void onPageScrolled(int arg0, float arg1, int arg2) {

                                }

                                @Override
                                public void onPageScrollStateChanged(int arg0) {

                                }
                            });
                            viewPager.setCurrentItem(0);
                            picturecount.setText("1/" + bitmaps.size());
                            viewPager.setAdapter(adapter);
                            adapter.addAll(imageViews);
                            viewPager.setVisibility(View.VISIBLE);
                            progressBar1.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(EquesBitmapActivity.this, "获取图片失败！请重试！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public String picturesPath() {
        return Api.getCamPath() + "Eques/" + fid.substring(0, fid.length() - 4);
    }


    private void showSignlepic(URL url) {

        ImageLoader.loadResourceReady(this, Uri.parse(url.toString()), R.mipmap.eques_alarm_error, new GlideDrawableImageViewTarget(alamss) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                progressBar1.setVisibility(View.GONE);
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                progressBar1.setVisibility(View.GONE);
            }
        });
    }

    String EQUES_PATH = "WangLi/Eques/";

    private void requestPicZip() {
        int downloadZipResult = downloadZip(alarmurl.toString(), camPath + fid);
        if (downloadZipResult == 1) {
            try {
                final File zipFile = new File(camPath + fid);
                upZipFile(zipFile, picturesPath());
                ArrayList<String> imagesPath = (ArrayList<String>) getImagePathFromSD(EQUES_PATH, fid.substring(0, fid.length() - 4));

                for (int i = 0; i < imagesPath.size(); i++) {
                    String image0Path = imagesPath.get(i);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    bitmap = BitmapFactory.decodeFile(image0Path, options);
                    bitmaps.add(bitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

            }
        }

    }

    public void upZipFile(File zipFile, String folderPath) throws IOException {
        try {
            ZipFile zf = null;
            zf = new ZipFile(zipFile);
            for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements(); ) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                // 后面插入判断是否为文件夹，如果是不处理(因为程序会根据你的路径自动去文件之上的文件夹)
                String name = entry.getName();
                if (name.endsWith(File.separator))
                    continue;
                in = zf.getInputStream(entry);
                String str = folderPath + File.separator + name;
                str = new String(str.getBytes("8859_1"), "GB2312");
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists() && !fileParentDir.isDirectory()) {
                        fileParentDir.mkdirs();
                    }
                    desFile.createNewFile();
                }
                out = new FileOutputStream(desFile);
                byte buffer[] = new byte[1204];
                int realLength;
                while ((realLength = in.read(buffer)) > 0) {
                    out.write(buffer, 0, realLength);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            in.close();
            out.close();
        }
    }

    public static ArrayList<String> getImagePathFromSD(String Equespath, String uid) {
        // 图片列表

        ArrayList<String> imagePathList = new ArrayList<String>();
        // 得到sd卡内image文件夹的路径 File.separator(/)
        // 得到该路径文件夹下所有的文件
        File fileAll = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath()
                        + "/" + Equespath + uid + File.separator);
        File[] files = fileAll.listFiles();
        if (files != null) {
            // 将所有的文件存入ArrayList中,并过滤所有图片格式的文件
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (checkIsImageFile(file.getPath())) {
                    imagePathList.add(file.getPath());
                }
            }
        }
        // 返回得到的图片列表
        return imagePathList;
    }

    /**
     * 检查扩展名，得到图片格式的文件
     *
     * @param fName 文件名
     * @return
     */

    private static boolean checkIsImageFile(String fName) {
        boolean isImageFile = false;
        // 获取扩展名
        String FileEnd = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length()).toLowerCase();
        if (FileEnd.equals("jpg") || FileEnd.equals("png")
                || FileEnd.equals("gif") || FileEnd.equals("jpeg")
                || FileEnd.equals("bmp")) {
            isImageFile = true;
        } else {
            isImageFile = false;
        }
        return isImageFile;
    }

    private int downloadZip(String url, String path) {
        int result = 0;
        InputStream is = null;
        OutputStream outputStream = null;
        try {
            is = HttpsURLConnectionHelp.requesByGetToStream(url);
            boolean tag = FileUtils.createFile(path);
            if (tag == true) {
                outputStream = new FileOutputStream(path);
                int byteCount = 0;

                byte[] bytes = new byte[1024];
                while ((byteCount = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, byteCount);
                }
                result = 1;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void showVideo() {
        Subscription sub = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (FileUtils.isFileExist(camPath)) {
                    camPath = Api.getCamPath() + fid.toString();
                } else {
                    int downloadVideoResult = downloadVideo(alarmurl.toString(), Api.getCamPath() + fid.toString());
                    camPath = Api.getCamPath() + fid.toString();
                }

                subscriber.onNext(camPath);
            }
        }).compose(TransformUtils.<String>defaultSchedulers())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String camPath) {
                        try {
                            picturecount.setVisibility(View.GONE);
                            alamss.setVisibility(View.GONE);
                            viewPager.setVisibility(View.GONE);
                            videoViewLinear.setVisibility(View.VISIBLE);
                            videoView.setVideoPath(camPath);
                            // 设置VideView与MediaController建立关联
                            videoView.setMediaController(mediaController);
                            // 设置MediaController与VideView建立关联
                            mediaController.setMediaPlayer(videoView);
                            //mediaController.setPadding(0,0,0,0);
                            // 让VideoView获取焦点
                            videoView.requestFocus();
                            // 开始播放
                            videoView.start();
                            progressBar1.setVisibility(View.GONE);
                        } catch (Exception e) {
                            Toast.makeText(EquesBitmapActivity.this, "获取视频失败！请重试！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private int downloadVideo(String url, String videoPath) {
        int result = 0;
        try {
            File desFile = new File(videoPath);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists() && !fileParentDir.isDirectory()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        InputStream is = null;
        OutputStream outputStream = null;
        try {
            is = HttpsURLConnectionHelp.requesByGetToStream(url);
            outputStream = new FileOutputStream(videoPath);
            int byteCount = 0;
            byte[] bytes = new byte[1024];
            if (is != null) {

                while ((byteCount = is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, byteCount);
                }
                result = 1;
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
