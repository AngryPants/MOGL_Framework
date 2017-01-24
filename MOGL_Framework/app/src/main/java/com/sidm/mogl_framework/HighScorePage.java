package com.sidm.mogl_framework;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by AFTERSHOCK on 27/11/2016.
 */

public class HighScorePage extends Activity {

	private Button btn_back;

	//share pref
	SharedPreferences SharedPrefScore;
	SharedPreferences SharedPrefName;

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
		setContentView(R.layout.highscorepage);

		TextView scoreText;
		scoreText = (TextView)findViewById(R.id.scoreText);
		//retrieve highscore and playername
		int highscore=0;
		String playername = "player";

		SharedPrefScore = getSharedPreferences("HighScore", Context.MODE_PRIVATE);
		highscore = SharedPrefScore.getInt("HighScore",0);

		SharedPrefName = getSharedPreferences("PlayerName",Context.MODE_PRIVATE);
		playername = SharedPrefName.getString("PlayerName","Default");

		//Print player name and score
		scoreText.setText(String.format(playername + ": " + "%02d",highscore));
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