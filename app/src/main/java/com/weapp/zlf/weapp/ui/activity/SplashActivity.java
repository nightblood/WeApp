package com.weapp.zlf.weapp.ui.activity;

import android.Manifest;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.Constant;
import com.weapp.zlf.weapp.common.utils.FileUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.widge.GyroscopeObserver;
import com.weapp.zlf.weapp.ui.widge.PanoramaImageView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.List;
import java.util.Random;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhuliangfei on 2018/3/6.
 */

@ContentView(R.layout.activity_splash)
public class SplashActivity extends BaseActivity {
    @ViewInject(R.id.panorama_image_view)
    private PanoramaImageView mIvImage;
    private GyroscopeObserver gyroscopeObserver;

    @Override
    protected void initView() {
        super.initView();

        gyroscopeObserver = new GyroscopeObserver();
        // Set the maximum radian the device should rotate to show image's bounds.
        // It should be set between 0 and π/2.
        // The default value is π/9.
//        gyroscopeObserver.setMaxRotateRadian(Math.PI / 9);
        // Set GyroscopeObserver for PanoramaImageView.
        mIvImage.setGyroscopeObserver(gyroscopeObserver);
        new RxPermissions(this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            Utils.getContext().initDb();
                            initWallpaper();
                            x.task().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    MainActivity.launch(SplashActivity.this);
                                }
                            }, 5000);
                        } else {
                            ToastUtils.showShortToast(getString(R.string.hint_write_permission_dinied));
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register GyroscopeObserver.
        gyroscopeObserver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister GyroscopeObserver.
        gyroscopeObserver.unregister();
    }

    private void initWallpaper() {
//        List<File> images = FileUtils.listFilesInDir(Constant.DIR_WALLPAPER);
//        RequestOptions options = new RequestOptions()
//                .centerCrop()
//                .placeholder(R.color.color_background)
//                .error(R.color.color_background)
//                .priority(Priority.HIGH)
//                .transform(new MultiTransformation<>(new CenterCrop()));
//                .transform(new MultiTransformation<>(new CenterCrop(), new ColorFilterTransformation(Color.argb(50, 0, 0, 0))));
//        if (images == null || images.isEmpty()) {
//            Glide.with(this).load(R.drawable.huolieniao).apply(options).into(mIvImage);
//        } else {
//            Glide.with(this).load(images.get(new Random().nextInt(images.size())).getAbsolutePath()).apply(options).into(mIvImage);
//            Glide.with(this).load(R.drawable.test3).into(mIvImage);
//        }


    }
}
