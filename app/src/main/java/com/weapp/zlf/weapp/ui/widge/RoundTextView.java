package com.weapp.zlf.weapp.ui.widge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.weapp.zlf.weapp.R;

/**
 * Created by zhuliangfei on 2018/1/15.
 */

public class RoundTextView extends View {
    private int mBgColor;
    private float mTextSize;
    private String mText;
    private TextPaint mPaint;
    private int mViewHeight;
    private int mViewWidth;
    private Paint mBgPaint;
    private int mRadius;

    public RoundTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundTextView);
        mBgColor = typedArray.getColor(R.styleable.RoundTextView_color, context.getResources().getColor(R.color.colorAccent));
        mTextSize = typedArray.getDimension(R.styleable.RoundTextView_text_size, context.getResources().getDimension(R.dimen.sp_12));
        mText = typedArray.getString(R.styleable.RoundTextView_text);
        typedArray.recycle();
        init();
    }

    private void init() {
        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setStyle(Paint.Style.FILL);

        mPaint = new TextPaint();
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mRadius = Math.min(w, h) / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mRadius, mRadius, mRadius, mBgPaint);

        if (TextUtils.isEmpty(mText)) {
            return;
        }

        // 计算Baseline绘制的起点X轴坐标 ，计算方式：画布宽度的一半 - 文字宽度的一半
        int baseX = (int) (canvas.getWidth() / 2 - mPaint.measureText(mText) / 2);
        // 计算Baseline绘制的Y坐标 ，计算方式：画布高度的一半 - 文字总高度的一半
        int baseY = (int) ((canvas.getHeight() / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));
        canvas.drawText(mText, baseX, baseY, mPaint);
    }

    public void setText(String string) {
        mText = string;
        invalidate();
    }

    public void setColor(int color) {
        mBgColor = color;
        mBgPaint.setColor(mBgColor);
        invalidate();
    }
    public void setTextColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }
}
