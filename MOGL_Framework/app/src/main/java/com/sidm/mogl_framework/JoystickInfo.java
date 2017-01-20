package com.sidm.mogl_framework;
//Created by <Insert Name> on 20/1/2017.

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class JoystickInfo {

	//Variable(s)
	public int actionPointerID; //The ID of the pointer we are using for this joystick.
	private float radius; //The max radius of this joystick.
	private Vector2 positionPivot; //Where is the starting reference point of this Joystick?
	private Vector2 positionCurrent; //Where is the player touching now?
	private Vector2 positionPrevious; //Where was the player touching previously?

	//Constructor(s)
	public JoystickInfo(final float _radius) { //Default
		this.actionPointerID = INVALID_POINTER_ID;
		this.radius = _radius;
		this.positionPivot = new Vector2();
		this.positionCurrent = new Vector2(this.positionPivot);
		this.positionPrevious = new Vector2(this.positionPivot);
	}
	public JoystickInfo(final JoystickInfo _rhs) { //Copy Constructor
		this.actionPointerID = INVALID_POINTER_ID;
		this.radius = _rhs.radius;
		this.positionPivot = new Vector2(_rhs.positionPivot);
		this.positionCurrent = new Vector2(_rhs.positionCurrent);
		this.positionPrevious = new Vector2(_rhs.positionPrevious);
	}
	public JoystickInfo(final float _positionStartX, final float _positionStartY, final float _radius) {
		this.actionPointerID = INVALID_POINTER_ID;
		this.radius = _radius;
		this.positionPivot = new Vector2(_positionStartX, _positionStartY);
		this.positionCurrent = new Vector2(this.positionPivot);
		this.positionPrevious = new Vector2(this.positionPivot);
	}
	public JoystickInfo(final Vector2 _positionStart, final float _radius) {
		this.actionPointerID = INVALID_POINTER_ID;
		this.radius = _radius;
		this.positionPivot = new Vector2(_positionStart);
		this.positionCurrent = new Vector2(this.positionPivot);
		this.positionPrevious = new Vector2(this.positionPivot);
	}

	//Interface Function(s)
	public void SetRadius(final float _radius) {
		if (_radius < MathUtility.EPSILON) {
			this.radius = MathUtility.EPSILON;
		} else {
			this.radius = _radius;
		}
	}
	public void SetPositionPivot(final float _x, final float _y) {
		this.positionPivot.Set(_x, _y);
		this.positionCurrent.Set(_x, _y);
		this.positionPrevious.Set(_x, _y);
	}
	public void SetPositionPivot(final Vector2 _position) {
		SetPositionPivot(_position.x, _position.y);
	}
	public void SetPositionCurrent(final float _x, final float _y) {
		this.positionPrevious.equals(this.positionCurrent);
		this.positionCurrent.Set(_x, _y);
	}
	void SetPositionCurrent(final Vector2 _position) {
		SetPositionCurrent(_position.x, _position.y);
	}

	public final float GetRadius() {
		return this.radius;
	}
	public final float GetDiameter() {
		return this.radius * 2.0f;
	}
	public final Vector2 GetPositionPivot() {
		return this.positionPivot;
	}
	public final Vector2 GetPositionCurrent() {
		return this.positionCurrent;
	}
	public final Vector2 GetPositionPrevious() {
		return this.positionPrevious;
	}

	public void ResetActionPointerID() {
		this.actionPointerID = INVALID_POINTER_ID;
	}
	public boolean HasValidActionPointerID() {
		return this.actionPointerID != INVALID_POINTER_ID;
	}

	//Magnitude ranges from 0.0f to 1.0f.
	public final Vector2 GetAxis() {
		Vector2 axis = positionCurrent.Minus(positionPivot);
		if (axis.LengthSquared() > radius * radius) {
			return axis.Normalise();
		}

		return axis.TimesEqual(1.0f / radius);
	}

}