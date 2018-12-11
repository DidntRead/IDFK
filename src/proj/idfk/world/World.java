package proj.idfk.world;

import org.joml.Vector2i;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.Config;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.generation.ChunkGenerator;
import proj.idfk.world.generation.NormalGenerator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static proj.idfk.world.Constants.CHUNK_SIZE;

public class World {
    private final String name;
    private final Long seed;
    private final ChunkGenerator generator;
    private final Map<VectorXZ, Chunk> chunkMap;

    public World(String name, Long seed) {
        this.name = name;
        this.seed = seed;
        this.generator = new NormalGenerator(seed);
        this.chunkMap = new ConcurrentHashMap<VectorXZ, Chunk>();
    }

    public Chunk getChunk(VectorXZ position) {
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
                app.getRenderer().add(chunk);
            }
        }
    }

    public String getName() {
        return this.name;
    }

    public Long getSeed() {
        return this.seed;
    }
}
