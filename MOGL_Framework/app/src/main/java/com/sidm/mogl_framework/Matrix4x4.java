package com.sidm.mogl_framework;

public class Matrix4x4 {

	//Variable(s)
	public float[] a;

	//Constructor(s)
	public Matrix4x4() {
		a = new float[16];
		SetToZero();
	}
	//a00 to a33 - Initial values for the matrix in (row, column) format.
	//Matrix4x4 is column-major format.
	public Matrix4x4(final float _a00, final float _a10, final float _a20, final float _a30, //Column 0
					 final float _a01, final float _a11, final float _a21, final float _a31, //Column 1
					 final float _a02, final float _a12, final float _a22, final float _a32, //Column 2
					 final float _a03, final float _a13, final float _a23, final float _a33) //Column 3
	{
		a = new float[16];

		//Column 0
		a[0] = _a00;
		a[1] = _a10;
		a[2] = _a20;
		a[3] = _a30;

		//Column 1
		a[4] = _a01;
		a[5] = _a11;
		a[6] = _a21;
		a[7] = _a31;

		//Column 2
		a[8] = _a02;
		a[9] = _a12;
		a[10] = _a22;
		a[11] = _a32;

		//Column 3
		a[12] = _a03;
		a[13] = _a13;
		a[14] = _a23;
		a[15] = _a33;
	}
	public Matrix4x4(final Matrix4x4 _rhs) {
		a = new float[16];
		Set(_rhs);
	}

	//Setter(s)
	public void Set(final Matrix4x4 _rhs) {
		for (int i = 0; i < 16; ++i) {
			a[i] = _rhs.a[i];
		}
	}
	public void SetToZero() {
		for (int i = 0; i < 16; ++i) {
			a[i] = 0.0f;
		}
	}
	public void SetToIdentity() {
		SetToZero();
		for (int column = 0; column < 4; ++column) {
			a[(column * 4) + column] = 1.0f;
		}
	}
	public void SetToTranspose() {
		for (int column = 0; column < 4; ++column) {
			for (int row = column + 1; row < 4; ++row) {
				float temp = a[(column * 4) + row];
				a[(column * 4) + row] = a[(row * 4) + column];
				a[(row * 4) + column] = temp;
			}
		}
	}
	public void SetToInverse() {
		Set(GetInverse());
	}

	//Getter(s)
	public Matrix4x4 GetTranspose() {
		Matrix4x4 mat = new Matrix4x4(this);
		mat.SetToTranspose();
		return mat;
	}

