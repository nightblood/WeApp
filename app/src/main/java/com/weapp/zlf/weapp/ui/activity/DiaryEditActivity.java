package com.weapp.zlf.weapp.ui.activity;

import android.content.Context;
import android.content.Intent;
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
import com.weapp.zlf.weapp.common.utils.AssertUtils;
import com.weapp.zlf.weapp.common.utils.Constant;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.event.DiaryEvent;
import com.weapp.zlf.weapp.ui.adapter.DiaryEditPhotoAdapter;
import com.weapp.zlf.weapp.ui.adapter.PanelAdapter;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static cn.pedant.SweetAlert.SweetAlertDialog.PROGRESS_TYPE;

/**
 * Created by zhuliangfei on 2018/1/9.
 */

@ContentView(R.layout.activity_diary_edit)
public class DiaryEditActivity extends BaseActivity {
    @ViewInject(R.id.rv_photos)
    private RecyclerView mRvPhotos;
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
//    @ViewInject(R.id.iv_image)
//    private ImageView mIvImage;
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
    private boolean isEditMode;
    private DiaryBean mData;
    private DiaryEditPhotoAdapter mPhotoAdapter;
    private SweetAlertDialog mLubanDialog;

    @Override
    protected void initView() {
        super.initView();

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

        initPhotosList();
        Intent intent = getIntent();
        Serializable data = intent.getSerializableExtra("data");

        if (data != null) {
            isEditMode = true;

            mData = (DiaryBean) data;
            StringBuilder builder = new StringBuilder();
            builder.append(mData.getYear()).append("-").append(mData.getMonth()).append("-").append(mData.getDate());
            mTvTitle.setText(builder.toString());

                mImages = getImageData(mData.getImages());
                mPhotoAdapter.setNewData(mImages);

            if (Integer.MAX_VALUE != mData.getWeather()) {
                mWeather = mData.getWeather();
                mIvWeather.setImageResource(AssertUtils.weatherlist.get(mData.getWeather()));
            }
            if (Integer.MAX_VALUE != mData.getMood()) {
                mMood = mData.getMood();
                mIvMood.setImageResource(AssertUtils.moodlist.get(mData.getMood()));
            }
            if (Integer.MAX_VALUE != mData.getTag()) {
                mTag = mData.getTag();
                mIvTag.setImageResource(AssertUtils.taglist.get(mData.getTag()));
            }
            mEtContent.setText(mData.getContent());
            mEtContent.setSelection(mData.getContent().length());
            mTvTime.setText(TimeUtils.date2String(new Date(mData.getTimeMillis()), "yyyy/MM/dd hh:mm:ss"));
        } else {
            mTvTitle.setText(getString(R.string.diary));
            isEditMode = true;
            mPhotoAdapter.setNewData(getImageData(null));
        }
    }

    private ArrayList<String> getImageData(List<String> images) {
        ArrayList<String> list = new ArrayList<>();
        if (images == null || images.isEmpty()) {
        } else {
            for (String image : images) {
                list.add(image);
            }
        }
        list.add("btn_add");
        return list;
    }

    private void initPhotosList() {

        mPhotoAdapter = new DiaryEditPhotoAdapter(null);
        mRvPhotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRvPhotos.setAdapter(mPhotoAdapter);

//        final View header = LayoutInflater.from(this).inflate(R.layout.item_diary_edit_photo, null);
//        ImageView imageView = (ImageView) header.findViewById(R.id.iv_item);
//        imageView.setImageResource(R.drawable.icon_add);
//        mPhotoAdapter.addFooterView(header);
        mPhotoAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if ((adapter.getItem(position)).equals("btn_add")) {
                    photoPick();
                } else {
                    photoPreview(getPreviewImageData(mImages), position);
                }
            }
        });
    }

    private ArrayList<String> getPreviewImageData(ArrayList<String> images) {
        ArrayList<String> res = new ArrayList<>();
        for (String item : images) {
            if (item.equals("btn_add"))
                continue;
            res.add(item);
        }
        return res;
    }

    private void initPanel() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRvMood.setLayoutManager(linearLayoutManager);
        mRvWeather.setLayoutManager(linearLayoutManager1);
        mRvTag.setLayoutManager(linearLayoutManager2);
        PanelAdapter moodAdapter = new PanelAdapter(AssertUtils.moodlist);
        PanelAdapter weatherAdapter = new PanelAdapter(AssertUtils.weatherlist);
        PanelAdapter tagAdapter = new PanelAdapter(AssertUtils.taglist);
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
    private void viewClick(View view) {
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
                .setPhotoCount(10 - mImages.size())
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
                    public void onNext(String string) {
                        EventBus.getDefault().post(new DiaryEvent());
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
                if (!(list == null || list.isEmpty())) { //删除所有原有图片， 再插入图片
                    for (ImageBean bean : list) {
                        dbManager.delete(bean);
                    }
                }
                for (String image : images) {
                    if (image.equals("btn_add"))
                        continue;
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

        if (resultCode == RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                final List<String> images = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
                mLubanDialog = new SweetAlertDialog(DiaryEditActivity.this, PROGRESS_TYPE);
                mLubanDialog.setTitleText(String.format(getString(R.string.picture_compressing), 0, images.size()));
                mLubanDialog.show();
                Luban.with(this)
                        .load(images)                                   // 传人要压缩的图片列表
                        .ignoreBy(100)                                  // 忽略不压缩图片的大小
                        .setTargetDir(Constant.DIR_DIARY_PHOTO)                        // 设置压缩后文件存储位置
                        .setCompressListener(new OnCompressListener() { //设置回调
                            private ArrayList<String> files = new ArrayList<>();
                            @Override
                            public void onStart() {
                                // TODO 压缩开始前调用，可以在方法内启动 loading UI
                            }

                            @Override
                            public void onSuccess(File file) {
                                // TODO 压缩成功后调用，返回压缩后的图片文件
                                files.add(file.getAbsolutePath());
                                mLubanDialog.setTitleText(String.format(getString(R.string.picture_compressing), files.size(), images.size()));
                                if (files.size() == images.size()) {
                                    mLubanDialog.dismiss();
                                    mImages = addPhoto(mImages, files);
                                    mPhotoAdapter.setNewData(mImages);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.showShortToast(e.toString());
                                mLubanDialog.dismiss();
                            }
                        }).launch();    //启动压缩
            }
        } else if (resultCode == RESULT_OK && requestCode == PhotoPreview.REQUEST_CODE) {
            mImages.clear();
            mImages = addPhoto(mImages, data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS));
            mPhotoAdapter.setNewData(mImages);
        }

    }

    private ArrayList<String> addPhoto(ArrayList<String> oriList, ArrayList<String> addList) {
        ArrayList<String> res = new ArrayList<>();
        for (String item : oriList) {
            if (!item.equals("btn_add"))
                res.add(item);
        }
        res.addAll(addList);
        res.add("btn_add");
        return res;
    }

    private void photoPreview(ArrayList<String> photoPaths, int pos) {
        if (photoPaths == null || photoPaths.isEmpty())
            return;
        PhotoPreview.builder()
                .setPhotos(photoPaths)
                .setCurrentItem(pos)
                .setShowDeleteButton(isEditMode)
                .start(DiaryEditActivity.this);
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
