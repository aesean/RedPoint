package com.aesean.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * NewRedPointView
 *
 * @author xl
 * @version V1.0
 * @since 16/7/22
 */
@SuppressWarnings("unused")
public class RedPointWindowView extends View implements View.OnTouchListener, RedPoint0AdhereEffect.UpdateListener {

    private static final float _1DP = toPx(1);
    private static final float _2DP = toPx(2);
    private static final float _4DP = toPx(4);
    private static final float _6DP = toPx(6);
    private static final float _7DP = toPx(7);
    private static final float _8DP = toPx(8);
    private static final float _12DP = toPx(12);
    private static final float _16DP = toPx(16);
    private static final float _32DP = toPx(32);
    private static final float _64DP = toPx(64);

    private Paint mPaint;
    private Paint mDisappearPaint;

    private RedPointAdhereEffect mAdhereEffect;

    public RedPointWindowView(Context context) {
        super(context);
        init();
    }

    public RedPointWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedPointWindowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RedPointWindowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setPointColor(@ColorInt int color) {
        mPaint.setColor(color);
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.red500, getContext().getTheme()));

        mDisappearPaint = new Paint();
        mDisappearPaint.setAntiAlias(true);
        mDisappearPaint.setStyle(Paint.Style.FILL);
        mDisappearPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.grey400, getContext().getTheme()));

        mAdhereEffect = new RedPointAdhereEffect();
        mAdhereEffect.setCircle0LinePartWidth(_8DP);
        mAdhereEffect.setCircle1LinePartWidth(_8DP);
        mAdhereEffect.setMaxDistance(toPx(200));
//        mAdhereEffect.setUpdateListener(this);

        setOnTouchListener(this);

        initDisappearAnimator();
//        initBrokeAnimator();
//        initShakeAnimator();
    }

    private float mRadius = 0;

    public RedPointAdhereEffect getAdhereEffect() {
        return mAdhereEffect;
    }

    public void setBrokeDistance(float distance) {
        mAdhereEffect.setMaxDistance(distance);
    }

    public void setCircle0Center(float x, float y, float radius) {
        mRadius = radius;
        mAdhereEffect.setCircle0Center(x, y, radius);
        mAdhereEffect.setCircle1Center(x, y, radius);
    }

    private boolean showDisappear = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mAdhereEffect.draw(canvas, mPaint);
        if (showDisappear) {
            drawDisappear(canvas);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAdhereEffect.setCircle0Center(event.getX(), event.getY(), mRadius);
                mAdhereEffect.setCircle1Center(event.getX(), event.getY(), mRadius);
                showDisappear = true;
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                mAdhereEffect.setCircle1Center(event.getRawX(), event.getRawY());

                if (mAdhereEffect.isBroken()) {
                    if (showDisappear && !mDisappearAnimator.isRunning()) {
                        mDisappearAnimator.start();
                    }
                    mAdhereEffect.setCircle0Radius(0);
                } else {
                    showDisappear = true;
                    mAdhereEffect.setCircle0Radius(mRadius * (1 - 0.6f * mAdhereEffect.getCenterDistance() / mAdhereEffect.getMaxDistance()));
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
//                if (mAdhereEffect.isBroken()) {
//                    getActivity().getWindowManager().removeView(this);
//                } else {
//                    mShakeAnimator.install();
//                    postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getActivity().getWindowManager().removeView(NewRedPointView.this);
//                        }
//                    }, 96);
//                }
                return true;
        }
        return false;
    }

    /**
     * 这里通过Window来显示整个效果.因为拉伸效果通常需要覆盖到整个窗口,
     * 所以这里为了易用性,使用Window来创建整个效果,这样当前控件就可以放进任意控件.
     *
     * @param activity Activity
     * @param view     需要显示的View
     */
    private static void addViewToWindow(Activity activity, View view) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, 1900, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, PixelFormat.TRANSPARENT);
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        layoutParams.gravity = Gravity.CENTER;
        activity.getWindowManager().addView(view, layoutParams);
    }

    public static RedPointWindowView showInWindow(View view) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int radius;
        int halfWidth = view.getWidth() / 2;
        int halfHeight = view.getHeight() / 2;
        radius = Math.min(halfWidth, halfHeight);
        return showInWindow((Activity) view.getContext(), location[0] + halfWidth, location[1] + halfHeight, radius);
    }

    public static RedPointWindowView showInWindow(Activity activity, float x, float y, float radius) {
        return showInWindow(activity, x, y, radius, ResourcesCompat.getColor(
                activity.getResources(), R.color.red500, activity.getTheme()));
    }

    public static RedPointWindowView showInWindow(Activity activity, float x, float y, float radius, @ColorInt int color) {
        RedPointWindowView redPointWindowView = new RedPointWindowView(activity);
        redPointWindowView.setCircle0Center(x, y, radius);
        redPointWindowView.setPointColor(color);
        addViewToWindow(activity, redPointWindowView);
        return redPointWindowView;
    }

    public void removeMySelfFromWindow() {
        getActivity().getWindowManager().removeView(this);
    }

    private Activity getActivity() {
        return (Activity) this.getContext();
    }

    private static float toPx(float dp) {
        TypedValue.complexToDimension(1, Resources.getSystem().getDisplayMetrics());
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    private void drawDisappear(Canvas canvas) {
        double angle = 0.25 * Math.PI;
        for (int i = 0; i < 4; i++) {
            canvas.drawCircle((float) (mAdhereEffect.getCircle0CenterX() + Math.cos(angle) * mDisappearDistance),
                    (float) (mAdhereEffect.getCircle0CenterY() + Math.sin(angle) * mDisappearDistance),
                    mDisappearRadius, mDisappearPaint);
            angle += 0.5f * Math.PI;
        }
    }

    private float mDisappearRadius;
    private float mDisappearDistance;

    //    private ValueAnimator mBrokeAnimator;
//    private ValueAnimator mShakeAnimator;
    private ValueAnimator mDisappearAnimator;

    private void initDisappearAnimator() {
        mDisappearAnimator = new ValueAnimator();
        mDisappearAnimator.setFloatValues(0, 1);
        mDisappearAnimator.setDuration(200);
        mDisappearAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mDisappearAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mDisappearRadius = mRadius - mRadius * value * 0.8f;
                mDisappearDistance = mRadius * value;
                invalidate();
            }
        });
        mDisappearAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mDisappearRadius = 0;
                showDisappear = false;
