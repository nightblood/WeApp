package com.weapp.zlf.weapp.bean;

import com.weapp.zlf.weapp.common.utils.TimeUtils;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by zhuliangfei on 2018/1/29.
 */

@Table(name = "tb_anniversary")
public class AnniversaryBean {
    public AnniversaryBean() {

    }

    @Column(name = "id",isId = true,autoGen = true, property = "NOT NULL")
    private int id;
    @Column(name = "time_millis")
    private long timeMillis;
    @Column(name = "create_time_millis")
    private long createTimeMillis;
    @Column(name = "name")
    private String name;
    @Column(name = "icon")
    private String icon;
    @Column(name = "gender")
    private int gender;
    @Column(name = "tag")
    private int tag;
    @Column(name = "content")
    private String content;
    @Column(name = "tag_color")
    private int tagColor;
    @Column(name = "tag_name")
    private String tagName;
    @Column(name = "month_day")
    private int monthDay;

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
        String day = TimeUtils.date2String(new Date(timeMillis), "MMdd");
        setMonthDay(Integer.parseInt(day));
    }

    public long getCreateTimeMillis() {
        return createTimeMillis;
    }

    public void setCreateTimeMillis(long createTimeMillis) {
        this.createTimeMillis = createTimeMillis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiaryBean toDiaryBean() {
        DiaryBean bean = new DiaryBean();

        return bean;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getTagColor() {
        return tagColor;
    }

    public void setTagColor(int tagColor) {
        this.tagColor = tagColor;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public int getMonthDay() {
        return monthDay;
    }

    public void setMonthDay(int monthDay) {
        this.monthDay = monthDay;
    }
}
