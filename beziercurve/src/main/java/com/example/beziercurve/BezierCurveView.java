package com.example.beziercurve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by June on 2016/7/28.
 */
public class BezierCurveView extends View{

    Point control = new Point();
    Point control2 = new Point();

    Point max = null;

    public BezierCurveView(Context context) {
        super(context);
        init();
    }

    public BezierCurveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BezierCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(control != null){
                    control.x = new Random().nextInt(max.x);
                    control.y = max.y/2 + new Random().nextInt(max.y/2);

                    control2.x = new Random().nextInt(max.x);
                    control2.y = new Random().nextInt(max.y / 2);
                    invalidate();
                }
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();

        Point start = new Point(getRight()/2, getBottom());
        Point end = new Point(new Random().nextInt(getRight()), 0);

        if(max == null)
            max = new Point(getRight(), getBottom());
//        Point p2 = new Point(getRight(), getBottom()/2);



        Paint paintQ = new Paint();
        paintQ.setAntiAlias(true);
        paintQ.setStyle(Paint.Style.STROKE);
        paintQ.setStrokeWidth(5);
        paintQ.setColor(Color.RED);

        Path path = new Path();
        path.moveTo(start.x, start.y);
        path.quadTo(control.x, control.y, new Random().nextInt(getRight()), getBottom()/2);
        path.quadTo(control2.x, control2.y, end.x,end.y);

        canvas.drawPath(path, paintQ);

        canvas.restore();
    }
}
