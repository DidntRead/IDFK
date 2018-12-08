package proj.idfk.shader;

import org.lwjgl.opengl.GL45;

public class UniformSampler extends Uniform {
    public UniformSampler(String name) {
        super(name);
    }

    public void loadTextureUnit(int textureUnit) {
        if (textureUnit >= GL45.GL_TEXTURE0 && textureUnit <= GL45.GL_TEXTURE31) {
            textureUnit -= GL45.GL_TEXTURE0;
        }

        if (textureUnit < 0 || textureUnit > 31) {
            System.out.println("Invalid texture unit " + textureUnit);
        }

        GL45.glProgramUniform1i(programID, location, textureUnit);
    }
}
