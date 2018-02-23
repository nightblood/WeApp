package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.ImageBean;
import com.weapp.zlf.weapp.common.Cache;
import com.weapp.zlf.weapp.common.utils.Constant;
import com.weapp.zlf.weapp.common.utils.SPUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.p2pmanager.p2pconstant.P2PConstant;
import com.weapp.zlf.weapp.p2pmanager.p2pentity.P2PFileInfo;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

import static com.weapp.zlf.weapp.common.utils.Constant.DB_DIRS;

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

        Observable.create(new ObservableOnSubscribe<List<P2PFileInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<P2PFileInfo>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                List<ImageBean> beans = dbManager.selector(ImageBean.class).findAll();
                if (beans == null)
                    beans = new ArrayList<>();
                List<P2PFileInfo> list = new ArrayList<>();
                for (ImageBean bean : beans) {
                    P2PFileInfo fileInfo = new P2PFileInfo();
                    fileInfo.path = bean.getImage();
                    fileInfo.name = bean.getImage().substring(bean.getImage().lastIndexOf("/"), bean.getImage().length());
                    fileInfo.type = P2PConstant.TYPE.PIC;
                    fileInfo.size = new File( fileInfo.path).length();
                    list.add(fileInfo);
                }

                P2PFileInfo fileInfo = new P2PFileInfo();
                fileInfo.path = DB_DIRS[0] + File.separator + Constant.DB_NAME;
                fileInfo.name = Constant.DB_NAME;
                fileInfo.type = P2PConstant.TYPE.DATABASE;
                fileInfo.size = new File(fileInfo.path).length();

                Log.d(TAG, "subscribe: " + fileInfo.path);
                if (!Cache.selectedList.contains(fileInfo)) {
                    Cache.selectedList.add(fileInfo);
                }
                observableEmitter.onNext(list);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<P2PFileInfo>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(List<P2PFileInfo> p2PFileInfos) {
                        Cache.selectedList.addAll(p2PFileInfos);
                        RadarScanActivity.launch(DiaryShareActivity.this, name);
                        finish();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    @Event(R.id.btn_receive)
    private void receiveClick(View view) {
        ReceiveActivity.launch(this);
        finish();
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, DiaryShareActivity.class);
        context.startActivity(intent);
    }
}
