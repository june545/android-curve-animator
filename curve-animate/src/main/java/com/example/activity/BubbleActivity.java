package com.example.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.example.view.HeartSurfaceView;
import com.example.view.R;

import java.util.Random;

public class BubbleActivity extends AppCompatActivity {

	HeartSurfaceView heartSurfaceView;

	int intervaltime = 1000;
	long lasttime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubble);

		heartSurfaceView = (HeartSurfaceView) findViewById(R.id.surface);
		heartSurfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				if(motionEvent.getAction() == MotionEvent.ACTION_MOVE)
				if(System.currentTimeMillis() - lasttime > 10){
					heartSurfaceView.addShapeHolder();
					lasttime = System.currentTimeMillis();
				}
				return false;
			}
		});
		init();
	}

	private void init(){
		final Random random = new Random();
		final Handler handler = new Handler();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				heartSurfaceView.addShapeHolder();
				handler.postDelayed(this, intervaltime);
			}
		};
		handler.postDelayed(r, intervaltime);
	}
}
