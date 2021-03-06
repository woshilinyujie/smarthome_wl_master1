package com.fbee.smarthome_wl.ui.plan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.fbee.smarthome_wl.R;
import com.fbee.smarthome_wl.adapter.PlanListAdapter;
import com.fbee.smarthome_wl.base.BaseActivity;
import com.fbee.smarthome_wl.common.AppContext;
import com.fbee.smarthome_wl.event.MyTimerTaskInfo;
import com.fbee.smarthome_wl.utils.AppUtil;
import com.fbee.smarthome_wl.utils.RxBus;
import com.fbee.smarthome_wl.utils.ThreadPoolUtils;
import com.fbee.smarthome_wl.utils.TransformUtils;
import com.fbee.smarthome_wl.widget.dialog.DialogManager;
import com.fbee.zllctl.Serial;
import com.fbee.zllctl.TaskInfo;
import com.fbee.zllctl.TimerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class PlanListActivity extends BaseActivity {
    private LinearLayout activityScenaManager;
    private RelativeLayout headerRl;
    private ImageView back;
    private TextView title;
    private ImageView ivRightMenu;
    private TextView tvRightMenu;
    private LinearLayout rlNodata;
    private TextView tvNodata;
    private SwipeMenuListView listView;
    private PlanListAdapter adapter;
    private List<TimerInfo> mlist;
    private List<MyTimerTaskInfo> mlistAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scena_manager);
    }

    @Override
    protected void initView() {
        activityScenaManager = (LinearLayout) findViewById(R.id.activity_scena_manager);
        headerRl = (RelativeLayout) findViewById(R.id.header_rl);
        back = (ImageView) findViewById(R.id.back);
        title = (TextView) findViewById(R.id.title);
        ivRightMenu = (ImageView) findViewById(R.id.iv_right_menu);
        tvRightMenu = (TextView) findViewById(R.id.tv_right_menu);
        listView = (SwipeMenuListView) findViewById(R.id.listView);
        rlNodata = (LinearLayout) findViewById(R.id.rl_nodata);
        tvNodata = (TextView) findViewById(R.id.tv_nodata);
        mlist = new ArrayList<>();
        mlistAction = new ArrayList<>();
    }

    @Override
    protected void initData() {
        mCompositeSubscription = new CompositeSubscription();
        tvNodata.setText("空空如也..请添加计划哦..");
        title.setText("定时计划");
        back.setVisibility(View.VISIBLE);
        tvRightMenu.setText("添加");
        tvRightMenu.setVisibility(View.VISIBLE);
        back.setOnClickListener(this);
        tvRightMenu.setOnClickListener(this);
        showLoadingDialog(null);
        adapter = new PlanListAdapter(mlist, this);
        listView.setAdapter(adapter);
        listView.setMenuCreator(creator);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        showDelateDialog(PlanListActivity.this, position);
                        break;
                }
                return false;
            }
        });
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
            }
        };

        Subscription subscription1 = Observable.timer(4000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        if (mlist.size() == 0) {
                            hideLoadingDialog();
                        }
                    }
                }, onErrorAction);
        mCompositeSubscription.add(subscription1);

        Subscription subscription = RxBus.getInstance().toObservable(TimerInfo.class).compose(TransformUtils.<TimerInfo>defaultSchedulers()).subscribe(new Action1<TimerInfo>() {
            @Override
            public void call(TimerInfo info) {
                if (info == null) return;
                if (rlNodata.getVisibility() == View.VISIBLE) {
                    rlNodata.setVisibility(View.GONE);
                }
                for (int i = 0; i < mlist.size(); i++) {
                    if (mlist.get(i).getTimerId() == info.getTimerId()) {
                        return;
                    }
                }
                mlist.add(0, info);
                adapter.notifyDataSetChanged();
                hideLoadingDialog();
            }
        });
        mCompositeSubscription.add(subscription);
        AppContext.getInstance().getSerialInstance().getTimers();

        //联动任务
        mSubscription = RxBus.getInstance().toObservable(MyTimerTaskInfo.class)
                .compose(TransformUtils.<MyTimerTaskInfo>defaultSchedulers())
                .subscribe(new Action1<MyTimerTaskInfo>() {
                    @Override
                    public void call(final MyTimerTaskInfo event) {
                        if (event == null) return;
                        if (rlNodata.getVisibility() == View.VISIBLE) {
                            rlNodata.setVisibility(View.GONE);
                        }
                        for (int i = 0; i < mlistAction.size(); i++) {
                            if (mlistAction.get(i).getTaskName().equals(event.getTaskName())) {
                                return;
                            }
                        }
                        TimerInfo info = new TimerInfo(event.getSceneId(), (byte) 3, (byte) 1, event.getTimerAction().getWorkMode(),
                                event.getTimerAction().getH(), event.getTimerAction().getM(), event.getTimerAction().getS(), (byte) 3, (byte) 0, (byte) 0);
                        info.setAddName(event.getTaskName());
                        mlist.add(0, info);
                        adapter.notifyDataSetChanged();
                        mlistAction.add(event);
                        hideLoadingDialog();
                    }
                });

        mCompositeSubscription.add(mSubscription);
        Subscription taskSubscription = RxBus.getInstance().toObservable(TaskInfo.class)
                .compose(TransformUtils.<TaskInfo>defaultSchedulers())
                .subscribe(new Action1<TaskInfo>() {
                    @Override
                    public void call(final TaskInfo event) {
                        ThreadPoolUtils.execute(new Runnable() {
                            @Override
                            public void run() {
                                AppContext.getInstance().getSerialInstance().getTaskInfo(event.getTaskName());
                            }
                        });
                    }
                });
        mCompositeSubscription.add(taskSubscription);
        AppContext.getInstance().getSerialInstance().getTasks();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PlanListActivity.this, PlanDetailActivity.class);
                intent.putExtra("TimerInfo", mlist.get(position));
                startActivity(intent);


            }
        });


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
            deleteItem.setWidth(AppUtil.dp2px(PlanListActivity.this, 75));
            // set a icon
            deleteItem.setIcon(R.mipmap.ic_delete);
            // add to menu
            menu.addMenuItem(deleteItem);
        }
    };


    private void showDelateDialog(final Context context, final int position) {
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
                detele(position);
            }


        });
    }


    private void detele(final int position) {
        showLoadingDialog("正在删除计划");
        Subscription sub = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                int ret = -1;
                Serial mSerial = AppContext.getInstance().getSerialInstance();
                if (mlist.get(position).AddressMode == 3) {
                    ret = mSerial.deleteTask(mlist.get(position).getAddName());
                } else {
                    ret = mSerial.deleteTimer(mlist.get(position));
                }

                subscriber.onNext(ret);
                subscriber.onCompleted();
            }

        }).compose(TransformUtils.<Integer>defaultSchedulers())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideLoadingDialog();
                    }

                    @Override
                    public void onNext(Integer ret) {
                        hideLoadingDialog();
                        if (ret >= 0) {
                            showToast("删除成功");
                            mlist.remove(mlist.get(position));
                            if (mlist.size() == 0) {
                                if (rlNodata.getVisibility() == View.GONE) {
                                    rlNodata.setVisibility(View.VISIBLE);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            showToast("删除失败");
                        }
                    }
                });
        mCompositeSubscription.add(sub);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_right_menu:
                Intent intent = new Intent(this, AddPlanActivity.class);
                startActivityForResult(intent, 1001);
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            AppContext.getInstance().getSerialInstance().getTimers();
        }

    }
}
