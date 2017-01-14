package com.sidm.mogl_framework;

public class Vector3 {

	//Variable(s)
	public float x, y, z;

	//Constructor(s)
	public Vector3() {
		SetZero();
	}
	public Vector3(final float _x, final float _y, final float _z) {
		Set(_x, _y, _z);
	}
	Vector3(final Vector3 _rhs) {
		Set(_rhs);
	}

	//Function(s)
	public void Set(final float _x, final float _y, final float _z) {
		x = _x; y = _y; z = _z;
	}
	public void Set(final Vector3 _rhs) {
		Set(_rhs.x, _rhs.y, _rhs.z);
	}
	public void SetZero() {
		Set(0.0f, 0.0f, 0.0f);
	}

	public float LengthSquared() {
		return x*x + y*y + z*z;
	}
	public float Length() {
		return (float)Math.sqrt(LengthSquared());
	}
	public float Dot(final Vector3 _rhs) {
		return x*_rhs.x + y*_rhs.y + z*_rhs.z;
	}
	public Vector3 Cross(final Vector3 _rhs) {
		return new Vector3(y * _rhs.z - z * _rhs.y, z * _rhs.x - x * _rhs.z, x * _rhs.y - y * _rhs.x);
	}
	public Vector3 Projection(final Vector3 _rhs) {
		Vector3 rhsNormalised = new Vector3(_rhs.Normalised());
		return rhsNormalised.Times(this.Dot(rhsNormalised));
	}

	public Vector3 Normalised() {
		return new Vector3(this).Normalise();
	}
	public Vector3 Normalise() {
		float length = Length();
		if (length < MathUtility.EPSILON) {
			MathUtility.ThrowDivideByZeroError();
		}
		this.x /= length; this.y /= length; this.z /= length;
		return this;
	}

	//Operator(s)
	public Vector3 Plus(final Vector3 _rhs) {
		return new Vector3(this.x + _rhs.x, this.y + _rhs.y, this.z + _rhs.z);
	}
	public Vector3 PlusEqual(final Vector3 _rhs) {
		this.x += _rhs.x; this.y += _rhs.y; this.z += _rhs.z;
		return this;
	}
	public Vector3 Minus(final Vector3 _rhs) {
		return new Vector3(this.x - _rhs.x, this.y - _rhs.y, this.z - _rhs.z);
	}
	public Vector3 MinusEqual(final Vector3 _rhs) {
		this.x -= _rhs.x; this.y -= _rhs.y; this.z -= _rhs.z;
		return this;
	}
	public Vector3 Times(final float _scalar) {
		return new Vector3(x * _scalar, y * _scalar, z * _scalar);
	}
	public Vector3 TimesEqual(final float _scalar) {
		this.x *= _scalar; this.y *= _scalar; this.z *= _scalar;
		return this;
	}
	public boolean IsEqual(final Vector3 _rhs) {
		return MathUtility.IsEqual(this.x, _rhs.x) && MathUtility.IsEqual(this.y, _rhs.y) && MathUtility.IsEqual(this.z, _rhs.z);
	}
	public boolean IsNotEqual(final Vector3 _rhs) {
		return !MathUtility.IsEqual(this.x, _rhs.x) || !MathUtility.IsEqual(this.y, _rhs.y) || !MathUtility.IsEqual(this.z, _rhs.z);
	}

	//Overrides
	@Override
	public String toString() {
		String str = "[" + String.valueOf(this.x) + ", " + String.valueOf(this.y) + ", " + String.valueOf(this.z) + "]";
		return str;
	}

}
