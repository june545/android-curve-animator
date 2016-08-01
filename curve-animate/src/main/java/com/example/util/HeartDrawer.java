package com.example.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * Created by June on 2016/8/1.
 */
public class HeartDrawer {
    /**
     *
     * @param canvas
     * @param x 中间菱形（实际为正方形）x坐标
     * @param y 中间菱形（实际为正方形）y坐标
     * @param l 正方形中心点到顶点的长度，决定了图案的大小
     */
    public static void drawHeart(Canvas canvas, float x, float y, float l, int alpha){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAlpha(alpha);

        float p1x = x-l, p1y = y;
        float p2x = x, p2y = y-l;
        float p3x = x+l, p3y = y;
        float p4x = x, p4y = y+l;

        Path p = new Path();
        p.moveTo(p1x, p1y);
        p.lineTo(p2x, p2y);
        p.lineTo(p3x, p3y);
        p.lineTo(p4x, p4y);
        canvas.drawPath(p,  paint);

        int r = (int) Math.sqrt((p1x - p2x)*(p1x - p2x) + (p1y - p2y)*(p1y - p2y)) / 2;

        float o1x = (p1x + p2x)/2, o1y = (p1y + p2y)/2;
        canvas.drawCircle(o1x, o1y, r, paint);

        float o2x = (p2x + p3x)/2, o2y = (p2y + p3y)/2;
        canvas.drawCircle(o2x, o2y, r, paint);
    }

    /**
     *
     * @param canvas
     * @param x 中间凹槽点x
     * @param y 中间凹槽点y
     * @param r 左右两个圆的半径，决定了图案的大小
     */
    public static void drawHeart1(Canvas canvas, float x, float y, float r, int alpha){
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAlpha(alpha);

        float dh = r * 2.5f;

        float o1x = x - r, o1y = y;
        float o2x = x + r, o2y = y;

        canvas.drawCircle(o1x, o1y, r, paint);
        canvas.drawCircle(o2x, o2y, r, paint);

        float bottomX = x, bottomY = y + dh;
        double radius = Math.atan((double)r / dh);
        double degree = Math.toDegrees(radius);

        double d = 90 - degree - degree;
        double radius2 = Math.toRadians(d);
        double delX = Math.sin(radius2) * r;
        double delY = Math.cos(radius2) * r;

        float lx = (float) (x - r - delX), ly = (float) (y + delY);
        float rx = (float) (x + r + delX), ry = (float) (y + delY);

        Path p = new Path();
        p.moveTo(lx, ly);
        p.lineTo(x, y);
        p.lineTo(rx, ry);
        p.lineTo(bottomX, bottomY);
        canvas.drawPath(p, paint);
    }
}
