package proj.idfk.world;

import org.joml.AABBf;
import org.joml.Vector3f;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.Config;
import proj.idfk.entity.Player;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.generation.ChunkGenerator;
import proj.idfk.world.generation.NormalGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static proj.idfk.world.Constants.CHUNK_HEIGHT;
import static proj.idfk.world.Constants.CHUNK_SIZE;

public class World {
    private final String name;
    private final Long seed;
    private final Player player;
    private final ChunkGenerator generator;
    private final Map<VectorXZ, Chunk> chunkMap;

    public World(String name, Long seed, Config config, Vector3f playerPosition) {
        this.name = name;
        this.seed = seed;
        this.player = new Player(config, playerPosition);
        this.generator = new NormalGenerator(seed);
        this.chunkMap = new ConcurrentHashMap<>();
    }

    public World(String name, Long seed, Config config) {
        this.name = name;
        this.seed = seed;
        this.generator = new NormalGenerator(seed);
        this.chunkMap = new ConcurrentHashMap<>();
        this.player = new Player(config, new Vector3f(0, getHeight(0, 0), 0));
    }

    public Player getPlayer() {
        return this.player;
    }

    private Chunk getChunk(VectorXZ position) {
        Chunk ch = chunkMap.get(position);
        if (ch == null) {
            ch = new Chunk(position, generator);
            chunkMap.put(position, ch);
        }
        return ch;
    }

    public void render(Application app, Camera camera) {
        final Config config = app.getConfig();

        int minX = (int)((camera.position.x / CHUNK_SIZE) - config.renderDistance);
        int minZ = (int)((camera.position.z / CHUNK_SIZE) - config.renderDistance);
        int maxX = (int)((camera.position.x / CHUNK_SIZE) + config.renderDistance);
        int maxZ = (int)((camera.position.z / CHUNK_SIZE) + config.renderDistance);

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                VectorXZ vec = new VectorXZ(x, z);
                getChunk(vec);
            }
        }

        for (Chunk chunk : chunkMap.values()) {
            VectorXZ location = chunk.getPosition();

            if (minX > location.x ||
                    minZ > location.z ||
                    maxZ < location.z ||
                    maxX < location.x)
            {
               chunkMap.remove(location).dispose();
            } else {
                final AABBf aabb = chunk.aabb;
                if (camera.getFrustrum().testAab(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ)) {
                    app.getRenderer().add(chunk);
                }
            }
        }
    }

    private VectorXZ getChunkXZ(int x, int z) {
        return new VectorXZ(x / CHUNK_SIZE, z / CHUNK_SIZE);
    }

    private VectorXZ getBlockXZ(int x, int z) {
        return new VectorXZ(x % CHUNK_SIZE, z % CHUNK_SIZE);
    }

    private int getHeight(int x, int z) {
        final VectorXZ chunk = getChunkXZ(x, z);
        final VectorXZ block = getBlockXZ(x, z);

        Chunk ch = getChunk(chunk);

        for (int i = CHUNK_HEIGHT -1; i >= 0; i--) {
            if (ch.getBlock(block.x, i, block.z) != BlockID.AIR) {
                return i + 3;
            }
        }

        return CHUNK_HEIGHT + 3;
    }

    public String getName() {
        return this.name;
    }

    public Long getSeed() {
        return this.seed;
    }
}
