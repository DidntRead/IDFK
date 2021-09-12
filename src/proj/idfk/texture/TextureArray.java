package proj.idfk.texture;

import org.lwjgl.opengl.ARBBindlessTexture;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45.*;

public class TextureArray extends BasicTexture {
    public TextureArray(String directory, int textureCount) {
        ByteBuffer data[] = new ByteBuffer[textureCount];

        if (!directory.endsWith("/")) {
            directory += "/";
        }

        for (int i = 0; i < textureCount; i++) {
            data[i] = loadTexture(directory + String.valueOf(i) + ".png");
        }

        this.id = glCreateTextures(GL_TEXTURE_2D_ARRAY);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        glTextureStorage3D(id, 1, GL_RGBA8, width, height, textureCount);
        for (int i = 0; i < textureCount; i++) {
            glTextureSubImage3D(id, 0, 0, 0, i, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, data[i]);
        }

        this.textureHandle = ARBBindlessTexture.glGetTextureHandleARB(id);
        ARBBindlessTexture.glMakeTextureHandleResidentARB(textureHandle);
    }
}
