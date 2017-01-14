package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

import android.graphics.Shader;
import android.opengl.GLES20;
import android.util.Log;

import java.util.HashMap;

public class ShaderHelper {

	//Private Variable(s)
	private static HashMap<String, GLESShaderProgram> shaderList = new HashMap<>();

	//Private Constructor(s)
	private ShaderHelper() {}

	static public boolean HasShaderProgram(final String _shaderName) {
		return shaderList.containsKey(_shaderName);
	}

	static public GLESShaderProgram GetShaderProgram(final String _shaderName) {
		return shaderList.get(_shaderName);
	}
	static public GLESShaderProgram AddShader(final String _shaderName, final String _vertexShaderSource, final String _fragmentShaderSource, final String[] _attributes) {
		GLESShaderProgram shaderProgram = shaderList.get(_shaderName);
		if (shaderProgram != null) {
			return shaderProgram;
		}

		shaderProgram = new GLESShaderProgram(_shaderName);
		shaderProgram.LoadShaders(_vertexShaderSource, _fragmentShaderSource, _attributes);
		shaderList.put(_shaderName, shaderProgram);

		return shaderProgram;
	}
	static public boolean RemoveShader(final String _shaderName) {
		GLESShaderProgram shaderProgram = shaderList.get(_shaderName);
		if (shaderProgram == null) {
			return false;
		}
		shaderProgram.DeleteShaders();
		shaderList.remove(_shaderName);
		return true;
	}

}