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
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.example.util.HeartDrawer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Created by June on 2016/8/2.
 */
public class BezierSurface extends SurfaceView implements SurfaceHolder.Callback {
	static final int DURATION = 3000;

	final Object LOCK = new Object();
	Vector<ShapeHolder> holders = new Vector<>();

	MyThread updateThread;

	public BezierSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		getHolder().addCallback(this);

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				addShapeHolder();
			}
		});

		updateThread = new MyThread(getHolder());
	}

	private void addShapeHolder() {
		int w = getRight() - getLeft();
		int h = getBottom() - getTop();
		ShapeHolder holder = new ShapeHolder();
		holder.setPath(buildBezierPath(w, h));
		synchronized (LOCK) {
			holders.add(holder);
		}
		holder.start();
	}

	private List<Point> buildPoints(int w, int h, int count, int range) {
		Random random = new Random();
		List<Point> points = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			int x;
			if (random.nextInt(2) % 2 == 0) {
				x = (int) (w / 2 + random.nextInt(range / 2) + range / 4F * (count - i));
			} else {
				x = (int) (w / 2 - random.nextInt(range / 2) - range / 4F * (count - i));
			}

			if (i == 0) {
				points.add(new Point(x, 0));
			} else {
				int y = (int) ((float) h / count * i);
				points.add(new Point(x, y));
			}
		}

		// the end point
		points.add(new Point(w / 2, h));

		return points;
	}

	private List<Point> midPoints(List<Point> points) {
		List<Point> midPoints = new ArrayList<>();
		for (int i = 0; i < points.size() - 1; i++) {
			Point p = points.get(i);
			Point next = points.get(i + 1);
			midPoints.add(new Point((p.x + next.x) / 2, (p.y + next.y) / 2));
		}
		return midPoints;
	}

	// 至少画两个点
	private Path buildBezierPath(int w, int h) {
		List<Point> points = buildPoints(w, h, 5, w / 4);
		Collections.reverse(points);
		List<Point> midPoints = midPoints(points);
		List<Point> midMidPoints = midPoints(midPoints);

		List<Point> ctrlPoints = new ArrayList<>();
		for (int i = 0; i < midMidPoints.size(); i++) {
			int x1 = points.get(i + 1).x - midMidPoints.get(i).x + midPoints.get(i).x;
			int y1 = points.get(i + 1).y - midMidPoints.get(i).y + midPoints.get(i).y;
			ctrlPoints.add(new Point(x1, y1));

			int x2 = points.get(i + 1).x - midMidPoints.get(i).x + midPoints.get(i + 1).x;
			int y2 = points.get(i + 1).y - midMidPoints.get(i).y + midPoints.get(i + 1).y;
			ctrlPoints.add(new Point(x2, y2));
		}

		Path path = new Path();
		for (int i = 0; i < points.size(); i++) {
			if (i == 0) {
				path.moveTo(points.get(i).x, points.get(i).y);
				path.quadTo(ctrlPoints.get(i).x, ctrlPoints.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
			} else if (i < points.size() - 2) {
				path.cubicTo(ctrlPoints.get(i * 2 - 1).x, ctrlPoints.get(i * 2 - 1).y, ctrlPoints.get(i * 2).x, ctrlPoints.get(i * 2).y, points.get(i + 1).x, points.get(i + 1).y);
			} else if (i == points.size() - 2) {
				path.quadTo(ctrlPoints.get(i * 2 - 1).x, ctrlPoints.get(i * 2 - 1).y, points.get(i + 1).x, points.get(i + 1).y);
			}
		}

		return path;
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		updateThread.isRunning = true;
		updateThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		updateThread.isRunning = false;
	}

	class MyThread extends Thread {
		boolean       isRunning;
		SurfaceHolder surfaceHolder;

		public MyThread(SurfaceHolder holder) {
			this.surfaceHolder = holder;
		}

		@Override
		public void run() {
			while (isRunning) {
				post(new Runnable() {
					@Override
					public void run() {
						addShapeHolder();
					}
				});

				Canvas canvas = surfaceHolder.lockCanvas();
				if (canvas == null) {
					continue;
				}
				Paint p = new Paint();
				p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
				canvas.drawPaint(p);
				try {
					if (holders != null && holders.size() > 0) {
						synchronized (LOCK) {
							Iterator<ShapeHolder> it = holders.iterator();
							while (it.hasNext()) {
								ShapeHolder holder = it.next();
								if (holder.animatorEnd) {

									it.remove();
								} else {
									holder.draw(canvas);
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (surfaceHolder != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	class ShapeHolder {
		int         color;
		Path        path;
		PathMeasure pathMeasure;
		PointF      pointF;
		int         mAlpha;
		float       sizeScale;
		boolean animatorEnd = false;

		public ShapeHolder() {
			Random random = new Random();
			color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
		}

		public void setPath(Path path) {
			this.path = path;
		}

		private void startAnim() {
			pathMeasure = new PathMeasure(path, false);
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
}
