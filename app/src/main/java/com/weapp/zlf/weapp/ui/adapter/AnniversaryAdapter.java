package com.weapp.zlf.weapp.ui.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.AnniversaryBean;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.ui.widge.RoundTextView;

import java.util.Date;
import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/29.
 */

public class AnniversaryAdapter extends BaseQuickAdapter<AnniversaryBean, BaseViewHolder> {
    private boolean bExperienced;

    public AnniversaryAdapter(List<AnniversaryBean> data) {
        super(R.layout.item_anniversary, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AnniversaryBean item) {
        helper.setText(R.id.tv_name, item.getName());
        String date = TimeUtils.date2String(new Date(item.getTimeMillis()), "yyyy-MM-dd-HH-mm-ss");
        String[] split = date.split("-");
        helper.setText(R.id.tv_time, split[1] + "\n" + split[2]);
        String[] split1 = TimeUtils.date2String(new Date(item.getCreateTimeMillis())).split(" ");
        helper.setText(R.id.tv_create_time, "since " + split1[0]);
        helper.setBackgroundColor(R.id.rl_item, item.getTagColor());
        RoundTextView tag = helper.getView(R.id.tv_tag);
        tag.setText(item.getTagName());
        tag.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));


        helper.setText(R.id.tv_times, "历经 " + getTimes(item.getTimeMillis()) + " 次");
        if (bExperienced) {
            helper.setVisible(R.id.iv_mask, true);
        } else {
            helper.setVisible(R.id.iv_mask, false);
        }

        if (!TextUtils.isEmpty(item.getIcon()))
            Glide.with(mContext).load(item.getIcon()).into((ImageView) helper.getView(R.id.iv_item));
    }

    private String getTimes(long timeMillis) {
        String date = TimeUtils.date2String(new Date(timeMillis), "yyyy-MM-dd");
        String currDate = TimeUtils.date2String(new Date(System.currentTimeMillis()), "yyyy-MM-dd");
        String[] dateArray = date.split("-");
        String[] currDateArray = currDate.split("-");

        int count = 0;
        count = Integer.parseInt(currDateArray[0]) - Integer.parseInt(dateArray[0]);
        if (count < 0) {
            bExperienced = false;
            return "0";
        }
        if (Integer.parseInt(currDateArray[1]) > Integer.parseInt(dateArray[1])
                || (Integer.parseInt(currDateArray[1]) == Integer.parseInt(dateArray[1])
                && Integer.parseInt(currDateArray[2]) >= Integer.parseInt(dateArray[2]))) {
            count++;
            bExperienced = true;
        } else {
            bExperienced = false;
        }

        return String.valueOf(count);
    }
}
