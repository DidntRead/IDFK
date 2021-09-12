package proj.idfk.world.event;

import org.joml.Vector3i;

import java.io.Serializable;

public class PlayerDigEvent implements Serializable {
    public Vector3i position;
    public byte blockID;

    public PlayerDigEvent(Vector3i position, byte blockID) {
        this.position = position;
        this.blockID = blockID;
    }
}
