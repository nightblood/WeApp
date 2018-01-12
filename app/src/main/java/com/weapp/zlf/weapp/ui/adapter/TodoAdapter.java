package com.weapp.zlf.weapp.ui.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.TodoBean;

import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/12.
 */

public class TodoAdapter extends BaseQuickAdapter<TodoBean, BaseViewHolder>{
    public TodoAdapter(List<TodoBean> data) {
        super(R.layout.item_todo, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TodoBean item) {
        helper.setText(R.id.tv_title, item.getTitle());
        helper.setText(R.id.tv_content, item.getContent());
        helper.setText(R.id.tv_time, item.getTimeMillis() + "");
        helper.setBackgroundColor(R.id.v_tag, item.getTagColor());

    }
}
