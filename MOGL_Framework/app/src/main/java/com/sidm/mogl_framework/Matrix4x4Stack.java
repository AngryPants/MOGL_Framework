package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

import java.util.Stack;

public class Matrix4x4Stack {

	//Variable(s)
	private Stack<Matrix4x4> ms;

	//Constructor(s)
	public Matrix4x4Stack() {
		Matrix4x4 mat = new Matrix4x4();
		mat.SetToIdentity();
		ms = new Stack<>();
		ms.push(mat);
	}

	//Function(s)
	final Matrix4x4 Top() {
		return ms.peek();
	}
	public Matrix4x4 PushMatrix() {
		Matrix4x4 mat = new Matrix4x4(ms.peek());
		ms.push(mat);
		return ms.peek();
	}
	public Matrix4x4 PopMatrix() {
		return ms.pop();
	}
	public void Clear() {
		while (ms.size() > 1) {
			ms.pop();
		}
	}

	public void LoadIdentity() {
		ms.peek().SetToIdentity();
	}
	public void LoadMatrix(final Matrix4x4 _rhs) {
		ms.peek().Set(_rhs);
	}
	public void MultiplyMatrix(final Matrix4x4 _rhs) {
		ms.peek().TimesEqual(_rhs);
	}

	public void Rotate(final float _degrees, final float _axisX, final float _axisY, final float _axisZ) {
		Matrix4x4 mat = new Matrix4x4();
		mat.SetToRotation(_degrees, _axisX, _axisY, _axisZ);
		ms.peek().TimesEqual(mat);
	}
	public void Scale(final float _scaleX, final float _scaleY, final float _scaleZ) {
		Matrix4x4 mat = new Matrix4x4();
		mat.SetToScale(_scaleX, _scaleY, _scaleZ);
		ms.peek().TimesEqual(mat);
	}
	public void Translate(final float _translateX, final float _translateY, final float _translateZ) {
		Matrix4x4 mat = new Matrix4x4();
		mat.SetToTranslation(_translateX, _translateY, _translateZ);
		ms.peek().TimesEqual(mat);
	}
	public void SetToLookAt(final float _eyeX, final float _eyeY, final float _eyeZ,
							final float _centerX, final float _centerY, final float _centerZ,
							final float _upX, final float _upY, final float _upZ)
	{
		Matrix4x4 mat = new Matrix4x4();
		mat.SetToLookAt(_eyeX, _eyeY, _eyeZ, _centerX, _centerY, _centerZ, _upX, _upY, _upZ);
		ms.peek().Set(mat);
	}

}