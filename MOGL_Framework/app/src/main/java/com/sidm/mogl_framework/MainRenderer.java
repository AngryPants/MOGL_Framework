package com.sidm.mogl_framework;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.provider.Settings;

import java.io.File;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

//Created by <Insert Name> on 13/1/2017.

public class MainRenderer implements GLSurfaceView.Renderer {

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
	private Context context;
	private GLESShaderProgram shaderProgram;

	public Matrix4x4Stack modelStack;
	public Matrix4x4Stack viewStack;
	public Matrix4x4Stack projectionStack;

	//Shader Attributes (Hardcode them here for now.)
	int a_Position;
	int a_Color;
	int a_Normal;
	int a_TexCoordinate;

	//Shader Uniforms (Hardcode them here for now.)
	int u_MVPMatrixHandle;
	int u_AlphaDiscardValue;
	int u_TextureOffsetHandle;
	int u_TextureScaleHandle;
	int[] u_TextureEnabledHandle;
	int[] u_TexturesHandle;

	//Test Variable(s)
	Camera2D camera;
	MeshBuilder.Mesh mesh;
	Textures textures;

	//Constructor(s)
	public MainRenderer(Context _context) {
		super();
		context = _context;
		//Initialise Matrix Stacks
		modelStack = new Matrix4x4Stack();
		viewStack = new Matrix4x4Stack();
		projectionStack = new Matrix4x4Stack();

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

		//Test Variable(s)
		camera = new Camera2D();
		camera.width = 4;
		camera.height = 3;
	}
	public void Exit() {
		MeshBuilder.ReleaseMesh(mesh.name);
		TextureManager.ReleaseTexture(textures.data[0].name);
	}

	//OpenGL Stuff.
	public void UseShader(GLESShaderProgram _shaderProgram) {
		GLES20.glUseProgram(_shaderProgram.GetShaderProgramHandle());
	}
	public void Enable(final int _mode) {
		GLES20.glEnable(_mode);
	}
	public void Disable(final int _mode) {
		GLES20.glDisable(_mode);
	}
	public void SetClearColor(final float _r, final float _g, final float _b, final float _a) {
		GLES20.glClearColor(_r, _g, _b, _a);
	}
	public void ClearBuffer(final int _bufferBit) {
		GLES20.glClear(_bufferBit);
	}
	public void SetViewport(final int _x, final int _y, final int _width, final int _height) {
		GLES20.glViewport(_x, _y, _width, _height);
	}

	//Camera
	public void SetToCamera2DView(Camera2D _camera) {
		modelStack.LoadIdentity();
		viewStack.SetToLookAt(_camera.position.x, _camera.position.y, _camera.position.z, _camera.position.x, _camera.position.y, -1, 0, 1, 0);
		Matrix4x4 orthoMatrix = new Matrix4x4();
		orthoMatrix.SetToOrtho(-_camera.width * 0.5f, _camera.width * 0.5f, -_camera.height * 0.5f, _camera.height * 0.5f, _camera.near, _camera.far);
		projectionStack.LoadMatrix(orthoMatrix);
	}

	//Rendering
	public void Render(MeshBuilder.Mesh _mesh, Textures _textures) {
		if (_mesh == null) {
			return;
		}

		//Get MVP.
		Matrix4x4 MVP = projectionStack.Top().Times(viewStack.Top()).Times(modelStack.Top());

		//Update Uniforms
		shaderProgram.UpdateUniform(u_MVPMatrixHandle, MVP, false);
		shaderProgram.UpdateUniform(u_TextureOffsetHandle, _mesh.textureOffset, 2);
		shaderProgram.UpdateUniform(u_TextureScaleHandle, _mesh.textureScale, 2);

		if (_textures != null) {
			for (int i = 0; i < Textures.MAX_TEXTURES; ++i) {
				if (_textures.data[i].handle == TextureManager.INVALID_TEXTURE_HANDLE) {
					shaderProgram.UpdateUniform(u_TextureEnabledHandle[i], false);
					shaderProgram.UpdateUniform(u_TexturesHandle[i], TextureManager.INVALID_TEXTURE_HANDLE);
				} else {
					shaderProgram.SetActiveTexture(i);
					shaderProgram.BindTexture(_textures.data[i].handle);
					shaderProgram.UpdateUniform(u_TextureEnabledHandle[i], true);
					shaderProgram.UpdateUniform(u_TexturesHandle[i], i);
				}
			}
		} else {
			for (int i = 0; i < Textures.MAX_TEXTURES; ++i) {
				shaderProgram.UpdateUniform(u_TextureEnabledHandle[i], false);
				shaderProgram.UpdateUniform(u_TexturesHandle[i], TextureManager.INVALID_TEXTURE_HANDLE);
			}
		}

		RenderMesh(_mesh);

		if (textures != null) {
			for (int i = 0; i < Textures.MAX_TEXTURES; ++i) {
				if (_textures.data[i].handle != TextureManager.INVALID_TEXTURE_HANDLE) {
					shaderProgram.UnbindTexture();
					break;
				}
			}
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

	//Overrides
	@Override
	public void onSurfaceCreated(GL10 _deprecated, EGLConfig _config) {
		//Set Background Color
		SetClearColor(0.0f, 0.0f, 0.4f, 0.0f);
		//Enable Depth Testing
		Enable(DEPTH_TEST);
		//Enable Backface Culling
		Enable(CULL_FACE);

		//The meshes and textures are for testing purposes. Eventually we need to intialise them elsewhere.
		//Initialise Mesh(es). Make sure to do this in onSurfaceCreated and not the constructor, since
		//OpenGL ES needs a context to be able to work, and somehow onSurfaceCreated has a context but
		//the constructor does not. Still not sure how this works but ¯\_(ツ)_/¯.
		//I wonder if I can do this inside a view. Theoretically should work.
		mesh = MeshBuilder.GenerateQuad("Test Quad", new Vertex.Color(1, 1, 1, 1), 1.0f);

		//Initialise Textures
		textures = new Textures();
		textures.data[0].name = "Test Texture";
		textures.data[0].handle = TextureManager.AddTexture(textures.data[0].name, context, R.drawable.test_texture, true);

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

		//Set Uniforms
		shaderProgram.UpdateUniform(u_AlphaDiscardValue, 0.1f);
		//Use the shader.
		UseShader(shaderProgram);
	}
	@Override
	public void onSurfaceChanged(GL10 _deprecated, int _width, int _height) {
		//Set the OpenGL viewport to the same size as the surface.
		SetViewport(0, 0, _width, _height);
	}

	@Override
	public void onDrawFrame(GL10 _deprecated) {
		ClearBuffer(DEPTH_BUFFER_BIT | COLOR_BUFFER_BIT);
		UseShader(shaderProgram);

		SetToCamera2DView(camera);

		modelStack.PushMatrix();
			//modelStack.Translate(0.5f, 0.5f, 0.0f);
			Render(mesh, textures);
		modelStack.PopMatrix();
	}
}