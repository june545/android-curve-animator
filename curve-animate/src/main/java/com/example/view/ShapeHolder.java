package com.example.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.view.animation.DecelerateInterpolator;

import com.example.util.HeartDrawer;

import java.util.Random;

/**
 * Created by June on 2016/8/2.
 */
public class ShapeHolder {
    long duration;
    int color;
    Path path;
    PathMeasure pathMeasure;
    PointF pointF;
    int mAlpha;
    float sizeScale;
    boolean animatorEnd = false;

    public ShapeHolder() {
        Random random = new Random();
        color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    private void startAnim() {
        pathMeasure = new PathMeasure(path, false);
        float length = pathMeasure.getLength();
        ValueAnimator animator = ValueAnimator.ofFloat(length);
        animator.setDuration(duration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float l = (float) animation.getAnimatedValue();
                float[] pos = new float[2];
                pathMeasure.getPosTan(l, pos, null);
                pointF = new PointF(pos[0], pos[1]);
            }
        });

        ValueAnimator aVA = ValueAnimator.ofInt(255, 255, 255, 0);
        aVA.setDuration((long) (new Random().nextInt((int) (duration / 5F)) + duration / 5F * 4));
        aVA.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mAlpha = (int) animation.getAnimatedValue();
            }
        });

        ValueAnimator scaleAnim = ValueAnimator.ofFloat(0, 1F);
        scaleAnim.setCurrentPlayTime(500);
        scaleAnim.setDuration(1000);
        scaleAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                sizeScale = (float) valueAnimator.getAnimatedValue();
            }
        });

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.play(animator).with(aVA).with(scaleAnim);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                animatorEnd = true;
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animatorSet.start();
    }

    public void draw(Canvas canvas) {
        if (pointF != null) {
            //				System.out.println("2 ################ " + pointF.x + ",  " + pointF.y + " " + path);
            HeartDrawer.drawHeart1(canvas, pointF.x, pointF.y, 30 * sizeScale, mAlpha, color);
        }
        //			Paint paint = new Paint();
        //			paint.setColor(Color.GREEN);
        //			paint.setStrokeWidth(3);
        //			paint.setStyle(Paint.Style.STROKE);
        //			canvas.drawPath(path, paint);
    }

    public void start() {
        startAnim();
    }
}
