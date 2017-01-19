package com.sidm.mogl_framework;
//Created by <Insert Name> on 17/1/2017.

import android.opengl.GLSurfaceView;

public class GameObject {

	//Variable(s)
	protected GLSurfaceView glSurfaceView;
	protected GLESRenderer glESRenderer;

	//Constructor(s)
	public GameObject(GLSurfaceView _glSurfaceView, GLESRenderer _glESRenderer) {
		glSurfaceView = _glSurfaceView;
		glESRenderer = _glESRenderer;
	}

	//Function(s)
	public void Update(double _deltaTime) {
	}
	public void Draw() {
	}

	//Psuedo Destructor
	public void Destroy() {
	}

}