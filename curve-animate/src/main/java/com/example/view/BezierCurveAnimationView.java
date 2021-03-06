package com.example.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.util.HeartDrawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 经过已知点的贝塞尔曲线
 *
 * Created by June on 2016/7/29.
 */
public class BezierCurveAnimationView extends View {

	Path mPath;
	PathMeasure pathMeasure;
	PointF mPoint;
	int mAlpha;
	float sizeScale;
	boolean animatorEnd = true;

	public BezierCurveAnimationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if(!animatorEnd){
					return;
				}
				anim();
			}
		});
	}

	private void anim(	){
		animatorEnd = false;
		pathMeasure = new PathMeasure(mPath,false);
		float length = pathMeasure.getLength();
		ValueAnimator animator = ValueAnimator.ofFloat(length);
		animator.setDuration(4000);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float l = (float) animation.getAnimatedValue();
				float[] pos = new float[2];
				pathMeasure.getPosTan(l, pos, null);
				mPoint = new PointF(pos[0], pos[1]);
				invalidate();
			}
		});
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationEnd(Animator animation) {
				animatorEnd = true;
			}

			@Override
			public void onAnimationCancel(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {

			}
		});

		ValueAnimator aVA = ValueAnimator.ofInt(255, 255, 255, 0);
		aVA.setCurrentPlayTime(3000);
		aVA.setDuration(new Random().nextInt(1000) + 3000);
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
		animatorSet.start();
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.save();

		int w = getRight() - getLeft();
		int h = getBottom() - getTop();

		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);

		if(animatorEnd)
		 drawPath(canvas, w, h);
		else canvas.drawPath(mPath,paint);

		if(mPoint != null){
			HeartDrawer.drawHeart1(canvas, mPoint.x, mPoint.y, 20 * sizeScale, mAlpha);
		}

		canvas.restore();
	}

	// 至少画两个点
	private void drawPath(Canvas canvas, int w, int h){
		Paint paint = new Paint();
		paint.setStrokeWidth(1);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.GREEN);

		List<Point> points = buildPoints(w, h, 5, w/5);
		Collections.reverse(points);
		List<Point> midPoints = midPoints(points);
		List<Point> midMidPoints = midPoints(midPoints);

		drawSegmentPath(canvas, points);
		drawPoint(canvas, midPoints, Color.BLUE);
		drawPoint(canvas, midMidPoints, Color.GREEN);

		List<Point> ctrlPoints = new ArrayList<>();
		for (int i=0; i < midMidPoints.size(); i++){
			int x1 = points.get(i+1).x - midMidPoints.get(i).x + midPoints.get(i).x;
			int y1 = points.get(i+1).y - midMidPoints.get(i).y + midPoints.get(i).y;
			ctrlPoints.add(new Point(x1, y1));

			int x2 = points.get(i+1).x - midMidPoints.get(i).x + midPoints.get(i+1).x;
			int y2 = points.get(i+1).y - midMidPoints.get(i).y + midPoints.get(i+1).y;
			ctrlPoints.add(new Point(x2, y2));
		}
		drawPoint(canvas, ctrlPoints, Color.YELLOW);

		Path path = new Path();
		for (int i=0; i < points.size(); i++){
			if(i == 0){
				path.moveTo(points.get(i).x, points.get(i).y);
				path.quadTo(ctrlPoints.get(i).x, ctrlPoints.get(i).y, points.get(i+1).x, points.get(i+1).y);
			}else if(i < points.size() - 2){
				path.cubicTo(ctrlPoints.get(i*2-1).x, ctrlPoints.get(i*2-1).y, ctrlPoints.get(i*2).x, ctrlPoints.get(i*2).y, points.get(i+1).x, points.get(i+1).y);
			}else if(i == points.size() -2){
				path.quadTo(ctrlPoints.get(i*2-1).x, ctrlPoints.get(i*2-1).y, points.get(i+1).x, points.get(i+1).y);
			}
		}

		canvas.drawPath(path, paint);
		mPath = path;
	}

	private List<Point> buildPoints(int w, int h, int count, int range){
		List<Point> points = new ArrayList<>();
		Random random = new Random();
		for(int i = 0; i < count; i++){
			int x;
			if(random.nextInt(2) % 2 == 0){
				x = w/2 + random.nextInt(range);
			}else{
				x = w/2 - random.nextInt(range);
			}

			if(i == 0){
				points.add(new Point(x, 0));
			}else{
				int y = (int) ((float) h / count * i);
				points.add(new Point(x, y));
			}
		}

		// the end point
		points.add(new Point(w/2, h));
		return points;
	}

	private List<Point> midPoints(List<Point> points){
		List<Point> midPoints = new ArrayList<>();
		for (int i = 0; i < points.size() - 1; i++){
			Point p = points.get(i);
			Point next = points.get(i + 1);
			midPoints.add(new Point((p.x + next.x) / 2, (p.y + next.y) / 2));
		}
		return midPoints;
	}

	// 已知点连成的线段
	private void drawSegmentPath(Canvas canvas, List<Point> points){
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);

		Path path = new Path();
		for (int i=0; i < points.size(); i++){
			if(i == 0){
				path.moveTo(points.get(i).x, points.get(i).y);
			}else{
				path.lineTo(points.get(i).x, points.get(i).y);
			}
		}
		canvas.drawPath(path, paint);
	}

	private void drawPoint(Canvas canvas, List<Point> points, int color){
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.FILL);

		for (Point point : points){
			canvas.drawCircle(point.x, point.y, 5, paint);
		}
	}

}
