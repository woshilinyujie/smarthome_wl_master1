package com.fbee.smarthome_wl.ui.subuser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.ItemSubUserListAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.AddSubUserInfo;
import com.fbee.smarthome_wl.bean.GateWayChangeInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.DeteleChildUser;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class SubUserListActivity extends BaseActivity<SubUserListContract.Presenter> implements SubUserListContract.View {
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private SwipeMenuListView listViewSubUser;
    private RelativeLayout rlNodata;

    private List<LoginResult.BodyBean.ChildUserListBean> mDatas;
    private ItemSubUserListAdapter adapter;
    private LoginResult.BodyBean.ChildUserListBean deleteItem;
    private String  USERINFO="userinfo";
    private List<LoginResult.BodyBean.ChildUserListBean> subUserList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_user_list);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        listViewSubUser = (SwipeMenuListView) findViewById(R.id.listView_sub_user);
        rlNodata = (RelativeLayout) findViewById(R.id.rl_nodata);

    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new SubUserListPresenter(this));
        title.setText("子账号");
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("添加");
        tvRightMenu.setOnClickListener(this);
        mDatas = new ArrayList<>();
        subUserList=AppContext.getInstance().getBodyBean().getChild_user_list();
        if (subUserList==null||subUserList.size()==0){
            ToastUtils.showShort("暂无子账号信息!");
        }else{
            rlNodata.setVisibility(View.GONE);
        }
        if(subUserList!=null&&subUserList.size()!=0){
            mDatas.addAll(subUserList);
        }
        adapter= new ItemSubUserListAdapter(this,mDatas);
        listViewSubUser.setAdapter(adapter);
        listViewSubUser.setMenuCreator(creator);

        listViewSubUser.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDelateDialog(SubUserListActivity.this,position);
                        break;
                }
                return false;
            }
        });
        listViewSubUser.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1=new Intent(SubUserListActivity.this,SubUserInfoActivity.class);
                intent1.putExtra(USERINFO,mDatas.get(i));
                startActivity(intent1);
            }
        });

        //修改子用户信息改变
        receiveGateWayChange();

    }
    private  void showDelateDialog(final Context context, final int position) {
        DialogManager.Builder builder = new DialogManager.Builder()
                .msg("是否确定删除？").cancelable(false).title("提示")
                .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
                .rightBtnText("删除");

        DialogManager.showDialog(context, builder, new DialogManager.ConfirmDialogListener() {
            @Override
            public void onLeftClick() {
            }

            @Override
            public void onRightClick() {
                //删除子账号
                delateSubUser(position);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        //接收子账号改变
        receiveSubUserChange();
    }
    /**
     * 修改子用户信息改变
     */
    private void receiveGateWayChange() {


        Subscription subHint= RxBus.getInstance().toObservable(GateWayChangeInfo.class)
                .compose(TransformUtils.<GateWayChangeInfo>defaultSchedulers())
                .subscribe(new Action1<GateWayChangeInfo>() {
                    @Override
                    public void call(GateWayChangeInfo event) {
                        mDatas.clear();
                        subUserList=AppContext.getInstance().getBodyBean().getChild_user_list();
                        mDatas.addAll(subUserList);
                        adapter.notifyDataSetChanged();
                        if(subUserList.size()==0){
                            if(rlNodata.getVisibility()==View.GONE){
                                rlNodata.setVisibility(View.VISIBLE);
                            }
                        }else {
                            if(rlNodata.getVisibility()==View.VISIBLE){
                                rlNodata.setVisibility(View.GONE);
                            }
                        }
                    }
                });
        mCompositeSubscription.add(subHint);
    }
    /**
     * 接收子账号改变
     */
    private void receiveSubUserChange() {

        Subscription subscriptionSence = RxBus.getInstance()
                .toObservable(AddSubUserInfo.class)
                .compose(TransformUtils.<AddSubUserInfo>defaultSchedulers())
                .subscribe(new Action1<AddSubUserInfo>() {
            @Override
            public void call(AddSubUserInfo event) {
                mDatas.clear();
                mDatas.addAll(AppContext.getInstance().getBodyBean().getChild_user_list());
                adapter.notifyDataSetChanged();
                if(AppContext.getInstance().getBodyBean().getChild_user_list().size()==0){
                    if(rlNodata.getVisibility()==View.GONE){
                        rlNodata.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(rlNodata.getVisibility()==View.VISIBLE){
                        rlNodata.setVisibility(View.GONE);
                    }
                }
            }
        });
        mCompositeSubscription.add(subscriptionSence);
    }


    /**
     * 删除子账号请求
     * @param position
     */
    private void delateSubUser(int position) {
        deleteItem=mDatas.get(position);
        DeteleChildUser body=new DeteleChildUser();
        body.setUsername(deleteItem.getUsername());
        presenter.reqDeleteSubUser(body);
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            //添加子账号
            case R.id.tv_right_menu:
                Intent intent1=new Intent(SubUserListActivity.this,AddSubUserActivity.class);
                startActivity(intent1);
                break;
        }
    }



    /**
     * 删除子账号返回
     * @param bean
     */
    @Override
    public void resDeleteSubUser(BaseResponse bean) {
        if(bean==null){
            ToastUtils.showShort("删除失败!");
            return;
        }
        if(bean!=null&&deleteItem!=null){
            if(bean.getHeader().getHttp_code().equals("200")){
                ToastUtils.showShort("删除成功!");
                AppContext.getInstance().getBodyBean().getChild_user_list().remove(deleteItem);
                subUserList.remove(deleteItem);
                mDatas.remove(deleteItem);
                adapter.notifyDataSetChanged();
                if(subUserList.size()==0){
                    if(rlNodata.getVisibility()==View.GONE){
                        rlNodata.setVisibility(View.VISIBLE);
                    }
                }else {
                    if(rlNodata.getVisibility()==View.VISIBLE){
                        rlNodata.setVisibility(View.GONE);
                    }
                }
            }else{
                ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
            }
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoadingDialog() {

    }

    SwipeMenuCreator creator = new SwipeMenuCreator() {

        @Override
        public void create(SwipeMenu menu) {
            // create "delete" item
            SwipeMenuItem deleteItem = new SwipeMenuItem(
                    getApplicationContext());
            // set item background
            deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                    0x3F, 0x25)));
            // set item width
            deleteItem.setWidth(AppUtil.dp2px(SubUserListActivity.this,75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };
}
