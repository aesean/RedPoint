package com.aesean.lib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.FloatRange;

/**
 * RedPoint0AdhereEffect
 * AdhereEffect是用来处理任何四个点需要粘连效果的类,需要提供四个点坐标和两个控制点坐标,或者用缺省的两个控制点坐标
 * RedPointAdhereEffect是将AdhereEffect封装成两个圆之间的粘连,
 * 可以看成是RedPointAdhereEffect是将两个圆坐标加上圆半径转换为四个点坐标和控制点坐标.
 *
 * @author xl
 * @version V1.0
 * @since 16/7/20
 */
public class RedPointAdhereEffect extends AdhereEffect {

    /**
     */
    public RedPointAdhereEffect() {
        super();
        mCircle0Path = new Path();
        mCircle1Path = new Path();
    }

    private boolean mShowCircle0 = true;
    private boolean mShowCircle1 = true;

    public boolean isShowCircle0() {
        return mShowCircle0;
    }

    public void setShowCircle0(boolean showCircle0) {
        mShowCircle0 = showCircle0;
    }

    public boolean isShowCircle1() {
        return mShowCircle1;
    }

    public void setShowCircle1(boolean showCircle1) {
        mShowCircle1 = showCircle1;
    }

    /**
     * 断开距离
     */
    private float mMaxDistance = Float.MAX_VALUE;

    /**
     * 圆0半径
     */
    private float mCircle0Radius;
    /**
     * 圆1半径
     */
    private float mCircle1Radius;

    /**
     * 圆0x坐标
     */
    private float mCircle0CenterX;
    /**
     * 圆0y坐标
     */
    private float mCircle0CenterY;

    /**
     * 圆1x坐标
     */
    private float mCircle1CenterX;
    /**
     * 圆1y坐标
     */
    private float mCircle1CenterY;

    public float getMaxDistance() {
        return mMaxDistance;
    }

    public void setMaxDistance(float maxDistance) {
        mMaxDistance = maxDistance;
        update();
    }

    public void setCircle0Radius(float circle0Radius) {
        mCircle0Radius = circle0Radius;
        update();
    }

    public void setCircle1Radius(float circle1Radius) {
        mCircle1Radius = circle1Radius;
        update();
    }

    public void setCircle0Center(float x, float y) {
        mCircle0CenterX = x;
        mCircle0CenterY = y;
        update();
    }

    public void setCircle0Center(float x, float y, float radius) {
        mCircle0CenterX = x;
        mCircle0CenterY = y;
        mCircle0Radius = radius;
        update();
    }

    @Override
    public void updateDefaultControlPoint() {
        // super.updateDefaultControlPoint();
        float radiusSum = mCircle0Radius + mCircle1Radius;
        float cX = getCircle1CenterX() + (getCircle0CenterX() - getCircle1CenterX()) * (getCircle1Radius() / radiusSum);
        float cY = getCircle1CenterY() + (getCircle0CenterY() - getCircle1CenterY()) * (getCircle1Radius() / radiusSum);
        setControlP0(cX, cY);
        setControlP1(cX, cY);
    }

    @Override
    public void updateAdherePath() {
        super.updateAdherePath();
    }

    public float getCenterDistance() {
        return (float) Math.sqrt((mCircle1CenterX - mCircle0CenterX) * (mCircle1CenterX - mCircle0CenterX)
                + (mCircle1CenterY - mCircle0CenterY) * (mCircle1CenterY - mCircle0CenterY));
    }

    private float mAfterBrokenProgress = 1;

    public float getAfterBrokenProgress() {
        return mAfterBrokenProgress;
    }

    /**
     * 设置断开后圆恢复的进度
     *
     * @param afterBrokenProgress 进度,取值范围0~1
     */
    public void setAfterBrokenProgress(@FloatRange(from = 0f, to = 1f) float afterBrokenProgress) {
        mAfterBrokenProgress = afterBrokenProgress;
        update();
    }

