package com.weapp.zlf.weapp.ui.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.melnykov.fab.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.event.DiaryEvent;
import com.weapp.zlf.weapp.ui.activity.DiaryDetailActivity;
import com.weapp.zlf.weapp.ui.activity.DiaryEditActivity;
import com.weapp.zlf.weapp.common.utils.AppUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.activity.DiaryListAdapter;
import com.weapp.zlf.weapp.ui.widge.DiaryDetailDilog;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.DbManager;
import org.xutils.db.Selector;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuliangfei on 2018/1/8.
 */

@ContentView(R.layout.frag_diary)
public class DiaryFragment extends BaseFragment {
    private static final String TAG = DiaryFragment.class.getSimpleName();
    @ViewInject(R.id.recycler_view)
    private RecyclerView mRvList;
    @ViewInject(R.id.fab)
    private FloatingActionButton mFab;
    @ViewInject(R.id.refresh_layout)
    private SmartRefreshLayout mRefreshLayout;
    private int mOffset = 0;
    private DiaryListAdapter mAdapter;
    private long mEndTime;
    private long mStartTime;
    private int mWeather= Integer.MAX_VALUE;
    private int mMood= Integer.MAX_VALUE;
    private int mTag = Integer.MAX_VALUE;
    private View mEmptyView;

    public static Fragment newInstance() {
        DiaryFragment diaryFragment = new DiaryFragment();
        return diaryFragment;
    }

