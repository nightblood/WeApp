package com.weapp.zlf.weapp.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zhuliangfei on 2018/2/28.
 */

@Table(name = "tb_habit")
public class HabitBean {
    public HabitBean() {

    }
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;
    @Column(name = "time_millis")
    private long timeMillis;
    @Column(name = "title")
    private String title;
    @Column(name = "minute_per_time")
    private int minutePerTime;
    @Column(name = "tomato_total_num")
    private int tomatoTotalNum;
    @Column(name = "tomato_total_time")
    private long tomatoTotalTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getMinutePerTime() {
        return minutePerTime;
    }

    public void setMinutePerTime(int minutePerTime) {
        this.minutePerTime = minutePerTime;
    }

    public int getTomatoTotalNum() {
        return tomatoTotalNum;
    }

    public void setTomatoTotalNum(int tomatoTotalNum) {
        this.tomatoTotalNum = tomatoTotalNum;
    }

    public long getTomatoTotalTime() {
        return tomatoTotalTime;
    }

    public void setTomatoTotalTime(long tomatoTotalTime) {
        this.tomatoTotalTime = tomatoTotalTime;
    }
}
