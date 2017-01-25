package com.sidm.mogl_framework;

import android.opengl.GLES20;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.HashMap;

public class MeshBuilder {

	//Nested Class
	static public class Mesh {
		//Name of our mesh.
		public String name;

		public Material material;

		//Declare Draw Modes
		public static final int DRAW_TRIANGLES = 0;
		public static final int DRAW_TRIANGLE_STRIP = 1;
		public int drawMode;

		//Texture Coordinates
		protected float[] textureOffset;
		protected float[] textureScale;

		//We use an array of size 1 for our vboHandle and iboHandle so that we can pass it into
		//a function that can edit it's value, since Java can only pass primitives by value,
		//we do this as a work around since arrays are pass by reference.
		public int[] vboHandle;
		public int numVertices;
		public int[] iboHandle;
		public int numIndices;

		//Constructor(s)
		private Mesh(final String _name) {
			name = new String(_name);
			material = new Material();
			drawMode = DRAW_TRIANGLES;
			textureOffset = new float[2]; textureOffset[0] = 0.0f; textureOffset[1] = 0.0f;
			textureScale = new float[2]; textureScale[0] = 1.0f; textureScale[1] = 1.0f;
			vboHandle = new int[1]; vboHandle[0] = 0; numVertices = 0;
			iboHandle = new int[1]; iboHandle[0] = 0; numIndices = 0;
		}

		public float[] GetTextureScale() {
			return textureScale;
		}
		public float[] GetTextureOffset() {
			return textureOffset;
		}

		public void SetTextureScale(final float _x, final float _y) {
			textureScale[0] = _x;
			textureScale[1] = _y;
		}
		public void SetTextureOffset(final float _x, final float _y) {
			textureOffset[0] = _x;
			textureOffset[1] = _y;
		}
	}

	static public class Text extends Mesh {
		//Variable(s)
		public String textString;
		private int numRows, numColumns;

		//Constructor(s)
		private Text(String _name, final int _numRows, final int _numColumns) {
			super(_name);

			SetNumRows(_numRows);
			SetNumColumns(_numColumns);
		}

		private void SetNumRows(final int _numRows) {
			if (_numRows < 1) {
				throw new RuntimeException("MeshBuilder.Text.numRows cannot be < 1!");
			}
			numRows = _numRows;
		}
		private void SetNumColumns(final int _numColumns) {
			if (_numColumns < 1) {
				throw new RuntimeException("MeshBuilder.Text.numColumnscannot be < 1!");
			}
			numColumns = _numColumns;
		}
		public int GetNumRows() {
			return numRows;
		}
		public int GetNumColumns() {
			return numColumns;
		}
		public void SetCharacter(final char _letter) {
			int column = (int)_letter % numColumns;
			int row = (int)_letter / numColumns;
			textureScale[1] = 1.0f / (float)numRows;
			textureScale[0] = 1.0f / (float)numColumns;
			textureOffset[0] = column * textureScale[0];
			textureOffset[1] = (numRows - 1 - row) * textureScale[1];
		}

		@Override
		public void SetTextureScale(final float _x, final float _y) {
			throw new RuntimeException("MeshBuilder.Text cannot SetTextureScale!");
		}
		@Override
		public void SetTextureOffset(final float _x, final float _y) {
			throw new RuntimeException("MeshBuilder.Text cannot SetTextureOffset!");
		}
	}

	static public class SpriteAnimation {
		//Variable(s)
		public int numRows, numColumns;
		public double currentTime;
		public int currentFrame;

		public int startFrame;
		public int endFrame;
		public boolean loop;
		public double animTime;
		public boolean pause;
		//Constructor(s)
		public SpriteAnimation(int _numRows, int _numColumns,
							   int _startFrame, int _endFrame,
							   boolean _loop, double _animTime, boolean _pause)
		{
			Set(_numRows, _numColumns, _startFrame, _endFrame, _loop, _animTime, _pause);
		}

		public void Set(int _numRows, int _numColumns,
						int _startFrame, int _endFrame,
						boolean _loop, double _animTime, boolean _pause)
		{
			numRows = _numRows;
			numColumns = _numColumns;
			startFrame = _startFrame;
			endFrame = _endFrame;
			currentFrame = startFrame;
			currentTime = 0.0;
			loop = _loop;
			animTime = _animTime;
			pause = _pause;
		}

		public void Reset() {
			currentFrame = startFrame;
			currentTime = 0.0f;
		}

