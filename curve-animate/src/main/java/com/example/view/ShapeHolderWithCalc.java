package com.example.view;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;

import com.example.util.HeartDrawer;

import java.util.Random;

/**
 * 持有每一个动画的数据
 * Created by June on 2016/8/2.
 */
public class ShapeHolderWithCalc {
    /** the speed (length/del_time) */
    static final float PATH_SPEED = 0.3F;

    private Paint paint;
    private int         color;
    private Path path;
    private PathMeasure pathMeasure;
    private float totalLength;
    private int         alpha;
    private float       sizeScale;

    long time0;
    long timeLast;
    long startFadeTime;
    float drawedLength;
    double fadingSpeedOfTime;
    PointF point;
    boolean animatorEnd = false;

    public ShapeHolderWithCalc() {
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setPaint(Paint paint){
        this.paint = paint;
    }

    private void init() {
        Random random = new Random();
        color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

        time0 = System.currentTimeMillis();

        pathMeasure = new PathMeasure(path, false);
        totalLength = pathMeasure.getLength();

//					mAlpha = (int) animation.getAnimatedValue();
//					sizeScale = (float) valueAnimator.getAnimatedValue();
    }

    public void start() {
        init();
    }

    /**
     * 刷新状态
     */
    private void update(){
        // 计算drawed path的长度
        long nowTime = System.currentTimeMillis();
        long elapsedTime = nowTime - time0;
        long deltaTime = nowTime - timeLast;
        timeLast = nowTime;

        drawedLength = PATH_SPEED * elapsedTime;
        if( drawedLength > totalLength){
            animatorEnd = true;
        }

        // 计算临近结束时的透明度，让其自然消失
        boolean fading = drawedLength > totalLength * 4 / 10;
        if(fading){
            if(startFadeTime == 0) {
                startFadeTime = nowTime;
                Random random = new Random();
                double tt = totalLength * (random.nextInt(1) + 4) / 10 / PATH_SPEED; // fading的时间数，平滑消失（即在当前view消失，不让其滑动到view边界）
                fadingSpeedOfTime = 255 / tt; // 时间速度
            }

            double a = (int) (fadingSpeedOfTime * (nowTime - startFadeTime)); // fading计时
            System.out.println("44444444444444444 speed=" + fadingSpeedOfTime + ", " + a + ", elapsedTime=" + elapsedTime + ", deltaTime=" + deltaTime);
            alpha = (int) (255 - a);
            alpha = alpha < 0 ? 0 : alpha;
        }else{
            alpha = 255;
        }

        // 计算当前位置
        if(point == null)
            point = new PointF();
        float[] xy = new float[2];
        pathMeasure.getPosTan(drawedLength, xy, null);
        point.x = xy[0];
        point.y = xy[1];
    }

    public void draw(Canvas canvas) {
        update();
        if(animatorEnd)
            return;

//			System.out.println("222222222 t0 " + t0 + "   current " + System.currentTimeMillis());

//			System.out.println("2 ################ " + point.x + ",  " + point.y + " " + path);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        paint.setAlpha(alpha);
        HeartDrawer.drawHeart1(canvas, point.x, point.y, 30, paint);

//			paint.setStrokeWidth(3);
//			paint.setStyle(Paint.Style.STROKE);
//			canvas.drawPath(path, paint);
//			System.out.println("111111111111 drawedLength " + drawedLength + ", totalLength " + totalLength);
    }
}
