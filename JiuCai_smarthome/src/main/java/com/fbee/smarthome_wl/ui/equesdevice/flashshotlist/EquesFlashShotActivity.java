package com.fbee.smarthome_wl.ui.equesdevice.flashshotlist;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eques.icvss.utils.Method;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.FlashShotAdapter;
import com.fbee.smarthome_wl.adapter.VisitorAdapter;
import com.fbee.smarthome_wl.api.Api;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.EquesVisitorInfo;
import com.fbee.smarthome_wl.ui.equesdevice.flashshotlist.sdpicture.SDpictureActivity;
import com.fbee.smarthome_wl.ui.equesdevice.visitorlist.EquesVisitorActivity;
import com.fbee.smarthome_wl.ui.login.LoginActivity;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.LogUtil;
import com.swipetoloadlayout.SwipeToLoadLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.fbee.smarthome_wl.R.id.allChose;
import static com.fbee.smarthome_wl.R.id.choseImage;

public class EquesFlashShotActivity extends BaseActivity {

    private ArrayList<String> imagePathFromSD;
    private ListView lvBitmaps;
    private String equespath;
    private ProgressBar progressBar1;
    private Bundle bundle;
    private String name;
    private LinearLayout botoomLayout;
    private FlashShotAdapter adapter;
    private ArrayList<String> contactSelectedList;
    private ArrayList<String> visitors;
    private String ringsEntity;
    int falg = 0;
    private LinearLayout choseAll;
    private TextView choseNumber;
    private TextView allChose;
    private ImageView choseImage;
    private boolean bool = false;
    private TextView tvRightMenu;
    private LinearLayout delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eques_alarm);
    }

    @Override
    protected void initView() {
        lvBitmaps = (ListView) findViewById(R.id.lv_bitmap);
        lvBitmaps.setVisibility(View.VISIBLE);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        botoomLayout = (LinearLayout) findViewById(R.id.botoomLayout);
        TextView title = (TextView) findViewById(R.id.title);
        choseAll = (LinearLayout) findViewById(R.id.choseall);
        choseNumber = (TextView) findViewById(R.id.choseNumber);
        allChose = (TextView) findViewById(R.id.allChose);
        choseImage = (ImageView) findViewById(R.id.choseImage);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        ImageView back = (ImageView) findViewById(R.id.back);
        LinearLayout delete = (LinearLayout) findViewById(R.id.delete);
        SwipeToLoadLayout viewById = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        back.setVisibility(View.VISIBLE);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("编辑");
        tvRightMenu.setOnClickListener(this);
        delete.setOnClickListener(this);
        back.setOnClickListener(this);
        choseAll.setOnClickListener(this);
        title.setText("抓拍影像");
        viewById.setVisibility(View.GONE);

    }

    @Override
    protected void initData() {
        visitors = new ArrayList<>();
        contactSelectedList = new ArrayList<>();
        name = getIntent().getExtras().getString(Method.ATTR_BUDDY_NICK);
        String uid = getIntent().getExtras().getString(Method.ATTR_BUDDY_UID);
        equespath = Api.getCamPath() + uid;
        imagePathFromSD = AppUtil.getImagePathFromSD(equespath);
        visitors.addAll(imagePathFromSD);
        adapter = new FlashShotAdapter(EquesFlashShotActivity.this, imagePathFromSD, name, false);
        lvBitmaps.setAdapter(adapter);
        progressBar1.setVisibility(View.GONE);
        bundle = new Bundle();
        lvBitmaps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                boolean isSelect = adapter.getisSelectedAt(position);
                if (botoomLayout.getVisibility() == View.VISIBLE) {
                    falg = 0;
                    if (!isSelect) {
                        //当前为被选中，记录下来，用于删除
                        contactSelectedList.add(imagePathFromSD.get(position));
                        ringsEntity = imagePathFromSD.get(position);


                        if (contactSelectedList.size() == visitors.size() && contactSelectedList.size() != 0) {
                            allChose.setText("取消全选");
                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.selected));
                        } else {
                            allChose.setText("全选");
                            choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                        }
                        choseNumber.setText("(" + contactSelectedList.size() + ")");
                    } else {
                        allChose.setText("全选");
                        contactSelectedList.remove(visitors.get(position));
                        choseNumber.setText("(" + contactSelectedList.size() + ")");
                        choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));

                    }
                    //选中状态的切换
                    adapter.setItemisSelectedMap(position, !isSelect);
                } else {
                    bundle.putString("path", imagePathFromSD.get(position));
                    skipAct(SDpictureActivity.class, bundle);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_right_menu:
                bool = !bool;
                falg = 0;
                adapter = new FlashShotAdapter(EquesFlashShotActivity.this, imagePathFromSD, name, bool);
                if (tvRightMenu.getText().equals("编辑")) {
                    tvRightMenu.setText("取消");
                    botoomLayout.setVisibility(View.VISIBLE);
                } else {
                    tvRightMenu.setText("编辑");
                    allChose.setText("全选");
                    contactSelectedList.clear();
                    choseNumber.setText("(0)");
                    botoomLayout.setVisibility(View.GONE);
                }
                lvBitmaps.setAdapter(adapter);
                break;
            case R.id.choseall:
                if (allChose.getText().equals("取消全选")) {
                    allChose.setText("全选");
                    for (int i = 0; i < visitors.size(); i++) {
                        adapter.setItemisSelectedMap(i, false);
                        contactSelectedList.removeAll(visitors);
                    }
                    choseNumber.setText("(0)");
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.unselected));
                } else {
                    contactSelectedList.clear();
                    falg = 2;
                    allChose.setText("取消全选");
                    for (int i = 0; i < visitors.size(); i++) {
                        adapter.setItemisSelectedMap(i, true);
                        contactSelectedList.add(visitors.get(i));
                    }
                    choseNumber.setText("(" + visitors.size() + ")");
                    choseImage.setImageDrawable(getResources().getDrawable(R.mipmap.selected));
                }
                break;
            case R.id.delete:
                Subscription subscribe = Observable.create(new Observable.OnSubscribe<ArrayList>() {
                    @Override
                    public void call(Subscriber<? super ArrayList> subscriber) {
                        for (int i = 0; i < contactSelectedList.size(); i++) {
                            if (!TextUtils.isEmpty(contactSelectedList.get(i))) {
                                File file = new File(contactSelectedList.get(i));
                                if (file.exists()) {
                                    imagePathFromSD.remove(contactSelectedList.get(i));
                                    file.delete();
                                } else {
                                    subscriber.onNext(imagePathFromSD);
                                }
                            }
                        }
                        subscriber.onNext(imagePathFromSD);
                    }
                }).subscribeOn(Schedulers.io()) //指定耗时进程
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ArrayList>() {
                            @Override
                            public void call(ArrayList person) {
                                visitors.removeAll(contactSelectedList);
                                contactSelectedList.clear();
                                adapter = new FlashShotAdapter(EquesFlashShotActivity.this, person, name, false);
                                lvBitmaps.setAdapter(adapter);
                                tvRightMenu.setText("编辑");
                                choseNumber.setText("(0)");
                                allChose.setText("全选");
                                botoomLayout.setVisibility(View.GONE);
                                bool = false;
                            }
                        });
                mCompositeSubscription.add(subscribe);
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
