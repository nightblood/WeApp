package com.weapp.zlf.weapp.ui.widge;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.HabitBean;
import com.weapp.zlf.weapp.common.utils.KeyboardUtils;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;

import org.xutils.DbManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuliangfei on 2018/2/27.
 */

public class HabitAddDialog extends Dialog implements View.OnClickListener {
    private EditText etMin;
    private EditText etContent;
    private TextView tv20;
    private TextView tv30;
    private TextView tv40;
    private int mMin = 20;

    public HabitAddDialog(@NonNull Context context) {
        super(context, R.style.ActionSheetDialogStyle);
        initView(context);
    }

    private void initView(Context context) {
        setContentView(R.layout.dialog_habit);

        etContent = (EditText) findViewById(R.id.et_content);
        etMin = (EditText) findViewById(R.id.et_min);
        tv20 = (TextView) findViewById(R.id.tv_20_min);
        tv30 = (TextView) findViewById(R.id.tv_30_min);
        tv40 = (TextView) findViewById(R.id.tv_40_min);
        findViewById(R.id.iv_close).setOnClickListener(this);
        findViewById(R.id.iv_submit).setOnClickListener(this);
        tv20.setOnClickListener(this);
        tv30.setOnClickListener(this);
        tv40.setOnClickListener(this);
        tv20.setSelected(true);
        etMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                tv20.setSelected(false);
                tv30.setSelected(false);
                tv40.setSelected(false);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()))
                    mMin = Integer.parseInt(s.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_20_min:
                tv20.setSelected(true);
                tv30.setSelected(false);
                tv40.setSelected(false);
                etMin.setSelected(false);
                KeyboardUtils.closeKeybord(etMin, getContext());
                mMin = 20;
                break;
            case R.id.tv_30_min:
                tv20.setSelected(false);
                tv30.setSelected(true);
                tv40.setSelected(false);
                etMin.setSelected(false);
                KeyboardUtils.closeKeybord(etMin, getContext());
                mMin = 30;
                break;
            case R.id.tv_40_min:
                tv20.setSelected(false);
                tv30.setSelected(false);
                tv40.setSelected(true);
                etMin.setSelected(false);
                KeyboardUtils.closeKeybord(etMin, getContext());
                mMin = 40;
                break;
            case R.id.iv_close:
                dismiss();
                break;
            case R.id.iv_submit:
                submit();
                break;
            default:
                break;
        }
    }

    private void submit() {
        final String name = etContent.getText().toString();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShortToast("您还未填写名称");
            return;
        }
        Observable.create(new ObservableOnSubscribe<HabitBean>() {
            @Override
            public void subscribe(ObservableEmitter<HabitBean> observableEmitter) throws Exception {
                HabitBean habitBean = new HabitBean();
                habitBean.setTitle(name);
                habitBean.setTimeMillis(System.currentTimeMillis());
                habitBean.setMinutePerTime(mMin);
                DbManager dbManager = Utils.getContext().getDbManager();
                dbManager.saveBindingId(habitBean);
                observableEmitter.onNext(habitBean);
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<HabitBean>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(HabitBean habitBean) {
                        ToastUtils.showShortToast("add success");
                        dismiss();
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
