package com.sidm.mogl_framework;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.util.LinkedList;
import java.util.Queue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//Created by <Insert Name> on 13/1/2017.

public class GLESRenderer implements GLSurfaceView.Renderer {

	//Declare Modes
	static final public int CULL_FACE = GLES20.GL_CULL_FACE;
	static final public int DEPTH_TEST = GLES20.GL_DEPTH_TEST;
	static final public int STENCIL_TEST = GLES20.GL_STENCIL_TEST;
	static final public int BLEND = GLES20.GL_BLEND;

	//Declare Buffers
	static final public int DEPTH_BUFFER_BIT = GLES20.GL_DEPTH_BUFFER_BIT;
	static final public int COLOR_BUFFER_BIT = GLES20.GL_COLOR_BUFFER_BIT;
	static final public int STENCIL_BUFFER_BIT = GLES20.GL_STENCIL_BUFFER_BIT;

	//Variable(s)
	private Object mtLock; //Multi-Threading Lock
	private boolean ready;
	private Context context;
	private GLESShaderProgram shaderProgram;

	public Matrix4x4Stack modelStack;
	public Matrix4x4Stack viewStack;
	public Matrix4x4Stack projectionStack;

	private Object renderLock;
	private Queue<Runnable> renderQueue;

	//Shader Attributes (Hardcode them here for now.)
	private int a_Position;
	private int a_Color;
	private int a_Normal;
	private int a_TexCoordinate;

	//Shader Uniforms (Hardcode them here for now.)
	private int u_MVPMatrixHandle;
	private int u_AlphaDiscardValue;
	private int u_TextureOffsetHandle;
	private int u_TextureScaleHandle;
	private int[] u_TextureEnabledHandle;
	private int[] u_TexturesHandle;

	//Constructor(s)
	public GLESRenderer(Context _context) {
		super();

		mtLock = new Object();
		context = _context;
		ready = false;

		//Initialise Matrix Stacks
		modelStack = new Matrix4x4Stack();
		viewStack = new Matrix4x4Stack();
		projectionStack = new Matrix4x4Stack();

		renderLock = new Object();
		renderQueue = new LinkedList<>();

		//Initialise Attributes
		a_Position = 0;
		a_Color = 0;
		a_Normal = 0;
		a_TexCoordinate = 0;

		//Initialise Uniforms
		u_MVPMatrixHandle = 0;
		u_AlphaDiscardValue = 0;
		u_TextureOffsetHandle = 0;
		u_TextureScaleHandle = 0;
		u_TextureEnabledHandle = new int[Textures.MAX_TEXTURES];
		u_TexturesHandle = new int[Textures.MAX_TEXTURES];

		System.out.println("GLESRenderer Constructor finished.");
	}

	//Status Check
	public final boolean IsReady() {
		synchronized (mtLock) {
			return ready;
		}
	}
	protected final void SetReady(boolean _val) {
		synchronized (mtLock) {
			ready = _val;
		}
	}

	//OpenGL Stuff.
	synchronized public void UseShader(GLESShaderProgram _shaderProgram) {
		GLES20.glUseProgram(_shaderProgram.GetShaderProgramHandle());
	}
	synchronized public void Enable(final int _mode) {
		GLES20.glEnable(_mode);

		switch (_mode) {
			case GLES20.GL_BLEND:
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
				break;
			default:
				//Do nothing.
		}
	}
	synchronized public void Disable(final int _mode) {
		GLES20.glDisable(_mode);
	}
	synchronized public void SetClearColor(final float _r, final float _g, final float _b, final float _a) {
		GLES20.glClearColor(_r, _g, _b, _a);
	}
	synchronized public void ClearBuffer(final int _bufferBit) {
		GLES20.glClear(_bufferBit);
	}
	synchronized public void SetViewport(final int _x, final int _y, final int _width, final int _height) {
		GLES20.glViewport(_x, _y, _width, _height);
	}

