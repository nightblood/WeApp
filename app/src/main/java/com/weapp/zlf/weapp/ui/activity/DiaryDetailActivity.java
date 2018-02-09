package com.weapp.zlf.weapp.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.lsjwzh.widget.recyclerviewpager.RecyclerViewPager;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.common.utils.AssertUtils;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.event.DiaryEvent;
import com.weapp.zlf.weapp.ui.adapter.DiaryDetailLoadMoreView;
import com.weapp.zlf.weapp.ui.adapter.DiaryPhotoAdapter;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
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
 * Created by zhuliangfei on 2018/1/9.
 */

@ContentView(R.layout.activity_diary_detail)
public class DiaryDetailActivity extends BaseActivity implements BaseQuickAdapter.OnItemChildClickListener {

    private static final String TAG = DiaryDetailActivity.class.getSimpleName();
    private DiaryBean mData;
    private MyAdapter mAdapter;
    @ViewInject(R.id.rvp)
    protected RecyclerViewPager mRecyclerView;
    private int mOffset;


    @Override
    protected void initView() {
        super.initView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Serializable data = bundle.getSerializable("data");
        if (null == data)
            finish();
        mData = (DiaryBean) data;

        initViewPager();

        loadData();
    }

    protected void initViewPager() {
        LinearLayoutManager layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layout);
        mAdapter = new MyAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        });

        mAdapter.setOnItemChildClickListener(this);
        mAdapter.setLoadMoreView(new DiaryDetailLoadMoreView());
        mAdapter.setEnableLoadMore(true);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLongClickable(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
//                updateState(scrollState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
//                mPositionText.setText("First: " + mRecyclerViewPager.getFirstVisiblePosition());
                int childCount = mRecyclerView.getChildCount();
                int width = mRecyclerView.getChildAt(0).getWidth();
                int padding = (mRecyclerView.getWidth() - width) / 2;
//                mCountText.setText("Count: " + childCount);

                for (int j = 0; j < childCount; j++) {
                    View v = recyclerView.getChildAt(j);
                    //往左 从 padding 到 -(v.getWidth()-padding) 的过程中，由大到小
                    float rate = 0;
                    ;
                    if (v.getLeft() <= padding) {
                        if (v.getLeft() >= padding - v.getWidth()) {
                            rate = (padding - v.getLeft()) * 1f / v.getWidth();
                        } else {
                            rate = 1;
                        }
                        v.setScaleY(1 - rate * 0.1f);
                        v.setScaleX(1 - rate * 0.1f);

                    } else {
                        //往右 从 padding 到 recyclerView.getWidth()-padding 的过程中，由大到小
                        if (v.getLeft() <= recyclerView.getWidth() - padding) {
                            rate = (recyclerView.getWidth() - padding - v.getLeft()) * 1f / v.getWidth();
                        }
                        v.setScaleY(0.9f + rate * 0.1f);
                        v.setScaleX(0.9f + rate * 0.1f);
                    }
                }
            }
        });
        mRecyclerView.addOnPageChangedListener(new RecyclerViewPager.OnPageChangedListener() {
            @Override
            public void OnPageChanged(int oldPosition, int newPosition) {
                Log.d("test", "oldPosition:" + oldPosition + " newPosition:" + newPosition);
            }
        });

        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (mRecyclerView.getChildCount() < 3) {
                    if (mRecyclerView.getChildAt(1) != null) {
                        if (mRecyclerView.getCurrentPosition() == 0) {
                            View v1 = mRecyclerView.getChildAt(1);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        } else {
                            View v1 = mRecyclerView.getChildAt(0);
                            v1.setScaleY(0.9f);
                            v1.setScaleX(0.9f);
                        }
                    }
                } else {
                    if (mRecyclerView.getChildAt(0) != null) {
                        View v0 = mRecyclerView.getChildAt(0);
                        v0.setScaleY(0.9f);
                        v0.setScaleX(0.9f);
                    }
                    if (mRecyclerView.getChildAt(2) != null) {
                        View v2 = mRecyclerView.getChildAt(2);
                        v2.setScaleY(0.9f);
                        v2.setScaleX(0.9f);
                    }
                }

            }
        });
    }

    private void loadData() {
        Observable.create(new ObservableOnSubscribe<List<DiaryBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DiaryBean>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                List<DiaryBean> list = dbManager.selector(DiaryBean.class)
                        .orderBy("id", true)
                        .where("id", "<", mData.getId())
                        .or("id", "=", mData.getId())
                        .offset(mOffset)
                        .limit(15)
                        .findAll();
                if (list == null) {
                    observableEmitter.onNext(new ArrayList<DiaryBean>());
                } else {
                    mOffset += list.size();
                    observableEmitter.onNext(list);
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
                        if (!diaryBeans.isEmpty()) {
                            if (mAdapter.isLoading()) {
                                mAdapter.addData(diaryBeans);
                                mAdapter.loadMoreComplete();
                            } else {
                                mAdapter.setNewData(diaryBeans);
                            }
                        } else {
                            mAdapter.loadMoreEnd();
                            mAdapter.setEnableLoadMore(false);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        ToastUtils.showLongToast(throwable.toString());
                        mAdapter.loadMoreFail();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void deleteClick(DiaryBean bean, int position) {
        DbManager dbManager = Utils.getContext().getDbManager();
        try {
            dbManager.delete(bean);
        } catch (DbException e) {
            e.printStackTrace();
        }
        mAdapter.remove(position);
        EventBus.getDefault().post(new DiaryEvent());
    }

    private void editClick(DiaryBean bean) {
        DiaryEditActivity.launch(this, bean);
        finish();
    }

    public static void launch(Activity activity, DiaryBean item) {
        Intent intent = new Intent(activity, DiaryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", item);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    public boolean onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        DiaryBean item = (DiaryBean) adapter.getItem(position);
        switch (view.getId()) {
            case R.id.iv_delete:
                deleteClick(item, position);
                break;
            case R.id.iv_edit:
                editClick(item);
                break;
        }
        return true;
    }

    private class MyAdapter extends BaseQuickAdapter<DiaryBean, BaseViewHolder> {
        public MyAdapter(List<DiaryBean> data) {
            super(R.layout.item_diary_detail, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, DiaryBean item) {
            RecyclerView rvPhoto = helper.getView(R.id.rv_photos);
            rvPhoto.setLayoutManager(new LinearLayoutManager(mContext));
            final DiaryPhotoAdapter mPhotoAdapter;
            if (null == item.getImages() || item.getImages().isEmpty()) {
                List<String> data = new ArrayList<>();
                data.add("NONE");
                mPhotoAdapter = new DiaryPhotoAdapter(data);
            } else {
                mPhotoAdapter = new DiaryPhotoAdapter(item.getImages());
            }
            rvPhoto.setAdapter(mPhotoAdapter);

            View header = LayoutInflater.from(mContext).inflate(R.layout.header_diary_detail, null);
            header.findViewById(R.id.tv_content);

            View bar = header.findViewById(R.id.v_status_bar);
            ViewGroup.LayoutParams layoutParams = bar.getLayoutParams();
            layoutParams.height = statusBarHeight;
            TextView tvYearMonth = (TextView) header.findViewById(R.id.tv_year_month);
            TextView tvWeekTime = (TextView) header.findViewById(R.id.tv_week_time);
            TextView tvName = (TextView) header.findViewById(R.id.tv_name);
            TextView tvContent = (TextView) header.findViewById(R.id.tv_content);
            TextView tvDay = (TextView) header.findViewById(R.id.tv_day);
            ImageView ivWeather = (ImageView) header.findViewById(R.id.iv_weather);
            ImageView ivTag = (ImageView) header.findViewById(R.id.iv_tag);
            ImageView ivMood = (ImageView) header.findViewById(R.id.iv_mood);
            ImageView ivDismiss = (ImageView) header.findViewById(R.id.iv_dismiss);

            ivWeather.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Bounce)
                            .playOn(v);
                }
            });
            ivTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Bounce)
                            .playOn(v);
                }
            });
            ivMood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    YoYo.with(Techniques.Bounce)
                            .playOn(v);
                }
            });
            ivDismiss.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            if (item.getWeather() != Integer.MAX_VALUE) {
                ivWeather.setImageResource(AssertUtils.weatherlist.get(item.getWeather()));
                ivWeather.setVisibility(View.VISIBLE);
            } else {
                ivWeather.setVisibility(View.GONE);
            }
            if (item.getTag() != Integer.MAX_VALUE) {
                ivTag.setImageResource(AssertUtils.taglist.get(item.getTag()));
                ivTag.setVisibility(View.VISIBLE);
            } else {
                ivTag.setVisibility(View.GONE);
            }
            if (item.getMood() != Integer.MAX_VALUE) {
                ivMood.setImageResource(AssertUtils.moodlist.get(item.getMood()));
                ivMood.setVisibility(View.VISIBLE);
            } else {
                ivMood.setVisibility(View.GONE);
            }
            if (!TimeUtils.isToday(item.getTimeMillis())) {
                helper.setVisible(R.id.iv_edit, false);
            } else {
                helper.setVisible(R.id.iv_edit, true);
            }
            helper.addOnClickListener(R.id.iv_edit).addOnClickListener(R.id.iv_delete);
            StringBuilder yearmonth = new StringBuilder();
            yearmonth.append(item.getYear()).append("年").append(item.getMonth()).append("月");
            tvYearMonth.setText(yearmonth.toString());
            StringBuilder weektime = new StringBuilder();
            weektime.append(item.getWeek()).append(" ").append(item.getTime());
            tvWeekTime.setText(weektime.toString());
            tvName.setText(item.getTitle());
            tvContent.setText(item.getContent());
            tvDay.setText(item.getDate());

            mPhotoAdapter.addHeaderView(header);
            mPhotoAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    photoPreview(mPhotoAdapter.getData(), position);
                }
            });
        }
        private void photoPreview(List<String> photoPaths, int pos) {
            if (photoPaths == null || photoPaths.isEmpty())
                return;
            ArrayList<String> list = new ArrayList<>();
            for (String photoPath : photoPaths) {
                if (photoPath.equals("NONE"))
                    continue;
                list.add(photoPath);
            }
            PhotoPreview.builder()
                    .setPhotos(list)
                    .setCurrentItem(pos)
                    .setShowDeleteButton(false)
                    .start(DiaryDetailActivity.this);
        }
    }
}
