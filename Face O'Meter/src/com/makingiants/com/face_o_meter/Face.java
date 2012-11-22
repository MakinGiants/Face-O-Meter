package com.makingiants.com.face_o_meter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import com.makingiants.com.face_o_meter.R;

public class Face {
	
	// ****************************************************************
	// Attributes
	// ****************************************************************
	
	// Ranges
	private float accelRange, accelRangeNegative;
	
	// Bitmap attributes
	private Bitmap bitmap, bitmapCenter, bitmapRotated;
	private float imageMiddleWidth, imageMiddleHeight;
	private float posX, posY;
	private Matrix matrix;
	private Paint paint;
	
	// ****************************************************************
	// Constructor
	// ****************************************************************
	
	public Face(Context context, int x, int y) {
		// Load eah face
		this.bitmapCenter = BitmapFactory.decodeResource(context.getResources(),
		        R.drawable.face_center);
		this.bitmapRotated = BitmapFactory.decodeResource(context.getResources(),
		        R.drawable.face_rotated);
		this.bitmap = bitmapCenter;
		
		// Image position
		float imageWidth = bitmap.getWidth();
		float imageHeight = bitmap.getHeight();
		
		this.imageMiddleWidth = imageWidth / 2;
		this.imageMiddleHeight = imageHeight / 2;
		
		this.posX = x - imageMiddleWidth;
		this.posY = y - imageMiddleHeight;
		this.matrix = new Matrix();
		matrix.postTranslate(posX, posY);
		
		// For Antialias and resolution 
		this.paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		
		// Load Ranges
		accelRange = Float.valueOf(context.getString(R.string.accelerometer_range));
		accelRangeNegative = -accelRange;
	}
	
	// ****************************************************************
	// Draw
	// ****************************************************************
	
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, matrix, paint);
		
	}
	
	// ****************************************************************
	// Rotation Changes Updates
	// ****************************************************************
	
	/**
	 * Check if the face should be rotated based on acceleration
	 * 
	 * @param accelY
	 * @return True if the rotation is not needed and false otherwise
	 */
	public boolean manageRotation(float accelY) {
		
		this.matrix = new Matrix();
		
		if (accelY < accelRange && accelY > accelRangeNegative) {
			bitmap = bitmapCenter;
			matrix.postTranslate(posX, posY);
			return true;
			
		} else {
			bitmap = bitmapRotated;
			matrix.postRotate(accelY * 10.0f, imageMiddleWidth, imageMiddleHeight);
			matrix.postTranslate(posX, posY);
			return false;
		}
		
	}
}
