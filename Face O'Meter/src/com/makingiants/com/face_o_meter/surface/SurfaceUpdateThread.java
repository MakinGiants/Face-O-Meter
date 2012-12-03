package com.makingiants.com.face_o_meter.surface;

import com.makingiants.com.face_o_meter.RotationView;

import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Call the onDraw of the DrawableView that contains
 */
public class SurfaceUpdateThread extends Thread {
	private final SurfaceHolder _surfaceHolder;
	private final RotationView _drawableView;
	private boolean _run = false;
	
	public SurfaceUpdateThread(final RotationView drawableView) {
		_surfaceHolder = ((SurfaceView) drawableView).getHolder();
		_drawableView = drawableView;
	}
	
	public void setRunning(final boolean run) {
		_run = run;
	}
	
	@Override
	public void run() {
		Canvas c;
		while (_run) {
			c = null;
			try {
				c = _surfaceHolder.lockCanvas(null);
				synchronized (_surfaceHolder) {
					_drawableView.onDraw(c);
				}
			} finally {
				// do this in a finally so that if an exception is thrown
				// during the above, we don't leave the Surface in an
				// inconsistent state
				if (c != null) {
					_surfaceHolder.unlockCanvasAndPost(c);
				}
			}
		}
	}
}
