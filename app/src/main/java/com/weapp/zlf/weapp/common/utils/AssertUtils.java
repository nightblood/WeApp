package com.weapp.zlf.weapp.common.utils;

import android.content.Context;
import android.content.res.TypedArray;

import com.weapp.zlf.weapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhuliangfei on 2018/1/31.
 */

public class AssertUtils {

    public static List<Integer> moodlist;
    public static List<Integer> weatherlist;
    public static List<Integer> taglist;
    public static List<String> tagColorList;

    public static void init(Context context) {
        TypedArray moodArray = context.getResources().obtainTypedArray(R.array.mood_emoji);
        TypedArray weatherArray = context.getResources().obtainTypedArray(R.array.weather);
        TypedArray tagArray = context.getResources().obtainTypedArray(R.array.tag);
        TypedArray colorsArray = context.getResources().obtainTypedArray(R.array.tag_color);

        moodlist = new ArrayList<>();
        weatherlist = new ArrayList<>();
        taglist = new ArrayList<>();
        tagColorList = new ArrayList<>();
        for (int i = 0; i < moodArray.length(); i++) {
            moodlist.add(moodArray.getResourceId(i, 0));
        }
        for (int i = 0; i < weatherArray.length(); i++) {
            weatherlist.add(weatherArray.getResourceId(i, 0));
        }
        for (int i = 0; i < tagArray.length(); i++) {
            taglist.add(tagArray.getResourceId(i, 0));
        }
        for (int i = 0; i < colorsArray.length(); i++) {
            tagColorList.add(colorsArray.getString(i));
        }
        moodArray.recycle();
        weatherArray.recycle();
        tagArray.recycle();
        colorsArray.recycle();


    }
}
