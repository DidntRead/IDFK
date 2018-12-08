package proj.idfk.shader;

import org.lwjgl.opengl.ARBBindlessTexture;

public class UniformBindlessSampler extends Uniform {
    public UniformBindlessSampler(String name) {
        super(name);
    }

    public void loadTextureHandle(long handle) {
        ARBBindlessTexture.glProgramUniformHandleui64ARB(programID, location, handle);
    }
}
