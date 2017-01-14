package com.sidm.mogl_framework;
//Created by <Insert Name> on 13/1/2017.

import android.opengl.GLES20;
import android.util.Log;

import java.util.HashMap;

public class GLESShaderProgram {

	//Nested Class(es)
	private class UniformHandle {
		public int handle;
		UniformHandle() {
			handle = -1;
		}
		UniformHandle(int _handle) {
			handle = _handle;
		}
		UniformHandle(final UniformHandle _rhs) {
			handle = _rhs.handle;
		}
	}
	private class AttributeHandle {
		public int handle;
		AttributeHandle() {
			handle = -1;
		}
		AttributeHandle(int _handle) {
			handle = _handle;
		}
		AttributeHandle(final AttributeHandle _rhs) {
			handle = _rhs.handle;
		}
	}

	//Declare Static Variable(s)
	static final int VERTEX_SHADER = GLES20.GL_VERTEX_SHADER;
	static final int FRAGMENT_SHADER = GLES20.GL_FRAGMENT_SHADER;
	static final int INVALID_SHADER_ID = 0;
	static final int INVALID_SHADER_PROGRAM_ID = 0;

	//Private Variable(s)
	private HashMap<String, UniformHandle> uniforms;
	private HashMap<String, AttributeHandle> attributes;

	//Variable(s)
	final public String name;
	private int shaderProgramHandle;

	//Constructor(s)
	public GLESShaderProgram(final String _shaderName) {
		name = new String(_shaderName);
		shaderProgramHandle = INVALID_SHADER_PROGRAM_ID;
		uniforms = new HashMap<>();
		attributes = new HashMap<>();
	}
	public int GetShaderProgramHandle() {
		return shaderProgramHandle;
	}

	static private int CompileShader(final int _shaderType, final String _shaderSource) {
		int shaderHandle = GLES20.glCreateShader(_shaderType);

		if (shaderHandle != INVALID_SHADER_ID) {
			//Pass in the shader source.
			GLES20.glShaderSource(shaderHandle, _shaderSource);
			//Compile the shader.
			GLES20.glCompileShader(shaderHandle);

			//Check compilation status.
			//EDIT: Not all phones support this. So let's just skip the check and pray for the best.
			/*final int[] compileStatus = new int[1];
			GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
			if (compileStatus[0] == 0) {
				Log.e("ShaderHelper", "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
				GLES20.glDeleteShader(shaderHandle);
				shaderHandle = INVALID_SHADER_ID;
			}*/
		}

		if (shaderHandle == INVALID_SHADER_ID) {
			throw new RuntimeException("Error creating shader!");
		}

		return shaderHandle;
	}

	public void LoadShaders(final String _vertexShaderSource, final String _fragmentShaderSource, final String[] _attributes) {
		int vertexShaderHandle = CompileShader(VERTEX_SHADER, _vertexShaderSource);
		int fragmentShaderHandle = CompileShader(FRAGMENT_SHADER, _fragmentShaderSource);
		shaderProgramHandle = GLES20.glCreateProgram();

		if (shaderProgramHandle != INVALID_SHADER_PROGRAM_ID) {
			//Attach shaders
			GLES20.glAttachShader(shaderProgramHandle, vertexShaderHandle);
			GLES20.glAttachShader(shaderProgramHandle, fragmentShaderHandle);

			//Bind Attributes
			if (_attributes != null) {
				for (int i = 0; i < _attributes.length; ++i) {
					GLES20.glBindAttribLocation(shaderProgramHandle, i, _attributes[i]);
				}
			}

			//Link the 2 shaders together into a program.
			GLES20.glLinkProgram(shaderProgramHandle);

			//Get the link status.
			//EDIT: Not all phones support this. So let's just skip the check and pray for the best.
			/*final int[] linkStatus = new int[1];
			GLES20.glGetShaderiv(shaderProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] == 0) {
				Log.e("ShaderHelper", "Error compiling program: " + GLES20.glGetProgramInfoLog(shaderProgramHandle));
				GLES20.glDeleteProgram(shaderProgramHandle);
				shaderProgramHandle = INVALID_SHADER_PROGRAM_ID;
			}*/
		}

		if (shaderProgramHandle == INVALID_SHADER_PROGRAM_ID) {
			throw new RuntimeException("Error creating & linking shader program!");
		}

		//We no longer need the individual shaders since we've already created the program.
		//EDIT: Deleting them seems to give an error. Maybe we shouldn't delete them like
		//we do on PC?
		//GLES20.glDeleteProgram(vertexShaderHandle);
		//GLES20.glDeleteProgram(fragmentShaderHandle);
	}

	public boolean DeleteShaders() {
		if (shaderProgramHandle == INVALID_SHADER_PROGRAM_ID) {
			return false;
		}

		GLES20.glDeleteProgram(shaderProgramHandle);
		shaderProgramHandle = INVALID_SHADER_PROGRAM_ID;
		return true;
	}

