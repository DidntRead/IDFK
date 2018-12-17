package proj.idfk.shader;

public class GUIShader extends ShaderProgram {
    private UniformBindlessSampler crosshair;

    public GUIShader() {
        super("gui.vert", "gui.frag");
        this.crosshair = new UniformBindlessSampler("crosshair");
        super.getAllUniformLocations(crosshair);
    }

    public void loadCrosshair(long texture) {
        crosshair.loadTextureHandle(texture);
    }
}
