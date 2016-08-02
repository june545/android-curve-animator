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
 * Created by June on 2016/7/29.
 */
public class BezierCurveAnimation2 extends View {
	static final int DURATION = 4000;

	Path mPath;
	PathMeasure pathMeasure;
	PointF mPoint;
	int mAlpha;
	float sizeScale;
	boolean animatorEnd = true;

	public BezierCurveAnimation2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){
		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
//				anim();
				testMulti();
			}
		});
	}

	private void anim(	){
		if(!animatorEnd){
			return;
		}

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

//		if(animatorEnd)
//		drawPath(canvas, w, h);

//		if(mPoint != null){
//			HeartDrawer.drawHeart1(canvas, mPoint.x, mPoint.y, 50 * sizeScale, mAlpha);
//		}

		if(holders != null && holders.size() > 0){
			for (ShapeHolder holder : holders){
				holder.draw(canvas);
			}
		}

		canvas.restore();
	}

	// 至少画两个点
	private void drawPath(Canvas canvas, int w, int h){
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStrokeWidth(3);
		paint.setStyle(Paint.Style.STROKE);

		List<Point> points = buildPoints(w, h, new Random().nextInt(3) + 2, w/5);
		Collections.reverse(points);
		List<Point> midPoints = midPoints(points);
		List<Point> midMidPoints = midPoints(midPoints);

//		drawSegmentPath(canvas, points);
//		drawPoint(canvas, midPoints, Color.BLUE);
//		drawPoint(canvas, midMidPoints, Color.GREEN);

		List<Point> ctrlPoints = new ArrayList<>();
		for (int i=0; i < midMidPoints.size(); i++){
			int x1 = points.get(i+1).x - midMidPoints.get(i).x + midPoints.get(i).x;
			int y1 = points.get(i+1).y - midMidPoints.get(i).y + midPoints.get(i).y;
			ctrlPoints.add(new Point(x1, y1));

			int x2 = points.get(i+1).x - midMidPoints.get(i).x + midPoints.get(i+1).x;
			int y2 = points.get(i+1).y - midMidPoints.get(i).y + midPoints.get(i+1).y;
			ctrlPoints.add(new Point(x2, y2));
		}
//		drawPoint(canvas, ctrlPoints, Color.YELLOW);

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

	Random random = new Random(System.currentTimeMillis());
	private List<Point> buildPoints(int w, int h, int count, int range){
		Random random = new Random();
		List<Point> points = new ArrayList<>();
		for(int i = 0; i < count; i++){
			int x;
			if(random.nextInt(2) % 2 == 0){
				x = (int) (w/2 + random.nextInt(range/2) + range /4F * (count-i));
			}else{
				x = (int) (w/2 - random.nextInt(range/2) - range/ 4F * (count-i));
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

	// 至少画两个点
	private Path buildBezierPath(int w, int h){
		List<Point> points = buildPoints(w, h, 5, w/4);
		Collections.reverse(points);
		List<Point> midPoints = midPoints(points);
		List<Point> midMidPoints = midPoints(midPoints);

		//		drawSegmentPath(canvas, points);
		//		drawPoint(canvas, midPoints, Color.BLUE);
		//		drawPoint(canvas, midMidPoints, Color.GREEN);

		List<Point> ctrlPoints = new ArrayList<>();
		for (int i=0; i < midMidPoints.size(); i++){
			int x1 = points.get(i+1).x - midMidPoints.get(i).x + midPoints.get(i).x;
			int y1 = points.get(i+1).y - midMidPoints.get(i).y + midPoints.get(i).y;
			ctrlPoints.add(new Point(x1, y1));

			int x2 = points.get(i+1).x - midMidPoints.get(i).x + midPoints.get(i+1).x;
			int y2 = points.get(i+1).y - midMidPoints.get(i).y + midPoints.get(i+1).y;
			ctrlPoints.add(new Point(x2, y2));
		}
		//		drawPoint(canvas, ctrlPoints, Color.YELLOW);

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

		return  path;
	}

	List<ShapeHolder> holders;

	private void testMulti(){
		holders = new ArrayList<>();
		int w = getRight() - getLeft();
		int h = getBottom() - getTop();
		System.out.println("1 ################ " + w + ",  " + h);
		for(int i = 0; i < 5; i++){
			ShapeHolder holder = new ShapeHolder();
			holder.setPath(buildBezierPath(w, h));
			holders.add(holder);
		}

		ValueAnimator valueAnimator = ValueAnimator.ofInt(1000);
		valueAnimator.setDuration(DURATION);
		valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				invalidate();
			}
		});
		valueAnimator.start();

		for (ShapeHolder holder : holders){
			holder.start();
		}
	}

	class ShapeHolder{
		int         color;
		Path        path;
		PathMeasure pathMeasure;
		PointF      pointF;
		int         mAlpha;
		float       sizeScale;
		boolean animatorEnd = true;

		public ShapeHolder(){
			Random random = new Random();
			color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
			System.out.println("3 ################ " + color);
		}

		public void setPath(Path path){
			this.path = path;
		}

		public Path getPath(){
			return path;
		}

		private void startAnim(){
			animatorEnd = false;
			pathMeasure = new PathMeasure(path,false);
			float length = pathMeasure.getLength();
			ValueAnimator animator = ValueAnimator.ofFloat(length);
			animator.setDuration(DURATION);
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
			aVA.setDuration((long) (new Random().nextInt((int) (DURATION / 5F)) + DURATION / 5F * 4));
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

		public void draw(Canvas canvas){
			System.out.println("2 ################ " + pointF.x + ",  " + pointF.y + " " + path);
			HeartDrawer.drawHeart1(canvas, pointF.x, pointF.y, 30 * sizeScale, mAlpha, color);
//			Paint paint = new Paint();
//			paint.setColor(Color.GREEN);
//			paint.setStrokeWidth(3);
//			paint.setStyle(Paint.Style.STROKE);
//			canvas.drawPath(path, paint);
		}

		public void start(){
			startAnim();
		}
	}
}
