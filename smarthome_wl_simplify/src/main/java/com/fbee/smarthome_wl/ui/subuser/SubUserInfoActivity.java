package com.fbee.smarthome_wl.ui.subuser;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.SubUserGateWayListAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.bean.GateWayChangeInfo;
import com.fbee.smarthome_wl.constant.RequestCode;
import com.fbee.smarthome_wl.request.QuerySubUserInfoReq;
import com.fbee.smarthome_wl.response.BaseResponse;
import com.fbee.smarthome_wl.response.LoginResult;
import com.fbee.smarthome_wl.response.QuerySubUserInfoResponse;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ToastUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class SubUserInfoActivity extends BaseActivity<SubUserInfoContract.Presenter> implements SubUserInfoContract.View {
    private String  USERINFO="userinfo";
    private String  GATEWAYINFO="getwayinfo";
    private LoginResult.BodyBean.ChildUserListBean childUserListBean;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private SwipeMenuListView listViewSubUserInfo;
    private TextView numSubUserInfo;
    private TextView alairsSubUserInfo;

    private List<QuerySubUserInfoResponse.BodyBean.GatewayListBean> mDatas;
    private SubUserGateWayListAdapter adapter;
    private QuerySubUserInfoResponse.BodyBean.GatewayListBean deleteItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_user_info);
    }

    @Override
    protected void initView() {
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        listViewSubUserInfo = (SwipeMenuListView) findViewById(R.id.listView_sub_user_info);
        numSubUserInfo = (TextView) findViewById(R.id.num_sub_user_info);
        alairsSubUserInfo = (TextView) findViewById(R.id.alairs_sub_user_info);
    }

    @Override
    protected void initData() {
        initApi();
        createPresenter(new SubUserInfoPresenter(this));
        childUserListBean= (LoginResult.BodyBean.ChildUserListBean) getIntent().getSerializableExtra(USERINFO);
        back.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        title.setText("子账号详情");
        tvRightMenu.setVisibility(View.VISIBLE);
        tvRightMenu.setText("编辑");
        tvRightMenu.setOnClickListener(this);
        if(childUserListBean==null)return;
        numSubUserInfo.setText(childUserListBean.getUsername());
        if(childUserListBean.getUser_alias()!=null||!childUserListBean.getUser_alias().equals("")){
            alairsSubUserInfo.setText(childUserListBean.getUser_alias());
        }
        mDatas = new ArrayList<>();
        adapter= new SubUserGateWayListAdapter(this,mDatas);
        listViewSubUserInfo.setAdapter(adapter);
        /*listViewSubUserInfo.setMenuCreator(creator);
        listViewSubUserInfo.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDelateDialog(SubUserInfoActivity.this,position);
                        break;
                }
                return false;
            }
        });*/
        listViewSubUserInfo.setOnItemClickListener(new SwipeMenuListView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
        //请求子账号信息
        querySubUserList();
        //接收网关改变
        receiveGateWayChange();

    }

    /**
     * 子用户信息个改变
     */
    private void receiveGateWayChange() {


        Subscription subHint= RxBus.getInstance().toObservable(GateWayChangeInfo.class)
                .compose(TransformUtils.<GateWayChangeInfo>defaultSchedulers())
                .subscribe(new Action1<GateWayChangeInfo>() {
                    @Override
                    public void call(GateWayChangeInfo event) {
                        mDatas.clear();
                        //请求子账号信息
                        querySubUserList();
                    }
                });
        mCompositeSubscription.add(subHint);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                Intent intent1=new Intent(SubUserInfoActivity.this,ModifySubUserInfo.class);
                intent1.putExtra(USERINFO,childUserListBean);
                startActivity(intent1);

                break;
        }
    }
//    private  void showDelateDialog(final Context context, final int position) {
//        DialogManager.Builder builder = new DialogManager.Builder()
//                .msg("是否确定删除？").cancelable(false).title("提示")
//                .leftBtnText("取消").Contentgravity(Gravity.CENTER_HORIZONTAL)
//                .rightBtnText("删除");
//
//        DialogManager.showDialog(context, builder, new DialogManager.ConfirmDialogListener() {
//            @Override
//            public void onLeftClick() {
//            }
//
//            @Override
//            public void onRightClick() {
//                //删除网关
//                deleteGateWay(position);
//            }
//        });
//    }

    /**
     * 删除网关
     * @param position
     */
//    public void deleteGateWay(int position){
//        deleteItem= mDatas.get(position);
//        DeleteGateWayReq body=new DeleteGateWayReq();
//        body.setUuid(deleteItem.getUuid());
//        body.setVendor_name(deleteItem.getVendor_name());
//        presenter.reqDeleteGateWay(body);
//
//    }
    /**
     * 发送子账号请求
     */
    private void querySubUserList() {

        QuerySubUserInfoReq body=new QuerySubUserInfoReq();
        body.setUsername(childUserListBean.getUsername());
        presenter.reqQuerySubUser(body);
    }
    /**
     * 查询子用户返回
     * @param bean
     */
    @Override
    public void resQuerySubUser(QuerySubUserInfoResponse bean) {
        if (bean==null)return;

        if(bean.getHeader().getHttp_code().equals("200")){
            if(bean.getBody().getGateway_list()!=null&&bean.getBody().getGateway_list().size()>0){

                if(bean.getBody().getUser_alias()!=null||!bean.getBody().getUser_alias().equals("")){
                    alairsSubUserInfo.setText(bean.getBody().getUser_alias());
                }
                mDatas.addAll(bean.getBody().getGateway_list());
                adapter.notifyDataSetChanged();
            }
        }else{
            ToastUtils.showShort(RequestCode.getRequestCode(bean.getHeader().getReturn_string()));
        }
    }

    /**
     * 删除网关返回
     * @param bean
     * @param id
     */
    @Override
    public void resDeleteGateWay(BaseResponse bean, int id) {
        if(bean!=null){
            if(bean.getHeader().getHttp_code().equals("200")){
                ToastUtils.showShort("删除成功!");
                mDatas.remove(deleteItem);
                adapter.notifyDataSetChanged();
            }else {
                ToastUtils.showShort("删除失败!");
            }
        }else {
            ToastUtils.showShort("删除失败!");
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
            deleteItem.setWidth(AppUtil.dp2px(SubUserInfoActivity.this,75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };
}
