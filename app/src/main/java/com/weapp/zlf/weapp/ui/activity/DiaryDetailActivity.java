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

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.ui.adapter.DiaryPhotoAdapter;

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
public class DiaryDetailActivity extends BaseActivity {


    @ViewInject(R.id.rv_photos)
    private RecyclerView mRvPhoto;

    private DiaryBean mData;
    private DiaryPhotoAdapter mAdapter;

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

    }

    private void initPhotos() {
        mRvPhoto.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new DiaryPhotoAdapter(mData.getImages());
        mRvPhoto.setAdapter(mAdapter);

        View header = LayoutInflater.from(this).inflate(R.layout.header_diary_detail, null);
        header.findViewById(R.id.tv_content);

        TextView tvYearMonth = (TextView) header.findViewById(R.id.tv_year_month);
        TextView tvWeekTime = (TextView) header.findViewById(R.id.tv_week_time);
        TextView tvName = (TextView) header.findViewById(R.id.tv_name);
        TextView tvContent = (TextView) header.findViewById(R.id.tv_content);
        TextView tvDay = (TextView) header.findViewById(R.id.tv_day);
        ImageView ivWeather = (ImageView) header.findViewById(R.id.iv_weather);
        ImageView ivTag = (ImageView) header.findViewById(R.id.iv_tag);
        ImageView ivMood = (ImageView) header.findViewById(R.id.iv_mood);
        ImageView ivDismiss = (ImageView) header.findViewById(R.id.iv_dismiss);
        View bar = header.findViewById(R.id.v_status_bar);
        ViewGroup.LayoutParams layoutParams = bar.getLayoutParams();
        layoutParams.height = statusBarHeight;

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
    private void delete(View view) {
        finish();
    }
    @Event(R.id.iv_edit)
    private void edit(View view) {
        DiaryEditActivity.launch(this, mData);
    }
    public static void launch(Activity activity, DiaryBean item) {
        Intent intent = new Intent(activity, DiaryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", item);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

}
