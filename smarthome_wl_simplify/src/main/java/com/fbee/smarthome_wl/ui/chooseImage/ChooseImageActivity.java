package com.fbee.smarthome_wl.ui.chooseImage;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class ChooseImageActivity extends BaseActivity {

    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private GridView idGridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_image);
    }

    @Override
    protected void initView() {
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        idGridView = (GridView) findViewById(R.id.id_gridView);
    }
    ImageAdapter adapter;
    List<Integer> list;
    @Override
    protected void initData() {
        title.setText("选择图标");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setText("确定");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);

        list = new ArrayList<Integer>();
        //场景
        if("sence".equals(getIntent().getStringExtra("type"))){
            list.add(R.mipmap.sleep);
            list.add(R.mipmap.runaway);
            list.add(R.mipmap.gohome);
            list.add(R.mipmap.scence_entertainment);
            list.add(R.mipmap.scence_receive);
            list.add(R.mipmap.scence_rest);
            list.add(R.mipmap.scence_video);
            list.add(R.mipmap.scence_gettogether);
        }
        //区域
        else{
            list.add(R.mipmap.master_bedroom);
            list.add(R.mipmap.living_room);
            list.add(R.mipmap.second_bedroom);
            list.add(R.mipmap.area_cockloft);
            list.add(R.mipmap.area_nanny_room);
            list.add(R.mipmap.area_tolet);
            list.add(R.mipmap.area_undercroft);
        }


        adapter = new ImageAdapter(list, this);
        idGridView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                ArrayMap<Integer, Boolean> map=adapter.getMap();
                for(Integer key:map.keySet()){
                    if(map.get(key)){
                        if(null != list){
                            Integer res = list.get(key);
                            Intent intent = new Intent();
                            intent.putExtra("res",res);
                            setResult(1002,intent);
                            finish();
                            return;
                        }

                    }

                }
                finish();
                break;

        }

    }
}
