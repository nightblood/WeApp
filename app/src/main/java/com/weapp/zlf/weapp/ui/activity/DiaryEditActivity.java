package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.DiaryBean;
import com.weapp.zlf.weapp.bean.ImageBean;
import com.weapp.zlf.weapp.common.BannerImageLoader;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.adapter.PanelAdapter;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.transformer.DepthPageTransformer;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.dreamtobe.kpswitch.util.KPSwitchConflictUtil;
import cn.dreamtobe.kpswitch.util.KeyboardUtil;
import cn.dreamtobe.kpswitch.widget.KPSwitchFSPanelLinearLayout;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

/**
 * Created by zhuliangfei on 2018/1/9.
 */

@ContentView(R.layout.activity_diary_edit)
public class DiaryEditActivity extends BaseActivity implements OnBannerListener {
    @ViewInject(R.id.banner)
    private Banner mBanner;
    @ViewInject(R.id.et_content)
    private EditText mEtContent;
    @ViewInject(R.id.tv_time)
    private TextView mTvTime;
    @ViewInject(R.id.panel_root)
    private KPSwitchFSPanelLinearLayout mPanelRoot;
    @ViewInject(R.id.iv_mood)
    private ImageView mIvMood;
    @ViewInject(R.id.rv_mood)
    private RecyclerView mRvMood;
    @ViewInject(R.id.rv_weather)
    private RecyclerView mRvWeather;
    @ViewInject(R.id.iv_weather)
    private ImageView mIvWeather;
    @ViewInject(R.id.rv_tag)
    private RecyclerView mRvTag;
    @ViewInject(R.id.iv_image)
    private ImageView mIvImage;
    @ViewInject(R.id.iv_tag)
    private ImageView mIvTag;
    @ViewInject(R.id.iv_title_right)
    private ImageView mIvTitleRight;
    @ViewInject(R.id.tv_title_name)
    private TextView mTvTitle;

    private ArrayList<String> mImages = new ArrayList<>();
    private int mMood = Integer.MAX_VALUE;
    private int mWeather = Integer.MAX_VALUE;
    private String mTitle;
    private int mTag = Integer.MAX_VALUE;
    private ArrayList<Integer> taglist;
    private ArrayList<Integer> moodlist;
    private ArrayList<Integer> weatherlist;
    private boolean isEditMode;
    private DiaryBean mData;

    @Override
    protected void initView() {
        super.initView();

        Integer[] images = {R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};
        List<Integer> asList = Arrays.asList(images);
        mBanner.setImages(new ArrayList<Object>(asList))
                .setImageLoader(new BannerImageLoader())
                .setBannerStyle(BannerConfig.CIRCLE_INDICATOR)
                .setPageTransformer(false, new DepthPageTransformer())
                .setOnBannerListener(this);
        String time = TimeUtils.date2String(new Date(System.currentTimeMillis()), "yyyy/MM/dd hh:mm:ss");
        mTvTime.setText(time);
        KeyboardUtil.attach(this, mPanelRoot);
        KPSwitchConflictUtil.attach(mPanelRoot, mIvWeather, mEtContent,
                new KPSwitchConflictUtil.SwitchClickListener() {
                    @Override
                    public void onClickSwitch(boolean switchToPanel) {
                        if (switchToPanel) {
                            mEtContent.clearFocus();
                        } else {
                            mEtContent.requestFocus();
                        }
                    }
                });
        KPSwitchConflictUtil.attach(mPanelRoot, mIvMood, mEtContent, null);
        KPSwitchConflictUtil.attach(mPanelRoot, mIvTag, mEtContent, null);
        initPanel();

        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra("data");
        if (data != null) {
            isEditMode = true;

            mData = (DiaryBean) data;
            StringBuilder builder = new StringBuilder();
            builder.append(mData.getYear()).append("-").append(mData.getMonth()).append("-").append(mData.getDate());
            mTvTitle.setText(builder.toString());
            if (TextUtils.isEmpty(mData.getImageStr())) {
                mBanner.setVisibility(View.GONE);
            } else {
                mBanner.setVisibility(View.VISIBLE);
                mBanner.setImages(mImages = new ArrayList<>(mData.getImages()));
                mBanner.start();
            }
            if (Integer.MAX_VALUE != mData.getWeather()) {
                mWeather = mData.getWeather();
                mIvWeather.setImageResource(weatherlist.get(mData.getWeather()));
            }
            if (Integer.MAX_VALUE != mData.getMood()) {
                mMood = mData.getMood();
                mIvMood.setImageResource(moodlist.get(mData.getMood()));
            }
            if (Integer.MAX_VALUE != mData.getTag()) {
                mTag = mData.getTag();
                mIvTag.setImageResource(taglist.get(mData.getTag()));
            }
            mEtContent.setText(mData.getContent());
            mEtContent.setSelection(mData.getContent().length());
            mTvTime.setText(TimeUtils.date2String(new Date(mData.getTimeMillis()), "yyyy/MM/dd hh:mm:ss"));
        } else {
            isEditMode = true;
        }
    }