    public void update() {
        if (mUpdateListener != null) {
            mUpdateListener.startUpdate();
        }
        updateDefaultControlPoint();
        updateFourAdherePoint();
        if (isBroken()) {
            float p0X = getP0X();
            float p0Y = getP0Y();

            float p1X = getP1X();
            float p1Y = getP1Y();

            float halfP01X = (p0X + p1X) * 0.5f;
            float halfP01Y = (p0Y + p1Y) * 0.5f;

            float p2X = getP2X();
            float p2Y = getP2Y();

            float p3X = getP3X();
            float p3Y = getP3Y();

            float halfP23X = (p2X + p3X) * 0.5f;
            float halfP23Y = (p2Y + p3Y) * 0.5f;

            float c0X = halfP01X + (getControlP0X() - halfP01X) * (1 - mAfterBrokenProgress);
            float c0Y = halfP01Y + (getControlP0Y() - halfP01Y) * (1 - mAfterBrokenProgress);
            float c1X = halfP23X + (getControlP1X() - halfP23X) * (1 - mAfterBrokenProgress);
            float c1Y = halfP23Y + (getControlP1Y() - halfP23Y) * (1 - mAfterBrokenProgress);

            setControlP0(c0X, c0Y);
            setControlP1(c1X, c1Y);
        }
        updateAdherePath();
        updateCirclePath();
        if (mUpdateListener != null) {
            mUpdateListener.endUpdate();
        }
    }

    private UpdateListener mUpdateListener;

    public UpdateListener getUpdateListener() {
        return mUpdateListener;
    }

