package com.fbee.smarthome_wl.ui.homedeviceedit;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.editfragment.MyItemRecyclerViewAdapter;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.bean.EquesListInfo;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.constant.FactoryType;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.zllctl.DeviceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * A fragment representing a list of Items.
 * <p/>
 *用于编辑设备
 * interface.
 */
public class EquipEditFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<HomePageResponse.BodyBean.DeviceListBean> mDatas;
    private MyItemRecyclerViewAdapter adapter;
    private List<HomePageResponse.BodyBean.DeviceListBean> mSelectDatas;
    private LinearLayout rlNodata;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EquipEditFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EquipEditFragment newInstance(int columnCount) {
        EquipEditFragment fragment = new EquipEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }
    public List<HomePageResponse.BodyBean.DeviceListBean> getmSelectDatas(){
        ArrayMap<String,Boolean> map=adapter.getSelectMap();

        for (int i = 0; i <mDatas.size() ; i++) {
            if(map.get(mDatas.get(i).getUuid())){
                mSelectDatas.add(mDatas.get(i));
            }
        }

        return mSelectDatas;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mCompositeSubscription=new CompositeSubscription();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        rlNodata = (LinearLayout) view.findViewById(R.id.rl_nodata);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        // Set the adapter
            Context context = view.getContext();
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            mDatas=new ArrayList<>();
            mSelectDatas=new ArrayList<>();
            adapter=new MyItemRecyclerViewAdapter(mDatas);
            showLoadingDialog(null);
            //原先配置的设备
//            if(null != AppContext.getInstance().getmHomebody()){
//                List<HomePageResponse.BodyBean.DeviceListBean> deviceList= AppContext.getInstance().getmHomebody().getDevice_list();
//                if(null != deviceList){
//                    mSelectDatas.addAll(deviceList);
//                }
//
//            }
            Action1<Throwable> onErrorAction = new Action1<Throwable>() {
                // onError()
                @Override
                public void call(Throwable throwable) {
                    // Error handling
                }
            };

            Subscription subscription1 = Observable.timer(3000, TimeUnit.MILLISECONDS)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (mDatas.size() == 0) {
                                hideLoadingDialog();
                            }
                        }
                    },onErrorAction);
            mCompositeSubscription.add(subscription1);
            //移康设备列表
            List<EquesListInfo.bdylistEntity> equesList = AppContext.getInstance().getBdylist();
            if(null != equesList&&equesList.size()>0){
                if(rlNodata.getVisibility()==View.VISIBLE){
                    rlNodata.setVisibility(View.GONE);
                }
                String name ="";
                for (int i = 0; i <equesList.size() ; i++) {
                    HomePageResponse.BodyBean.DeviceListBean bean = new HomePageResponse.BodyBean.DeviceListBean();
                    bean.setUuid(equesList.get(i).getBid());
                    if(equesList.get(i).getNick()==null || equesList.get(i).getNick().length() ==0){
                        name =equesList.get(i).getName();
                    }else{
                        name = equesList.get(i).getNick();
                    }
                    bean.setNote(name);
                    bean.setVendor_name(FactoryType.EQUES);
                    bean.setType(FactoryType.EQUESCATEYE);
                    mDatas.add(bean);
                    adapter.getSelectMap().put(bean.getUuid(),isSelceted(bean.getUuid()));
                    hideLoadingDialog();
                }

            }


            recyclerView.setAdapter(adapter);

            //注册RXbus
            mSubscription = RxBus.getInstance().toObservable(DeviceInfo.class)
                    .compose(TransformUtils.<DeviceInfo>defaultSchedulers())
                    .subscribe(new Action1<DeviceInfo>() {
                        @Override
                        public void call(DeviceInfo event) {
                            try {

                                if(mDatas.size() >0){
                                    for (int i = 0; i < mDatas.size(); i++) {
                                        if (mDatas.get(i).getUuid().equals(String.valueOf(event.getUId()))) {
                                            return;
                                        }
                                    }
                                }
                                if(null != event){

                                    if(rlNodata.getVisibility()==View.VISIBLE){
                                        rlNodata.setVisibility(View.GONE);
                                    }
                                    HomePageResponse.BodyBean.DeviceListBean bean = new HomePageResponse.BodyBean.DeviceListBean();
                                    bean.setVendor_name(FactoryType.FBEE);
                                    bean.setType(String.valueOf(event.getDeviceId()));
                                    bean.setUuid(String.valueOf(event.getUId()));
                                    bean.setNote(event.getDeviceName());
                                    mDatas.add(bean);
                                    adapter.getSelectMap().put(bean.getUuid(),isSelceted(bean.getUuid()));
                                    adapter.notifyDataSetChanged();
                                    hideLoadingDialog();
                                }
                            }catch(Exception e){

                            }



                        }
                    },onErrorAction);
            AppContext.getInstance().getSerialInstance().getDevices();


        return view;
    }


    private boolean isSelceted(String  uuid){
        try{
            HomePageResponse.BodyBean homebody = AppContext.getInstance().getmHomebody();
            if(homebody  != null){
                List<HomePageResponse.BodyBean.DeviceListBean> list = homebody.getDevice_list();
                if(list ==null || list.size()<=0){
                    return false;
                }
                for (int i =0; i<list.size() ;i++){
                    if(list.get(i).getUuid().equals(uuid)){
                        return true;
                    }
                }
                return false;
            }else{
                return false;
            }

        }catch(Exception e){
            return false;
        }


    }


    @Override
    public int onSetLayoutId() {
        return 0;
    }

    @Override
    public void initView() {

    }

    @Override
    public void bindEvent() {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }

}