		public void Update(double _deltaTime) {
			if (pause) {
				return;
			}

			currentTime += _deltaTime;

			int numFrame;
			if (endFrame > startFrame) {
				numFrame = endFrame - startFrame + 1;
			} else {
				numFrame = (numRows * numColumns) - (startFrame - endFrame) + 1;
			}

			double frameTime = animTime / (float)numFrame;
			currentFrame = (int)(currentTime / frameTime) + startFrame;
			currentFrame %= (numRows * numColumns);

			if (currentTime > animTime) {
				currentFrame = endFrame;
				if (loop == false) {
					pause = true;
				} else {
					Reset();
				}
			}
		}
	}

	static public class Sprite extends Mesh {
		//Variable(s)
		public SpriteAnimation animation;
		//Constructor(s)
		private Sprite(String _name) {
			super(_name);
			animation = null;
		}

		@Override
		public float[] GetTextureOffset() {
			if (animation != null) {
				int column = animation.currentFrame % animation.numColumns;
				int row = animation.currentFrame / animation.numColumns;
				textureScale[0] = 1.0f / (float)animation.numColumns;
				textureScale[1] = 1.0f / (float)animation.numRows;
				textureOffset[0] = column * textureScale[0];
				textureOffset[1] = (animation.numRows - 1 - row) * textureScale[1];
			}
			return textureOffset;
		}
		@Override
		public float[] GetTextureScale() {
			if (animation != null) {
				textureScale[0] = 1.0f / (float)animation.numColumns;
				textureScale[1] = 1.0f / (float)animation.numRows;
			}
			return textureScale;
		}
		@Override
		public void SetTextureScale(final float _x, final float _y) {
			throw new RuntimeException("MeshBuilder.Sprite cannot SetTextureScale!");
		}
		@Override
		public void SetTextureOffset(final float _x, final float _y) {
			throw new RuntimeException("MeshBuilder.Sprite cannot SetTextureOffset!");
		}
	}

	//Private Variable(s)
	//Make sure multiple functions don't access meshList at the same time.
	static private HashMap<String, MeshBuilder.Mesh> meshList = new HashMap<>();
	static private HashMap<String, MeshBuilder.Text> textList = new HashMap<>();
	static private HashMap<String, MeshBuilder.Sprite> spriteList = new HashMap<>();

	//Private Constructor(s)
	private MeshBuilder() {}

	//Static Function(s)
	static public Mesh GetMesh(String _meshName) {
		synchronized (meshList) {
			return meshList.get(_meshName);
		}
	}
	static public boolean ReleaseMesh(final String _meshName) {
		synchronized (meshList) {
			Mesh mesh = meshList.get(_meshName);
			if (mesh != null) {
				GLES20.glDeleteBuffers(1, mesh.vboHandle, 0);
				GLES20.glDeleteBuffers(1, mesh.iboHandle, 0);
				meshList.remove(_meshName);
				return true;
			}
			return false;
		}
	}
	static public boolean HasMesh(final String _meshName) {
		synchronized (meshList) {
			return meshList.containsKey(_meshName);
		}
	}
	static public Text GetText(String _meshName) {
		synchronized (textList) {
			return textList.get(_meshName);
		}
	}
	static public boolean ReleaseText(final String _meshName) {
		synchronized (textList) {
			Mesh mesh = textList.get(_meshName);
			if (mesh != null) {
				GLES20.glDeleteBuffers(1, mesh.vboHandle, 0);
				GLES20.glDeleteBuffers(1, mesh.iboHandle, 0);
				textList.remove(_meshName);
				return true;
			}
			return false;
		}
	}
	static public boolean HasText(final String _meshName) {
		synchronized (textList) {
			return textList.containsKey(_meshName);
		}
	}
	static public Sprite GetSprite(final String _meshName) {
		synchronized (spriteList) {
			return spriteList.get(_meshName);
		}
	}
	static public boolean ReleaseSprite(final String _meshName) {
		synchronized (spriteList) {
			Mesh mesh = spriteList.get(_meshName);
			if (mesh != null) {
				GLES20.glDeleteBuffers(1, mesh.vboHandle, 0);
				GLES20.glDeleteBuffers(1, mesh.iboHandle, 0);
				spriteList.remove(_meshName);
				return true;
			}
			return false;
		}
	}
	static public boolean HasSprite(final String _meshName) {
		synchronized (spriteList) {
			return spriteList.containsKey(_meshName);
		}
	}

