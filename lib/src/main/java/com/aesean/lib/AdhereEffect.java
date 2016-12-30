package com.aesean.lib;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * AdhereEffect
 * 粘滞效果
 *
 * @author xl
 * @version V1.0
 * @since 16/7/19
 */
@SuppressWarnings("unused")
public class AdhereEffect {
    public AdhereEffect() {
        mAdherePath = new Path();
    }

    private float mP0X;
    private float mP0Y;
    private float mP1X;
    private float mP1Y;

    private float mControlP0X;
    private float mControlP0Y;
    private float mControlP1X;
    private float mControlP1Y;

    private float mP2X;
    private float mP2Y;
    private float mP3X;
    private float mP3Y;

    public void setPoint(float p0X, float p0Y, float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y) {
        this.mP0X = p0X;
        this.mP0Y = p0Y;

        this.mP1X = p1X;
        this.mP1Y = p1Y;

        this.mP2X = p2X;
        this.mP2Y = p2Y;

        this.mP3X = p3X;
        this.mP3Y = p3Y;

        updateDefaultControlPoint();
        updateAdherePath();
    }

    public void setPoint(float p0X, float p0Y, float p1X, float p1Y,
                         float p2X, float p2Y, float p3X, float p3Y,
                         float cP03X, float cP03Y, float cP12X, float cP12Y) {
        this.mP0X = p0X;
        this.mP0Y = p0Y;

        this.mP1X = p1X;
        this.mP1Y = p1Y;

        this.mP2X = p2X;
        this.mP2Y = p2Y;

        this.mP3X = p3X;
        this.mP3Y = p3Y;

        this.mControlP0X = cP03X;
        this.mControlP0Y = cP03Y;

        this.mControlP1X = cP12X;
        this.mControlP1Y = cP12Y;

        updateAdherePath();
    }

    public void updateDefaultControlPoint() {
        // 这里主要就是解析几何的东西,通过四个点坐标计算中心点坐标,以中心点作为控制点
        float k0 = (getP1Y() - getP3Y()) / (getP1X() - getP3X());
        float b0 = (getP3Y() * getP1X() - getP1Y() * getP3X()) / (getP1X() - getP3X());

        float k1 = (getP2Y() - getP0Y()) / (getP2X() - getP0X());
        float b1 = (getP2Y() * getP0X() - getP0Y() * getP2X()) / (getP0X() - getP2X());

        float cX = (b0 - b1) / (k1 - k0);
        float cY = (b1 * k0 - b0 * k1) / (k0 - k1);

        setControlP0(cX, cY);
        setControlP1(cX, cY);
    }

    private Path mAdherePath;

    public void updateAdherePathBroken() {
        mAdherePath.reset();
        mAdherePath.moveTo(mP0X, mP0Y);
        mAdherePath.lineTo(mP1X, mP1Y);
        mAdherePath.quadTo(mControlP0X, mControlP0Y, mP0X, mP0Y);
        mAdherePath.moveTo(mP2X, mP2Y);
        mAdherePath.lineTo(mP3X, mP3Y);
        mAdherePath.quadTo(mControlP1X, mControlP1Y, mP2X, mP2Y);
    }

    public void updateAdherePathNoBroken() {
        mAdherePath.reset();
        mAdherePath.moveTo(mP0X, mP0Y);
        mAdherePath.lineTo(mP1X, mP1Y);
        mAdherePath.quadTo(mControlP0X, mControlP0Y, mP2X, mP2Y);
        mAdherePath.lineTo(mP3X, mP3Y);
        mAdherePath.quadTo(mControlP1X, mControlP1Y, mP0X, mP0Y);
    }

    public boolean isBroken() {
        return false;
    }

    public void setControlP0(float controlP0X, float controlP0Y) {
        mControlP0X = controlP0X;
        mControlP0Y = controlP0Y;
    }

    public void setControlP1(float controlP1X, float controlP1Y) {
        mControlP1X = controlP1X;
        mControlP1Y = controlP1Y;
    }

    public float getControlP1Y() {
        return mControlP1Y;
    }

    public void setControlP1Y(float controlP1Y) {
        mControlP1Y = controlP1Y;
    }

    public float getControlP1X() {
        return mControlP1X;
    }

    public void setControlP1X(float controlP1X) {
        mControlP1X = controlP1X;
    }

    public float getControlP0Y() {
        return mControlP0Y;
    }

    public void setControlP0Y(float controlP0Y) {
        mControlP0Y = controlP0Y;
    }

    public float getControlP0X() {
        return mControlP0X;
    }

    public void setControlP0X(float controlP0X) {
        mControlP0X = controlP0X;
    }

    public void updateAdherePath() {
        if (isBroken()) {
            updateAdherePathBroken();
        } else {
            updateAdherePathNoBroken();
        }
    }

    public Path getAdherePath() {
        return mAdherePath;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawPath(mAdherePath, paint);
    }

    public float getP0X() {
        return mP0X;
    }

    public void setP0(float x, float y) {
        mP0X = x;
        mP0Y = y;
    }

    public void setP1(float x, float y) {
        mP1X = x;
        mP1Y = y;
    }

    public void setP2(float x, float y) {
        mP2X = x;
        mP2Y = y;
    }

    public void setP3(float x, float y) {
        mP3X = x;
        mP3Y = y;
    }

    public void setP0X(float p0X) {
        mP0X = p0X;
    }

    public float getP0Y() {
        return mP0Y;
    }

    public void setP0Y(float p0Y) {
        mP0Y = p0Y;
    }

    public float getP1X() {
        return mP1X;
    }

    public void setP1X(float p1X) {
        mP1X = p1X;
    }

    public float getP1Y() {
        return mP1Y;
    }

    public void setP1Y(float p1Y) {
        mP1Y = p1Y;
    }

    public float getP2X() {
        return mP2X;
    }

    public void setP2X(float p2X) {
        mP2X = p2X;
    }

    public float getP2Y() {
        return mP2Y;
    }

    public void setP2Y(float p2Y) {
        mP2Y = p2Y;
    }

    public float getP3X() {
        return mP3X;
    }

    public void setP3X(float p3X) {
        mP3X = p3X;
    }

    public float getP3Y() {
        return mP3Y;
    }

    public void setP3Y(float p3Y) {
        mP3Y = p3Y;
    }

}
