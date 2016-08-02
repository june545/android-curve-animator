package com.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.util.CanvasUtil;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * Created by June on 2016/8/2.
 */
public class BezierSurface extends SurfaceView implements SurfaceHolder.Callback {
    static final int DURATION = 5000;

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
        holder.setPath(CanvasUtil.buildBezierPath(w, h));
        holder.setDuration(DURATION);
        synchronized (LOCK) {
            holders.add(holder);
        }
        holder.start();
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

    /**
     * update canvas
     */
    class MyThread extends Thread {
        boolean isRunning;
        SurfaceHolder surfaceHolder;

        public MyThread(SurfaceHolder holder) {
            this.surfaceHolder = holder;
        }

        @Override
        public void run() {
            while (isRunning) {
                if (new Random().nextInt(20) % 20 == 0)
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
}