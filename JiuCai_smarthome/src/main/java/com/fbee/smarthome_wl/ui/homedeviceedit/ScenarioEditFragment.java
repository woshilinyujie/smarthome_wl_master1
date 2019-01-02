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
import com.fbee.smarthome_wl.adapter.editfragment.MyHomeScenarioAdapter;
import com.fbee.smarthome_wl.base.BaseFragment;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.response.HomePageResponse;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.zllctl.SenceInfo;
import com.fbee.zllctl.Serial;

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
 *用于编辑场景
 * interface.
 */
public class ScenarioEditFragment extends BaseFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private List<HomePageResponse.BodyBean.SceneListBean> mDatas;
    private MyHomeScenarioAdapter adapter;
    private List<HomePageResponse.BodyBean.SceneListBean> mSelectDatas;
    private Serial serial;

    private LinearLayout rlNodata;
    private RecyclerView recyclerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScenarioEditFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ScenarioEditFragment newInstance(int columnCount) {
        ScenarioEditFragment fragment = new ScenarioEditFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mCompositeSubscription=new CompositeSubscription();
        }
    }
    public List<HomePageResponse.BodyBean.SceneListBean> getmSelectDatas(){

        ArrayMap<String,Boolean> map=adapter.getSelectMap();
        for (int i = 0; i <mDatas.size() ; i++) {
            if(map.get(mDatas.get(i).getNote())){
                mSelectDatas.add(mDatas.get(i));
            }
        }
        return mSelectDatas;
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
            adapter = new MyHomeScenarioAdapter(mDatas);
            showLoadingDialog(null);

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


            //原先配置的场景
//            if(null != AppContext.getInstance().getmHomebody()){
//                List<HomePageResponse.BodyBean.SceneListBean>  sceneList=AppContext.getInstance().getmHomebody().getScene_list();
//                if(null != sceneList)
//                mSelectDatas.addAll(sceneList);
//            }

            serial= AppContext.getInstance().getSerialInstance();

            //接收到Serial中
            Subscription subscription= RxBus.getInstance().toObservable(SenceInfo.class)
                    .onBackpressureBuffer(10000)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<SenceInfo>() {
                @Override
                public void call(SenceInfo senceInfo) {

                        for (int i = 0; i <mDatas.size() ; i++) {
                            if (mDatas.get(i).getNote().equals(senceInfo.getSenceName())){
                                return;
                            }
                        }
                    if(rlNodata.getVisibility()==View.VISIBLE){
                        rlNodata.setVisibility(View.GONE);
                    }
                    HomePageResponse.BodyBean.SceneListBean bean = new HomePageResponse.BodyBean.SceneListBean();
                    bean.setNote(senceInfo.getSenceName());
                    bean.setUuid(String.valueOf(senceInfo.getSenceId()));
                    mDatas.add(bean);
                    adapter.getSelectMap().put(bean.getNote(),isSelceted(bean.getNote()));
                    adapter.notifyDataSetChanged();
                    hideLoadingDialog();
                }
            },onErrorAction);
            mCompositeSubscription.add(subscription);
            //获取所有场景
            serial.getSences();


            recyclerView.setAdapter(adapter);


        return view;
    }

    private boolean isSelceted(String name){
        try{
            HomePageResponse.BodyBean homebody = AppContext.getInstance().getmHomebody();
            if(homebody  != null){
                List<HomePageResponse.BodyBean.SceneListBean> scenceList = homebody.getScene_list();
                if(scenceList ==null || scenceList.size()<=0){
                    return false;
                }
                for (int i =0; i<scenceList.size() ;i++){
                    if(scenceList.get(i).getNote().equals(name)){
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
