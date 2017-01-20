package com.sidm.mogl_framework;
//Created by <Insert Name> on 17/1/2017.

import android.opengl.GLSurfaceView;

public class GameObject {

	//Variable(s)
	protected GamePanelSurfaceView gamePanelSurfaceView;
	protected GLESRenderer glESRenderer;

	//Constructor(s)
	public GameObject(GamePanelSurfaceView _gamePanelSurfaceView, GLESRenderer _glESRenderer) {
		gamePanelSurfaceView = _gamePanelSurfaceView;
		glESRenderer = _glESRenderer;
	}

	//Function(s)
	public void Update(double _deltaTime) {
	}
	public void Draw() {
	}
	public void DrawUI() {
	}

	//Psuedo Destructor
	public void Destroy() {
	}

}