	public Matrix4x4 GetInverse() {
		float a0 = a[ 0]*a[ 5] - a[ 1]*a[ 4];
		float a1 = a[ 0]*a[ 6] - a[ 2]*a[ 4];
		float a2 = a[ 0]*a[ 7] - a[ 3]*a[ 4];
		float a3 = a[ 1]*a[ 6] - a[ 2]*a[ 5];
		float a4 = a[ 1]*a[ 7] - a[ 3]*a[ 5];
		float a5 = a[ 2]*a[ 7] - a[ 3]*a[ 6];
		float b0 = a[ 8]*a[13] - a[ 9]*a[12];
		float b1 = a[ 8]*a[14] - a[10]*a[12];
		float b2 = a[ 8]*a[15] - a[11]*a[12];
		float b3 = a[ 9]*a[14] - a[10]*a[13];
		float b4 = a[ 9]*a[15] - a[11]*a[13];
		float b5 = a[10]*a[15] - a[11]*a[14];

		float det = a0*b5 - a1*b4 + a2*b3 + a3*b2 - a4*b1 + a5*b0;
		if(Math.abs(det) < MathUtility.EPSILON) {
			MathUtility.ThrowDivideByZeroError();
		}
		Matrix4x4 inverse = new Matrix4x4();
		if (Math.abs(det) > MathUtility.EPSILON)
		{
			inverse.a[ 0] =   a[ 5]*b5 - a[ 6]*b4 + a[ 7]*b3;
			inverse.a[ 4] = - a[ 4]*b5 + a[ 6]*b2 - a[ 7]*b1;
			inverse.a[ 8] =   a[ 4]*b4 - a[ 5]*b2 + a[ 7]*b0;
			inverse.a[12] = - a[ 4]*b3 + a[ 5]*b1 - a[ 6]*b0;
			inverse.a[ 1] = - a[ 1]*b5 + a[ 2]*b4 - a[ 3]*b3;
			inverse.a[ 5] =   a[ 0]*b5 - a[ 2]*b2 + a[ 3]*b1;
			inverse.a[ 9] = - a[ 0]*b4 + a[ 1]*b2 - a[ 3]*b0;
			inverse.a[13] =   a[ 0]*b3 - a[ 1]*b1 + a[ 2]*b0;
			inverse.a[ 2] =   a[13]*a5 - a[14]*a4 + a[15]*a3;
			inverse.a[ 6] = - a[12]*a5 + a[14]*a2 - a[15]*a1;
			inverse.a[10] =   a[12]*a4 - a[13]*a2 + a[15]*a0;
			inverse.a[14] = - a[12]*a3 + a[13]*a1 - a[14]*a0;
			inverse.a[ 3] = - a[ 9]*a5 + a[10]*a4 - a[11]*a3;
			inverse.a[ 7] =   a[ 8]*a5 - a[10]*a2 + a[11]*a1;
			inverse.a[11] = - a[ 8]*a4 + a[ 9]*a2 - a[11]*a0;
			inverse.a[15] =   a[ 8]*a3 - a[ 9]*a1 + a[10]*a0;

			float invDet = 1.0f/det;
			for (int i = 0; i < 16; ++i) {
				inverse.a[i] *= invDet;
			}
		}
		return inverse;
	}

	//Operator(s)
	public Matrix4x4 Plus(final Matrix4x4 _rhs) {
		Matrix4x4 mat = new Matrix4x4(this);
		return mat.PlusEqual(_rhs);
	}
	public Matrix4x4 PlusEqual(final Matrix4x4 _rhs) {
		for (int i = 0; i < 16; ++i) {
			a[i] += _rhs.a[i];
		}
		return this;
	}

	public Matrix4x4 Minus(final Matrix4x4 _rhs) {
		Matrix4x4 mat = new Matrix4x4(this);
		return mat.MinusEqual(_rhs);
	}
	public Matrix4x4 MinusEqual(final Matrix4x4 _rhs) {
		for (int i = 0; i < 16; ++i) {
			a[i] -= _rhs.a[i];
		}
		return this;
	}

	public Matrix4x4 Times(final Matrix4x4 _rhs) {
		Matrix4x4 mat = new Matrix4x4();
		for(int i = 0; i < 4; i++) {
			for(int j = 0; j < 4; j++) {
				mat.a[i * 4 + j] = this.a[0 * 4 + j] * _rhs.a[i * 4 + 0] +
						this.a[1 * 4 + j] * _rhs.a[i * 4 + 1] +
						this.a[2 * 4 + j] * _rhs.a[i * 4 + 2] +
						this.a[3 * 4 + j] * _rhs.a[i * 4 + 3];
			}
		}
		return mat;
	}
	public Matrix4x4 TimesEqual(final Matrix4x4 _rhs) {
		Set(this.Times(_rhs));
		return this;
	}

	//Transformation Function(s)
	public void SetToScale(final float _x, final float _y, final float _z) {
		SetToIdentity();
		a[0] = _x;
		a[5] = _y;
		a[10] = _z;
	}

	public void SetToTranslation(final float _x, final float _y, final float _z) {
		SetToIdentity();
		a[12] = _x;
		a[13] = _y;
		a[14] = _z;
	}

