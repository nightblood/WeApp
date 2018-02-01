package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.weapp.zlf.weapp.R;

import org.xutils.view.annotation.ContentView;

/**
 * Created by zhuliangfei on 2018/1/31.
 */
@ContentView(R.layout.activity_diary_search)
public class DiarySearchActivity extends BaseActivity{
    public static void launch(Context context) {
        Intent intent = new Intent(context, DiarySearchActivity.class);
        context.startActivity(intent);
    }
}
