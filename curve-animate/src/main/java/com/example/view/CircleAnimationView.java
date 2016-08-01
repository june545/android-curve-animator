package com.example.view;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.util.HeartDrawer;

/**
 * Created by June on 2016/7/29.
 */
public class CircleAnimationView extends View {

	int minDegree = 0;
	int maxDegree = 360;

	int radius;
	float cx1;
	float cy1;
	boolean drawRing;

	public CircleAnimationView(Context context) {
		super(context);
		init();
	}

	public CircleAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CircleAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(){
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				drawRing = true;
				startAnim();
			}
		});

		startAnim();
	}

	private void startAnim(){
		ValueAnimator valueAnimator = new ValueAnimator();
		valueAnimator.setInterpolator(new LinearInterpolator());
		valueAnimator.setDuration(10000);
		valueAnimator.setIntValues(360, 0);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (int) valueAnimator.getAnimatedValue();
				double r = Math.PI / 180f * value;
				int w = getRight() - getLeft();
				int h = getBottom() - getTop();
				cx1 = (float) Math.sin(r) * radius + w/2;
				cy1 = (float) Math.cos(r) * radius + h/2;
				System.out.println("=====================" + value + ", " + cx1 + ", " + cy1);
				invalidate();
			}
		});
		valueAnimator.start();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();

		int w = getRight() - getLeft();
		int h = getBottom() - getTop();

		// center point
		float cx = w/2;
		float cy = h/2;

		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
		radius = getRight()/2 - 200;
		canvas.drawCircle(cx, cy, radius, paint); // draw a big circle

		paint.setColor(Color.BLUE);

		canvas.drawCircle(cx1, cy1, 10, paint); // draw a small ring

		canvas.restore();
	}


}
