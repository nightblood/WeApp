package com.weapp.zlf.weapp.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by zhuliangfei on 2018/1/30.
 */

@Table(name = "tb_image")
public class ImageBean {
    public ImageBean() {

    }

    @Column(name = "id", isId = true, autoGen = true, property = "NOT NULL")
    private int id;
    @Column(name = "diary_id", property = "NOT NULL")
    private int diaryId;
    @Column(name = "image")
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDiaryId() {
        return diaryId;
    }

    public void setDiaryId(int diaryId) {
        this.diaryId = diaryId;
    }
}
