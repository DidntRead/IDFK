package proj.idfk.render;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.Application;
import proj.idfk.shader.GUIShader;
import proj.idfk.util.Disposable;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GUIRenderer implements Disposable {
    private final int crosshairVAO, crosshairVBO;
    private final int crosshairTexture;
    private final long crosshairTextureHandle;
    private final GUIShader guiShader;

    public GUIRenderer(Application app) {
        Vector2i windowSize = app.getWindow().getWindowSize();
        Matrix4f ortho = new Matrix4f().setOrtho(0, windowSize.x, windowSize.y, 0, -1, 1);
        float[] crosshair = new float[] {
                (windowSize.x / 2) - 10, (windowSize.y / 2) - 10,
                (windowSize.x / 2) + 10, (windowSize.y / 2) + 10,
                (windowSize.x / 2) + 10, (windowSize.y / 2) - 10,
                (windowSize.x / 2) - 10, (windowSize.y / 2) - 10,
                (windowSize.x / 2) - 10, (windowSize.y / 2) + 10,
                (windowSize.x / 2) + 10, (windowSize.y / 2) + 10,
        };

        float[] crosshairTexture = new float[] {
                0.0f, 0.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
        };

        for (int i = 0; i < crosshair.length; i += 2) {
            Vector4f vec = new Vector4f(crosshair[i], crosshair[i + 1], 0, 1);
            ortho.transform(vec);
            crosshair[i] = vec.x;
            crosshair[i + 1] = vec.y;
        }

        this.crosshairVAO = glCreateVertexArrays();
        this.crosshairVBO = glCreateBuffers();
        this.guiShader = new GUIShader();

        glVertexArrayVertexBuffer(crosshairVAO, 0, crosshairVBO, 0, 8);
        glVertexArrayVertexBuffer(crosshairVAO, 1, crosshairVBO, crosshair.length * Float.BYTES, 8);

        glEnableVertexArrayAttrib(crosshairVAO, 0);
        glEnableVertexArrayAttrib(crosshairVAO, 1);

        glVertexArrayAttribFormat(crosshairVAO, 0, 2, GL_FLOAT, false, 0);
        glVertexArrayAttribFormat(crosshairVAO, 1, 2, GL_FLOAT, false, 8);


        glVertexArrayAttribBinding(crosshairVAO, 0, 0);
        glVertexArrayAttribBinding(crosshairVAO, 1, 1);

        glNamedBufferStorage(crosshairVBO, (crosshair.length + crosshairTexture.length) * Float.BYTES, GL_DYNAMIC_STORAGE_BIT);
        glNamedBufferSubData(crosshairVBO, 0, crosshair);
        glNamedBufferSubData(crosshairVBO, crosshair.length * Float.BYTES, crosshairTexture);

        this.crosshairTexture = glCreateTextures(GL_TEXTURE_2D);

        try {
            PNGDecoder decoder = new PNGDecoder(GUIShader.class.getResourceAsStream("/textures/crosshair.png"));
            ByteBuffer buf = MemoryUtil.memAlloc(4 * decoder.getWidth() * decoder.getHeight());
            decoder.decode(buf , 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
            buf.flip();
            glTextureStorage2D(this.crosshairTexture, 1, GL_RGBA8, decoder.getWidth(), decoder.getHeight());
            glTextureSubImage2D(this.crosshairTexture, 0, 0, 0, decoder.getWidth(), decoder.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, buf);
            MemoryUtil.memFree(buf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.crosshairTextureHandle = ARBBindlessTexture.glGetTextureHandleARB(this.crosshairTexture);
        ARBBindlessTexture.glMakeTextureHandleResidentARB(this.crosshairTextureHandle);
        guiShader.loadCrosshair(this.crosshairTextureHandle);
    }

    public void render() {
        guiShader.bind();
        glBindVertexArray(crosshairVAO);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    @Override
    public void dispose() {
        glDeleteTextures(this.crosshairTexture);
        glDeleteBuffers(crosshairVBO);
        glDeleteVertexArrays(crosshairVAO);
    }
}
