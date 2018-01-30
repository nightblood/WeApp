package com.weapp.zlf.weapp.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.bean.ImageBean;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.adapter.PhotoAdapter;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by zhuliangfei on 2018/1/23.
 */

@ContentView(R.layout.frag_my_info)
public class MyInfoFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener {
    @ViewInject(R.id.rv_photos)
    private RecyclerView mRvPhotos;
    @ViewInject(R.id.refresh_layout)
    private SmartRefreshLayout mRefreshLayout;
    private PhotoAdapter mAdapter;
    private int mOffset;
    private TextView mTvNumDiary;
    private TextView mTvNumPhoto;

    public static Fragment newInstance() {
        return new MyInfoFragment();
    }


    @Override
    protected void initView() {
        super.initView();
        initRefreshLayout();
//        getPhotoData();
    }

    private void initRefreshLayout() {
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

        initRecyclerView();
        mRefreshLayout.autoRefresh();
    }

    private void loadMore() {
        getPhotoData();
    }

    private void refresh() {
        mOffset = 0;
        getPhotoData();
    }

    private void initRecyclerView() {
        StaggeredGridLayoutManager mgr = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mRvPhotos.setLayoutManager(mgr);
        mAdapter = new PhotoAdapter(null);
        mRvPhotos.setAdapter(mAdapter);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.ui_my_info_header, null);
        mAdapter.addHeaderView(header);
        mTvNumDiary = (TextView) header.findViewById(R.id.tv_num_diary);
        mTvNumPhoto = (TextView) header.findViewById(R.id.tv_num_photo);
        header.findViewById(R.id.iv_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                YoYo.with(Techniques.Bounce).playOn(v);
            }
        });
        mAdapter.setOnItemClickListener(this);

        Observable.create(new ObservableOnSubscribe<List<String>>() {
            @Override
            public void subscribe(ObservableEmitter<List<String>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                long photoNum = dbManager.selector(ImageBean.class).count();
                long diaryNum = dbManager.selector(DiaryBean.class).count();

                List<String> list = new ArrayList<>();
                list.add(String.valueOf(diaryNum));
                list.add(String.valueOf(photoNum));
                observableEmitter.onNext(list);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(List<String> data) {
                        mTvNumDiary.setText(data.get(0));
                        mTvNumPhoto.setText(data.get(1));
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void getPhotoData() {
        Observable.create(new ObservableOnSubscribe<List<ImageBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ImageBean>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                List<ImageBean> data = dbManager.selector(ImageBean.class)
                        .orderBy("id", false)
                        .offset(mOffset)
                        .limit(20)
                        .findAll();

                if (data == null) {
                    data = new ArrayList<>();
                }
                observableEmitter.onNext(data);
                observableEmitter.onComplete();
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<List<ImageBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(List<ImageBean> diaryBeans) {
                        if (mRefreshLayout.isRefreshing()) {
                            if (diaryBeans != null) {
                                onRefreshSuccess(diaryBeans);
                                mOffset += diaryBeans.size();
                            } else {
                                ToastUtils.showLongToast(getString(R.string.toast_no_data));
                            }
                        } else if (mRefreshLayout.isLoading()) {
                            if (diaryBeans != null) {
                                onLoadMoreSuccess(diaryBeans);
                                mOffset += diaryBeans.size();
                            } else {
                                ToastUtils.showLongToast(getString(R.string.toast_no_more_data));
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

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
    private void onLoadMoreSuccess(List<ImageBean> list) {
        mAdapter.addData(list);
    }

    private void onRefreshSuccess(List<ImageBean> list) {
        if (mAdapter == null) {
            mAdapter.addData(list);
        } else {
            mAdapter.setNewData(list);
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ArrayList<ImageBean> data = (ArrayList<ImageBean>) mAdapter.getData();
        ArrayList<String> list = new ArrayList<>();
        for (ImageBean bean : data) {
            list.add(bean.getImage());
        }
        PhotoPreview.builder()
                .setPhotos(list)
                .setCurrentItem(position)
                .setShowDeleteButton(false)
                .start(getActivity());
    }
}
