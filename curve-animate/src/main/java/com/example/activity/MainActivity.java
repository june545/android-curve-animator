package com.example.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.view.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	private Button btn_circle;
	private Button btn_simple_bezier;
	private Button btn_bezier;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		btn_circle = (Button) findViewById(R.id.circle_btn);
		btn_simple_bezier = (Button) findViewById(R.id.bezier1_btn);
		btn_bezier = (Button) findViewById(R.id.bezier2_btn);

		btn_circle.setOnClickListener(this);
		btn_simple_bezier.setOnClickListener(this);
		btn_bezier.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
			case R.id.circle_btn:
				startActivity(new Intent(getApplicationContext(), CircleActivity.class));
				break;
			case R.id.bezier1_btn:
				startActivity(new Intent(getApplicationContext(), SimpleBezierActivity.class));
				break;
			case R.id.bezier2_btn:
				startActivity(new Intent(getApplicationContext(), BubbleActivity.class));
				break;
		}
	}
}
