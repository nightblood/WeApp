package com.weapp.zlf.weapp.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.MaterialDialog;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.AnniversaryBean;
import com.weapp.zlf.weapp.bean.TodoBean;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.widge.KeyBoardDialog;
import com.weapp.zlf.weapp.ui.widge.RoundTextView;

import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.Date;

import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.DoublePicker;
import cn.qqtheme.framework.picker.TimePicker;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuliangfei on 2018/1/15.
 */

@ContentView(R.layout.activity_todo_edit)
public class TodoEditActivity extends BaseActivity{

    private static final String TAG = TodoEditActivity.class.getSimpleName();
    public static final int TYPE_ANNIVERSARY = 1; //纪念日
    public static final int TYPE_TODO = 2; //备忘录
    @ViewInject(R.id.et_content)
    private EditText mEtContent;
    @ViewInject(R.id.tv_title_name)
    private TextView mTvTitle;
    @ViewInject(R.id.tv_name)
    private TextView mTvTodo;
    @ViewInject(R.id.rtv_name)
    private RoundTextView mRtvTag;
    private String mTagColor = "#7c8489";
    private String mTag;
    private long mTimeMills;
    @ViewInject(R.id.tv_time)
    private TextView mTvTime;
    private int mType;

    @Override
    protected void initView() {
        super.initView();
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", TYPE_TODO);
        if (mType == TYPE_TODO) {
            mTvTitle.setText(getString(R.string.title_todo));
            mTvTodo.setHint(getString(R.string.title_todo));
            mRtvTag.setText("备");
        } else {
            mTvTitle.setText(getString(R.string.title_anniversary));
            mTvTodo.setHint(getString(R.string.title_anniversary));
            mRtvTag.setText("纪");
        }
        mRtvTag.setColor(Color.parseColor(mTagColor));
        mTimeMills = System.currentTimeMillis();
        mTvTime.setText(TimeUtils.date2String(new Date(mTimeMills), TimeUtils.DEFAULT_PATTERN));
    }

    @Event(R.id.ll_name)
    private void editName(View view) {
        KeyBoardDialog.Builder builder = new KeyBoardDialog.Builder(this);
        builder.setOnClickListener(new KeyBoardDialog.ClickListener() {
            @Override
            public void onClick(String s) {
                if (!TextUtils.isEmpty(s)) {
                    mTvTodo.setText(s);
                    mRtvTag.setText(s.substring(0, 1));
                }
            }
        }).show();
    }
    @Event(R.id.ll_tag)
    private void editTag(View view) {
        String string = mTvTodo.getText().toString();
        if (TextUtils.isEmpty(string))
            return;
        final ArrayList<String> firstData = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            firstData.add(string.substring(i, i + 1));
        }