	static public Mesh GenerateQuad(final String _meshName, Vertex.Color _color, float _length) {
		synchronized (meshList) {
			Mesh mesh = GetMesh(_meshName);
			if (mesh != null) {
				return mesh;
			}

			mesh = new Mesh(_meshName);
			mesh.numVertices = 4;
			mesh.numIndices = 4;
			mesh.drawMode = Mesh.DRAW_TRIANGLE_STRIP;

			//Remember to generate buffers!
			GLES20.glGenBuffers(1, mesh.vboHandle, 0);
			GLES20.glGenBuffers(1, mesh.iboHandle, 0);

			//Make our Vertex Buffer Data
			Vertex[] vertexBufferData = new Vertex[mesh.numVertices];

			Vertex v = new Vertex();
			v.color.Set(_color);
			v.normal.Set(0, 0, 1);

			v.position.Set(-0.5f * _length, 0.5f * _length, 0);
			v.texCoord.Set(0, 1);
			vertexBufferData[0] = new Vertex(v);

			v.position.Set(-0.5f * _length, -0.5f * _length, 0);
			v.texCoord.Set(0, 0);
			vertexBufferData[1] = new Vertex(v);

			v.position.Set(0.5f * _length, 0.5f * _length, 0);
			v.texCoord.Set(1, 1);
			vertexBufferData[2] = new Vertex(v);

			v.position.Set(0.5f * _length, -0.5f * _length, 0);
			v.texCoord.Set(1, 0);
			vertexBufferData[3] = new Vertex(v);

			//Convert our vertexBufferData into a FloatBuffer.
			FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexBufferData.length * Vertex.SizeOf()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			//Put our data into the vertexBuffer
			for (int i = 0; i < mesh.numVertices; ++i) {
				float[] vertexDataArray = vertexBufferData[i].GetAsFloatArray();
				for (int j = 0; j < vertexDataArray.length; ++j) {
					vertexBuffer.put(j + (i * vertexDataArray.length), vertexDataArray[j]);
				}
			}

			//Make our Index Buffer Data
			short[] indexBufferData = new short[mesh.numIndices];
			for (short i = 0; i < mesh.numIndices; ++i) {
				indexBufferData[i] = i;
			}

			//Convert our indexBufferData into a ShortBuffer
			ShortBuffer indexBuffer = ByteBuffer.allocateDirect(indexBufferData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
			indexBuffer.put(indexBufferData).position(0);

			//Bind our buffers.
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mesh.vboHandle[0]);
			//Set the data inside the buffers.
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mesh.numVertices * Vertex.SizeOf(), vertexBuffer, GLES20.GL_STATIC_DRAW);

			//Bind our buffers.
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.iboHandle[0]);
			//Set the data inside the buffers.
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.numIndices * 2, indexBuffer, GLES20.GL_STATIC_DRAW);

			//Unbind the buffers
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);


			//Store the mesh into our mesh list.
			meshList.put(_meshName, mesh);

			return mesh;
		}
	}

	static public MeshBuilder.Text GenerateText(final String _meshName, Vertex.Color _color, int _numRows, int _numColumns) {
		synchronized (textList) {
			MeshBuilder.Text mesh = GetText(_meshName);
			if (mesh != null) {
				return mesh;
			}

			mesh = new Text(_meshName, _numRows, _numColumns);
			mesh.numVertices = 4;
			mesh.numIndices = 4;
			mesh.drawMode = Mesh.DRAW_TRIANGLE_STRIP;

			//Remember to generate buffers!
			GLES20.glGenBuffers(1, mesh.vboHandle, 0);
			GLES20.glGenBuffers(1, mesh.iboHandle, 0);

			//Make our Vertex Buffer Data
			Vertex[] vertexBufferData = new Vertex[mesh.numVertices];

			Vertex v = new Vertex();
			v.color.Set(_color);
			v.normal.Set(0, 0, 1);

			v.position.Set(-0.5f, 0.5f, 0);
			v.texCoord.Set(0, 1);
			vertexBufferData[0] = new Vertex(v);

			v.position.Set(-0.5f, -0.5f, 0);
			v.texCoord.Set(0, 0);
			vertexBufferData[1] = new Vertex(v);

			v.position.Set(0.5f, 0.5f, 0);
			v.texCoord.Set(1, 1);
			vertexBufferData[2] = new Vertex(v);

			v.position.Set(0.5f, -0.5f, 0);
			v.texCoord.Set(1, 0);
			vertexBufferData[3] = new Vertex(v);

			//Convert our vertexBufferData into a FloatBuffer.
			FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexBufferData.length * Vertex.SizeOf()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			//Put our data into the vertexBuffer
			for (int i = 0; i < mesh.numVertices; ++i) {
				float[] vertexDataArray = vertexBufferData[i].GetAsFloatArray();
				for (int j = 0; j < vertexDataArray.length; ++j) {
					vertexBuffer.put(j + (i * vertexDataArray.length), vertexDataArray[j]);
				}
			}

			//Make our Index Buffer Data
			short[] indexBufferData = new short[mesh.numIndices];
			for (short i = 0; i < mesh.numIndices; ++i) {
				indexBufferData[i] = i;
			}

			//Convert our indexBufferData into a ShortBuffer
			ShortBuffer indexBuffer = ByteBuffer.allocateDirect(indexBufferData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
			indexBuffer.put(indexBufferData).position(0);

			//Bind our buffers.
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mesh.vboHandle[0]);
			//Set the data inside the buffers.
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mesh.numVertices * Vertex.SizeOf(), vertexBuffer, GLES20.GL_STATIC_DRAW);

			//Bind our buffers.
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.iboHandle[0]);
			//Set the data inside the buffers.
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.numIndices * 2, indexBuffer, GLES20.GL_STATIC_DRAW);

			//Unbind the buffers
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);

			//Store the mesh into our mesh list.
			textList.put(_meshName, mesh);

			return mesh;
		}
	}

	static public Sprite GenerateSprite(final String _meshName) {
		synchronized (spriteList) {
			Sprite mesh = GetSprite(_meshName);
			if (mesh != null) {
				return mesh;
			}

			mesh = new Sprite(_meshName);
			mesh.numVertices = 4;
			mesh.numIndices = 4;
			mesh.drawMode = Mesh.DRAW_TRIANGLE_STRIP;

			//Remember to generate buffers!
			GLES20.glGenBuffers(1, mesh.vboHandle, 0);
			GLES20.glGenBuffers(1, mesh.iboHandle, 0);

			//Make our Vertex Buffer Data
			Vertex[] vertexBufferData = new Vertex[mesh.numVertices];

			Vertex v = new Vertex();
			v.color.Set(new Vertex.Color(1.0f, 1.0f, 1.0f, 1.0f));
			v.normal.Set(0, 0, 1);

			v.position.Set(-0.5f, 0.5f, 0);
			v.texCoord.Set(0, 1);
			vertexBufferData[0] = new Vertex(v);

			v.position.Set(-0.5f, -0.5f, 0);
			v.texCoord.Set(0, 0);
			vertexBufferData[1] = new Vertex(v);

			v.position.Set(0.5f, 0.5f, 0);
			v.texCoord.Set(1, 1);
			vertexBufferData[2] = new Vertex(v);

			v.position.Set(0.5f, -0.5f, 0);
			v.texCoord.Set(1, 0);
			vertexBufferData[3] = new Vertex(v);

			//Convert our vertexBufferData into a FloatBuffer.
			FloatBuffer vertexBuffer = ByteBuffer.allocateDirect(vertexBufferData.length * Vertex.SizeOf()).order(ByteOrder.nativeOrder()).asFloatBuffer();
			//Put our data into the vertexBuffer
			for (int i = 0; i < mesh.numVertices; ++i) {
				float[] vertexDataArray = vertexBufferData[i].GetAsFloatArray();
				for (int j = 0; j < vertexDataArray.length; ++j) {
					vertexBuffer.put(j + (i * vertexDataArray.length), vertexDataArray[j]);
				}
			}

			//Make our Index Buffer Data
			short[] indexBufferData = new short[mesh.numIndices];
			for (short i = 0; i < mesh.numIndices; ++i) {
				indexBufferData[i] = i;
			}

			//Convert our indexBufferData into a ShortBuffer
			ShortBuffer indexBuffer = ByteBuffer.allocateDirect(indexBufferData.length * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
			indexBuffer.put(indexBufferData).position(0);

			//Bind our buffers.
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, mesh.vboHandle[0]);
			//Set the data inside the buffers.
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, mesh.numVertices * Vertex.SizeOf(), vertexBuffer, GLES20.GL_STATIC_DRAW);

			//Bind our buffers.
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.iboHandle[0]);
			//Set the data inside the buffers.
			GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, mesh.numIndices * 2, indexBuffer, GLES20.GL_STATIC_DRAW);

			//Unbind the buffers
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
			GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);


			//Store the mesh into our mesh list.
			spriteList.put(_meshName, mesh);

			return mesh;
		}
	}

}