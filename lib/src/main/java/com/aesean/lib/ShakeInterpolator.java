package com.aesean.lib;

import android.view.animation.Interpolator;

/**
 * ShakeInterpolator
 * 震动插值器
 *
 * @author xl
 * @version V1.0
 * @since 16/7/21
 */
@SuppressWarnings("unused")
public class ShakeInterpolator implements Interpolator {

    /**
     * 没有阻尼效果
     */
    public static final int TYPE_DAMP_NULL = 0;
    /**
     * 有阻尼效果,振幅从小变大
     */
    public static final int TYPE_DAMP_SMALL_IN_LARGE_OUT = 1;
    /**
     * 有阻尼效果,振幅从大变小
     */
    public static final int TYPE_DAMP_LARGE_IN_SMALL_OUT = 2;

    /**
     * 震动次数,默认4次
     */
    private int mShakeTimes = 4;
    /**
     * 阻尼效果
     */
    private int mDampType = TYPE_DAMP_NULL;

    public int getShakeTimes() {
        return mShakeTimes;
    }

    /**
     * 设置震动次数,默认4次
     *
     * @param shakeTimes 次数
     * @return ShakeInterpolator
     */
    public ShakeInterpolator setShakeTimes(int shakeTimes) {
        mShakeTimes = shakeTimes;
        return this;
    }

    /**
     * 设置阻尼效果,默认没有阻尼效果.
     *
     * @param dampType 阻尼效果,参考{@link #TYPE_DAMP_NULL},{@link #TYPE_DAMP_SMALL_IN_LARGE_OUT}和{@link #TYPE_DAMP_LARGE_IN_SMALL_OUT}
     * @return ShakeInterpolator
     */
    public ShakeInterpolator setDampEffects(int dampType) {
        mDampType = dampType;
        return this;
    }

    public int getDampType() {
        return mDampType;
    }

    /**
     * 获取阻尼系数
     *
     * @param input input
     * @return 系数
     */
    protected float getDampK(float input) {
        switch (mDampType) {
            case TYPE_DAMP_SMALL_IN_LARGE_OUT:
                return input;
            case TYPE_DAMP_LARGE_IN_SMALL_OUT:
                return 1 - input;
            case TYPE_DAMP_NULL:
            default:
                return 1;
        }
    }

    @Override
    public float getInterpolation(float input) {
        double v = input * mShakeTimes * 2 * Math.PI;
        return (float) ((float) Math.sin(v) * getDampK(input) * 2 / Math.PI);
    }
}
