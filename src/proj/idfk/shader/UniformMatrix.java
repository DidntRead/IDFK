package proj.idfk.shader;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;

import java.nio.FloatBuffer;

public class UniformMatrix extends Uniform{
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    public UniformMatrix(String name) {
        super(name);
    }

    public void loadMatrix(Matrix4f mat) {
        mat.get(matrixBuffer);
        GL45.glProgramUniformMatrix4fv(programID, location, false, matrixBuffer);
    }

    public void loadMatrix(FloatBuffer mat) {
        GL45.glProgramUniformMatrix4fv(programID, location, false, mat);
    }
}
