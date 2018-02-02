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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.common.utils.AssertUtils;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.adapter.DiaryPhotoAdapter;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/9.
 */

@ContentView(R.layout.activity_diary_detail)
public class DiaryDetailActivity extends BaseActivity implements View.OnClickListener {


    @ViewInject(R.id.rv_photos)
    private RecyclerView mRvPhoto;
    @ViewInject(R.id.iv_delete)
    private ImageView mIvDelete;

    private DiaryBean mData;
    private DiaryPhotoAdapter mAdapter;
    @ViewInject(R.id.iv_edit)
    private ImageView mIvEdit;

    @Override
    protected void initView() {
        super.initView();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Serializable data = bundle.getSerializable("data");
        if (null == data)
            finish();
        mData = (DiaryBean) data;

        initPhotos();
        if (!TimeUtils.isToday(mData.getTimeMillis())) {
            mIvEdit.setVisibility(View.GONE);
        }
    }

    private void initPhotos() {
        mRvPhoto.setLayoutManager(new LinearLayoutManager(this));
        if (null == mData.getImages() || mData.getImages().isEmpty()) {
            List<String> data = new ArrayList<>();
            data.add("NONE");
            mAdapter = new DiaryPhotoAdapter(data);
        } else {
            mAdapter = new DiaryPhotoAdapter(mData.getImages());
        }
        mRvPhoto.setAdapter(mAdapter);

        View header = LayoutInflater.from(this).inflate(R.layout.header_diary_detail, null);
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

        ivWeather.setOnClickListener(this);
        ivTag.setOnClickListener(this);
        ivMood.setOnClickListener(this);
        ivDismiss.setOnClickListener(this);
        if (mData.getWeather() != Integer.MAX_VALUE) {
            ivWeather.setImageResource(AssertUtils.weatherlist.get(mData.getWeather()));
            ivWeather.setVisibility(View.VISIBLE);
        }
        if (mData.getTag() != Integer.MAX_VALUE) {
            ivTag.setImageResource(AssertUtils.taglist.get(mData.getTag()));
            ivTag.setVisibility(View.VISIBLE);
        }
        if (mData.getMood() != Integer.MAX_VALUE) {
            ivMood.setImageResource(AssertUtils.moodlist.get(mData.getMood()));
            ivMood.setVisibility(View.VISIBLE);
        }

        StringBuilder yearmonth = new StringBuilder();
        yearmonth.append(mData.getYear()).append("年").append(mData.getMonth()).append("月");
        tvYearMonth.setText(yearmonth.toString());
        StringBuilder weektime = new StringBuilder();
        weektime.append(mData.getWeek()).append(" ").append(mData.getTime());
        tvWeekTime.setText(weektime.toString());
        tvName.setText(mData.getTitle());
        tvContent.setText(mData.getContent());
        tvDay.setText(mData.getDate());

        mAdapter.addHeaderView(header);
    }

    @Event(R.id.iv_delete)
    private void deleteClick(View view) {
        DbManager dbManager = Utils.getContext().getDbManager();
        try {
            dbManager.delete(mData);
        } catch (DbException e) {
            e.printStackTrace();
        }
        finish();
    }
    @Event(R.id.iv_edit)
    private void editClick(View view) {
        DiaryEditActivity.launch(this, mData);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_weather:
            case R.id.iv_tag:
            case R.id.iv_mood:
                YoYo.with(Techniques.Bounce)
                        .playOn(v);
                break;
            case R.id.iv_dismiss:
                finish();
                break;
        }
    }
}
