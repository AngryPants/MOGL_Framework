package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

import java.util.HashMap;

public class Textures {

	//Nested Class(es)
	static public class TextureData {
		public String name;
		public int handle;
		TextureData(String _textureName, int _handle) {
			name = new String(_textureName);
			handle = _handle;
		}
		TextureData() {
			name = new String();
			handle = TextureManager.INVALID_TEXTURE_HANDLE;
		}
	}

	//Declare Static Variable(s)
	public static int MAX_TEXTURES = 8;

	//Public Variable(s)
	public TextureData[] data;

	Textures() {
		data = new TextureData[MAX_TEXTURES];
		for (int i = 0; i < MAX_TEXTURES; ++i) {
			data[i] = new TextureData();
		}
	}
	Textures(final Textures _rhs) {
		data = new TextureData[MAX_TEXTURES];
		for (int i = 0; i < MAX_TEXTURES; ++i) {
			data[i] = new TextureData(_rhs.data[i].name, _rhs.data[i].handle);
		}
	}
	public void Set(final Textures _other) {
		for (int i = 0; i < MAX_TEXTURES; ++i) {
			data[i].name = _other.data[i].name;
			data[i].handle = _other.data[i].handle;
		}
	}

}
