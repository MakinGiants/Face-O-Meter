package com.makingiants.com.face_o_meter.activities;

import android.app.Activity;
import android.os.Bundle;

import com.makingiants.com.face_o_meter.RotationView;

public class MainActivity extends Activity {
	
	RotationView view;
	
	// ****************************************************************
	// Activity Overrides
	// ****************************************************************
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new RotationView(this);
		
		setContentView(view);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		view.startDrawFlow();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		view.stopDrawFlow();
		
		finish();
	}
	
}
