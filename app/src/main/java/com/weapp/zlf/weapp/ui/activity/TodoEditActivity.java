package com.weapp.zlf.weapp.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import com.weapp.zlf.weapp.common.utils.AssertUtils;
import com.weapp.zlf.weapp.common.utils.TimeUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.event.AnniversaryEvent;
import com.weapp.zlf.weapp.ui.widge.KeyBoardDialog;
import com.weapp.zlf.weapp.ui.widge.RoundTextView;

import org.greenrobot.eventbus.EventBus;
import org.xutils.DbManager;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import cn.qqtheme.framework.picker.DatePicker;
import cn.qqtheme.framework.picker.DateTimePicker;
import cn.qqtheme.framework.picker.DoublePicker;
import cn.qqtheme.framework.picker.TimePicker;
import cn.qqtheme.framework.util.ConvertUtils;
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
    private AnniversaryBean mAnnivsaryData;
    private TodoBean mTodoBean;

    @Override
    protected void initView() {
        super.initView();
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", TYPE_TODO);

        Serializable data = intent.getSerializableExtra("data_anniversary");

        if (data != null) {
            if (mType == TYPE_TODO) {
                mTodoBean = (TodoBean) data;
                mTagColor = "#" + Integer.toHexString(mTodoBean.getTagColor());
                if (!TextUtils.isEmpty(mTodoBean.getContent())) {
                    mEtContent.setText(mTodoBean.getContent());
                    mEtContent.setSelection(mTodoBean.getContent().length());
                }
                mTimeMills = mTodoBean.getTimeMillis();
                mTvTodo.setText(mTodoBean.getTitle());
                mRtvTag.setText(mTodoBean.getTagName());
                mTvTime.setText(TimeUtils.date2String(new Date(mTimeMills), TimeUtils.DEFAULT_PATTERN));
            } else {
                mAnnivsaryData = (AnniversaryBean) data;
                mTagColor = "#" + Integer.toHexString(mAnnivsaryData.getTagColor());
                if (!TextUtils.isEmpty(mAnnivsaryData.getContent())) {
                    mEtContent.setText(mAnnivsaryData.getContent());
                    mEtContent.setSelection(mAnnivsaryData.getContent().length());
                }
                mTimeMills = mAnnivsaryData.getTimeMillis();
                mTvTodo.setText(mAnnivsaryData.getName());
                mRtvTag.setText(mAnnivsaryData.getTagName());
                mTvTime.setText(TimeUtils.date2String(new Date(mTimeMills), "MM-dd"));
            }
        } else {
            mTimeMills = System.currentTimeMillis();
            if (mType == TYPE_TODO) {
                mTvTime.setText(TimeUtils.date2String(new Date(mTimeMills), TimeUtils.DEFAULT_PATTERN));
                mTvTodo.setHint(getString(R.string.title_todo));
                mRtvTag.setText("备");
            } else {
                mTvTodo.setHint(getString(R.string.title_anniversary));
                mRtvTag.setText("纪");
                mTvTime.setText(TimeUtils.date2String(new Date(mTimeMills), "MM-dd"));
            }
        }

        if (mType == TYPE_TODO) {
            mTvTitle.setText(getString(R.string.title_todo));
        } else {
            mTvTitle.setText(getString(R.string.title_anniversary));
        }
        mRtvTag.setColor(Color.parseColor(mTagColor));
    }

    @Event(R.id.ll_name)
    private void editNameClick(View view) {
        KeyBoardDialog.Builder builder = new KeyBoardDialog.Builder(this);
        builder.setOnClickListener(new KeyBoardDialog.ClickListener() {
            @Override
            public void onClick(String s) {
                if (!TextUtils.isEmpty(s)) {
                    mTvTodo.setText(s);
                    mRtvTag.setText(s.substring(0, 1));
                }
            }
        })
                .setContent(mTvTodo.getText().toString())
                .setTitle("名字")
                .show();
    }
    @Event(R.id.ll_tag)
    private void editTagClick(View view) {
        String string = mTvTodo.getText().toString();
        if (TextUtils.isEmpty(string))
            return;
        final ArrayList<String> firstData = new ArrayList<>();
        for (int i = 0; i < string.length(); i++) {
            firstData.add(string.substring(i, i + 1));
        }

        final DoublePicker picker = new DoublePicker(this, firstData, AssertUtils.tagColorList);
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
                mTagColor = AssertUtils.tagColorList.get(selectedSecondIndex);
                mRtvTag.setColor(Color.parseColor(mTagColor));
            }
        });
        picker.show();
    }
    @Event(R.id.iv_title_left)
    private void finishClickClick(View view) {
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
                AnniversaryBean bean;
                if (mAnnivsaryData == null) {
                    bean = new AnniversaryBean();
                } else {
                    bean = mAnnivsaryData;
                }
                bean.setName(anniversaryName);
                bean.setContent(mEtContent.getText().toString());
                bean.setGender(0);
                bean.setTagColor(Color.parseColor(mTagColor));
                bean.setCreateTimeMillis(System.currentTimeMillis());
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
                            EventBus.getDefault().post(new AnniversaryEvent());
                            dbManager.saveOrUpdate(bean);
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
                TodoBean bean;
                if (mTodoBean == null) {
                    bean = new TodoBean();
                } else {
                    bean = mTodoBean;
                }
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
                            dbManager.saveOrUpdate(todoBean);
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

    private void onMonthDayPicker() {
        DatePicker picker = new DatePicker(this, DatePicker.MONTH_DAY);
        picker.setUseWeight(false);
        picker.setTextPadding(ConvertUtils.toPx(this, 15));//加宽显示项
        picker.setGravity(Gravity.BOTTOM);
        picker.setRangeStart(1, 1);
        picker.setRangeEnd(12, 31);
        picker.setSelectedItem(1, 1);
        picker.setOnDatePickListener(new DatePicker.OnMonthDayPickListener() {
            @Override
            public void onDatePicked(String month, String day) {

                String[] split = TimeUtils.date2String(new Date(System.currentTimeMillis()), "yyyy-MM-dd-HH-mm").split("-");
                String string = (split[0] + "-" + month + "-" + day + " " + split[3] + ":" + split[4]);
                mTvTime.setText(month + "-" + day);
                mTimeMills = TimeUtils.string2Millis(string, "yy-MM-dd HH:mm");
            }
        });
        picker.show();
    }
    private void onYearMonthDayPicker() {
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
        @Event(value = R.id.ll_time)
    private void pickTimeClick(View view) {
        if (mType == TYPE_TODO) {
            onYearMonthDayPicker();
        } else {
            onMonthDayPicker();
        }
    }

    public static void launch(Context context, int type, AnniversaryBean bean) {
        Intent intent = new Intent(context, TodoEditActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data_anniversary", bean);
        context.startActivity(intent);
    }
    public static void launch(Context context, int type, TodoBean bean) {
        Intent intent = new Intent(context, TodoEditActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("data_todo", bean);
        context.startActivity(intent);
    }

    public static void launch(Context context, int type) {
        Intent intent = new Intent(context, TodoEditActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }
}
