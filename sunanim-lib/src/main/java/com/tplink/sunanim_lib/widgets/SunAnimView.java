/*
 * Copyright (C) 2016, TP-LINK TECHNOLOGIES CO., LTD.
 *
 * SunAnimView.java
 *
 * SunAnimView
 *
 * Author huanghaiqi, Created at 2016-12-21
 *
 * Ver 1.0, 2016-12-21, huanghaiqi, Create file.
 */

package com.tplink.sunanim_lib.widgets;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.tplink.sunanim_lib.R;

import java.util.ArrayList;

public class SunAnimView extends View {

    private static final String TAG = "DEBUG";
    /**
     * 默认太阳的光晕数量
     */
    private int DEFAULT_HALO_COUNT = 3;
    private int mHaloCount = DEFAULT_HALO_COUNT;//太阳的光晕数量
    /**
     * 默认太阳的半径
     */
    private int DEFAULT_RADIUS = 100;
    private int mSunRaidus = DEFAULT_RADIUS;//太阳的半径
    /**
     * 默认halo之间的padding
     */
    private float DEFAULT_PADDING_RADIUS = 10;
    /**
     * 默认太阳光晕扩散结束的半径
     */
    private int DEFAULT_END_RADIUS = 150;
    private int mHaloSpreadEndRaidus = DEFAULT_END_RADIUS;//太阳光晕扩散结束的半径
    /**
     * 默认太阳的颜色
     */
    private int DEFAULT_SUN_COLOR = Color.YELLOW;
    private int mSunColor = DEFAULT_SUN_COLOR;//太阳的颜色
    /**
     * 默认太阳的中心位置x
     */
    private float DEFAULT_SUN_CENTER_X = 500;
    private float mSunCenterX = DEFAULT_SUN_CENTER_X;//太阳的中心位置x
    /**
     * 默认太阳的中心位置y
     */
    private float DEFAULT_SUN_CENTER_Y = 1000;
    private float mSunCenterY = DEFAULT_SUN_CENTER_Y;//太阳的中心位置y

    private Paint mPaint;
    private int mMeasuredWidth;
    private int mMeasuredHeight;
    private int mCenterWidth;
    private int mCenterHeight;
    private ArrayList<RadiusVo> mRadiusVos;
    private UpdateRadiusTask mUpdateRadiusTask;
    private float mDeltaRadius;
    private float mDeltaAlpha;

    public SunAnimView(Context context) {
        this(context, null);
    }

