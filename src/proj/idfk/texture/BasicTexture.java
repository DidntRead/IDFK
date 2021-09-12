package proj.idfk.texture;

import org.lwjgl.opengl.ARBBindlessTexture;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import proj.idfk.util.BufferUtil;
import proj.idfk.util.Disposable;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL45.*;

public class BasicTexture implements Disposable {
    protected int id;
    protected long textureHandle;
    protected int width,height;

    /**
     * Used only for the textureArray class
     */
    protected BasicTexture() {

    }

    public BasicTexture(String filename) {
        ByteBuffer data = loadTexture(filename);

        this.id = glCreateTextures(GL_TEXTURE_2D);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTextureStorage2D(id, 1, GL_RGBA8, width, height);
        glTexSubImage2D(id, 0, 0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, data);

        this.textureHandle = ARBBindlessTexture.glGetTextureHandleARB(id);
        ARBBindlessTexture.glMakeTextureHandleResidentARB(textureHandle);
    }

    public long getTextureHandle() {
        return this.textureHandle;
    }

    public void bind(int unit) {
        glBindTextureUnit(unit, id);
    }

    protected ByteBuffer loadTexture(String filename) {
        try {
            ByteBuffer imgSrc;
            ByteBuffer img;
            imgSrc = BufferUtil.ioResourceToByteBuffer(filename, 8 * 1024);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer w = stack.mallocInt(1);
                IntBuffer h = stack.mallocInt(1);
                IntBuffer comp = stack.mallocInt(1);

                img = STBImage.stbi_load_from_memory(imgSrc, w, h, comp, 4);

                if (img == null) {
                    throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
                }

                this.width = w.get(0);
                this.height = h.get(0);

                return img;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void dispose() {
        ARBBindlessTexture.glMakeTextureHandleNonResidentARB(textureHandle);
        glDeleteTextures(id);
    }
}
