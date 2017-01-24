package com.sidm.mogl_framework;
//Created by <Insert Name> on 16/1/2017.

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {

	//Variable(s)
	//The current state of our Game Thread.
	private boolean stop;

	//A surface holder to access the device's physical surface.
	private GamePanelSurfaceView gamePanelSurfaceView;

	//To Calculate Framerate
	long previousTime, currentTime;

	//Constructor(s)
	public GameThread(GamePanelSurfaceView _gamePanelSurfaceView) {
		//Call our parent's constructor
		super();

		//Initialise Variable(s)
		gamePanelSurfaceView = _gamePanelSurfaceView;
		stop = false;
		currentTime = System.currentTimeMillis();
		previousTime = currentTime;

	}

	public void StopThread() {
		System.out.println("Stop Thread");
		stop = true;
	}
	public boolean IsStopped() {
		return stop;
	}

	//Returns the time pasted since last frame in seconds.
	private double CalculateDeltaTime() {
		currentTime = System.currentTimeMillis();
		double deltaTime = (double)(currentTime - previousTime) * 0.001;
		previousTime = currentTime;
		return deltaTime;
	}

	//Override(s)
	@Override
	public void run() {
		//Initialise
		gamePanelSurfaceView.Initialise();

		//Wait for gamePanelSurfaceView to be ready to start.
		while (gamePanelSurfaceView.IsReady() == false) {
			try {
				sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		//Update & Render the game.
		/*while (stop == false) {
			try {
				gamePanelSurfaceView.Update(CalculateDeltaTime());
				gamePanelSurfaceView.Draw();
				sleep(10); //Sleep so that the GL Thread has a chance to run.
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}*/

		while (stop == false) {
			System.out.println("GameThread While Loop");
			gamePanelSurfaceView.Update(CalculateDeltaTime());
			gamePanelSurfaceView.Draw();
		}

		//Clean up
		gamePanelSurfaceView.Destroy();
	}

}