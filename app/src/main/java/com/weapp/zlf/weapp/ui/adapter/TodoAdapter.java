package com.weapp.zlf.weapp.ui.adapter;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.daimajia.swipe.SwipeLayout;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.TodoBean;
import com.weapp.zlf.weapp.common.utils.ToastUtils;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.activity.TodoEditActivity;
import com.weapp.zlf.weapp.ui.widge.TagDetailDialog;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuliangfei on 2018/1/12.
 */

public class TodoAdapter extends BaseQuickAdapter<TodoBean, BaseViewHolder>{
    public TodoAdapter(List<TodoBean> data) {
        super(R.layout.item_todo, data);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final TodoBean item) {
        final TextView tvTitle = helper.getView(R.id.tv_title);
//        helper.setText(R.id.tv_title, item.getTitle());
//        helper.setText(R.id.tv_content, item.getContent());
        helper.setText(R.id.tv_time, item.getTime());
        helper.setBackgroundColor(R.id.v_tag, item.getTagColor());
        helper.setText(R.id.tv_tag, item.getTagName());
        helper.setText(R.id.tv_date, item.getDate());

        final SwipeLayout root = helper.getView(R.id.item_root);
        root.addDrag(SwipeLayout.DragEdge.Right, helper.getView(R.id.bottom_wrapper));
        if (item.getIsDone() == 1) {
            SpannableString spannableString = new SpannableString(" " + item.getTitle() + " ");
            spannableString.setSpan(new StrikethroughSpan(), 0, item.getTitle().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTitle.setText(spannableString);
//            root.setAlpha(0.5f);
        } else {
            tvTitle.setText(item.getTitle());
//            root.setAlpha(1f);
        }

        helper.getView(R.id.star).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoEditActivity.launch(mContext, TodoEditActivity.TYPE_TODO, item);
                root.close();
            }
        });
        helper.getView(R.id.trash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.close();
                if (item.getIsDone() == 1) {
                    item.setIsDone(0);
                    tvTitle.setText(item.getTitle());

//                    root.setAlpha(1f);
                } else {
//                    root.setAlpha(0.5f);
                    SpannableString spannableString = new SpannableString(" " + item.getTitle() + " ");
                    spannableString.setSpan(new StrikethroughSpan(), 0, item.getTitle().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tvTitle.setText(spannableString);
                    item.setIsDone(1);
                }
                setDbDoneField(item);
            }
        });
        root.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TagDetailDialog.Builder(mContext)
                        .setTitle(item.getTitle())
                        .setContent(item.getContent())
                        .setTime(item.getTimeMillis())
                        .setIsDone(item.getIsDone())
                        .setTagColor(item.getTagColor())
                        .show();
            }
        });
    }

    private void setDbDoneField(final TodoBean bean) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                WhereBuilder whereBuilder = WhereBuilder.b("id", "=", bean.getId());
                KeyValue value = new KeyValue("is_done", bean.getIsDone());
                dbManager.update(TodoBean.class, whereBuilder, value);
                observableEmitter.onNext(bean.getIsDone() == 1);
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                if (aBoolean)
                    ToastUtils.showLongToast("大吉大利，终于完成啦。。。");
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
