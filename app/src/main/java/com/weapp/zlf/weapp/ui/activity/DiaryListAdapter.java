package com.weapp.zlf.weapp.ui.activity;

import android.graphics.Color;
import android.text.TextUtils;

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
            helper.setBackgroundColor(R.id.ll_item, Color.parseColor("#770099bb"));
        } else {
//            helper.setBackgroundRes(R.id.ll_item, R.drawable.bg_clickable_item);
            helper.setBackgroundColor(R.id.ll_item, Color.parseColor("#aaffffff"));
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
