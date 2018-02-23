package com.weapp.zlf.weapp.bean;

import android.text.TextUtils;

import com.weapp.zlf.weapp.MainApplication;
import com.weapp.zlf.weapp.common.utils.Constant;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.File;

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
        return Constant.PHOTO_DIRS[MainApplication.mDirIndex] + File.separator + image;
    }

    public void setImage(String image) {
        if (TextUtils.isEmpty(image))
            return;
        this.image = image.substring(image.lastIndexOf(File.separator) + 1);
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
