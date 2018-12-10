package proj.idfk.entity;

import org.joml.AABBf;
import org.joml.Vector3f;

public class Entity {
    public Vector3f position;
    public Vector3f rotation;
    public Vector3f velocity;
    public AABBf box;

    public Entity() {
        this.position = new Vector3f();
        this.rotation = new Vector3f();
        this.velocity = new Vector3f();
        this.box = new AABBf(position.x, position.y, position.z, 0, 0, 0);
    }

    public Entity(Vector3f position, Vector3f rotation) {
        this.position = position;
        this.rotation = rotation;
        this.velocity = new Vector3f();
        this.box = new AABBf(position.x, position.y, position.z, 0, 0, 0);
    }

    public Entity(Vector3f position, Vector3f rotation, Vector3f dimensions) {
        this.position = position;
        this.rotation = rotation;
        this.velocity = new Vector3f();
        this.box = new AABBf(position.x, position.y, position.z, dimensions.x, dimensions.y, dimensions.z);
    }
}
