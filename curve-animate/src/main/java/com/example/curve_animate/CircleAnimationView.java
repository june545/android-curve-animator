package com.example.curve_animate;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

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
				cx1 = (float) Math.sin(r) * radius + getRight()/2;
				cy1 = (float) Math.cos(r) * radius + getBottom()/2;
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

		// center point
		float cx = getRight()/2;
		float cy = getBottom()/2;

		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);
		radius = getRight()/2 - 200;
		canvas.drawCircle(cx, cy, radius, paint); // draw a big circle


		paint.setColor(Color.BLUE);


		// center point
//		float cx1 = (float) Math.sin(20) * radius + getRight()/2;
//		float cy1 = (float) Math.cos(20) * radius + getBottom()/2;
//		if(drawRing)
		canvas.drawCircle(cx1, cy1, 10, paint); // draw a small ring


		canvas.restore();
	}

	private void anim(){
		ValueAnimator valueAnimator = new ValueAnimator();
		valueAnimator.setInterpolator(new LinearInterpolator());
		valueAnimator.setDuration(10000);
		valueAnimator.setIntValues(360, 0);
		valueAnimator.setEvaluator(new MyTypeEvaluator());
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				int value = (int) valueAnimator.getAnimatedValue();
				double r = Math.PI / 180F * value;
				cx1 = (float) Math.sin(r) * radius + getRight()/2;
				cy1 = (float) Math.cos(r) * radius + getBottom()/2;
				invalidate();
			}
		});
		valueAnimator.start();
	}

	class MyTypeEvaluator implements TypeEvaluator<PointF>{

		@Override
		public PointF evaluate(float v, PointF pointF, PointF t1) {

			return null;
		}
	}

}
