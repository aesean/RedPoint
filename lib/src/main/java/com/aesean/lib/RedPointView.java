package com.aesean.lib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * NewRedPointBaseView
 *
 * @author xl
 * @version V1.0
 * @since 16/7/22
 */
public class RedPointView extends TextView implements View.OnTouchListener {
    private static final String TAG = "RedPointView";

    private Paint mPaint;
    private RedPointWindowView mRedPointWindowView;

    public RedPointView(Context context) {
        super(context);
        init();
    }

    public RedPointView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedPointView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RedPointView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(ResourcesCompat.getColor(getResources(), R.color.red500, getContext().getTheme()));
        initShakeAnimator();
        setOnTouchListener(this);
    }

    private static Path createEllipsePath(float cx, float cy, float width, float height) {
        if (width > height) {
            return createEllipsePath(cx, cy, height * 0.5f, height, width - height);
        } else {
            return createEllipsePath(cx, cy, width * 0.5f, width, 0);
        }
    }

    private static Path createEllipsePath(float cx, float cy, float circleWidth, float height, float lineLength) {
        Path path = new Path();
        float halfLineLength = lineLength * 0.5f;
        float halfHeight = height * 0.5f;
        RectF rectF = new RectF();
        rectF.set(cx - halfLineLength - circleWidth, cy - halfHeight, cx - halfLineLength + circleWidth, cy + halfHeight);
        path.arcTo(rectF, 90, 180, true);
        path.rLineTo(lineLength, 0);
        rectF.set(cx + halfLineLength - circleWidth, cy - halfHeight, cx + halfLineLength + circleWidth, cy + halfHeight);
        path.arcTo(rectF, -90, 180, true);
        path.rLineTo(-lineLength, 0);
        return path;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int halfWidth = width / 2;
        int height = getHeight();
        int halfHeight = height / 2;
//        canvas.drawCircle(halfWidth, halfHeight, Math.min(halfWidth, halfHeight), mPaint);
        Path ellipsePath = createEllipsePath(halfWidth, halfHeight, width, height);
        canvas.drawPath(ellipsePath, mPaint);
        super.onDraw(canvas);
    }

    private ValueAnimator mShakeAnimator;
    private float mAngle;

    private void initShakeAnimator() {
        mShakeAnimator = new ValueAnimator();
        mShakeAnimator.setDuration(168);
        mShakeAnimator.setIntValues(0, 256);
//        mShakeAnimator.setStartDelay(100);
//        mShakeAnimator.setInterpolator(new Interpolator() {
//            @Override
//            public float getInterpolation(float input) {
//                // 利用正弦曲线生成一个震动插值器
//                double v = input * 8 * Math.PI;
//                return (float) ((float) Math.sin(v) * (1 - input) * 2 / Math.PI);
//            }
//        });
        mShakeAnimator.setInterpolator(new ShakeInterpolator().setDampEffects(ShakeInterpolator.TYPE_DAMP_LARGE_IN_SMALL_OUT));
        mShakeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                setTranslationX((float) (value * Math.cos(mAngle)));
                setTranslationY((float) (value * Math.sin(mAngle)));
            }
        });
        mShakeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
//                brokenSmokeAnimator();
//                mNewRedPointView.removeMySelfFromWindow();
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mRedPointWindowView = RedPointWindowView.showInWindow(this);
                setVisibility(INVISIBLE);
                return true;
            case MotionEvent.ACTION_MOVE:
                boolean b = (event.getFlags() & MotionEvent.FLAG_WINDOW_IS_OBSCURED) != 0;
                mRedPointWindowView.dispatchTouchEvent(event);
                return true;
            case MotionEvent.ACTION_UP:
                setVisibility(VISIBLE);
                if (!mRedPointWindowView.getAdhereEffect().isBroken()) {
                    mAngle = mRedPointWindowView.getAdhereEffect().getAngle();
                    mShakeAnimator.start();
                }
                mRedPointWindowView.removeMySelfFromWindow();
                return true;
        }
        return false;
    }
}
