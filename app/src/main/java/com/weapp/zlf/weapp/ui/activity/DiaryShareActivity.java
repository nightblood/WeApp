package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.guo.duoduo.p2pmanager.p2pconstant.P2PConstant;
import com.guo.duoduo.p2pmanager.p2pentity.P2PFileInfo;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.Cache;
import com.weapp.zlf.weapp.common.utils.Constant;
import com.weapp.zlf.weapp.common.utils.SPUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;

import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by zhuliangfei on 2018/2/5.
 */

@ContentView(R.layout.activity_diary_share)
public class DiaryShareActivity extends BaseActivity{

    private static final String TAG = DiaryShareActivity.class.getSimpleName();
    @ViewInject(R.id.iv_title_right)
    private ImageView mIvRight;
    @ViewInject(R.id.iv_image)
    private ImageView mIvImage;
    @ViewInject(R.id.iv_icon)
    private ImageView mIvIcon;
    @ViewInject(R.id.tv_name)
    private TextView mTvName;
    @ViewInject(R.id.tv_title_name)
    private TextView mTvTitle;
    private String name;

    @Override
    protected void initView() {
        super.initView();
        mIvRight.setVisibility(View.GONE);
        mTvTitle.setText(getString(R.string.share_diary));
        SPUtils spUtils = new SPUtils("user_info");
        String portrait = spUtils.getString("portrait");
        String honor = spUtils.getString("honor");

        if (!TextUtils.isEmpty(name)) {
            name = spUtils.getString("name");
        } else {
            name = getString(R.string.app_name);
        }
        if (!TextUtils.isEmpty(portrait)) {
            Glide.with(this).load(portrait).into(mIvIcon);
        } else {
            Glide.with(this).load(R.drawable.plant_2).into(mIvIcon);
        }
        mTvName.setText(name);


    }

    @Event(R.id.iv_title_left)
    private void leftClick(View view) {
        finish();
    }
    @Event(R.id.btn_send)
    private void sendClick(View view) {
        /*PhotoPicker.builder()
                .setPhotoCount(1)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);*/

        P2PFileInfo fileInfo = new P2PFileInfo();
        fileInfo.path = getFilesDir().getPath() + File.separator + Constant.DB_NAME;
        fileInfo.name = Constant.DB_NAME;
        fileInfo.type = P2PConstant.TYPE.PIC;
        fileInfo.size = new File( fileInfo.path).length();

        if (Cache.selectedList.contains(fileInfo)) {
            Cache.selectedList.remove(fileInfo);
        } else {
            Cache.selectedList.add(fileInfo);
        }
        RadarScanActivity.launch(this, name);
    }
    @Event(R.id.btn_receive)
    private void receiveClick(View view) {
        ReceiveActivity.launch(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> extra = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                P2PFileInfo fileInfo = new P2PFileInfo();
                fileInfo.path = extra.get(0);
                fileInfo.name = extra.get(0).substring(extra.get(0).lastIndexOf("/") + 1, extra.get(0).length());
                fileInfo.type = P2PConstant.TYPE.PIC;
                fileInfo.size = new File( extra.get(0)).length();

                Log.d(TAG, "onActivityResult: " + fileInfo.path + "==> " +fileInfo.name + "==>" + fileInfo.size);
                if (Cache.selectedList.contains(fileInfo)) {
                    Cache.selectedList.remove(fileInfo);
                } else {
                    Cache.selectedList.add(fileInfo);
                }
                RadarScanActivity.launch(this, name);
            }
        }

    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, DiaryShareActivity.class);
        context.startActivity(intent);
    }
}
