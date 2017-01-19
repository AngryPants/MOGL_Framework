package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

import java.util.HashMap;

public class Textures {

	//Declare Static Variable(s)
	public static int MAX_TEXTURES = 8;

	//Public Variable(s)
	public int[] handles;

	Textures() {
		handles = new int[MAX_TEXTURES];
		for (int i = 0; i < MAX_TEXTURES; ++i) {
			handles[i] = TextureManager.INVALID_TEXTURE_HANDLE;
		}
	}
	Textures(final Textures _rhs) {
		handles = new int[MAX_TEXTURES];
		for (int i = 0; i < MAX_TEXTURES; ++i) {
			handles[i] = _rhs.handles[i];
		}
	}
	public void Set(final Textures _other) {
		for (int i = 0; i < MAX_TEXTURES; ++i) {
			handles[i] = _other.handles[i];
		}
	}

}
