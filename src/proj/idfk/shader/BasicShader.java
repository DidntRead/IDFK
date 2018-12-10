package proj.idfk.shader;

import org.joml.Matrix4f;

public class BasicShader extends ShaderProgram {
    private final UniformMatrix projectionView;

    public BasicShader() {
        super("basic.vert", "basic.frag");
        this.projectionView = new UniformMatrix("projView");
        super.getAllUniformLocations(projectionView);
    }

    public void loadProjectionView(Matrix4f projView) {
        projectionView.loadMatrix(projView);
    }
}