	public int GetUniformHandle(final String _uniformName) {
		UniformHandle uniformHandle = uniforms.get(_uniformName);
		if (uniformHandle != null) {
			return uniformHandle.handle;
		}

		uniformHandle = new UniformHandle(GLES20.glGetUniformLocation(shaderProgramHandle, _uniformName));
		uniforms.put(_uniformName, uniformHandle);

		return uniformHandle.handle;
	}

	public int GetAttributeHandle(final String _attributeName) {
		AttributeHandle attributeHandle = attributes.get(_attributeName);
		if (attributeHandle != null) {
			return attributeHandle.handle;
		}

		attributeHandle = new AttributeHandle(GLES20.glGetAttribLocation(shaderProgramHandle, _attributeName));
		attributes.put(_attributeName, attributeHandle);

		return attributeHandle.handle;
	}

	boolean BindTexture (final int _textureID) {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, _textureID);
		return true;
	}
	boolean UnbindTexture() {
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		return true;
	}
	boolean SetActiveTexture(int _textureIndex) {
		int MAX_TEXTURE_INDEX = 32;
		if (_textureIndex >= 0 && _textureIndex <= MAX_TEXTURE_INDEX) {
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + _textureIndex);
			return true;
		}
		return false;
	}

	//Uniform Updates
	//Boolean
	void UpdateUniform(final int _uniformHandle, final boolean _value) {
		if (_value) {
			GLES20.glUniform1i(_uniformHandle, 1);
		} else {
			GLES20.glUniform1i(_uniformHandle, 0);
		}
	}

	//Integer
	void UpdateUniform(final int _uniformHandle, final int _value) {
		GLES20.glUniform1i(_uniformHandle, _value);
	}
	void UpdateUniform(final int _uniformHandle, final int _value0, final int _value1) {
		GLES20.glUniform2i(_uniformHandle, _value0, _value1);
	}
	void UpdateUniform(final int _uniformHandle, final int _value0, final int _value1, final int _value2) {
		GLES20.glUniform3i(_uniformHandle, _value0, _value1, _value2);
	}
	void UpdateUniform(final int _uniformHandle, final int _value0, final int _value1, final int _value2, final int _value3) {
		GLES20.glUniform4i(_uniformHandle, _value0, _value1, _value2, _value3);
	}
	void UpdateUniform(final int _uniformHandle, final int[] _value, final int _numElements) {
		switch (_numElements) {
			case 1:
				GLES20.glUniform1iv(_uniformHandle, 1, _value, 0);
				break;
			case 2:
				GLES20.glUniform2iv(_uniformHandle, 1, _value, 0);
				break;
			case 3:
				GLES20.glUniform3iv(_uniformHandle, 1, _value, 0);
				break;
			case 4:
				GLES20.glUniform4iv(_uniformHandle, 1, _value, 0);
				break;
			default:
				//Do nothing.
		}
	}

	//Float
	void UpdateUniform(final int _uniformHandle, final float _value) {
		GLES20.glUniform1f(_uniformHandle, _value);
	}
	void UpdateUniform(final int _uniformHandle, final float _value0, final float _value1) {
		GLES20.glUniform2f(_uniformHandle, _value0, _value1);
	}
	void UpdateUniform(final int _uniformHandle, final float _value0, final float _value1, final float _value2) {
		GLES20.glUniform3f(_uniformHandle, _value0, _value1, _value2);
	}
	void UpdateUniform(final int _uniformHandle, final float _value0, final float _value1, final float _value2, final float _value3) {
		GLES20.glUniform4f(_uniformHandle, _value0, _value1, _value2, _value3);
	}
	void UpdateUniform(final int _uniformHandle, final float[] _value, final int _numElements) {
		switch (_numElements) {
			case 1:
				GLES20.glUniform1fv(_uniformHandle, 1, _value, 0);
				break;
			case 2:
				GLES20.glUniform2fv(_uniformHandle, 1, _value, 0);
				break;
			case 3:
				GLES20.glUniform3fv(_uniformHandle, 1, _value, 0);
				break;
			case 4:
				GLES20.glUniform4fv(_uniformHandle, 1, _value, 0);
				break;
			default:
				//Do nothing.
		}
	}
	void UpdateUniform(final int _uniformHandle, final Matrix4x4 _value, final boolean _transpose) {
		GLES20.glUniformMatrix4fv(_uniformHandle, 1, _transpose, _value.a, 0);
	}
	void UpdateUniform(final int _uniformHandle, final Matrix4x4 _value, final boolean _transpose, final int _count) {
		GLES20.glUniformMatrix4fv(_uniformHandle, _count, _transpose, _value.a, 0);
	}

}