    private void initPanel() {
        TypedArray moodArray = getResources().obtainTypedArray(R.array.mood_emoji);
        TypedArray weatherArray = getResources().obtainTypedArray(R.array.weather);
        TypedArray tagArray = getResources().obtainTypedArray(R.array.tag);
        moodlist = new ArrayList<>();
        weatherlist = new ArrayList<>();
        taglist = new ArrayList<>();
        for (int i = 0; i < moodArray.length(); i++) {
            moodlist.add(moodArray.getResourceId(i, 0));
        }
        for (int i = 0; i < weatherArray.length(); i++) {
            weatherlist.add(weatherArray.getResourceId(i, 0));
        }
        for (int i = 0; i < tagArray.length(); i++) {
            taglist.add(tagArray.getResourceId(i, 0));
        }
        moodArray.recycle();
        weatherArray.recycle();
        tagArray.recycle();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvMood.setLayoutManager(linearLayoutManager);
        mRvWeather.setLayoutManager(linearLayoutManager1);
        mRvTag.setLayoutManager(linearLayoutManager2);
        PanelAdapter moodAdapter = new PanelAdapter(moodlist);
        PanelAdapter weatherAdapter = new PanelAdapter(weatherlist);
        PanelAdapter tagAdapter = new PanelAdapter(taglist);
        mRvMood.setAdapter(moodAdapter);
        mRvWeather.setAdapter(weatherAdapter);
        mRvTag.setAdapter(tagAdapter);

        moodAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mIvMood.setImageResource((int) adapter.getItem(position));
                mMood = position;
            }
        });
        weatherAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mIvWeather.setImageResource((int) adapter.getItem(position));
                mWeather = position;
            }
        });
        tagAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mIvTag.setImageResource((int) adapter.getItem(position));
                mTag = position;
            }
        });
    }

    @Event(value = {R.id.iv_title_left, R.id.iv_title_right, R.id.iv_image, R.id.iv_mood, R.id.iv_weather})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.iv_title_left:
                showDialog();
                break;
            case R.id.iv_title_right:
                submit();
                break;
            case R.id.iv_image:
                photoPick();
                break;
            case R.id.iv_mood:
                break;
            case R.id.iv_weather:
                break;
            default:
                break;
        }
    }

    private void photoPick() {
        PhotoPicker.builder()
                .setPhotoCount(9)
                .setShowCamera(true)
                .setShowGif(true)
                .setPreviewEnabled(false)
                .start(this, PhotoPicker.REQUEST_CODE);
    }

    private void submit() {
        if (TextUtils.isEmpty(mEtContent.getText().toString())) {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog
                    .btnNum(1)
                    .content("您还未填写任何内容。。。")//
                    .btnText("确定")//
//                    .showAnim(mBasIn)//
//                    .dismissAnim(mBasOut)//
                    .show();

            dialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                }
            });
            return;
        }

        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> observableEmitter) throws Exception {
                if (mData == null) {
                    mData = new DiaryBean();
                }

                mData.setContent(mEtContent.getText().toString());
                mData.setGender(0);
                mData.setImages(mImages);
                mData.setWeather(mWeather);
                mData.setMood(mMood);
                mData.setTitle(mTitle);
                mData.setFullTime();
                mData.setTag(mTag);
                String ids = updateImageTb(mData.getId(), mData.getImages());
                mData.setImageIds(ids);
                try {
                    DbManager dbManager = Utils.getContext().getDbManager();
                    dbManager.saveOrUpdate(mData);
                } catch (DbException e) {
                    e.printStackTrace();
                    observableEmitter.onError(e);
                }

                observableEmitter.onNext("");
                observableEmitter.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(String diaryBean) {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        finishAfterTransition();
                    }
                });

    }

    // 返回 image ids
    private String updateImageTb(int diaryId, List<String> images) {
        String ids = "";
        DbManager dbManager = Utils.getContext().getDbManager();
        try {
            List<ImageBean> list = dbManager.selector(ImageBean.class)
                    .where("diary_id", "=", diaryId)
                    .findAll();
            if (images == null || images.isEmpty()) {
                if (!(list == null || list.isEmpty())) { // 删除图片
                    for (ImageBean bean : list) {
                        dbManager.delete(bean);
                    }
                }
            } else {
                if (!(list == null || list.isEmpty())) { // 插入图片
                    for (ImageBean bean : list) {
                        dbManager.delete(bean);
                    }
                }
                for (String image : images) {
                    ImageBean imageBean = new ImageBean();
                    imageBean.setDiaryId(diaryId);
                    imageBean.setImage(image);
                    dbManager.saveBindingId(imageBean);
                    if (!TextUtils.isEmpty(ids))
                        ids += "&&";
                    ids += imageBean.getId();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return ids;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && (requestCode == PhotoPicker.REQUEST_CODE || requestCode == PhotoPreview.REQUEST_CODE)) {
            if (data != null) {
                mImages = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                mBanner.setImages(mImages);
                mBanner.setVisibility(View.VISIBLE);
                mBanner.start();
                mIvImage.setImageResource(R.drawable.icon_pic_select);
            }
        }
    }

    private void photoPreview(ArrayList<String> photoPaths, int pos) {
        PhotoPreview.builder()
                .setPhotos(photoPaths)
                .setCurrentItem(pos)
                .setShowDeleteButton(isEditMode)
                .start(DiaryEditActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开始轮播
        mBanner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //结束轮播
        mBanner.stopAutoPlay();
    }

    @Override
    public void OnBannerClick(int i) {
        photoPreview(mImages, i);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        /*if (isEditMode) {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog
                    .btnNum(1)
                    .content("真的不留下点什么吗。。。")//
                    .btnText("寡人去意已决")//
//                    .showAnim(mBasIn)//
//                    .dismissAnim(mBasOut)//
                    .show();

            dialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                    finishAfterTransition();
                }
            });
        } else {
            finishAfterTransition();
        }*/
        finishAfterTransition();
    }

    public static void launch(Context context, DiaryBean data) {
        Intent intent = new Intent(context, DiaryEditActivity.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }
}
