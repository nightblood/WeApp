package com.weapp.zlf.weapp.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/10.
 */

public class PanelAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {
    public PanelAdapter(List<Integer> data) {
        super(R.layout.item_panel, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Integer item) {
        helper.setImageResource(R.id.iv_item, item);

    }
}
