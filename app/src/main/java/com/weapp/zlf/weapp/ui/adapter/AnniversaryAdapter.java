package com.weapp.zlf.weapp.ui.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.AnniversaryBean;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.activity.TodoEditActivity;
import com.weapp.zlf.weapp.ui.widge.AnniversaryDialog;
import com.weapp.zlf.weapp.ui.widge.RoundTextView;
import com.weapp.zlf.weapp.ui.widge.TagDetailDialog;
import com.weapp.zlf.weapp.ui.widge.popupwindow.MenuPopup;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

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
    protected void convert(final BaseViewHolder helper, final AnniversaryBean item) {
        helper.setText(R.id.tv_name, item.getName());
        final String date = TimeUtils.date2String(new Date(item.getTimeMillis()), "yyyy-MM-dd-HH-mm-ss");
        String[] split = date.split("-");
        helper.setText(R.id.tv_time, split[1] + "\n" + split[2]);
        String[] split1 = TimeUtils.date2String(new Date(item.getCreateTimeMillis())).split(" ");
        helper.setText(R.id.tv_create_time, "始于 " + split1[0]);
        helper.setBackgroundColor(R.id.rl_item, item.getTagColor());
        RoundTextView tag = helper.getView(R.id.tv_tag);
        tag.setText(item.getTagName());
        tag.setColor(ContextCompat.getColor(mContext, R.color.colorPrimary));


        helper.setText(R.id.tv_times, "历经 " + getTimes(item.getCreateTimeMillis()) + " 次");
        if (bExperienced) {
            helper.setVisible(R.id.iv_mask, true);
        } else {
            helper.setVisible(R.id.iv_mask, false);
        }

        if (!TextUtils.isEmpty(item.getIcon()))
            Glide.with(mContext).load(item.getIcon()).into((ImageView) helper.getView(R.id.iv_item));

        final SwipeLayout root = helper.getView(R.id.item_root);
        root.addDrag(SwipeLayout.DragEdge.Right, helper.getView(R.id.bottom_wrapper));

        helper.getView(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoEditActivity.launch(mContext, TodoEditActivity.TYPE_ANNIVERSARY, item);
                root.close();
            }
        });
        helper.getView(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbManager dbManager = Utils.getContext().getDbManager();
                try {
                    dbManager.delete(item);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                remove(helper.getAdapterPosition());
                root.close();
            }
        });
        root.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AnniversaryDialog.Builder(mContext)
//                        .setContent(item.getContent())
//                        .setTitle(item.getName())
//                        .show();
                MenuPopup menuPopup = new MenuPopup(mContext, item.getContent());
                menuPopup.show(v);

            }
        });
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
