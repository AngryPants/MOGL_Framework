package com.sidm.mogl_framework;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

	private GLSurfaceView glSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Hide Title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Hide Top (Fullscreen)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		glSurfaceView = new GLSurfaceView(this);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		if (configurationInfo.reqGlEsVersion >= 0x20000) {
			glSurfaceView.setEGLContextClientVersion(2);
			glSurfaceView.setRenderer(new MainRenderer(this));
		} else {
			throw new RuntimeException("The device does not support OpenGL ES 2.0!");
		}

		setContentView(glSurfaceView);
	}
}
