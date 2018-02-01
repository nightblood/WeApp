package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.allen.library.SuperTextView;
import com.weapp.zlf.weapp.MainApplication;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.UserInfo;
import com.weapp.zlf.weapp.common.utils.SPUtils;
import com.weapp.zlf.weapp.event.UserInfoChangeEvent;
import com.weapp.zlf.weapp.ui.widge.KeyBoardDialog;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;

/**
 * Created by zhuliangfei on 2018/2/1.
 */

@ContentView(R.layout.activity_my_info_edit)
public class MyInfoEditActivity extends BaseActivity{
    @ViewInject(R.id.iv_title_right)
    private ImageView mIvRight;
    @ViewInject(R.id.tv_title_name)
    private TextView mTvTitle;
    @ViewInject(R.id.stv_honor)
    private SuperTextView mStvHonor;
    @ViewInject(R.id.stv_icon)
    private SuperTextView mStvIcon;
    @ViewInject(R.id.stv_name)
    private SuperTextView mStvName;
    private UserInfo mUserInfo;

    @Override
    protected void initView() {
        super.initView();
        mIvRight.setVisibility(View.GONE);
        mTvTitle.setText(getString(R.string.info_edit));
        mUserInfo = MainApplication.mUserInfo;
        Drawable drawable = Drawable.createFromPath(mUserInfo.getPortrait());
        mStvIcon.setRightIcon(drawable);
        mStvHonor.setLeftBottomString(mUserInfo.getHonor());
        mStvName.setLeftBottomString(mUserInfo.getName());
    }

    @Event(R.id.stv_honor)
    private void editHonor(View view) {
        new KeyBoardDialog.Builder(this)
                .setOnClickListener(new KeyBoardDialog.ClickListener() {
                    @Override
                    public void onClick(String s) {
                        mStvHonor.setLeftBottomString(s);
                        SPUtils spUtils = new SPUtils("user_info");
                        spUtils.putString("honor", s);
                        mUserInfo.setHonor(s);
                        EventBus.getDefault().post(new UserInfoChangeEvent(mUserInfo));
                    }
                })
                .setTitle("座右铭")
                .setContent(mUserInfo.getHonor())
                .show();
    }
    @Event(R.id.stv_icon)
    private void editIcon(View view) {
        PhotoPicker.builder()
                .setPhotoCount(1)
                .setPreviewEnabled(true)
                .start(this);
    }
    @Event(R.id.stv_name)
    private void editName(View view) {
        new KeyBoardDialog.Builder(this)
                .setOnClickListener(new KeyBoardDialog.ClickListener() {
                    @Override
                    public void onClick(String s) {
                        mStvName.setLeftBottomString(s);
                        SPUtils spUtils = new SPUtils("user_info");
                        spUtils.putString("name", s);
                        mUserInfo.setName(s);
                        EventBus.getDefault().post(new UserInfoChangeEvent(mUserInfo));

                    }
                })
                .setContent(mUserInfo.getName())
                .setTitle("昵称")
                .show();

    }

    @Event(R.id.iv_title_left)
    private void leftClick(View view) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoPicker.REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> listExtra = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            if (listExtra != null && !listExtra.isEmpty()) {
                mUserInfo.setPortrait(listExtra.get(0));
                Drawable drawable = Drawable.createFromPath(listExtra.get(0));
                mStvIcon.setRightIcon(drawable);
                SPUtils spUtils = new SPUtils("user_info");
                spUtils.putString("portrait", listExtra.get(0));
                EventBus.getDefault().post(mUserInfo);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, MyInfoEditActivity.class);
        context.startActivity(intent);
    }
}
