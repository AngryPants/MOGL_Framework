package com.sidm.mogl_framework;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.v4.view.MotionEventCompat;
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
	private Player player;

	//Joysticks to handle player input.
	public JoystickInfo joystickMove;
	private Vector2 joystickMoveResetPosition;
	public JoystickInfo joystickShoot;
	private Vector2 joystickShootResetPosition;

	private MeshBuilder.Mesh joystickMesh;
	private Textures textureJoystickMove;
	private Textures textureJoystickShoot;
	private Textures textureJoystickPivot;

	//Constructor(s)
	public GamePanelSurfaceView(Context _context) {
		//Context is the current state of the application/object. An Activity is a child class of Context.
		super(_context);

		//Adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);

		//Our GLESRenderer
		glESRenderer = null;

		//Create our rendering thread.
		gameThread = new GameThread(this);
		//A lock for locking resources during multi-threading to prevent 2 threads getting the same resource at the same time.
		mtLock = new Object();

		//Get our display metrics.
		DisplayMetrics displayMetrics = _context.getResources().getDisplayMetrics();
		screenWidth = displayMetrics.widthPixels;
		screenHeight = displayMetrics.heightPixels;

		//Initialise our Joysticks
		//Coordinates range from 0.0f to 1.0f for both axis. Y-Axis starts from the top.
		joystickMoveResetPosition = new Vector2(GetScreenRatio() * 0.2f, 0.7f);
		joystickMove = new JoystickInfo(joystickMoveResetPosition, 0.3f);
		joystickShootResetPosition = new Vector2(GetScreenRatio() * 0.8f, 0.7f);
		joystickShoot = new JoystickInfo(joystickShootResetPosition, 0.3f);

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
		MeshBuilder.GenerateQuad("Quad", new Vertex.Color(0.0f, 1.0f, 1.0f, 1.0f), 1.0f);
		System.out.println("Meshes Loaded.");
	}
	private void LoadTextures() {
		TextureManager.AddTexture("Test GameObject Texture", getContext(), R.drawable.test_texture, true);
		TextureManager.AddTexture("Joystick Move", getContext(), R.drawable.joystick_move, true);
		TextureManager.AddTexture("Joystick Shoot", getContext(), R.drawable.joystick_shoot, true);
		TextureManager.AddTexture("Joystick Pivot", getContext(), R.drawable.joystick_pivot, true);
		System.out.println("Textures Loaded.");
	}
	private void ReleaseMeshes() {
		MeshBuilder.ReleaseMesh("Quad");
		System.out.println("Meshes Released.");
	}
	private void ReleaseTextures() {
		TextureManager.ReleaseTexture("Test GameObject Texture");
		TextureManager.ReleaseTexture("Joystick Move");
		TextureManager.ReleaseTexture("Joystick Shoot");
		TextureManager.ReleaseTexture("Joystick Pivot");
		System.out.println("Textures Released.");
	}
	private void LoadGameObjects() {
		player = new Player(this, glESRenderer);
		testCamera = new Camera2D();
		System.out.println("GameObjects Loaded.");
	}
	private void DestroyGameObjects() {
		player.Destroy();
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

			//Mesh(es) & Textures for our Joysticks
			joystickMesh = MeshBuilder.GetMesh("Quad");
			textureJoystickMove = new Textures();
			textureJoystickMove.handles[0] = TextureManager.GetTextureID("Joystick Move");
			textureJoystickShoot = new Textures();
			textureJoystickShoot.handles[0] = TextureManager.GetTextureID("Joystick Shoot");
			textureJoystickPivot = new Textures();
			textureJoystickPivot.handles[0] = TextureManager.GetTextureID("Joystick Pivot");

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
		testCamera.height = 5.0f;
		testCamera.width = ((float)screenWidth/(float)screenHeight) * testCamera.height;
		//Update GameObjects
		player.Update(_deltaTime);

		//Move the player. Remember to invert Y-Axis!
		Vector3 playerTranslation = new Vector3(joystickMove.GetAxis().x, -joystickMove.GetAxis().y, 0.0f);
		playerTranslation.TimesEqual((float)_deltaTime);
		player.position.PlusEqual(playerTranslation);
	}

	private class DrawRequest implements Runnable {
		DrawRequest() {}
		@Override
		public void run() {
			//Clear Buffer
			glESRenderer.ClearBuffer(GLESRenderer.DEPTH_BUFFER_BIT | GLESRenderer.COLOR_BUFFER_BIT | GLESRenderer.STENCIL_BUFFER_BIT);

			//Clear the matrice stacks
			glESRenderer.modelStack.Clear();
			glESRenderer.viewStack.Clear();
			glESRenderer.projectionStack.Clear();

			//Enable Depth and Render
			glESRenderer.Enable(GLESRenderer.DEPTH_TEST);
			glESRenderer.SetToCamera2DView(testCamera);
			player.Draw();

			//Clear the matrice stacks
			glESRenderer.modelStack.Clear();
			glESRenderer.viewStack.Clear();
			glESRenderer.projectionStack.Clear();

			//Disable Depth and RenderUI
			glESRenderer.Disable(GLESRenderer.DEPTH_TEST);
			glESRenderer.SetToUIView();
			player.DrawUI();

			//Render our Joysticks
			Matrix4x4Stack modelStack = glESRenderer.modelStack;
			modelStack.PushMatrix();
				/*
					Convert Joystick values to screen coordinates:
					joystickShoot.GetPositionPivot().y (Joystick's Y-Position)
					(1.0f - joystickShoot.GetPositionPivot().y) (Input Y-Axis and Rendering Y-Axis are flipped)
					(1.0f - (-1.0f) - 1.0f) (ScreenTop - ScreenBottom) - ScreenBottom.
				*/
				modelStack.Translate(GetScreenRatio(true) * joystickShoot.GetPositionPivot().x * (1.0f - (-1.0f)) - 1.0f, (1.0f - joystickShoot.GetPositionPivot().y) * (1.0f - (-1.0f)) - 1.0f, 0.0f);
				modelStack.Scale(GetScreenRatio(true) * joystickShoot.GetDiameter(), joystickShoot.GetDiameter(), 1.0f);
				glESRenderer.Render(joystickMesh, textureJoystickPivot);
			modelStack.PopMatrix();

			modelStack.PushMatrix();
				modelStack.Translate(GetScreenRatio(true) * joystickMove.GetPositionPivot().x * (1.0f - (-1.0f)) - 1.0f, (1.0f - joystickMove.GetPositionPivot().y) * (1.0f - (-1.0f)) - 1.0f, 0.0f);
				modelStack.Scale(GetScreenRatio(true) * joystickMove.GetDiameter(), joystickMove.GetDiameter(), 1.0f);
				glESRenderer.Render(joystickMesh, textureJoystickPivot);
			modelStack.PopMatrix();

			modelStack.PushMatrix();
				modelStack.Translate(GetScreenRatio(true) * joystickShoot.GetPositionCurrent().x * (1.0f - (-1.0f)) - 1.0f, (1.0f - joystickShoot.GetPositionCurrent().y) * (1.0f - (-1.0f)) - 1.0f, 0.0f);
				modelStack.Scale(GetScreenRatio(true) * joystickShoot.GetRadius(), joystickShoot.GetRadius(), 1.0f);
				glESRenderer.Render(joystickMesh, textureJoystickShoot);
			modelStack.PopMatrix();

			modelStack.PushMatrix();
				modelStack.Translate(GetScreenRatio(true) * joystickMove.GetPositionCurrent().x * (1.0f - (-1.0f)) - 1.0f, (1.0f - joystickMove.GetPositionCurrent().y) * (1.0f - (-1.0f)) - 1.0f, 0.0f);
				modelStack.Scale(GetScreenRatio(true) * joystickMove.GetRadius(), joystickMove.GetRadius(), 1.0f);
				glESRenderer.Render(joystickMesh, textureJoystickMove);
			modelStack.PopMatrix();
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
	public void SetGLRenderer(GLESRenderer _glESRenderer) {
		super.setRenderer(_glESRenderer);
		glESRenderer = _glESRenderer;
	}

	public int GetScreenWidth() {
		return screenWidth;
	}
	public int GetScreenHeight() {
		return screenHeight;
	}
	//Returns the ratio of the screen width over screen height.
	public float GetScreenRatio() {
		return GetScreenRatio(false);
	}
	//Returns the ratio of the screen width over screen height if _inverse == false.
	//Returns the ratio of the screen height over screen width if _inverse == true.
	public float GetScreenRatio(final boolean _inverse) {
		if (_inverse) {
			return (float)screenHeight/(float)screenWidth;
		} else {
			return (float)screenWidth/(float)screenHeight;
		}
	}

	//Overrides
	@Override
	public void setRenderer(Renderer _renderer) {
		throw new RuntimeException("GamePanelSurfaceView cannot setRenderer()! Use setGLRenderer() instead.");
	}

	private void JoystickEvent(MotionEvent _motionEvent) {
		//If the event starts from the left side of the screen, then it is meant for joystickMove.
		//If the event starts from the right side of the screen, then it is meant for joystickShoot.

		//int action = _event.getActionMasked();
		int action = MotionEventCompat.getActionMasked(_motionEvent);

		//Get the index and ID of the pointer associated with the action.
		//THE INDEX & ID OF A POINTER IS DIFFERENT!
		//The INDEX is the pointer's slot in the array in MotionEvent. It may change within the pointer's lifetime.
		//The ID is a number assigned to the pointer. It is constant throughout the pointer's lifetime.
		int index = MotionEventCompat.getActionIndex(_motionEvent);
		int id = _motionEvent.getPointerId(index);

		//The coordinates of the current screen contact, relative to the responding View or Activity.
		float xPos = _motionEvent.getX(index);
		float yPos = _motionEvent.getY(index);

		//Normalise xPos and yPos.
		xPos /= (float)screenHeight;
		yPos /= (float)screenHeight;

		System.out.println("Pointer xPos: " + String.valueOf(xPos));
		System.out.println("Pointer yPos: " + String.valueOf(yPos));

		/*if (_event.getPointerCount() > 1) {
			//Multi touch event.
		} else {
			//Single touch event.
		}*/

		switch (action) {
			case MotionEvent.ACTION_DOWN: { //Start of a new gesture. A finger has touched the view after a time where NO fingers were touching the view.
				//Assign a pointer to a joystick.
				if (xPos < 0.5f) {
					joystickMove.actionPointerID = id;
					joystickMove.SetPositionPivot(xPos, yPos);
					System.out.println("Assigned ID " + String.valueOf(id) + " to joystickMove.");
				} else {
					joystickShoot.actionPointerID = id;
					joystickShoot.SetPositionPivot(xPos, yPos);
					System.out.println("Assigned ID " + String.valueOf(id) + " to joystickShoot.");
				}
			}
			break;
			case MotionEvent.ACTION_POINTER_DOWN: { //A another finger has touched the screen.
				//Assign a pointer to a joystick.
				if (xPos < 0.5f) {
					if (!joystickMove.HasValidActionPointerID()) {
						joystickMove.actionPointerID = id;
						joystickMove.SetPositionPivot(xPos, yPos);
						System.out.println("Assigned ID " + String.valueOf(id) + " to joystickMove.");
					}
				} else {
					if (!joystickShoot.HasValidActionPointerID()) {
						joystickShoot.actionPointerID = id;
						joystickShoot.SetPositionPivot(xPos, yPos);
						System.out.println("Assigned ID " + String.valueOf(id) + " to joystickShoot.");
					}
				}
			}
			break;
			case MotionEvent.ACTION_MOVE: {
				//Unlike the rest of the actions which can be fired as separate events, move is related to every pointer.
				//Thus when ACTION_MOVE is triggered, there can be more than 1 pointer triggering it.
				//Hence we need to iterate through every pointer to check.
				for (int p_index = 0;  p_index < _motionEvent.getPointerCount(); ++p_index) {
					if (_motionEvent.getPointerId(p_index) == joystickMove.actionPointerID) {
						float currentPosX = _motionEvent.getX(p_index) / (float)screenHeight;
						float currentPosY = _motionEvent.getY(p_index) / (float)screenHeight;
						joystickMove.SetPositionCurrent(currentPosX, currentPosY);
						//System.out.println("joystickMove moved.");
					}
					if (_motionEvent.getPointerId(p_index) == joystickShoot.actionPointerID) {
						float currentPosX = _motionEvent.getX(p_index) / (float)screenHeight;
						float currentPosY = _motionEvent.getY(p_index) / (float)screenHeight;
						joystickShoot.SetPositionCurrent(currentPosX, currentPosY);
						//System.out.println("joystickShoot moved.");
					}
				}
			}
			break;
			case MotionEvent.ACTION_UP: { //The last finger has stopped touching the screen.
				joystickMove.ResetActionPointerID();
				joystickMove.SetPositionPivot(joystickMoveResetPosition);
				joystickShoot.ResetActionPointerID();
				joystickShoot.SetPositionPivot(joystickShootResetPosition);
				System.out.println("All fingers have been lifted from the screen.");
			}
			break;
			case MotionEvent.ACTION_POINTER_UP: {
				if (id == joystickMove.actionPointerID) {
					joystickMove.ResetActionPointerID();
					joystickMove.SetPositionPivot(joystickMoveResetPosition);
					System.out.println("joystickMove reset.");
				} else if (id == joystickShoot.actionPointerID) {
					joystickShoot.ResetActionPointerID();
					joystickShoot.SetPositionPivot(joystickShootResetPosition);
					System.out.println("joystickShoot reset.");
				}
			}
			break;
			case MotionEvent.ACTION_OUTSIDE: {
				//Do the same thing as ACTION_UP?
				/*joystickMove.ResetActionPointerID();
				joystickMove.SetPositionPivot(joystickMoveResetPosition);
				joystickShoot.ResetActionPointerID();
				joystickShoot.SetPositionPivot(joystickShootResetPosition);
				System.out.println("All fingers have been lifted from the screen.");*/

				//Do nothing? <- Emphasis on the question mark.
				System.out.println("ACTION_OUTSIDE? No idea what that means.");
			}
			break;
			case MotionEvent.ACTION_CANCEL: {
				joystickMove.ResetActionPointerID();
				joystickMove.SetPositionPivot(joystickMoveResetPosition);
				joystickShoot.ResetActionPointerID();
				joystickShoot.SetPositionPivot(joystickShootResetPosition);
				System.out.println("Gesture canceled.");
			}
			break;
			default:
				//Do nothing
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent _motionEvent) {
		JoystickEvent(_motionEvent);
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