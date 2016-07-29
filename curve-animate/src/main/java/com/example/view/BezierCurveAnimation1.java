package com.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by June on 2016/7/29.
 */
public class BezierCurveAnimation1 extends View {

	public BezierCurveAnimation1(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init(){

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

		List<Point> points = buildPath(new Point(w/2, 0), 3, 10, h);
		for (Point point : points){
			canvas.drawCircle(point.x, point.y, 4, paint);
		}

		canvas.restore();
	}

	float intensity = 0.2F;
	/**
	 * 画曲线
	 * @param path
	 * @param points
	 */
	private void drawFllowerPath(Path path, List<Point> points) {
//		if (points.size() > 1) {
//			for (int j = 0; j < points.size(); j++) {
//
//				Point point = points.get(j);
//				float dx;
//				float dy;
//
//				if (j == 0) {
//					Point next = points.get(j + 1);
//					dx = ((next.x - point.x) * intensity);
//					dy = ((next.y - point.y) * intensity);
//				} else if (j == points.size() - 1) {
//					Point prev = points.get(j - 1);
//					dx = ((point.x - prev.x) * intensity);
//					dy = ((point.y - prev.y) * intensity);
//				} else {
//					Point next = points.get(j + 1);
//					Point prev = points.get(j - 1);
//					dx = ((next.x - prev.x) * intensity);
//					dy = ((next.y - prev.y) * intensity);
//				}
//
//				// create the cubic-spline path
//				if (j == 0) {
//					path.moveTo(point.x, point.y);
//				} else {
//					Point prev = points.get(j - 1);
//					path.cubicTo(prev.x + dx, (prev.y + prev.dy),
//							point.x - point.dx, (point.y - point.dy),
//							point.x, point.y);
//				}
//			}
//		}
	}


	/**
	 * 画路径
	 *
	 * @param point
	 * @return
	 */
	private List<Point> builderPath(Point point,int range, int height) {
		int quadCount = 5;
		List<Point> points = new ArrayList<Point>();
		Random random = new Random();
		for (int i = 0; i < quadCount; i++) {
			if (i == 0) {
				points.add(point);

			} else {
				Point tmp = new Point(0, 0);
				if (random.nextInt(2) % 2 == 0) {
					tmp.x = point.x + random.nextInt(range);
				} else {
					tmp.x = point.x - random.nextInt(range);
				}
				tmp.y = (int) (height / (float) quadCount * i);
				points.add(tmp);
			}
		}
		return points;
	}

	private List<Point> buildPath(Point anchor, int peaks, int range, int height){
		List<Point> points = new ArrayList<>();
		Random random = new Random();
		for(int i = 0; i < peaks; i++){
			if(i == 0){
				points.add(anchor);
			}else{
				int x,y;
				if(random.nextInt(2) % 2 == 0){
					x = anchor.x + random.nextInt(range);
				}else{
					x = anchor.x - random.nextInt(range);
				}
				y = (int) ((float) height / peaks * i);
				points.add(new Point(x, y));
			}
		}

		return points;
	}

	private Path buildPath(List<Point> points){


		return null;
	}
}
