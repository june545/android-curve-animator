package com.example.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * Created by June on 2016/8/1.
 */
public class HeartDrawer {

    public static Path d(){
        Path p = new Path();
        p.moveTo(75,40);
        p.cubicTo(75,37,70,25,50,25);
        p.cubicTo(20,25,22,62.5F,22,55);
        p.cubicTo(20,80,40,102,75,120);
        p.cubicTo(110,102,130,80,128,55);
        p.cubicTo(128,55,130,25,100,25);
        p.cubicTo(85,25,75,37,75,40);

//        p.moveTo(0,0);
//        p.cubicTo(0,-3,-5,-15,-25,-15);
//        p.cubicTo(20,25,22,62.5F,22,55);
//        p.cubicTo(20,80,40,102,75,120);
//        p.cubicTo(110,102,130,80,128,55);
//        p.cubicTo(128,55,130,25,100,25);
//        p.cubicTo(85,25,75,37,75,40);
        return p;
    }

	/**
	 * moveTo(0, 0)
     * @param width
     * @param height
     * @return
     */
    public static Path buildHeart(float width, float height){
        return buildHeart(width, height, 0, 0);
    }

	/**
     *
     * @param width
     * @param height
     * @param x
     * @param y
     * @return
     */
    public static Path buildHeart(float width, float height, float x, float y){
        float r = width / 4;
        float dh = height - r;

        float o1x = x - r, o1y = y;
        float o2x = x + r, o2y = y;

        float bottomX = x, bottomY = y + dh;
        double radius = Math.atan((double) r / dh);
        double d1 = Math.toDegrees(radius);

        double d2 = 90 - d1 - d1;
        double radius2 = Math.toRadians(d2);
        double delX = Math.sin(radius2) * r;
        double delY = Math.cos(radius2) * r;

        float lx = (float) (x - r - delX), ly = (float) (y + delY); // 左圆切点
//        float rx = (float) (x + r + delX), ry = (float) (y + delY); // 右圆切点

        Path p = new Path();
        p.moveTo(bottomX, bottomY);
        p.lineTo(lx, ly);
        RectF rectF1 = new RectF(o1x - r, o1y - r, o1x + r, o1y + r);
        p.arcTo(rectF1, (float) (90+d2), (float) (270-d2)); // left arc
        RectF rectF2 = new RectF(o2x - r, o2y - r, o2x + r, o2y + r);
        p.arcTo(rectF2, 180, (float) (270 - d2)); // right arc
        p.close();

//        canvas.drawArc(new RectF(o1x - r, o1y - r, o1x + r, o1y + r), (float) (90 + d2), (float) (270 - d2), true, paint);
//        canvas.drawArc(new RectF(o2x - r, o2y - r, o2x + r, o2y + r), 180, (float) (270 - d2), true, paint);

        return p;
    }

    /**
     * @param canvas
     * @param x      中间菱形（实际为正方形）x坐标
     * @param y      中间菱形（实际为正方形）y坐标
     * @param l      正方形中心点到顶点的长度，决定了图案的大小
     */
    public static void drawHeart(Canvas canvas, float x, float y, float l, int alpha) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAlpha(alpha);

        float p1x = x - l, p1y = y;
        float p2x = x, p2y = y - l;
        float p3x = x + l, p3y = y;
        float p4x = x, p4y = y + l;

        Path p = new Path();
        p.moveTo(p1x, p1y);
        p.lineTo(p2x, p2y);
        p.lineTo(p3x, p3y);
        p.lineTo(p4x, p4y);
        canvas.drawPath(p, paint);

        int r = (int) Math.sqrt((p1x - p2x) * (p1x - p2x) + (p1y - p2y) * (p1y - p2y)) / 2;

        float o1x = (p1x + p2x) / 2 + 0.5F, o1y = (p1y + p2y) / 2 + 0.5F;
        canvas.drawArc(new RectF(o1x - r, o1y - r, o1x + r, o1y + r), 135, 180, true, paint);

        float o2x = (p2x + p3x) / 2 + 0.5F, o2y = (p2y + p3y) / 2 - 0.5F;
        canvas.drawArc(new RectF(o2x - r, o2y - r, o2x + r, o2y + r), 225, 180, true, paint);
    }

    /**
     * @param canvas
     * @param x      中间凹槽点x
     * @param y      中间凹槽点y
     * @param r      左右两个圆的半径，决定了图案的大小
     */
    public static void drawHeart1(Canvas canvas, float x, float y, float r, Paint paint) {
        float dh = r * 2.5f;

        float o1x = x - r, o1y = y;
        float o2x = x + r, o2y = y;

        float bottomX = x, bottomY = y + dh;
        double radius = Math.atan((double) r / dh);
        double d1 = Math.toDegrees(radius);

        double d2 = 90 - d1 - d1;
        double radius2 = Math.toRadians(d2);
        double delX = Math.sin(radius2) * r;
        double delY = Math.cos(radius2) * r;

        float lx = (float) (x - r - delX), ly = (float) (y + delY); // 左圆切点
        float rx = (float) (x + r + delX), ry = (float) (y + delY); // 右圆切点

        Path p = new Path();
        p.moveTo(lx, ly);
        p.lineTo(o1x, o1y);
        p.lineTo(x, y);
        p.lineTo(o2x, o2y);
        p.lineTo(rx, ry);
        p.lineTo(bottomX, bottomY);
        canvas.drawPath(p, paint);

        canvas.drawArc(new RectF(o1x - r, o1y - r, o1x + r, o1y + r), (float) (90 + d2), (float) (270 - d2), true, paint);
        canvas.drawArc(new RectF(o2x - r, o2y - r, o2x + r, o2y + r), 180, (float) (270 - d2), true, paint);
    }

    public static void drawHeart1(Canvas canvas, float x, float y, float r, int alpha, int color) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setAlpha(alpha);
        drawHeart1(canvas, x, y, r, paint);
    }

    public static void drawHeart1(Canvas canvas, float x, float y, float r, int alpha) {
        drawHeart1(canvas, x, y, r, alpha, Color.RED);
    }

    public static void drawHeart1(Canvas canvas, float x, float y, float r) {
        drawHeart1(canvas, x, y, r, 255, Color.RED);
    }

}
