package com.sidm.mogl_framework;

import android.graphics.Bitmap;

/**
 * Created by AFTERSHOCK on 28/11/2016.
 */

public class Character {

	//Variable(s)
	public Transform transform;
	public float movementSpeed;
	public int health;
	public int maxHealth;



	public void ReceiveDamage(int damage)
	{
		this.health -= damage;
	}

	//Constructor(s)
	public Character() {
		transform = new Transform();
		movementSpeed = 500.0f;
		maxHealth = 100;
		health = maxHealth;
	}

}