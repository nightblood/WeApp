package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.AnniversaryBean;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.adapter.AnniversaryAdapter;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhuliangfei on 2018/1/29.
 */

@ContentView(R.layout.activity_anniversary_list)
public class AnniversaryListActivity extends BaseActivity{
    @ViewInject(R.id.tv_title_name)
    private TextView mTvTitle;
    @ViewInject(R.id.refresh_layout)
    private SmartRefreshLayout mRefreshLayout;
    @ViewInject(R.id.rv_list)
    private RecyclerView mRvList;
    @ViewInject(R.id.iv_title_right)
    private ImageView mIvRight;
    private int mOffset = 0;
    private AnniversaryAdapter mAdapter;

    @Override
    protected void initView() {
        super.initView();
        mTvTitle.setText(getString(R.string.title_anniversary));
        mIvRight.setImageResource(R.drawable.icon_add_todo);
        initList();
        mRefreshLayout.autoRefresh();
    }

    private void loadMore() {
        loadData();
    }

    private void refresh() {
        mOffset = 0;
        loadData();
    }

    private void loadData() {
        Observable.create(new ObservableOnSubscribe<List<AnniversaryBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<AnniversaryBean>> observableEmitter) throws Exception {

                DbManager dbManager = Utils.getContext().getDbManager();
                List<AnniversaryBean> list = dbManager.selector(AnniversaryBean.class)
                        .orderBy("month_day", false)
                        .offset(mOffset)
                        .limit(15)
                        .findAll();
                if (null == list) {
                    list = new ArrayList<>();
                }
                observableEmitter.onNext(list);
                observableEmitter.onComplete();
            }
        }).subscribe(new Observer<List<AnniversaryBean>>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(List<AnniversaryBean> anniversaryBeans) {
                if (anniversaryBeans == null || anniversaryBeans.isEmpty()) {
                    if (mRefreshLayout.isLoading()) {
                        ToastUtils.showLongToast("没有了哦");
                    } else {
                        ToastUtils.showLongToast("还没有添加纪念日哦。。");
                    }
                } else {
                    mOffset += anniversaryBeans.size();
                    if (mRefreshLayout.isLoading()) {
                        mAdapter.addData(anniversaryBeans);
                    } else {
                        mAdapter.setNewData(anniversaryBeans);
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

    private void initList() {
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

        mRvList.setLayoutManager(new GridLayoutManager(this, 1));
        mAdapter = new AnniversaryAdapter(null);
        mRvList.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                ToastUtils.showLongToast(((AnniversaryBean)adapter.getItem(position)).getContent());
            }
        });
    }
    @Event(R.id.iv_title_left)
    private void leftClick(View view) {
        finish();
    }

    @Event(R.id.iv_title_right)
    private void rightClick(View view) {
        TodoEditActivity.launch(this, TodoEditActivity.TYPE_ANNIVERSARY);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, AnniversaryListActivity.class);
        context.startActivity(intent);
    }
}
