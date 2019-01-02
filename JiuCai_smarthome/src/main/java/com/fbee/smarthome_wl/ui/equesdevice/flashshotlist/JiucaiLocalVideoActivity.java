package com.fbee.smarthome_wl.ui.equesdevice.flashshotlist;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.BitmapEntity;
import com.fbee.smarthome_wl.bean.EquesAlarmInfo;
import com.fbee.smarthome_wl.bean.SeleteEquesDeviceInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

import static com.bairuitech.anychat.R.string.back;


public class JiucaiLocalVideoActivity extends BaseActivity {

    private static String absolutePath;
    private String path;
    private ListView listview;
    private ArrayList<BitmapEntity> bitmaps;
    private LinearLayout botoomLayout;
    private VideoAdapter videoAdapter;
    //记录被选中过的item
    private List<BitmapEntity> contactSelectedList = new ArrayList<>();
    private Button delete;
    private Button allChose;
    private BitmapEntity bitmapEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_local_video);
    }

    public void initData() {
        botoomLayout.setOnClickListener(this);
        allChose.setOnClickListener(this);
        delete.setOnClickListener(this);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (botoomLayout.getVisibility() == View.VISIBLE) {
                    boolean isSelect = videoAdapter.getisSelectedAt(position);
                    if (!isSelect) {
                        //当前为被选中，记录下来，用于删除
                        contactSelectedList.add(bitmaps.get(position));
                        if (contactSelectedList.size() == bitmaps.size() && contactSelectedList.size() != 0) {
//                            allChose.setText("取消全选");
//                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.selected));
                        } else {
//                            allChose.setText("全选");
//                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                        }
                    } else {
                        contactSelectedList.remove(bitmaps.get(position));
//                        allChose.setText("全选");
//                        choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
//                        choseNumber.setText("(" + contactSelectedList.size() + ")");
                    }
                    //选中状态的切换
                    videoAdapter.setItemisSelectedMap(position, !isSelect);
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String type = "video/mp4";
                    Uri uri = Uri.parse("file://" + bitmaps.get(position).getFilePath());
                    intent.setDataAndType(uri, type);
                    startActivity(intent);
                }
            }
        });
        Observable.create(new Observable.OnSubscribe<ArrayList<BitmapEntity>>() {
            @Override
            public void call(Subscriber<? super ArrayList<BitmapEntity>> subscriber) {
                recursive(path);
                subscriber.onNext(bitmaps);
            }
        }).compose(TransformUtils.<ArrayList<BitmapEntity>>defaultSchedulers())
                .subscribe(new Action1<ArrayList<BitmapEntity>>() {
                               @Override
                               public void call(ArrayList<BitmapEntity> bitmapEntityArrayList) {
                                   hideLoadingDialog();
                                   if (bitmapEntityArrayList == null || bitmapEntityArrayList.size() == 0) {
                                       showToast("暂无数据");
                                       return;
                                   }
                                   videoAdapter = new VideoAdapter(JiucaiLocalVideoActivity.this, bitmapEntityArrayList);
                                   listview.setAdapter(videoAdapter);
                               }
                           }
                );
    }

    public void initView() {
        showLoadingDialog("");
        bitmaps = new ArrayList<BitmapEntity>();
        path = getIntent().getStringExtra("path");
        TextView rightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rightMenu.setVisibility(View.VISIBLE);
        rightMenu.setText("编辑");
        rightMenu.setOnClickListener(this);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("抓拍视频");
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        listview = (ListView) findViewById(R.id.album_listview);
        botoomLayout = (LinearLayout) findViewById(R.id.botoomLayout);
        delete = (Button) findViewById(R.id.delete);
        allChose = (Button) findViewById(R.id.allChose);
    }

    private void recursive(String path) {

        File dir = new File(path);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                String fileNames = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    recursive(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else if (fileNames.endsWith(".mp4")) { // 判断文件名是否以.mp4结尾
                    //文件地址
                    String strFileName = files[i].getAbsolutePath();

                    //文件名称
                    String fileName = getFileName(strFileName);
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(strFileName);
                    //文件时长
                    String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    long parseLong = Long.parseLong(duration);
                    int time = (int) (parseLong / 1000);
                    String times = Api.secToTime(time);
                    Bitmap videoThumbnail = getVideoThumbnail(strFileName);
                    bitmapEntity = new BitmapEntity(videoThumbnail, fileName, times, strFileName);
                    bitmaps.add(bitmapEntity);
                } else {
                    continue;
                }
            }
        }
    }

    public static String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }

    }

    public static Bitmap getVideoThumbnail(String url) {
        File file = new File(url);
        Bitmap bitmap = null;
        //MediaMetadataRetriever 是android中定义好的一个类，提供了统一
        //的接口，用于从输入的媒体文件中取得帧和元数据；
        absolutePath = file.getAbsolutePath();
        bitmap = ThumbnailUtils.createVideoThumbnail(absolutePath, MediaStore.Video.Thumbnails.MICRO_KIND);
//        MediaMetadataRetriever media = new MediaMetadataRetriever();
//        media.setDataSource(url);
//
//        bitmap = media.getFrameAtTime();
//        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//        try {
//            //根据文件路径获取缩略图
//            retriever.setDataSource(url, new HashMap());
//            //获得第一帧图片
//            bitmap = retriever.getFrameAtTime();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } finally {
//            retriever.release();
//        }
        return bitmap;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_menu:
                if (bitmaps == null || bitmaps.size() == 0) {
                    return;
                }
                if (botoomLayout.getVisibility() == View.GONE) {
                    for (int i = 0; i < bitmaps.size(); i++) {
                        videoAdapter.setItemisSelectedMap(i, false);
                    }
                    contactSelectedList.clear();
                    botoomLayout.setVisibility(View.VISIBLE);
                    videoAdapter.setCheckBoxisShow(true);
                } else {
                    botoomLayout.setVisibility(View.GONE);
                    videoAdapter.setCheckBoxisShow(false);
                }
                break;
            case R.id.allChose:
                for (int i = 0; i < bitmaps.size(); i++) {
                    contactSelectedList.add(bitmaps.get(i));
                    videoAdapter.setItemisSelectedMap(i, true);
                }
                break;
            case R.id.delete:
                for (BitmapEntity delepath : contactSelectedList) {
                    delFile(delepath.getFilePath());
                }
                bitmaps.removeAll(contactSelectedList);
                contactSelectedList.clear();
                videoAdapter = new VideoAdapter(this, bitmaps);
                listview.setAdapter(videoAdapter);
                botoomLayout.setVisibility(View.GONE);
                break;
            case R.id.back:
                finish();
                break;
        }

    }

    public static void delFile(String fileName) {
        File file = new File(fileName);
        if (file.isFile()) {
            file.delete();
        }
        file.exists();
    }

    public class VideoAdapter extends BaseAdapter {

        private final ArrayList<BitmapEntity> bitmapArrayList;
        private ViewHolder holder;
        private boolean isShow;
        private HashMap<Integer, Boolean> isSelectedMap;

        public VideoAdapter(Context context, ArrayList<BitmapEntity> bitmapArrayList) {
            this.bitmapArrayList = bitmapArrayList;
            isSelectedMap = new HashMap<Integer, Boolean>();
        }

        @Override
        public int getCount() {
            return bitmapArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        public void setCheckBoxisShow(boolean isShow) {
            this.isShow = isShow;
            notifyDataSetChanged();
        }

        public boolean getisSelectedAt(int position) {

            //如果当前位置的key值为空，则表示该item未被选择过，返回false，否则返回true

            if (isSelectedMap.get(position) != null) {
                return isSelectedMap.get(position);
            }
            return false;
        }

        public void setItemisSelectedMap(int position, boolean isSelectedMap) {
            this.isSelectedMap.put(position, isSelectedMap);
            notifyDataSetChanged();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_eques_visitor_list, null);
                holder = new ViewHolder();
                holder.iv = (ImageView) convertView.findViewById(R.id.iv);
                holder.title = (TextView) convertView.findViewById(R.id.title);
                holder.summary = (TextView) convertView.findViewById(R.id.summary);
                holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (isShow) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.GONE);
            }
            holder.title.setText(bitmapArrayList.get(position).getFilename());
            holder.summary.setText(bitmapArrayList.get(position).getVideosize());
            holder.iv.setImageBitmap(bitmapArrayList.get(position).getBitmap());
            holder.checkBox.setChecked(getisSelectedAt(position));
            return convertView;
        }

        public class ViewHolder {
            public CheckBox delete;
            public ImageView iv;
            public TextView title;
            public TextView summary;

            public CheckBox checkBox;
        }
    }

}
