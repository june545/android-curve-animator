package com.example.curve_animate;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by June on 2016/7/29.
 */
public class CurveAnimationView extends View {

	Path mPath;
	PointF ball;

	PathMeasure pathMeasure;

	public CurveAnimationView(Context context) {
		super(context);
		init();
	}

	public CurveAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CurveAnimationView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init(){
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				anim1();
			}
		});
	}

	private void anim1(){
		pathMeasure = new PathMeasure(mPath, false);
		float length = pathMeasure.getLength();

		ValueAnimator animator = ValueAnimator.ofFloat(0, length);
		animator.setDuration(5000);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				float l = (float) valueAnimator.getAnimatedValue();
				float[] pos = new float[2];
				pathMeasure.getPosTan(l, pos, null);
				ball = new PointF(pos[0], pos[1]);
				invalidate();
			}
		});
		animator.start();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();

		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);

		Path path = new Path();
		path.moveTo(0,0);
		path.quadTo(getRight()/2 + 200, getBottom()/2, getRight(), getBottom());
		mPath = path;
		canvas.drawPath(path, paint);

		if(ball != null){
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.RED);
			canvas.drawCircle(ball.x, ball.y, 10, paint);
		}


		canvas.restore();
	}


}
