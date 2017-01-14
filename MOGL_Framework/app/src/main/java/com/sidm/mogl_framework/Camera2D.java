package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

public class Camera2D {
	public Vector3 position;
	public float width;
	public float height;
	public float near;
	public float far;

	//Constructor(s)
	Camera2D() {
		position = new Vector3();
		width = 40.0f;
		height = 30.0f;
		near = -100.0f;
		far = 100.0f;
	}
}