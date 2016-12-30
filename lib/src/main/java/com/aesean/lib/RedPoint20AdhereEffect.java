package com.aesean.lib;

import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;

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
public class RedPoint20AdhereEffect extends RedPoint0AdhereEffect {

    private float mCircle0LinePartWidth = 0;
    private float mCircle1LinePartWidth = 1;

    public RedPoint20AdhereEffect() {
        super();
    }

    public void setCircle0Center(float x, float y, float radius, float linePartWidth) {
        super.setCircle1Center(x, y, radius);
        mCircle0LinePartWidth = linePartWidth;
    }

    public void setCircle1Center(float x, float y, float radius, float linePartWidth) {
        super.setCircle0Center(x, y, radius);
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

    @Override
    protected void updateCirclePath() {
//        super.updateCirclePath();
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
    protected float[][] getFourAdherePoint() {
//        float[][] points = super.getFourAdherePoint();
//        float halfLine0 = mCircle0LinePartWidth / 2;
//        float halfLine1 = mCircle1LinePartWidth / 2;
//        if (points[0][1] > points[2][1]) {
//            points[0][0] -= halfLine0;
//            points[1][0] += halfLine0;
//            points[2][0] += halfLine0;
//            points[3][0] -= halfLine0;
//        } else {
//            points[0][0] += halfLine0;
//            points[1][0] -= halfLine0;
//            points[2][0] -= halfLine0;
//            points[3][0] += halfLine0;
//        }

//        return points;
        return MathUtils.getCircleLineCircle(getCircle0CenterX(), getCircle0CenterY(), getCircle0Radius(), mCircle0LinePartWidth,
                getCircle1CenterX(), getCircle1CenterY(), getCircle1Radius(), mCircle1LinePartWidth);
    }
}
