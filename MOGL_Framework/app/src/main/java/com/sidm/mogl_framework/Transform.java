package com.sidm.mogl_framework;

import android.media.RemoteControlClient;

/**
 * Created by AFTERSHOCK on 28/11/2016.
 */

public class Transform {

	private Vector2 position;
	private Vector2 scale;
	private float rotation; //In Degrees

	public Transform() {
		position = new Vector2();
		scale = new Vector2(1.0f, 1.0f);
		rotation = 0.0f;
	}
	public Transform(final Transform rhs) {
		this.position.Equals(rhs.position);
		this.rotation = rhs.rotation;
	}

	public final Vector2 GetPosition() {
		return this.position;
	}
	public final Vector2 GetScale() {
		return this.scale;
	}
	public final float GetRotation() {
		return this.rotation;
	}
	public Vector2 GetForward() {
		return new Vector2( (float)(Math.cos(Math.toRadians(rotation))), (float)( Math.sin(Math.toRadians(rotation))) );
	}

	//This should be fucking GetLeft() but fucking Android is being a fucking cunt and refuses to follow normal fucking rotational shit.
	public Vector2 GetRight() {
		return new Vector2( (float)(Math.cos(Math.toRadians(rotation + 90.f))), (float)( Math.sin(Math.toRadians(rotation + 90.0f))) );
	}

	public void SetPosition(final float x, final float y) {
		this.position.Set(x, y);
	}
	public void SetPosition(final Vector2 position) {
		this.position.Equals(position);
	}
	public void Translate(final float x, final float y) {
		this.position.x += x;
		this.position.y += y;
	}
	public void Translate(final Vector2 translation) {
		this.position.PlusEquals(translation);
	}

	public void SetScale(final float x, final float y) {
		this.scale.x = x;
		this.scale.y = y;
	}
	public void SetScale(final Vector2 scale) {
		this.scale.Equals(scale);
	}
	public void Scale(final float x, final float y) {
		this.scale.x *= x;
		this.scale.y *= y;
	}

	public void SetRotation(final float rotation) {
		this.rotation = rotation;
	}
	public void Rotate(final float rotation) {
		this.rotation += rotation;
	}

}
