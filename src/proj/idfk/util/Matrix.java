package proj.idfk.util;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import proj.idfk.Camera;
import proj.idfk.Config;

public class Matrix {
    private static final Vector3f rotX = new Vector3f(1, 0, 0);
    private static final Vector3f rotY = new Vector3f(0, 1, 0);
    private static final Vector3f rotZ = new Vector3f(0, 0, 1);


    public static Matrix4f makeProjectionMatrix(final Config config) {
        return new Matrix4f().perspective((float)Math.toRadians(config.fov), (float) config.width / (float)config.height, 0.1f, 2000.0f);
    }

    public static Matrix4f makeViewMatrix(final Camera camera) {
        Matrix4f mat = new Matrix4f();

        mat.rotate((float)Math.toRadians(camera.rotation.x), rotX)
                .rotate((float)Math.toRadians(camera.rotation.y), rotY)
                .rotate((float)Math.toRadians(camera.rotation.z), rotZ)
                .translate(new Vector3f(camera.position).negate());

        return mat;
    }
}
