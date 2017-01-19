package com.sidm.mogl_framework;

import android.content.Context;
import android.graphics.Canvas;
import android.opengl.GLES10;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

/*
SurfaceView - https://developer.android.com/reference/android/view/SurfaceView.html

Multi-touch:
MotionEvent - https://developer.android.com/reference/android/view/MotionEvent.html
StackOverflow Link - http://stackoverflow.com/questions/4268426/android-difference-between-action-up-and-action-pointer-up

Android thinks about touch events in terms of gestures.
A gesture in this sense includes all events from the first finger that touches the screen to the last finger that leaves the screen.
A single gesture's entire event sequence is always sent to the same view that was picked during the initial ACTION_DOWN unless a parent intercepts the event stream for some reason.
If a parent intercepts a child's event stream, the child will get ACTION_CANCEL.

If you're working with multitouch events, always use the value returned by getActionMasked() to determine the action.
If you don't need multitouch or are working with an older platform version, you can ignore the ACTION_POINTER_* events.
- ACTION_DOWN is for the first finger that touches the screen. This starts the gesture. The pointer data for this finger is always at index 0 in the MotionEvent.
- ACTION_POINTER_DOWN is for extra fingers that enter the screen beyond the first. The pointer data for this finger is at the index returned by getActionIndex().
- ACTION_POINTER_UP is sent when a finger leaves the screen but at least one finger is still touching it. The last data sample about the finger that went up is at the index returned by getActionIndex().
- ACTION_UP is sent when the last finger leaves the screen. The last data sample about the finger that went up is at index 0. This ends the gesture.
- ACTION_CANCEL means the entire gesture was aborted for some reason. This ends the gesture.
*/

public class GamePanelSurfaceView extends GLSurfaceView implements SurfaceHolder.Callback {

	//Have we been initialised?
	private Object mtLock;
	private boolean ready;

	//Our Renderer
	GLESRenderer glESRenderer;

	//The Screen's Width & Height
	private int screenWidth, screenHeight;
	//Thread to control rendering.
	private GameThread gameThread;

	//Test Variable(s)
	Camera2D testCamera;
	private GameObject testGameObject;

	//Constructor(s)
	public GamePanelSurfaceView(Context _context) {
		//Context is the current state of the application/object. An Activity is a child class of Context.
		super(_context);

		//Adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		mtLock = new Object();

		glESRenderer = null;

		//Create our rendering thread.
		gameThread = new GameThread(this);

		//Get our display metrics.
		DisplayMetrics displayMetrics = _context.getResources().getDisplayMetrics();
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;

		System.out.println("GamePanelSurfaceView Constructor finished.");
	}

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

	//Load our assets
	private void LoadMeshes() {
		MeshBuilder.GenerateQuad("Test GameObject Mesh", new Vertex.Color(1.0f, 1.0f, 1.0f, 1.0f), 1.0f);
		System.out.println("Meshes Loaded.");
	}
	private void LoadTextures() {
		TextureManager.AddTexture("Test GameObject Texture", getContext(), R.drawable.test_texture, true);
		System.out.println("Textures Loaded.");
	}
	private void ReleaseMeshes() {
		MeshBuilder.ReleaseMesh("Test GameObject Mesh");
		System.out.println("Meshes Released.");
	}
	private void ReleaseTextures() {
		TextureManager.ReleaseTexture("Test GameObject Texture");
		System.out.println("Textures Released.");
	}
	private void LoadGameObjects() {
		testGameObject = new TestGameObject(this, glESRenderer);
		testCamera = new Camera2D();
		System.out.println("GameObjects Loaded.");
	}
	private void DestroyGameObjects() {
		testGameObject.Destroy();
		System.out.println("GameObjects Destroyed.");
	}

	private class LoadAssets implements Runnable {
		LoadAssets() {}
		@Override
		public void run() {
			glESRenderer.LoadShaders();
			LoadMeshes();
			LoadTextures();
			LoadGameObjects();
			SetReady(true);
			System.out.println("Assets Loaded.");
		}
	}

	//Implement Interface Function(s)
	public void Initialise() {
		queueEvent(new LoadAssets());
	}

	public void Update(double _deltaTime) {
		//Update Camera
		testCamera.height = 2.0f;
		testCamera.width = ((float)screenWidth/(float)screenHeight) * testCamera.height;
		//Update GameObjects
		testGameObject.Update(_deltaTime);
	}

	private class DrawRequest implements Runnable {
		DrawRequest() {}
		@Override
		public void run() {
			//Clear Buffer
			glESRenderer.ClearBuffer(GLESRenderer.DEPTH_BUFFER_BIT | GLESRenderer.COLOR_BUFFER_BIT | GLESRenderer.STENCIL_BUFFER_BIT);
			glESRenderer.SetToCamera2DView(testCamera);
			testGameObject.Draw();
		}
	}
	public void Draw() {
		if (glESRenderer == null) {
			throw new RuntimeException("Error: GamePanelSurfaceView's glESRenderer is null!");
		}

		glESRenderer.AddToRenderQueue(new DrawRequest());
		//System.out.println("Added To Render Queue.");
		requestRender();
		//System.out.println("Requested Render.");
	}

	private class DestroyAssets implements Runnable {
		DestroyAssets() {}
		@Override
		public void run() {
			DestroyGameObjects();
			ReleaseMeshes();
			ReleaseTextures();
			glESRenderer.DeleteShaders();
			SetReady(false);
			System.out.println("Assets Destroyed.");
		}
	}

	public void Destroy() {
		queueEvent(new DestroyAssets());
	}

	//Other(s)
	public void setGLRenderer(GLESRenderer _glESRenderer) {
		super.setRenderer(_glESRenderer);
		glESRenderer = _glESRenderer;
	}

	//Overrides
	@Override
	public void setRenderer(Renderer _renderer) {
		throw new RuntimeException("GamePanelSurfaceView cannot setRenderer()! Use setGLRenderer() instead.");
	}

	@Override
	public boolean onTouchEvent(MotionEvent _event) {
		return true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder _surfaceHolder) {
		super.surfaceCreated(_surfaceHolder);

		//Start our thread.
		if (gameThread.isAlive() == false) {
			gameThread = new GameThread(this);
			gameThread.start();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder _surfaceHolder, int _format, int _width, int _height) {
		super.surfaceChanged(_surfaceHolder, _format, _width, _height);
		screenWidth = _width;
		screenHeight = _height;
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder _surfaceHolder) {
		super.surfaceDestroyed(_surfaceHolder);

		//Stop our thread.
		if (gameThread.isAlive()) {
			gameThread.StopThread();
		}
		while (true) {
			try {
				gameThread.join(); //Wait for gameThread to finish.
				break;
			} catch (InterruptedException e) {
				//Do nothing.
			}
		}
	}

}