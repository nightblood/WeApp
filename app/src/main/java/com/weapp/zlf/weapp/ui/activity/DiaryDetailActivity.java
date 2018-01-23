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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/9.
 */

@ContentView(R.layout.activity_diary_detail)
public class DiaryDetailActivity extends BaseActivity {

    @ViewInject(R.id.viewpager)
    private ViewPager mVpContainer;
    private DiaryBean mData;
    private LayoutInflater mInflater;

    @Override
    protected void initView() {
//        super.initView();

        mInflater = LayoutInflater.from(this);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Serializable data = bundle.getSerializable("data");
        if (null == data)
            finish();
        mData = (DiaryBean) data;
        List<DiaryBean> list = new ArrayList<>();
        list.add(mData);
        /*mVpContainer.setPageTransformer(true, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                seightDis(page, position);
            }
        });*/
        mVpContainer.setAdapter(new DiaryDetailAdapter(list));

    }

    public void seightDis(View view, float position) {
        if (position >= -1 && position <= 1) {
            ViewGroup vg = (ViewGroup) view.findViewById(R.id.rl);
            for (int i = 0; i < vg.getChildCount(); i++) {
                View child = vg.getChildAt(i);
                child.setTranslationX(Math.abs(position) * child.getWidth() * 2);
            }
        }
    }

    public static void launch(Activity activity, DiaryBean item) {
        Intent intent = new Intent(activity, DiaryDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", item);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    private class DiaryDetailAdapter extends PagerAdapter {


        private final List<DiaryBean> mList;

        public DiaryDetailAdapter(List<DiaryBean> list) {
            this.mList = list;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = mInflater.inflate(R.layout.ui_diary_detail_item, container, false);

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
