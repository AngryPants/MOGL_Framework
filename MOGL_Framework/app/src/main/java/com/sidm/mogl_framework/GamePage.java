package com.sidm.mogl_framework;
//Created by <Insert Name> on 16/1/2017.

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GamePage extends Activity {

	//Overrides
	@Override
	public void onCreate(Bundle _savedInstanceState) {
		//Call our parent's function.
		super.onCreate(_savedInstanceState);

		//Hide Title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Hide Top
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		GamePanelSurfaceView gamePanelSurfaceView = new GamePanelSurfaceView(this,this);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		if (configurationInfo.reqGlEsVersion >= 0x00020000) {
			gamePanelSurfaceView.setEGLContextClientVersion(2);
			gamePanelSurfaceView.SetGLRenderer(new GLESRenderer(this));
			gamePanelSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
			//gamePanelSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		} else {
			throw new RuntimeException("The device does not support OpenGL ES 2.0!");
		}

		//Set our view
		setContentView(gamePanelSurfaceView);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

}