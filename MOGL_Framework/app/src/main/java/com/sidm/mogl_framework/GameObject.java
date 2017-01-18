package com.sidm.mogl_framework;
//Created by <Insert Name> on 17/1/2017.

import android.opengl.GLSurfaceView;

public class GameObject {

	//Variable(s)
	private Object mtLock; //Lock for synchronising multiple threads.
	private boolean ready;
	protected GLSurfaceView glSurfaceView;
	protected GLESRenderer glESRenderer;

	//Constructor(s)
	public GameObject(GLSurfaceView _glSurfaceView, GLESRenderer _glESRenderer) {
		mtLock = new Object();
		ready = false;
		glSurfaceView = _glSurfaceView;
		glESRenderer = _glESRenderer;
	}

	//Function(s)
	public void Update(double _deltaTime) {
		if (IsReady() == false) {
			return;
		}
	}
	public void Draw() {
		if (IsReady() == false) {
			return;
		}
	}

	//Psuedo Destructor
	public void Destroy() {
	}

	//Other(s)
	final public boolean IsReady() {
		synchronized (mtLock) {
			return ready;
		}
	}
	final protected void SetReady(boolean _val) {
		synchronized (mtLock) {
			ready = _val;
		}
	}

}