package proj.idfk.shader;

import org.joml.Matrix4f;

public class ChunkShader extends ShaderProgram {
    private UniformMatrix projectionView;
    private UniformBindlessSampler blocks;

    public ChunkShader() {
        super("chunk.vert", "chunk.frag");
        projectionView = new UniformMatrix("projView");
        blocks = new UniformBindlessSampler("blocksTexture");
        super.getAllUniformLocations(projectionView, blocks);
    }

    public void loadProjectionView(Matrix4f mat) {
        projectionView.loadMatrix(mat);
    }

    public void loadBlockTextureArrayHandle(long handle) {
        blocks.loadTextureHandle(handle);
    }
}
