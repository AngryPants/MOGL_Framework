package com.sidm.mogl_framework;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by AFTERSHOCK on 27/11/2016.
 */

public class OptionsPage extends Activity {

	//Overrides
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Call our parent's function.
		super.onCreate(savedInstanceState);

		//Hide Title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Hide Top
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//Set our view
		setContentView(R.layout.optionspage);
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