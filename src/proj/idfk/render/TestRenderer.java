package proj.idfk.render;

import proj.idfk.Camera;
import proj.idfk.shader.BasicShader;
import proj.idfk.util.Disposable;

import static org.lwjgl.opengl.GL45.*;

public class TestRenderer implements Disposable {
    private BasicShader shader;
    private int vao,vbo;

    public TestRenderer() {
        this.shader = new BasicShader();
        this.vao = glCreateVertexArrays();
        this.vbo = glCreateBuffers();

        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 12);

        glEnableVertexArrayAttrib(vao, 0);

        glVertexArrayAttribFormat(vao, 0, 3, GL_FLOAT, false, 0);

        glVertexArrayAttribBinding(vao, 0, 0);

        glNamedBufferData(vbo, new float[] {
                0.0f,  0.5f,
                0.5f, -0.5f,
                -0.5f, -0.5f
        }, GL_STATIC_DRAW);
    }

    public void render(Camera camera) {
        shader.bind();
        shader.loadProjectionView(camera.getProjectionViewMatrix());
        glBindVertexArray(vao);
        glDrawArrays(GL_TRIANGLES, 0, 3);
    }

    @Override
    public void dispose() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        shader.dispose();
    }
}
