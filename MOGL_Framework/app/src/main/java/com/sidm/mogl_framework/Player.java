package com.sidm.mogl_framework;
//Created by <Insert Name> on 17/1/2017.

import android.opengl.GLSurfaceView;
import android.provider.Settings;

import java.util.List;
import java.util.Vector;

public class Player extends GameObject {

	//Variable(s)


	Transform transform;

	public Vector<Zombie> zombieList;
	public Vector<Bullet> bulletList;

	public float f_movSpd;

	public int health;
	//int i_damage = 0;
	float f_fireDebounceTimer;

	public Gun gun;
	public Gun defaultGun;

	public static int i_score =0;

	//Our player's mesh and stuff.
	//MeshBuilder.Mesh mesh;
	MeshBuilder.Sprite sprite;
	MeshBuilder.SpriteAnimation walkAnimation;
	MeshBuilder.SpriteAnimation reloadAnimation;
	Textures textures;

	//Constructor(s)
	public Player(GamePanelSurfaceView _gamePanelSurfaceView, GLESRenderer _glESRenderer) {
		super(_gamePanelSurfaceView, _glESRenderer);

		transform = new Transform();

		health = 100;

		f_movSpd = 3.f;

		//mesh = MeshBuilder.GetMesh("Quad");
		walkAnimation = new MeshBuilder.SpriteAnimation(4, 20, 20, 39, true, 0.4f, false);
		reloadAnimation = new MeshBuilder.SpriteAnimation(4, 20, 60, 79, true, 0.2f, false);
		sprite = MeshBuilder.GetSprite("Player Sprite");
		sprite.animation = walkAnimation;

		textures = new Textures();
		textures.handles[0] = TextureManager.GetTextureID("Player Texture");

		bulletList = new Vector<Bullet>(0);

		f_fireDebounceTimer = 0.f;

		gun = new Pistol();
		defaultGun = gun;
	}

	public void ReceiveDamage(int damage)
	{
		this.health -= damage;
	}

	public void UpdateBullets(double deltaTIme)
	{

		for(Bullet bullet:bulletList)
		{
			if(bullet.b_isActive == true)
			{
				bullet.Update(deltaTIme);
			}
		}
	}


	public void Shoot()
	{
		//Vector2 forward = new Vector2( (float)(Math.cos(Math.toRadians(rotation))), (float)( Math.sin(Math.toRadians(rotation))) );

		if(f_fireDebounceTimer >= 1.f/gun.f_fireRate && gun.i_ammoInMag > 0)
		{
			f_fireDebounceTimer =0.f;
			gun.i_ammoInMag--;
			if(gun.name != "Pistol")
			{
				gun.i_totalAmmo--;
			}


			for(Bullet bullet : bulletList)
			{

				if(bullet.b_isActive == false)
				{
					bullet.Set(this.transform.GetPosition(),this.transform.GetForward());
					bullet.damage = gun.i_damage;
					bullet.b_isActive = true;
					return;
				}
			}

			Bullet temp = new Bullet(this.transform.GetPosition(),this.transform.GetForward(),zombieList);;
			temp.damage = gun.i_damage;

			//synchronized (bulletList) {
				bulletList.add(temp);
			//}
		}

	}

	public void DrawwAllBullets()
	{
		Matrix4x4Stack modelStack = glESRenderer.modelStack;
		//synchronized (bulletList) {
			for (Bullet bullet : bulletList) {
				if (bullet.b_isActive == true) {
					modelStack.PushMatrix();

					modelStack.Translate(bullet.transform.GetPosition().x, bullet.transform.GetPosition().y, 1);
					modelStack.Rotate(bullet.transform.GetRotation(), 0, 0, 1);
					modelStack.Scale(bullet.transform.GetScale().x, bullet.transform.GetScale().y, 1);

					glESRenderer.Render(bullet.bulletMesh, bullet.texture);
					modelStack.PopMatrix();
				}
			}
		//}
	}


	//Function(s)
	@Override
	public void Update(double _deltaTime) {
		//rotation += _deltaTime * 10.0f;

		f_fireDebounceTimer+=_deltaTime;

		UpdateBullets(_deltaTime);

		walkAnimation.Update(_deltaTime);
	}

	@Override
	public void Draw() {
		Matrix4x4Stack modelStack = glESRenderer.modelStack;
		modelStack.PushMatrix();
			modelStack.Translate(transform.GetPosition().x,transform.GetPosition().y,1);
			modelStack.Rotate(transform.GetRotation(), 0.0f, 0.0f, 1.0f);
			modelStack.Scale(transform.GetScale().x,transform.GetScale().y,1);
			//glESRenderer.Render(mesh, textures);
			glESRenderer.Render(sprite, textures);
		modelStack.PopMatrix();

		DrawwAllBullets();
	}

	@Override
	public void DrawUI() {}

	//Psuedo Destructor
	@Override
	public void Destroy() {}

}