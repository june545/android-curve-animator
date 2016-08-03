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
public class HeartShapeHolder {
	/**
	 * the speed (length/del_time)
	 */
	static final float PATH_SPEED = 0.3F;

	private static Path        heartShapePath; // 形状
	static {
		heartShapePath = HeartDrawer.buildHeart(120, 100); // build heart shape
	}

	private Paint       paint;
	private int         color;
	private Path        motionPath; // 移动路径
	private PathMeasure pathMeasure;
	private float       totalLength; // 路径总长度
	private int         alpha; // 透明度
	private float       sizeScale;

	long   time0; // 开始时间
	long   timeLast; // 计时
	long   startFadeTime; // 开始fade的时间
	float  drawedLength; // 已经行进的长度
	double fadingSpeed; // 消失的速度，即alpha值的变化率
	PointF point; // 瞬时位置
	boolean animatorEnd = false; // 结束此动画

	public HeartShapeHolder() {
	}

	public void setPath(Path path) {
		this.motionPath = path;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

	public void setImage(Path path) {
		this.heartShapePath = path;
	}

	private void init() {
		Random random = new Random();
		color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));

		time0 = System.currentTimeMillis();

		pathMeasure = new PathMeasure(motionPath, false);
		totalLength = pathMeasure.getLength();
	}

	public void start() {
		init();
	}

	/**
	 * 刷新状态
	 */
	private void calculateStatus() {
		// 计算drawed path的长度
		long nowTime = System.currentTimeMillis();
		long elapsedTime = nowTime - time0;
		timeLast = nowTime;

		drawedLength = PATH_SPEED * elapsedTime;
		if (drawedLength > totalLength) {
			animatorEnd = true;
		}

		// 计算临近结束时的透明度，让其自然消失
		boolean fading = drawedLength > totalLength * 4 / 10;
		if (fading) {
			if (startFadeTime == 0) {
				startFadeTime = nowTime;
				Random random = new Random();
				double tt = totalLength * (random.nextInt(2) + 4) / 10 / PATH_SPEED; // 透明度255~0变化的总时长，平滑消失（即在当前view消失，不让其滑动到view边界）
				fadingSpeed = 255 / tt; // 时间速度
			}

			double a = (int) (fadingSpeed * (nowTime - startFadeTime)); // elapsed时间计时
//			System.out.println("44444444444444444 speed=" + fadingSpeed + ", " + a + ", elapsedTime=" + elapsedTime + ", deltaTime=" + deltaTime);
			alpha = (int) (255 - a);
			if(alpha < 0){
				alpha = 0;
				animatorEnd = true;
			}
		} else {
			alpha = 255;
		}

		// TODO 开始时的缩放动画

		// 计算当前位置
		if (point == null)
			point = new PointF();
		float[] xy = new float[2];
		pathMeasure.getPosTan(drawedLength, xy, null);
		point.x = xy[0];
		point.y = xy[1];
	}

	public void draw(Canvas canvas) {
		calculateStatus();
		if (animatorEnd)
			return;

		//			System.out.println("2 ################ " + point.x + ",  " + point.y + " " + path);
		paint.reset();
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(color);
		paint.setAlpha(alpha);
		Path  movingImg = new Path();
		heartShapePath.offset(point.x, point.y, movingImg); // 平移到瞬时位置
		canvas.drawPath(movingImg, paint);

		//			paint.setStrokeWidth(3);
		//			paint.setStyle(Paint.Style.STROKE);
		//			canvas.drawPath(motionPath, paint);
		//			System.out.println("111111111111 drawedLength " + drawedLength + ", totalLength " + totalLength);
	}
}
