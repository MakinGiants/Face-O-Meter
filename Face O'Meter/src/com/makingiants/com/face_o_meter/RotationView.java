package com.makingiants.com.face_o_meter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.makingiants.com.face_o_meter.R;
import com.makingiants.com.face_o_meter.surface.SurfaceUpdateThread;

public class RotationView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {
	
	// ****************************************************************
	// Attributes
	// ****************************************************************
	
	// Draw flow variables
	private final SurfaceUpdateThread updateThread;
	
	// Accel variables
	private SensorManager mSensorManager;
	
	// Screen colors and text variables
	private float accelY;
	private int degrees;
	private int background;
	private int backgroundRed;
	
	private int textSize;
	private final Paint paintText;
	private final int textX, textY;
	
	// Screen size variables
	private final int screenWidth, screenHeight;
	
	// Objects
	private final Face face;
	
	// ****************************************************************
	// Constructor
	// ****************************************************************
	
	public RotationView(final Context context) {
		super(context);
		getHolder().addCallback(this);
		setKeepScreenOn(true);
		setFocusable(true);
		
		this.textSize = getResources().getInteger(R.integer.text_size);
		
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
		        .getDefaultDisplay();
		
		this.screenWidth = display.getWidth();
		this.screenHeight = display.getHeight();
		this.textX = 0;
		this.textY = screenHeight - textSize / 3;
		
		this.mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
		this.face = new Face(context, screenWidth / 2, screenHeight / 2);
		this.updateThread = new SurfaceUpdateThread(this);
		
		this.paintText = new Paint();
		paintText.setColor(Color.BLACK);
		paintText.setStyle(Style.FILL);
		paintText.setTextSize(textSize);
		paintText.setAntiAlias(true);
		
		this.background = Color.BLACK;
	}
	
	// ****************************************************************
	// Flows 
	// ****************************************************************
	
	public void startDrawFlow() {
		mSensorManager.registerListener(this,
		        mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
		        SensorManager.SENSOR_DELAY_FASTEST);
		
	}
	
	public void stopDrawFlow() {
		mSensorManager.unregisterListener(this);
		
	}
	
	// ****************************************************************
	// Draw methods
	// ****************************************************************
	
	@Override
	public void onDraw(final Canvas canvas) {
		//canvas.drawColor(backgroundColor);
		canvas.drawColor(background);
		
		face.draw(canvas);
		
		canvas.drawText(String.format("%d¼", degrees), textX, textY, paintText);
	}
	
	@Override
	public void surfaceChanged(final SurfaceHolder arg0, final int arg1, final int arg2, final int arg3) {
	}
	
	@Override
	public void surfaceCreated(final SurfaceHolder arg0) {
		
		updateThread.setRunning(true);
		updateThread.start();
		
	}
	
	@Override
	public void surfaceDestroyed(final SurfaceHolder arg0) {
		
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		updateThread.setRunning(false);
		
		while (retry) {
			try {
				updateThread.join();
				retry = false;
			} catch (final InterruptedException e) {
				// we will try it again and again...
			}
		}
		
	}
	
	// ****************************************************************
	// SensorEventListener Overrides
	// ****************************************************************
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		
		accelY = Float.valueOf(String.format("%.2f", event.values[1]));
		
		if (face.manageRotation(accelY)) {
			background = Color.BLACK;
			paintText.setColor(Color.GREEN);
		} else {
			
			// Wee need max color when max accel arrived
			if (accelY >= 0) {
				backgroundRed = (int) (accelY * 30);
			} else {
				backgroundRed = (int) (accelY * -30);
			}
			if (backgroundRed > 255) {
				backgroundRed = 255;
			}
			
			background = Color.rgb(backgroundRed, 0, 0);
			paintText.setColor(Color.WHITE);
		}
		
		degrees = (int) (accelY * -10);
		
	}
}
