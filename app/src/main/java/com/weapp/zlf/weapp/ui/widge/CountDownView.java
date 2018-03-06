package com.weapp.zlf.weapp.ui.widge;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.weapp.zlf.weapp.R;
import com.weapp.zlf.weapp.common.utils.ConstUtils;
import com.weapp.zlf.weapp.common.utils.SizeUtils;
import com.weapp.zlf.weapp.common.utils.TimeUtils;

/**
 * Created by zhuliangfei on 2018/2/27.
 */

public class CountDownView extends View {

    private static final String TAG = CountDownView.class.getSimpleName();
    private long mTotalTime;
    private long mStartTime;
    private long mCurrTime;
    private Paint mPaint;
    private OnFinishListener mFinishListener;
    private RectF mRectF;
    private TextPaint mTextPaint;
    private int mViewHeight;
    private int mViewWidth;

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
//        typedArray.getDimension(R.styleable.CountDownView_)
        typedArray.recycle();
    }


    public void start() {
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(SizeUtils.dp2px(4));
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(SizeUtils.sp2px(40));
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.white));

        mRectF = new RectF(SizeUtils.dp2px(2), SizeUtils.dp2px(2), w - SizeUtils.dp2px(2), h - SizeUtils.dp2px(2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTotalTime <= 0)
            return;
        if (mCurrTime == 0 && mStartTime == 0) {
            mStartTime = mCurrTime = System.currentTimeMillis();
        } else {
            mCurrTime = System.currentTimeMillis();
        }
        float sweepAngle = (float) (2 * 180 * (mTotalTime - (mCurrTime - mStartTime)) / (float)mTotalTime);
        Log.d(TAG, "onDraw: " + sweepAngle);

        drawTime(canvas, mTotalTime ,(mCurrTime - mStartTime));
        if (sweepAngle <= 0) {
            if (mFinishListener != null)
                mFinishListener.onFinished();
        } else {
            canvas.drawArc(mRectF, -90, sweepAngle, false, mPaint);
            invalidate();
        }
    }

    private void drawTime(Canvas canvas, long t1, long t2) {
        long sec = TimeUtils.getTimeSpan(t1 + 1000, t2, ConstUtils.TimeUnit.SEC);
        long min = TimeUtils.getTimeSpan(t1 + 1000, t2, ConstUtils.TimeUnit.MIN);
        StringBuilder text = new StringBuilder();
        if (min < 10) {
            text.append(0);
        }
        text.append(min);
        text.append(":");
        if (sec < 10) {
            text.append(0);
        }
        text.append(sec);
        float length = mTextPaint.measureText(text.toString());
        canvas.drawText(text.toString(), (mViewWidth - length) / 2, mViewHeight / 2, mTextPaint);
    }

    public void setTotalTime(long mTotalTime) {
        this.mTotalTime = mTotalTime;
    }
    public void setOnFinishListener(OnFinishListener listener) {
        mFinishListener = listener;
    }
    public interface OnFinishListener {
        void onFinished();
    }
}
