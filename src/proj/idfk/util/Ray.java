package proj.idfk.util;

import org.joml.Vector3f;

public class Ray {
    private Vector3f direction;
    private Vector3f start;
    private Vector3f end;

    public Ray(Vector3f position,Vector3f direction) {
        this.start = position;
        this.end = new Vector3f(position);
        this.direction = direction;
    }

    public void step(float scale) {
        float yaw = (float)Math.toRadians(direction.y + 90);

        end.x -= Math.cos(yaw) * scale;
        end.z -= Math.sin(yaw) * scale;
        end.y -= Math.tan(Math.toRadians(direction.x)) * scale;
    }

    public Vector3f getEnd() {
        return this.end;
    }

    public float getLength() {
        return end.distance(start);
    }
}