    protected void initView() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refresh();
            }
        });
        mRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                loadMore();
            }
        });

        initList();
        mRefreshLayout.autoRefresh();
    }

    private void loadMore() {
        if (mMood == Integer.MAX_VALUE && mTag == Integer.MAX_VALUE && mWeather == Integer.MAX_VALUE && mStartTime == 0 && mEndTime == 0) {
            load();
        } else {
            loadWithArgs();
        }
    }

    private void initList() {
        TypedArray moodArray = getResources().obtainTypedArray(R.array.mood_emoji);
        TypedArray weatherArray = getResources().obtainTypedArray(R.array.weather);
        TypedArray tagArray = getResources().obtainTypedArray(R.array.tag);
        List<Integer> moodlist = new ArrayList<>();
        List<Integer> weatherlist = new ArrayList<>();
        List<Integer> taglist = new ArrayList<>();
        for (int i = 0; i < moodArray.length(); i++) {
            moodlist.add(moodArray.getResourceId(i, 0));
        }
        for (int i = 0; i < weatherArray.length(); i++) {
            weatherlist.add(weatherArray.getResourceId(i, 0));
        }
        for (int i = 0; i < tagArray.length(); i++) {
            taglist.add(tagArray.getResourceId(i, 0));
        }
        moodArray.recycle();
        weatherArray.recycle();
        tagArray.recycle();

        mRvList.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new DiaryListAdapter(null, moodlist, weatherlist, taglist);
        mRvList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DiaryDetailActivity.launch(getActivity(), (DiaryBean) adapter.getItem(position));
//                new DiaryDetailDilog.Builder(getContext()).setData((DiaryBean) adapter.getItem(position)).show();
            }
        });
        mRvList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    mFab.show();
                } else {
                    mFab.hide();
                }
            }
        });

        mEmptyView = LayoutInflater.from(getContext()).inflate(R.layout.ui_list_empty, null);
        TextView tvContent = (TextView) mEmptyView.findViewById(R.id.tv_content);
        tvContent.setText(getString(R.string.empty_diary_list));
    }

    @Override
    public void onTabReselect() {
        super.onTabReselect();
        mRvList.smoothScrollToPosition(0);
        mRefreshLayout.autoRefresh();
        refresh();
    }

    private void load() {
        Observable.create(new ObservableOnSubscribe<List<DiaryBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DiaryBean>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                List<DiaryBean> list = dbManager.selector(DiaryBean.class)
                        .orderBy("id", true)
                        .offset(mOffset)
                        .limit(15)
                        .findAll();
                if (list == null) {
                    observableEmitter.onNext(new ArrayList<DiaryBean>());
                } else {
                    mOffset += list.size();
                    observableEmitter.onNext(list);
                    for (DiaryBean bean : list) {
                        Log.d(TAG, "subscribe: " + bean.getImages() + "," + bean.getContent());
                    }
                }
                observableEmitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<DiaryBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onNext(List<DiaryBean> diaryBeans) {
                        Log.d(TAG, "onNext: " + mRefreshLayout.isRefreshing());
                        if (mRefreshLayout.isRefreshing()) {
                            if (!diaryBeans.isEmpty()) {
                                onRefreshSuccess(diaryBeans);
                            } else {
                                ToastUtils.showLongToast(getString(R.string.empty_diary_list));
                                mAdapter.setEmptyView(mEmptyView);
                            }
                        } else if (mRefreshLayout.isLoading()) {
                            if (!diaryBeans.isEmpty()) {
                                onLoadMoreSuccess(diaryBeans);
                            } else {
                                ToastUtils.showLongToast(getString(R.string.toast_no_more_data));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ToastUtils.showLongToast(throwable.toString());
                    }

                    @Override
                    public void onComplete() {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.finishRefresh(500);
                        } else if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.finishLoadmore(500);
                        }
                    }
                });
    }

    private void refresh() {
        if (mMood == Integer.MAX_VALUE && mTag == Integer.MAX_VALUE && mWeather == Integer.MAX_VALUE && mStartTime == 0 && mEndTime == 0) {
            mOffset = 0;
            load();
        } else {
            mOffset = 0;
            loadWithArgs();
        }
    }

    private void onLoadMoreSuccess(List<DiaryBean> list) {
        mAdapter.addData(list);
    }

    private void onRefreshSuccess(List<DiaryBean> list) {
        if (mAdapter == null) {
          mAdapter.addData(list);
        } else {
            mAdapter.setNewData(list);
        }
    }

    @Event(value = R.id.fab)
    private void fabClick(final View view) {
        Observable.create(new ObservableOnSubscribe<DiaryBean>() {
            @Override
            public void subscribe(ObservableEmitter<DiaryBean> observableEmitter) throws Exception {
                String date = TimeUtils.date2String(new Date(System.currentTimeMillis()), "yyyy-MM-dd");
                String[] split = date.split("-");
                DbManager dbManager = Utils.getContext().getDbManager();
                DiaryBean bean = dbManager.selector(DiaryBean.class)
                        .where("year", "=", split[0])
                        .and("month", "=", split[1])
                        .and("date", "=", split[2])
                        .findFirst();
                if (bean != null)
                    observableEmitter.onNext(bean);
                else
                    observableEmitter.onError(new Throwable("no data found!!!"));
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<DiaryBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(DiaryBean diaryBean) {
                        Intent intent = new Intent(getActivity(), DiaryEditActivity.class);
                        intent.putExtra("data", diaryBean);
                        startActivity(intent);
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "mybtn").toBundle());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Intent intent = new Intent(getActivity(), DiaryEditActivity.class);
                        startActivity(intent);
//                        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, "mybtn").toBundle());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDiaryEvent(DiaryEvent event) {
        mRefreshLayout.autoRefresh();
    }

    public void setArguments(final int mood, final int tag, final int weather, final long startTime, final long endTime) {
        if (startTime > endTime) {
            ToastUtils.showLongToast("时间选择有误，请重新选择！！！");
            return;
        }

        mMood = mood;
        mTag = tag;
        mWeather = weather;
        mStartTime = startTime;
        mEndTime = endTime;
        mRefreshLayout.autoRefresh();

    }
    public void loadWithArgs() {
        Observable.create(new ObservableOnSubscribe<List<DiaryBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DiaryBean>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                Selector selector = dbManager.selector(DiaryBean.class);
                int index[] = new int[3];
                int value[] = new int[] {mMood, mTag, mWeather};
                String[] field = new String[] {"mood", "tag", "weather"};
                boolean bSet = false;
                if (mMood != Integer.MAX_VALUE) {
                    bSet = true;
                    index[0] = 1;
                }

                if (mTag != Integer.MAX_VALUE) {
                    bSet = true;
                    index[1] = 1;
                }

                if (mWeather != Integer.MAX_VALUE) {
                    bSet = true;
                    index[2] = 1;
                }

                int firstFlag = 0;
                for (int i = 0; i < 3; i++) {
                    if (index[i] == 1) {
                        WhereBuilder whereBuilder = WhereBuilder.b(field[i], "=", value[i]);
                        if (firstFlag == 0) {
                            selector.where(whereBuilder);
                        } else {
                            selector.and(whereBuilder);
                        }
                        firstFlag++;
                    }
                }
                if (mEndTime != 0 && mStartTime != 0 && mStartTime <= mEndTime) {

                    if (bSet) {
                        WhereBuilder end = WhereBuilder.b("time_millis", "<=", mEndTime);
                        WhereBuilder start = WhereBuilder.b("time_millis", ">=", mStartTime);
                        selector.and(end).and(start);
                    } else {
                        WhereBuilder end = WhereBuilder.b("time_millis", "<=", mEndTime);
                        WhereBuilder start = WhereBuilder.b("time_millis", ">=", mStartTime);
                        selector.where(start).and(end);
                    }
                }

                List<DiaryBean> list = selector
                        .orderBy("id", true)
                        .offset(mOffset)
                        .limit(15)
                        .findAll();
                if (list == null) {
                    observableEmitter.onNext(new ArrayList<DiaryBean>());
                } else {
                    mOffset += list.size();
                    observableEmitter.onNext(list);
                    for (DiaryBean bean : list) {
                        Log.d(TAG, "subscribe: " + bean.getImages() + "," + bean.getContent());
                    }
                }
                observableEmitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<DiaryBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onNext(List<DiaryBean> diaryBeans) {
                        Log.d(TAG, "onNext: " + mRefreshLayout.isRefreshing());
                        if (mRefreshLayout.isRefreshing()) {
                            if (!diaryBeans.isEmpty()) {
                                onRefreshSuccess(diaryBeans);
                            } else {
                                ToastUtils.showLongToast(getString(R.string.toast_no_data));
                            }
                        } else if (mRefreshLayout.isLoading()) {
                            if (!diaryBeans.isEmpty()) {
                                onLoadMoreSuccess(diaryBeans);
                            } else {
                                ToastUtils.showLongToast(getString(R.string.toast_no_more_data));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ToastUtils.showLongToast(throwable.toString());
                    }

                    @Override
                    public void onComplete() {
                        if (mRefreshLayout.isRefreshing()) {
                            mRefreshLayout.finishRefresh(1000);
                        } else if (mRefreshLayout.isLoading()) {
                            mRefreshLayout.finishLoadmore(1000);
                        }
                    }
                });
    }
}
