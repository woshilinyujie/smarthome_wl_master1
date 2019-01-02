package com.fbee.smarthome_wl.ui.rule;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.ChooseAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;

import java.util.List;

public class ChooseActivity extends BaseActivity implements ChooseAdapter.OnWitchSelectListener {
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private ListView listview;
    private List<String> mDatalist;
    private int selectindex =-1;
    private String tag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
    }

    @Override
    protected void initView() {
        mDatalist = (List<String>) getIntent().getSerializableExtra("DataList");
        tag=getIntent().getStringExtra("tag");
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        listview = (ListView) findViewById(R.id.listview);

    }

    @Override
    protected void initData() {
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("请选择");
        tvRightMenu.setText("完成");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setOnClickListener(this);
        if(null != mDatalist){
            ChooseAdapter adapter = new ChooseAdapter(this, mDatalist,tag);
            adapter.setListener(this);
            listview.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_right_menu:
                Intent intent=new Intent();
                intent.putExtra("INDEX",selectindex);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.back:
                finish();
                break;

        }
    }

    @Override
    public void onSelectItem(int position) {
        selectindex =  position;
    }



}
