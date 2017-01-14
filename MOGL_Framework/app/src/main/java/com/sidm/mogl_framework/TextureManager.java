package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import java.util.HashMap;

public class TextureManager {

	//Nested Class(es)
	//I hope I did this static thingy right.
	static private class Texture {
		//Private Variable(s)
		final public int[] textureID;
		final public String name;
		public int referenceCount;

		//Constructor(s)
		public Texture(final String _textureName, final int _textureID) {
			name = new String(_textureName);
			textureID = new int[1];
			textureID[0] = _textureID;
			referenceCount = 0;
		}
	}

	//Declare Static Variable(s)
	static int INVALID_TEXTURE_HANDLE = GLES20.GL_INVALID_VALUE;

	//Private Variable(s)
	static private HashMap<String, Texture> textureList = new HashMap<>();

	//Private Constructor(s)
	private TextureManager() {}

	//Call this function when we want a texture.
	static public int GetTextureID(final String _textureName) {
		Texture texture = textureList.get(_textureName);
		if (texture != null) {
			++texture.referenceCount;
			return texture.textureID[0];
		}
		return INVALID_TEXTURE_HANDLE;
	}
	/*Call this function when we no longer want a texture.
	The texture will get removed and deleted by GC later.
	Don't call this too many times or you might delete the texture for others.
	Only call this as many times as you called AddTexture or GetTexture.*/
	static public boolean ReleaseTexture(final String _textureName) {
		Texture texture = textureList.get(_textureName);
		if (texture != null) {
			--texture.referenceCount;
			if (texture.referenceCount <= 0) {
				GLES20.glDeleteTextures(1, texture.textureID, 0);
				textureList.remove(_textureName);
			}
			return true;
		}
		return false;
	}
	static public boolean HasTexture(final String _textureName) {
		return textureList.containsKey(_textureName);
	}
	static public int AddTexture(final String _textureName, final Context _context, final int _resourceId) {
		//Create an int array of size 1 as primitives are pass by reference but arrays are pass by reference.
		final int[] textureHandle = new int[1];

		//Check if we already have a texture with this name. If yes, return that.
		textureHandle[0] = GetTextureID(_textureName);
		if (textureHandle[0] != INVALID_TEXTURE_HANDLE) {
			return textureHandle[0];
		}

		//Generate a texture.
		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != INVALID_TEXTURE_HANDLE) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; //No pre-scaling

			//Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(_context.getResources(), _resourceId, options);

			//Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

			//Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

			//Set wrapping
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);
			//GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			//GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			//Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

			//Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}

		if (textureHandle[0] == INVALID_TEXTURE_HANDLE) {
			throw new RuntimeException("Error loading texture!");
		}

		Texture texture = new Texture(_textureName, textureHandle[0]);
		texture.referenceCount = 1;
		textureList.put(texture.name, texture);

		return textureHandle[0];
	}

}