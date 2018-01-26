package com.weapp.zlf.weapp.bean;

import android.text.TextUtils;

import com.weapp.zlf.weapp.common.utils.TimeUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/8.
 */
@Table(name = "tb_diary",onCreated = "")
public class DiaryBean implements Serializable{
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;
    @Column(name = "images")
    private String imageStr;
    private List<String> images;
    @Column(name = "mood")
    private int mood;
    @Column(name = "weather")
    private int weather;
    @Column(name = "time")
    private String time;
    @Column(name = "week")
    private String week;
    @Column(name = "date")
    private String date;
    @Column(name = "gender")
    private int gender;
    @Column(name = "tag")
    private int tag;
    @Column(name = "time_millis")
    private long timeMillis;

    @Column(name = "id",isId = true,autoGen = true,property = "NOT NULL")
    private int id;
    private String year;
    private String month;

    public DiaryBean() {

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
        if (TextUtils.isEmpty(title)) {
            title = content.substring(0, content.length() > 6 ? 6 : content.length());
        }
    }

    public List<String> getImages() {
//        if (images != null) {
//            return images;
//        } else if (!TextUtils.isEmpty(imageStr)){
//            String[] split = imageStr.split("||");
//            images = Arrays.asList(split);
//        }
        if (!TextUtils.isEmpty(imageStr)){
            String[] split = imageStr.split("&&");
            images = Arrays.asList(split);
        }
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < images.size(); i++) {
            if (i == images.size() - 1) {
                builder.append(images.get(i));
            } else {
                builder.append(images.get(i)).append("&&");
            }
        }
        imageStr = builder.toString();
    }

    public int getMood() {
        return mood;
    }

    public void setMood(int mood) {
        this.mood = mood;
    }

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFullTime() {
        setTimeMillis(System.currentTimeMillis());
        Date date = new Date(timeMillis);
        String time = TimeUtils.date2String(date, "yyyy/MM/dd hh:mm:ss");
        setDate(time.substring(time.lastIndexOf("/") + 1, time.indexOf(" ")));
        setTime(time.substring(time.indexOf(" ")));
        setWeek(TimeUtils.getWeek(date));
    }

    public String getImageStr() {
        return imageStr;
    }

    public void setImageStr(String imageStr) {
        this.imageStr = imageStr;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public long getTimeMillis() {
        return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
        this.timeMillis = timeMillis;
    }

    public String getYear() {
        if (TextUtils.isEmpty(year)) {
            Date date = new Date(timeMillis);
            String time = TimeUtils.date2String(date, "yyyy/MM");
            String[] split = time.split("/");
            year = split[0];
            month = split[1];
        }
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        if (TextUtils.isEmpty(year)) {
            Date date = new Date(timeMillis);
            String time = TimeUtils.date2String(date, "yyyy/MM");
            String[] split = time.split("/");
            year = split[0];
            month = split[1];
        }
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }


}
