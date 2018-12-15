package proj.idfk.render;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import proj.idfk.util.Disposable;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45.*;

public class GUIRenderer implements Disposable {
    private float[] vertexData;
    private int vao,vbo;
    private int hotbarTexture, selectedTexture;
    private long hotbar, selected;

    public GUIRenderer() {
        this.vertexData = new float[] {
                0.8f, -0.8f, -0.8f, -0.8f, -0.8f, -1f, -0.8f, -1f, 0.8f, -1f,  0.8f, -0.8f,
        };
        this.vao = glCreateVertexArrays();
        this.vbo = glCreateBuffers();
        glNamedBufferData(vbo, vertexData, GL_STATIC_DRAW);
        glVertexArrayVertexBuffer(vao, 0, vbo, 0, 8);
        glEnableVertexArrayAttrib(vao, 0);
        glVertexArrayAttribFormat(vao, 0, 3, GL_FLOAT, false, 0);
        glVertexArrayAttribBinding(vao, 0, 0);

        hotbarTexture = glCreateTextures(GL_TEXTURE_2D);
        glTextureParameteri(hotbarTexture, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTextureParameteri(hotbarTexture, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        selectedTexture = glCreateTextures(GL_TEXTURE_2D);
        glTextureParameteri(selectedTexture, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTextureParameteri(selectedTexture, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            try {
                PNGDecoder decoder = new PNGDecoder(GUIRenderer.class.getResourceAsStream("/textures/hotbar.png"));
                ByteBuffer img = stack.malloc(4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(img, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
                img.flip();
                glTextureStorage2D(hotbarTexture, 1, GL_RGBA8, decoder.getWidth(), decoder.getHeight());
                glTextureSubImage2D(hotbarTexture, 0, 0, 0, decoder.getWidth(), decoder.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, img);
                decoder = new PNGDecoder(GUIRenderer.class.getResourceAsStream("/textures/selected.png"));
                img = stack.malloc(4 * decoder.getWidth() * decoder.getHeight());
                decoder.decode(img, 4 * decoder.getWidth(), PNGDecoder.Format.RGBA);
                img.flip();
                glTextureStorage2D(selectedTexture, 1, GL_RGBA8, decoder.getWidth(), decoder.getHeight());
                glTextureSubImage2D(selectedTexture, 0, 0, 0, decoder.getWidth(), decoder.getHeight(), GL_RGBA, GL_UNSIGNED_BYTE, img);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        hotbar = ARBBindlessTexture.glGetTextureHandleARB(hotbarTexture);
        selected = ARBBindlessTexture.glGetTextureHandleARB(selectedTexture);
        ARBBindlessTexture.glMakeTextureHandleResidentARB(hotbar);
        ARBBindlessTexture.glMakeTextureHandleResidentARB(selected);
    }

    public void render() {
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    @Override
    public void dispose() {
        glDeleteVertexArrays(vao);
        glDeleteBuffers(vbo);
    }
}