	//Camera
	synchronized public void SetToCamera2DView(final Camera2D _camera) {
		modelStack.LoadIdentity();
		viewStack.SetToLookAt(_camera.position.x, _camera.position.y, _camera.position.z, _camera.position.x, _camera.position.y, -1, 0, 1, 0);
		Matrix4x4 orthoMatrix = new Matrix4x4();
		orthoMatrix.SetToOrtho(-_camera.width * 0.5f, _camera.width * 0.5f, -_camera.height * 0.5f, _camera.height * 0.5f, _camera.near, _camera.far);
		projectionStack.LoadMatrix(orthoMatrix);
	}
	synchronized public void SetToUIView() {
		modelStack.LoadIdentity();
		viewStack.LoadIdentity();
		Matrix4x4 orthoMatrix = new Matrix4x4();
		orthoMatrix.SetToOrtho(-1.0f, 1.0f, -1.0f, 1.0f, -1.0f, 1.0f);
		projectionStack.LoadMatrix(orthoMatrix);
	}

	//Rendering
	synchronized public void Render(MeshBuilder.Mesh _mesh, Textures _textures) {
		if (_mesh == null) {
			return;
		}

		//Get MVP.
		Matrix4x4 MVP = projectionStack.Top().Times(viewStack.Top()).Times(modelStack.Top());

		//Update Uniforms
		shaderProgram.UpdateUniform(u_MVPMatrixHandle, MVP, false);
		shaderProgram.UpdateUniform(u_TextureOffsetHandle, _mesh.GetTextureOffset(), 2);
		shaderProgram.UpdateUniform(u_TextureScaleHandle, _mesh.GetTextureScale(), 2);

		if (_textures == null) {
			for (int i = 0; i < Textures.MAX_TEXTURES; ++i) {
				shaderProgram.UpdateUniform(u_TextureEnabledHandle[i], false);
				//This fucking line caused SO MUCH TAIJI.
				//shaderProgram.UpdateUniform(u_TexturesHandle[i], TextureManager.INVALID_TEXTURE_HANDLE);
			}
		} else {
			for (int i = 0; i < Textures.MAX_TEXTURES; ++i) {
				if (_textures.handles[i] == TextureManager.INVALID_TEXTURE_HANDLE) {
					shaderProgram.UpdateUniform(u_TextureEnabledHandle[i], false);
					//This fucking line also caused SO MUCH TAIJI.
					//shaderProgram.UpdateUniform(u_TexturesHandle[i], TextureManager.INVALID_TEXTURE_HANDLE);
				} else {
					shaderProgram.SetActiveTexture(i);
					shaderProgram.BindTexture(_textures.handles[i]);
					shaderProgram.UpdateUniform(u_TextureEnabledHandle[i], true);
					shaderProgram.UpdateUniform(u_TexturesHandle[i], i);
				}
			}
		}

		RenderMesh(_mesh);

		if (_textures != null) {
			for (int i = 0; i < Textures.MAX_TEXTURES; ++i) {
				if (_textures.handles[i] != TextureManager.INVALID_TEXTURE_HANDLE) {
					shaderProgram.UnbindTexture();
					break;
				}
			}
		}
	}

	public void RenderText(MeshBuilder.Text _textMesh, Textures _textures, final String _str, float _textWidth, float _textHeight) {
		if (_textMesh == null) {
			return;
		}

		for (int i = 0; i < _str.length(); ++i) {
			char c = _str.charAt(i);
			_textMesh.SetCharacter(c);
			modelStack.PushMatrix();
			modelStack.Translate(_textWidth* i, 0, 0);
			modelStack.Scale(_textWidth, _textHeight, 1);
			Render(_textMesh, _textures);
			modelStack.PopMatrix();
		}
	}

	private void RenderMesh(MeshBuilder.Mesh _mesh) {
		if (_mesh == null) {
			return;
		}

		//Bind VBO
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, _mesh.vboHandle[0]);

		//Enable Vertex Attributes
		GLES20.glEnableVertexAttribArray(a_Position);
		GLES20.glEnableVertexAttribArray(a_Color);
		GLES20.glEnableVertexAttribArray(a_Normal);
		GLES20.glEnableVertexAttribArray(a_TexCoordinate);

		//Bind Attributes
		GLES20.glVertexAttribPointer(a_Position, 3, GLES20.GL_FLOAT, false, Vertex.SizeOf(), 0);
		GLES20.glVertexAttribPointer(a_Color, 4, GLES20.GL_FLOAT, false, Vertex.SizeOf(), Vertex.Position.SizeOf());
		GLES20.glVertexAttribPointer(a_Normal, 3, GLES20.GL_FLOAT, false, Vertex.SizeOf(), Vertex.Position.SizeOf() + Vertex.Color.SizeOf());
		GLES20.glVertexAttribPointer(a_TexCoordinate, 2, GLES20.GL_FLOAT, false, Vertex.SizeOf(), Vertex.Position.SizeOf() + Vertex.Color.SizeOf() + Vertex.Normal.SizeOf());

