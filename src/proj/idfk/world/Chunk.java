package proj.idfk.world;

import proj.idfk.util.Disposable;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.generation.ChunkGenerator;

public class Chunk implements Disposable {
    private final ChunkMesh mesh;
    private final ChunkMeshBuilder meshBuilder;
    private final VectorXZ position;
    private byte[] blocks;
    private boolean dirty = true;

    protected Chunk(VectorXZ position, ChunkGenerator generator) {
        this.position = position;
        this.mesh = new ChunkMesh();
        this.meshBuilder = new ChunkMeshBuilder(this);
        this.blocks = new byte[Constants.CHUNK_VOLUME];
        generator.generate(this);
        //TODO generate - maybe in a thread pool
    }

    private int getFlatIndex(int x, int y, int z) {
        return (x + Constants.CHUNK_SIZE * (y + Constants.CHUNK_HEIGHT * z));
    }

    public byte getBlock(int x, int y, int z) {
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

    protected final ChunkMesh getMeshWithoutBuilding() {
        return this.mesh;
    }

    public final VectorXZ getPosition() {
        return this.position;
    }

    @Override
    public void dispose() {
        mesh.dispose();
    }
}
