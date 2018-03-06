package com.weapp.zlf.weapp.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zhuliangfei on 2018/2/28.
 * 某一天的用户习惯数据
 */


@Table(name = "tb_habit_user")
public class HabitUserBean {
    public HabitUserBean() {

    }
    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;

    @Column(name = "habit_id")
    private int habitId;

    @Column(name = "time_millis")
    private long timeMillis;
    @Column(name = "tomato_num")
    private int tomatoNum;
    @Column(name = "tomato_time")
    private long tomatoTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public int getTomatoNum() {
        return tomatoNum;
    }

    public void setTomatoNum(int tomatoNum) {
        this.tomatoNum = tomatoNum;
    }

    public long getTomatoTime() {
        return tomatoTime;
    }

    public void setTomatoTime(long tomatoTime) {
        this.tomatoTime = tomatoTime;
    }
}
