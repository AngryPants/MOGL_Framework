package com.sidm.mogl_framework;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by AFTERSHOCK on 24/11/2016.
 */

public class MainMenu extends Activity implements View.OnClickListener {

	private Button button_start;
	private Button button_highScore;
	private Button button_options;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Call our parent class's function.
		super.onCreate(savedInstanceState);

		//Hide Title
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//Hide Top (Fullscreen)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mainmenu);

		//Go to resources, find a thingy with the id btn_start, which happens to be
		//named the same thing as our start button.
		button_start = (Button)findViewById(R.id.button_start);
		button_start.setOnClickListener(this);

		button_options = (Button)findViewById(R.id.button_options);
		button_options.setOnClickListener(this);

		button_highScore = (Button)findViewById(R.id.button_highScore);
		button_highScore.setOnClickListener(this);
	}

	//What we intend to do when we click on the view that is passed in.
	public void onClick(View button) {
		Intent intent = new Intent();
		if (button == button_start) {
			//Once I click this button, go to another class called GamePage.
			intent.setClass(this, GamePage.class);
		} else if (button == button_highScore) {
			intent.setClass(this, HighScorePage.class);
		} else if (button == button_options) {
			intent.setClass(this, OptionsPage.class);
		}
		startActivity(intent); //Start the activity.
	}

	protected void onPause() {
		super.onPause();
	}

	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

}