    public SunAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SunAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomAttrs(context, attrs);
        initDrawVos();
        initDrawTool();
    }

    private void initCustomAttrs(Context context, AttributeSet attrs) {
        DEFAULT_RADIUS = context.getResources().getDimensionPixelSize(R.dimen.default_sun_radius);
        DEFAULT_END_RADIUS = context.getResources().getDimensionPixelSize(R.dimen
                .default_sun_end_radius);
        DEFAULT_PADDING_RADIUS = context.getResources().getDimensionPixelSize(R.dimen
                .default_padding_radius);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                com.tplink.sunanim_lib.R.styleable.SunAnimView);
        mHaloCount = ta.getInteger(
                R.styleable.SunAnimView_halo_count,
                DEFAULT_HALO_COUNT);
        mSunRaidus = ta.getDimensionPixelSize(
                com.tplink.sunanim_lib.R.styleable.SunAnimView_raidus,
                DEFAULT_RADIUS);
        mHaloSpreadEndRaidus = ta.getDimensionPixelSize(
                R.styleable.SunAnimView_end_radius,
                DEFAULT_END_RADIUS);
        mSunCenterX = ta.getFloat(
                R.styleable.SunAnimView_sun_center_x,
                DEFAULT_SUN_CENTER_X);
        mSunCenterY = ta.getFloat(
                R.styleable.SunAnimView_sun_center_y,
                DEFAULT_SUN_CENTER_Y);
        mSunColor = ta.getColor(
                com.tplink.sunanim_lib.R.styleable.SunAnimView_sun_color,
                DEFAULT_SUN_COLOR);
        ta.recycle();
        ta = null;
    }

    private void initDrawVos() {
        mDeltaRadius = Math.abs(mHaloSpreadEndRaidus - mSunRaidus);
        mDeltaAlpha = 255f / mDeltaRadius;
        mRadiusVos = new ArrayList<>();
        for (int i = 0; i < mHaloCount; i++) {
            RadiusVo radiusVo = new RadiusVo(255, mSunColor, mSunRaidus);
            if (i > 0) {
                radiusVo.mPre = mRadiusVos.get(mRadiusVos.size() - 1);
            }
            mRadiusVos.add(radiusVo);
        }
    }

    private void initDrawTool() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(mSunColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
        mCenterWidth = mMeasuredWidth / 2;
        mCenterHeight = mMeasuredHeight / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //先画halo
        for (int i = 0; i < mRadiusVos.size(); i++) {
            RadiusVo radiusVo = mRadiusVos.get(i);
            //先设置颜色在设置透明度
            mPaint.setColor(radiusVo.mColor);
            mPaint.setAlpha(radiusVo.mAlpha);
            canvas.drawCircle(mSunCenterX, mSunCenterX, radiusVo.mCurrentRadius, mPaint);
        }
        //最后画太阳
        mPaint.setColor(mSunColor);
        mPaint.setAlpha(255);
        canvas.drawCircle(mSunCenterX, mSunCenterX, mSunRaidus, mPaint);
    }

    public void startHaloSpread() {
        mIsPause = false;
        if (mUpdateRadiusTask == null) {
            mUpdateRadiusTask = new UpdateRadiusTask();
        }
        post(mUpdateRadiusTask);
        //---------------------
//        for (int i = 0; i < mHaloCount; i++) {
//
//        }
//        ValueAnimator radiusAnim = ValueAnimator.ofFloat(mSunRaidus, mHaloSpreadEndRaidus);
//        ValueAnimator alphaAnim = ValueAnimator.ofInt(255, 0);
//        AnimatorSet animatorSet = new AnimatorSet();
//        animatorSet.playTogether(radiusAnim);

    }

    public void pauseHaloSpread() {
        mIsPause = true;
    }

    public void restartHaloSpread() {

    }

    /**
     * 扩散速率
     */
    private int mSpreadSpeed = 20;
    private boolean mIsPause = false;

    class UpdateRadiusTask implements Runnable {
        @Override
        public void run() {
            boolean isOuterHaloEnd = false;
            for (RadiusVo radiusVo : mRadiusVos) {
                if (radiusVo.mPre != null) {
                    if (radiusVo.mPre.mCurrentRadius - mSunRaidus >= DEFAULT_PADDING_RADIUS) {
                        radiusVo.mAlpha -= mDeltaAlpha;
                        radiusVo.mCurrentRadius++;
                        if (radiusVo.mAlpha < 0) {
                            radiusVo.mAlpha = 255;
                            radiusVo.mCurrentRadius = mSunRaidus;
                        }
                    }
                } else {//radiusVo.mPre == null, the head halo
                    radiusVo.mAlpha -= mDeltaAlpha;
                    radiusVo.mCurrentRadius++;
                    isOuterHaloEnd = false;
                    if (radiusVo.mAlpha <= 0) {
                        isOuterHaloEnd = true;
                        radiusVo.mAlpha = 255;
                        radiusVo.mCurrentRadius = mSunRaidus;
                    }
                }
            }
            invalidate();
            if (isOuterHaloEnd) {
                if (mRadiusVos.size() > 2) {
                    RadiusVo firstRadiusVo = mRadiusVos.remove(0);
                    RadiusVo lastRadiusVo = mRadiusVos.get(mRadiusVos.size() - 1);
                    firstRadiusVo.mPre = lastRadiusVo;
                    RadiusVo radiusVo = mRadiusVos.get(0);
                    radiusVo.mPre = null;
                    mRadiusVos.add(firstRadiusVo);
                }
            }
            if (mIsPause) {
                return;
            }
            postDelayed(this, mSpreadSpeed);
        }
    }

    /**
     * 存储太阳圆弧的信息的bean
     */
    class RadiusVo {
        /**
         * 透明度0-255
         */
        int mAlpha = 255;
        int mColor = Color.YELLOW;
        float mCurrentRadius = -1;
        RadiusVo mPre;

        public RadiusVo(int alpha,
                        int color,
                        float currentRadius) {
            this.mAlpha = alpha;
            this.mColor = color;
            this.mCurrentRadius = currentRadius;
        }
    }

}
