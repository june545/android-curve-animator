package com.example.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.view.HeartSurfaceView;
import com.example.view.R;

import java.util.Random;

public class BubbleActivity extends AppCompatActivity {

	HeartSurfaceView heartSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bubble);

		heartSurfaceView = (HeartSurfaceView) findViewById(R.id.surface);
		init();
	}

	private void init(){
		final Random random = new Random();
		final Handler handler = new Handler();
		Runnable r = new Runnable() {
			@Override
			public void run() {
				heartSurfaceView.addShapeHolder();
				handler.postDelayed(this, 50 * random.nextInt(2));
			}
		};
		handler.postDelayed(r, 50);
	}
}
