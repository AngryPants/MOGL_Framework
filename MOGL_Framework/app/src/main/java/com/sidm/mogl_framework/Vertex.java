package com.sidm.mogl_framework;

public class Vertex {

	static public class Position {
		float x, y, z;
		public Position() {
			Set(0.0f, 0.0f, 0.0f);
		}
		public Position(final float _x, final float _y, final float _z) {
			Set(_x, _y, _z);
		}
		public Position(final Position _rhs) {
			Set(_rhs);
		}
		public void Set(final float _x, final float _y, final float _z) {
			x = _x; y = _y; z = _z;
		}
		public void Set(final Position _other) {
			Set(_other.x, y = _other.y, z = _other.z);
		}
		static public int SizeOf() {
			return 12; //3 * 4bytes
		}
	}

	static public class Color {
		float r, g, b, a;
		public Color() {
			Set(0.0f, 0.0f, 0.0f, 1.0f);
		}
		public Color(final float _r, final float _g, final float _b, final float _a) {
			Set(_r, _g, _b, _a);
		}
		public Color(final Color _rhs) {
			Set(_rhs);
		}
		public void Set(final float _r, final float _g, final float _b, final float _a) {
			r = _r; g = _g; b = _b; a = _a;
		}
		public void Set(final Color _other) {
			Set(_other.r, _other.g, _other.b, _other.a);
		}
		static public int SizeOf() {
			return 16; //4 * 4bytes
		}
	}

	static public class Normal {
		float x, y, z;
		public Normal() {
			Set(0.0f, 0.0f, 1.0f);
		}
		public Normal(final float _x, final float _y, final float _z) {
			Set(_x, _y, _z);
		}
		public Normal(final Normal _rhs) {
			Set(_rhs);
		}
		public void Set(final float _x, final float _y, final float _z) {
			x = _x; y = _y; z = _z;
		}
		public void Set(final Normal _other) {
			Set(_other.x, _other.y, _other.z);
		}
		static public int SizeOf() {
			return 12; //3 * 4bytes
		}
	}

	static public class TexCoord {
		float u, v;
		public TexCoord() {
			Set(0.0f, 0.0f);
		}
		public TexCoord(final float _u, final float _v) {
			Set(_u, _v);
		}
		public TexCoord(final TexCoord _rhs) {
			Set(_rhs);
		}
		public void Set(final float _u, final float _v) {
			u = _u; v = _v;
		}
		public void Set(final TexCoord _other) {
			Set(_other.u, _other.v);
		}
		static public int SizeOf() {
			return 8; //2 * 4bytes
		}
	}

	public Position position;
	public Color color;
	public Normal normal;
	public TexCoord texCoord;
	public Vertex() {
		position = new Position();
		color = new Color();
		normal = new Normal();
		texCoord = new TexCoord();
	}
	public Vertex(final Vertex _rhs) {
		position = new Position(_rhs.position);
		color = new Color(_rhs.color);
		normal = new Normal(_rhs.normal);
		texCoord = new TexCoord(_rhs.texCoord);
	}
	public void Set(final Vertex _other) {
		position.Set(_other.position);
		color.Set(_other.color);
		normal.Set(_other.normal);
		texCoord.Set(_other.texCoord);
	}
	static public int SizeOf() { //Return the number of bytes that a Vertex has.
		return Position.SizeOf() + Color.SizeOf() + Normal.SizeOf() + TexCoord.SizeOf();
	}
	public float[] GetAsFloatArray() {
		float[] data = new float[Vertex.SizeOf() / 4];

		//Position
		data[0] = position.x;
		data[1] = position.y;
		data[2] = position.z;
		//Color
		data[3] = color.r;
		data[4] = color.g;
		data[5] = color.b;
		data[6] = color.a;
		//Normal
		data[7] = normal.x;
		data[8] = normal.y;
		data[9] = normal.z;
		//TexCoord
		data[10] = texCoord.u;
		data[11] = texCoord.v;

		return data;
	}

}