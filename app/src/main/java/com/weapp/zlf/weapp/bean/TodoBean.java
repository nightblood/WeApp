package com.weapp.zlf.weapp.bean;

import android.text.TextUtils;

import com.weapp.zlf.weapp.common.utils.TimeUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by zhuliangfei on 2018/1/12.
 */
@Table(name = "tb_todo")
public class TodoBean {
    @Column(name = "id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    @Column(name = "time_millis")
    private long timeMillis;
    @Column(name = "content")
    private String content;
    @Column(name = "title")
    private String title;
    @Column(name = "tag_color")
    private int tagColor;
    @Column(name = "tag_name")
    private String tagName;
    @Column(name = "gender")
    private int gender;
    @Column(name = "date")
    private String date;
    @Column(name = "is_done")
    private int isDone;
    @Column(name = "time")
    private String time;

    public TodoBean() {

    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getTagColor() {
        return tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = tagColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
        String date = TimeUtils.date2String(new Date(timeMillis), "yyyy-MM-dd");
        String time = TimeUtils.date2String(new Date(timeMillis), "HH:mm:ss");
        setTime(time);
        setDate(date);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        if (TextUtils.isEmpty(date)) {
            date = TimeUtils.date2String(new Date(timeMillis), "yyyy-MM-dd");
        }
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        if (TextUtils.isEmpty(time))
            time = TimeUtils.date2String(new Date(timeMillis), "HH:mm:ss");
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }
}
