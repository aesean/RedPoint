package com.aesean.lib;

/**
 * MathUtils
 *
 * @author xl
 * @version V1.0
 * @since 16/7/21
 */
public class MathUtils {
    private MathUtils() {
        throw new RuntimeException("静态工具方法，禁止实例化。");
    }

    /**
     * 获取两个圆之间夹两条直线,直线与圆的四个切点
     *
     * @param circle0CenterX 第一个圆圆心x坐标
     * @param circle0CenterY 第一个圆圆心y坐标
     * @param circle0Radius  第一个圆半径
     * @param circle1CenterX 第二个圆圆心x坐标
     * @param circle1CenterY 第二个圆圆心y坐标
     * @param circle1Radius  第二个圆坐标
     * @return 返回float[4][2]四个点, float[0] float[1] circle0的左右切点,float[2] float[3] circle1的左右切点
     */
    public static float[][] getCircleCircle(float circle0CenterX, float circle0CenterY, float circle0Radius,
                                            float circle1CenterX, float circle1CenterY, float circle1Radius) {
        return getCircleLineCircle(circle0CenterX, circle0CenterY, circle0Radius, 0f, circle1CenterX, circle1CenterY, circle1Radius, 0f);
    }

    /**
     * 获取两个圆之间夹两条直线,直线与圆的四个切点
     *
     * @param circle0CenterX    第一个圆圆心x坐标
     * @param circle0CenterY    第一个圆圆心y坐标
     * @param circle0Radius     第一个圆半径
     * @param circle0LineLength 中间直线长度
     * @param circle1CenterX    第二个圆圆心x坐标
     * @param circle1CenterY    第二个圆圆心y坐标
     * @param circle1Radius     第二个圆坐标
     * @param circle1LineLength 中间直线长度
     * @return 返回float[4][2]四个点, float[0] float[1] circle0的左右切点,float[2] float[3] circle1的左右切点
     */
    public static float[][] getCircleLineCircle(float circle0CenterX, float circle0CenterY, float circle0Radius, float circle0LineLength,
                                                float circle1CenterX, float circle1CenterY, float circle1Radius, float circle1LineLength) {
        float[][] result = new float[4][2];
        for (int i = 0; i < result.length; i++) {
            result[i] = new float[2];
        }

        float radiusSum = circle0Radius + circle1Radius;

        float cX = circle1CenterX + (circle0CenterX - circle1CenterX) * (circle1Radius / radiusSum);
        float cY = circle1CenterY + (circle0CenterY - circle1CenterY) * (circle1Radius / radiusSum);

        float[][] math = getCircleLinePoint(circle0CenterX, circle0CenterY, circle0LineLength, circle0Radius, cX, cY);
        result[0] = math[0];
        result[1] = math[1];

        math = getCircleLinePoint(circle1CenterX, circle1CenterY, circle1LineLength, circle1Radius, cX, cY);
        result[2] = math[0];
        result[3] = math[1];

        return result;
    }

    /**
     * 已知一个圆,和一个点坐标,求切点坐标
     *
     * @param circleCenterX 圆心x坐标
     * @param circleCenterY 圆心y坐标
     * @param circleRadius  圆半径
     * @param x             切线经过的点x坐标
     * @param y             切线经过的点y坐标
     * @return 切点, float[0]第一个切点,float[1]第二个切点
     */
    public static float[][] getCirclePoint(float circleCenterX, float circleCenterY, float circleRadius, float x, float y) {
        return getCircleLinePoint(circleCenterX, circleCenterY, 0f, circleRadius, x, y);
    }

    public static float[][] getCircleLinePoint(float circleCenterX, float circleCenterY, float lineLength, float circleRadius, float x, float y) {
        float[][] result = new float[2][2];

        float d0 = (float) Math.sqrt((circleCenterX - x) * (circleCenterX - x)
                + (circleCenterY - y) * (circleCenterY - y));

        // 算出夹角
        double angle = Math.asin(circleRadius / d0);
        // 算出两个圆心连线的k角度
        double angleK = Math.atan(((circleCenterY - y) / (circleCenterX - x)));

        // 算夹角+-之后的两个方程
        float kRight = (float) Math.tan(angleK - angle);
        float bRight = y - kRight * x;

        float kLeft = (float) Math.tan(angleK + angle);
        float bLeft = y - kLeft * x;
        float halfLineLength = lineLength * 0.5f;
        /**
         * 直线与切点方程解:
         * {
         *      (x-x0)^2+(y-y0)^2=r^2
         *      kx+b=y
         * }
         * (x-x0)^2+(kx+b-y0)^2=r^2
         * (k^2+1)x^2+(2k(b-y0)-2x0)x+x0^2+(b-y0)^2-r^2=0
         * x=-b/2a=-((2k(b-y0)-2x0)/(2(k^2+1)))
         * =(2x0-2k(b-y0))/(2(k^2+1))
         * =(x0+k(y0-b))/(k^2+1)
         * y=kx+b
         */
        //代入上面的公式计算切点坐标
        result[0] = getLineCircle(circleCenterX, circleCenterY, kLeft, bLeft);
        if (result[0][0] > circleCenterX) {
            result[0][0] += halfLineLength;
        } else {
            result[0][0] -= halfLineLength;
        }
        result[1] = getLineCircle(circleCenterX, circleCenterY, kRight, bRight);
        if (result[1][0] > circleCenterX) {
            result[1][0] += halfLineLength;
        } else {
            result[1][0] -= halfLineLength;
        }
        return result;
    }

    /**
     * 注意这里只能计算已知这条线和圆是相切的
     *
     * @param circleCenterX 圆心x坐标
     * @param circleCenterY 圆心y坐标
     * @param k             直线斜率
     * @param b             直线b
     * @return 返回float[2], float[0]切点x坐标, float[1]切点y坐标
     */
    private static float[] getLineCircle(float circleCenterX, float circleCenterY, float k, float b) {
        float[] result = new float[2];
        result[0] = (circleCenterX - k * (b - circleCenterY)) / (k * k + 1);
        result[1] = k * result[0] + b;
        return result;
    }
}