//                showDisappear = false;
//                mShakeAnimator.install();
            }
        });
    }

//    private void initBrokeAnimator() {
//        mBrokeAnimator = new ValueAnimator();
//        mBrokeAnimator.setFloatValues(0, 1);
//        mBrokeAnimator.setDuration(36);
//        mBrokeAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
//        mBrokeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float value = (float) animation.getAnimatedValue();
//                mAdhereEffect.setAfterBrokenProgress(value);
//                invalidate();
//            }
//        });
//        mBrokeAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
//                mDisappearAnimator.install();
//            }
//        });
//    }
//
//    private void initShakeAnimator() {
//        mShakeAnimator = new ValueAnimator();
//        mShakeAnimator.setDuration(96);
//        mShakeAnimator.setIntValues(0, 72);
//        mShakeAnimator.setInterpolator(new Interpolator() {
//            @Override
//            public float getInterpolation(float input) {
//                // 利用正弦曲线生成一个震动插值器
//                double v = input * 16 * Math.PI;
//                return (float) ((float) Math.sin(v) * (1 - input) * 2 / Math.PI);
//            }
//        });
//        mShakeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float angle = mAdhereEffect.getAngle();
//                int value = (int) animation.getAnimatedValue();
//                setTranslationX((float) (value * Math.cos(angle)));
//                setTranslationY((float) (value * Math.sin(angle)));
//            }
//        });
//        mShakeAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                super.onAnimationEnd(animation);
////                brokenSmokeAnimator();
//            }
//        });
//    }

    @Override
    public void startUpdate() {

    }

    @Override
    public void endUpdate() {
//        if (mAdhereEffect.isBroken()) {
//            if (!mDisappearAnimator.isRunning()) {
//                mDisappearAnimator.install();
//            }
//        }
    }
}
