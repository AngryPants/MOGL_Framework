package com.sidm.mogl_framework;
//Created by <Insert Name> on 17/1/2017.

import android.opengl.GLSurfaceView;
import android.provider.Settings;

public class Player extends GameObject {

	//Variable(s)
	Vector3 position;
	Vector3 scale;
	float rotation;

	//Our player's mesh and stuff.
	MeshBuilder.Mesh mesh;
	Textures textures;

	//Constructor(s)
	public Player(GamePanelSurfaceView _gamePanelSurfaceView, GLESRenderer _glESRenderer) {
		super(_gamePanelSurfaceView, _glESRenderer);

		position = new Vector3();
		scale = new Vector3(1.0f, 1.0f, 1.0f);
		rotation = 0.0f;

		mesh = MeshBuilder.GetMesh("Quad");
		textures = new Textures();
		textures.handles[0] = TextureManager.GetTextureID("Test GameObject Texture");
	}

	//Function(s)
	@Override
	public void Update(double _deltaTime) {
		//rotation += _deltaTime * 10.0f;
	}

	@Override
	public void Draw() {
		Matrix4x4Stack modelStack = glESRenderer.modelStack;
		modelStack.PushMatrix();
			modelStack.Translate(position.x, position.y, position.z);
			modelStack.Rotate(rotation, 0.0f, 0.0f, 1.0f);
			modelStack.Scale(scale.x, scale.y, scale.z);
			glESRenderer.Render(mesh, textures);
		modelStack.PopMatrix();
	}

	@Override
	public void DrawUI() {}

	//Psuedo Destructor
	@Override
	public void Destroy() {}

}