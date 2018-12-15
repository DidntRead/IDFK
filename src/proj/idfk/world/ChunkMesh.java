package proj.idfk.world;

import proj.idfk.util.Disposable;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class ChunkMesh implements Disposable {
    private final int vao;
    private int vboSize = 0;
    private int vbo,ebo;
    private int count;

    public ChunkMesh() {
        this.vao = glCreateVertexArrays();
        this.vbo = glCreateBuffers();
        this.ebo = glCreateBuffers();

        glVertexArrayElementBuffer(vao, ebo);
        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 12);

        glEnableVertexArrayAttrib(vao, 0);
        glEnableVertexArrayAttrib(vao, 1);

        glVertexArrayAttribFormat(vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribFormat(vao, 1, 1, GL_FLOAT, false, 0);

        glVertexArrayAttribBinding(vao, 0, 0);
        glVertexArrayAttribBinding(vao, 1, 1);
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public int getCount() {
        return this.count;
    }

    public void uploadData(FloatBuffer positionVertex, FloatBuffer textureIndex, IntBuffer elementIndex) {
        glInvalidateBufferData(vbo);
        glInvalidateBufferData(ebo);
        int vertexSize = (positionVertex.limit() + textureIndex.limit()) * Float.BYTES;
        if (vertexSize > vboSize) {
            if (vbo != 0) {
                glDeleteBuffers(vbo);
                this.vbo = glCreateBuffers();
                glVertexArrayVertexBuffer(vao, 0, vbo, 0, 12);
            }
            glNamedBufferStorage(vbo, vertexSize, GL_DYNAMIC_STORAGE_BIT);
            this.vboSize = vertexSize;
        }
        glNamedBufferSubData(vbo, 0, positionVertex);
        glNamedBufferSubData(vbo, positionVertex.limit() * Float.BYTES, textureIndex);
        glVertexArrayVertexBuffer(vao, 1, vbo, positionVertex.limit() * Float.BYTES, 4);
        glNamedBufferData(ebo, elementIndex, GL_STATIC_DRAW);
        this.count = elementIndex.limit();
    }

    @Override
    public void dispose() {
        glDeleteBuffers(vbo);
        glDeleteBuffers(ebo);
        glDeleteVertexArrays(vao);
    }
}
