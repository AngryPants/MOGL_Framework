package com.sidm.mogl_framework;

public class Material {

	public class Component {
		public float r, g, b;
		Component() {
			Set(0.1f, 0.1f, 0.1f);
		}
		Component(final float _r, final float _g, final float _b) {
			Set(_r, _g, _b);
		}
		void Set(final float _r, final float _g, final float _b) {
			r = _r;
			g = _g;
			b = _b;
		}
	}

	//Variable(s)
	Material.Component ambient;
	Material.Component diffuse;
	Material.Component specular;
	float shininess;

	//Constructor(s)
	Material() {
		ambient = new Material.Component(0.1f, 0.1f, 0.1f);
		diffuse = new Material.Component(0.6f, 0.6f, 0.6f);
		specular = new Material.Component(0.1f, 0.1f, 0.1f);
		shininess = 5.0f;
	}

}