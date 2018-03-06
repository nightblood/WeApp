package com.weapp.zlf.weapp.ui.activity;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.URLSpan;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;

import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/10.
 */

public class DiaryListAdapter extends BaseQuickAdapter<DiaryBean, BaseViewHolder> {
    private List<Integer> moods;
    private List<Integer> weathers;
    private List<Integer> tags;

    public DiaryListAdapter(List<DiaryBean> data, List<Integer> mood, List<Integer> weather, List<Integer> tag) {
        super(R.layout.item_diary, data);
        this.moods = mood;
        this.weathers = weather;
        this.tags = tag;
    }

    @Override
    protected void convert(BaseViewHolder helper, DiaryBean item) {
        helper.setText(R.id.tv_content, item.getContent());
        helper.setText(R.id.tv_time, item.getTime());
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setVisible(R.id.iv_image, !TextUtils.isEmpty(item.getImageStr()));
        helper.setText(R.id.tv_week, item.getWeek());
        helper.setText(R.id.tv_date, item.getDate());

        if (1== item.getGender()) {
            helper.setTextColor(R.id.tv_title, ContextCompat.getColor(mContext, R.color.color8));
            helper.setTextColor(R.id.tv_time, ContextCompat.getColor(mContext, R.color.color8));
            helper.setTextColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.color8));
            helper.setTextColor(R.id.tv_week, ContextCompat.getColor(mContext, R.color.color8));
            helper.setTextColor(R.id.tv_date, ContextCompat.getColor(mContext, R.color.color8));
        } else {
            helper.setTextColor(R.id.tv_title, ContextCompat.getColor(mContext, R.color.colorPrimary));
            helper.setTextColor(R.id.tv_time, ContextCompat.getColor(mContext, R.color.colorPrimary));
            helper.setTextColor(R.id.tv_content, ContextCompat.getColor(mContext, R.color.colorPrimary));
            helper.setTextColor(R.id.tv_week, ContextCompat.getColor(mContext, R.color.colorPrimary));
            helper.setTextColor(R.id.tv_date, ContextCompat.getColor(mContext, R.color.colorPrimary));
        }

        if (Integer.MAX_VALUE != item.getMood()) {
            helper.setVisible(R.id.iv_mood, true);
            helper.setImageResource(R.id.iv_mood, moods.get(item.getMood()));
        } else {
            helper.setVisible(R.id.iv_mood, false);
        }
        if (Integer.MAX_VALUE != item.getWeather()) {
            helper.setVisible(R.id.iv_weather, true);
            helper.setImageResource(R.id.iv_weather, weathers.get(item.getWeather()));
        } else {
            helper.setVisible(R.id.iv_weather, false);
        }
        if (Integer.MAX_VALUE != item.getTag()) {
            helper.setAlpha(R.id.iv_tag, 1);
            helper.setImageResource(R.id.iv_tag, tags.get(item.getTag()));
        } else {
            helper.setAlpha(R.id.iv_tag, 0);
        }

    }
}