    public void setUpdateListener(UpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public interface UpdateListener {
        void startUpdate();

        void endUpdate();
    }

    public float getAngle() {
        return (float) Math.atan(getK());
    }

    public float getK() {
        return (getCircle0CenterY() - getCircle1CenterY()) / (getCircle0CenterX() - getCircle1CenterX());
    }

    @Override
    public boolean isBroken() {
        return getCenterDistance() > mMaxDistance;
    }

    public void setCircle1Center(float x, float y) {
        mCircle1CenterX = x;
        mCircle1CenterY = y;
        update();
    }

    public void setCircle1Center(float x, float y, float radius) {
        mCircle1CenterX = x;
        mCircle1CenterY = y;
        mCircle1Radius = radius;
        update();
    }


    public float getCircle0Radius() {
        return mCircle0Radius;
    }

    public float getCircle1Radius() {
        return mCircle1Radius;
    }

    public float getCircle0CenterX() {
        return mCircle0CenterX;
    }

    public void setCircle0CenterX(float circle0CenterX) {
        mCircle0CenterX = circle0CenterX;
        update();
    }

    public float getCircle0CenterY() {
        return mCircle0CenterY;
    }

    public void setCircle0CenterY(float circle0CenterY) {
        mCircle0CenterY = circle0CenterY;
        update();
    }

    public float getCircle1CenterX() {
        return mCircle1CenterX;
    }

    public void setCircle1CenterX(float circle1CenterX) {
        mCircle1CenterX = circle1CenterX;
        update();
    }

    public float getCircle1CenterY() {
        return mCircle1CenterY;
    }

    public void setCircle1CenterY(float circle1CenterY) {
        mCircle1CenterY = circle1CenterY;
        update();
    }

    protected float[][] getFourAdherePoint() {
        return MathUtils.getCircleLineCircle(getCircle0CenterX(), getCircle0CenterY(), getCircle0Radius(), mCircle0LinePartWidth,
                getCircle1CenterX(), getCircle1CenterY(), getCircle1Radius(), mCircle1LinePartWidth);
    }

    protected void updateFourAdherePoint() {
        updateFourAdherePoint(getFourAdherePoint());
    }

    protected void updateFourAdherePoint(float[][] points) {
        setP0(points[0][0], points[0][1]);
        setP1(points[1][0], points[1][1]);
        setP2(points[2][0], points[2][1]);
        setP3(points[3][0], points[3][1]);
    }

    private Path mCircle0Path;
    private Path mCircle1Path;

    public void draw(Canvas canvas, Paint adherePaint, Paint circle0Paint, Paint circle1Paint) {
        super.draw(canvas, adherePaint);
        if (mShowCircle0) {
            drawCircle0(canvas, circle0Paint);
        }
        if (mShowCircle1) {
            drawCircle1(canvas, circle1Paint);
        }
    }

    protected void drawCircle0(Canvas canvas, Paint circle0Paint) {
        canvas.drawPath(mCircle0Path, circle0Paint);
    }

    protected void drawCircle1(Canvas canvas, Paint circle1Paint) {
        canvas.drawPath(mCircle1Path, circle1Paint);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        draw(canvas, paint, paint, paint);
    }

    private RectF mTempRectF = new RectF();

    protected RectF getTempRectF() {
        return mTempRectF;
    }

    public Path getCircle0Path() {
        return mCircle0Path;
    }

    public Path getCircle1Path() {
        return mCircle1Path;
    }

    protected void updateCirclePath() {
        Path circle0Path = getCircle0Path();
        Path circle1Path = getCircle1Path();

        float circle0CenterX = getCircle0CenterX();
        float circle0CenterY = getCircle0CenterY();
        float circle0Radius = getCircle0Radius();

        float circle1CenterX = getCircle1CenterX();
        float circle1CenterY = getCircle1CenterY();
        float circle1Radius = getCircle1Radius();


        float halfLine0 = mCircle0LinePartWidth / 2;
        float halfLine1 = mCircle1LinePartWidth / 2;
        circle0Path.reset();
        circle1Path.reset();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            circle0Path.arcTo(circle0CenterX - halfLine0 - circle0Radius, circle0CenterY - circle0Radius,
                    circle0CenterX - halfLine0 + circle0Radius, circle0CenterY + circle0Radius, 90, 180, true);
            circle0Path.rLineTo(mCircle0LinePartWidth, 0);

            circle0Path.arcTo(circle0CenterX + halfLine0 - circle0Radius, circle0CenterY - circle0Radius,
                    circle0CenterX + halfLine0 + circle0Radius, circle0CenterY + circle0Radius, -90, 180, true);
            circle0Path.rLineTo(-mCircle1LinePartWidth, 0);

            circle1Path.arcTo(circle1CenterX - halfLine1 - circle1Radius, circle1CenterY - circle1Radius,
                    circle1CenterX - halfLine1 + circle1Radius, circle1CenterY + circle1Radius, 90, 180, true);
            circle1Path.rLineTo(mCircle0LinePartWidth, 0);

            circle1Path.arcTo(circle1CenterX + halfLine1 - circle1Radius, circle1CenterY - circle1Radius,
                    circle1CenterX + halfLine1 + circle1Radius, circle1CenterY + circle1Radius, -90, 180, true);
            circle1Path.rLineTo(-mCircle1LinePartWidth, 0);

        } else {
            RectF tempRectF = getTempRectF();
            tempRectF.set(circle1CenterX - halfLine1 - circle1Radius, circle1CenterY - circle1Radius,
                    circle1CenterX - halfLine1 + circle1Radius, circle1CenterY - circle1Radius);
            circle0Path.arcTo(tempRectF, 90, 180, true);
            circle0Path.rLineTo(mCircle0LinePartWidth, 0);

            tempRectF.set(circle1CenterX + halfLine1 - circle1Radius, circle1CenterY - circle1Radius,
                    circle1CenterX + halfLine1 + circle1Radius, circle1CenterY + circle1Radius);
            circle1Path.arcTo(tempRectF, -90, 180, true);
            circle1Path.rLineTo(-mCircle1LinePartWidth, 0);
        }
    }


    private float mCircle0LinePartWidth = 0;
    private float mCircle1LinePartWidth = 0;

    public void setCircle0Center(float x, float y, float radius, float linePartWidth) {
        setCircle1Center(x, y, radius);
        mCircle0LinePartWidth = linePartWidth;
    }

    public void setCircle1Center(float x, float y, float radius, float linePartWidth) {
        setCircle0Center(x, y, radius);
        mCircle1LinePartWidth = linePartWidth;
    }

    public float getCircle0LinePartWidth() {
        return mCircle0LinePartWidth;
    }

    public void setCircle0LinePartWidth(float circle0LinePartWidth) {
        mCircle0LinePartWidth = circle0LinePartWidth;
    }

    public float getCircle1LinePartWidth() {
        return mCircle1LinePartWidth;
    }

    public void setCircle1LinePartWidth(float circle1LinePartWidth) {
        mCircle1LinePartWidth = circle1LinePartWidth;
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
}
