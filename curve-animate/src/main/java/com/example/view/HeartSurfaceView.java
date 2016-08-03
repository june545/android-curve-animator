package com.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.util.CanvasUtil;
import com.example.util.FPSUtil;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by June on 2016/8/2.
 */
public class HeartSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    static final int FPS = 30;

    final Object LOCK = new Object();
    Vector<HeartShapeHolder> holders = new Vector<>();

    RefreshThread refreshThread; // 刷新线程

    Paint mPaint = new Paint();

    private int mWidth;
    private int mHeight;

    public HeartSurfaceView(Context context, AttributeSet attrs) {
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

        refreshThread = new RefreshThread(getHolder());
    }

	/**
     * 添加心形动画
     */
    public void addShapeHolder() {
        HeartShapeHolder holder = new HeartShapeHolder();
        holder.setPath(CanvasUtil.buildBezierPath(mWidth, mHeight));
        holder.setPaint(mPaint);
//        holder.setImage(HeartDrawer.buildHeart(120, 100));
        synchronized (LOCK) {
            holders.add(holder);
        }
        holder.start();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mWidth = w;
        this.mHeight = h;
        System.out.println(" onSizeChanged------ w=" + w + ", h=" + h);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        refreshThread.isRunning = true;
        refreshThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        refreshThread.isRunning = false;
    }

    /**
     * update canvas
     */
    class RefreshThread extends Thread {
        boolean isRunning;
        SurfaceHolder surfaceHolder;

        public RefreshThread(SurfaceHolder holder) {
            this.surfaceHolder = holder;
        }

        @Override
        public void run() {
            while (isRunning) {
                long t0 = System.nanoTime();
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) {
                    continue;
                }
                // 清楚虚影
                Paint p = new Paint();
                p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                canvas.drawPaint(p);

                mPaint.setColor(Color.GREEN);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setTextSize(50);
                canvas.drawText(FPSUtil.get(), 20, 50, mPaint);

                // 绘制holders中的图形
                try {
                    if (holders != null && holders.size() > 0) {
                        synchronized (LOCK) {
                            canvas.drawText("HeartCount:" + holders.size(), 300, 50, mPaint);

                            Iterator<HeartShapeHolder> it = holders.iterator();
                            while (it.hasNext()) {
                                HeartShapeHolder holder = it.next();
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

                // 尽量稳定帧率
                long delt = System.nanoTime() - t0;
                long st = (long) ((1000000000F / FPS - delt) / 1000000);
                if(st > 1) {
                    try {
                        Thread.sleep(st);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
