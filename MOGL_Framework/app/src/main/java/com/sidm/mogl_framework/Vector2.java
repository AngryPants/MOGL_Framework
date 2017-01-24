package com.sidm.mogl_framework;

public class Vector2 {

	//Variable(s)
	public float x, y;

	//Constructor(s) & Destructor
	public Vector2() {
		SetZero();
	}
	public Vector2(final float x, final float y) {
		Set(x, y);
	}
	public Vector2(final Vector2 rhs) {
		Set(rhs.x, rhs.y);
	}

	//Function(s)
	public void Set(final float x, final float y) {
		this.x = x; this.y = y;
	}
	public void Set(final Vector2 _rhs) {
		Set(_rhs.x, _rhs.y);
	}
	public void SetZero() {
		Set(0.0f, 0.0f);
	}

	public float Length() {
		return (float)Math.sqrt(LengthSquared());
	}
	public float LengthSquared() {
		return x*x + y*y;
	}
	public float Dot(final Vector2 rhs) {
		return this.x * rhs.x + this.y * rhs.y;
	}
	public float AngleBetween(final Vector2 rhs) {
		return (float)Math.acos( Math.toDegrees(Dot(rhs) / Length() * rhs.Length()) );
	}

	public Vector2 Equals(final Vector2 rhs) {
		this.x = rhs.x;
		this.y = rhs.y;

		return this;
	}

	public Vector2 Normalised() {
		return new Vector2(this).Normalise();
	}
	public Vector2 Normalise() {
		float length = Length();
		//Need to finish MathUtility so that I can haz Math::EPSILON.
		if (length < MathUtility.EPSILON) {
			MathUtility.ThrowDivideByZeroError();
		}
		this.x /= length; this.y /= length;
		return this;
	}

	//Operators(s)
	public Vector2 Plus(final Vector2 rhs) {
		return new Vector2(this.x + rhs.x, this.y + rhs.y);
	}
	public Vector2 PlusEquals(final Vector2 rhs) {
		this.x += rhs.x; this.y += rhs.y;
		return this;
	}
	public Vector2 Minus(final Vector2 rhs) {
		return new Vector2(this.x - rhs.x, this.y - rhs.y);
	}
	public Vector2 MinusEqual(final Vector2 rhs) {
		this.x -= rhs.x; this.y -= rhs.y;
		return this;
	}
	public Vector2 Times(final float scalar) {
		return new Vector2(this.x * scalar, this.y * scalar);
	}
	public Vector2 TimesEqual(final float scalar) {
		this.x *= scalar; this.y *= scalar;
		return this;
	}
	public boolean IsEqual(final Vector2 _rhs) {
		return MathUtility.IsEqual(this.x, _rhs.x) && MathUtility.IsEqual(this.y, _rhs.y);
	}
	public boolean IsNotEqual(final Vector2 _rhs) {
		return !MathUtility.IsEqual(this.x, _rhs.x) || !MathUtility.IsEqual(this.y, _rhs.y);
	}

	//Overrides
	@Override
	public String toString() {
		String str = "[" + String.valueOf(this.x) + ", " + String.valueOf(this.y) + "]";
		return str;
	}

}