	public void SetToRotation(final float _degrees, final float _axisX, final float _axisY, final float _axisZ) {
		double mag = Math.sqrt(_axisX * _axisX + _axisY * _axisY + _axisZ * _axisZ);
		if(Math.abs((float)mag) < MathUtility.EPSILON) {
			MathUtility.ThrowDivideByZeroError();
		}
		double x = _axisX / mag, y = _axisY / mag, z = _axisZ / mag;
		double c = Math.cos(_degrees * Math.PI / 180), s = Math.sin(_degrees * Math.PI / 180);
		a[0] = (float)(x * x * (1.0f - c) + c);
		a[1] = (float)(y * x * (1.0f - c) + z * s);
		a[2] = (float)(x * z * (1.0f - c) - y * s);
		a[3] = 0.0f;
		a[4] = (float)(x * y * (1.0f - c) - z * s);
		a[5] = (float)(y * y * (1.0f - c) + c);
		a[6] = (float)(y * z * (1.0f - c) + x * s);
		a[7] = 0.0f;
		a[8] = (float)(x * z * (1.0f - c) + y * s);
		a[9] = (float)(y * z * (1.0f - c) - x * s);
		a[10] = (float)(z * z * (1.0f - c) + c);
		a[11] = 0.0f;
		a[12] = 0.0f;
		a[13] = 0.0f;
		a[14] = 0.0f;
		a[15] = 1.0f;
	}

	public void SetToLookAt(final float eyeX, final float eyeY, final float eyeZ,
							final float centerX, final float centerY, final float centerZ,
							final float upX, final float upY, final float upZ)
	{
		Vector3 forward = new Vector3(centerX - eyeX, centerY - eyeY, centerZ - eyeZ);
		forward.Normalise();
		Vector3 up = new Vector3(upX, upY, upZ);
		up.Normalise();
		Vector3 s = forward.Cross(up); //Right
		up.Set(s.Cross(forward)); //Up

		Matrix4x4 mat = new Matrix4x4(s.x, up.x, -forward.x, 0, s.y, up.y, -forward.y, 0, s.z, up.z, -forward.z, 0,	0, 0, 0, 1);
		Matrix4x4 tran = new Matrix4x4();
		tran.SetToTranslation(-eyeX, -eyeY, -eyeZ);
		this.Set(mat.Times(tran));
	}
	public void SetToFrustum(double left, double right, double bottom, double top, double near, double far) {
		this.Set(new Matrix4x4((float)(2 * near / (right - left)), 0.0f, 0.0f, 0.0f,
				0.0f, (float)(2.0f * near / (top - bottom)), 0.0f, 0.0f,
				(float)((right + left) / (right - left)), (float)((top + bottom) / (top - bottom)), - (float)((far + near) / (far - near)), -1.0f,
				0.0f, 0.0f, - (float)(2 * far * near / (far - near)), 0));
	}
	public void SetToPerspective(final float fovy, final float aspect, final float zNear, final float zFar) {
		float f = 1.0f / (float)Math.tan(Math.PI / 180.0 * (double)fovy * 0.5);
		this.Set(new Matrix4x4(f / aspect, 0.0f, 0.0f, 0.0f,
				0.0f, f, 0.0f, 0.0f,
				0.0f, 0.0f, ((zFar + zNear) / (zNear - zFar)), -1.0f,
				0.0f, 0.0f, (2.0f * zFar * zNear / (zNear - zFar)), 0.0f));
	}
	public void SetToOrtho(float left, float right, float bottom, float top, float nearVal, float farVal) {
		this.Set(new Matrix4x4(2.0f / (right - left), 0.0f, 0.0f, 0.0f,
				0.0f, 2.0f / (top - bottom), 0.0f, 0.0f,
				0.0f, 0.0f, -2.0f / (farVal - nearVal), 0.0f,
				-((right + left) / (right - left)), -((top + bottom) / (top - bottom)), -((farVal + nearVal) / (farVal - nearVal)), 1.0f));
	}

	public void Print() {
		for (int row = 0; row < 4; ++row) {
			for (int column = 0; column < 4; ++column) {
				System.out.print(a[(column * 4) + row]);
				System.out.print(' ');
			}
			System.out.println();
		}
	}

}