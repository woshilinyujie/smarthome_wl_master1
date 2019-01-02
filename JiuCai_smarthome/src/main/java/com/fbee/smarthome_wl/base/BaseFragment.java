package com.fbee.smarthome_wl.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fbee.smarthome_wl.api.ApiWrapper;
import com.fbee.smarthome_wl.api.SimpleMyCallBack;

import rx.Subscriber;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by ZhaoLi.Wang on 2016/9/27.
 */
public abstract class BaseFragment<T extends BasePresenter> extends Fragment {
    public BaseActivity mContext;
    public View mContentView = null;
    /**
     * 使用CompositeSubscription来持有所有的Subscriptions
     */
    public CompositeSubscription mCompositeSubscription;
    /**
     * Api类的包装 对象
     */
    public ApiWrapper mApiWrapper;

    protected Subscription mSubscription;

    public  T presenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getBaseActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = inflater.inflate(onSetLayoutId(), container, false);
        }
        //缓存的rootView需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) mContentView.getParent();
        if (parent != null) {
            parent.removeView(mContentView);
        }
        return mContentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
        bindEvent();
    }

    public BaseActivity getBaseActivity() {
        return (BaseActivity) this.getActivity();
    }


    /**
     * 创建相应的 presenter
     */
    public void createPresenter(T presenter) {
        if (presenter != null) {
            this.presenter = presenter;
        }
    }


    /**
     * 初始化 Api  根据具体需要初始化
     */
    public void initApi() {
        mCompositeSubscription  = mContext.getCompositeSubscription();
        mApiWrapper = mContext.getApiWrapper();
    }


    /**
     * 设置布局文件
     *
     * @return 返回布局文件资源Id
     */
    public abstract int onSetLayoutId();

    public abstract void initView();

    public abstract void bindEvent();

    public <T> Subscriber newMySubscriber(final SimpleMyCallBack onNext) {
        return mContext.newMySubscriber(onNext);
    }
    public void showToast(String content) {
        mContext.showToast(content);
    }

    public void showLoadingDialog(String msg) {
        mContext.showLoadingDialog(msg);
    }

    public void hideLoadingDialog() {
        mContext.hideLoadingDialog();
    }
    public void skipAct(Class clazz) {
        mContext.skipAct(clazz);
    }

    public void skipAct(Class clazz, Bundle bundle) {
        mContext.skipAct(clazz,bundle);
    }

    public void skipAct(Class clazz, Bundle bundle, int flags) {
        mContext.skipAct(clazz,bundle,flags);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解绑 presenter
        if (presenter != null) {
            presenter.unsubscribe();
        }
        mContext.cancelSubscription(mSubscription);
    }



}
