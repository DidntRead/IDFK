package proj.idfk.world;

import org.joml.AABBf;
import org.joml.Vector3f;
import org.joml.Vector3i;
import proj.idfk.Application;
import proj.idfk.Camera;
import proj.idfk.Config;
import proj.idfk.player.Player;
import proj.idfk.util.VectorXZ;
import proj.idfk.world.event.PlayerDigEvent;
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
    private final Map<VectorXZ, List<PlayerDigEvent>> eventMap;

    public World(String name, Long seed, Config config, Vector3f playerPosition, Map<VectorXZ, List<PlayerDigEvent>> eventMap) {
        this.name = name;
        this.seed = seed;
        this.player = new Player(config, playerPosition, this);
        this.generator = new NormalGenerator(seed);
        //this.generator = new FlatGenerator();
        this.chunkMap = new ConcurrentHashMap<>();
        this.eventMap = eventMap;
    }

    public World(String name, Long seed, Config config) {
        this.name = name;
        this.seed = seed;
        this.generator = new NormalGenerator(seed);
        //this.generator = new FlatGenerator();
        this.chunkMap = new ConcurrentHashMap<>();
        this.eventMap = new HashMap<>();
        this.player = new Player(config, new Vector3f(0, getHeight(0, 0) + 1, 0), this);
    }

    public Player getPlayer() {
        return this.player;
    }

    private Chunk getChunk(VectorXZ position) {
        Chunk ch = chunkMap.get(position);
        if (ch == null) {
            ch = new Chunk(position, generator, eventMap.get(position));
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
        return new VectorXZ((int)Math.floor(x / (float)CHUNK_SIZE), (int)Math.floor(z / (float)CHUNK_SIZE));
    }

    private int getBlockCoordinate(int v) {
        v = v % CHUNK_SIZE;
        if (v < 0) {
            return CHUNK_SIZE + v;
        } else {
            return v;
        }
    }

    private VectorXZ getBlockXZ(int x, int z) {
        return new VectorXZ(getBlockCoordinate(x), getBlockCoordinate(z));
    }

    public byte getBlock(int x, int y, int z) {
        if (y > CHUNK_HEIGHT - 1 || y < 0) {
            return 0;
        }
        final VectorXZ block = getBlockXZ(x, z);
        if (block.x < 0 || block.z < 0) {
            System.out.println("WTF " + block.x + " " + block.z + " " + x + " " + y + " " + z);
        }
        return getChunk(getChunkXZ(x, z)).getBlock(block.x, y, block.z);
    }

    public void digEvent(int x, int y, int z, byte blockID, boolean place) {
        final VectorXZ chunk = getChunkXZ(x, z);
        final VectorXZ block = getBlockXZ(x, z);
        List<PlayerDigEvent> events = eventMap.get(chunk);
        if (events == null) {
            events = new ArrayList<>();
            eventMap.put(chunk, events);
        }

        boolean handled = false;
        Iterator<PlayerDigEvent> it = events.iterator();
        while (it.hasNext()) {
            PlayerDigEvent ev = it.next();
            if (ev.position.x == x && ev.position.y == y && ev.position.z == z) {
                ev.blockID = blockID;
                handled = true;
                break;
            }
        }
        if (!handled) {
            events.add(new PlayerDigEvent(new Vector3i(block.x, y, block.z), blockID));
        }
        getChunk(chunk).setBlock(block.x, y, block.z, blockID);
    }

    public void setBlock(int x, int y, int z, byte id) {
        final VectorXZ block = getBlockXZ(x, z);
        getChunk(getChunkXZ(x, z)).setBlock(block.x, y, block.z, id);
    }

    public void setBlock(Vector3i pos, byte id) {
        final VectorXZ block = getBlockXZ(pos.x, pos.z);
        getChunk(getChunkXZ(pos.x, pos.z)).setBlock(block.x, pos.y, block.z, id);
    }

    public Chunk get(int worldX, int worldZ) {
        return getChunk(getChunkXZ(worldX, worldZ));
    }

    public int getHeight(int x, int z) {
        final VectorXZ chunk = getChunkXZ(x, z);
        final VectorXZ block = getBlockXZ(x, z);

        Chunk ch = getChunk(chunk);

        for (int i = CHUNK_HEIGHT -1; i >= 0; i--) {
            if (ch.getBlock(block.x, i, block.z) != BlockID.AIR) {
                return i;
            }
        }

        return CHUNK_HEIGHT;
    }

    public Map<VectorXZ, List<PlayerDigEvent>> getEventMap() {
        return this.eventMap;
    }

    public String getName() {
        return this.name;
    }

    public Long getSeed() {
        return this.seed;
    }
}
