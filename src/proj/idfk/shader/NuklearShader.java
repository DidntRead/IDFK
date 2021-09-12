package proj.idfk.shader;

import org.joml.Matrix4f;

public class NuklearShader extends ShaderProgram {
    private final UniformMatrix projectionMat;
    private final UniformBindlessSampler textureSampler;

    public NuklearShader() {
        super("nuklear.vert", "nuklear.frag");
        projectionMat = new UniformMatrix("ProjMtx");
        textureSampler = new UniformBindlessSampler("Texture");
        super.getAllUniformLocations(projectionMat, textureSampler);
    }

    public void loadTextureHandle(long handle) {
        textureSampler.loadTextureHandle(handle);
    }

    public void loadProjectionMatrix(Matrix4f mat) {
        projectionMat.loadMatrix(mat);
    }
}