		//Bind IBO
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, _mesh.iboHandle[0]);

		//Draw
		if (_mesh.drawMode == MeshBuilder.Mesh.DRAW_TRIANGLE_STRIP) {
			GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, _mesh.numIndices, GLES20.GL_UNSIGNED_SHORT, 0);
		} else if (_mesh.drawMode == MeshBuilder.Mesh.DRAW_TRIANGLES) {
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, _mesh.numIndices, GLES20.GL_UNSIGNED_SHORT, 0);
		}

		//Unbind Buffers
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
		GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	public void AddToRenderQueue(Runnable _runnable) {
		synchronized (renderLock) {
			renderQueue.add(_runnable);
		}
	}

	public void LoadShaders() {
		System.out.println("GLESRenderer LoadShaders() started.");

		//Initialise Shader.
		String vertexShaderSource = FileLoader.ReadTextFileFromRawResource(context, R.raw.vertex_shader);
		String fragmentShaderSource = FileLoader.ReadTextFileFromRawResource(context, R.raw.fragment_shader);
		String[] attributes = new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"};
		shaderProgram = ShaderHelper.AddShader("Simple Shader", vertexShaderSource, fragmentShaderSource, attributes);
		//Get our attributes.
		a_Position = shaderProgram.GetAttributeHandle("a_Position");
		a_Color = shaderProgram.GetAttributeHandle("a_Color");
		a_Normal = shaderProgram.GetAttributeHandle("a_Normal");
		a_TexCoordinate = shaderProgram.GetAttributeHandle("a_TexCoordinate");
		//Get our uniforms.
		u_MVPMatrixHandle = shaderProgram.GetUniformHandle("u_MVPMatrix");
		u_AlphaDiscardValue = shaderProgram.GetUniformHandle("u_AlphaDiscardValue");
		u_TextureOffsetHandle = shaderProgram.GetUniformHandle("u_TextureOffset");
		u_TextureScaleHandle = shaderProgram.GetUniformHandle("u_TextureScale");
		for (int i = 0; i < Textures.MAX_TEXTURES; ++i) {
			u_TextureEnabledHandle[i] = shaderProgram.GetUniformHandle("u_TextureEnabled[" + String.valueOf(i) + "]");;
			u_TexturesHandle[i] = shaderProgram.GetUniformHandle("u_Textures[" + String.valueOf(i) + "]");;
		}
		//Use the shader.
		UseShader(shaderProgram);

		//Set Uniforms
		shaderProgram.UpdateUniform(u_AlphaDiscardValue, 0.1f);

		//We have finished initialising and is ready.
		SetReady(true);

		System.out.println("GLESRenderer LoadShaders() finished.");
	}

	public void DeleteShaders() {
		//Delete Shaders
		ShaderHelper.RemoveShader("Simple Shader");
	}

	public void ClearMatrices() {
		modelStack.Clear();
		viewStack.Clear();
		projectionStack.Clear();
	}

	//Overrides
	@Override
	public void onSurfaceCreated(GL10 _deprecated, EGLConfig _config) {
		//Set Background Color
		SetClearColor(0.0f, 0.0f, 0.4f, 0.0f);
		//Enable Depth Testing
		Enable(DEPTH_TEST);
		//Enable Backface Culling
		Enable(CULL_FACE);
		//Enable Blending
		Enable(BLEND);
	}

	@Override
	public void onSurfaceChanged(GL10 _deprecated, int _width, int _height) {
		//Set the OpenGL viewport to the same size as the surface.
		SetViewport(0, 0, _width, _height);
	}

	@Override
	public void onDrawFrame(GL10 _deprecated) {
		//System.out.println("Entered onDrawFrame.");
		synchronized (renderLock) {
			//System.out.println("Started onDrawFrame.");
			while (renderQueue.isEmpty() == false) {
				renderQueue.peek().run();
				renderQueue.remove();
			}
			//System.out.println("Finished onDrawFrame.");
		}
		//System.out.println("Exited onDrawFrame.");
	}
}