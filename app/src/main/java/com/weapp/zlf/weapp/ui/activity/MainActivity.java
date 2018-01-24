package com.weapp.zlf.weapp.ui.activity;

import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.bean.TodoBean;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.adapter.PanelAdapter;
import com.weapp.zlf.weapp.ui.fragment.BaseFragment;
import com.weapp.zlf.weapp.ui.fragment.CanlenderFragment;
import com.weapp.zlf.weapp.ui.fragment.DiaryFragment;
import com.weapp.zlf.weapp.ui.fragment.MyInfoFragment;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.qqtheme.framework.picker.DateTimePicker;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.drawer_layout)
    private DrawerLayout mDlContainer;
    @ViewInject(R.id.tab_layout)
    private SegmentTabLayout mTabLayout;
    @ViewInject(R.id.view_pager)
    private ViewPager mViewPager;
    @ViewInject(R.id.tv_title)
    private TextView mTvTitle;
    @ViewInject(R.id.iv_top_l)
    private ImageView mIvTopLeft;
    @ViewInject(R.id.iv_top_r)
    private ImageView mIvTopRight;

    @ViewInject(R.id.rv_mood)
    private RecyclerView mRvMood;
    @ViewInject(R.id.rv_weather)
    private RecyclerView mRvWeather;
    @ViewInject(R.id.rv_tag)
    private RecyclerView mRvTag;
    @ViewInject(R.id.tv_time_start)
    private TextView mTvTimeStart;
    @ViewInject(R.id.tv_time_end)
    private TextView mTvTimeEnd;

    private String[] mTabStrArray = new String[] {"日记", "备忘", "我の"};
    private String[] mTitleName = new String[] {"1", "2", "3"};
    private ArrayList<Fragment> mFragments;
    private ArrayList<Integer> moodlist;
    private ArrayList<Integer> weatherlist;
    private ArrayList<Integer> taglist;
    @ViewInject(R.id.iv_mood)
    private ImageView mIvMood;
    @ViewInject(R.id.iv_weather)
    private ImageView mIvWeather;
    @ViewInject(R.id.iv_tag)
    private ImageView mIvTag;
    private int mMood= Integer.MAX_VALUE;
    private int mWeather = Integer.MAX_VALUE;
    private int mTag= Integer.MAX_VALUE;
    private long mStartTime;
    private long mEndTime;

    protected void initView() {
        super.initView();

        initViewPager();
        initTabLayout();
        initNavView();
        initToDoDialog();
    }

    private void initToDoDialog() {
        Observable.create(new ObservableOnSubscribe<TodoBean>() {
            @Override
            public void subscribe(ObservableEmitter<TodoBean> observableEmitter) throws Exception {
                TodoBean bean = Utils.getContext().getDbManager()
                        .selector(TodoBean.class)
                        .where("time_millis", ">", System.currentTimeMillis())
                        .and("is_done", "=", 0)
                        .orderBy("time_millis", false)
                        .findFirst();
                if (bean == null) {
                    observableEmitter.onComplete();
                } else {
                    observableEmitter.onNext(bean);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TodoBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(TodoBean bean) {
                        long interval = bean.getTimeMillis() -  System.currentTimeMillis();
                        long day = interval / 1000/60/60/24;
                        long hour = (interval - day * 1000 * 60 *60 *24) / 1000 / 60 / 60;
                        long minute = (interval - day * 1000 * 60 *60 *24 - hour * 1000 * 60 * 60) / 1000 / 60;
//                        StringBuilder builder = new StringBuilder();
//                        builder.append("距离最近的任务还剩余 ").append(day).append("天").append(hour).append("时").append(minute).append("分");
//                        ;
                        new NearestTodoDialog.Builder(MainActivity.this).setDesc(new Long[]{day, hour, minute}).setData(bean).show();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void initNavView() {
        TypedArray moodArray = getResources().obtainTypedArray(R.array.mood_emoji);
        TypedArray weatherArray = getResources().obtainTypedArray(R.array.weather);
        TypedArray tagArray = getResources().obtainTypedArray(R.array.tag);
        moodlist = new ArrayList<>();
        weatherlist = new ArrayList<>();
        taglist = new ArrayList<>();
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


//        View headerView = mNavViewRight.getHeaderView(0);
//        mRvMood = (RecyclerView) headerView.findViewById(R.id.rv_mood);
//        mRvWeather = (RecyclerView) headerView.findViewById(R.id.rv_weather);
//        mRvTag = (RecyclerView) headerView.findViewById(R.id.rv_tag);
//        mIvMood = (ImageView) headerView.findViewById(R.id.iv_mood);
//        mIvWeather = (ImageView) headerView.findViewById(R.id.iv_weather);
//        mIvTag = (ImageView) headerView.findViewById(R.id.iv_tag);


        mRvMood.setLayoutManager(new GridLayoutManager(this, 4));
        mRvWeather.setLayoutManager(new GridLayoutManager(this, 4));
        mRvTag.setLayoutManager(new GridLayoutManager(this, 4));

        PanelAdapter moodAdapter;
        PanelAdapter weatherAdapter;
        PanelAdapter tagAdapter;
        mRvMood.setAdapter(moodAdapter = new PanelAdapter(moodlist));
        mRvWeather.setAdapter(weatherAdapter = new PanelAdapter(weatherlist));
        mRvTag.setAdapter(tagAdapter = new PanelAdapter(taglist));

        moodAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mMood = position;
                mIvMood.setImageResource((int)adapter.getItem(position));
            }
        });
        weatherAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mWeather = position;
                mIvWeather.setImageResource((int)adapter.getItem(position));
            }
        });
        tagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mTag = position;
                mIvTag.setImageResource((int)adapter.getItem(position));
            }
        });
    }

    private void initViewPager() {
        mFragments = new ArrayList<>();
        mFragments.add(DiaryFragment.newInstance());
        mFragments.add(CanlenderFragment.newInstance());
        mFragments.add(MyInfoFragment.newInstance());
        mViewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
    }

    private void initTabLayout() {
        mTabLayout.setTabData(mTabStrArray);
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
                mTvTitle.setText(mTitleName[position]);
            }

            @Override
            public void onTabReselect(int position) {
                ((BaseFragment)mFragments.get(position)).onTabReselect();
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position);
                mTvTitle.setText(mTitleName[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Event(value = R.id.iv_top_l)
    private void showDrawerLayout(View view) {
        mDlContainer.openDrawer(GravityCompat.START);
    }

    @Event(value = R.id.iv_top_r)
    private void showDrawerLayoutR(View view) {
        mDlContainer.openDrawer(GravityCompat.END);
    }
    @Event(value = R.id.ll_search)
    private void searchClick(View view) {
        SearchActivity.launch(this);
    }

    @Event(value = {R.id.tv_reset, R.id.tv_submit})
    private void resetClick(View view) {
        if (view.getId() == R.id.tv_reset) {
            mIvMood.setImageResource(R.drawable.icon_moood);
            mIvTag.setImageResource(R.drawable.icon_tag);
            mIvWeather.setImageResource(R.drawable.icon_weather);
            mTvTimeEnd.setText("");
            mTvTimeStart.setText("");
            mMood = Integer.MAX_VALUE;
            mTag = Integer.MAX_VALUE;
            mWeather = Integer.MAX_VALUE;
            mStartTime = 0;
            mEndTime = 0;
        } else {
            mDlContainer.closeDrawer(GravityCompat.END);
            ((DiaryFragment) mFragments.get(0)).setArguments(mMood, mTag, mWeather, mStartTime, mEndTime);
        }
    }

    @Event(value = {R.id.tv_time_end, R.id.tv_time_start})
    private void timePick(View view) {
        final int viewId = view.getId();
        Observable.create(new ObservableOnSubscribe<List<DiaryBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DiaryBean>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                DiaryBean start = dbManager.selector(DiaryBean.class)
                        .orderBy("time_millis", false)
                        .limit(1)
                        .findFirst();
                DiaryBean end = dbManager.selector(DiaryBean.class)
                        .orderBy("time_millis", true)
                        .limit(1)
                        .findFirst();
                List<DiaryBean> list = new ArrayList<>();
                list.add(start);
                list.add(end);
                    observableEmitter.onNext(list);
                    observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<DiaryBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(List<DiaryBean> list) {
                        if (list.isEmpty() || list.get(0) == null || list.get(1) == null) {
                            ToastUtils.showLongToast("没有日记就没法搜索哦。。。");
                        } else {
                            showTimePicker(viewId, list.get(0).getTimeMillis(),list.get(1).getTimeMillis());
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void showTimePicker(final int viewId, long startTime, long endMillis) {
        String string1 = TimeUtils.date2String(new Date(startTime), TimeUtils.DEFAULT_PATTERN);
        String substring1 = string1.substring(0, string1.indexOf(" "));
        String[] start = substring1.split("-");
        String string2 = TimeUtils.date2String(new Date(endMillis), TimeUtils.DEFAULT_PATTERN);
        String substring2 = string2.substring(0, string2.indexOf(" "));
        String[] end = substring2.split("-");

        DateTimePicker picker = new DateTimePicker(this, DateTimePicker.HOUR_24);
        picker.setDateRangeStart(Integer.parseInt(start[0]), Integer.parseInt(start[1]), Integer.parseInt(start[2]));
        picker.setDateRangeEnd(Integer.parseInt(end[0]), Integer.parseInt(end[1]), Integer.parseInt(end[2]));
//        picker.setTimeRangeStart(9, 0);
//        picker.setTimeRangeEnd(20, 30);
        picker.setTopLineColor(0x99FF0000);
        picker.setLabelTextColor(0xFFFF0000);
        picker.setDividerColor(0xFFFF0000);
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                String string = (year + "-" + month + "-" + day + " " + hour + ":" + minute);
                long timeMills = TimeUtils.string2Millis(string, "yy-MM-dd HH:mm");
                if (viewId == R.id.tv_time_end) {
                    mTvTimeEnd.setText(string);
                    mEndTime = timeMills;
                } else {
                    mStartTime = timeMills;
                    mTvTimeStart.setText(string);
                }
            }
        });
        picker.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}