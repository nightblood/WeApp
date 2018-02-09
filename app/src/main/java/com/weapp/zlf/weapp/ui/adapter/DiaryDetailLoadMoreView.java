package com.weapp.zlf.weapp.ui.adapter;

import com.weapp.zlf.weapp.R;

/**
 * Created by zhuliangfei on 2018/2/8.
 */

public class DiaryDetailLoadMoreView extends com.chad.library.adapter.base.loadmore.LoadMoreView {
    @Override
    public int getLayoutId() {
        return R.layout.diary_detail_load_more;
    }
    @Override protected int getLoadingViewId() {
        return com.chad.library.R.id.load_more_loading_view;
    }

    @Override protected int getLoadFailViewId() {
        return com.chad.library.R.id.load_more_load_fail_view;
    }

    @Override protected int getLoadEndViewId() {
        return com.chad.library.R.id.load_more_load_end_view;
    }
}
