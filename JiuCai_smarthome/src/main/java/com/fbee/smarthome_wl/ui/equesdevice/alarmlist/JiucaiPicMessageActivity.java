package com.fbee.smarthome_wl.ui.equesdevice.alarmlist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.BitmapEntity;
import com.fbee.smarthome_wl.ui.equesdevice.alarmlist.alarm.alarmbitmap.JiucaiImageActivity;
import com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.JiucaiLocalVideoActivity;
import com.fbee.smarthome_wl.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.JiucaiLocalVideoActivity.getFileName;


//File baseFile = new File("/storage/sdcard1/DingDong/e20d8a7ecb66454888bd75eff5d5ef4c/");

/***
 * 抓拍图片
 */
public class JiucaiPicMessageActivity extends BaseActivity {
    private ListView listview;
    private ImageView back;
    private int allChoseTag;
    private TextView top_tv_name;
    private String path;
    private TextView rightMenu;
    private Button deleteChose;
    private LinearLayout botoomLayout;
    private Button allChose;
    private BitmapEntity bitmapEntity;
    private ArrayList<BitmapEntity> bitmaps;
    private Adapter picAdapter;
    private Date date;
    private ArrayList<BitmapEntity> deleteBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_photoalbum);
    }

    @Override
    protected void initView() {
        bitmaps = new ArrayList<>();
        deleteBitmap = new ArrayList<>();
        listview = (ListView) findViewById(R.id.album_listview);
        top_tv_name = (TextView) findViewById(R.id.title);
        top_tv_name.setText("抓拍信息");
        deleteChose = (Button) findViewById(R.id.delete);
        botoomLayout = (LinearLayout) findViewById(R.id.botoomLayout);
        allChose = (Button) findViewById(R.id.allChose);
        back = (ImageView) findViewById(R.id.back);
        rightMenu = (TextView) findViewById(R.id.tv_right_menu);
        rightMenu.setText("编辑");
        rightMenu.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        path = getIntent().getStringExtra("path");
        if (path != null) {
            recursive(path);
        }
    }

    private void recursive(String path) {
        File dir = new File(path);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null && files.length > 0) {
            for (int i = 0; i < files.length; i++) {
                String fileNames = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    recursive(files[i].getAbsolutePath()); // 获取文件绝对路径
                } else if (fileNames.endsWith(".jpg")) { // 判断文件名是否以.jpg结尾
                    String strFileName = files[i].getAbsolutePath();
                    //文件创建时间
                    date = new Date(files[i].lastModified());
                    String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            .format(date);
                    //文件名称
                    String fileName = getFileName(strFileName);
                    bitmapEntity = new BitmapEntity(null, fileNames, time, strFileName);
                    bitmaps.add(bitmapEntity);
                } else {
                    continue;
                }
            }
        } else {
            showToast("暂无数据");
        }
    }

    public void initData() {
        back.setOnClickListener(this);
        rightMenu.setOnClickListener(this);
        allChose.setOnClickListener(this);
        deleteChose.setOnClickListener(this);
        picAdapter = new Adapter(this, bitmaps);
        listview.setAdapter(picAdapter);
        listview.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (botoomLayout.getVisibility() == View.VISIBLE) {
                    boolean isSelect = picAdapter.getisSelectedAt(position);
                    if (!isSelect) {
                        picAdapter.setItemisSelectedMap(position, true);
                        deleteBitmap.add(bitmaps.get(position));
                    } else {
                        deleteBitmap.remove(bitmaps.get(position));
                        picAdapter.setItemisSelectedMap(position, false);
                    }
                } else {
                    Intent intent = new Intent(JiucaiPicMessageActivity.this, JiucaiImageActivity.class);
                    intent.putExtra("path", bitmaps.get(position).getFilePath());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_right_menu:
                if (bitmaps == null || bitmaps.size() == 0) {
                    return;
                }
                if (botoomLayout.getVisibility() == View.VISIBLE) {
                    botoomLayout.setVisibility(View.GONE);
                    picAdapter.setCheckBoxisShow(false);
                    for (int i = 0; i < bitmaps.size(); i++) {
                        picAdapter.setItemisSelectedMap(i, false);
                    }
                    deleteBitmap.clear();
                } else {
                    botoomLayout.setVisibility(View.VISIBLE);
                    picAdapter.setCheckBoxisShow(true);
                }

                break;
            case R.id.allChose:
                for (int i = 0; i < bitmaps.size(); i++) {
                    picAdapter.setItemisSelectedMap(i, true);
                    deleteBitmap.add(bitmaps.get(i));
                }
                break;
            case R.id.delete:
                for (BitmapEntity bitmapEntity : deleteBitmap) {
                    deleteFromSD(bitmapEntity.getFilePath());
                }
                bitmaps.removeAll(deleteBitmap);
                botoomLayout.setVisibility(View.GONE);
                deleteBitmap.clear();
                picAdapter = new Adapter(this, bitmaps);
                listview.setAdapter(picAdapter);
                break;
            case R.id.back:
                finish();
                break;
        }

    }

    private void deleteFromSD(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }

    }

    public class Adapter extends BaseAdapter {

        private final ArrayList<BitmapEntity> bitmapArrayList;
        private ViewHolder holder;
        private boolean isShow;
        private HashMap<Integer, Boolean> isSelectedMap;

        public Adapter(Context context, ArrayList<BitmapEntity> bitmapArrayList) {
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
            holder.iv.setImageBitmap(BitmapFactory.decodeFile(bitmapArrayList.get(position).getFilePath()));
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
