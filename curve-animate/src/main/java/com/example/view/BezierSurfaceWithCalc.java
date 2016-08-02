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

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * Created by June on 2016/8/2.
 */
public class BezierSurfaceWithCalc extends SurfaceView implements SurfaceHolder.Callback {

    static final int FRESH_FREQUENCY = 40; // ms

    final Object LOCK = new Object();
    Vector<ShapeHolderWithCalc> holders = new Vector<>();

    MyThread updateThread;

    Paint mPaint = new Paint();

    private int mWidth;
    private int mHeight;

    public BezierSurfaceWithCalc(Context context, AttributeSet attrs) {
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
        ShapeHolderWithCalc holder = new ShapeHolderWithCalc();
        holder.setPath(CanvasUtil.buildBezierPath(mWidth, mHeight));
        holder.setPaint(mPaint);
        synchronized (LOCK) {
            holders.add(holder);
        }
        holder.start();
    }



    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.YELLOW);
        canvas.drawRect(2, 2, mWidth, mHeight, mPaint);
        canvas.restore();
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
                if (new Random().nextInt(10) % 10 == 0)
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
                            Iterator<ShapeHolderWithCalc> it = holders.iterator();
                            while (it.hasNext()) {
                                ShapeHolderWithCalc holder = it.next();
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
                Thread.sleep(FRESH_FREQUENCY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
