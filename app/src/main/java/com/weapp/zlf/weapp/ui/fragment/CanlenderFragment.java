package com.weapp.zlf.weapp.ui.fragment;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.bean.TodoBean;
import com.weapp.zlf.weapp.common.utils.Utils;
import com.weapp.zlf.weapp.ui.activity.TodoEditActivity;
import com.weapp.zlf.weapp.ui.adapter.TodoAdapter;
import com.weapp.zlf.weapp.ui.widge.TagDetailDialog;

import org.xutils.DbManager;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by zhuliangfei on 2018/1/8.
 */

@ContentView(R.layout.frag_canlender)
public class CanlenderFragment extends BaseFragment implements CalendarView.OnDateSelectedListener, CalendarView.OnYearChangeListener{

    private static final String TAG= CanlenderFragment.class.getSimpleName();
    @ViewInject(R.id.calendarView)
    CalendarView mCalendarView;
    @ViewInject(R.id.tv_lunar)
    TextView mTextLunar;
    @ViewInject(R.id.tv_year)
    TextView mTextYear;
    @ViewInject(R.id.tv_month_day)
    TextView mTextMonthDay;
    @ViewInject(R.id.tv_current_day)
    TextView mTextCurrentDay;

    @ViewInject(R.id.rv_todo)
    RecyclerView mRvTodo;
    private int mYear;
    private int mOffset;
    private TodoAdapter mTodoAdapter;
    private String mMonthKey;

    public static Fragment newInstance() {
        CanlenderFragment fragment = new CanlenderFragment();
        return fragment;
    }

    protected void initView() {
        initAdapter();
        initCalenter();
    }

    private void initCalenter() {
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnDateSelectedListener(this);
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));
//        mMonthKey = getMonthKey(mCalendarView.getCurYear(), mCalendarView.getCurMonth());
//        getCalenterTagData(mMonthKey);
    }

    private String getMonthKey(int year, int month) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(year).append("-");
        if (month < 10) {
            keyBuilder.append("0");
        }
        keyBuilder.append(month);
        return keyBuilder.toString();
    }

    private void getCalenterTagData(final String key) {
        Observable.create(new ObservableOnSubscribe<List<TodoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TodoBean>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                Cursor cursor = dbManager.execQuery("select title, content, date, tag_name, tag_color, time_millis, gender, is_done, max(id) maxid from tb_todo where date like '%"+key+"%' group by date");
                ArrayList<TodoBean> list = new ArrayList<>();
                while (cursor.moveToNext()) {
                    Log.d(TAG, "getCalenterTagData: " + cursor.getString(cursor.getColumnIndex("title"))
                    + ", " + cursor.getString(cursor.getColumnIndex("date")));
                    TodoBean bean = new TodoBean();
                    bean.setTitle(cursor.getString(cursor.getColumnIndex("title")));
                    bean.setTagName(cursor.getString(cursor.getColumnIndex("tag_name")));
                    bean.setContent(cursor.getString(cursor.getColumnIndex("content")));
                    bean.setTimeMillis(cursor.getLong(cursor.getColumnIndex("time_millis")));
                    bean.setTagColor(cursor.getInt(cursor.getColumnIndex("tag_color")));
                    bean.setIsDone(cursor.getInt(cursor.getColumnIndex("is_done")));
                    bean.setGender(cursor.getInt(cursor.getColumnIndex("gender")));
                    list.add(bean);
                }
                observableEmitter.onNext(list);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Observer<List<TodoBean>>() {
            @Override
            public void onSubscribe(Disposable disposable) {

            }

            @Override
            public void onNext(List<TodoBean> todoBeans) {
                updateTagOnCalender(todoBeans);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void updateTagOnCalender(List<TodoBean> list) {
        final List<Calendar> schemes = new ArrayList<>();

        for (TodoBean bean : list) {
            String[] split = bean.getDate().split("-");

            schemes.add(getSchemeCalendar
                    (Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), bean.getTagColor(), bean.getTagName()));
        }
        mCalendarView.setSchemeDate(schemes);
    }

    private void initAdapter() {
        mRvTodo.setLayoutManager(new LinearLayoutManager(getContext()));
        mTodoAdapter = new TodoAdapter(null);
        mRvTodo.setAdapter(mTodoAdapter);
        View header = LayoutInflater.from(getContext()).inflate(R.layout.ui_calender_list_header, null);
        header.findViewById(R.id.iv_add_todo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoEditActivity.launch(getContext());
            }
        });
        mTodoAdapter.addHeaderView(header);
    }

    private void getTodoListData(final String date) {
        Observable.create(new ObservableOnSubscribe<List<TodoBean>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TodoBean>> observableEmitter) throws Exception {
                DbManager dbManager = Utils.getContext().getDbManager();
                List<TodoBean> list = dbManager.selector(TodoBean.class)
                        .orderBy("id", true)
                        .where("date", "=", date)
                        .offset(mOffset)
                        .findAll();
                if (list != null) {
                    observableEmitter.onNext(list);
                    mOffset += list.size();
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<TodoBean>>() {
                    @Override
                    public void onSubscribe(Disposable disposable) {

                    }

                    @Override
                    public void onNext(List<TodoBean> todoBeans) {
                        mTodoAdapter.setNewData(todoBeans);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }

    @Event(value = R.id.tv_month_day)
    private void monthDayClick(View view) {
        mCalendarView.showSelectLayout(mYear);
        mTextLunar.setVisibility(View.GONE);
        mTextYear.setVisibility(View.GONE);
        mTextMonthDay.setText(String.valueOf(mYear));
    }

    @Override
    public void onDateSelected(Calendar calendar, boolean b) {
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();

        String monthKey = getMonthKey(calendar.getYear(), calendar.getMonth());
        Log.d(TAG, "onDateSelected: " +monthKey + ", " + mMonthKey);
        if (!monthKey.equals(mMonthKey)) {
            mMonthKey = monthKey;
            getCalenterTagData(mMonthKey);
        }

        mOffset = 0;
        final StringBuilder dbDate = new StringBuilder("");
        dbDate.append(calendar.getYear()).append("-");
        if (calendar.getMonth() < 10) {
            dbDate.append("0").append(calendar.getMonth());
        } else {
            dbDate.append(calendar.getMonth());
        }
        dbDate.append("-");
        if (calendar.getDay() < 10) {
            dbDate.append("0").append(calendar.getDay());
        } else {
            dbDate.append(calendar.getDay());
        }
        getTodoListData(dbDate.toString());
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));

    }

    @Event(value = R.id.fl_current)
    private void toToday(View view) {
        mCalendarView.scrollToCurrent();
    }

}
