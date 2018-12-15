package proj.idfk;

import org.joml.FrustumIntersection;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import proj.idfk.entity.Entity;
import proj.idfk.util.Matrix;

public class Camera {
    public Vector3f position;
    public Vector3f rotation;

    private Matrix4f viewMatrix;
    private Matrix4f inverseViewMatrix;
    private Matrix4f projectionMatrix;
    private Matrix4f inverseProjectionMatrix;
    private Matrix4f projViewMatrix;

    private FrustumIntersection frustrum;

    private Entity entity = null;

    public Camera(Config config) {
        this.position = new Vector3f(0, 0, -3.5f);
        this.rotation = new Vector3f();

        this.frustrum = new FrustumIntersection();

        this.projectionMatrix = Matrix.makeProjectionMatrix(config);
        this.inverseProjectionMatrix = new Matrix4f(projectionMatrix).invert();
        this.inverseViewMatrix = new Matrix4f();
        this.projViewMatrix = new Matrix4f();
    }

    public void update() {
        if (entity != null) {
            this.position.set(entity.position);
            this.position.y += 2;
            this.rotation = entity.rotation;
        }

        viewMatrix = Matrix.makeViewMatrix(this);
        viewMatrix.invert(inverseViewMatrix);
        projectionMatrix.invert(inverseProjectionMatrix);
        projViewMatrix.set(projectionMatrix).mul(viewMatrix);
        frustrum.set(projViewMatrix, false);
    }

    public void hookEntity(final Entity ent) {
        this.entity = ent;
    }

    public Matrix4f getViewMatrix() {
        return this.viewMatrix;
    }

    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    public Matrix4f getInverseProjectionMatrix() {
        return this.inverseProjectionMatrix;
    }

    public Matrix4f getInverseViewMatrix() {
        return this.inverseViewMatrix;
    }

    public Matrix4f getProjectionViewMatrix() {
        return this.projViewMatrix;
    }

    public Vector3f getPosition() {
        return this.position;
    }

    public FrustumIntersection getFrustrum() {
        return this.frustrum;
    }
}