        final ArrayList<String> secondData = new ArrayList<>();
        secondData.add("#7c8489");
        secondData.add("#4fb3a4");
        secondData.add("#ff7073");
        secondData.add("#f5b977");
        secondData.add("#fdfc7f");
        final DoublePicker picker = new DoublePicker(this, firstData, secondData);
        picker.setDividerVisible(true);
        picker.setCycleDisable(true);
        picker.setSelectedIndex(0, 0);
        picker.setFirstLabel("标签", null);
        picker.setSecondLabel("颜色", null);
        picker.setTextSize(14);
        picker.setContentPadding(15, 10);
        picker.setOnPickListener(new DoublePicker.OnPickListener() {
            @Override
            public void onPicked(int selectedFirstIndex, int selectedSecondIndex) {
                Log.d(TAG, "onPicked: " + selectedSecondIndex);
                mTag = firstData.get(selectedFirstIndex);
                mRtvTag.setText(mTag);
                mTagColor = secondData.get(selectedSecondIndex);
                mRtvTag.setColor(Color.parseColor(mTagColor));
            }
        });
        picker.show();
    }
    @Event(R.id.iv_title_left)
    private void finishClick(View view) {
        finish();
    }
    @Event(R.id.iv_title_right)
    private void submit(View view) {
        final String todoName = mTvTodo.getText().toString();
        if (TextUtils.isEmpty(todoName)) {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog
                    .btnNum(1)
                    .content("您还未填写任何内容。。。")//
                    .btnText("确定")//
                    .show();

            dialog.setOnBtnClickL(new OnBtnClickL() {
                @Override
                public void onBtnClick() {
                    dialog.dismiss();
                }
            });
        } else {
            if (TYPE_ANNIVERSARY == mType) {
                submitAnniversary(todoName);
            } else {
                submitTodo(todoName);
            }

        }
    }

    private void submitAnniversary(final String anniversaryName) {
        Observable.create(new ObservableOnSubscribe<AnniversaryBean>() {
            @Override
            public void subscribe(ObservableEmitter<AnniversaryBean> observableEmitter) throws Exception {
                AnniversaryBean bean = new AnniversaryBean();
                bean.setName(anniversaryName);
                bean.setContent(mEtContent.getText().toString());
                bean.setGender(0);
                bean.setTagColor(Color.parseColor(mTagColor));
                if (TextUtils.isEmpty(mTag)) {
                    bean.setTagName(anniversaryName.substring(0, 1));
                } else {
                    bean.setTagName(mTag);
                }
                bean.setTimeMillis(mTimeMills);
                observableEmitter.onNext(bean);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<AnniversaryBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onNext(AnniversaryBean bean) {
                        DbManager dbManager = Utils.getContext().getDbManager();
                        try {
                            dbManager.save(bean);
                            finish();
                        } catch (DbException e) {
                            e.printStackTrace();
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

    private void submitTodo(final String todoName) {
        Observable.create(new ObservableOnSubscribe<TodoBean>() {
            @Override
            public void subscribe(ObservableEmitter<TodoBean> observableEmitter) throws Exception {
                TodoBean bean = new TodoBean();
                bean.setTitle(todoName);
                bean.setContent(mEtContent.getText().toString());
                bean.setGender(0);
                bean.setTagColor(Color.parseColor(mTagColor));
                if (TextUtils.isEmpty(mTag)) {
                    bean.setTagName(todoName.substring(0, 1));
                } else {
                    bean.setTagName(mTag);
                }
                bean.setTimeMillis(mTimeMills);
                observableEmitter.onNext(bean);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<TodoBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {
                    }

                    @Override
                    public void onNext(TodoBean todoBean) {
                        DbManager dbManager = Utils.getContext().getDbManager();
                        try {
                            dbManager.save(todoBean);
                            finish();
                        } catch (DbException e) {
                            e.printStackTrace();
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

    @Event(value = R.id.ll_time)
    private void pickTime(View view) {
        String string = TimeUtils.date2String(new Date(System.currentTimeMillis()), TimeUtils.DEFAULT_PATTERN);
        String substring = string.substring(0, string.indexOf(" "));
        String[] split = substring.split("-");
        DateTimePicker picker = new DateTimePicker(this, DateTimePicker.HOUR_24);
        picker.setDateRangeStart(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
        picker.setDateRangeEnd(2025, 11, 11);
        picker.setTimeRangeStart(9, 0);
        picker.setTimeRangeEnd(20, 30);
        picker.setTopLineColor(0x99FF0000);
        picker.setLabelTextColor(0xFFFF0000);
        picker.setDividerColor(0xFFFF0000);
        picker.setOnDateTimePickListener(new DateTimePicker.OnYearMonthDayTimePickListener() {
            @Override
            public void onDateTimePicked(String year, String month, String day, String hour, String minute) {
                String string = (year + "-" + month + "-" + day + " " + hour + ":" + minute);
                mTvTime.setText(string);
                mTimeMills = TimeUtils.string2Millis(string, "yy-MM-dd HH:mm");
            }
        });
        picker.show();
    }

    public static void launch(Context context) {
        launch(context, TodoEditActivity.TYPE_TODO);
    }

    public static void launch(Context context, int type) {
        Intent intent = new Intent(context, TodoEditActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
}
