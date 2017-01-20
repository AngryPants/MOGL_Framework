package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

public class MathUtility {

	static final float EPSILON = 0.00001f;
	static final float PI = 3.1415926535897932384626433832795f;
	static final float TWO_PI = PI * 2.0f;
	static final float HALF_PI = PI * 0.5f;
	static final float QUATER_PI = PI * 0.25f;

	public static float ThrowDivideByZeroError() {
		throw new RuntimeException("Divide By Zero Error!");
	}

	public static boolean IsEqual(final float a, final float b) {
		float difference = a - b;
		return (difference <= EPSILON && difference >= -EPSILON);
	}

	public static float DegreeToRadian(float _degree) {
		return _degree * PI / 180.0f;
	}
	public static float RadianToDegree(float _radian) {
		return _radian * PI / 180.0f;
	}

	public static short Clamp(short _value, short _min, short _max) {
		if (_value < _min) {
			return _min;
		} else if (_value > _max) {
			return _max;
		} else {
			return _value;
		}
	}
	public static int Clamp(int _value, int _min, int _max) {
		if (_value < _min) {
			return _min;
		} else if (_value > _max) {
			return _max;
		} else {
			return _value;
		}
	}
	public static float Clamp(float _value, float _min, float _max) {
		if (_value < _min) {
			return _min;
		} else if (_value > _max) {
			return _max;
		} else {
			return _value;
		}
	}
	public static double Clamp(double _value, double _min, double _max) {
		if (_value < _min) {
			return _min;
		} else if (_value > _max) {
			return _max;
		} else {
			return _value;
		}
	}

}