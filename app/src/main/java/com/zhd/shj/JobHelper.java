package com.zhd.shj;

import android.util.Log;

public class JobHelper {
    public static double jiaoduAB(double x1, double y1, double x2, double y2)
    {
        double direction = Math.atan2(y2 - y1, x2 - x1); // 计算方向，返回弧度值

// 将弧度值转换为角度值（可选）
        return  Math.toDegrees(direction);
    }
    public static double pointToBezierLine(double x1, double y1, double x2,
                                           double y2, double x0, double y0) {
        double space = 0;
        double a, b, c;
        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离
        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c >= a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b >= a * a + c * c) {
            space = c;
            return space;
        }
        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
        return space;
    }

    // 计算两点之间的距离
    public static double lineSpace(double x1, double y1, double x2, double y2) {
        double lineLength = 0;
        lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));

        Log.i("line", Double.toString(lineLength));

        return lineLength;
    }
    public static double[] getCrossPoint(double x1, double y1, double x2,
                                         double y2, double x3, double y3, double x4, double y4) {
        /*
         * x1 = x1 - orginx; x2 = x2 - orginx; x3 = x3 - orginx; x4 = x4 -
         * orginx; y1 = y1 - orginy; y2 = y2 - orginy; y3 = y3 - orginy; y4 = y4
         * - orginy;
         */
        if (x2 == x3 && y2 == y3)
            return new double[] { x2, y2 };
        double a1 = y2 - y1;
        double b1 = x1 - x2;
        double c1 = (x2 - x1) * y1 - (y2 - y1) * x1;
        if (b1 < 0) {
            a1 *= -1;
            b1 *= -1;
            c1 *= -1;
        } else if (b1 == 0 && a1 < 0) {
            a1 *= -1;
            c1 *= -1;
        }
        double a2 = y4 - y3;
        double b2 = x3 - x4;

        double c2 = (x4 - x3) * y3 - (y4 - y3) * x3;
        if (b2 < 0) {
            a2 *= -1;
            b2 *= -1;
            c2 *= -1;
        } else if (b2 == 0 && a2 < 0) {
            a2 *= -1;
            c2 *= -1;
        }

        double m = a1 * b2 - a2 * b1;

        double x = (c2 * b1 - c1 * b2) / m;
        double y = (c1 * a2 - c2 * a1) / m;

        return new double[] { x, y };
    }
    // 计算三点中A的夹角
    public static double getDegree(double AX, double AY, double BX, double BY,
                                   double CX, double CY) {
        double AB = Math.sqrt(Math.pow(AX - BX, 2) + Math.pow(AY - BY, 2));
        double BC = Math.sqrt(Math.pow(CX - BX, 2) + Math.pow(CY - BY, 2));
        double AC = Math.sqrt(Math.pow(CX - AX, 2) + Math.pow(CY - AY, 2));
        double cosA = (Math.pow(AB, 2) + Math.pow(AC, 2) - Math.pow(BC, 2))
                / (2 * AB * AC);
        double angleA = Math.round(Math.acos(cosA) * 180 / Math.PI);
        return angleA;
    }

    public static double pointToLine(double x1, double y1, double x2,
                                     double y2, double x0, double y0) {
        double space = 0;
        double a, b, c;

        a = lineSpace(x1, y1, x2, y2);// 线段的长度
        b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
        c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离

        if (c <= 0.000001 || b <= 0.000001) {
            space = 0;
            return space;
        }
        if (a <= 0.000001) {
            space = b;
            return space;
        }
        if (c * c == a * a + b * b) {
            space = b;
            return space;
        }
        if (b * b == a * a + c * c) {
            space = c;
            return space;
        }

        double p = (a + b + c) / 2;// 半周长
        double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
        space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）

        return getFlag(x1, y1, x2, y2, x0, y0) * space;
    }
    /**
     * 判断在线的左侧还是右侧
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x0
     * @param y0
     * @return
     */
    public static int getFlag(double x1, double y1, double x2, double y2,
                              double x0, double y0) {
        int flag = 1;

        double a = (y1 - y2) / (x1 - x2);
        double b = y1 - a * x1;

        double m = x0 + a * y0;

        // 求两直线的交点坐标
        double xc = (m - a * b) / (a * a + 1);
        double yc = a * xc + b;

        if (y1 > y2) {
            if (x0 < xc)
                flag = -1;
        } else {
            if (x0 > xc)
                flag = -1;
        }

        Log.i("xc", "xc" + Double.toString(xc) + "yc" + Double.toString(yc));

        return flag;
    }
    /**
     * 获取两点间的角度
     *
     * @param offsetX
     * @param offsetY
     * @return
     */
    public static float getOffsetAngle(float offsetX, float offsetY) {
        float x = offsetX;
        float y = offsetY;
        return (float) (Math.atan(y / x) / Math.PI * 180);// 最终角度
    }
    public static double[] reAbLineCoord(double[] setAbLineCoord, double distance,
                                         Double degreer) {

        double[] newAbLineCoord = new double[4];

        double a = (setAbLineCoord[3] - setAbLineCoord[1])
                / (setAbLineCoord[2] - setAbLineCoord[0]);

        double degree = Math.abs(Math.atan(a));
        // double degree = ((Math.atan(a) / Math.PI * 180));
        /*
         * double s=Math.sin(degree); double c=Math.cos(degree); double pp=s+c;
         */
        // double degree = ((Math.atan(a) / Math.PI * 180));
        /*
         * if (degree < 0) degree += 90;
         */

        double distant_x = Math.sin(Math.toRadians(degreer)) * distance;
        double distant_y = Math.cos(Math.toRadians(degreer)) * distance;
        Log.v("DEv123",
                degree + ";" + Math.sin(degree) + ";" + Math.cos(degree));
        /*
         * if (setAbLineCoord[2] > setAbLineCoord[0]) { newAbLineCoord[0] =
         * setAbLineCoord[0] - distant_x; newAbLineCoord[2] = setAbLineCoord[2]
         * - distant_x; } else {
         */
        if (0 < degreer && degreer <= 90) {
            newAbLineCoord[0] = setAbLineCoord[0] + distant_x;
            newAbLineCoord[2] = setAbLineCoord[2] + distant_x;
            newAbLineCoord[1] = setAbLineCoord[1] - distant_y;
            newAbLineCoord[3] = setAbLineCoord[3] - distant_y;
        } else if (90 < degreer && degreer <= 180) {
            newAbLineCoord[0] = setAbLineCoord[0] + distant_x;
            newAbLineCoord[2] = setAbLineCoord[2] + distant_x;
            newAbLineCoord[1] = setAbLineCoord[1] - distant_y;
            newAbLineCoord[3] = setAbLineCoord[3] - distant_y;
        } else if (180 < degreer && degreer <= 270) {
            newAbLineCoord[0] = setAbLineCoord[0] - distant_x;
            newAbLineCoord[2] = setAbLineCoord[2] - distant_x;
            newAbLineCoord[1] = setAbLineCoord[1] + distant_y;
            newAbLineCoord[3] = setAbLineCoord[3] + distant_y;
        } else {
            newAbLineCoord[0] = setAbLineCoord[0] - distant_x;
            newAbLineCoord[2] = setAbLineCoord[2] - distant_x;
            newAbLineCoord[1] = setAbLineCoord[1] + distant_y;
            newAbLineCoord[3] = setAbLineCoord[3] + distant_y;
        }
        /*
         * } if (setAbLineCoord[3] > setAbLineCoord[1]) {
         */

        /*
         * } else { newAbLineCoord[1] = setAbLineCoord[1] - distant_y;
         * newAbLineCoord[3] = setAbLineCoord[3] - distant_y; }
         */
        double[] pointA = new double[]{setAbLineCoord[0], setAbLineCoord[1]};
        double[] pointB = new double[]{setAbLineCoord[2], setAbLineCoord[3]};
        double[] CurrentPoint = new double[]{newAbLineCoord[0],
                newAbLineCoord[1]};
        double[] CurrentPoint1 = new double[]{newAbLineCoord[2],
                newAbLineCoord[3]};

        double[] verticalPoint = getVerticalPoint(pointA, pointB,
                CurrentPoint);
        double[] verticalPoint1 = getVerticalPoint(pointA, pointB,
                CurrentPoint1);

        double abc = Math.sqrt((verticalPoint[0] - newAbLineCoord[0])
                * (verticalPoint[0] - newAbLineCoord[0])
                + (verticalPoint[1] - newAbLineCoord[1])
                * (verticalPoint[1] - newAbLineCoord[1]));
        double abc1 = Math.sqrt((verticalPoint1[0] - newAbLineCoord[2])
                * (verticalPoint1[0] - newAbLineCoord[2])
                + (verticalPoint1[1] - newAbLineCoord[3])
                * (verticalPoint1[1] - newAbLineCoord[3]));
        Log.v("QQC", abc + ";" + degree);

        return newAbLineCoord;
    }
    /**
     * 获取点到直线的交点
     *
     * @param pointA
     * @param pointB
     * @param pointX
     * @return
     */
    public static double[] getVerticalPoint(double[] pointA, double[] pointB,
                                            double[] pointX) {
        double fa = pointB[1] - pointA[1];
        double fb = pointA[0] - pointB[0];
        double fc = pointA[1] * pointB[0] - pointA[0] * pointB[1];
        double[] foot = new double[] {
                (fb * fb * pointX[0] - fa * fb * pointX[1] - fa * fc)
                        / (fa * fa + fb * fb),
                (fa * fa * pointX[1] - fa * fb * pointX[0] - fb * fc)
                        / (fa * fa + fb * fb) };// 垂足

        return foot;
    }

}
