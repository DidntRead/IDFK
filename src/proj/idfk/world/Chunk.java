package proj.idfk.world;

import org.joml.AABBf;
import proj.idfk.util.Disposable;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.event.PlayerDigEvent;
import proj.idfk.world.generation.ChunkGenerator;

import java.util.List;

import static proj.idfk.world.Constants.CHUNK_SIZE;

public class Chunk implements Disposable {
    private final ChunkMesh mesh;
    private final ChunkMeshBuilder meshBuilder;
    private final VectorXZ position;
    public final AABBf aabb;
    private byte[] blocks;
    private boolean dirty = true;

    protected Chunk(VectorXZ position, ChunkGenerator generator, List<PlayerDigEvent> events) {
        this.position = position;
        this.mesh = new ChunkMesh();
        this.aabb = new AABBf(position.x * CHUNK_SIZE, 0, position.z * CHUNK_SIZE, (position.x + 1) * CHUNK_SIZE, 16, (position.z + 1) * CHUNK_SIZE);
        this.meshBuilder = new ChunkMeshBuilder(this);
        this.blocks = new byte[Constants.CHUNK_VOLUME];
        generator.generate(this);
        if (events != null) {
            events.iterator().forEachRemaining(event -> {
                blocks[getFlatIndex(event.position.x, event.position.y, event.position.z)] = event.blockID;
            });
        }
        //TODO generate - maybe in a thread pool
    }

    private int getFlatIndex(int x, int y, int z) {
        return (x + CHUNK_SIZE * (y + Constants.CHUNK_HEIGHT * z));
    }

    public int getHeight(int x, int z) {
        x = Math.abs(x);
        z = Math.abs(z);
        for (int i = Constants.CHUNK_HEIGHT - 1; i >= 0; i--) {
            if (getBlock(x, i, z) != 0) {
                return i;
            }
        }
        return Constants.CHUNK_HEIGHT;
    }

    public byte getBlock(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) {
            System.out.format("X: %d, Y: %d, Z: %d\n", x, y, z);
        }
        if (getFlatIndex(x, y, z) == 65536) {
            System.out.format("X: %d, Y: %d, Z: %d\n", x, y, z);
        }
        return this.blocks[getFlatIndex(x, y, z)];
    }

    public void setBlock(int x, int y, int z, byte id) {
        this.blocks[getFlatIndex(x, y, z)] = id;
        dirty = true;
    }

    public void buildMesh() {
        dirty = false;
        meshBuilder.build();
    }

    public final ChunkMesh getMesh() {
        if (dirty) {
            buildMesh();
        }
        return this.mesh;
    }

    final ChunkMesh getMeshWithoutBuilding() {
        return this.mesh;
    }

    public final VectorXZ getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return String.format("X: %d, Z: %d", position.x, position.z);
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}
