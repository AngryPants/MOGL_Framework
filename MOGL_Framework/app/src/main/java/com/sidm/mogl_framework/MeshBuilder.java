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
		public float[] textureOffset;
		public float[] textureScale;

		//We use an array of size 1 for our vboHandle and iboHandle so that we can pass it into
		//a function that can edit it's value, since Java can only pass primitives by value,
		//we do this as a work around since arrays are pass by reference.
		public int[] vboHandle;
		public int numVertices;
		public int[] iboHandle;
		public int numIndices;

		private int referenceCount;

		//Constructor(s)
		Mesh(final String _name) {
			name = new String(_name);
			material = new Material();
			drawMode = DRAW_TRIANGLES;
			textureOffset = new float[2]; textureOffset[0] = 0.0f; textureOffset[1] = 0.0f;
			textureScale = new float[2]; textureScale[0] = 1.0f; textureScale[1] = 1.0f;
			vboHandle = new int[1]; vboHandle[0] = 0; numVertices = 0;
			iboHandle = new int[1]; iboHandle[0] = 0; numIndices = 0;
			referenceCount = 0;
		}
	}

	//Private Variable(s)
	static private HashMap<String, Mesh> meshList = new HashMap<>();

	//Private Constructor(s)
	private MeshBuilder() {}

	//Static Function(s)
	static public Mesh GetMesh(String _meshName) {
		Mesh mesh = meshList.get(_meshName);
		if (mesh != null) {
			++mesh.referenceCount;
		}
		return mesh;
	}
	static public boolean ReleaseMesh(final String _meshName) {
		Mesh mesh = meshList.get(_meshName);
		if (mesh != null) {
			--mesh.referenceCount;
			if (mesh.referenceCount <= 0) {
				GLES20.glDeleteBuffers(1, mesh.vboHandle, 0);
				GLES20.glDeleteBuffers(1, mesh.iboHandle, 0);
				meshList.remove(_meshName);
			}
			return true;
		}
		return false;
	}
	static public Mesh GenerateQuad(final String _meshName, Vertex.Color _color, float _length) {
		Mesh mesh = GetMesh(_meshName);
		if (mesh != null) {
			return mesh;
		}
		mesh = new Mesh(_meshName);
		mesh.referenceCount = 1;
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